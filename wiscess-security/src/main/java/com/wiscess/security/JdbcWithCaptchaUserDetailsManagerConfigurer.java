package com.wiscess.security;

import org.springframework.security.config.annotation.authentication.ProviderManagerBuilder;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configurers.provisioning.JdbcUserDetailsManagerConfigurer;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.wiscess.security.web.CaptchaDaoAuthenticationProvider;

public class JdbcWithCaptchaUserDetailsManagerConfigurer<B extends ProviderManagerBuilder<B>>
		extends JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> {

	CaptchaDaoAuthenticationProvider authProvider = new CaptchaDaoAuthenticationProvider();
	
	public JdbcUserDetailsManagerConfigurer<AuthenticationManagerBuilder> passwordEncoder(PasswordEncoder passwordEncoder) {
		authProvider.setPasswordEncoder(passwordEncoder);
		
		return this;
	}
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
        authProvider.setUserDetailsService(this.getUserDetailsService());
        authProvider = postProcess(authProvider);
		auth.authenticationProvider(authProvider);
	}

}
