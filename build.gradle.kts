plugins {
    java
    `maven-publish`
}

group = "top.mrxiaom"
version = "5.1.5.2"

repositories {
    mavenLocal()
    maven("https://mirrors.huaweicloud.com/repository/maven/")
    mavenCentral()
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.onarandombox.com/content/groups/public/")
    maven("https://repo.mikeprimm.com/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://maven.fastmirror.net/repositories/minecraft")
    maven("https://jitpack.io/")
    maven("https://oss.sonatype.org/content/groups/public/")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.20.4-R0.1-SNAPSHOT")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.2")
    compileOnly("com.onarandombox.multiversecore:Multiverse-Core:4.3.1")
    compileOnly("us.dynmap:dynmap-api:3.4")
    compileOnly("us.dynmap:DynmapCoreAPI:3.4")
    compileOnly("me.clip:placeholderapi:2.11.2")
    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.Zrips:CMILib:1.4.7.4")

    implementation(fileTree("libs"))
}

val targetJavaVersion = 8
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion.set(JavaLanguageVersion.of(targetJavaVersion))
    }
    withSourcesJar()
}

tasks{
    withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
            options.release.set(targetJavaVersion)
        }
    }
    processResources {
        duplicatesStrategy = DuplicatesStrategy.INCLUDE
        from(sourceSets.main.get().resources.srcDirs) {
            expand(mapOf("version" to version))
            include("plugin.yml")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenRelease") {
            from(components.getByName("java"))
            groupId = project.group.toString()
            artifactId = project.name
            version = project.version.toString()
        }
    }
}
