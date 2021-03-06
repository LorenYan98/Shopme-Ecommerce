package com.shopme.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
//	@Bean
//	public UserDetailsService UserDetailsService() {
//		return new ShopmeUserDetailsService();
//	}
//	
//	@Bean
//	public PasswordEncoder PasswordEncoder() {
//		return new BCryptPasswordEncoder();
//	}
//	
//	public DaoAuthenticationProvider authenticationProvider() {
//		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
//		
//		authProvider.setUserDetailsService(UserDetailsService());
//		authProvider.setPasswordEncoder(PasswordEncoder());
//		return authProvider;
//	}
//	
//	
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//		auth.authenticationProvider(authenticationProvider());
//	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests().anyRequest().permitAll();
//		.antMatchers("/users/**").hasAuthority("Admin")
//		.antMatchers("/categories/**").hasAnyAuthority("Admin","Editor")
//		.antMatchers("/brands/**").hasAnyAuthority("Admin","Editor")
//		.antMatchers("/products","/products/","/products/detail/**","/products/page/**")
//			.hasAnyAuthority("Admin","Editor","Salesperson", "Shipper")
//		.antMatchers("/products/new","/product/delete/**")
//			.hasAnyAuthority("Admin","Editor")
//		.antMatchers("/products/edit/**","/products/save","/products/check_unique")
//			.hasAnyAuthority("Admin","Editor","Salesperson")
//		.antMatchers("/products/**").hasAnyAuthority("Admin","Editor")
//		.anyRequest().authenticated()
//		.and()
//		.formLogin()
//			.loginPage("/login")
//			.usernameParameter("email")
//			.permitAll()
//		.and().logout().permitAll()
//		.and().rememberMe();
		}

	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring().antMatchers("/images/**", "/js/**", "/webjars/**", "/static/**","/css/**");
	}

}
