package com.ffaero.openrocketassembler.util.lambda;

import java.util.function.Consumer;

public class Consumer1<T1> implements Consumer<T1> {
	private final Consumer<T1> func;
	
	@Override
	public void accept(T1 arg) {
		func.accept(arg);
	}
	
	public Consumer1(Consumer<T1> func) {
		this.func = func;
	}
	
	public Consumer1(Runnable func) {
		this.func = x -> func.run();
	}
}
