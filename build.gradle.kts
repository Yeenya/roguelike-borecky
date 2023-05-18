plugins {
    kotlin("multiplatform") version "1.8.21"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "cz.cuni.gamedev.nail123"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "19"
        }
        testRuns["test"].executionTask.configure {
            useJUnit()
        }
        withJava()
    }
    sourceSets {
        val commonMain by getting
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(kotlin("stdlib"))
                implementation("io.github.gabrielshanahan:moroccode:1.0.0")
                implementation("org.hexworks.zircon:zircon.core-jvm:2020.1.6-HOTFIX")
                implementation("org.hexworks.zircon:zircon.jvm.swing:2020.1.6-HOTFIX")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }
    }
}

tasks {
    build {
        dependsOn("zipTilesets")
    }
    clean {
        dependsOn("cleanTilesets")
    }
    wrapper {
        distributionType = Wrapper.DistributionType.ALL
    }
    getByName<ProcessResources>("jvmProcessResources") {
        dependsOn("zipTilesets")
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
    }
    getByName<Jar>("shadowJar") {
        manifest {
            attributes["Main-Class"] = "cz.cuni.gamedev.nail123.roguelike.MainKt"
        }
    }
    getByName("compileKotlinJvm") { mustRunAfter("zipTilesets") }
}

tasks.register<JavaExec>("renderPng") {
    group = "custom"
    dependsOn("zipTilesets", "compileKotlinJvm")

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("cz.cuni.gamedev.nail123.roguelike.RenderAreaKt")
}

tasks.register<JavaExec>("playGame") {
    group = "custom"
    dependsOn("zipTilesets", "compileKotlinJvm")

    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("cz.cuni.gamedev.nail123.roguelike.RunGameKt")
}

val tilesetDirectories: Array<File> = File("tilesets").listFiles(File::isDirectory)!!
for (directory in tilesetDirectories) {
    val directoryName = directory.name
    tasks.register<Zip>("zipTileset-$directoryName") {
        group = "other"
        duplicatesStrategy = DuplicatesStrategy.WARN
        from(directory)

        archiveFileName.set("$directoryName.zip")
        destinationDirectory.set(file("src/jvmMain/resources/tilesets/$directoryName"))
    }
}

tasks.register("zipTilesets") {
    group = "custom"

    dependsOn(tilesetDirectories.map { "zipTileset-${it.name}" })
}
tasks.register<Delete>("cleanTilesets") {
    group = "custom"
    delete("src/jvmMain/resources/tilesets")
}
