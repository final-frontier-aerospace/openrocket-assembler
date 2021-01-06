package com.ffaero.openrocketassembler.view

import javax.swing.JPanel
import com.ffaero.openrocketassembler.controller.ProjectController
import javax.swing.JScrollPane

class RocketList(private val proj: ProjectController) : JScrollPane(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER) {
}
