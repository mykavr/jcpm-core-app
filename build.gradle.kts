plugins {
	java
	id("org.springframework.boot") version "3.1.4"
	id("io.spring.dependency-management") version "1.1.0"
}

group = "com.theroom307.jcpm"
version = "0.1.0"
java {
	sourceCompatibility = JavaVersion.VERSION_21
}

// Define versions for libraries not managed by Spring Boot
val springdocVersion = "2.1.0"
val lombokVersion = "1.18.38"

repositories {
	mavenCentral()
}

dependencies {
	// Spring Boot managed dependencies (versions provided by Spring Boot dependency management)
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	runtimeOnly("com.h2database:h2")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	// Dependency with explicit version management via defined property
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:$springdocVersion")

	// Lombok dependencies with centralized versioning
	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")
	testCompileOnly("org.projectlombok:lombok:$lombokVersion")
	testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks.test {
	useJUnitPlatform()
}
