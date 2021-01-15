package com.ffaero.openrocketassembler.controller

import org.w3c.dom.Element
import java.io.InputStream
import java.io.OutputStream
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult

class OpenRocketXML(stream: InputStream) {
	companion object {
		private val docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder()
		private val transformer = TransformerFactory.newInstance().newTransformer()
	}
	
	private val doc = docBuilder.parse(stream)
	private val subcomponents = makePath(doc.documentElement, "rocket", "subcomponents", "stage", "subcomponents")
	private val simulations = makePath(doc.documentElement, "simulations")
	
	private fun makePath(root: Element, vararg path: String): Element {
		var el = root
		path.forEach {
			val list = el.childNodes
			var i = 0
			var found = false
			while (i < list.length) {
				val node = list.item(i)
				if (node is Element && node.tagName == it) {
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
	
	fun getSubcomponents() = sequence {
		val list = subcomponents.childNodes
		var i = 0
		while (i < list.length) {
			val node = list.item(i)
			if (node is Element) {
				yield(node)
			}
			i++
		}
	}

	fun getSimulations() = sequence {
		val list = simulations.childNodes
		var i = 0
		while (i < list.length) {
			val node = list.item(i)
			if (node is Element) {
				val name = makePath(node, "name").textContent.trim()
				val id = makePath(node, "conditions", "configid").textContent.trim()
				yield(Pair(name, id))
			}
			i++
		}
	}

	fun addSubcomponents(comp: Sequence<Element>, sims: Sequence<Pair<String, String>>) = comp.forEach {
		val clone = it.cloneNode(true)
		doc.adoptNode(clone)
		subcomponents.appendChild(clone)
		val stack = LinkedList<Element>()
		stack.push(clone as Element)
		while (stack.isNotEmpty()) {
			val el = stack.pop()
			if (el.tagName == "motor" && el.hasAttribute("configid")) {
				val id = el.getAttribute("configid")
				val name = sims.firstOrNull { s -> s.second == id }?.first
				if (name != null) {
					val realId = getSimulations().firstOrNull { s -> s.first == name }?.second
					if (realId != null) {
						el.setAttribute("configid", realId)
					}
				}
			} else {
				val list = el.childNodes
				var i = 0
				while (i < list.length) {
					val node = list.item(i)
					if (node is Element) {
						stack.push(node)
					}
					i++
				}
			}
		}
	}
	
	fun clearSubcomponents() {
		while (subcomponents.hasChildNodes()) {
			subcomponents.removeChild(subcomponents.firstChild)
		}
	}
	
	fun write(stream: OutputStream) = transformer.transform(DOMSource(doc), StreamResult(stream))
}
