package com.marcwoodyard.RaspberryPiThermostat.web.security;

import com.marcwoodyard.RaspberryPiThermostat.utils.ProgramSettings;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        auth
                .inMemoryAuthentication()
                .withUser(ProgramSettings.getWebUsername())
                .password(encoder.encode(ProgramSettings.getWebPassword()))
                .roles("USER", "ADMIN");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                //.antMatchers("/", "/assets/*").permitAll()
                // TODO: Just for dev, remove when done
                .antMatchers("/*", "/assets/*").permitAll()
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login").failureUrl("/login?error").permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout")).logoutSuccessUrl("/login")
                .and()
                .requiresChannel()
                .anyRequest()
                .requiresSecure();

        // TODO: Is this still needed???

        // Error: java.lang.IllegalStateException: Cannot create a session after the response has been committed
        // Link: https://stackoverflow.com/questions/52982246/spring-thymeleaf-throws-cannot-create-a-session-after-the-response-has-been-com
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS);
    }

}
