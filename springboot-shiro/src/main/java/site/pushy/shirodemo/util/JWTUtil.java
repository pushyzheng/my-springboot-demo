package site.pushy.shirodemo.util;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 生成存放userId的sessionId(token)的工具类
 * @author Pushy
 */

public class JWTUtil {

    private static final String SECRET_KEY = "JWT ";

    /**
     * 默认加密token
     * @param userId
     * @return
     */
    public static String encode(String userId) {
        Integer ept = 10080;  // 一周
        return JWTUtil.encode(userId, ept);
    }

    /**
     * 加密token
     * @param userId
     * @param exceptionTime 过期时间，分钟级别
     * @return
     */
    public static String encode(String userId, Integer exceptionTime) {
        Map<String, Object> claims = new HashMap<>();
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = nowMillis + exceptionTime * 60000L;
        claims.put("userId", userId);
        return Jwts.builder()
                .setSubject("subValue")
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationMillis))
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY).compact();
    }

    public static String decode(String accessToken) throws JwtException{
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(accessToken).getBody();
        return (String) claims.get("userId");
    }

}
