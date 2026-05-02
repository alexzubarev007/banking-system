package runners;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"runners",
        "controllers",
        "services",
        "repositories",
        "mapping",
        "handlers",
        "securityConfigs"})
@EnableJpaRepositories(basePackages = {"Repositories.Jpa"})
@EntityScan(basePackages = {"Entities"})
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}