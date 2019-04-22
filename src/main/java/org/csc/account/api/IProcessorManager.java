package org.csc.account.api;

public interface IProcessorManager {

	IProcessor getProcessor(int version);

}