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
package zz.pseas.ghost.login.tencent.dao;

import org.json.JSONObject;
import zz.pseas.ghost.login.tencent.Cookie;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class GetFriendAccount extends Abstract{
    
    String type = "1";
    String uin, account;
    String result;
    public GetFriendAccount(Cookie cookie) {
        super(cookie);
        super.url = "http://s.web2.qq.com/api/get_friend_uin2";
    }
    
    
    private int getResult() {

        String url = super.url + "?t=" + t + "&tuin=" + super.tuin + "&type=" + type + "&vfwebqq=" + vfwebqq;
        try {
            URL u = new URL(url);
            System.out.println(url);
            HttpURLConnection http = (HttpURLConnection) u.openConnection();
            http.setRequestProperty("Host", Host);
            http.setRequestProperty("Referer", Referer);
            http.setRequestProperty("User-Agent", User_Agent);
            http.setRequestProperty("Cookie", cookie.toString());
            BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
            StringBuffer sb = new StringBuffer();
            while(input.ready()) {
                sb.append(input.readLine());
                sb.append("\n");
            }          
            result = sb.toString();
            return 1;
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            System.err.println("getFriendAccount net work error");
            System.err.println("System will try again");
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            System.err.println("getFriendAccount read error");
            System.err.println("System will try again");
            e.printStackTrace();
            return 0;
        }
    }
    
    public String getaccount(String tuin) {
        super.tuin = tuin;
        while(getResult() == 0) {
            
        }
        while(jsonProcess() == -1) {
            while(getResult() == 0);
        }
        
        if(account == null) {
            System.err.println("unexcepted error on get account");
        }
        else 
            return account;
        return null;
    }
    
    private int jsonProcess() {
        System.out.println("result :   " + result);
        
        if(result != null) {
            JSONObject json = new JSONObject(result);
            Object retcode = json.get("retcode");
            if( (Integer)retcode < 0 || (Integer)retcode > 0) 
                return -1;
            else {
                JSONObject res = json.getJSONObject("result");
                Object acc = res.get("account");
                account = acc.toString();
                return 1;
            }
        }
        return 0;
    }
}
