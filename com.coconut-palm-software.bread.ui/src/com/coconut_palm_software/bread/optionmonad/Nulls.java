package com.coconut_palm_software.bread.optionmonad;

public class Nulls {
	public static <T> Option<T> option(T object) { 
		return object == null ? new None<T>() : new Some<T>(object); 
	}
	
    public static <T, E extends Throwable> T getOrThrow(T object, E ex) throws E {
    	if (object != null) {
    		return object;
    	}
    	throw ex;
    }
    
    public static <T> T getOrReturn(T object, T defaultValue) {
    	if (object != null) {
    		return object;
    	}
    	return defaultValue;
    }

}
