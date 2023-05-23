package org.example.newsstreaming.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

/**
 * @author irfan.nagoo
 */

@Configuration
@ComponentScan(basePackages = "org.springdoc")
@EnableWebMvc
public class OpenApiConfig {
}
