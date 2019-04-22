package org.csc.account.api;

import java.util.Map;

import com.google.common.cache.Cache;

public interface IStorageTrieCache {
	void put(String key, IStorageTrie val);

	IStorageTrie get(String key);

	void delete(String key);

	void updateBatch(Map<String, IStorageTrie> rows);

	Cache<String, IStorageTrie> getStorage();
}