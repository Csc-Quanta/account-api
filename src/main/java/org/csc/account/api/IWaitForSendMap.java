package org.csc.account.api;

import java.util.Map;

import com.google.protobuf.ByteString;
import org.csc.account.bean.HashPair;

/**
 * 待广播的Tx记录
 * @author lance
 * @since 2019.1.23 23:51
 */
public interface IWaitForSendMap {

	/**
	 * 缓存单条记录
	 * @param key	交易Hash
	 * @param val	交易对象
	 */
	void put(ByteString key, HashPair val);

	/**
	 * 缓存多条
	 * @param map	交易Map
	 */
	void put(Map<ByteString, HashPair> map);

	/**
	 * 根据Hash删除待广播的Tx记录
	 * @param key	交易hash
	 */
	void delete(ByteString key);

	/**
	 * 待广播记录数量
	 * @return
	 */
	int size();

	/**
	 * 更新待广播的Tx记录
	 * @param rows
	 */
	void updateBatch(Map<ByteString, HashPair> rows);

	/**
	 * 所有待广播的Tx记录
	 * @return
	 */
	Map<ByteString, HashPair> getStorage();
}