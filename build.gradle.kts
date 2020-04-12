val platform: String? by project
val platformProperty: String = platform ?: "linux"

plugins {
    kotlin("multiplatform") version "1.3.70"
}

repositories {
    mavenCentral()
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }

        val generators by creating {
            dependencies {
                implementation(kotlin("stdlib-common"))
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
            dependsOn(generators)
        }

        jvm().compilations["main"].defaultSourceSet {
            dependencies {
                implementation(kotlin("stdlib-jdk8"))
                implementation("org.openjfx:javafx-controls:11.0.2:$platformProperty")
                implementation("org.openjfx:javafx-graphics:11.0.2:$platformProperty")
                implementation("org.openjfx:javafx-base:11.0.2:$platformProperty")
            }
        }

        jvm().compilations["test"].defaultSourceSet {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }

    targets.all {
        compilations.all {
            kotlinOptions {
                allWarningsAsErrors = true
            }
        }
    }
}