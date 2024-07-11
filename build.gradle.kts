import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("jvm") version "2.0.20-Beta2"
    id("org.jetbrains.compose") version "1.6.11"
    kotlin("plugin.serialization") version "2.0.20-Beta2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.20-Beta2"
    id("org.graalvm.buildtools.native") version "0.10.2"
}

group = "org.shirakawatyu.share"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

val disableWindowsTerminal by extra(true)

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

graalvmNative {
    toolchainDetection.set(false)
    binaries{
        named("main"){
            mainClass.set("MainKt")
            imageName.set("TyuShare")
            buildArgs("-O4", "--initialize-at-build-time=kotlin.DeprecationLevel")
        }
    }

    agent{
        metadataCopy {
            mergeWithExisting.set(true)
        }
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation("org.jetbrains.compose.material3:material3-desktop:1.6.11")
    implementation("com.formdev:flatlaf:3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.7.1")
    implementation("com.github.oshi:oshi-core:6.6.1")
    implementation("com.darkrockstudios:mpfilepicker:2.0.2")
    implementation("com.github.kwhat:jnativehook:2.2.2")
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            packageName = "TyuShare"
            packageVersion = "2.2.2"
            description = "TyuShare"
            vendor = "ShirakawaTyu"
            includeAllModules = true
            appResourcesRootDir.set(project.layout.projectDirectory.dir("bin"))
            windows {
                iconFile.set(File("D:\\IdeaProjects\\TyuShare\\favicon-128.ico"))
                upgradeUuid = "2085302f-1da8-3661-c9a4-52588270393e"
                shortcut = true
                dirChooser = true
            }
        }
    }
}
