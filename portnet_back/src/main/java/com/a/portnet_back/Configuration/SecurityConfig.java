package com.a.portnet_back.Configuration;

import com.a.portnet_back.Services.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.beans.factory.annotation.Autowired;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    @Autowired
    private JwtAuthFilter jwtAuthFilter;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private CorsConfigurationSource corsConfigurationSource;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/refresh",
                                "/api/agents/activation",
                                "/api/auth/activation"
                        ).permitAll()


                        .requestMatchers("/api/importateur/register").permitAll()


                        .requestMatchers("/api/superviseur/**").hasAuthority("ROLE_SUPERVISEUR")


                        .requestMatchers("POST", "/api/agents").hasAuthority("ROLE_SUPERVISEUR")
                        .requestMatchers("GET", "/api/agents").hasAnyAuthority("ROLE_SUPERVISEUR", "ROLE_AGENT")
                        .requestMatchers("/api/agents/**").hasAnyAuthority("ROLE_SUPERVISEUR", "ROLE_AGENT")


                        .requestMatchers("/api/agent/**").hasAuthority("ROLE_AGENT")


                        .requestMatchers("/api/importateur/**").hasAuthority("ROLE_IMPORTATEUR")

                        .requestMatchers("GET", "/api/importateur/all").hasAuthority("ROLE_SUPERVISEUR")
                        .requestMatchers("GET", "/api/importateur/{id}").hasAuthority("ROLE_SUPERVISEUR")

                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
}