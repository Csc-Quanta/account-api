package org.csc.account.api;

public interface IStateTrie {

	public void setRoot(byte[] root) throws Exception;

	public byte[] get(byte[] key) throws Exception;

	public void put(byte[] key, byte[] value);

	public void delete(byte[] key);

	public byte[] getRootHash();
	public void clear() ;
	public long getCacheSize();

}