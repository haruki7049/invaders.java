// Import necessary classes for tasks and extensions
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.compile.JavaCompile
import org.gradle.language.jvm.tasks.ProcessResources
import java.io.File

// Repositories for the root project
repositories {
    mavenCentral()
}

// Task to generate assets.txt
// From https://lyze.dev/2021/04/29/libGDX-Internal-Assets-List/
tasks.register("generateAssetList") {
    // Define the assets directory as an input
    val assetsDir = rootProject.layout.projectDirectory.dir("assets")
    inputs.dir(assetsDir)

    // Define the output file for incremental build support
    val assetsFile = assetsDir.file("assets.txt")
    outputs.file(assetsFile)

    // Use doLast to ensure file operations run during the execution phase
    doLast {
        val assetsFolder = assetsDir.asFile
        val assetsFileAsFile = assetsFile.asFile // Get File object from RegularFile

        // Delete the file if it already exists
        assetsFileAsFile.delete()

        // Iterate through all files, get relative paths, sort them
        val assetPaths =
            assetsFolder
                .walkTopDown()
                .filter { it.isFile && it.name != "assets.txt" } // Exclude assets.txt itself
                .map {
                    // Use URI relativize to ensure '/' (slash) separators, not '\' (backslash)
                    assetsFolder.toURI().relativize(it.toURI()).path
                }.sorted()

        // Write all paths to the file, separated by newline
        if (assetPaths.toList().isNotEmpty()) {
            assetsFileAsFile.writeText(assetPaths.joinToString(separator = "\n", postfix = "\n"))
        }
    }
}

// Configure all subprojects
subprojects {
    // Apply the java-library plugin
    apply(plugin = "java-library")

    // Configure Java extension (e.g., source compatibility)
    configure<JavaPluginExtension> {
        sourceCompatibility = JavaVersion.VERSION_21
    }

    // Set version from project property (e.g., in gradle.properties)
    // Provide a default value if not set
    version = project.property("projectVersion") ?: "1.0.0"

    // Set extension properties using 'extra'
    extra["appName"] = "invaders"

    // Repositories for subprojects
    repositories {
        mavenCentral()
    }

    // Make processResources depend on the asset list generation
    tasks.withType<ProcessResources> {
        dependsOn(":generateAssetList")
    }

    // Configure Java compilation options
    tasks.withType<JavaCompile> {
        options.isIncremental = true
    }
}
