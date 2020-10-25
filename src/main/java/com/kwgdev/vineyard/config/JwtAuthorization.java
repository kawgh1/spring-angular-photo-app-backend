package com.kwgdev.vineyard.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * created by kw on 10/19/2020 @ 1:21 AM
 */
// this class is only called once, so OncePerRequestFilter
public class JwtAuthorization extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        try {

            // Basically if we don't add these headers, our Angular frontend (4200, different domain URL) won't be able to access
            // anything stored on our backed, won't be able to authenticate users or serve them their posts/profiles, etc.

            response.addHeader("Access-Control-Allow-Origin", SecurityConstants.CLIENT_DOMAIN_URL);

            response.addHeader("Access-Control-Allow-Headers", "Origin, Accept, X-Requested-With, "
                    + "Content-Type, Access-Control-Request-Method, " + "Access-Control-Request-Headers, Authorization");

            response.addHeader("Access-Control-Expose-Headers",
                    "Access-Control-Allow-Origin, " + "Access-Control-Allow-Credentials, " + "Authorization");

            response.addHeader("Access-Control-Allow-Methods", "GET," + "POST, " + "DELETE");

            // this FIRST request from our app to this backend is always going to have a HEADER with OPTIONS
            // we want to check every request and if it has OPTIONS in its header
            // then we want to let it through - the OPTIONS will contain all the info listed above
            if ((request.getMethod().equalsIgnoreCase("OPTIONS"))) {
                try {
                    // If the header has OPTIONS, then do nothing, let it through and return 200 OK
                    response.setStatus(HttpServletResponse.SC_OK);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {



                // if the request header doesn't have OPTIONS,
                // then we want to authenticate that request by getting the authorization token
                String jwtToken = request.getHeader(SecurityConstants.HEADER_TYPE);
                // check to make sure token is not null and that token is the right one,
                // starting with our PREFIX "Bearer " in the header
                if (jwtToken == null || !jwtToken.startsWith(SecurityConstants.TOKEN_PREFIX)) {

//                    System.out.println("token = " + jwtToken);
//                    System.out.println("request = " + request.getHeader(SecurityConstants.HEADER_TYPE));
                    // if no token or it's not our token, then we don't care, don't authenticate it and let it do whatever
                    // error out or whatever, it won't come through
                    filterChain.doFilter(request, response);
                    return;
                }

//                System.out.println("got to here");

                // If it IS our header, then we need to authenticate it and take it apart to make sure it's legit

                // Here is what our token looks like in JwtAuthentication.class

            /* String jwtToken = JWT.create()

                    // this is the company/issuer (google.com, facebook.com, etc.)
                    // if local host it will be "localhost:xxxx/login"

                        .withIssuer(request.getRequestURI())

                    // for this user

                        .withSubject(user.getUsername())

                    // pass on the roles for that token

                        .withArrayClaim("roles", roles.toArray(new String[roles.size()]))

                    // when to expire (5 days)

                        .withExpiresAt(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME))

                    // our secret token code

                        .sign(Algorithm.HMAC256(SecurityConstants.SECRET));*/

                // this is the token from the header
                JWT.require(Algorithm.HMAC256(SecurityConstants.SECRET));
                // decode the token - remove "Bearer " prefix with substring token.length()
                DecodedJWT jwt = JWT.decode(jwtToken.substring(SecurityConstants.TOKEN_PREFIX.length()));

                // get username from header token
                String username = jwt.getSubject();

                // get roles for username
                List<String> roles = jwt.getClaims().get("roles").asList(String.class);

                // get Spring Security authorities
                Collection<GrantedAuthority> authorities = new ArrayList<>();

                // for each role in username, add that Spring Security authority for the user
                roles.forEach(role -> authorities.add(new SimpleGrantedAuthority(role)));


                // if we've gotten to this point, we know the user is legitimate
                // token is good, username checks out, roles/authorities check out

                System.out.println("User has been authenticated");


                // password is null here, we don't care about the password at this point
                UsernamePasswordAuthenticationToken authenticatedUser = new UsernamePasswordAuthenticationToken(username,
                        null, authorities);

                // call Spring Security Context and take this user as the authenticated user
                SecurityContextHolder.getContext().setAuthentication(authenticatedUser);

                // and then proceed with whatever the user request is asking for from this backend server/API
                filterChain.doFilter(request, response);

        }

        } catch (Exception e) {
            System.out.println("JwtAuthorization error" );
            e.printStackTrace();
        }
    }
}
