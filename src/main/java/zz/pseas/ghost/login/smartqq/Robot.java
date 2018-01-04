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
package zz.pseas.ghost.login.smartqq;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
/**   
* @date 2016年12月15日 下午6:04:10 
* @version   
* @since JDK 1.8  
*/

/**
 * 使用小黄鸡的接口
 * http://www.niurenqushi.com/app/simsimi/ajax.aspx
 * @author Zhang JT
 *
 */
public class Robot {
	 CloseableHttpClient httpclient = HttpClients.createDefault();
	public static String postsms(String sms)
	{
			CloseableHttpClient httpclient = HttpClients.createDefault();
		 	HttpPost httppost = new HttpPost("http://www.niurenqushi.com/app/simsimi/ajax.aspx");
			String html="";
			 List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			 formparams.add(new BasicNameValuePair("txt",sms));
			 UrlEncodedFormEntity uefEntity;
			 try {
				uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
				 httppost.setEntity(uefEntity);  
		        System.out.println("executing request " + httppost.getURI());  
		         HttpResponse response =  httpclient.execute(httppost);
		         HttpEntity entity = response.getEntity();
		         html= EntityUtils.toString(entity, "utf-8");
		         System.out.println(html);
		     } catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}  
			 catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
	    
	    
		 return html;
	}
	public static void main(String[] args)
	{
		Robot rb=new Robot();
		rb.postsms("你");
	}
}
