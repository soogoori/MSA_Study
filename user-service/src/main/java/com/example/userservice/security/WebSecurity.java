package com.example.userservice.security;

import com.example.userservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class WebSecurity{

    private UserService userService;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private Environment env;

    public WebSecurity(Environment env, UserService userService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userService = userService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.env=env;
    }

    // 권한
    @Bean
    protected SecurityFilterChain config(HttpSecurity http) throws Exception {
        AuthenticationManager authenticationManager = authenticationManager(http.getSharedObject(AuthenticationConfiguration.class));

        http.csrf().disable();
        http.authorizeRequests()
                .antMatchers("/**")
                .hasIpAddress("192.168.219.105")
                //.hasIpAddress("172.20.19.12")
                .and()
                .addFilter(getAuthenticationFilter(authenticationManager)); // 로그인할 때 제일 먼저 시도

        http.headers().frameOptions().disable();
        return http.build();
    }

    /**
     * 인자로 전달받은 유저에 대한 인증 정보를 담고 있으며,
     * 해당 인증 정보가 유효할 경우 UserDetailsService에서
     * 적절한 Principal을 가지고 있는 Authentication 객체 반환
     */

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }
    private AuthenticationFilter getAuthenticationFilter(AuthenticationManager authenticationManager) throws Exception {
        AuthenticationFilter authenticationFilter = new AuthenticationFilter(authenticationManager, userService, env);

        // authenticationFilter.setAuthenticationManager(authenticationManager); // 생성자를 통해서 만들어졌으므로 필요 없음.

        return authenticationFilter;
    }

    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        // 사용자 정보를 다른 저장소에서 가져와 인증
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }
}
