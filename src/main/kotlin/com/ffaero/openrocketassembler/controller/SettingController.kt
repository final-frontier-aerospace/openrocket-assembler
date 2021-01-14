package com.ffaero.openrocketassembler.controller

import com.ffaero.openrocketassembler.model.proto.SettingsOuterClass.Settings
import net.harawata.appdirs.AppDirsFactory
import java.awt.Dimension
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import javax.swing.UIManager

class SettingController(val app: ApplicationController) : DispatcherBase<SettingListener, SettingListenerList>(SettingListenerList()) {
    companion object {
        private const val publisher = "ffaero"
        private const val application = "openrocketassembler"
        private const val version = "1.0"
        private val appDirs = AppDirsFactory.getInstance()
        private val defCacheDir = File(appDirs.getUserCacheDir(application, version, publisher)).apply { mkdirs() }
        private val defTempDir = File(File(appDirs.getUserDataDir(application, version, publisher)), "Temp").apply { mkdirs() }
        private val defJavaPath: String
        private val settingsFile = File(appDirs.getUserDataDir(application, version, publisher), "settings.bin").apply { parentFile.mkdirs() }

        init {
            val bin = File(File(System.getProperty("java.home")), "bin")
            var exe = File(bin, "javaw")
            if (!exe.exists()) {
                exe = File(bin, "javaw.exe")
            }
            defJavaPath = if (exe.exists()) {
                exe.absolutePath
            } else {
                "javaw"
            }
        }

        private val desc = Settings.getDescriptor()
        // General Settings
        private val historyLengthDesc = desc.findFieldByNumber(Settings.HISTORYLENGTH_FIELD_NUMBER)
        private val initialWidthDesc = desc.findFieldByNumber(Settings.INITIALWIDTH_FIELD_NUMBER)
        private val initialHeightDesc = desc.findFieldByNumber(Settings.INITIALHEIGHT_FIELD_NUMBER)
        //openFromCWDDesc
        private val openrocketUpdatePeriodDesc = desc.findFieldByNumber(Settings.OPENROCKETUPDATEPERIOD_FIELD_NUMBER)
        // Storage Settings
        private val initialDirDesc = desc.findFieldByNumber(Settings.INITIALDIR_FIELD_NUMBER)
        private val cacheDirDesc = desc.findFieldByNumber(Settings.CACHEDIR_FIELD_NUMBER)
        private val tempDirDesc = desc.findFieldByNumber(Settings.TEMPDIR_FIELD_NUMBER)
        // Java Settings
        private val lookAndFeelDesc = desc.findFieldByNumber(Settings.LOOKANDFEEL_FIELD_NUMBER)
        private val javaPathDesc = desc.findFieldByNumber(Settings.JAVAPATH_FIELD_NUMBER)
        //enableUnsafeUIDesc
        // Disable Warnings
        //warnDifferentVersionDesc
        //warnFileExistsDesc
        //warnTemplateOpenDesc
        //warnConfigOpenDesc
        //warnUnsavedChangesDesc
        //warnInvalidReferenceDesc
        //warnRelocateDesc
        //warnEmptyData
    }

    private val model = Settings.newBuilder()

    fun load() {
        try {
            FileInputStream(settingsFile).use {
                model.clear()
                model.mergeFrom(it)
            }
        } catch (ex: IOException) {
        }
    }

    fun save() {
        try {
            FileOutputStream(settingsFile).use {
                model.build().writeTo(it)
            }
        } catch (ex: IOException) {
        }
        listener.onSettingsUpdated(this)
    }

