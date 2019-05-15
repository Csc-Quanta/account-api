package org.csc.account.api;

import java.math.BigInteger;
import java.util.List;

import org.csc.account.bean.HashPair;
import org.csc.evmapi.gens.Tx;

import com.google.protobuf.ByteString;

public interface IConfirmTxMap {

	boolean containsKey(ByteString hash);

	HashPair getHP(ByteString hash);

	void confirmTx(HashPair pair, BigInteger bits);

	void confirmTx(ByteString key, BigInteger bits);

	List<Tx.Transaction> poll(int maxsize);

	HashPair invalidate(ByteString key);

	HashPair revalidate(ByteString key);

	List<Tx.Transaction> poll(int maxsize, int minConfirm);

	int getMaxElementsInMemory();

	int getQueueSize();
	int getRemoveSize();

	/**
	 * 交易入块后清理缓存的确认交易
	 */
	void clear();

	/**
	 * 交易入块后清理缓存的确认交易
	 * @param blockNumber 当前BlockNumber
	 */
	void clear(long blockNumber);

	int clearQueue();

	int clearRemoveQueue();

	int clearStorage();

	int size();
}