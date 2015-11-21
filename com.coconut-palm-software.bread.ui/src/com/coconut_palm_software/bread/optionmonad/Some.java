package com.coconut_palm_software.bread.optionmonad;
public final class Some<T> implements Option<T> {
    private final T value;
 
    public Some(T value) {
        this.value = value;
    }
 
    public T get() {
        return value;
    }
    
    public <E extends Throwable> T getOrThrow(E ex) throws E {
    	return value;
    };
    
    public T getOrReturn(T defaultValue) {
    	return value;
    };
    
    /**
     * Constructs an optional value that has a value of the given argument.
     *
     * @param t The value for the returned optional value.
     * @return An optional value that has a value of the given argument.
     */
    public static <T> Option<T> some(final T t) {
      return new Some<T>(t);
    }

	public boolean hasValue() {
		return true;
	}

}
 