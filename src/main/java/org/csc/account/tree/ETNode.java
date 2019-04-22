package org.csc.account.tree;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.csc.account.exception.EPTException;
import org.csc.account.exception.EPTParseException;
import org.csc.bcutil.Hex;
import org.csc.core.util.ByteUtil;

import java.io.*;
import java.math.BigInteger;
import java.util.Arrays;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Slf4j
public class ETNode {
	// String hashs[] = new String[EHelper.radix];
	// String childrenKeys[] = new String[EHelper.radix + 1];
	String childrenHashs[] = new String[EHelper.radix + 1];
	private String key = "ROOT";
	private byte v[];
	private boolean dirty = true;
	private boolean deleted = false;
	private byte[] hash = null;
	// private String hashKey = null;
	private byte[] contentData = null;
	private boolean leafNode = false;
	private ETNode children[] = new ETNode[EHelper.radix + 1];
	public static ETNode NULL_NODE = new ETNode("", null);

	public boolean isNullNode() {
		return this == NULL_NODE;
	}

	public void appendJsonString(StringBuffer sb) {
		if (this == NULL_NODE) {
			sb.append("{\"NULL_NODE\":true}");
		} else {
			sb.append("{");
			sb.append("\"key\":\"").append(key).append("\"");
			if (v != null) {
				sb.append(",\"v\":\"").append(Hex.toHexString(v)).append("\"");
			}
			if (hash != null) {
				sb.append(",\"hash\":\"").append(Hex.toHexString(hash)).append("\"");
			}
			for (int i = 0; i < EHelper.radix + 1; i++) {
				char ch = '_';
				if (i < EHelper.radix) {
					ch = EHelper.AlphbetMap.charAt(i);
				}
				if (children[i] != null) {
					sb.append(",\"node_").append(ch).append("\":");
					children[i].appendJsonString(sb);
					// } else if (childrenKeys[i] != null) {
					// sb.append(",\"node_").append(ch).append("\":\"").append(childrenHashs[i]).append("\"");
				}
			}

			sb.append("}");
		}
	}

	public String toJsonString() {
		StringBuffer sb = new StringBuffer();
		appendJsonString(sb);
		return sb.toString();
	}

	public ETNode deepLoad(IETStorage storage) {
		for (int i = 0; i < EHelper.radix + 1; i++) {
			if (childrenHashs[i] != null) {
				if (children[i] == null) {
					byte bb[] = storage.esget(childrenHashs[i]);
					if (bb == null) {
						throw new EPTException("hash not found:" + childrenHashs[i]);
					}
					children[i] = fromBytes(bb);
					children[i].deepLoad(storage);
				}
			}
		}
		return this;
	}

	public byte[] encode(IETStorage storage) {
		if (hash != null && !dirty) {
			return hash;
		}
		contentData = toBytes(storage);
		hash = EHelper.encAPI.sha3Encode(contentData);
		String hashKey = EHelper.bytesToMapping(hash);
		byte bb[] = EHelper.bytesFrom(hashKey);
		if (!Arrays.equals(bb, hash)) {
			hashKey = EHelper.bytesToMapping(hash);
			System.out.println("not equal===>" + hashKey + "," + Hex.toHexString(bb) + "," + Hex.toHexString(hash));
		}
		dirty = false;
		if (storage != null) {
			storage.esPut(EHelper.bytesToMapping(hash), contentData);
		}
		return hash;
	}

	public ETNode getChild(char ch, IETStorage storage) {
		int idx = EHelper.findIdx(ch);
		if (children[idx] != null) {
			return children[idx];
		} else if (childrenHashs[idx] != null && storage != null) {
			children[idx] = fromBytes(storage.esget(childrenHashs[idx]));
		}
		return children[EHelper.findIdx(ch)];
	}

	public ETNode getByKey(String key, IETStorage storage) {
		ETNode node = getByKey(key, storage, 0);
		//log.debug("getKey:" + key + ",hex=" + EHelper.encAPI.hexEnc(EHelper.mapStringToByte(key)) + ",hasStorage="+ (storage != null));
		// log.debug("hashjson=" + node.toJsonString());
		return node;
	}

	public ETNode getByKey(String key, IETStorage storage, int deep) {
		if (this.key != null && this.key.equals(key)) {
			return this;
		}
		// if (this.hashKey != null && this.hashKey.equals(key)) {
		// return this;
		// }

		ETNode child = NULL_NODE;
		if (deep > key.length() - 1) {
			child = children[EHelper.radix];
			if (child == null && storage != null) {
				child = fromBytes(storage.esget(childrenHashs[EHelper.radix]));
			}
		} else {
			char ch = key.charAt(deep);
			int idx = EHelper.findIdx(ch);
			if (children[idx] != null && children[idx] != NULL_NODE) {
				child = children[idx];
			} else if (childrenHashs[idx] != null) {
				child = fromBytes(storage.esget(childrenHashs[idx]));
			} else {
				// child = NULL_NODE;
			}

		}
		if (child != NULL_NODE) {
			return child.getByKey(key, storage, deep + 1);
		}
		return NULL_NODE;
	}

