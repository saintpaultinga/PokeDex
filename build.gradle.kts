import com.github.benmanes.gradle.versions.updates.DependencyUpdatesTask
import com.novoda.staticanalysis.StaticAnalysisExtension
import io.gitlab.arturbosch.detekt.detekt
import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {

    repositories {
        google()
        jcenter()
        maven("https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Versions.buildTools}")
        classpath(kotlin("gradle-plugin", version = Versions.kotlin))
        classpath("io.gitlab.arturbosch.detekt:detekt-gradle-plugin:${Versions.detekt}")
        classpath("org.jlleitschuh.gradle:ktlint-gradle:${Versions.ktlint}")
        classpath("com.novoda:gradle-static-analysis-plugin:${Versions.staticAnalysis}")
        classpath("com.github.ben-manes:gradle-versions-plugin:${Versions.versionsPlugin}")
        classpath("androidx.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}")
        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}

allprojects {
    repositories {
        google()
        jcenter()
    }
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "io.gitlab.arturbosch.detekt")
    apply(plugin = "com.novoda.static-analysis")

    configure<StaticAnalysisExtension> {
        penalty {
            maxErrors = 0
            maxWarnings = 0
        }

        ktlint {
            version.set("0.35.0")
            android.set(true)
            enableExperimentalRules.set(true)
            reporters {
                reporter(ReporterType.PLAIN)
                reporter(ReporterType.CHECKSTYLE)
            }
            additionalEditorconfigFile.set(file("${project.projectDir}/.editorConfig"))
        }

        detekt {
            config = files("${project.projectDir}/config/detekt/detekt.yml")
        }

        // TODO: Figure out why this doesn't build
        // lintOptions {
        //
        // }
    }
}

tasks.register<Delete>("clean") {
    delete(rootProject.buildDir)
}

afterEvaluate {
    // We install the hook at the first occasion
    tasks["clean"].dependsOn(tasks.getByName("addKtlintFormatGitPreCommitHook"))
}

/**
 * This is a special task that allows us to pass a flag to avoid any tasks with Lint in the name.
 *
 * To use, you can call `./gradlew build -PnoLint` from the command line.
 *
 * https://kousenit.org/2016/04/20/excluding-gradle-tasks-with-a-name-pattern/
 */
gradle.taskGraph.whenReady {
    if (project.hasProperty("noLint")) {
        this.allTasks.filter {
            it.name.contains("lint")
        }.forEach {
            it.enabled = false
        }
    }
}

plugins {
    id("org.jlleitschuh.gradle.ktlint").version(Versions.ktlint)
    id("org.jlleitschuh.gradle.ktlint-idea").version(Versions.ktlint)
    id("com.github.ben-manes.versions").version(Versions.versionsPlugin)
}

tasks.withType<DependencyUpdatesTask> {
    checkForGradleUpdate = true

    resolutionStrategy {
        componentSelection {
            all {
                val rejected = listOf(
                    "alpha",
                    "beta",
                    "rc",
                    "cr",
                    "m",
                    "preview",
                    "b",
                    "ea"
                ).any { qualifier ->
                    this.candidate.version.matches(Regex("/(?i).*[.-]$qualifier[.\\d-+]*/"))
                }

                if (rejected) {
                    reject("Release Candidate")
                }
            }
        }
    }
}