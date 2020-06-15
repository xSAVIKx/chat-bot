/*
 * Copyright 2020, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

plugins {
    application
    id("com.github.johnrengelman.shadow")
    id("com.google.cloud.tools.jib")
    id("io.spine.tools.gradle.bootstrap")
}

val gcpProject: String by project

spine {
    enableJava().server()
}

dependencies {
    annotationProcessor(enforcedPlatform(Build.micronaut.bom))
    annotationProcessor(Build.micronaut.injectJava)
    annotationProcessor(Build.micronaut.validation)

    compileOnly(enforcedPlatform(Build.micronaut.bom))

    implementation(enforcedPlatform(Build.micronaut.bom))
    implementation(Build.micronaut.inject)
    implementation(Build.micronaut.validation)
    implementation(Build.micronaut.runtime)
    implementation(Build.micronaut.netty)
    implementation("javax.annotation:javax.annotation-api")

    implementation("org.apache.logging.log4j:log4j-core:2.13.3")
    runtimeOnly("org.apache.logging.log4j:log4j-api:2.13.3")
    runtimeOnly("org.apache.logging.log4j:log4j-slf4j-impl:2.13.3")

    implementation("io.spine.gcloud:spine-datastore:1.5.0")
    implementation("com.google.cloud:google-cloud-secretmanager:1.0.1")
    implementation("com.google.api.grpc:proto-google-cloud-pubsub-v1:1.89.0")

    implementation("com.google.apis:google-api-services-chat:v1-rev20200502-1.30.9")
    implementation("com.google.auth:google-auth-library-oauth2-http:0.20.0")
    testAnnotationProcessor(enforcedPlatform(Build.micronaut.bom))
    testAnnotationProcessor(Build.micronaut.injectJava)
    testImplementation("io.spine:spine-testutil-server:${spine.version()}")
    testImplementation(enforcedPlatform(Build.micronaut.bom))
    testImplementation(Build.micronaut.testJUnit5)
    testImplementation(Build.micronaut.httpClient)
}

application {
    mainClassName = "io.spine.chatbot.Application"
}

jib {
    to {
        image = "gcr.io/${gcpProject}/chat-bot-server"
    }
    container {
        mainClass = application.mainClassName
    }
}
