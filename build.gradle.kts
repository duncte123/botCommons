/*
 *    Copyright 2018 - 2019 Duncan "duncte123" Sterken
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

plugins {
    idea
    `java-library`
    `maven-publish`
}

group = "me.duncte123"
version = "${getVersionPrefix()}2.3.${getBuildNum()}"
val archivesBaseName = "botCommons"

repositories {
    mavenCentral()
    maven("https://m2.dv8tion.net/releases")
    maven("https://m2.duncte123.dev/releases")
    maven("https://duncte123.jfrog.io/artifactory/maven")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val jdaVersion = "4.4.0_352"

dependencies {
    api(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.10.1")
    api(group = "org.jsoup", name = "jsoup", version = "1.13.1")
    api(group = "com.squareup.okhttp3", name = "okhttp", version = "3.14.9")
    api(group = "me.duncte123", name = "reliqua", version = "2.6.5") {
        exclude(group = "com.squareup.okhttp3", module = "okhttp")
    }

    compileOnly(group = "org.json", name = "json", version = "20180813") // Provided by the user
    compileOnly(group = "net.dv8tion", name = "JDA", version = jdaVersion) {
        exclude(module = "opus-java")
    }

    testImplementation (group = "net.dv8tion", name = "JDA", version = jdaVersion) {
        exclude(module = "opus-java")
    }
    testImplementation(group = "junit", name = "junit", version = "4.12")
    testImplementation("com.squareup.okhttp3:mockwebserver:3.14.4")

}

fun getBuildNum(): String {
    return System.getenv("GITHUB_RUN_NUMBER") ?: "dev"
}

fun getVersionPrefix(): String {
    return System.getenv("VERSION_PREFIX") ?: ""
}

val compileJava: JavaCompile by tasks
val javadoc: Javadoc by tasks
val jar: Jar by tasks
val build: Task by tasks
val publish: Task by tasks
val clean: Task by tasks
val test: Task by tasks
val check: Task by tasks

javadoc.apply {
    isFailOnError = false
}

val sourcesJar = task<Jar>("sourcesJar") {
    archiveClassifier.set("sources")
    from(sourceSets["main"].allJava)
}

val javadocJar = task<Jar>("javadocJar") {
    dependsOn(javadoc)
    archiveClassifier.set("javadoc")
}

publishing {
    repositories {
        maven {
            name = "duncte123-m2"
            url = uri("https://m2.duncte123.dev/releases")
            credentials {
                username = System.getenv("USERNAME")
                password = System.getenv("PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("duncte123-m2") {
            from(components["java"])

            artifactId = archivesBaseName
            groupId = project.group as String
            version = project.version as String

            artifact(javadocJar)
            artifact(sourcesJar)
        }
    }
}

build.apply {
    dependsOn(jar)
    dependsOn(javadocJar)
    dependsOn(sourcesJar)

    jar.mustRunAfter(clean)
    javadocJar.mustRunAfter(jar)
    sourcesJar.mustRunAfter(javadocJar)
}

publish.apply {
    dependsOn(build)

    onlyIf {
        System.getenv("JFROG_USERNAME") != null && System.getenv("JFROG_TOKEN") != null
    }
}

tasks.withType<Wrapper> {
    gradleVersion = "6.8"
    distributionType = Wrapper.DistributionType.ALL
}
