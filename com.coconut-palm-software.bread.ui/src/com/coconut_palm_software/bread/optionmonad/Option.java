package com.coconut_palm_software.bread.optionmonad;
	
public interface Option<T> {
    public T get();
    public <E extends Throwable> T getOrThrow(E ex) throws E;
    public T getOrReturn(T defaultValue);
    public boolean hasValue();
}