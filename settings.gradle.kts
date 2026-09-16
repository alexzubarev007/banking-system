pluginManagement {
    val springBootVersion: String by settings
    plugins { id("org.springframework.boot") version springBootVersion }
}
rootProject.name = "banking-system"
include("shared")
include("bank-service:domain")
include("bank-service:application-abstractions")
include("bank-service:application-contracts")
include("bank-service:application")
include("bank-service:infrastructure")
include("bank-service:presentation")
include("rates-service:application")
include("rates-service:infrastructure")
include("rates-service:presentation")
