plugins {
    id("java")
    id("org.jetbrains.kotlin.jvm") version "1.9.22"
    id("org.jetbrains.intellij.platform") version "2.10.5"
}

group = "de.bungee.idea.plugins.uifile"
version = "1.0.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

// Exclude Kotlin stdlib to avoid conflicts with IntelliJ Platform's bundled version
configurations.all {
    exclude(
        group = "org.jetbrains.kotlin",
        module = "kotlin-stdlib"
    )
    exclude(
        group = "org.jetbrains.kotlin",
        module = "kotlin-stdlib-common"
    )
    exclude(
        group = "org.jetbrains.kotlin",
        module = "kotlin-stdlib-jdk8"
    )
    exclude(
        group = "org.jetbrains.kotlin",
        module = "kotlin-stdlib-jdk7"
    )
}

dependencies {
    intellijPlatform {
        intellijIdeaUltimate("2025.1")
        bundledPlugins(listOf(/* Plugin Dependencies */))
    }

    testImplementation("junit:junit:4.13.2")
}

// Configure Gradle IntelliJ Platform Plugin
intellijPlatform {
    buildSearchableOptions = false

    pluginConfiguration {
        version = "1.0.0"

        ideaVersion {
            sinceBuild = "251"
            untilBuild = "251.*"
        }
    }

    pluginVerification {
        ides {
            recommended()
        }
    }

    signing {
        certificateChain =
            System.getenv("CERTIFICATE_CHAIN")
        privateKey = System.getenv("PRIVATE_KEY")
        password = System.getenv("PRIVATE_KEY_PASSWORD")
    }

    publishing {
        token = System.getenv("PUBLISH_TOKEN")
    }
}
