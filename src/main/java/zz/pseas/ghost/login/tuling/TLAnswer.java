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
package zz.pseas.ghost.login.tuling;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class TLAnswer {
    private String URL = "http://www.tuling123.com/openapi/api";
    private String Key = "you tuling123 key";
    private String ask, answer;
    
    public void setAsk(String ask) {
        this.ask = ask;
    }
    
    public String ask(String content) {
        this.ask = content;
        if(content != null) {
            try {
                ask = URLEncoder.encode(this.ask, "utf-8");
            }catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }   
        }
        
        String url = URL + "?key=" + this.Key + "&info=" + this.ask;
        
        URL u = null;
        try {
            u = new URL(url);
        } catch (MalformedURLException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        HttpURLConnection http = null;
        try {
            http = (HttpURLConnection) u.openConnection();
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        BufferedReader input = null;
        try {
            input = new BufferedReader(new InputStreamReader(http.getInputStream()));
        } catch (IOException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        StringBuffer sb = new StringBuffer();
        try {
            while(input.ready()) {
                sb.append(input.readLine());
                sb.append("\n");
            }
            
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        
        JSONObject json = new JSONObject(sb.toString());
        return json.getString("text");
         
    }
    public static void main(String ...strings) {
        TLAnswer tLAnswer = new TLAnswer();
        System.out.println(tLAnswer.ask("墨子是谁？"));
    }
}
