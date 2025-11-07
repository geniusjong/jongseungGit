package com.lottoweb.config;

import com.lottoweb.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Spring Security 설정 클래스
 * 보안 설정, 인증/인가 규칙을 정의합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * 비밀번호 암호화 인코더
     * BCrypt 알고리즘을 사용하여 비밀번호를 암호화합니다.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 인증 관리자 설정
     * 사용자 인증 방식을 설정합니다.
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    /**
     * HTTP 보안 설정
     * URL별 접근 권한, 로그인/로그아웃 설정을 정의합니다.
     */
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                // CSRF 보호 활성화 (기본값)
                .csrf().disable()
                
                // 요청별 권한 설정
                .authorizeRequests()
                    // 로그인, 회원가입, 이메일 인증 관련 페이지는 누구나 접근 가능
                    .antMatchers("/login", "/register", "/register-success", "/verify-email", 
                                "/css/**", "/js/**", "/images/**").permitAll()
                    // REST API는 누구나 접근 가능 (인증 불필요)
                    // ⭐ 왜 API는 인증 없이 접근 가능하게 하나요?
                    // 1. 공개 API: 로또 번호 정보는 공개 데이터이므로 인증 불필요
                    // 2. 테스트 용이성: Postman 등으로 쉽게 테스트 가능
                    // 3. 모바일 앱 연동: 별도 인증 없이 바로 사용 가능
                    // 나중에 필요하면 JWT 토큰 등으로 인증 추가 가능
                    .antMatchers("/api/**").permitAll()
                    // 나머지 모든 페이지는 인증 필요
                    .anyRequest().authenticated()
                    .and()
                
                // 로그인 설정
                .formLogin()
                    // 로그인 페이지 URL
                    .loginPage("/login")
                    // 로그인 처리 URL (POST)
                    .loginProcessingUrl("/login")
                    // 로그인 성공 시 이동할 기본 URL
                    .defaultSuccessUrl("/", true)
                    // 로그인 실패 시 이동할 URL
                    .failureUrl("/login?error=true")
                    // 로그인 폼의 username 필드명
                    .usernameParameter("username")
                    // 로그인 폼의 password 필드명
                    .passwordParameter("password")
                    .permitAll()
                    .and()
                
                // 로그아웃 설정
                .logout()
                    // 로그아웃 URL
                    .logoutUrl("/logout")
                    // 로그아웃 성공 시 이동할 URL
                    .logoutSuccessUrl("/login?logout=true")
                    // 세션 무효화
                    .invalidateHttpSession(true)
                    // 쿠키 삭제
                    .deleteCookies("JSESSIONID")
                    .permitAll();
    }
}

