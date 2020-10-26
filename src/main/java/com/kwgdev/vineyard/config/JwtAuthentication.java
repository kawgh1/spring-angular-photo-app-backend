package com.kwgdev.vineyard.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kwgdev.vineyard.model.AppUser;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * created by kw on 10/19/2020 @ 12:45 AM
 */
public class JwtAuthentication extends UsernamePasswordAuthenticationFilter {

    // first have to get an authentication manager
    private AuthenticationManager authenticationManager;

    public JwtAuthentication(AuthenticationManager authenticationManager) {
        this.authenticationManager = authenticationManager;
    }


    @Override
    // this is the method that will be called when Spring Security tries to authenticate a user
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        // first have to get user information from the JSON object that comes in
        // and convert it into an AppUser object to authenticate
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        AppUser appUser;

        try{
            // ObjectMapper is from Jackson, reads JSON from user request to log in and tries to convert to Java Object AppUser
            appUser = objectMapper.readValue(request.getInputStream(), AppUser.class);

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to convert user from JSON to Java Object: " + e);
        }

        // try to authenticate the username and password
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                appUser.getUsername(), appUser.getPassword()
        ));

    }

    // Now if the above method is successful (successfully authenticated) then this method below will be called

    // https://github.com/auth0/java-jwt documentation

    // add com.auth0 -> java-jwt dependency to POM

    @Override
    protected void successfulAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain,
                                            // authentication object from Spring
                                            Authentication authentication) throws IOException, ServletException {

        // extract the user from the authentication object
        User user = (User) authentication.getPrincipal();
        // get their roles and assign to ArrayList
        List<String> roles = new ArrayList<>();
        user.getAuthorities().forEach(authority -> {
            roles.add(authority.getAuthority());
        });

        // give user their jwt token
        String jwtToken = JWT.create()
                // this is the company/issuer (google.com, facebook.com, etc.)
                // if local host it will be front end "localhost:4200/login"
                //.withIssuer(request.getRequestURI())
                .withIssuer("https://spring-angular-photoshar-front.herokuapp.com")
                // for this user
                .withSubject(user.getUsername())
                // pass on the roles for that token
                // .withArrayClaim("roles", roles.toArray(new String[roles.size()]))
                .withArrayClaim("roles", roles.stream().toArray(String[]::new))
                // when to expire (5 days)
                .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))
                .sign(Algorithm.HMAC256(SecurityConstants.SECRET));

        // this means anyone who has our token can be authenticated,
        // no further requirements about where is the token from, did you use this specific tool, etc.
        // as long as you have this token in your header then user is ok to access the app
        response.addHeader(SecurityConstants.HEADER_TYPE, SecurityConstants.TOKEN_PREFIX + jwtToken);
    }

}
