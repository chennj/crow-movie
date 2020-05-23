package org.crow.movie.user.common.util;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;

public final class TokenUtil {

	//过期时间
    private static final long overdeuTime = 30*60*1000;
    //私钥uuid生成，确定唯一性
    private static final String tokenSecRet="6f67b401-910e-11ea-b22b-54e1ad0a1f62";
    
    /**
     * 生成token，用户退出后消失
     * @param userCode
     * @param userId
     * @return
     */
    public static String genToken(String userCode,int userId)
    {
        //设置过期时间
		Date date=new Date(System.currentTimeMillis()+overdeuTime);
		//token私钥加密
		Algorithm algorithm=Algorithm.HMAC256(tokenSecRet);
		//设置头部信息
		Map<String,Object> requestHender=new HashMap<>(2);
		requestHender.put("type","JWT");
		requestHender.put("encryption","HS256");
		long date1=new Date().getTime();
 
		//返回带有用户信息的签名
		return JWT.create().withHeader(requestHender)
		        .withClaim("userCode",userCode)
		        .withClaim("userId",userId)
		        .withClaim("Time",date1)
		        .withExpiresAt(date)
		        .sign(algorithm);
    }
 
    /**
     * 验证token是否正确
     * @param token
     * @return
     */
    public static boolean tokenVerify(String token){
    	
        try {
            Algorithm algorithm=Algorithm.HMAC256(tokenSecRet);
            JWTVerifier verifier=JWT.require(algorithm).build();
            //验证
			@SuppressWarnings("unused")
			DecodedJWT decodedJWT=verifier.verify(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }
    
    /**
     * 获取用户名
     * @param token
     * @return
     */
    public static String getUsername(String token){
    	
    	try{
    		DecodedJWT jwt = JWT.decode(token);
    		return jwt.getClaim("userCode").asString();
    	} catch (JWTDecodeException e){
    		return null;
    	}
    }
 
    /**
     * 获取登陆用户token中的用户ID
     * @param token
     * @return
     */
    public static int getUserID(String token){
    	
        DecodedJWT decodedJWT=JWT.decode(token);
        return decodedJWT.getClaim("userId").asInt();
    }
}
