package org.csc.account.tree64;

import java.util.HashMap;

/**
 * 默认存储
 *
 * @author lance
 * 2019.3.27 20:54
 */
public class DefaultStorage64 implements IETStorage64 {
    private HashMap<byte[], byte[]> storage = new HashMap<>();

    @Override
    public byte[] esPut(byte[] key, byte[] value) {
        return storage.put(key, value);
    }

    @Override
    public byte[] esRemove(Object key) {
        return storage.remove(key);
    }

    @Override
    public byte[] esGet(byte[] key) {
        return storage.get(key);
    }

    public int size() {
        return storage.size();
    }

    public String content() {
        return storage.toString();
    }
}
