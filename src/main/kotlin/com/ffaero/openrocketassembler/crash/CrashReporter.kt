package com.ffaero.openrocketassembler.crash

import org.slf4j.LoggerFactory
import java.awt.*
import java.awt.datatransfer.StringSelection
import java.awt.event.WindowEvent
import java.io.*
import java.time.LocalDateTime
import javax.swing.*

class CrashReporter(private val file: File) {
    companion object {
        private val log = LoggerFactory.getLogger(CrashReporter::class.java)
    }

    private val textArea = JTextArea().apply {
        isEditable = false
    }

    private fun loadFile() {
        try {
            BufferedReader(InputStreamReader(FileInputStream(file))).use {
                textArea.text = it.readText().trim()
            }
        } catch (ex: IOException) {
            log.info("Unable to open log file", ex)
            try {
                val out = ByteArrayOutputStream()
                ex.printStackTrace(PrintStream(out))
                textArea.text = out.toString()
            } catch (ex: IOException) {
                log.info("Unable to format exception", ex)
            }
        }
    }

    init {
        JFrame().apply {
            defaultCloseOperation = JFrame.DISPOSE_ON_CLOSE
            preferredSize = Dimension(1024, 768)
            size = preferredSize
            contentPane.apply {
                layout = BorderLayout()
                add(JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS), BorderLayout.CENTER)
            }
            title = "OpenRocket Assembler Crash Report: '" + file.name + "'"
            jMenuBar = JMenuBar().apply {
                add(JMenu("File").apply {
                    if (Desktop.isDesktopSupported()) {
                        val desktop = Desktop.getDesktop()
                        add(JMenuItem("Open Externally").apply {
                            addActionListener { desktop.open(file) }
                        })
                    }
                    add(JMenuItem("Reload").apply {
                        addActionListener { loadFile() }
                    })
                    addSeparator()
                    add(JMenuItem("Exit").apply {
                        addActionListener {
                            Window.getWindows().forEach { it.dispatchEvent(WindowEvent(it, WindowEvent.WINDOW_CLOSING)) }
                        }
                    })
                })
                add(JMenu("Edit").apply {
                    add(JMenuItem("Copy All").apply {
                        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
                        addActionListener {
                            val sel = StringSelection(textArea.text)
                            clipboard.setContents(sel, sel)
                        }
                    })
                })
                add(JMenu("Window").apply {
                    add(JMenuItem("Close").apply {
                        addActionListener { dispose() }
                    })
                })
            }
            isVisible = true
            toFront()
            requestFocus()
            state = JFrame.NORMAL
            log.info("Crash reporter opened.")
        }
        log.info("Crash reported at {}", LocalDateTime.now())
        System.getProperties().mapKeys { it.key.toString() }.toSortedMap().forEach { (k, v) ->
            log.info("System.properties[\"{}\"] = \"{}\"", k, v)
        }
        System.getenv().toSortedMap().forEach { (k, v) ->
            log.info("System.env[\"{}\"] = \"{}\"", k, v)
        }
        val rt = Runtime.getRuntime()
        log.info("Runtime.freeMemory = {}", rt.freeMemory())
        log.info("Runtime.maxMemory = {}", rt.maxMemory())
        log.info("Runtime.totalMemory = {}", rt.totalMemory())
        Thread.getAllStackTraces().toSortedMap { a, b ->
            a.id.compareTo(b.id)
        }.forEach { (t, s) ->
            log.info("Thread \"{}\" ({}):", t.name, t.id)
            s.forEach {
                log.info("  {}", it)
            }
        }
        log.info("End of crash report.")
        loadFile()
    }
}
