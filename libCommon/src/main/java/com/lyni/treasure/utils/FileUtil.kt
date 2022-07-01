package com.lyni.treasure.utils

import android.os.Environment
import android.webkit.MimeTypeMap
import androidx.annotation.IntDef
import com.lyni.treasure.ktx.appContext
import com.lyni.treasure.ktx.cnCompare
import com.lyni.treasure.ktx.printOnDebug
import java.io.*
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Pattern

@Suppress("unused", "MemberVisibilityCanBePrivate")
object FileUtil {

    fun getExternalFilesDir(): File = appContext.getExternalFilesDir(null) ?: appContext.filesDir

    fun getExternalCacheDir(): File = appContext.externalCacheDir ?: appContext.cacheDir

    fun createFileIfNotExist(root: File, vararg subDirFiles: String): File {
        val filePath = getPath(root, *subDirFiles)
        return createFileIfNotExist(filePath)
    }

    fun createFolderIfNotExist(root: File, vararg subDirs: String): File {
        val filePath = getPath(root, *subDirs)
        return createFolderIfNotExist(filePath)
    }

    fun createFolderIfNotExist(filePath: String): File {
        val file = File(filePath)
        if (!file.exists()) {
            file.mkdirs()
        }
        return file
    }

    @Synchronized
    fun createFileIfNotExist(filePath: String): File {
        val file = File(filePath)
        try {
            if (!file.exists()) {
                //创建父类文件夹
                file.parent?.let {
                    createFolderIfNotExist(it)
                }
                //创建文件
                file.createNewFile()
            }
        } catch (e: IOException) {
            e.printOnDebug()
        }
        return file
    }

    fun createFileOrReplace(filePath: String): File {
        val file = File(filePath)
        if (!file.exists()) {
            //创建父类文件夹
            file.parent?.let {
                createFolderIfNotExist(it)
            }
            //创建文件
            file.createNewFile()
        } else {
            file.delete()
            file.createNewFile()
        }
        return file
    }

    fun getPath(rootPath: String, vararg subDirFiles: String): String {
        val path = StringBuilder(rootPath)
        subDirFiles.forEach {
            if (it.isNotEmpty()) {
                if (!path.endsWith(File.separator)) {
                    path.append(File.separator)
                }
                path.append(it)
            }
        }
        return path.toString()
    }

    fun getPath(root: File, vararg subDirFiles: String): String {
        val path = StringBuilder(root.absolutePath)
        subDirFiles.forEach {
            if (it.isNotEmpty()) {
                path.append(File.separator).append(it)
            }
        }
        return path.toString()
    }

    fun getCachePath(): String {
        return getExternalCacheDir().absolutePath
    }

    fun getSdCardPath(): String {
        var sdCardDirectory = Environment.getExternalStorageDirectory().absolutePath
        try {
            sdCardDirectory = File(sdCardDirectory).canonicalPath
        } catch (e: IOException) {
            e.printOnDebug()
        }
        return sdCardDirectory
    }

    const val BY_NAME_ASC = 0
    const val BY_NAME_DESC = 1
    const val BY_TIME_ASC = 2
    const val BY_TIME_DESC = 3
    const val BY_SIZE_ASC = 4
    const val BY_SIZE_DESC = 5
    const val BY_EXTENSION_ASC = 6
    const val BY_EXTENSION_DESC = 7

    @IntDef(value = [BY_NAME_ASC, BY_NAME_DESC, BY_TIME_ASC, BY_TIME_DESC, BY_SIZE_ASC, BY_SIZE_DESC, BY_EXTENSION_ASC, BY_EXTENSION_DESC])
    @Retention(AnnotationRetention.SOURCE)
    annotation class SortType

    fun separator(path: String): String {
        var path1 = path
        val separator = File.separator
        path1 = path1.replace("\\", separator)
        if (!path1.endsWith(separator)) {
            path1 += separator
        }
        return path1
    }

    fun closeSilently(c: Closeable?) {
        if (c == null) {
            return
        }
        try {
            c.close()
        } catch (ignored: IOException) {
        }

    }

