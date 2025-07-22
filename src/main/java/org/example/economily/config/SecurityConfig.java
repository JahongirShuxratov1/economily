package org.example.economily.config;

import lombok.RequiredArgsConstructor;

import org.example.economily.config.hander.CustomAccessDeniedHandler;
import org.example.economily.config.hander.CustomAuthenticationEntryPoint;
import org.example.economily.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomAuthenticationEntryPoint customAuthEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    //    @Autowired
//    public SecurityConfig(JwtFilter jwtFilter, CustomAuthenticationEntryPoint customAuthEntryPoint, CustomAccessDeniedHandler customAccessDeniedHandler) {
//        this.jwtFilter = jwtFilter;
//        this.customAuthEntryPoint = customAuthEntryPoint;
//        this.customAccessDeniedHandler = customAccessDeniedHandler;
//    }
    public static String[] WHITE_LIST = {"/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-ui.html",
            "api/v1/auth/**"};

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    //                                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
    @Bean
    protected SecurityFilterChain configure(HttpSecurity http) throws Exception {
        return http.csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> {})
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(WHITE_LIST).permitAll()
                        .anyRequest().authenticated()
                )
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(h -> h.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(customAuthEntryPoint)
                        .accessDeniedHandler(customAccessDeniedHandler)
                )
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.addAllowedOriginPattern("*");
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedOrigins(List.of("http://localhost:3001", "https://localhost:3000","https://snowtam-production.up.railway.app"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}

