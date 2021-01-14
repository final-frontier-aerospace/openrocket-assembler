package com.ffaero.openrocketassembler.view

import com.ffaero.openrocketassembler.controller.SettingAdapter
import com.ffaero.openrocketassembler.controller.SettingController
import com.ffaero.openrocketassembler.model.TimeSpan
import org.slf4j.LoggerFactory
import java.awt.BorderLayout
import java.awt.Font
import java.awt.event.KeyEvent
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.io.Closeable
import java.io.File
import javax.swing.*
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener
import javax.swing.text.DefaultFormatter
import kotlin.math.abs

class SettingsWindow(private val view: ViewManager, private val settings: SettingController) : Closeable {
    companion object {
        private val log = LoggerFactory.getLogger(SettingsWindow::class.java)
        private const val BASELINE = SpringLayout.BASELINE
        private const val NORTH = SpringLayout.NORTH
        private const val EAST = SpringLayout.EAST
        private const val SOUTH = SpringLayout.SOUTH
        private const val WEST = SpringLayout.WEST
    }

    private var _modified = false
    private var modified: Boolean
            get() = _modified
            set(value) {
                _modified = value
                if (value) {
                    frame.title = "OpenRocket Assembler Settings *"
                } else {
                    frame.title = "OpenRocket Assembler Settings"
                }
            }

    private val historyBox: JSpinner
    private val initialHeightBox: JSpinner
    private val initialWidthBox: JSpinner
    private val openFromCWDBox: JCheckBox
    private val openrocketUpdateBox: JComboBox<TimeSpan>
    private val initialDirBox: JTextField
    private val cacheDirBox: JTextField
    private val tempDirBox: JTextField
    private val keepLogFilesBox: JCheckBox
    private val lookAndFeelBox: JComboBox<String>
    private val javaPathBox: JTextField
    private val enableUnsafeUIBox: JCheckBox
    private val warnDifferentVersionBox: JCheckBox
    private val warnFileExistsBox: JCheckBox
    private val warnTemplateOpenBox: JCheckBox
    private val warnConfigOpenBox: JCheckBox
    private val warnUnsavedChangesBox: JCheckBox
    private val warnInvalidReferenceBox: JCheckBox
    private val warnRelocateBox: JCheckBox
    private val warnEmptyDataBox: JCheckBox

