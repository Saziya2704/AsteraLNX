package com.newasteralnx.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final RestAuthenticationEntryPoint authenticationEntryPoint;
    private final RestAccessDeniedHandler accessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(Customizer.withDefaults())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(authenticationEntryPoint)
                        .accessDeniedHandler(accessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/forgot-password",
                                "/api/auth/reset-password"
                        ).permitAll()
                        .requestMatchers("/", "/index.html", "/app.html", "/css/**", "/js/**", "/img/**", "/favicon.ico").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                        /* Admin-only */
                        .requestMatchers("/api/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/reports/**").hasRole("ADMIN")

                        /* Customers: WH read; ADMIN/OPS full */
                        .requestMatchers(HttpMethod.GET, "/api/customers/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/customers/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")

                        /* Inquiry / Quote: WH read; ADMIN/OPS full */
                        .requestMatchers(HttpMethod.GET, "/api/inquiries/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/inquiries/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/quotes/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/quotes/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")

                        /* Orders: WH read; ADMIN/OPS full */
                        .requestMatchers(HttpMethod.GET, "/api/orders/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/orders/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")

                        /* Bookings / invoices / tracking / vehicles — ops + admin */
                        .requestMatchers("/api/bookings/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")
                        .requestMatchers("/api/invoices/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")
                        .requestMatchers("/api/tracking/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")
                        .requestMatchers("/api/vehicles/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")
                        .requestMatchers("/api/drivers/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")

                        /* Warehouse domain: OPS read; ADMIN + WH full */
                        .requestMatchers(HttpMethod.GET, "/api/warehouses/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/warehouses/**")
                        .hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/commodities/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/commodities/**")
                        .hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/receipts/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/receipts/**")
                        .hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")
                        .requestMatchers(HttpMethod.GET, "/api/stock-transfers/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER", "WAREHOUSE_MANAGER")
                        .requestMatchers("/api/stock-transfers/**")
                        .hasAnyRole("ADMIN", "WAREHOUSE_MANAGER")

                        /* Shipments / trips: DRIVER own endpoints; others ops */
                        .requestMatchers(HttpMethod.GET, "/api/shipments/mine").hasRole("DRIVER")
                        .requestMatchers(HttpMethod.GET, "/api/trips/mine").hasRole("DRIVER")
                        .requestMatchers("/api/shipments/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")
                        .requestMatchers("/api/trips/**")
                        .hasAnyRole("ADMIN", "OPERATIONS_MANAGER")

                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
