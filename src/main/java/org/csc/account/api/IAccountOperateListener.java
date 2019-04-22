package org.csc.account.api;

import com.google.protobuf.ByteString;

public interface IAccountOperateListener {

	void offerNewAccount(ByteString hexaddress, int nonce);

}