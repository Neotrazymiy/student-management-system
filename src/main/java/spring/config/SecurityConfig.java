package spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import lombok.RequiredArgsConstructor;
import spring.auxiliaryObjects.CustomAuthFailureHandler;
import spring.auxiliaryObjects.CustomAuthSuccessHandler;
import spring.auxiliaryObjects.OAuthUserServiceHelper;
import spring.auxiliaryObjects.RateLimitFilter;
import spring.service.CastomUserDetailsService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	private final CastomUserDetailsService detailsService;
	private final OAuthUserServiceHelper helper;
	private final CustomAuthFailureHandler customAuthFailureHandler;
	private final CustomAuthSuccessHandler customAuthSuccessHandler;
	private final RateLimitFilter rateLimitFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		return new SessionRegistryImpl();
	}

	@Bean
	public HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(detailsService).passwordEncoder(passwordEncoder());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.sessionManagement()
				.sessionFixation().migrateSession()
				.maximumSessions(1)
					.maxSessionsPreventsLogin(false)
					.expiredUrl("/login?expired=true")
					.sessionRegistry(sessionRegistry())
				.and()
			.and()
			.authorizeRequests()
				.antMatchers("/webjars/**", "/login*", "/css/**", "/js/**").permitAll().antMatchers("/admin/**")
				.hasRole("ADMIN").antMatchers("/quest/registration", "/quest/registration/edit").permitAll()
				.antMatchers("/quest/**").hasRole("QUEST").antMatchers("/student/**").hasRole("STUDENT")
				.antMatchers("/teacher/**").hasRole("TEACHER").antMatchers("/methodist/**").hasRole("METHODIST")
				.anyRequest().authenticated()
			.and()
			.addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
			.logout(logaut -> logaut.logoutUrl("/logout").logoutSuccessUrl("/login?logout")
				.invalidateHttpSession(true).clearAuthentication(true))
			.formLogin(login -> login.loginPage("/login").loginProcessingUrl("/perform_login")
				.usernameParameter("username").passwordParameter("password").defaultSuccessUrl("/home", true)
				.failureHandler(customAuthFailureHandler).successHandler(customAuthSuccessHandler))
			.oauth2Login(config -> config.loginPage("/login").defaultSuccessUrl("/home", true)
				.failureUrl("/login?error=true")
				.userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService())));
	}

	private OAuth2UserService<OidcUserRequest, OidcUser> oidcUserService() {
		return userRequest -> {
			String email = userRequest.getIdToken().getClaim("email");
			UserDetails userDetails;
			try {
				userDetails = detailsService.loadUserByEmail(email);
			} catch (UsernameNotFoundException e) {
				helper.createUserFromGoogle(email, userRequest.getIdToken().getClaim("given_name"),
						userRequest.getIdToken().getClaim("family_name"));
				userDetails = detailsService.loadUserByEmail(email);
			}
			return new DefaultOidcUser(userDetails.getAuthorities(), userRequest.getIdToken(), "email");
		};
	}
}
