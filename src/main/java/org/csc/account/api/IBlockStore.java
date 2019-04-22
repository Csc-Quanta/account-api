package org.csc.account.api;

import org.csc.account.bean.BlockStoreNodeValue;
import org.csc.evmapi.gens.Block.BlockEntity;

import java.util.Iterator;
import java.util.Map;

/**
 * Block存储管理
 * 添加分片查询接口
 * @author lance
 * @since 2019.1.18 11:42
 */
public interface IBlockStore {
    boolean containKey(String hash);

    BlockEntity get(String hash);

    boolean add(BlockEntity block);

    BlockEntity getBlockByNumber(long number);

    BlockEntity rollBackTo(long number);

    void clear();

    Iterator<Map.Entry<String, BlockStoreNodeValue>> getUnStableBlocks(long number);

    long getMaxReceiveNumber();

    long getCacheStableBlockSize();

    long getMaxConnectNumber();

    long getMaxStableNumber();
}
