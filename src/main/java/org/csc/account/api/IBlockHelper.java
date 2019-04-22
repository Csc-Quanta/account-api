package org.csc.account.api;

import com.google.protobuf.ByteString;
import org.csc.account.exception.BlockException;
import org.csc.account.gens.Blockimpl.AddBlockResponse;
import org.csc.evmapi.gens.Act.Account;
import org.csc.evmapi.gens.Block.BlockEntity;
import org.csc.evmapi.gens.Tx;

import java.util.LinkedList;
import java.util.List;

public interface IBlockHelper {

	/**
	 * 创建新区块
	 * 
	 * @param extraData
	 * @return
	 * @throws BlockException
	 */
	BlockEntity createNewBlock(String extraData, int confirmRecvCount, String term, List<String> partAddress, String nodeBit) throws BlockException;

	/**
	 * 创建新区块
	 * 
	 * @param txCount
	 * @param extraData
	 * @return
	 * @throws BlockException
	 */
	BlockEntity createNewBlock(int txCount, int confirmRecvCount, String extraData, String term, List<String> partAddress, String nodeBit)
			throws BlockException;

	/**
	 * 创建新区块
	 * 
	 * @param txs
	 * @param extraData
	 * @return
	 * @throws BlockException
	 */
	BlockEntity createNewBlock(List<Tx.Transaction> txs, String extraData, String term, List<String> partAddress, String nodeBit) throws BlockException;

	/**
	 * 创建创世块
	 * 
	 * @param txs
	 * @param extraData
	 * @throws BlockException
	 */
	void createGenesisBlock(LinkedList<Account> accounts, LinkedList<Tx.Transaction> txs, String extraData)
			throws BlockException;

	AddBlockResponse ApplyBlock(ByteString bs) throws BlockException;

	AddBlockResponse ApplyBlock(ByteString bs, boolean fastApply) throws BlockException;

	AddBlockResponse ApplyBlock(BlockEntity.Builder block) throws BlockException;

	AddBlockResponse ApplyBlock(BlockEntity.Builder block, boolean fastApply) throws BlockException;

	/**
	 * 根据区块Hash获取区块信息
	 * 
	 * @param blockHash
	 * @return
	 * @throws BlockException
	 */
	BlockEntity.Builder getBlock(String blockHash) throws BlockException;

	/**
	 * 获取节点最后一个区块
	 * 
	 * @return
	 * @throws BlockException
	 */
	BlockEntity.Builder GetBestBlock() throws BlockException;

	/**
	 * 获取Block中的全部交易
	 * 
	 * @param blockHash
	 * @return
	 * @throws BlockException
	 */
	LinkedList<Tx.Transaction> GetTransactionsByBlock(String blockHash) throws BlockException;

	/**
	 * 根据交易，获取区块信息
	 * 
	 * @param txHash
	 * @return
	 * @throws BlockException
	 */
	BlockEntity getBlockByTransaction(byte[] txHash) throws BlockException;

	/**
	 * 根据账户地址，读取账户关联的交易(可以增加遍历的区块的数量)
	 * 
	 * @param address
	 * @return
	 * @throws BlockException
	 */
	LinkedList<Tx.Transaction> getTransactionByAddress(String address, int blockCount) throws BlockException;
}