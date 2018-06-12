package site.pushy.websocketdemo;

import io.jsonwebtoken.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 生成存放userId的sessionId(token)的工具类
 * @author Pushy
 */

public class SessionIdUtil {

    private static final String SECRET_KEY = UUID.randomUUID().toString();

    public static String encode(String userId) {
        Map<String, Object> claims = new HashMap<>();
        long nowMillis = System.currentTimeMillis();
        long expirationMillis = nowMillis + 4 * 2419200000L;  // 设置过期时间为四周
        //long expirationMillis = nowMillis + 15000;
        claims.put("userId",userId);
        return Jwts.builder()
                .setSubject("subValue")
                .addClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(expirationMillis))
                .signWith(SignatureAlgorithm.HS256,SECRET_KEY).compact();
    }

    public static String decode(String accessToken) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(accessToken).getBody();
            System.out.println(claims.get("userId"));
            return (String) claims.get("userId");
        } catch (JwtException error) {
            /* 解密失败 */
            System.out.println("解密失败");
            return null;
        }
    }

    public static String decodeWithoutCatchError(String accessToken) {
        Claims claims = Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(accessToken).getBody();
        return (String) claims.get("userId");
    }

}
