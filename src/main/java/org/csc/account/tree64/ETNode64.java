package org.csc.account.tree64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.csc.account.tree.EHelper;
import org.csc.bcutil.Hex;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import onight.tfw.outils.conf.PropHelper;
import onight.tfw.outils.pool.ReusefulLoopPool;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class ETNode64 {
	private static int CHILDREN_MASK_BYTES = 16;
	private static int TREE_RADIX = 64;
	private static byte[] ROOT_KEY = "ROOT".getBytes();
	private static byte[] NULL_KEY = new byte[0];

	private byte[] childrenHashs[] = new byte[TREE_RADIX + 1][];
	private byte[] key = ROOT_KEY;
	private byte v[];
	private boolean dirty = false;
	private boolean deleted = false;
	private byte[] hash = null;
	// private String hashKey = null;
	private byte[] contentData = null;
	private byte[][] contentSplitData = null;
	private boolean leafNode = false;
	private ETNode64 children[] = new ETNode64[TREE_RADIX + 1];
	public static ETNode64 NULL_NODE = new ETNode64(NULL_KEY, null);

	private final static int POOL_SIZE = new PropHelper(null).get("org.csc.account.state.parallel",
			Math.max(4, Runtime.getRuntime().availableProcessors()));
	private static ExecutorService executor = Executors.newFixedThreadPool(POOL_SIZE);
	private static ExecutorService executor1 = Executors.newFixedThreadPool(POOL_SIZE);
	static AtomicInteger maxDeep = new AtomicInteger(0);
	static AtomicInteger codeDeep = new AtomicInteger(0);
	static AtomicInteger parrelCC0 = new AtomicInteger(0);
	static AtomicInteger bufferAlloc = new AtomicInteger(0);
	static AtomicInteger parrelCC1 = new AtomicInteger(0);

	public boolean isNullNode() {
		return this == NULL_NODE;
	}

	public boolean isNotNullNode() {
		return this != NULL_NODE;
	}

	public String toString() {
		return "ETNODE64[]";
	}

	@Data
	public static class Stat {
		long size;
		int deep;
		long dataSize;
		int count;

		Stat() {
			this.size = 0;
			this.deep = 0;
			this.dataSize = 0;
		}
	}

	private void appendJsonString(StringBuilder sb, Stat stat) {
		if (isNullNode()) {
			sb.append("{\"NULL_NODE\":true}");
		} else {
			sb.append("{");
			long size = CHILDREN_MASK_BYTES + key.length;
			long dataSize = key.length;
			sb.append("\"key\":\"").append(Hex.toHexString(key)).append("\"");
			if (v != null) {
				size += v.length;
				dataSize += v.length;
				sb.append(",\"v\":\"").append(Hex.toHexString(v)).append("\"");
			}
			if (hash != null) {
				size += hash.length;
				sb.append(",\"hash\":\"").append(Hex.toHexString(hash)).append("\"");
			}
			String format = ",\"node_%02d\":";
			int subDeep = 0;
			long subSize = 0;
			long subDataSize = 0;
			int subCount = 0;
			for (int i = 0; i < TREE_RADIX + 1; i++) {
				if (children[i] != null) {
					sb.append(String.format(format, i));
					Stat st = new Stat();
					children[i].appendJsonString(sb, st);
					if (st.getDeep() > subDeep) {
						subDeep = st.getDeep();
					}
					subCount += st.getCount();
					subSize += st.getSize();
					subSize += childrenHashs[i].length;
					subDataSize += st.getDataSize();
				}
			}
			sb.append("}");

			stat.setCount(stat.getCount() + 1 + subCount);
			stat.setDeep(stat.getDeep() + 1 + subDeep);
			stat.setSize(stat.getSize() + size + subSize);
			stat.setDataSize(stat.getDataSize() + dataSize + subDataSize);
		}
	}

	public String toJsonString() {
		StringBuilder sb = new StringBuilder();
		Stat stat = new Stat();
		appendJsonString(sb, stat);
		// log.info("tree size: {} ,data size: {}, maxDeep: {} , extend rate: {} %",
		// stat.getSize(), stat.getDataSize(), stat.getDeep(), (stat.getSize() -
		// stat.getDataSize()) * 100.0 / stat.getDataSize());
		return sb.toString();
	}

	public Stat getStat() {
		StringBuilder sb = new StringBuilder();
		Stat stat = new Stat();
		appendJsonString(sb, stat);
		return stat;
	}

	// 加载当前节点和子节点，递归调用
	public ETNode64 deepLoad(IETStorage64 storage) {
		for (int i = 0; i < TREE_RADIX + 1; i++) {
			if (childrenHashs[i] != null) {
				if (children[i] == null) {
					// 从KV数据库中获取Value
					byte bb[] = storage.esGet(childrenHashs[i]);
					if (bb == null) {
						throw new RuntimeException("hash not found:" + childrenHashs[i]);
					}
					// 反序列化
					children[i] = fromBytes(childrenHashs[i], bb);
					// 递归获取子Node
					children[i].deepLoad(storage);
				}
			}
		}
		return this;
	}

	public byte[] encode(IETStorage64 storage) {
		codeDeep.set(0);
		parrelCC0.set(0);
		parrelCC1.set(0);
		bufferAlloc.set(0);
		byte[] ret = encode(storage, 0);

		return ret;
	}

	public byte[] encode(IETStorage64 storage, int deep) {
		if (maxDeep.get() < deep) {
			maxDeep.set(deep);
		}
		// 如果已经计算过hash，则直接返回
		if (hash != null && !dirty) {
			return hash;
		}
		if (codeDeep.get() < deep) {
			codeDeep.set(deep);
		}

		// 序列化
		contentData = toBytes(storage, deep);
		// len = 64
		hash = EHelper.encAPI.sha3Encode(contentData);

		dirty = false;
		if (storage != null) {
			storage.esPut(hash, contentData);
		}
		if (deep == 0 && codeDeep.get() > 0) {
			log.error("trie-calc::-->maxdeep=" + maxDeep.get() + ",codeDeep=" + codeDeep.get() + ",parrelCC0="
					+ parrelCC0.get() + ",parrelCC1=" + parrelCC1.get() + ",buffalloc=" + bufferAlloc.get()
					+ ",buffcount=" + bbPools.getActiveObjs().size());// + );
		}
		return hash;
	}
	
	/**
	 * 获取子节点
	 *
	 * @param idx
	 *            子节点索引
	 * @param storage
	 *            存储
	 * @return 子节点
	 */
	public ETNode64 getChild(int idx, IETStorage64 storage) {
		if (idx > TREE_RADIX) {
			throw new RuntimeException("ETNode child index invalid:" + idx);
		}
		if (children[idx] != null) {
			return children[idx];
		} else if (childrenHashs[idx] != null && storage != null) {
			children[idx] = fromBytes(childrenHashs[idx], storage.esGet(childrenHashs[idx]));
		}
		return children[idx];
	}

	/**
	 * 获取子节点
	 *
	 * @param key
	 *            子节点的Key
	 * @param storage
	 *            存储
	 * @return 子节点
	 */
	public ETNode64 getByKey(byte[] key, IETStorage64 storage) {
		ETNode64 node = getByKey(key, storage, 0);
		return node;
	}

	public static int keySize(byte[] key) {
		int r = (key.length * 8) % 6;
		return key.length * 8 / 6 + (r == 0 ? 0 : 1);
	}

	public static int keyIndexAt(byte[] key, int deep) {
		int start = deep / 4 * 3;
		int offset = deep % 4;
		int remainder = key.length % 3;
		if (start <= key.length - remainder - 3) {
			switch (offset) {
			case 0:
				return (key[start] & 0xff) >> 2;
			case 1:
				return ((key[start] & 0x03) << 4) + ((key[start + 1] & 0xf0) >> 4);
			case 2:
				return ((key[start + 1] & 0x0f) << 2) + ((key[start + 2] & 0xff) >> 6);
			case 3:
				return (key[start + 2] & 0x03f);
			}
		} else if (start <= key.length - remainder) {
			if (remainder == 1) {
				switch (offset) {
				case 0:
					return (key[start] & 0xff) >> 2;
				case 1:
					return ((key[start] & 0x03) << 4);
				default:
					throw new RuntimeException("deep invalid");
				}
			} else if (remainder == 2) {
				switch (offset) {
				case 0:
					return (key[start] & 0xff) >> 2;
				case 1:
					return ((key[start] & 0x03) << 4) + ((key[start + 1] & 0xf0) >> 4);
				case 2:
					return ((key[start + 1] & 0x0f) << 2);
				default:
					throw new RuntimeException("deep invalid");
				}
			} else {
				throw new RuntimeException("deep invalid");
			}
		} else {
			throw new RuntimeException("deep invalid");
		}
		return 0;
	}

	public ETNode64 getByKey(byte[] key, IETStorage64 storage, int deep) {
		if (this.key != null && Arrays.equals(this.key, key)) {
			return this;
		}
		int keySize = keySize(key);

		ETNode64 child = NULL_NODE;
		if (deep < keySize) {
			int idx = keyIndexAt(key, deep);
			if (children[idx] != null && children[idx] != NULL_NODE) {
				child = children[idx];
			} else if (childrenHashs[idx] != null) {
				child = fromBytes(childrenHashs[idx], storage.esGet(childrenHashs[idx]));
				children[idx] = child;
			} else {
				// child = NULL_NODE;
			}
		} else {
			child = children[TREE_RADIX];
			if (child == null && storage != null && childrenHashs[TREE_RADIX] != null) {
				child = fromBytes(childrenHashs[TREE_RADIX], storage.esGet(childrenHashs[TREE_RADIX]));
				if (child != null) {
					children[TREE_RADIX] = child;
				}
			}
		}

		if (child != null && child != NULL_NODE) {
			return child.getByKey(key, storage, deep + 1);
		}
		return NULL_NODE;
	}

	public void appendChildNode(ETNode64 node, int idx) {
		this.children[idx] = node;
		this.children[idx].setDirty(true);
		this.dirty = true;
	}

	public void overrideChildNode(int idx, byte[] v) {
		this.children[idx].setV(v);
		this.children[idx].setDirty(true);
		// this.children[idx] = node;
		this.dirty = true;
	}

	public void appendLeafNode(ETNode64 node) {
		this.children[TREE_RADIX] = node;
		this.dirty = true;
	}

	public void writeShort(byte bb[], OutputStream out) throws IOException {
		out.write(bb.length);
		out.write(bb);
	}

	public static byte[] readShort(InputStream in) throws IOException {
		int len = in.read();
		byte bb[] = new byte[len];
		in.read(bb);
		return bb;
	}

	public static byte[] readMask(InputStream in) throws IOException {
		byte[] mask = new byte[CHILDREN_MASK_BYTES];
		int readBytes = in.read(mask);
		if (readBytes != CHILDREN_MASK_BYTES) {
			log.error("date format error, read children mask bits error,need:{}, real:{}", CHILDREN_MASK_BYTES,
					readBytes);
		}
		return mask;
	}

	private static byte[] bitTestTable = new byte[] { (byte) ((1 << 0) & 0xff), (byte) ((1 << 1) & 0xff),
			(byte) ((1 << 2) & 0xff), (byte) ((1 << 3) & 0xff), (byte) ((1 << 4) & 0xff), (byte) ((1 << 5) & 0xff),
			(byte) ((1 << 6) & 0xff), (byte) ((1 << 7) & 0xff) };

	public static boolean bitTest(byte[] d, int index) {
		if (index >= d.length * 8) {
			return false;
		}
		int start = index / 8;
		int offset = index % 8;
		return (d[start] & bitTestTable[offset]) != 0;
	}

	public static void bitSet(byte[] d, int index) {
		if (index >= d.length * 8) {
			return;
		}
		int start = index / 8;
		int offset = index % 8;
		d[start] |= bitTestTable[offset];
	}

	static ReusefulLoopPool<ByteBuffer> bbPools = new ReusefulLoopPool<>();
	static int BUFFER_LIMIT = 65536;

	public static synchronized ByteBuffer borrowBuffer(int size) {
		if (size > BUFFER_LIMIT) {// 800k,65536
			log.error("alloc big buffer:" + size);
			return ByteBuffer.allocate(size);
		}
		ByteBuffer bb = bbPools.borrow();
		if (bb == null) {
			bufferAlloc.incrementAndGet();
			bb = ByteBuffer.allocateDirect(BUFFER_LIMIT);
		} else {
			bb.clear();
		}
		return bb;
	}

	static {
		for (int i = 0; i < 64; i++) {
			bbPools.retobj(borrowBuffer(BUFFER_LIMIT));
		}
	}

	public static synchronized void returnBuffer(ByteBuffer bb) {
		if (bb.limit() <= BUFFER_LIMIT && bbPools.getActiveObjs().size() < 1024) {
			bbPools.retobj(bb);
		} else {
			log.error("drop return buffer:" + bbPools.getActiveObjs().size());
		}

	}

	public byte[] toBytes(IETStorage64 storage, int deep) {
		byte[] mask = new byte[CHILDREN_MASK_BYTES];

		if (deep == 0 || deep == 1) {
			// parrell encode
			final CountDownLatch cdl = new CountDownLatch(TREE_RADIX + 1);
			ExecutorService exec = executor;
			if (deep == 1) {
				exec = executor1;
			}

			for (int i = 0; i < TREE_RADIX + 1; i++) {
				if (children[i] != null && (children[i].isDirty() || children[i].hash == null)) {
					final ETNode64 node = children[i];
					exec.submit(new Runnable() {
						@Override
						public void run() {
							try {
								if (deep == 0) {
									parrelCC0.incrementAndGet();
								} else {
									parrelCC1.incrementAndGet();
								}
								node.encode(storage, deep + 1);
							} finally {
								cdl.countDown();
							}
						}
					});
				} else {
					cdl.countDown();
				}
			}
			try {
				cdl.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		int totalLen = key.length + 1 + CHILDREN_MASK_BYTES;

		for (int i = 0; i < TREE_RADIX + 1; i++) {
			if (children[i] != null || childrenHashs[i] != null) {
				bitSet(mask, i);
			}
			if (children[i] != null) {
				byte[] childHash = children[i].encode(storage, deep + 1);
				childrenHashs[i] = childHash;
				totalLen += 65;
			}
		}
		if (v != null) {
			totalLen += v.length;
		}
		totalLen += 64;// for test

		ByteBuffer bb = null;
		try {
			bb = borrowBuffer(totalLen);
			bb.put((byte) key.length);
			bb.put(key);
			bb.put(mask);
			for (int i = 0; i < TREE_RADIX + 1; i++) {
				if (childrenHashs[i] != null) {
					bb.put((byte) childrenHashs[i].length);
					bb.put(childrenHashs[i]);
				}
			}
			if (v != null) {
				bb.put(v);
			}
			byte retbb[] = new byte[bb.position()];
			bb.rewind();
			bb.get(retbb);
			return retbb;
		} catch (Exception e) {
			log.error("eto bytes error::", e);
			return null;
		} finally {
			if (bb != null) {
				returnBuffer(bb);
			}
		}

		// try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
		// // Write KEY
		// writeShort(key, bout);
		// // Write Children Mask
		// byte[] mask = new byte[CHILDREN_MASK_BYTES];
		// for (int i = 0; i < TREE_RADIX + 1; i++) {
		// if (children[i] != null || childrenHashs[i] != null) {
		// bitSet(mask, i);
		// }
		// }
		// bout.write(mask);
		// // Write Children Hash
		//
		// // Write Value
		// if (v != null && v.length > 0) {
		// bout.write(v);
		// }
		// return bout.toByteArray();
		// } catch (Exception e) {
		// log.error("error::", e);
		// }
		// return null;
	}

	public byte[] toBytes(IETStorage64 storage, int deep, int part) {

		try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
			// Write KEY
			writeShort(key, bout);
			// Write Children Mask
			byte[] mask = new byte[CHILDREN_MASK_BYTES];
			for (int i = 0; i < TREE_RADIX + 1; i++) {
				if (children[i] != null || childrenHashs[i] != null) {
					bitSet(mask, i);
				}
			}
			bout.write(mask);
			// Write Children Hash
			if (deep == 0) {
				// parrell encode
				final CountDownLatch cdl = new CountDownLatch(TREE_RADIX + 1);
				ExecutorService exec = executor;
				// if (deep == 1) {
				// exec = executor1;
				// }
				for (int i = 0; i < TREE_RADIX + 1; i++) {
					if (children[i] != null && (children[i].isDirty() || children[i].hash == null)) {
						final ETNode64 node = children[i];
						exec.submit(new Runnable() {
							@Override
							public void run() {
								try {
									// if (deep == 0) {
									parrelCC0.incrementAndGet();
									// } else {
									// parrelCC1.incrementAndGet();
									// }

									node.encode(storage, deep + 1);
								} finally {
									cdl.countDown();
								}
							}
						});
					} else {
						cdl.countDown();
					}
				}
				cdl.await();
			}

			for (int i = 0; i < TREE_RADIX + 1; i++) {
				if (children[i] != null) {
					byte[] childHash = children[i].encode(storage, deep + 1);
					childrenHashs[i] = childHash;
					writeShort(childHash, bout);
				} else if (childrenHashs[i] != null) {
					writeShort(childrenHashs[i], bout);
				}
			}
			// Write Value
			if (v != null && v.length > 0) {
				bout.write(v);
			}
			return bout.toByteArray();
		} catch (Exception e) {
			log.error("error::", e);
		}
		return null;
	}

	public static ETNode64 dfromBytes(byte[] hash, byte[] bb) {
		// KEY|
		try (ByteArrayInputStream bin = new ByteArrayInputStream(bb)) {
			// 读取当前Node的Key
			byte[] key = readShort(bin);
			// 读取子Node掩码
			byte[] mask = readMask(bin);
			// 读取子Node的Hash
			byte[][] childrenHash = new byte[TREE_RADIX + 1][];
			for (int i = 0; i < TREE_RADIX + 1; i++) {
				if (bitTest(mask, i)) {
					childrenHash[i] = readShort(bin);
				}
			}
			// 其他信息
			byte[] bbv = null;
			if (bin.available() > 0) {
				bbv = new byte[bin.available()];
				bin.read(bbv);
			}
			return new ETNode64(hash, key, bbv, childrenHash);
		} catch (Exception e) {
			throw new RuntimeException("parse etnode error:" + bb.length, e);
		}
	}

	public static ETNode64 fromBytes(byte[] hash, byte[] bb) {
		// KEY|

		try (ByteArrayInputStream bin = new ByteArrayInputStream(bb)) {
			// 读取当前Node的Key
			byte[] key = readShort(bin);
			// 读取子Node掩码
			byte[] mask = readMask(bin);
			// 读取子Node的Hash
			byte[][] childrenHash = new byte[TREE_RADIX + 1][];
			for (int i = 0; i < TREE_RADIX + 1; i++) {
				if (bitTest(mask, i)) {
					childrenHash[i] = readShort(bin);
				}
			}
			// 其他信息
			byte[] bbv = null;
			if (bin.available() > 0) {
				bbv = new byte[bin.available()];
				bin.read(bbv);
			}
			return new ETNode64(hash, key, bbv, childrenHash);
		} catch (Exception e) {
			throw new RuntimeException("parse etnode error:" + bb.length, e);
		}
	}

	public ETNode64(byte[] key, byte[] v) {
		this.key = key;
		this.v = v;
	}

	public void flushMemory(int deep) {
		if (deep < 2) {
			for (int i = 0; i < TREE_RADIX + 1; i++) {
				if (children[i] != null) {
					children[i].flushMemory(deep + 1);
				}
			}
		} else {
			for (int i = 0; i < TREE_RADIX + 1; i++) {
				children[i] = null;
			}
		}
	}

	public ETNode64(byte[] hash) {
		this.key = ROOT_KEY;
		this.hash = hash;
	}

	public ETNode64 delete(byte[] key, IETStorage64 storage) {
		ETNode64 node = getByKey(key, storage, 0);
		node.setDeleted(true);
		return node;
	}

	public ETNode64(byte[] hash, byte[] key, byte[] v, byte[] childrenHash[]) {
		this.hash = hash;
		this.key = key;
		this.v = v;
		this.childrenHashs = childrenHash;
		this.dirty = false;
	}

}
