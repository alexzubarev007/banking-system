dependencies {
    api(project(":bank-service:application-abstractions"))
    api(project(":bank-service:application-contracts"))
    implementation("org.springframework:spring-context")
    implementation("org.springframework:spring-tx")
    implementation("org.springframework.security:spring-security-core")
    implementation("org.springframework.security:spring-security-crypto")
}
