package org.csc.account.bean;

import org.csc.evmapi.gens.Block.BlockEntity;

import lombok.Data;

@Data
public class BlockStoreSummary {
	private BLOCK_BEHAVIOR behavior;
	private BlockEntity block;

	public enum BLOCK_BEHAVIOR {
		DROP, EXISTS_DROP, EXISTS_PREV, CACHE, APPLY, APPLY_CHILD, STORE, DONE, ERROR
	}
}
