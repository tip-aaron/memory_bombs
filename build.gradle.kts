plugins {
    java
    application
    id("com.gradleup.shadow") version "8.3.5"
}

group = "com.github.tip-aaron"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

dependencies {
    // ===== FLATLAF =====
    implementation("com.formdev:flatlaf:3.6.1")
    implementation("com.formdev:flatlaf-extras:3.6.2")
    
    // ===== LAYOUT =====
    implementation("com.miglayout:miglayout-swing:11.4.2")
    
    // ===== LOMBOK =====
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    
    // ===== STRINGS =====
    implementation("org.apache.commons:commons-text:1.14.0")
    implementation("commons-beanutils:commons-beanutils:1.11.0")
    
    // ===== ANNOTATIONS =====
    implementation("org.jetbrains:annotations:24.0.1")
    
    // ===== CLASS GRAPHS =====
    implementation("io.github.classgraph:classgraph:4.8.179")
    
    // ===== TESTS =====
    testImplementation(platform("org.junit:junit-bom:5.11.0"))
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
}

application {
    mainClass.set("memory_bombs.App")
    mainModule.set("memory_bombs")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "memory_bombs.App"
        )
    }
}

tasks.shadowJar {
    archiveClassifier.set("launcher")
    // Exclude module-info and license files similar to Maven shade plugin
    exclude("module-info.class")
    exclude("META-INF/versions/9/module-info.class")
    exclude("META-INF/MANIFEST.MF")
    exclude("META-INF/LICENSE")
    
    manifest {
        attributes(
            "Main-Class" to "memory_bombs.App"
        )
    }
}

tasks.processResources {
    // Filter .properties files for dynamic values (similar to Maven)
    filesMatching("**/*.properties") {
        expand(project.properties)
    }
}
