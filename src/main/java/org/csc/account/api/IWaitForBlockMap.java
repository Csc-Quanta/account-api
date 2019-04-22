package org.csc.account.api;

import java.util.Map;

import org.csc.account.bean.HashPair;

public interface IWaitForBlockMap {

	void put(String key, HashPair val);

	HashPair get(String key);

	void delete(String key);

	int size();

	void updateBatch(Map<String, HashPair> rows);

}