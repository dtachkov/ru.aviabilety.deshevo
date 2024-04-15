package appconfig.parsers

import appconfig.GRADLE_TASK_NAME
import appconfig.Localization
import org.gradle.api.Project
import java.io.File
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name

object LocalizationParser {

    const val PATH = "src/main/res/values"

    fun parseLocalization(
        project: Project,
        filename: String,
        parameterName: String,
        localization: Localization
    ) {
        // Clean previously generated files

        cleanPreviouslyGeneratedFiles(project, filename)

        // Generate new files
        localization.localized.keys.forEach { code ->
            val filepath = if (code == localization.base) {
                PATH
            } else {
                "${PATH}-${code.lowercase()}"
            }

            val valuesDir = project.file(filepath)
            if (!valuesDir.exists()) {
                valuesDir.mkdir()
            }

            val outputFile = project.file(valuesDir.path + "/" + filename)

            if (!outputFile.exists()) {
                outputFile.createNewFile()
            }

            outputFile.writeText(
                stringsXmlTemplate.format(parameterName, localization.localized[code])
            )
        }
    }

    private fun cleanPreviouslyGeneratedFiles(
        project: Project,
        filename: String
    ) {
        val resPath = project.file("src/main/res").toPath()
        val dirs = resPath.listDirectoryEntries().filter {
            it.name.matches(Regex("values.*"))
        }

        dirs.forEach { valuesDir ->
            val file = File("$valuesDir/$filename")
            if (file.exists()) {
                file.delete()
            }
        }
    }

    private val stringsXmlTemplate = """
        <?xml version="1.0" encoding="utf-8"?>
        <!--Do not modify. Generated by '$GRADLE_TASK_NAME' task-->
        <resources>
            <string name="%s">%s</string>
        </resources>
    """.trimIndent()

}