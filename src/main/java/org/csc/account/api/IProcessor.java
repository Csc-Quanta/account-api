
package org.csc.account.api;

import com.google.protobuf.ByteString;
import org.csc.account.exception.BlockException;
import org.csc.account.gens.Blockimpl.AddBlockResponse;
import org.csc.evmapi.gens.Block.BlockEntity;
import org.csc.evmapi.gens.Tx;

import java.util.List;
import java.util.Map;

/**
 * 处理器
 * @author lance
 * @since 2019.1.24 00:25
 */
public interface IProcessor {
	void applyReward(BlockEntity.Builder block) throws BlockException;

	AddBlockResponse applyBlock(BlockEntity.Builder block, boolean fastApply);

	BlockEntity createNewBlock(List<Tx.Transaction> txs, String extraData, String term, List<String> partAddress, String nodebit) throws BlockException;

	Map<ByteString, ByteString> executeTransaction(List<Tx.Transaction> txs, BlockEntity block) throws BlockException;
}