    @JvmOverloads
    fun listDirs(
        startDirPath: String,
        excludeDirs: Array<String>? = null, @SortType sortType: Int = BY_NAME_ASC
    ): Array<File> {
        var excludeDirs1 = excludeDirs
        val dirList = ArrayList<File>()
        val startDir = File(startDirPath)
        if (!startDir.isDirectory) {
            return arrayOf()
        }
        val dirs = startDir.listFiles(FileFilter { f ->
            if (f == null) {
                return@FileFilter false
            }
            f.isDirectory
        }) ?: return arrayOf()
        if (excludeDirs1 == null) {
            excludeDirs1 = arrayOf()
        }
        for (dir in dirs) {
            val file = dir.absoluteFile
            if (!excludeDirs1.contentDeepToString().contains(file.name)) {
                dirList.add(file)
            }
        }
        when (sortType) {
            BY_NAME_ASC -> Collections.sort(dirList, SortByName())
            BY_NAME_DESC -> {
                Collections.sort(dirList, SortByName())
                dirList.reverse()
            }
            BY_TIME_ASC -> Collections.sort(dirList, SortByTime())
            BY_TIME_DESC -> {
                Collections.sort(dirList, SortByTime())
                dirList.reverse()
            }
            BY_SIZE_ASC -> Collections.sort(dirList, SortBySize())
            BY_SIZE_DESC -> {
                Collections.sort(dirList, SortBySize())
                dirList.reverse()
            }
            BY_EXTENSION_ASC -> Collections.sort(dirList, SortByExtension())
            BY_EXTENSION_DESC -> {
                Collections.sort(dirList, SortByExtension())
                dirList.reverse()
            }
        }
        return dirList.toTypedArray()
    }

    @JvmOverloads
    fun listDirsAndFiles(
        startDirPath: String,
        allowExtensions: Array<String>? = null
    ): Array<File> {
        val dirs: Array<File>?
        val files: Array<File>? = if (allowExtensions == null) {
            listFiles(startDirPath)
        } else {
            listFiles(startDirPath, allowExtensions)
        }
        dirs = listDirs(startDirPath)
        if (files == null) {
            return arrayOf()
        }
        val res = dirs + files
        res.forEach {
            Log.e("TAG", "listDirsAndFiles: ${it.name}")
        }
        return res
    }

    @JvmOverloads
    fun listFiles(
        startDirPath: String,
        filterPattern: Pattern? = null, @SortType sortType: Int = BY_NAME_ASC
    ): Array<File> {
        val fileList = ArrayList<File>()
        val f = File(startDirPath)
        if (!f.isDirectory) {
            return arrayOf()
        }
        val files = f.listFiles(FileFilter { file ->
            if (file == null) {
                return@FileFilter false
            }
            if (file.isDirectory) {
                return@FileFilter false
            }

            filterPattern?.matcher(file.name)?.find() ?: true
        })
            ?: return arrayOf()
        for (file in files) {
            fileList.add(file.absoluteFile)
        }
        when (sortType) {
            BY_NAME_ASC -> Collections.sort(fileList, SortByName())
            BY_NAME_DESC -> {
                Collections.sort(fileList, SortByName())
                fileList.reverse()
            }
            BY_TIME_ASC -> Collections.sort(fileList, SortByTime())
            BY_TIME_DESC -> {
                Collections.sort(fileList, SortByTime())
                fileList.reverse()
            }
            BY_SIZE_ASC -> Collections.sort(fileList, SortBySize())
            BY_SIZE_DESC -> {
                Collections.sort(fileList, SortBySize())
                fileList.reverse()
            }
            BY_EXTENSION_ASC -> Collections.sort(fileList, SortByExtension())
            BY_EXTENSION_DESC -> {
                Collections.sort(fileList, SortByExtension())
                fileList.reverse()
            }
        }
        return fileList.toTypedArray()
    }

    fun listFiles(startDirPath: String, allowExtensions: Array<String>?): Array<File>? {
        val file = File(startDirPath)
        return file.listFiles { _, name ->
            //返回当前目录所有以某些扩展名结尾的文件
            val extension = getExtension(name)
            allowExtensions?.contentDeepToString()?.contains(extension) == true
                    || allowExtensions == null
        }
    }

    fun listFiles(startDirPath: String, allowExtension: String?): Array<File>? {
        return if (allowExtension == null)
            listFiles(startDirPath, allowExtension = null)
        else
            listFiles(startDirPath, arrayOf(allowExtension))
    }

