package com.ffaero.openrocketassembler.view

import java.awt.Component
import javax.swing.Spring
import javax.swing.SpringLayout

object SpringUtilities {
    fun minus(a: Spring, b: Spring) = Spring.sum(a, Spring.minus(b))

    fun width(layout: SpringLayout, comp: Component) = minus(layout.getConstraint(SpringLayout.EAST, comp), layout.getConstraint(SpringLayout.WEST, comp))

    fun split(layout: SpringLayout, width: Component, subtract: Component, margin: Int, portions: Float) = Spring.scale(minus(width(layout, width), Spring.sum(Spring.width(subtract), Spring.constant(margin))), 1.0f / portions)
}
