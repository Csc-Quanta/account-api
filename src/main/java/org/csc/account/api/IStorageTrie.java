package org.csc.account.api;

public interface IStorageTrie {

	void setRoot(byte[] root);

	byte[] get(byte[] key);

	void delete(byte[] key);
	
	void put(byte[] key, byte[] value) ;
	
	byte[] getRootHash();

	void clear();
}