	public void appendChildNode(ETNode node, char ch) {
		log.debug("addCH:@" + ch + ",key=" + node.getKey() + ",hex="
				+ EHelper.encAPI.hexEnc(EHelper.mapStringToByte(node.getKey())));
		int idx = EHelper.findIdx(ch);
		this.children[idx] = node;
		this.children[idx].setDirty(true);
		this.dirty = true;
	}

	public void overrideChildNode(char ch, byte[] v) {
		// log.debug("overrideCH:@" + ch + ",key=" +
		// node.getKey()+",hex="+EHelper.encAPI.hexEnc(EHelper.mapStringToByte(node.getKey())));
		int idx = EHelper.findIdx(ch);
		this.children[idx].setV(v);
		this.children[idx].setDirty(true);
		// this.children[idx] = node;
		this.dirty = true;
	}

	public void appendLeafNode(ETNode node) {
		this.children[EHelper.radix] = node;
		this.dirty = true;
	}

	public void writeShortString(String key, OutputStream out) throws IOException {
		writeShort(EHelper.mapStringToByte(key), out);
	}

	public void writeShort(byte bb[], OutputStream out) throws IOException {
		out.write(bb.length);
		out.write(bb);
	}

	public static String readShortString(InputStream in) throws IOException {
		return EHelper.mapByteToString(readShort(in));
	}

	public static byte[] readShort(InputStream in) throws IOException {
		int len = in.read();
		byte bb[] = new byte[len];
		in.read(bb);
		return bb;
	}

	public byte[] toBytes(IETStorage storage) {

		try (ByteArrayOutputStream bout = new ByteArrayOutputStream()) {
			writeShortString(key, bout);
			BigInteger bint = BigInteger.ZERO;
			for (int i = 0; i < EHelper.radix + 1; i++) {
				if (children[i] != null || childrenHashs[i] != null) {
					bint = bint.setBit(i);
				}
			}
			// System.out.println("w.bint=" + bint.toString(16));
			bout.write(ByteUtil.bigIntegerToBytes(bint, 8));
			for (int i = 0; i < EHelper.radix + 1; i++) {
				if (children[i] != null) {
					byte[] childHash = children[i].encode(storage);
					childrenHashs[i] = EHelper.bytesToMapping(childHash);
					writeShort(childHash, bout);
				} else if (childrenHashs[i] != null) {
					writeShort(EHelper.bytesFrom(childrenHashs[i]), bout);
				}
			}
			if (v != null && v.length > 0) {
				bout.write(v);
			}
			return bout.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static ETNode fromBytes(byte[] bb) {
		try (ByteArrayInputStream bin = new ByteArrayInputStream(bb);) {
			String key = readShortString(bin);
			byte childbits[] = new byte[8];
			bin.read(childbits);
			BigInteger bint = ByteUtil.bytesToBigInteger(childbits);
			// System.out.println("r.bint=" + bint.toString(16));
			// String[] childrenKeys = new String[EHelper.radix + 1];
			String[] childrenHash = new String[EHelper.radix + 1];
			for (int i = 0; i < EHelper.radix + 1; i++) {
				if (bint.testBit(i)) {
					// childrenKeys[i] = readShortString(bin);
					childrenHash[i] = EHelper.bytesToMapping(readShort(bin));
				}
			}

			byte[] bbv = null;
			if (bin.available() > 0) {
				bbv = new byte[bin.available()];
				bin.read(bbv);
			}

			return new ETNode(key, bbv, childrenHash);
		} catch (Exception e) {
			throw new EPTParseException("parse etnode error:" + bb.length, e);
		}
	}

	public ETNode(String key, byte[] v) {
		this.key = key;
		this.v = v;
	}

	public void flushMemory(int deep) {
		if (deep < 8) {
			for (int i = 0; i < EHelper.radix + 1; i++) {
				if (children[i] != null) {
					children[i].flushMemory(deep + 1);
				}
			}
		} else {
			for (int i = 0; i < EHelper.radix + 1; i++) {
				if (children[i] != null) {
					if (childrenHashs[i] != null) {
						children[i] = null;//destroy 
					}
				}
			}
		}
	}

	public ETNode(byte[] hash) {
		this.key = "root";
		this.hash = hash;
		// this.hashKey = EHelper.bytesToMapping(hash);
	}

	public ETNode delete(String key, IETStorage storage) {
		ETNode node = getByKey(key, storage, 0);
		node.setDeleted(true);
		return node;
	}

	public ETNode(String key, byte[] v, String childrenHash[]) {
		this.key = key;
		this.v = v;
		// this.childrenKeys = childrenKeys;
		this.childrenHashs = childrenHash;
	}

}
