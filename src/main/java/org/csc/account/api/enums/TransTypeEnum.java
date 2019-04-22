package org.csc.account.api.enums;

import java.util.Arrays;

/**
 * 交易类型
 * @author lance
 * @since 2019.3.12 13:59
 */
public enum TransTypeEnum {
    /**默认交易类型*/
	TYPE_DEFAULT(0),
    /**创建联合账户*/
    TYPE_CreateUnionAccount(1),
    /**ERC20 Token交易*/
    TYPE_TokenTransaction(2),
    /**联合账户转账交易*/
    TYPE_UnionAccountTransaction(3),
    /**调用内部交易*/
    TYPE_CallInternalFunction(4),
    /**调用内部交易*/
    TYPE_CryptoTokenTransaction(5),
    /**锁定Token交易*/
    TYPE_LockTokenTransaction(6),
    /**创建合约交易*/
    TYPE_CreateContract(7),
    /**调用合约交易*/
    TYPE_CallContract(8),
    /**创建ERC20交易*/
    TYPE_CreateToken(9),
    /**创建ERC721交易*/
    TYPE_CreateCryptoToken(10),
    /**仲裁交易*/
    TYPE_Sanction(11),
    /**联合账户token交易*/
    TYPE_UnionAccountTokenTransaction(12),
    /**增发Token交易*/
    TYPE_MintToken(13),
    /**燃烧Token交易*/
    TYPE_BurnToken(14),
    /**冻结Token交易*/
    TYPE_freezeToken(15),
    /**解冻Token交易*/
    TYPE_unfreezeToken(16),
    /**子链注册交易*/
    TYPE_sideChainReg(17),
    /**子链同步交易*/
    TYPE_sideChainSync(18),
    /**主链转出交易*/
    TYPE_DOWN_TRANS(19),
    /**线下交易上主链*/
    TYPE_UP_TRANS(20),
    /**挖矿交易*/
    TYPE_CoinBase(888);

	private int value;
	TransTypeEnum(int value) {
		this.value = value;
	}

	public int value() {
		return this.value;
	}

	public static TransTypeEnum trans(int value) {
		return Arrays.stream(values()).filter(f->f.value == value).findFirst().orElse(TYPE_DEFAULT);
	}
}
