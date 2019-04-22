package org.csc.account.tree;

import java.util.HashMap;

/**
 * 默认存储
 *
 * @author lance
 * 2019.3.27 20:54
 */
public class DefaultStorage implements IETStorage {
    private HashMap<String, byte[]> storage = new HashMap<>();

    @Override
    public byte[] esPut(String key, byte[] value) {
        return storage.put(key, value);
    }

    @Override
    public byte[] esRemove(Object key) {
        return storage.remove(key);
    }

    @Override
    public byte[] esget(String key) {
        return storage.get(key);
    }

    public int size() {
        return storage.size();
    }

    public String content() {
        return storage.toString();
    }
}
