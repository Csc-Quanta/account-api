package org.csc.account.api;

import com.google.protobuf.ByteString;
import org.csc.account.bean.BlockStoreSummary;
import org.csc.account.exception.BlockException;
import org.csc.account.exception.BlockNotFoundInStoreException;
import org.csc.account.util.NodeDef;
import org.csc.evmapi.gens.Block.BlockEntity;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;

/**
 * 添加分片查询接口
 *
 * @author lance
 * @since 2019.1.18 11:48
 */
public interface IChainHelper {

	/**
	 * 获取节点最后一个区块的Hash
	 *
	 * @return
	 * @throws BlockException
	 */
	String GetStableBestBlockHash() throws BlockException;

	BlockEntity GetStableBestBlock() throws BlockException;

	String GetConnectBestBlockHash() throws BlockException;

	BlockEntity GetConnectBestBlock() throws BlockException;
	
	BlockEntity GetConnectLastBlock() throws BlockException;

	BlockEntity confirmBlock(String blockHash);

	/**
	 * 获取节点区块个数
	 *
	 * @return
	 */
	long getBlockCount();

	/**
	 * 获取节点信息
	 *
	 * @return
	 */
	NodeDef getNode();

	/**
	 * 获取网络
	 *
	 * @return
	 */
	String getNet();

	/**
	 * 获取adminkey
	 *
	 * @return
	 */
	String getAdminKey();

	/**
	 * 获取节点网络
	 *
	 * @return
	 */
	String getNodeNet();

	BigInteger getMaxTokenTotal();

	BigInteger getMinTokenTotal();

	String getKeystoreNumber();

	/**
	 * 获取最后一个区块的索引
	 *
	 * @return
	 * @throws BlockException
	 */
	long getLastStableBlockNumber() throws BlockException;

	long getLastBlockNumber();

	/**
	 * 获取创世块
	 *
	 * @return
	 * @throws BlockException
	 */
	BlockEntity getGenesisBlock() throws BlockException;

	boolean isExistsGenesisBlock() throws BlockException;

	/**
	 * 向区块链中加入新的区块
	 *
	 * @param oBlock
	 * @return
	 * @throws BlockNotFoundInStoreException
	 */
	BlockStoreSummary connectBlock(BlockEntity oBlock) throws BlockNotFoundInStoreException;

	BlockStoreSummary stableBlock(BlockEntity oBlock);

	List<BlockEntity> getChildBlock(BlockEntity oBlock);

	BlockEntity rollbackTo(long number);

	BlockStoreSummary addBlock(BlockEntity oBlock);

	BlockStoreSummary tryAddBlock(BlockEntity applyBlock);

	/**
	 * 从一个块开始遍历整个区块链，返回该块的所有子孙
	 *
	 * @param blockHash
	 * @return
	 * @throws BlockException
	 */
	LinkedList<BlockEntity> getBlocks(byte[] blockHash) throws BlockException;

	/**
	 * 从一个块开始遍历整个区块链,到一个块结束，返回区间内的区块。默认返回200块
	 *
	 * @param blockHash
	 * @param endBlockHash
	 * @return 200块
	 * @throws BlockException
	 */
	LinkedList<BlockEntity> getBlocks(byte[] blockHash, byte[] endBlockHash) throws BlockException;

	/**
	 * 从一个块开始遍历整个区块链,到一个块结束，返回区间指定数量的区块
	 *
	 * @param blockHash
	 * @param endBlockHash
	 * @param maxCount
	 * @return
	 * @throws BlockException
	 */
	LinkedList<BlockEntity> getBlocks(byte[] blockHash, byte[] endBlockHash, int maxCount) throws BlockException;

	/**
	 * 从一个块开始，遍历整个整个区块，返回改块的所有父块
	 *
	 * @param blockHash
	 * @return
	 * @throws BlockException
	 */
	LinkedList<BlockEntity> getParentsBlocks(String blockHash) throws BlockException;

	/**
	 * 从一个块开始，到一个块结束，遍历整个整个区块，返回改块的所有父块
	 *
	 * @param blockHash
	 * @param endBlockHash
	 * @return 200块
	 * @throws BlockException
	 */
	LinkedList<BlockEntity> getParentsBlocks(String blockHash, String endBlockHash) throws BlockException;

	/**
	 * 从一个块开始，到一个块结束，遍历整个整个区块，返回指定数量的该块的所有父块
	 *
	 * @param blockHash
	 * @param endBlockHash
	 * @param maxCount
	 * @return
	 * @throws BlockException
	 */
	LinkedList<BlockEntity> getParentsBlocks(String blockHash, String endBlockHash, long maxCount)
			throws BlockException;

	/**
	 * 根据区块高度，获取区块信息
	 *
	 * @param number
	 * @return
	 * @throws BlockException
	 */
	BlockEntity getBlockByNumber(long number);

	/**
	 * 根据区块高度，获取一组区块信息
	 * 
	 * @param number
	 * @return
	 */
	List<BlockEntity> getBlocksByNumber(long number);

	/**
	 * 根据区块高度，获取一组已经连接的区块信息
	 * 
	 * @param number
	 * @return
	 */
	List<BlockEntity> getConnectBlocksByNumber(long number);

	/**
	 * get node account info.
	 * <p>
	 * return null while not found.
	 *
	 * @return
	 */
	String getNodeAccount();

	/**
	 * get node config account address
	 *
	 * @return config account address
	 */
	String getNodeConfigAccount();

	void onStart(String bcuid, String address, String name);

	void reloadBlockCache() throws BlockException;

	BlockEntity getBlockByHash(ByteString blockHash) throws BlockException;

	int getUnStableStorageSize();
}