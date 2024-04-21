package it.unipi.dsmt.dsmt_taboo;

import it.unipi.dsmt.dsmt_taboo.utility.FilterManagement;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DsmtTabooApplication
{
    public static void main(String[] args) {
        SpringApplication.run(DsmtTabooApplication.class, args);
    }
    @Bean
    public FilterRegistrationBean corsFilterRegistration() {
        FilterRegistrationBean registrationBean =
                new FilterRegistrationBean(new FilterManagement());
        registrationBean.setName("CORS Filter");
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        return registrationBean;
    }

    /**
     * This class represents the main application class for the DSMT Taboo-Game Web Application.
     * It is annotated with @SpringBootApplication to enable Spring Boot autoconfiguration.
     * The main method initializes and runs the Spring application.
     * Additionally, it includes a method corsFilterRegistration() annotated with @Bean,
     * which registers the FilterManagement class as a CORS filter for handling cross-origin requests.
     * The filter registration is configured to apply to all URL patterns and has an order of 1.
     */
}

