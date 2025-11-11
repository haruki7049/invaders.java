import org.gradle.api.tasks.compile.JavaCompile

// Access properties from gradle.properties
val ashleyVersion: String by project
val gdxControllersVersion: String by project
val gdxVersion: String by project
val enableGraalNative: String by project
val graalHelperVersion: String by project

// Set encoding for Java compilation tasks
tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    // Defines the API dependencies for the core project
    api("com.badlogicgames.ashley:ashley:$ashleyVersion")
    api("com.badlogicgames.gdx-controllers:gdx-controllers-core:$gdxControllersVersion")
    api("com.badlogicgames.gdx:gdx-freetype:$gdxVersion")
    api("com.badlogicgames.gdx:gdx:$gdxVersion")

    // Conditionally add GraalVM helper annotations
    if (enableGraalNative == "true") {
        implementation("io.github.berstanio:gdx-svmhelper-annotations:$graalHelperVersion")
    }
}
