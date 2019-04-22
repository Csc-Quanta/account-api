package org.csc.account.api;

import com.google.protobuf.ByteString;
import org.csc.account.exception.BlockException;
import org.csc.bcapi.backend.ODBSupport;
import org.csc.evmapi.gens.Act.*;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

/**
 * <dl>
 * <dt>添加分片方法：</dt>
 * <dd>1.根据地址获取当前地址所在的分片</dd>
 * <dd>2.获取当前节点所在分片</dd>
 * </dl>
 *
 * @author lance
 * @since 2019.1.8 14:20
 */
public interface IAccountHelper {

    /**
     * 查询账户
     * @param address
     * @return
     */
    Account.Builder getAccount(ByteString address);

    /**
     * 从DB中查询账户信息
     *
     * @param address 地址
     * @return 账户
     */
    Account.Builder getAccountFromDB(ByteString address);

    List<AccountContractValue> getContractByCreator(ByteString address);

    ODBSupport getCommonDao();

    String getKeyConstantPWD();

    void setKeyConstantPWD(String pwd);

    /**
     * 获取用户账户，如果用户不存在，则创建账户
     *
     * @param address
     * @return
     */
    Account.Builder getAccountOrCreate(ByteString address);

    /**
     * 获取用户账户的Balance
     *
     * @param address
     * @return
     * @throws BlockException
     */
    BigInteger getBalance(ByteString address) throws BlockException;

    BigInteger getTokenBalance(Account.Builder oAccount, ByteString token) throws BlockException;

    List<AccountCryptoToken> getCryptoTokenBalance(ByteString address, ByteString symbol) throws BlockException;

    /**
     * 根据token的索引，返回cryptotoken的对象
     *
     * @param symbol
     * @param index
     * @return
     */
    AccountCryptoToken getCryptoTokenByIndex(ByteString symbol, int index);

    AccountCryptoToken getCryptoTokenByIndex(Account recordAccount, ByteString symbol, int index);


    CryptoTokenOrigin.Builder getCryptoBySymbol(Account cryptoRecordAccount, byte[] symbol);

    /**
     * 根据symbol和crypotoken hash，获取cryptotoken对象
     *
     * @param hash
     * @return
     */
    AccountCryptoToken getCryptoTokenByHash(ByteString symbol, byte[] hash);

    AccountCryptoToken getCryptoTokenByHash(Account cryptoAccount, byte[] hash);

    IStorageTrie getStorageTrie(Account.Builder oAccount);

    boolean isExist(ByteString address) throws BlockException;

    /**
     * 判断账户地址是否是合约账户
     *
     * @param address
     * @return
     */
    boolean isContract(ByteString address);

    /**
     * 判断token是否已经存在
     *
     * @param token
     * @return
     * @throws BlockException
     */
    boolean isExistsToken(ByteString token) throws BlockException;

    /**
     * 判断crypto token是否已经存在
     *
     * @param symbol
     * @return
     * @throws BlockException
     */
    boolean isExistsCryptoToken(ByteString symbol) throws BlockException;

    Account.Builder createAccount(ByteString address);

    void putAccountValue(final ByteString address, final AccountValue oAccountValue, boolean stateable);

    void putAccountValue(ByteString address, AccountValue oAccountValue);

    void putAccountValue(ByteString address, ByteString values);

    void createContract(ByteString address, ByteString contract) throws BlockException;

    Account.Builder createUnionAccount(ByteString address, ByteString max, ByteString acceptMax, int acceptLimit,
                                       List<ByteString> addresses);

    byte[] getStorage(ByteString address, byte[] key);

    byte[] getStorage(Account.Builder oAccount, byte[] key);

    BigInteger subBalance(Account.Builder oAccount, BigInteger balance) throws BlockException;

    BigInteger addTokenBalance(ByteString address, ByteString token, BigInteger balance) throws BlockException;

    Account addBalance(ByteString address, BigInteger balance) throws BlockException;

    BigInteger addBalance(Account.Builder oAccount, BigInteger balance) throws BlockException;

    BigInteger addTokenBalance(Account.Builder oAccount, ByteString token, BigInteger balance) throws BlockException;

    BigInteger subTokenBalance(Account.Builder oAccount, ByteString token, BigInteger balance) throws BlockException;

    BigInteger addTokenLockBalance(Account.Builder oAccount, ByteString token, BigInteger balance) throws BlockException;

    void putStorage(Account.Builder oAccount, byte[] key, byte[] value);

    void batchPutAccounts(Map<ByteString, Account.Builder> accountValues);

    int getNonce(ByteString address) throws BlockException;

    List<ERC20TokenValue> getTokens(ByteString address, ByteString token);

    /**
     * 获取当前分片
     *
     * @return SliceId
     */
    int getCurrentSlice();

    /**
     * 获取总的分片数量
     *
     * @return total
     */
    int getSliceTotal();

    /**
     * 验证分片是否在当前节点
     *
     * @param slice 分片号
     * @return true/false
     */
    boolean validSlice(int slice);

    /**
     * 是否能执行
     *
     * @param slice 分片号
     * @return 能执行
     */
    default boolean canExecute(int slice) {
        return getSliceTotal() <= 0 || (getSliceTotal() > 0 && validSlice(slice));
    }

    ERC20TokenValue getToken(String tokenName);
}