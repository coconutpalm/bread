package com.coconut_palm_software.bread.ui;

import org.osgi.framework.Bundle;

public class BundleClassLoader extends ClassLoader {
	private Bundle delegate;
	
	public BundleClassLoader(Bundle delegate) {
		super();
		this.delegate = delegate;
	}

	@Override
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		try {
			return delegate.loadClass(name);
		} catch (ClassNotFoundException e) {
			return super.findClass(name);
		}
	}
}

