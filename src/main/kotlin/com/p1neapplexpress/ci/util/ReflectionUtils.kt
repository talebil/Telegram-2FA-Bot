package com.p1neapplexpress.ci.util

import java.io.File
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile

object ReflectionUtils {

    private const val PACKAGE_NAME = "com.p1neapplexpress.ci"

    fun searchForClassesWithAnnotation(a: Class<out Annotation>): MutableList<Class<*>> {
        val packageName = PACKAGE_NAME.replace(".", "/")
        val result = mutableListOf<Class<*>>()

        fun createAndCheck(className: String) {
            if (PACKAGE_NAME !in className) return
            try {
                val clazz = Class.forName(className)
                val annotations = clazz.declaredAnnotations
                annotations.forEach { annotation ->
                    if (annotation.annotationClass.simpleName == a.simpleName) {
                        result.add(clazz)
                    }
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }

        val listFiles = File(".").listFiles()
        if (listFiles?.any { it.name.endsWith(".jar") } == true) {
            val jarFile = JarFile(listFiles.first { it.name.endsWith(".jar") })
            val entries: Enumeration<JarEntry> = jarFile.entries()
            while (entries.hasMoreElements()) {
                try {
                    val entry: JarEntry = entries.nextElement()
                    if (entry.name.endsWith(".class")) {
                        val className: String = entry.name.replace("/", ".").replace(".class", "")
                        createAndCheck(className)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            jarFile.close()
            return result
        }

        val classLoader = Thread.currentThread().contextClassLoader
        val resources = classLoader.getResources(packageName)
        while (resources.hasMoreElements()) {
            val rootPath = Paths.get(resources.nextElement().toURI())
            Files.walkFileTree(rootPath, object : SimpleFileVisitor<Path>() {
                override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
                    if (file.toString().endsWith(".class")) {
                        val className = rootPath.relativize(file).toString().replace(".class", "").replace("/", ".")
                        createAndCheck("$PACKAGE_NAME.$className")
                    }
                    return FileVisitResult.CONTINUE
                }

            })
        }

        return result
    }
}