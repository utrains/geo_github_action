//package com.spring.bioMedical.config;
//
//import javax.sql.DataSource;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.builders.WebSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.provisioning.JdbcUserDetailsManager;
//import org.springframework.security.provisioning.UserDetailsManager;
//
///**
// *
// * @author Soumyadip Chowdhury
// * @github soumyadip007
// *
// */
//@Configuration
//@EnableWebSecurity
//public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//
//	@Autowired
//	private DataSource securityDataSource;
//
//	@Autowired
//	private CustomAuthenticationSuccessHandler successHandler;
//
//
//	@Override
//	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
//
//		// use jdbc authentication ... oh yeah!!!
//		  auth.jdbcAuthentication().dataSource(securityDataSource)
//		  .usersByUsernameQuery(
//		   "select username,password,enabled from user where username=?")
//		  .authoritiesByUsernameQuery(
//		   "select username, authority from user where username=?")
//		  .passwordEncoder(passwordEncoder()) ;
//		 }
//
//	@Bean
//	public PasswordEncoder passwordEncoder(){
//	    return new PasswordEnconderTest();
//	}
//
//
//
//
//	@Override
//	protected void configure(HttpSecurity http) throws Exception {
//
//		http.authorizeRequests()
//			.antMatchers("/admin/**").hasRole("ADMIN")
//			.antMatchers("/user/**").hasRole("USER")
//			.antMatchers("/register").permitAll()
//			.antMatchers("/confirm").permitAll()
//			.antMatchers("/login/**").permitAll()
//			.antMatchers("/css/**").permitAll()
//			.antMatchers("/js/**").permitAll()
//			.antMatchers("/static/**").permitAll()
//			.antMatchers("/vendor/**").permitAll()
//			.antMatchers("/resources/**").permitAll()
//			.anyRequest().authenticated()
//			.and()
//			.formLogin()
//			.loginPage("/showMyLoginPage")
//			.loginProcessingUrl("/authenticateTheUser")
//			//.defaultSuccessUrl("/register")
//			.permitAll()
//			.successHandler(successHandler)
//		.and()
//		.logout().permitAll()
//		.and()
//		.exceptionHandling().accessDeniedPage("/register");
//
//	}
//
//
//
//	@Override
//	public void configure(WebSecurity web) throws Exception {
//
//		web.ignoring().antMatchers("/resources/**","/login/**","/static/**","/Script/**","/Style/**","/Icon/**",
//				"/js/**","/vendor/**","/bootstrap/**","/Image/**");
//
//		//logoutSuccessUrl("/customLogout")
//	}
//
//
//	@Bean
//	public UserDetailsManager userDetailsManager() {
//
//		JdbcUserDetailsManager jdbcUserDetailsManager = new JdbcUserDetailsManager();
//
//		jdbcUserDetailsManager.setDataSource(securityDataSource);
//
//		return jdbcUserDetailsManager;
//	}
//
//
//
//}
//
//class PasswordEnconderTest implements PasswordEncoder {
//    @Override
//    public String encode(CharSequence charSequence) {
//        return charSequence.toString();
//    }
//
//    @Override
//    public boolean matches(CharSequence charSequence, String s) {
//        return charSequence.toString().equals(s);
//    }
//}


package com.spring.bioMedical.config;

import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

/**
 *
 * @author Soumyadip Chowdhury
 * @github soumyadip007
 *
 */
@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

	private final DataSource securityDataSource;
	private final AuthenticationSuccessHandler successHandler;

	public SecurityConfiguration(DataSource securityDataSource,
								 AuthenticationSuccessHandler successHandler) {
		this.securityDataSource = securityDataSource;
		this.successHandler = successHandler;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEnconderTest();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(auth -> auth
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/user/**").hasRole("USER")
						.requestMatchers("/register", "/confirm", "/login/**",
								"/css/**", "/js/**", "/static/**",
								"/vendor/**", "/resources/**").permitAll()
						.anyRequest().authenticated()
				)
				.formLogin(form -> form
						.loginPage("/showMyLoginPage")
						.loginProcessingUrl("/authenticateTheUser")
						.successHandler(successHandler)
						.permitAll()
				)
				.logout(logout -> logout
						.permitAll()
				)
				.exceptionHandling(exception -> exception
						.accessDeniedPage("/register")
				);

		return http.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return web -> web.ignoring().requestMatchers(
				"/resources/**", "/login/**", "/static/**", "/Script/**",
				"/Style/**", "/Icon/**", "/js/**", "/vendor/**",
				"/bootstrap/**", "/Image/**"
		);
	}

	@Bean
	public UserDetailsService userDetailsService() {
		JdbcUserDetailsManager userDetailsManager = new JdbcUserDetailsManager();
		userDetailsManager.setDataSource(securityDataSource);
		userDetailsManager.setUsersByUsernameQuery(
				"select username, password, enabled from user where username=?"
		);
		userDetailsManager.setAuthoritiesByUsernameQuery(
				"select username, authority from user where username=?"
		);
		return userDetailsManager;
	}

	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
		authProvider.setUserDetailsService(userDetailsService());
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}
}

class PasswordEnconderTest implements PasswordEncoder {
	@Override
	public String encode(CharSequence charSequence) {
		return charSequence.toString();
	}

	@Override
	public boolean matches(CharSequence charSequence, String s) {
		return charSequence.toString().equals(s);
	}
}