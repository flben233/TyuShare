import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.6.2"
    kotlin("plugin.serialization") version "1.9.21"
}

group = "org.shirakawatyu.share"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}


kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.material3:material3-desktop:1.6.11")
                implementation("com.formdev:flatlaf:3.2")
                implementation("cn.hutool:hutool-all:5.8.28")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json-jvm:1.6.3")
                implementation("com.github.oshi:oshi-core:6.6.1")
                implementation("com.darkrockstudios:mpfilepicker:2.0.2")
                implementation("com.github.kwhat:jnativehook:2.2.2")
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Exe, TargetFormat.Msi)
            packageName = "TyuShare"
            packageVersion = "2.2.1"
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
