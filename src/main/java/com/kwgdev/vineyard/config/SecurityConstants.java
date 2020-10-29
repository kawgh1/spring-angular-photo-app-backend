package com.kwgdev.vineyard.config;

/**
 * created by kw on 10/19/2020 @ 1:12 AM
 */
public class SecurityConstants {

    // this is the secret code that is embedded in every token, this needs to be HIDDEN from deployment
    public static final String SECRET = "***************";

    public static final long EXPIRATION_TIME = 432_000_000; // 5 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_TYPE = "Authorization";

    // myWebApp.com  , all other URLS will be denied
    // "http://localhost:4200/*" for dev
    public static final String CLIENT_DOMAIN_URL = "https://spring-angular-photoshar-front.herokuapp.com";
//    public static final String CLIENT_DOMAIN_URL = "*";

}
