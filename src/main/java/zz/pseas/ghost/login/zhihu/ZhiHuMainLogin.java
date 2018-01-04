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
package zz.pseas.ghost.login.zhihu;

import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**   
* @date 2016年9月14日 下午9:26:00 
* @version   
* @since JDK 1.8  
*/

/**
 * 模拟登录知乎
 */
public class ZhiHuMainLogin {
    //知乎首页
    final private static String INDEX_URL = "https://www.zhihu.com";
    //邮箱登录地址
    final private static String EMAIL_LOGIN_URL = "https://www.zhihu.com/login/email";
    //手机号码登录地址
    final private static String PHONENUM_LOGIN_URL = "https://www.zhihu.com/login/phone_num";
    //登录验证码地址
    final private static String YZM_URL = "https://www.zhihu.com/captcha.gif?type=login";

    private CloseableHttpClient httpClient;
    private HttpClientContext httpClientContext;

    /**
     * 初始化CloseableHttpClient、HttpClientContext并设置Cookie策略
     */
    public ZhiHuMainLogin(){
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
                .build();
        httpClient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .build();

        httpClientContext = HttpClientContext.create();
        Registry<CookieSpecProvider> registry = RegistryBuilder
                .<CookieSpecProvider> create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory()).build();
        httpClientContext.setCookieSpecRegistry(registry);
    }
    /**
     *
     * @param emailOrPhoneNum 邮箱或手机号码
     * @param pwd 密码
     * @return
     */
    public boolean login(String emailOrPhoneNum,
                         String pwd){
        String yzm = null;
        String loginState = null;
        HttpGet getRequest = new HttpGet(INDEX_URL);
        HttpClientUtil.getWebPage(httpClient, httpClientContext, getRequest, "utf-8", false);
        HttpPost request = null;
        List<NameValuePair> formParams = new ArrayList<NameValuePair>();
        if(emailOrPhoneNum.contains("@")){
            //通过邮箱登录
            request = new HttpPost(EMAIL_LOGIN_URL);
            formParams.add(new BasicNameValuePair("email", emailOrPhoneNum));
        }
        else {
            //通过手机号码登录
            request = new HttpPost(PHONENUM_LOGIN_URL);
            formParams.add(new BasicNameValuePair("phone_num", emailOrPhoneNum));
        }
        yzm = zhCaptcha(httpClient, httpClientContext, YZM_URL);
        formParams.add(new BasicNameValuePair("captcha", yzm));
        formParams.add(new BasicNameValuePair("_xsrf", ""));//这个参数可以不用
        formParams.add(new BasicNameValuePair("password", pwd));
        formParams.add(new BasicNameValuePair("remember_me", "true"));
        UrlEncodedFormEntity entity = null;
        try {
            entity = new UrlEncodedFormEntity(formParams, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        request.setEntity(entity);
        loginState = HttpClientUtil.getWebPage(httpClient, httpClientContext, request, "utf-8", true);//登录
        JSONObject jo = new JSONObject(loginState);
        if(jo.get("r").toString().equals("0")){
            System.out.println("登录知乎成功");
            getRequest = new HttpGet("https://www.zhihu.com");
            /**
             * 访问首页
             */
            String content = HttpClientUtil.getWebPage(httpClient, httpClientContext,getRequest, "utf-8", false);
            System.out.println(content);
            return true;
        }else{
            throw new RuntimeException(HttpClientUtil.decodeUnicode(loginState));
        }
    }


    public CookieStore loginAndGetCookieStore(String emailOrPhoneNum,String pwd){
        boolean isSuccess = login(emailOrPhoneNum, pwd);
        if(isSuccess){
            return httpClientContext.getCookieStore();
        }
        return  null;

    }
    /**
     * 下载验证码至本地，并手动输入验证码//
     * @return
     */
    public String zhCaptcha(CloseableHttpClient httpClient, HttpClientContext context, String url){
        String verificationCodePath = System.getProperty("user.home");
        //下载验证码至本地
        HttpClientUtil.downloadFile(httpClient, context, url, verificationCodePath, "/yzm.jpg",true);
        System.out.println("请输入 " + verificationCodePath + "\\zhihu_captcha01.jpg 下的验证码：");
        Scanner sc = new Scanner(System.in);
        String yzm = sc.nextLine();
        return yzm;
    }
    public static void main(String[] args) throws IOException {
        ZhiHuMainLogin modelLogin = new ZhiHuMainLogin();
        /*modelLogin.login("17801002476", "c846008154601358");*/

        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .build();

        CookieStore cookieStore = new BasicCookieStore();

        cookieStore.addCookie(new BasicClientCookie("_xsrf","3f6b1b37-870c-467b-a588-2d7a8424a5af"));
        cookieStore.addCookie(new BasicClientCookie("aliyungf_tc","AQAAAG8/xh1ryggAc5MzPS8U+R10LTsL"));
        cookieStore.addCookie(new BasicClientCookie("cap_id","\"MDFmY2NiYzkzZDIyNDlkMzk1ODMxYzg4NDA2YjE5MDc=|1515046622|35600896ad3e4cde743da740ff6dbae979b029e8\""));
        cookieStore.addCookie(new BasicClientCookie("d_c0","\"APBgrt8H8AyPThSEM8ebb-J3gLMIGR8hFNQ=|1515046621\""));
        cookieStore.addCookie(new BasicClientCookie("q_c1","140d94459f6c4b059f20ca6eadbb502d|1515046621000|1515046621000"));
        cookieStore.addCookie(new BasicClientCookie("r_cap_id","\"NTExNDkyMTkxOTRlNDgwZThhYTNmYTEwMjYzZmNhOGU=|1515046622|56152660dd368039e74212a0fc02f539f3a1026c\""));
        cookieStore.addCookie(new BasicClientCookie("z_c0","Mi4xeFNrcEJ3QUFBQUFBOEdDdTN3ZndEQmNBQUFCaEFsVk4tUlE3V3dCN21HaUZQTV9sRURScDB5ZzNwSkE5RjVDcHJR|1515046650|8baf5500f8e9391fc84686017def64d755aee65b"));

        //cookieStore.addCookie();

        modelLogin.httpClientContext.setCookieStore(cookieStore);
        HttpGet httpGet = new HttpGet("https://www.zhihu.com/people/tenderne13-49/activities");
        CloseableHttpResponse response = httpClient.execute(httpGet, modelLogin.httpClientContext);
        String s = EntityUtils.toString(response.getEntity(), "UTF-8");
        System.out.println(s);


    }
}
