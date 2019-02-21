package kotlinx.team.infra.node

import groovy.json.*
import kotlinx.team.infra.*
import org.gradle.api.*
import org.gradle.api.tasks.*
import org.gradle.process.internal.*
import java.io.*
import javax.inject.*

open class NpmInstallTask : DefaultTask() {
    private val config = NodeExtension[project]
    private val variant by lazy { config.buildVariant() }

    @Input
    var packages = mutableListOf<Any>()

    @TaskAction
    fun exec() {
        val packages = packages.map { it.toString() }
        
        logger.infra("Verifying node packages: $packages")
        val exec = getExecActionFactory().newExecAction().apply {
            workingDir = config.nodeModulesContainer
            executable = variant.npmExec
            isIgnoreExitValue = true
        }

        exec.args = listOf("list", "-json") + packages
        val captureOutput = ByteArrayOutputStream()
        val errorOutput = ByteArrayOutputStream()
        exec.errorOutput = errorOutput 
        exec.standardOutput = captureOutput
        
        logger.trace(captureOutput.toString("UTF-8"))
        logger.trace(errorOutput.toString("UTF-8"))
        
        val result = exec.execute()
        val install = if (result.exitValue != 0) {
            // install all
            packages
        } else {
            val output = captureOutput.toString("UTF-8")
            // TODO: parse JSON properly
            packages.filter { !output.contains(it) }
        }

        if (install.isEmpty()) {
            return
        }

        logger.infra("Installing node packages: $packages")

        exec.isIgnoreExitValue = false
        exec.args = listOf("install") + packages
        exec.execute()
    }

    @Inject
    protected open fun getExecActionFactory(): ExecActionFactory = throw UnsupportedOperationException()
}