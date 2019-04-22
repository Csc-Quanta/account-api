package org.csc.account.api;

import com.google.protobuf.ByteString;
import org.csc.account.exception.BlockException;
import org.csc.evmapi.gens.Act.Account;
import org.csc.evmapi.gens.Block.BlockEntity;
import org.csc.evmapi.gens.Tx;

import java.util.Map;

public interface ITransactionActuator {
    boolean needSignature();

    Map<ByteString, Tx.Transaction> getTxValues();

    /**
     * 交易签名校验
     *
     * @param tx
     * @param accounts
     * @throws BlockException
     */
    void onVerifySignature(Tx.Transaction tx, Map<ByteString, Account.Builder> accounts) throws BlockException;

    /**
     * 交易执行前的数据校验。
     *
     * @param tx
     * @param accounts
     * @throws BlockException
     */
    void onPrepareExecute(Tx.Transaction tx, Map<ByteString, Account.Builder> accounts) throws BlockException;

    /**
     * 交易执行。
     *
     * @param tx
     * @param accounts
     * @throws BlockException
     */
    ByteString onExecute(Tx.Transaction tx, Map<ByteString, Account.Builder> accounts) throws BlockException;

    /**
     * 交易执行成功后。
     *
     * @param tx
     * @throws BlockException
     */
    void onExecuteDone(Tx.Transaction tx, BlockEntity block, ByteString result) throws BlockException;

    /**
     * 交易执行失败后。
     *
     * @param tx
     * @throws BlockException
     */
    void onExecuteError(Tx.Transaction tx, BlockEntity block, ByteString result) throws BlockException;
}
