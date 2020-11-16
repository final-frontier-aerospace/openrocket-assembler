package com.ffaero.openrocketassembler.util.lambda;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Consumer;

public class WindowClosingAdapter implements WindowListener {
	private final Consumer<WindowEvent> func;
	
	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosed(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
		func.accept(e);
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}

	public WindowClosingAdapter(Consumer<WindowEvent> func) {
		this.func = func;
	}
}
