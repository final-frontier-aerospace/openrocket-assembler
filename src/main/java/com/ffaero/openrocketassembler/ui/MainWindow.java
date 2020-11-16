package com.ffaero.openrocketassembler.ui;

import java.awt.Dimension;

import javax.swing.JFrame;
import javax.swing.UIManager;

import com.ffaero.openrocketassembler.util.lambda.Consumer1;
import com.ffaero.openrocketassembler.util.lambda.WindowClosingAdapter;

public class MainWindow {
	private final JFrame frame;
	private final MenuBar menu;

	public MainWindow() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		frame = new JFrame();
		menu = new MenuBar(frame);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setPreferredSize(new Dimension(1024, 768));
		frame.setSize(frame.getPreferredSize());
		frame.setTitle("OpenRocket Assembler");
		frame.addWindowListener(new WindowClosingAdapter(new Consumer1<>(menu.getController()::exit)));
		frame.setVisible(true);
	}
}
