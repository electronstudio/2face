plugins {
    java
    kotlin("jvm") version "1.3.72"
    application
}

application {
    mainClassName = "uk.co.electronstudio.gopher.Main"
}

group = "uk.co.electronstudio"
version = ""

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    testCompile("junit", "junit", "4.12")
    implementation("com.googlecode.lanterna:lanterna:3.0.3")
    implementation("net.sourceforge.argparse4j", "argparse4j", "0.8.1")
   // implementation("org.apache.commons","commons-text","1.8")
   // implementation("org.pushing-pixels:radiance-substance:2.5.1")

}

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_1_8
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }


}

val jar by tasks.getting(Jar::class) {
    manifest {
        attributes["Main-Class"] = "uk.co.electronstudio.gopher.Main"
    }
}

val fatJar = task("fatJar", type = Jar::class) {
    baseName = "2face"
    manifest {
        attributes["Implementation-Title"] = "2face"
        attributes["Implementation-Version"] = version
        attributes["Main-Class"] = "uk.co.electronstudio.gopher.Main"
    }
    from(configurations.runtimeClasspath.get().map({ if (it.isDirectory) it else zipTree(it) }))
    with(tasks.jar.get() as CopySpec)
}
