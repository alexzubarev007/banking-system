plugins { id("org.springframework.boot") }
dependencies {
    implementation(project(":rates-service:application"))
    implementation(project(":rates-service:infrastructure"))
    implementation("org.springframework.boot:spring-boot-starter")
}