    fun exist(path: String): Boolean {
        val file = File(path)
        return file.exists()
    }

    @JvmOverloads
    fun delete(file: File, deleteRootDir: Boolean = false): Boolean {
        var result = false
        if (file.isFile) {
            //是文件
            result = deleteNonEBUSY(file)
        } else {
            //是目录
            val files = file.listFiles() ?: return false
            if (files.isEmpty()) {
                result = deleteRootDir && deleteNonEBUSY(file)
            } else {
                for (f in files) {
                    delete(f, deleteRootDir)
                    result = deleteNonEBUSY(f)
                }
            }
            if (deleteRootDir) {
                result = deleteNonEBUSY(file)
            }
        }
        return result
    }

    private fun deleteNonEBUSY(file: File): Boolean {
        val to = File(file.absolutePath + System.currentTimeMillis())
        file.renameTo(to)
        return to.delete()
    }

    @JvmOverloads
    fun delete(path: String, deleteRootDir: Boolean = true): Boolean {
        val file = File(path)

        return if (file.exists()) {
            delete(file, deleteRootDir)
        } else false
    }

    fun copy(src: String, tar: String): Boolean {
        val srcFile = File(src)
        return srcFile.exists() && copy(srcFile, File(tar))
    }

    fun copy(src: File, tar: File): Boolean {
        try {
            if (src.isFile) {
                val inputStream = FileInputStream(src)
                val outputStream = FileOutputStream(tar)
                inputStream.use {
                    outputStream.use {
                        inputStream.copyTo(outputStream)
                        outputStream.flush()
                    }
                }
            } else if (src.isDirectory) {
                tar.mkdirs()
                src.listFiles()?.forEach { file ->
                    copy(file.absoluteFile, File(tar.absoluteFile, file.name))
                }
            }
            return true
        } catch (e: Exception) {
            return false
        }

    }

    fun move(src: String, tar: String): Boolean = move(File(src), File(tar))

    fun move(src: File, tar: File): Boolean = rename(src, tar)

    fun rename(oldPath: String, newPath: String) = rename(File(oldPath), File(newPath))

    fun rename(src: File, tar: File) = src.renameTo(tar)

    @JvmOverloads
    fun readText(filepath: String, charset: String = "utf-8"): String {
        try {
            val data = readBytes(filepath)
            if (data != null) {
                return String(data, Charset.forName(charset)).trim { it <= ' ' }
            }
        } catch (ignored: UnsupportedEncodingException) {
        }
        return ""
    }

    fun readBytes(filepath: String): ByteArray? {
        var fis: FileInputStream? = null
        try {
            fis = FileInputStream(filepath)
            val outputStream = ByteArrayOutputStream()
            val buffer = ByteArray(1024)
            while (true) {
                val len = fis.read(buffer, 0, buffer.size)
                if (len == -1) {
                    break
                } else {
                    outputStream.write(buffer, 0, len)
                }
            }
            val data = outputStream.toByteArray()
            outputStream.close()
            return data
        } catch (e: IOException) {
            return null
        } finally {
            closeSilently(fis)
        }
    }

    @JvmOverloads
    fun writeText(filepath: String, content: String, charset: String = "utf-8"): Boolean {
        return try {
            writeBytes(filepath, content.toByteArray(charset(charset)))
        } catch (e: UnsupportedEncodingException) {
            false
        }

    }

