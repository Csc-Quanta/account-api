package org.csc.account.tree64;

public interface IETStorage64 {
	public byte[] esPut(byte[] key, byte[] value);
	public byte[] esGet(byte[] key);

	public byte[] esRemove(Object key);
}
