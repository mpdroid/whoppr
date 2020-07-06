package com.whoppr.registry;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

  //https://stackoverflow.com/questions/50971891/how-to-secure-spring-cloud-eureka-service-with-basic-auth
  @Bean
  public BCryptPasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth.inMemoryAuthentication()
        .withUser("joshua")
        .password(passwordEncoder().encode("joshua"))
        .authorities("ADMIN", "ALL");
    // Appears that ADMIN role is needed to allow access to dashboard and register services
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http.csrf().disable() //
        .authorizeRequests() //
        .antMatchers(HttpMethod.GET, "/eureka/**").authenticated() // eureka client
        .antMatchers(HttpMethod.POST, "/eureka/**").authenticated() // eureka client
        .antMatchers(HttpMethod.DELETE, "/eureka/**").authenticated() // eureka client
        .anyRequest().authenticated()
        .and().httpBasic(); // dashboard authorization    super.configure(http);
  }

//  @Bean
//  CorsConfigurationSource corsConfigurationSource() {
//    CorsConfiguration configuration = new CorsConfiguration();
//    configuration.setAllowedOrigins(Arrays.asList("*"));
//    configuration.setAllowedMethods(Arrays.asList("*"));
//    configuration.setAllowedHeaders(Arrays.asList("*"));
//    configuration.setAllowCredentials(true);
//    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//    source.registerCorsConfiguration("/**", configuration);
//    return source;
//  }

}
