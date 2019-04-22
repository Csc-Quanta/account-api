package org.csc.account.bean;

import com.google.protobuf.ByteString;
import org.csc.evmapi.gens.Block;
import org.csc.evmapi.gens.Tx;

import java.util.Arrays;

/**
 * Transaction状态
 * @author lance
 * @since 2019.1.23 20:29
 */
public class TXStatus {
    private static ByteString DONE = ByteString.copyFromUtf8("D");
    private static ByteString ERROR = ByteString.copyFromUtf8("E");

    public static boolean isDone(Tx.TransactionOrBuilder tx) {
        return Arrays.equals(DONE.toByteArray(), tx.getStatus().toByteArray());
    }

    public static boolean isError(Tx.TransactionOrBuilder tx) {
        return Arrays.equals(ERROR.toByteArray(), tx.getStatus().toByteArray());
    }

    public static boolean isProccessed(Tx.TransactionOrBuilder mtx) {
        return isDone(mtx) || isError(mtx);
    }

    public static void setDone(Tx.Transaction.Builder tx, Block.BlockEntity block) {
        setDone(tx, block, ByteString.EMPTY);
    }

    /**
     * 交易完成后, 设置交易状态, 设置blockNumber/Hash
     * @param tx        当前的Tx
     * @param block     当前Tx所在的block
     * @param result    Tx执行结果
     */
    public static void setDone(Tx.Transaction.Builder tx, Block.BlockEntity block, ByteString result) {
        setTx(tx, block, result, DONE);
    }

    public static void setError(Tx.Transaction.Builder tx, Block.BlockEntity block) {
        setError(tx, block, ByteString.EMPTY);
    }

    public static void setError(Tx.Transaction.Builder tx, Block.BlockEntity block, ByteString result) {
        setTx(tx, block, result, ERROR);
    }

    /**
     * 交易完成后, 设置交易状态, 设置blockNumber/Hash
     * @param tx        当前的Tx
     * @param block     当前Tx所在的block
     * @param result    Tx执行结果
     */
    private static void setTx(Tx.Transaction.Builder tx, Block.BlockEntity block, ByteString result, ByteString status){
        tx.setStatus(status);
        tx.setResult(result);

        if(block != null){
            tx.setNumber(block.getHeader().getNumber());
            tx.setBlockHash(block.getHeader().getHash());
        }
    }

    public static String getDoneStr() {
        return DONE.toStringUtf8();
    }

    public static String getErrorStr() {
        return ERROR.toStringUtf8();
    }
}
