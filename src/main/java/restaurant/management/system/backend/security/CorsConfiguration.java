package restaurant.management.system.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration CORS
 */
@Configuration
public class CorsConfiguration implements WebMvcConfigurer {

    @Value("${frontend.server.url}")
    private String frontendServerUrl;

    /**
     * Enables CORS for all endpoints
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowCredentials(true).allowedOriginPatterns("http://localhost:[*]", frontendServerUrl).allowedMethods("*").allowedHeaders("*");
    }
}
