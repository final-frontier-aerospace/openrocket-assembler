package com.ffaero.openrocketassembler.controller

import javax.xml.transform.TransformerFactory
import java.io.InputStream
import javax.xml.parsers.DocumentBuilderFactory
import org.w3c.dom.Element
import java.io.OutputStream
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class OpenRocketXML(stream: InputStream) {
	companion object {
		private val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		private val transformer = TransformerFactory.newInstance().newTransformer()
	}
	
	private val doc = docBuilder.parse(stream)
	private val subcomponents = makePath("rocket", "subcomponents", "stage", "subcomponents")
	
	private fun makePath(vararg path: String): Element {
		var el = doc.getDocumentElement()
		path.forEach {
			val list = el.getChildNodes()
			var i = 0
			var found = false
			while (i < list.getLength()) {
				val node = list.item(i)
				if (node is Element && node.getTagName().equals(it)) {
					el = node
					found = true
					break
				}
				i++
			}
			if (!found) {
				val child = doc.createElement(it)
				el.appendChild(child)
				el = child
			}
		}
		return el
	}
	
	public fun getSubcomponents() = sequence {
		val list = subcomponents.getChildNodes()
		var i = 0
		while (i < list.getLength()) {
			val node = list.item(i)
			if (node is Element) {
				yield(node)
			}
			i++
		}
	}
	
	public fun addSubcomponents(comp: Sequence<Element>) = comp.forEach {
		val clone = it.cloneNode(true)
		doc.adoptNode(clone)
		subcomponents.appendChild(clone)
	}
	
	public fun clearSubcomponents() {
		while (subcomponents.hasChildNodes()) {
			subcomponents.removeChild(subcomponents.getFirstChild())
		}
	}
	
	public fun write(stream: OutputStream) = transformer.transform(DOMSource(doc), StreamResult(stream))
}
