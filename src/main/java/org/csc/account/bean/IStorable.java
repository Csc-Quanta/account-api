package org.csc.account.bean;

import java.nio.ByteBuffer;

public interface IStorable {

	void toBytes(ByteBuffer buff);

	void fromBytes(ByteBuffer buff);
	
	String getHexKey();
	
	long calcSize();

}