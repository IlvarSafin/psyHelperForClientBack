package com.example.helppsy.security;

public class SecurityConstants {
    public static final String SIGN_UP_URLS = "/api/auth/**";

    public static final String SECRET = "SecretKeyGenJWT";
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String CONTENT_TYPE = "application/json";
    public static final long JWT_EXPIRATION_MS = 36000*15;
    public static final long JWT_REFRESH_EXPIRATION_MS = 86400000;

    public static final String REFRESH_COOKIE_NAME = "refToken";

}