    // General Settings
    var historyLength: Int
            get() = if (model.hasField(historyLengthDesc)) {
                model.historyLength
            } else {
                Int.MAX_VALUE
            }
            set(value) {
                if (historyLength != value) {
                    model.historyLength = value
                }
            }
    var initialWidth: Int
            get() = if (model.hasField(initialWidthDesc)) {
                model.initialWidth
            } else {
                1024
            }
            set(value) {
                if (initialWidth != value) {
                    model.initialWidth = value
                }
            }
    var initialHeight: Int
            get() = if (model.hasField(initialHeightDesc)) {
                model.initialHeight
            } else {
                768
            }
            set(value) {
                if (initialHeight != value) {
                    model.initialHeight = value
                }
            }
    var initialSize: Dimension
            get() = Dimension(initialWidth, initialHeight)
            set(value) {
                initialWidth = value.width
                initialHeight = value.height
            }
    var openFromCWD: Boolean
            get() = !model.openFromCWD
            set(value) {
                if (openFromCWD != value) {
                    model.openFromCWD = !value
                }
            }
    var openrocketUpdatePeriod: Long
            get() = if (model.hasField(openrocketUpdatePeriodDesc)) {
                model.openrocketUpdatePeriod
            } else {
                1000 * 60 * 60 * 24 * 7
            }
            set(value) {
                if (openrocketUpdatePeriod != value) {
                    model.openrocketUpdatePeriod = value
                }
            }
    // Storage Settings
    var initialDir: File
            get() = if (model.hasField(initialDirDesc)) {
                File(model.initialDir)
            } else {
                File(".")
            }
            set(value) {
                if (initialDir.absolutePath != value.absolutePath) {
                    model.initialDir = value.absolutePath
                }
            }
    var cacheDir: File
            get() = if (model.hasField(cacheDirDesc)) {
                File(model.cacheDir)
            } else {
                defCacheDir
            }
            set(value) {
                if (cacheDir.absolutePath != value.absolutePath) {
                    model.cacheDir = value.absolutePath
                }
            }
    var tempDir: File
            get() = if (model.hasField(tempDirDesc)) {
                File(model.tempDir)
            } else {
                defTempDir
            }
            set(value) {
                if (tempDir.absolutePath != value.absolutePath) {
                    model.tempDir = value.absolutePath
                }
            }
    // Java Settings
    var lookAndFeel: String
            get() = if (model.hasField(lookAndFeelDesc)) {
                model.lookAndFeel
            } else {
                UIManager.getSystemLookAndFeelClassName()
            }
            set(value) {
                if (lookAndFeel != value) {
                    model.lookAndFeel = value
                }
            }
    var javaPath: String
            get() = if (model.hasField(javaPathDesc)) {
                model.javaPath
            } else {
                defJavaPath
            }
            set(value) {
                if (javaPath != value) {
                    model.javaPath = value
                }
            }
    var enableUnsafeUI: Boolean
            get() = !model.enableUnsafeUI
            set(value) {
                if (enableUnsafeUI != value) {
                    model.enableUnsafeUI = !value
                }
            }
    // Disable Warnings
    var warnDifferentVersion: Boolean
            get() = !model.warnDifferentVersion
            set(value) {
                if (warnDifferentVersion != value) {
                    model.warnDifferentVersion = !value
                }
            }
    var warnFileExists: Boolean
            get() = !model.warnFileExists
            set(value) {
                if (warnFileExists != value) {
                    model.warnFileExists = !value
                }
            }
    var warnTemplateOpen: Boolean
            get() = !model.warnTemplateOpen
            set(value) {
                if (warnTemplateOpen != value) {
                    model.warnTemplateOpen = !value
                }
            }
    var warnConfigOpen: Boolean
            get() = !model.warnConfigOpen
            set(value) {
                if (warnConfigOpen != value) {
                    model.warnConfigOpen = !value
                }
            }
    var warnUnsavedChanges: Boolean
            get() = !model.warnUnsavedChanges
            set(value) {
                if (warnUnsavedChanges != value) {
                    model.warnUnsavedChanges = !value
                }
            }
    var warnInvalidReference: Boolean
            get() = !model.warnInvalidReference
            set(value) {
                if (warnInvalidReference != value) {
                    model.warnInvalidReference = !value
                }
            }
    var warnRelocate: Boolean
            get() = !model.warnRelocate
            set(value) {
                if (warnRelocate != value) {
                    model.warnRelocate = !value
                }
            }
    var warnEmptyData: Boolean
            get() = !model.warnEmptyData
            set(value) {
                if (warnEmptyData != value) {
                    model.warnEmptyData = !value
                }
            }
}
