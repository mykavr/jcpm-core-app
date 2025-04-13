plugins {
	java
	id("org.springframework.boot") version "3.4.4"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.theroom307.jcpm"
version = "0.1.0"
java {
	sourceCompatibility = JavaVersion.VERSION_21
}

// Define versions for libraries not managed by Spring Boot
val springdocVersion = "2.8.6"
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

	// Lombok dependencies
	compileOnly("org.projectlombok:lombok:$lombokVersion")
	annotationProcessor("org.projectlombok:lombok:$lombokVersion")
	testCompileOnly("org.projectlombok:lombok:$lombokVersion")
	testAnnotationProcessor("org.projectlombok:lombok:$lombokVersion")
}

tasks {
	register<Test>("unitTest") {
		description = "Runs unit tests"
		group = "verification"

		useJUnitPlatform {
			includeTags("unit") // matches TestTypes.UNIT_TEST
		}
	}

	register<Test>("integrationTest") {
		description = "Runs integration tests"
		group = "verification"

		useJUnitPlatform {
			includeTags("integration") // matches TestTypes.INTEGRATION_TEST
		}
	}

	test {
		description = "Runs all tests"
		dependsOn(":unitTest", ":integrationTest")

		// Make sure the main test task doesn't run tests directly
		// It will rely on the specialized tasks instead
		enabled = false
	}
}
