package org.csc.account.api;

import com.google.protobuf.ByteString;
import org.csc.account.bean.HashPair;
import org.csc.evmapi.gens.Tx;

import java.math.BigInteger;
import java.util.List;

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
	void clear();

	int clearQueue();

	int clearRemoveQueue();

	int clearStorage();

	int size();
}