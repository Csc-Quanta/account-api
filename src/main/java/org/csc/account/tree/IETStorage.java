package org.csc.account.tree;

public interface IETStorage {
	public byte[] esPut(String key, byte[] value);
	public byte[] esget(String key);

	public byte[] esRemove(Object key);
}
