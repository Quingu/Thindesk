package com.pfc.thindesk.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder codificadorSenha() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public SecurityFilterChain filtroSeguranca(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(autorizacao -> autorizacao
                .requestMatchers("/", "/login", "/cadastro", "/acesso-negado",
                        "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error", "/error/**").permitAll()
                .requestMatchers("/administrador/**", "/api/**").hasRole("ADMINISTRADOR")
                .requestMatchers("/gerente/**", "/ajustes-horarios/**").hasAnyRole("GERENTE", "ADMINISTRADOR")
                .requestMatchers("/dashboard", "/perfil", "/chamados/**", "/clientes/**")
                        .hasAnyRole("USUARIO", "GERENTE", "ADMINISTRADOR")
                .anyRequest().authenticated()
            )
            .formLogin(formulario -> formulario
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("username")
                .passwordParameter("password")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?erro")
                .permitAll()
            )
            .logout(saida -> saida
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("SESSION", "JSESSIONID")
                .permitAll()
            )
            .csrf(csrf -> csrf.ignoringRequestMatchers("/api/**"))
            .exceptionHandling(excecao -> excecao.accessDeniedPage("/acesso-negado"));

        return http.build();
    }
}
