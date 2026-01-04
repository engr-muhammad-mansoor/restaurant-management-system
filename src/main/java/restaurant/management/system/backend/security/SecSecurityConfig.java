package restaurant.management.system.backend.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecSecurityConfig {

    private static final String[] WHITE_LIST_URL = {"/login", "/v2/api-docs", "/oauth/**", "/**", "/web/payment/**", "/v3/api-docs", "/v3/api-docs/**", "/swagger-resources", "/swagger-resources/**", "/configuration/ui", "/configuration/security", "/swagger-ui/**", "/webjars/**", "/swagger-ui.html"};
    @Autowired
    private JwtAuthenticationEntryPoint point; // Ensure this is used correctly
    @Autowired
    private JwtAuthenticationFilter filter; // Ensure this is used correctly
    @Autowired
    private OAuthAuthenticationSuccessHandler handler; // Ensure this is used if needed

    @Value("${frontend.server.url}")
    private String frontendServerUrl;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable).csrf(AbstractHttpConfigurer::disable).authorizeHttpRequests(auth -> auth.requestMatchers(WHITE_LIST_URL).permitAll() // Allow access to whitelisted URLs
                        .anyRequest().hasAuthority("ROLE_OWNER") // Ensure only users with the ROLE_OWNER can access
                ).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS) // Use stateless for JWT
                ).exceptionHandling(exceptions -> exceptions.authenticationEntryPoint(point) // Use the correct entry point variable
                )
//                .authenticationProvider(daoAuthenticationProvider()) // Use the provider bean
                .addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class) // Use the filter variable
                // Form Login configuration
                .formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login").defaultSuccessUrl("/home/getuser", true).failureUrl("/login?error=true").usernameParameter("username").passwordParameter("password")).logout(logout -> logout.logoutUrl("/api/v1/auth/logout") // Ensure the logout URL is consistent with your app's routing
                        .logoutSuccessHandler((request, response, authentication) -> SecurityContextHolder.clearContext())).oauth2Login(oauth -> {
                    oauth.loginPage("/login");
//                    oauth.loginPage(frontendServerUrl + "/login");
                    oauth.successHandler(handler); // Ensure handler is implemented and working correctly
                });
        return http.build();
    }
}
