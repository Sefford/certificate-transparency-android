import com.appmattus.markdown.rules.LineLengthRule
import com.appmattus.markdown.rules.ProperNamesRule
import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask

buildscript {
    repositories {
        google()
        jcenter()
    }
    dependencies {
        classpath("com.android.tools.build:gradle:3.5.2")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.3.60")
        classpath("org.jetbrains.dokka:dokka-gradle-plugin:0.10.0")
        classpath("org.owasp:dependency-check-gradle:5.2.4")
        classpath("org.kt3k.gradle.plugin:coveralls-gradle-plugin:2.8.3")
        classpath("com.novoda:bintray-release:0.9.1")
    }
}

plugins {
    id("io.gitlab.arturbosch.detekt") version "1.1.1"
    id("com.github.ben-manes.versions") version "0.27.0"
    id("com.appmattus.markdown") version "0.6.0"
}

allprojects {
    repositories {
        google()
        jcenter()

        // For material dialogs
        maven(url = "https://dl.bintray.com/drummer-aidan/maven/")
    }
}

task("clean", type = Delete::class) {
    delete(rootProject.buildDir)
}

dependencies {
    detektPlugins("io.gitlab.arturbosch.detekt:detekt-formatting:1.1.1")
}

tasks.named<DependencyUpdatesTask>("dependencyUpdates") {
    resolutionStrategy {
        componentSelection {
            all {
                fun isNonStable(version: String) = listOf("alpha", "beta", "rc", "cr", "m", "preview", "b", "ea").any { qualifier ->
                    version.matches(Regex("(?i).*[.-]$qualifier[.\\d-+]*"))
                }
                if (isNonStable(candidate.version) && !isNonStable(currentVersion)) {
                    reject("Release candidate")
                }
            }
        }
    }
}

detekt {
    input = files("$projectDir")
    buildUponDefaultConfig = true
    config = files("detekt-config.yml")
}

markdownlint {
    rules {
        +LineLengthRule(codeBlocks = false)
        +ProperNamesRule { excludes = listOf(".*/NOTICE.md") }
    }
}
