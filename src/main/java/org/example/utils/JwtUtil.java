package org.example.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JwtUtil {
    private static final String KEY = "seeHowMuchILoveYouTT";
    // access toke过期时间
    private static final long ACCESS_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 3;
    // refresh token过期时间七天
    private static final long REFRESH_TOKEN_EXPIRATION = 1000 * 60 * 60 * 24 * 7;
    // 用户ID名称
    private static final String CLAIM_KEY_USER_ID = "userId";
    // 令牌类型声明
    private static final String CLAIM_KEY_TOKEN_TYPE = "tokenType";
    // access token的类型
    private static final String TOKEN_TYPE_ACCESS = "access";
    // refresh toke的类型
    private static final String TOKEN_TYPE_REFRESH = "refresh";

    // 根据用户id生成access token  就像景区门票
    public static String generateAccessToken(Long userID) {
        Map<String,Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userID);
        claims.put(CLAIM_KEY_TOKEN_TYPE,TOKEN_TYPE_ACCESS);
        return genToken(claims, ACCESS_TOKEN_EXPIRATION);
    }

    // 根据用户id生成刷新token   景区门票丢失的时候不需要再拿身份证补  拿这个凭证就可以再申请
    public static String generateRefreshToken(Long userID) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(CLAIM_KEY_USER_ID, userID);
        claims.put(CLAIM_KEY_TOKEN_TYPE, TOKEN_TYPE_REFRESH);
        return genToken(claims, REFRESH_TOKEN_EXPIRATION);
    }

    // 制作令牌的核心方法
    public static String genToken(Map<String, Object> claims, long expiration) {
        return JWT.create()
                .withClaim("claims",claims)
                .withExpiresAt(new Date(System.currentTimeMillis() + expiration))
                .sign(Algorithm.HMAC256(KEY));
    }

    // 接收token 验证 并返回业务数据   解析令牌携带信息
    public static Map<String, Object> parseToken(String token) {
        return JWT.require(Algorithm.HMAC256(KEY))
                .build()
                .verify(token)
                .getClaim("claims")
                .asMap();
    }

    // 快速判断这个令牌是否有效
    public static boolean validateToken(String token) {
        try {
            parseToken(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
