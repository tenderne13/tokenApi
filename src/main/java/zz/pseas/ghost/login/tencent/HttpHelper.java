/** 
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * 
 */
package zz.pseas.ghost.login.tencent;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class HttpHelper {
    //private static CookieContainer cookieContainer = new CookieContainer();
    private static String referer = "http://ui.ptlogin2.qq.com/cgi-bin/login?target=self&style=5&mibao_css=m_webqq&appid=1003903&enable_qlogin=0&no_verifyimg=1&s_url=http%3A%2F%2Fweb.qq.com%2Floginproxy.html&f_url=loginerroralert&strong_login=1&login_state=10&t=20120504001";
    private static String userAgent = "Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; .NET CLR 1.1.4322; .NET4.0C; .NET4.0E)";
    private static String accept = "*/*";
    private static String contentType = "application/x-www-form-urlencoded; charset=UTF-8";
    private Map<String, String> cookies;
    private String url;
    private String cookie;
    private String html;
    private String [] param;
    public void setURL(String url) {
        this.url = url;
    }
    
    public void setCookie(Map<String, String> m) {
        StringBuffer sb = new StringBuffer();
        for(Entry<String, String> entry : m.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(entry.getValue());
            sb.append(";");
        }
        cookie = sb.toString();
    }
    
    public void setReferer(String referer) {
        this.referer = referer;
    }
    
    public void setParam(String [] param) {
        this.param = param;
    }
    
    private String setParamToUrl(String url,String [] param) {
        StringBuffer s = new StringBuffer();
        if( param != null) {
            
            s.append(url);
            for(int i = 0; i < param.length - 1; i++) {
                s.append(param[i]);
                s.append("&");
            
            }
            s.append(param[param.length - 1]);
            this.url =  s.toString();
        }
        return s.toString();
    }
    
    public void getResponse() {
        
        
        
        
        try {
            URL u = new URL(this.url);
            URLConnection con = u.openConnection();
            
            HttpURLConnection http = (HttpURLConnection) con;
            http.setInstanceFollowRedirects(true);
            
            if(cookie != null)
                http.setRequestProperty("Cookie", cookie);
            if(referer != null) 
                http.setRequestProperty("Referer", referer);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuffer sb = new StringBuffer();
            while(input.ready()) {
                String line = input.readLine();
                sb.append(line + "\n");
            }
            
            
            Map<String, List<String>> m = http.getHeaderFields();
            
            Map<String, String> map = new HashMap<String, String>();
            
            for(String s : m.get("Set-Cookie")) {
                
                s = s.substring(0, s.indexOf(";"));
                
                int index = s.indexOf("=");
                String name = s.substring(0, index);
                String value = s.substring(index + 1);
                map.put(name, value);
                
            }
            cookies = map;
            
            input.close();
            http.disconnect();
            
            
            
            
            
            html =  sb.toString();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    
    private void getResponseByPost() {
        try {
            
            URL u = new URL(this.url);
            URLConnection con = u.openConnection();
            
            HttpURLConnection http = (HttpURLConnection) con;
            http.setInstanceFollowRedirects(true);
            
            if(cookie != null)
                http.setRequestProperty("Cookie", cookie);
            if(referer != null) 
                http.setRequestProperty("Referer", referer);
            
            BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuffer sb = new StringBuffer();
            while(input.ready()) {
                String line = input.readLine();
                sb.append(line + "\n");
            }
            
            
            Map<String, List<String>> m = http.getHeaderFields();
            
            Map<String, String> map = new HashMap<String, String>();
            
            for(String s : m.get("Set-Cookie")) {
                
                s = s.substring(0, s.indexOf(";"));
                
                int index = s.indexOf("=");
                String name = s.substring(0, index);
                String value = s.substring(index + 1);
                map.put(name, value);
                
            }
            cookies = map;
            
            input.close();
            http.disconnect();
            
            
            
            
            
            html =  sb.toString();
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    public Map<String, String> getCookies() {
        return cookies;
    }
    
    public String getHtml() {
        return html;
    }
    
    public static void main(String [] args) {
        HttpHelper http = new HttpHelper();
        http.setURL("");
        //System.out.println(http.getResponse());
    }
}
