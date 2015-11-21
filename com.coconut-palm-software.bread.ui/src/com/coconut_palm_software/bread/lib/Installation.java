package com.coconut_palm_software.bread.lib;

import org.eclipse.core.runtime.Platform;

public class Installation {

	static String basePath = null;

	public static String getBasePath() {
		if (basePath == null) {
			basePath = Platform.getInstallLocation().getURL().toExternalForm().substring(6);
		}
		return basePath;
	}

}
