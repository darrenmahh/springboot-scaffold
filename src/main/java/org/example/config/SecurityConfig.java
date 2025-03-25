package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())  // 在开发阶段可以禁用CSRF
                .authorizeHttpRequests(auth -> auth
                        // .requestMatchers("/api/user/register").permitAll()  // 允许注册接口无需认证
                        // .requestMatchers("/api/public/**").permitAll()      // 其他公开接口
                        // .anyRequest().authenticated()                       // 其他所有请求需要认证
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}