    private val frame = JFrame().apply {
        defaultCloseOperation = JFrame.DO_NOTHING_ON_CLOSE
        preferredSize = settings.initialSize
        size = preferredSize
        contentPane.apply {
            layout = BorderLayout()
            add(JScrollPane(JPanel().apply {
                val layout = SpringLayout()
                setLayout(layout)

                val generalLabel = JLabel("General Settings")
                val catFont = Font(generalLabel.font.name, generalLabel.font.style or Font.BOLD, generalLabel.font.size + 2)
                generalLabel.font = catFont
                add(generalLabel)
                layout.putConstraint(NORTH, generalLabel, 10, NORTH, this)
                layout.putConstraint(EAST, generalLabel, -5, EAST, this)
                layout.putConstraint(WEST, generalLabel, 5, WEST, this)

                historyBox = JSpinner().apply {
                    toolTipText = "Number of actions to remember in history for undo/redo"
                }
                add(historyBox)
                layout.putConstraint(NORTH, historyBox, 5, SOUTH, generalLabel)
                layout.putConstraint(EAST, historyBox, -5, EAST, this)

                val historyLabel = JLabel("History Buffer Length:")
                add(historyLabel)
                layout.putConstraint(BASELINE, historyLabel, 0, BASELINE, historyBox)
                layout.putConstraint(WEST, historyLabel, 5, WEST, this)

                initialHeightBox = JSpinner().apply {
                    toolTipText = "Initial height of new windows in pixels"
                }
                add(initialHeightBox)
                layout.putConstraint(NORTH, initialHeightBox, 5, SOUTH, historyBox)
                layout.putConstraint(EAST, initialHeightBox, -5, EAST, this)

                val initialSizeX = JLabel("x")
                add(initialSizeX)
                layout.putConstraint(BASELINE, initialSizeX, 0, BASELINE, initialHeightBox)
                layout.putConstraint(EAST, initialSizeX, -5, WEST, initialHeightBox)

                initialWidthBox = JSpinner().apply {
                    toolTipText = "Initial width of new windows in pixels"
                }
                add(initialWidthBox)
                layout.putConstraint(BASELINE, initialWidthBox, 0, BASELINE, initialSizeX)
                layout.putConstraint(WEST, initialWidthBox, 0, WEST, historyBox)
                layout.putConstraint(EAST, initialWidthBox, SpringUtilities.split(layout, historyBox, initialSizeX, 10, 2f), WEST, initialWidthBox)
                layout.putConstraint(WEST, initialHeightBox, Spring.minus(SpringUtilities.split(layout, historyBox, initialSizeX, 10, 2f)), EAST, initialHeightBox)

                val initialSizeLabel = JLabel("Initial window size:")
                add(initialSizeLabel)
                layout.putConstraint(BASELINE, initialSizeLabel, 0, BASELINE, initialWidthBox)
                layout.putConstraint(EAST, initialSizeLabel, -5, WEST, initialWidthBox)
                layout.putConstraint(WEST, initialSizeLabel, 5, WEST, this)

                openFromCWDBox = JCheckBox("Open files from initial directory on startup").apply {
                    toolTipText = "Should OpenRocket Assembler open a single file out of the startup directory if only one file exists there?"
                }
                add(openFromCWDBox)
                layout.putConstraint(NORTH, openFromCWDBox, 5, SOUTH, initialHeightBox)
                layout.putConstraint(EAST, openFromCWDBox, -5, EAST, this)
                layout.putConstraint(WEST, openFromCWDBox, 5, WEST, this)

                openrocketUpdateBox = JComboBox(TimeSpan.standard).apply {
                    toolTipText = "Amount of time between automatic update checks"
                }
                add(openrocketUpdateBox)
                layout.putConstraint(NORTH, openrocketUpdateBox, 5, SOUTH, openFromCWDBox)
                layout.putConstraint(EAST, openrocketUpdateBox, -5, EAST, this)
                layout.putConstraint(WEST, historyBox, 0, WEST, openrocketUpdateBox)

                val openrocketUpdateLabel = JLabel("OpenRocket Update Period:")
                add(openrocketUpdateLabel)
                layout.putConstraint(BASELINE, openrocketUpdateLabel, 0, BASELINE, openrocketUpdateBox)
                layout.putConstraint(WEST, openrocketUpdateLabel, 5, WEST, this)
                layout.putConstraint(WEST, openrocketUpdateBox, 5, EAST, openrocketUpdateLabel)

                val storageLabel = JLabel("Storage Settings").apply {
                    font = catFont
                }
                add(storageLabel)
                layout.putConstraint(NORTH, storageLabel, 10, SOUTH, openrocketUpdateBox)
                layout.putConstraint(EAST, storageLabel, -5, EAST, this)
                layout.putConstraint(WEST, storageLabel, 5, WEST, this)

                val initialDirBrowse = JButton("Browse").apply {
                    toolTipText = "Browse for new initial directory"
                }
                add(initialDirBrowse)
                layout.putConstraint(NORTH, initialDirBrowse, 5, SOUTH, storageLabel)

                initialDirBox = JTextField().apply {
                    toolTipText = "Initial directory in which to start file chooser dialogs"
                }
                initialDirBrowse.addActionListener { browse(initialDirBox.text, true) { initialDirBox.text = it } }
                add(initialDirBox)
                layout.putConstraint(BASELINE, initialDirBox, 0, BASELINE, initialDirBrowse)
                layout.putConstraint(EAST, initialDirBox, -5, WEST, initialDirBrowse)
                layout.putConstraint(WEST, initialDirBox, 0, WEST, openrocketUpdateBox)

                val initialDirLabel = JLabel("Initial directory:")
                add(initialDirLabel)
                layout.putConstraint(BASELINE, initialDirLabel, 0, BASELINE, initialDirBox)
                layout.putConstraint(EAST, initialDirLabel, -5, WEST, initialDirBox)
                layout.putConstraint(WEST, initialDirLabel, 5, WEST, this)

                val cacheDirEmpty = JButton("Empty").apply {
                    toolTipText = "Delete all files in cache directory"
                }
                add(cacheDirEmpty)
                layout.putConstraint(NORTH, cacheDirEmpty, 5, SOUTH, initialDirBrowse)
                layout.putConstraint(EAST, cacheDirEmpty, -5, EAST, this)

                val cacheDirBrowse = JButton("Browse").apply {
                    toolTipText = "Browse for new cache directory"
                }
                add(cacheDirBrowse)
                layout.putConstraint(NORTH, cacheDirBrowse, 5, SOUTH, initialDirBrowse)
                layout.putConstraint(EAST, cacheDirBrowse, -5, WEST, cacheDirEmpty)
                layout.putConstraint(EAST, initialDirBrowse, 0, EAST, cacheDirBrowse)
                layout.putConstraint(WEST, initialDirBrowse, 0, WEST, cacheDirBrowse)

                cacheDirBox = JTextField().apply {
                    toolTipText = "Directory in which to store data used to speed up the program, but can be deleted"
                }
                cacheDirEmpty.addActionListener { empty(cacheDirBox.text) }
                cacheDirBrowse.addActionListener { browse(cacheDirBox.text, true) { cacheDirBox.text = it } }
                add(cacheDirBox)
                layout.putConstraint(BASELINE, cacheDirBox, 0, BASELINE, cacheDirBrowse)
                layout.putConstraint(EAST, cacheDirBox, -5, WEST, cacheDirBrowse)
                layout.putConstraint(WEST, cacheDirBox, 0, WEST, initialDirBox)

                val cacheDirLabel = JLabel("Cache directory:")
                add(cacheDirLabel)
                layout.putConstraint(BASELINE, cacheDirLabel, 0, BASELINE, cacheDirBox)
                layout.putConstraint(EAST, cacheDirLabel, 0, EAST, initialDirLabel)
                layout.putConstraint(WEST, cacheDirLabel, 0, WEST, initialDirLabel)

                val tempDirEmpty = JButton("Empty").apply {
                    toolTipText = "Delete all files in temporary directory"
                }
                add(tempDirEmpty)
                layout.putConstraint(NORTH, tempDirEmpty, 5, SOUTH, cacheDirEmpty)
                layout.putConstraint(EAST, tempDirEmpty, 0, EAST, cacheDirEmpty)
                layout.putConstraint(WEST, tempDirEmpty, 0, WEST, cacheDirEmpty)

                val tempDirBrowse = JButton("Browse").apply {
                    toolTipText = "Browse for new temporary directory"
                }
                add(tempDirBrowse)
                layout.putConstraint(BASELINE, tempDirBrowse, 0, BASELINE, tempDirEmpty)
                layout.putConstraint(EAST, tempDirBrowse, 0, EAST, cacheDirBrowse)
                layout.putConstraint(WEST, tempDirBrowse, 0, WEST, cacheDirBrowse)

                tempDirBox = JTextField().apply {
                    toolTipText = "Directory in which to store temporary files"
                }
                tempDirEmpty.addActionListener { empty(tempDirBox.text) }
                tempDirBrowse.addActionListener { browse(tempDirBox.text, true) { tempDirBox.text = it } }
                add(tempDirBox)
                layout.putConstraint(BASELINE, tempDirBox, 0, BASELINE, tempDirBrowse)
                layout.putConstraint(EAST, tempDirBox, 0, EAST, cacheDirBox)
                layout.putConstraint(WEST, tempDirBox, 0, WEST, cacheDirBox)

                val tempDirLabel = JLabel("Temporary directory:")
                add(tempDirLabel)
                layout.putConstraint(BASELINE, tempDirLabel, 0, BASELINE, tempDirBox)
                layout.putConstraint(EAST, tempDirLabel, 0, EAST, cacheDirLabel)
                layout.putConstraint(WEST, tempDirLabel, 0, WEST, cacheDirLabel)

                keepLogFilesBox = JCheckBox("Keep log files").apply {
                    toolTipText = "Store log files in temporary directory"
                }
                add(keepLogFilesBox)
                layout.putConstraint(NORTH, keepLogFilesBox, 5, SOUTH, tempDirEmpty)
                layout.putConstraint(EAST, keepLogFilesBox, -5, EAST, this)
                layout.putConstraint(WEST, keepLogFilesBox, 5, WEST, this)

                val javaLabel = JLabel("Java Settings").apply {
                    font = catFont
                }
                add(javaLabel)
                layout.putConstraint(NORTH, javaLabel, 10, SOUTH, keepLogFilesBox)
                layout.putConstraint(EAST, javaLabel, -5, EAST, this)
                layout.putConstraint(WEST, javaLabel, 5, WEST, this)

                lookAndFeelBox = JComboBox(UIManager.getInstalledLookAndFeels().map { it.className }.toTypedArray()).apply {
                    toolTipText = "Class name for UI look and feel"
                }
                add(lookAndFeelBox)
                layout.putConstraint(NORTH, lookAndFeelBox, 5, SOUTH, javaLabel)
                layout.putConstraint(EAST, lookAndFeelBox, -5, EAST, this)
                layout.putConstraint(WEST, lookAndFeelBox, 0, WEST, tempDirBox)

                val lookAndFeelLabel = JLabel("Look and Feel:")
                add(lookAndFeelLabel)
                layout.putConstraint(BASELINE, lookAndFeelLabel, 0, BASELINE, lookAndFeelBox)
                layout.putConstraint(EAST, lookAndFeelLabel, -5, WEST, lookAndFeelBox)
                layout.putConstraint(WEST, lookAndFeelLabel, 5, WEST, this)

                val javaPathBrowse = JButton("Browse").apply {
                    toolTipText = "Browse for Java executable"
                }
                add(javaPathBrowse)
                layout.putConstraint(NORTH, javaPathBrowse, 5, SOUTH, lookAndFeelBox)
                layout.putConstraint(EAST, javaPathBrowse, -5, EAST, this)

                javaPathBox = JTextField().apply {
                    toolTipText = "Path to the Java executable (javaw)"
                }
                javaPathBrowse.addActionListener { browse(javaPathBox.text, false) { javaPathBox.text = it } }
                add(javaPathBox)
                layout.putConstraint(BASELINE, javaPathBox, 0, BASELINE, javaPathBrowse)
                layout.putConstraint(EAST, javaPathBox, -5, WEST, javaPathBrowse)
                layout.putConstraint(WEST, javaPathBox, 0, WEST, lookAndFeelBox)

                val javaPathLabel = JLabel("Java executable:")
                add(javaPathLabel)
                layout.putConstraint(BASELINE, javaPathLabel, 0, BASELINE, javaPathBox)
                layout.putConstraint(EAST, javaPathLabel, -5, WEST, javaPathBox)
                layout.putConstraint(WEST, javaPathLabel, 5, WEST, this)

                enableUnsafeUIBox = JCheckBox("Enable unsafe UI code").apply {
                    toolTipText = "Enable UI code which may become unsafe in future versions of Java"
                }
                add(enableUnsafeUIBox)
                layout.putConstraint(NORTH, enableUnsafeUIBox, 5, SOUTH, javaPathBrowse)
                layout.putConstraint(EAST, enableUnsafeUIBox, -5, EAST, this)
                layout.putConstraint(WEST, enableUnsafeUIBox, 5, WEST, this)

                val disableWarnsLabel = JLabel("Disable Warnings").apply {
                    font = catFont
                }
                add(disableWarnsLabel)
                layout.putConstraint(NORTH, disableWarnsLabel, 10, SOUTH, enableUnsafeUIBox)
                layout.putConstraint(EAST, disableWarnsLabel, -5, EAST, this)
                layout.putConstraint(WEST, disableWarnsLabel, 5, WEST, this)

                warnDifferentVersionBox = JCheckBox("Warn about file saved in different version of OpenRocket Assembler")
                add(warnDifferentVersionBox)
                layout.putConstraint(NORTH, warnDifferentVersionBox, 5, SOUTH, disableWarnsLabel)
                layout.putConstraint(EAST, warnDifferentVersionBox, -5, EAST, this)
                layout.putConstraint(WEST, warnDifferentVersionBox, 5, WEST, this)

                warnFileExistsBox = JCheckBox("Warn about file saving over existent file")
                add(warnFileExistsBox)
                layout.putConstraint(NORTH, warnFileExistsBox, 5, SOUTH, warnDifferentVersionBox)
                layout.putConstraint(EAST, warnFileExistsBox, -5, EAST, this)
                layout.putConstraint(WEST, warnFileExistsBox, 5, WEST, this)

                warnTemplateOpenBox = JCheckBox("Warn about exiting while template editor is still open")
                add(warnTemplateOpenBox)
                layout.putConstraint(NORTH, warnTemplateOpenBox, 5, SOUTH, warnFileExistsBox)
                layout.putConstraint(EAST, warnTemplateOpenBox, -5, EAST, this)
                layout.putConstraint(WEST, warnTemplateOpenBox, 5, WEST, this)

                warnConfigOpenBox = JCheckBox("Warn about exiting while configuration editor is still open")
                add(warnConfigOpenBox)
                layout.putConstraint(NORTH, warnConfigOpenBox, 5, SOUTH, warnTemplateOpenBox)
                layout.putConstraint(EAST, warnConfigOpenBox, -5, EAST, this)
                layout.putConstraint(WEST, warnConfigOpenBox, 5, WEST, this)

                warnUnsavedChangesBox = JCheckBox("Warn about exiting with unsaved project changes")
                add(warnUnsavedChangesBox)
                layout.putConstraint(NORTH, warnUnsavedChangesBox, 5, SOUTH, warnConfigOpenBox)
                layout.putConstraint(EAST, warnUnsavedChangesBox, -5, EAST, this)
                layout.putConstraint(WEST, warnUnsavedChangesBox, 5, WEST, this)

                warnInvalidReferenceBox = JCheckBox("Warn about opening project with invalid component references")
                add(warnInvalidReferenceBox)
                layout.putConstraint(NORTH, warnInvalidReferenceBox, 5, SOUTH, warnUnsavedChangesBox)
                layout.putConstraint(EAST, warnInvalidReferenceBox, -5, EAST, this)
                layout.putConstraint(WEST, warnInvalidReferenceBox, 5, WEST, this)

                warnRelocateBox = JCheckBox("Warn before relocating component file reference")
                add(warnRelocateBox)
                layout.putConstraint(NORTH, warnRelocateBox, 5, SOUTH, warnInvalidReferenceBox)
                layout.putConstraint(EAST, warnRelocateBox, -5, EAST, this)
                layout.putConstraint(WEST, warnRelocateBox, 5, WEST, this)

                warnEmptyDataBox = JCheckBox("Warn before deleting data files")
                add(warnEmptyDataBox)
                layout.putConstraint(NORTH, warnEmptyDataBox, 5, SOUTH, warnRelocateBox)
                layout.putConstraint(EAST, warnEmptyDataBox, -5, EAST, this)
                layout.putConstraint(WEST, warnEmptyDataBox, 5, WEST, this)
            }, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER), BorderLayout.CENTER)
        }
        isVisible = true
        toFront()
        requestFocus()
        state = JFrame.NORMAL
        addWindowListener(object : WindowAdapter() {
            override fun windowClosing(e: WindowEvent?) = close()
        })
        jMenuBar = JMenuBar().apply {
            add(JMenu("File").apply {
                add(JMenuItem("Save").apply {
                    accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK)
                    addActionListener { save() }
                })

                add(JMenuItem("Exit").apply {
                    addActionListener { view.exit() }
                })
            })
            add(JMenu("Window").apply {
                add(JMenuItem("Close").apply {
                    accelerator = KeyStroke.getKeyStroke(KeyEvent.VK_F4, KeyEvent.ALT_DOWN_MASK)
                    addActionListener { close() }
                })
            })
        }
    }

    private fun browse(initial: String, dir: Boolean, callback: Function1<String, Unit>) {
        JFileChooser().apply {
            if (dir) {
                fileSelectionMode = JFileChooser.DIRECTORIES_ONLY
            }
            currentDirectory = File(initial)
            if (showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
                callback(selectedFile.absolutePath)
            }
        }
    }

    private fun delete(root: File, ignore: HashSet<String>): Boolean {
        log.info("Deleting folder {}", root.absolutePath)
        var empty = true
        root.listFiles()?.forEach {
            if (!ignore.contains(it.absolutePath)) {
                if (it.isDirectory) {
                    if (!delete(it, ignore)) {
                        return@forEach
                    }
                }
                log.info("Deleting {}", it.absolutePath)
                it.delete()
            } else {
                log.info("Ignoring in-use file {}", it.absolutePath)
                empty = false
            }
        }
        return empty
    }

    private fun empty(dir: String) {
        if (settings.warnEmptyData && JOptionPane.showConfirmDialog(frame, "Are you sure you want to delete all files in the following directory?\n$dir", "Empty", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.YES_OPTION) {
            return
        }
        val ignore = HashSet<String>()
        ignore.addAll(settings.app.cacheInUse.map { it.absolutePath })
        ignore.addAll(settings.app.tempInUse.map { it.absolutePath })
        delete(File(dir), ignore)
    }

    private val settingListener = object : SettingAdapter() {
        override fun onSettingsUpdated(sender: SettingController) {
            historyBox.value = sender.historyLength
            initialHeightBox.value = sender.initialHeight
            initialWidthBox.value = sender.initialWidth
            openFromCWDBox.isSelected = sender.openFromCWD
            openrocketUpdateBox.selectedItem = TimeSpan.standard.minByOrNull { abs(it.millis - sender.openrocketUpdatePeriod) }!!
            initialDirBox.text = sender.initialDir.absolutePath
            cacheDirBox.text = sender.cacheDir.absolutePath
            tempDirBox.text = sender.tempDir.absolutePath
            keepLogFilesBox.isSelected = sender.keepLogFiles
            lookAndFeelBox.selectedItem = sender.lookAndFeel
            javaPathBox.text = sender.javaPath
            enableUnsafeUIBox.isSelected = sender.enableUnsafeUI
            warnDifferentVersionBox.isSelected = sender.warnDifferentVersion
            warnFileExistsBox.isSelected = sender.warnFileExists
            warnTemplateOpenBox.isSelected = sender.warnTemplateOpen
            warnConfigOpenBox.isSelected = sender.warnConfigOpen
            warnUnsavedChangesBox.isSelected = sender.warnUnsavedChanges
            warnInvalidReferenceBox.isSelected = sender.warnInvalidReference
            warnRelocateBox.isSelected = sender.warnRelocate
            warnEmptyDataBox.isSelected = sender.warnEmptyData
            frame.preferredSize = sender.initialSize
            modified = false
        }
    }.apply {
        onSettingsUpdated(settings)
        settings.addListener(this)
    }

    private fun save() {
        settings.historyLength = historyBox.value as Int
        settings.initialHeight = initialHeightBox.value as Int
        settings.initialWidth = initialWidthBox.value as Int
        settings.openFromCWD = openFromCWDBox.isSelected
        settings.openrocketUpdatePeriod = (openrocketUpdateBox.selectedItem as TimeSpan).millis
        settings.initialDir = File(initialDirBox.text)
        settings.cacheDir = File(cacheDirBox.text)
        settings.tempDir = File(tempDirBox.text)
        settings.keepLogFiles = keepLogFilesBox.isSelected
        settings.lookAndFeel = lookAndFeelBox.selectedItem as String
        settings.javaPath = javaPathBox.text
        settings.enableUnsafeUI = enableUnsafeUIBox.isSelected
        settings.warnDifferentVersion = warnDifferentVersionBox.isSelected
        settings.warnFileExists = warnFileExistsBox.isSelected
        settings.warnTemplateOpen = warnTemplateOpenBox.isSelected
        settings.warnConfigOpen = warnConfigOpenBox.isSelected
        settings.warnUnsavedChanges = warnUnsavedChangesBox.isSelected
        settings.warnInvalidReference = warnInvalidReferenceBox.isSelected
        settings.warnRelocate = warnRelocateBox.isSelected
        settings.warnEmptyData = warnEmptyDataBox.isSelected
        settings.save()
        modified = false
    }

    init {
        view.addDialog(this)
        arrayOf(historyBox, initialHeightBox, initialWidthBox).forEach {
            it.model = SpinnerNumberModel(it.value as Int, 1, Int.MAX_VALUE, 1)
            val editor = JSpinner.NumberEditor(it)
            it.editor = editor
            (editor.textField.formatter as? DefaultFormatter)?.commitsOnValidEdit = true
            it.addChangeListener { modified = true }
        }
        arrayOf(openFromCWDBox, keepLogFilesBox, enableUnsafeUIBox, warnDifferentVersionBox,
                warnFileExistsBox, warnTemplateOpenBox, warnConfigOpenBox, warnUnsavedChangesBox,
                warnInvalidReferenceBox, warnRelocateBox).forEach {
            it.addItemListener { modified = true }
        }
        arrayOf(openrocketUpdateBox, lookAndFeelBox).forEach {
            it.addItemListener { modified = true }
        }
        arrayOf(initialDirBox, cacheDirBox, tempDirBox, javaPathBox).forEach {
            it.document.addDocumentListener(object : DocumentListener {
                override fun insertUpdate(e: DocumentEvent?) {
                    modified = true
                }

                override fun removeUpdate(e: DocumentEvent?) {
                    modified = true
                }

                override fun changedUpdate(e: DocumentEvent?) {
                    modified = true
                }
            })
        }
    }

    override fun close() {
        if (modified && settings.warnUnsavedChanges) {
            when (JOptionPane.showConfirmDialog(frame, "Settings have unsaved changes.\nSave before closing window?", "Close", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE)) {
                JOptionPane.YES_OPTION -> {
                    save()
                }
                JOptionPane.NO_OPTION -> {
                }
                else -> {
                    return
                }
            }
        }
        frame.dispose()
        view.removeDialog(this)
        settings.removeListener(settingListener)
    }
}