    fun writeBytes(filepath: String, data: ByteArray): Boolean {
        val file = File(filepath)
        var fos: FileOutputStream? = null
        return try {
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                file.createNewFile()
            }
            fos = FileOutputStream(filepath)
            fos.write(data)
            true
        } catch (e: IOException) {
            false
        } finally {
            closeSilently(fos)
        }
    }

    fun writeInputStream(filepath: String, data: InputStream): Boolean {
        val file = File(filepath)
        return writeInputStream(file, data)
    }

    fun writeInputStream(file: File, data: InputStream): Boolean {
        return try {
            if (!file.exists()) {
                file.parentFile?.mkdirs()
                file.createNewFile()
            }
            data.use {
                FileOutputStream(file).use { fos ->
                    data.copyTo(fos)
                    fos.flush()
                }
            }
            true
        } catch (e: IOException) {
            false
        }
    }

    fun appendText(path: String, content: String): Boolean {
        val file = File(path)
        var writer: FileWriter? = null
        return try {
            if (!file.exists()) {
                file.createNewFile()
            }
            writer = FileWriter(file, true)
            writer.write(content)
            true
        } catch (e: IOException) {
            false
        } finally {
            closeSilently(writer)
        }
    }

    fun getLength(path: String): Long {
        val file = File(path)
        return if (!file.isFile || !file.exists()) {
            0
        } else file.length()
    }

    fun getName(pathOrUrl: String?): String {
        if (pathOrUrl == null) {
            return ""
        }
        val pos = pathOrUrl.lastIndexOf('/')
        return if (0 <= pos) {
            pathOrUrl.substring(pos + 1)
        } else {
            System.currentTimeMillis().toString() + "." + getExtension(pathOrUrl)
        }
    }

    fun getNameExcludeExtension(path: String): String {
        return try {
            var fileName = File(path).name
            val lastIndexOf = fileName.lastIndexOf(".")
            if (lastIndexOf != -1) {
                fileName = fileName.substring(0, lastIndexOf)
            }
            fileName
        } catch (e: Exception) {
            ""
        }

    }

    fun getExtension(pathOrUrl: String): String {
        val dotPos = pathOrUrl.lastIndexOf('.')
        return if (0 <= dotPos) {
            pathOrUrl.substring(dotPos + 1)
        } else {
            "ext"
        }
    }

    fun getMimeType(pathOrUrl: String): String {
        val ext = getExtension(pathOrUrl)
        val map = MimeTypeMap.getSingleton()
        return map.getMimeTypeFromExtension(ext) ?: "*/*"
    }

    fun makeDirs(path: String): Boolean = makeDirs(File(path))

    fun makeDirs(file: File): Boolean = file.mkdirs()

    class SortByExtension : Comparator<File> {

        override fun compare(f1: File?, f2: File?): Int {
            return if (f1 == null || f2 == null) {
                if (f1 == null) -1 else 1
            } else {
                if (f1.isDirectory && f2.isFile) {
                    -1
                } else if (f1.isFile && f2.isDirectory) {
                    1
                } else {
                    f1.name.compareTo(f2.name, ignoreCase = true)
                }
            }
        }

    }

    class SortByName : Comparator<File> {
        private var caseSensitive: Boolean = false

        constructor(caseSensitive: Boolean) {
            this.caseSensitive = caseSensitive
        }

        constructor() {
            this.caseSensitive = false
        }

        override fun compare(f1: File?, f2: File?): Int {
            if (f1 == null || f2 == null) {
                return if (f1 == null) {
                    -1
                } else {
                    1
                }
            } else {
                return if (f1.isDirectory && f2.isFile) {
                    -1
                } else if (f1.isFile && f2.isDirectory) {
                    1
                } else {
                    val s1 = f1.name
                    val s2 = f2.name
                    if (caseSensitive) {
                        s1.cnCompare(s2)
                    } else {
                        s1.compareTo(s2, ignoreCase = true)
                    }
                }
            }
        }

    }

    class SortBySize : Comparator<File> {
        override fun compare(f1: File?, f2: File?): Int {
            return if (f1 == null || f2 == null) {
                if (f1 == null) {
                    -1
                } else {
                    1
                }
            } else {
                if (f1.isDirectory && f2.isFile) {
                    -1
                } else if (f1.isFile && f2.isDirectory) {
                    1
                } else {
                    if (f1.length() < f2.length()) {
                        -1
                    } else {
                        1
                    }
                }
            }
        }

    }

    class SortByTime : Comparator<File> {

        override fun compare(f1: File?, f2: File?): Int {
            return if (f1 == null || f2 == null) {
                if (f1 == null) {
                    -1
                } else {
                    1
                }
            } else {
                if (f1.isDirectory && f2.isFile) {
                    -1
                } else if (f1.isFile && f2.isDirectory) {
                    1
                } else {
                    if (f1.lastModified() > f2.lastModified()) {
                        -1
                    } else {
                        1
                    }
                }
            }
        }

    }
}
