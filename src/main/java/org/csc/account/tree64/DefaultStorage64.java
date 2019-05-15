package org.csc.account.tree64;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.codec.binary.Hex;

/**
 * 默认存储
 *
 * @author lance 2019.3.27 20:54
 */
public class DefaultStorage64 implements IETStorage64 {
	private Map<String, byte[]> storage = new ConcurrentHashMap<>();

	@Override
	public byte[] esPut(byte[] key, byte[] value) {
		return storage.put(Hex.encodeHexString(key), value);
	}

	@Override
	    public byte[] esRemove(Object key) {
	    	if(key instanceof String)
	        {
	    		return storage.remove(key);
	        }else {
	        	return storage.remove(Hex.encodeHexString((byte[])key));
	        }
	    }

	@Override
	public byte[] esGet(byte[] key) {
		return storage.get(Hex.encodeHexString(key));
	}

	public int size() {
		return storage.size();
	}

	public String content() {
		return storage.toString();
	}
}
