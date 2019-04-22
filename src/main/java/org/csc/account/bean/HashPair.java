package org.csc.account.bean;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;
import org.csc.evmapi.gens.Tx;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Arrays;

/**
 * 存储hash->Tx
 * @author lance
 * @since 2019.1.23 20:56
 */
@Data
public class HashPair implements Serializable {
	private static final long serialVersionUID = 5829951203336980749L;
	
	@Getter(lombok.AccessLevel.NONE)
	@Setter(lombok.AccessLevel.NONE)
	private transient ByteString key;
	private String hexKey;
	private byte[]data;
	private transient Tx.Transaction tx;
	private BigInteger bits ;
	private boolean isRemoved = false;
	private boolean isNeedBroadCast = false;
	private long lastUpdateTime = System.currentTimeMillis();
	private boolean isStoredInDisk = false;

	@Override
	public int hashCode() {
		return key.hashCode();
	}

	public ByteString getKey() {
		if (key == null) {
			try {
				return ByteString.copyFrom(Hex.decodeHex(hexKey));
			} catch (DecoderException e) {
				return null;
			}
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

	public HashPair(ByteString key, Tx.Transaction tx) {
		super();
		this.key = key;
		this.hexKey = Hex.encodeHexString(key.toByteArray());
		this.tx = tx;
		this.data = tx.toByteArray();
		this.bits = BigInteger.ZERO;
	}

	public HashPair(ByteString key, byte[] data, Tx.Transaction tx) {
		super();
		this.key = key;
		this.hexKey = Hex.encodeHexString(key.toByteArray());
		this.tx = tx;
		this.data = data;
		this.bits = BigInteger.ZERO;
	}
}
