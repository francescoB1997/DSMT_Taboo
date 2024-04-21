package it.unipi.dsmt.dsmt_taboo.utility;

import it.unipi.dsmt.dsmt_taboo.DsmtTabooApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ServletInitializer extends SpringBootServletInitializer
{
    /**
     * This class represents a servlet initializer for configuring the Spring Boot application
     * to be deployed as a WAR (Web Application Archive) file in a servlet container.
     * It extends the SpringBootServletInitializer class.
     * The configure method overrides the method in the superclass to specify the application sources.
     * It returns a SpringApplicationBuilder configured with the main application class of DsmtTabooApplication.
     */

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(DsmtTabooApplication.class);
    }
}