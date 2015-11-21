package com.coconut_palm_software.bread.optionmonad;

public final class None<T> implements Option<T> {
 
    public None() {}
 
    public T get() {
        throw new UnsupportedOperationException("Cannot resolve value on None");
    }

    public <E extends Throwable> T getOrThrow(E ex) throws E {
    	throw ex;
    };
    
    public T getOrReturn(T defaultValue) {
    	return defaultValue;
    };
    
    /**
     * Constructs an optional value that has no value.
     *
     * @return An optional value that has no value.
     */
    public static <T> Option<T> none() {
      return new None<T>();
    }

	public boolean hasValue() {
		return false;
	}

}
