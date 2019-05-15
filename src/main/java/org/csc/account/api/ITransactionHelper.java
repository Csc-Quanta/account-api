package org.csc.account.api;

import com.google.protobuf.ByteString;

import org.csc.account.bean.HashPair;
import org.csc.account.bean.StatsInfo;
import org.csc.account.exception.BlockException;
import org.csc.account.gens.Tximpl.MultiTransactionImpl;
import org.csc.evmapi.gens.Act.Account;
import org.csc.evmapi.gens.Block.BlockEntity;
import org.csc.evmapi.gens.Tx;
import org.csc.evmapi.gens.Tx.BroadcastTransactionMsg;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

public interface ITransactionHelper {

	void confirmBlock(String blockHash);
	
	void invalid();

	/**
	 * 保存交易方法。 交易不会立即执行，而是等待被广播和打包。只有在Block中的交易，才会被执行。 交易签名规则 1. 清除signatures 2.
	 * txHash=ByteString.EMPTY 3. 签名内容=oMultiTransaction.toByteArray()
	 * 
	 * @param oMultiTransaction
	 * @throws BlockException
	 */
	HashPair CreateMultiTransaction(Tx.Transaction.Builder oMultiTransaction) throws BlockException;

	String CreateGenesisMultiTransaction(Tx.Transaction.Builder oMultiTransaction) throws BlockException;

	/**
	 * 广播交易方法。 交易广播后，节点收到的交易会保存在本地db中。交易不会立即执行，而且不被广播。只有在Block中的交易，才会被执行。
	 * 
	 * @param tx
	 * @throws BlockException
	 */
	void syncTransaction(Tx.Transaction.Builder tx, BigInteger bits) throws BlockException;

	void syncTransaction(Tx.Transaction.Builder oMultiTransaction) throws BlockException;

	void syncTransaction(Tx.Transaction.Builder oMultiTransaction, boolean isBroadCast);

	void syncTransaction(Tx.Transaction.Builder oMultiTransaction, boolean isBroadCast, BigInteger bits);

	void syncTransactionBatch(List<Tx.Transaction.Builder> oMultiTransaction, BigInteger bits) throws BlockException;

	boolean containConfirm(ByteString hash, int bit);

	void syncTransactionBatch(List<Tx.Transaction.Builder> oMultiTransaction, boolean isBroadCast, BigInteger bits);

	BroadcastTransactionMsg getWaitSendTxToSend(int count) throws BlockException;

	/**
	 * 从待打包交易列表中，查询出等待打包的交易。
	 * 
	 * @param count
	 * @return
	 */
	List<Tx.Transaction> getWaitBlockTx(int count, int confirmTimes);

	void confirmRecvTx(ByteString key, BigInteger fromBits);

	HashPair removeWaitingSendOrBlockTx(ByteString hash) throws BlockException;

	boolean isExistsWaitBlockTx(ByteString hash) throws BlockException;

	/**
	 * 根据交易Hash，返回交易实体。
	 * 
	 * @param hash
	 * @return
	 * @throws BlockException
	 */
	Tx.Transaction GetTransaction(ByteString hash) throws BlockException;

	/**
	 * 获取交易签名后的Hash
	 * 
	 * @param oTransaction
	 * @throws BlockException
	 */
	void getTransactionHash(Tx.Transaction.Builder oTransaction) throws BlockException;

	/**
	 * 映射为接口类型
	 * 
	 * @param oTransaction
	 * @return
	 */
	MultiTransactionImpl.Builder parseToImpl(Tx.Transaction oTransaction);

	Tx.Transaction.Builder parse(MultiTransactionImpl oTransaction) throws BlockException;

	/**
	 * 校验并保存交易。该方法不会执行交易，用户的账户余额不会发生变化。
	 * 
	 * @param oMultiTransaction
	 * @throws BlockException
	 */
	HashPair verifyAndSaveMultiTransaction(Tx.Transaction.Builder oMultiTransaction) throws BlockException;

	void resetActuator(ITransactionActuator actuator, BlockEntity oCurrentBlock);

	/**
	 * @param transactionType
	 * @return
	 */
	ITransactionActuator getActuator(int transactionType, BlockEntity oCurrentBlock);

	byte[] getTransactionContent(Tx.Transaction oTransaction);

	void verifySignature(String pubKey, String signature, byte[] tx) throws BlockException;

	void setTransactionDone(Tx.Transaction tx, BlockEntity be, ByteString result) throws BlockException;

	void setTransactionError(Tx.Transaction tx, BlockEntity be, ByteString result) throws BlockException;

	/**
	 * generate contract address by transaction
	 * 
	 * @param oMultiTransaction
	 * @return
	 */
	ByteString getContractAddressByTransaction(Tx.Transaction oMultiTransaction) throws BlockException;

	boolean isExistsTransaction(ByteString hash);

	Map<ByteString, Account.Builder> getTransactionAccounts(Tx.Transaction.Builder oMultiTransaction);

	Map<ByteString, Account.Builder> merageTransactionAccounts(Tx.Transaction.Builder oMultiTransaction,
			Map<ByteString, Account.Builder> current);

	Map<ByteString, Account.Builder> merageTransactionAccounts(Tx.Transaction oMultiTransaction,
			Map<ByteString, Account.Builder> current);

	void merageSystemAccounts(Map<ByteString, Account.Builder> current);
	
	StatsInfo getStats();
	
	long getSendingSize();
	
	long getConfirmSize();

	List<ByteString> getRelationAccount(Tx.Transaction oMultiTransaction);
	
	
	public void setTxSaveDeferMode(boolean defermode);
	
	public boolean isTxSaveDeferMode() ;
	public void flushDeferCache() ;
}