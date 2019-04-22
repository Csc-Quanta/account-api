package org.csc.account.api;

import java.math.BigInteger;
import java.util.List;
import java.util.Map;

import org.csc.account.exception.BlockException;
import org.csc.evmapi.gens.Act.Account;
import org.csc.evmapi.gens.Act.AccountContractValue;
import org.csc.evmapi.gens.Act.AccountCryptoToken;
import org.csc.evmapi.gens.Act.ERC20TokenValue;

import com.google.protobuf.ByteString;

public interface ImmutableAccountHelper {

	/**
	 * 账户是否存在
	 * 
	 * @param address
	 * @return
	 * @throws BlockException
	 */

	boolean isExist(ByteString address) throws BlockException;

	List<AccountContractValue> getContractByCreator(ByteString address);

	String getSyncStr(ByteString address);

	boolean isContract(ByteString address);

	/**
	 * 获取用户账户Nonce
	 * 
	 * @param address
	 * @return
	 * @throws BlockException
	 */
	int getNonce(ByteString address) throws BlockException;

	/**
	 * 获取用户账户的Balance
	 * 
	 * @param address
	 * @return
	 * @throws BlockException
	 */
	BigInteger getBalance(ByteString address) throws BlockException;

	/**
	 * 获取用户Token账户的Balance
	 * 
	 * @param address
	 * @return
	 * @throws BlockException
	 */
	BigInteger getTokenBalance(ByteString address, String token) throws BlockException;

	BigInteger getTokenBalance(Account.Builder oAccount, String token) throws BlockException;

	BigInteger getTokenLockedBalance(ByteString address, String token) throws BlockException;

	/**
	 * 获取加密Token账户的余额
	 * 
	 * @param address
	 * @param symbol
	 * @return
	 * @throws BlockException
	 */
	List<AccountCryptoToken> getCryptoTokenBalance(ByteString address, ByteString symbol) throws BlockException;

	List<ERC20TokenValue> getTokens(String address, String token);

	/**
	 * 判断token是否已经发行
	 * 
	 * @param token
	 * @return
	 * @throws BlockException
	 */
	boolean isExistsToken(String token) throws BlockException;

	boolean isExistsCryptoToken(byte[] hash) throws BlockException;

	/**
	 * 根据token的hash获取此token的信息
	 * 
	 * @param tokenHash
	 * @return
	 */
	AccountCryptoToken.Builder getCryptoTokenByTokenHash(ByteString tokenHash);

	Map<String, byte[]> getStorage(Account.Builder oAccount, List<byte[]> keys);

	Map<String, byte[]> getStorage(ByteString address, List<byte[]> keys);

	byte[] getStorage(Account.Builder oAccount, byte[] key);

	byte[] getStorage(ByteString address, byte[] key);

//	CryptoTokenValue getCryptoTokenValue(ByteString symbolBytes);

}