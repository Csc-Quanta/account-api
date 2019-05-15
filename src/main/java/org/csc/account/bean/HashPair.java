package org.csc.account.bean;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.csc.account.tree.EHelper;
import org.csc.evmapi.gens.Tx;

import java.io.Serializable;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * 存储hash->Tx
 *
 * @author lance
 * @since 2019.1.23 20:56
 */
@Data
public class HashPair implements Serializable, IStorable {
	private static final long serialVersionUID = 5829951203336980749L;

	@Getter(lombok.AccessLevel.NONE)
	@Setter(lombok.AccessLevel.NONE)
	private transient ByteString key;
	private String hexKey;
	private byte[] data;
	private transient Tx.Transaction tx;
	private BigInteger bits;
	private boolean isRemoved = false;
	private boolean isNeedBroadCast = false;
	private long lastUpdateTime = System.currentTimeMillis();
	private boolean isStoredInDisk = false;

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csc.account.bean.IStorable#toBytes(java.nio.ByteBuffer)
	 */
	@Override
	public void toBytes(ByteBuffer buff) {
		byte[] keybb = hexKey.getBytes();
		byte bitsbb[] = bits.toByteArray();

		int totalSize = (4 + keybb.length) + (4 + data.length) + (4 + bitsbb.length) + 3 + 8;
		buff.putInt(totalSize);

		buff.putInt(keybb.length);
		buff.put(keybb);

		buff.putInt(data.length);
		buff.put(data);

		buff.putInt(bitsbb.length);
		buff.put(bitsbb);

		if (isRemoved) {
			buff.put((byte) 1);
		} else {
			buff.put((byte) 0);
		}
		if (isNeedBroadCast) {
			buff.put((byte) 1);
		} else {
			buff.put((byte) 0);
		}
		buff.putLong(lastUpdateTime);
		if (isStoredInDisk) {
			buff.put((byte) 1);
		} else {
			buff.put((byte) 0);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.csc.account.bean.IStorable#fromBytes(java.nio.ByteBuffer)
	 */
	@Override
	public void fromBytes(ByteBuffer buff) {
		int totalSize = buff.getInt();

		int len = buff.getInt();
		byte[] hexKeybb = new byte[len];
		buff.get(hexKeybb);
		hexKey = new String(hexKeybb);

		len = buff.getInt();
		data = new byte[len];
		buff.get(data);

		len = buff.getInt();
		byte bitsbb[] = new byte[len];
		buff.get(bitsbb);
		bits = new BigInteger(bitsbb);

		isRemoved = (buff.get() == 1);
		isNeedBroadCast = (buff.get() == 1);

		lastUpdateTime = buff.getLong();
		isStoredInDisk = (buff.get() == 1);
	}

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	public ByteString getKey() {
		if (key == null) {
			return ByteString.copyFrom(EHelper.encAPI.hexDec(hexKey));
		} else {
			return key;
		}
	}

	public Tx.Transaction getTx() {
		if (tx == null && data != null) {
			try {
				tx = Tx.Transaction.parseFrom(data);
			} catch (InvalidProtocolBufferException e) {
			}
		}
		return tx;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof HashPair) {
			HashPair hp = (HashPair) obj;
			return Arrays.equals(hp.getKey().toByteArray(), key.toByteArray()) && Arrays.equals(hp.getData(), data);
		} else {
			return false;
		}
	}

	public synchronized void setBits(BigInteger bits) {
		this.bits = this.bits.or(bits);
	}

	public byte[] getKeyBytes() {
		return key.toByteArray();
	}

	public HashPair() {

	}

	public HashPair(ByteString key, Tx.Transaction tx) {
		super();
		this.key = key;
		this.hexKey = EHelper.encAPI.hexEnc(key.toByteArray());
		this.tx = tx;
		this.data = tx.toByteArray();
		this.bits = BigInteger.ZERO;
	}

	public HashPair(ByteString key, byte[] data, Tx.Transaction tx) {
		super();
		this.key = key;
		this.hexKey = EHelper.encAPI.hexEnc(key.toByteArray());
		this.tx = tx;
		this.data = data;
		this.bits = BigInteger.ZERO;
	}

	@Override
	public long calcSize() {
		return data.length + hexKey.length() * 3 + bits.bitLength() / 8 + 1 + 1024;
	}
}
