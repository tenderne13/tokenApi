package com.jwt.utils;

import com.auth0.jwt.JWTSigner;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.internal.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Jwt {
    private static final String SECRET = "XX#$%()(#*!()!KL<><MQLMNQNQJQK sdfkjsdrow32234545fdf>?N<:{LWPW";

    private static final String EXP = "exp";

    private static final String PAYLOAD = "payload";

    //sign
    public static <T> String sign(T object,long maxAge){
        try {
            JWTSigner signer = new JWTSigner(SECRET);
            Map<String,Object> claims = new HashMap<String, Object>();
            ObjectMapper mapper = new ObjectMapper();
            String jsonString = mapper.writeValueAsString(object);
            claims.put(PAYLOAD,jsonString);
            claims.put(EXP,System.currentTimeMillis()+maxAge);
            return signer.sign(claims);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    //unsign
    public static <T> T unsign(String jwt,Class<T> classT){
        final JWTVerifier verifier = new JWTVerifier(SECRET);

        try {
            final Map<String,Object> claims = verifier.verify(jwt);
            if(claims.containsKey(EXP)&&claims.containsKey(PAYLOAD)){
                long exp = (Long) claims.get(EXP);
                long currentMils=System.currentTimeMillis();
                if(exp>currentMils){
                    String json =(String) claims.get(PAYLOAD);
                    ObjectMapper mapper =new ObjectMapper();
                    return mapper.readValue(json,classT);
                }
            }
            return null;
        } catch (Exception e) {
            return null;
        }

    }

}
