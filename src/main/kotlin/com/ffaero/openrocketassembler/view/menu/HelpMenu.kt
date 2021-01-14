package com.ffaero.openrocketassembler.view.menu

import javax.swing.JMenu
import javax.swing.JMenuItem

class HelpMenu : JMenu("Help") {
    init {
        JMenuItem("Take Crash Report").apply {
            addActionListener {
                throw RuntimeException("Generating fake crash report")
            }
            this@HelpMenu.add(this)
        }
    }
}