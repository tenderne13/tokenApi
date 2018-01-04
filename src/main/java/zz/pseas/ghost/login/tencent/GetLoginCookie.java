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

import zz.pseas.ghost.login.tencent.dao.ShowImage;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class GetLoginCookie {
	
    private String pwd, salt, verifycode, login_sig, pt_verifysession_v1;
    
    private String appid = "501004106";
    private String action = "0-0-201958";
    private String uin = "";
    private String js_type = "0";
    private String js_ver = "10173";
    private String urlLogin = "https://ssl.ptlogin2.qq.com/login?";
    
    private String urlSuccess = "http://www.qq.com/qq2012/loginSuccess.htm";
    
    private String urlSig = "http://ui.ptlogin2.qq.com/cgi-bin/login";
    
    private String urlCheck = "https://ssl.ptlogin2.qq.com/check";
    private String urlverifycode = "https://ssl.captcha.qq.com/getimage";
    
    Cookie cookie = new Cookie();
    
    public GetLoginCookie() {     
        this.run();
    }
    
      //this method is used in password login,it is useless for QR login
    private void getPsessionid() {
        String url = "http://d1.web2.qq.com/channel/login2";
       
        try {
            URL u = new URL(url);
            HttpURLConnection http = (HttpURLConnection) u.openConnection();

            http.setRequestProperty("Host", "d1.web2.qq.com");
            http.setRequestProperty("Referer", "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
            http.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:47.0) Gecko/20100101 Firefox/47.0");
            
            String ptwebqq = cookie.get("ptwebqq");
            String param = "r={\"ptwebqq\":\"" + ptwebqq + "\",\"clientid\": " + 53999199 + ",\"psessionid\":\"\",\"status\":\"online\"}";
            System.out.println("login param:"+param);
           
            http.setRequestProperty("Cookie", cookie.toString());
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            BufferedWriter output = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
            output.write(param);
            output.flush();
            output.close();
      
            BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream(), "utf-8"));
            
            StringBuffer sbs = new StringBuffer();
            while(input.ready()) {
                sbs.append(input.readLine());
                sbs.append("\n");
            }
            String result = sbs.toString();
            System.out.println("result:"+result);
            
            Pattern pattern = Pattern.compile("\"psessionid\":\".+?\"");
            Matcher matcher = pattern.matcher(result);
            if(matcher.find()) {
                String s = matcher.group();
                int index = s.indexOf(":");
                int end = s.lastIndexOf("\"");
                String psessionid = s.substring(index + 2, end);
                System.out.println("psessionid = " + psessionid);
                cookie.put("psessionid", psessionid);
            }
        }catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
	public void run() {
        this.get_signature();
        this.getQR();
        ShowImage s = new ShowImage("./QR.jpg");
        int flag = 1;
        while(flag != 0) {
            flag = checkQR();
            System.out.println("flag : " + flag);
            try {
                Thread.sleep(7000);
                System.out.println("flag : " + "亲扫描二维码登录QQ.");
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        s.setVisible(false);
        this.getVfwebqq();
        this.getPsessionid();
    }
    
    private void get_signature() {
        ///setp 1 :: 
        try {
            String url = urlSig + "?appid=" + URLEncoder.encode(appid, "utf-8").toString() + "&" + "s_url=" + URLEncoder.encode(urlSuccess, "utf-8").toString() + "&no_verifyimg=1";
            
            URL u = new URL(url);
            System.out.println(u);
            HttpURLConnection http = (HttpURLConnection) u.openConnection();
           
            Map<String, List<String>> m = http.getHeaderFields();
            
            for(Entry<String, String> entry : getCookie(m).entrySet()) {
                cookie.put(entry.getKey(), entry.getValue());  
            }
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    
    /*
    private void check_login() {
        //setp 2 :: get verifycode and pt_verifysession_v1
        String url = urlCheck + "?";
        try {
        String [] param = {
                "regmaster=", 
                "uin=" + uin, 
                "appid=" + appid,  
                "login_sig=" +login_sig,
                "js_ver=10173",
                "js_type=0",
                "u1=" + URLEncoder.encode("http://w.qq.com/proxy.html","utf-8").toString(), 
                "r=0.9115912268128662"};
        for(int i = 0; i < param.length - 1; i++)
            url += param[i] + "&";
        url += param[param.length - 1];
        }
        catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }
        
        System.out.println("setp2 : url = " + url); 
        try {
            URL u = new URL(url);
            HttpURLConnection http = (HttpURLConnection) u.openConnection();
            BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuffer sb = new StringBuffer();
            while(input.ready()) {
                sb.append(input.readLine() + "\n");
            }     
            String result = sb.toString();
            System.out.println("result  : " + result);
            Pattern pattern = Pattern.compile("'.+'");
            Matcher matcher = pattern.matcher(result);
            String [] find = {};
            while(matcher.find()) {
                find = matcher.group(0).replaceAll("'" , "").split(",");
                System.out.println("find" + find[0]);
            }   
            if(Integer.valueOf(find[0]) !=  0) {
                System.out.println("Need Input Vericode");
                this.pt_verifysession_v1 = this.getImage();
                this.salt = find[2];
                System.out.println("please input verifycode:");
                Scanner sc = new Scanner(System.in);
                this.verifycode = sc.nextLine().toUpperCase();
                System.out.println("verifycode:" + this.verifycode);
                
                
            }
            else {
                this.verifycode = find[1];
                this.salt = find[2];
                this.pt_verifysession_v1 = find[3];
                Map<String, List<String>> m = http.getHeaderFields();
                for(Entry<String, String> entry : getCookie(m).entrySet()) {
                    cookie.put(entry.getKey(), entry.getValue());
                    System.out.println("Step 2 -- Key = " + entry.getKey() + "   Value = " + entry.getValue());
                }
            }
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    */
    
    
    private Map<String, String> getCookie(Map<String, List<String>> m) {
        
        Map<String, String> map = new HashMap<String, String>();
        if( m.get("Set-Cookie") != null)
        for(String s : m.get("Set-Cookie")) {  
            s = s.substring(0, s.indexOf(";"));
            int index = s.indexOf("=");
            String name = s.substring(0, index);
            String value = s.substring(index + 1);
            map.put(name, value);
        }
        return map;
    }
    
    public Cookie getCookies() {
        
        return cookie;
    }
    
    private String getImage() {
        try {
            URL u = new URL(urlverifycode);
            HttpURLConnection http = (HttpURLConnection) u.openConnection();
            
            Map<String, String> coo = getCookie(http.getHeaderFields());
            
            String verifysession = coo.get("verifysession");
            
            FileOutputStream output = new FileOutputStream("./verifycode.jpg");
            InputStream input = http.getInputStream();
            int i = -1;
            while((i = input.read()) != -1) {
                output.write(i);
            }
            output.close();
            input.close();
            return verifysession;
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
    
    
    private void getQR() {
        try {
            String url = "https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.1";
            URL u = new URL(url);
            HttpURLConnection http = (HttpURLConnection) u.openConnection();
            Map<String, String> coo = getCookie(http.getHeaderFields());
            System.out.println("qrsig = " + coo.get("qrsig"));
            this.cookie.put("qrsig", coo.get("qrsig"));
            
            FileOutputStream output = new FileOutputStream("./QR.jpg");
            InputStream input = http.getInputStream();
            int i = -1;
            while((i = input.read()) != -1) {
                output.write(i);
            }
            output.close();
            input.close();
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
    }
    
    private int checkQR() {
        
        try {
            String url = "https://ssl.ptlogin2.qq.com/ptqrlogin?";
            
            String [] param = {
                    "webqq_type=10",
                    "remember_uin=1",
                    "login2qq=1",
                    "aid=501004106",
                    "u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10",
                    "ptredirect=0",
                    "ptlang=2052",
                    "daid=164",
                    "from_ui=1",
                    "pttype=1",
                    "dumy=",
                    "fp=loginerroralert",
                    "action=0-0-82429",
                    "mibao_css=m_webqq",
                    "t=1",
                    "g=1",
                    "js_type=0",
                    "js_ver=10156",
                    "login_sig=",
                    "pt_randsslat=2"
            };
            StringBuffer s = new StringBuffer();
            s.append(url);
            
            for(int i = 0; i < param.length - 1; i++) {
                s.append(param[i]);
                s.append("&");
            }
            
            s.append(param[param.length - 1]);
            url = s.toString();
            
            URL u = new URL(url);
            HttpURLConnection http = (HttpURLConnection) u.openConnection();
            
            http.setRequestProperty("Cookie", cookie.toString());
            
            BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuffer sb = new StringBuffer();
            while(input.ready()) {
                sb.append(input.readLine());
                sb.append("\n");
            }
            
            input.close();
            System.out.println("QR statu : " + sb.toString());
            String resultStr = sb.toString();
            Pattern patter = Pattern.compile("\'.+\'");
            Matcher matcher = patter.matcher(resultStr);
            String [] find = null;
            while(matcher.find()) {
                find = matcher.group().replaceAll("'", "").split(",");
            }
            
            if(Integer.valueOf(find[0]) != 0) {
                return 1;
            }
            else {
                Map<String, String> coo = getCookie(http.getHeaderFields());
                cookie.put("ptwebqq", coo.get("ptwebqq"));
                cookie.put("uin", coo.get("uin"));
         
                String reurl = find[2];
                URL ul = new URL(reurl);
                HttpURLConnection https = (HttpURLConnection) ul.openConnection();
                
                https.setInstanceFollowRedirects(false);
                Map<String, String> cooki = getCookie(https.getHeaderFields());
                
                cookie.put("skey", cooki.get("skey"));
                
                cookie.put("p_skey", cooki.get("p_skey"));
                cookie.put("pt4_token", cooki.get("pt4_token"));
                cookie.put("uin", cooki.get("uin"));
                cookie.put("p_uin", cooki.get("p_uin"));
                cookie.put("pt2gguin", cooki.get("pt2gguin"));
                return 0;
            }     
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }
    
    
    private void getVfwebqq() {
        String url = "http://s.web2.qq.com/api/getvfwebqq?";
        String referer = "http://s.web2.qq.com/proxy.hml?v=20130916001&callback=1&id=1";
        String param [] = {
                "ptwebqq=" + cookie.get("ptwebqq"),
                "clientid=53999199",
                "t=" + new Random().nextDouble(),
        };
        url = this.setParamToUrl(url, param);
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HttpURLConnection https;
        try {
            https = (HttpURLConnection) u.openConnection();
            String name [] = {
                    "skey",
                    "p_skey",
                    "pt4_token",
                    "uin",
                    "p_uin",
                    "pt2gguin",
            };
            https.setRequestProperty("Cookie", cookie.toString());
            https.setRequestProperty("referer", referer);
            String result = readHtml(https.getInputStream());
            Pattern pattern = Pattern.compile("\"vfwebqq\":\".+\"");
            Matcher matcher = pattern.matcher(result);
            if(matcher.find()) {
                String s = matcher.group();    
                int index = s.indexOf(":");
                int end = s.lastIndexOf("\"");
                String vfwebqq = s.substring(index + 2, end);
                System.out.println("vfwebqq = " + vfwebqq);
                cookie.put("vfwebqq", vfwebqq);
            }
            System.out.println("vfwebqq:"+result) ;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private String readHtml(InputStream input) {
        BufferedReader in = new BufferedReader(new InputStreamReader(input));
        StringBuffer sb = new StringBuffer();
        try {
            while(in.ready()) {
                sb.append(in.readLine());
                sb.append("\n");
            }
           
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return sb.toString();
    }
    
    
    private String setParamToUrl(String url, String[] param) {
        StringBuffer s = new StringBuffer();
        s.append(url);
        for(int i = 0; i < param.length - 1; i++) {
            s.append(param[i]);
            s.append("&");
        }
        s.append(param[param.length - 1]);
        return s.toString();
    }
    
    
    private String setCookie(Map<String, String> Cookie, String [] name) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < name.length; i++) {
            sb.append(name[i]);
            sb.append("=");
            sb.append(Cookie.get(name[i]));
            sb.append(";");
        }
        return sb.toString();
    }
    
    public static void main(String...strings) {
        
        
    }
}
