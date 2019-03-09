import Build_gradle.Props.http4kVersion
import Build_gradle.Props.kotlinPluginArtifact
import Build_gradle.Props.kotlinPluginLocation
import Build_gradle.Props.kotlinVersion
import jdk.nashorn.internal.objects.NativeArray.forEach
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import java.net.URL

group = "io.hexlabs"
version = "0.1-SNAPSHOT"

plugins {
    kotlin("jvm") version "1.3.20"
}

repositories {
    mavenCentral()
}

dependencies {
    compile(
        group = "org.jetbrains.kotlin",
        version = kotlinVersion,
        dependencies = listOf("kotlin-stdlib-jdk8", "kotlin-compiler")
    )
    compile(
        group = "org.http4k",
        version = http4kVersion,
        dependencies = listOf("http4k-core", "http4k-format-jackson", "http4k-multipart")
    )
    compile("com.beust:jcommander:1.72")
    compile(dependencyFrom("https://teamcity.jetbrains.com/guestAuth/repository/download/$kotlinPluginLocation",
        artifact = kotlinPluginArtifact,
        version = kotlinVersion)
    )
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

fun dependencyFrom(url: String, artifact: String, version: String) = File("$buildDir/download/$artifact-$version.jar").let { file ->
    file.parentFile.mkdirs()
    if (!file.exists()) {
        file.writeText(URL(url).readText())
    }
    files(file.absolutePath)
}

fun DependencyHandlerScope.compile(group: String, version: String, dependencies: List<String>){
    dependencies.forEach { dependency -> compile("$group:$dependency:$version") }
}

object Props {
    const val kotlinVersion = "1.3.20"
    const val http4kVersion = "3.115.1"
    const val kotlinPluginArtifact = "kotlin-plugin"
    private const val kotlinRepository = "Kotlin_1320_CompilerAllPlugins"
    private const val kotlinId = "1907319"
    private const val kotlinPluginRelease = "release-IJ2018.1-1"
    const val kotlinPluginLocation = "$kotlinRepository/$kotlinId:id/$kotlinPluginArtifact-$kotlinVersion-$kotlinPluginRelease.zip!/Kotlin/lib/$kotlinPluginArtifact.jar"
}
group = "hexlabs.io"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}
group = "io.hexlabs"
version = "0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    compile(kotlin("stdlib-jdk8"))
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}