package com.kwgdev.vineyard.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * created by kw on 10/19/2020 @ 12:06 AM
 */

// brings in Spring Security to the entire app

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // once we have security enabled we need to bring in the user details service because we need to pass
    // those to the authentication manager builder so that we can tell Spring what to use when it is
    // trying to authenticate our users

    // define array of URLs that users can access without being authenticated
    private static final String[] PUBLIC_MATCHERS = {"user/login", "/user/register", "/user/resetPassword/**", "/image/**"};
//    private static final String[] PUBLIC_MATCHERS = {"/**"};

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    protected void configure(AuthenticationManagerBuilder auth) throws Exception {

        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        JwtAuthentication jwtAuthentication = new JwtAuthentication(authenticationManager());
        // by default Spring Security is listening for the "/login" for authentication but we need "/user/login",
        // which won't work, so here we set the authentication with "/user/login" from PUBLIC_MATCHERS
        // to override the default Spring Context because it doesn't work well with our overall application design
        // and configuration
        jwtAuthentication.setFilterProcessesUrl(PUBLIC_MATCHERS[0]);

        // disable CrossSite Request Forgeries (csrf) and Cross Origin Resource Sharing (cors)
        http.csrf().disable().cors().disable()
                // we have to tell Spring how to manage our session because we're not going to keep track
                // of the user the whole time they're authenticated.
                // once they're authenticated we will only check every time they access the application so
                // we can make sure they have a valid token.
                // So we're not going to be keeping track of them the entire time they're logged in and
                // they have a session in the application because our session will be STATELESS (angular).
                // They will log in, we'll give them a token (stored in the browser) and any time they need
                // to access the application we ask for that token.
                // Once we get that token we validate it and get the username information and access rights
                // and give them access appropriate to their access role.

                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                // allow access to any URLs in PUBLIC_MATCHERS
                .authorizeRequests().antMatchers(PUBLIC_MATCHERS).permitAll()
                // anything NOT in PUBLIC_MATCHERS, we want to authenticate the user
                .anyRequest().authenticated()
                .and()
                .addFilter(jwtAuthentication)
                .addFilterBefore(new JwtAuthorization(), UsernamePasswordAuthenticationFilter.class);

    }


}
