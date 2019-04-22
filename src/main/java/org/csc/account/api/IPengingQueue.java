package org.csc.account.api;

import java.util.List;

public interface IPengingQueue<T> {

	void shutdown();

	void addElement(T hp);

	void addLast(T hp);

	int size();
	
	boolean hasMoreElement();

	T pollFirst();

	List<T> poll(int size);
	String getStatInfo() ;
}