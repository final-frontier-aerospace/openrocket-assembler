package com.ffaero.openrocketassembler.ui;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;

import com.ffaero.openrocketassembler.util.lambda.Consumer1;

public class MenuBar {
	private final MenuController controller;
	
	public MenuController getController() {
		return controller;
	}
	
	public MenuBar(JFrame frame) {
		controller = new MenuController();
		JMenuBar menuBar = new JMenuBar();
		frame.setJMenuBar(menuBar);
		JMenu fileMenu = new JMenu("File");
		menuBar.add(fileMenu);
		JMenu fileNewMenu = new JMenu("New");
		fileMenu.add(fileNewMenu);
		JMenuItem fileNewProjectMenu = new JMenuItem("Project");
		fileNewMenu.add(fileNewProjectMenu);
		fileNewProjectMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		fileNewProjectMenu.addActionListener(new Consumer1<>(controller::newProject)::accept);
		JMenuItem fileNewComponentMenu = new JMenuItem("Component");
		fileNewMenu.add(fileNewComponentMenu);
		fileNewComponentMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		fileNewComponentMenu.addActionListener(new Consumer1<>(controller::newComponent)::accept);
		JMenuItem fileNewConfigurationMenu = new JMenuItem("Configuration");
		fileNewMenu.add(fileNewConfigurationMenu);
		fileNewConfigurationMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		fileNewConfigurationMenu.addActionListener(new Consumer1<>(controller::newConfiguration)::accept);
		JMenuItem fileOpenMenu = new JMenuItem("Open Project");
		fileMenu.add(fileOpenMenu);
		fileOpenMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK));
		fileOpenMenu.addActionListener(new Consumer1<>(controller::openProject)::accept);
		JMenuItem fileSaveMenu = new JMenuItem("Save Project");
		fileMenu.add(fileSaveMenu);
		fileSaveMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_DOWN_MASK));
		fileSaveMenu.addActionListener(new Consumer1<>(controller::saveProject)::accept);
		JMenuItem fileCloseMenu = new JMenuItem("Close Project");
		fileMenu.add(fileCloseMenu);
		fileCloseMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		fileCloseMenu.addActionListener(new Consumer1<>(controller::closeProject)::accept);
		JMenuItem fileExitMenu = new JMenuItem("Exit");
		fileMenu.add(fileExitMenu);
		fileExitMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_DOWN_MASK));
		fileExitMenu.addActionListener(new Consumer1<>(controller::exit)::accept);
		JMenu editMenu = new JMenu("Edit");
		menuBar.add(editMenu);
		JMenuItem editUndoMenu = new JMenuItem("Undo");
		editMenu.add(editUndoMenu);
		editUndoMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_DOWN_MASK));
		editUndoMenu.addActionListener(new Consumer1<>(controller::undo)::accept);
		JMenuItem editRedoMenu = new JMenuItem("Redo");
		editMenu.add(editRedoMenu);
		editRedoMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_DOWN_MASK));
		editRedoMenu.addActionListener(new Consumer1<>(controller::redo)::accept);
		JMenu componentMenu = new JMenu("Component");
		menuBar.add(componentMenu);
		JMenuItem componentNewMenu = new JMenuItem("New");
		componentMenu.add(componentNewMenu);
		componentNewMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		componentNewMenu.addActionListener(new Consumer1<>(controller::newComponent)::accept);
		JMenuItem componentOpenMenu = new JMenuItem("Open");
		componentMenu.add(componentOpenMenu);
		componentOpenMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		componentOpenMenu.addActionListener(new Consumer1<>(controller::openComponent)::accept);
		JMenuItem componentDeleteMenu = new JMenuItem("Delete");
		componentMenu.add(componentDeleteMenu);
		componentDeleteMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK));
		componentDeleteMenu.addActionListener(new Consumer1<>(controller::deleteComponent)::accept);
		componentMenu.addSeparator();
		JMenuItem componentTemplateMenu = new JMenuItem("(Template)");
		componentMenu.add(componentTemplateMenu);
		componentTemplateMenu.addActionListener(new Consumer1<>(controller::selectTemplateComponent)::accept);
		JMenu configurationMenu = new JMenu("Configuration");
		menuBar.add(configurationMenu);
		JMenuItem configurationNewMenu = new JMenuItem("New");
		configurationMenu.add(configurationNewMenu);
		configurationNewMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		configurationNewMenu.addActionListener(new Consumer1<>(controller::newConfiguration)::accept);
		JMenuItem configurationOpenMenu = new JMenuItem("Open");
		configurationMenu.add(configurationOpenMenu);
		configurationOpenMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		configurationOpenMenu.addActionListener(new Consumer1<>(controller::openConfiguration)::accept);
		JMenuItem configurationDeleteMenu = new JMenuItem("Delete");
		configurationMenu.add(configurationDeleteMenu);
		configurationDeleteMenu.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_DOWN_MASK | InputEvent.ALT_DOWN_MASK | InputEvent.SHIFT_DOWN_MASK));
		configurationDeleteMenu.addActionListener(new Consumer1<>(controller::deleteConfiguration)::accept);
		configurationMenu.addSeparator();
		JMenu openrocketMenu = new JMenu("OpenRocket");
		menuBar.add(openrocketMenu);
		JMenuItem openrocketUpdateMenu = new JMenuItem("Check for Updates");
		openrocketMenu.add(openrocketUpdateMenu);
		openrocketUpdateMenu.addActionListener(new Consumer1<>(controller::checkForOpenRocketUpdates)::accept);
		openrocketMenu.addSeparator();
		JMenuItem openrocketOtherVersionMenu = new JMenuItem("Other version...");
		openrocketMenu.add(openrocketOtherVersionMenu);
		openrocketOtherVersionMenu.addActionListener(new Consumer1<>(controller::selectOtherOpenRocketVersion)::accept);
	}
}
