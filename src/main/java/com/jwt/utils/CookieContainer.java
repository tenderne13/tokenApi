package com.jwt.utils;

import org.apache.http.client.CookieStore;

import java.util.HashMap;
import java.util.Map;

public class CookieContainer {
    private static Map<String,CookieStore> cookieMap=new HashMap<String, CookieStore>(30);

    public static CookieStore getCookieStore(String key) {
        return cookieMap.get(key);
    }

    public static void setCookieStore(String uuid,CookieStore cookieStore) {
        cookieMap.put(uuid,cookieStore);
    }

    public static void removeCookieStore(String uuid){
        cookieMap.remove(uuid);
    }
}
