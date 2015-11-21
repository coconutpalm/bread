package com.coconut_palm_software.bread.ui;

import org.eclipse.ui.IStartup;

import com.coconut_palm_software.bread.lib.index.NetBibleIndexer;

public class CheckIndexes implements IStartup {

	@Override
	public void earlyStartup() {
		// Check / rebuild Net bible index
		NetBibleIndexer.getDefault();
	}

}
