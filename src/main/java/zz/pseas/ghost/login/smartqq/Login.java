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
/**   
* @date 2016年12月15日 下午6:04:10 
* @version   
* @since JDK 1.8  
*/

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Login {
	private boolean loginflag=false;
	private String ptuiCBurl="";
	private String ptwebqq="";
	private String psessionid="";
	private String clientid="";
	private String aid="";
	private String getCookieurl="";
	private String iamgeImg="D:\\jj";
	public CloseableHttpClient pagemain()
	{
		 CloseableHttpClient httpclient = HttpClients.createDefault();
		 HttpGet httpget = new HttpGet("http://w.qq.com/");
		 httpget.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");

		 System.out.println("Executing request " + httpget.getURI());//开始
		 String html="";
	     try {
			CloseableHttpResponse response = httpclient.execute(httpget);
			 System.out.println(response.getStatusLine());
			 HttpEntity entity = response.getEntity();
	         html= EntityUtils.toString(entity, "utf-8");
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	     System.out.println(html);
	     return httpclient;
	}
	//https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.3770239355508238
	public CloseableHttpClient getErweima(CloseableHttpClient httpclient)
	{
		 HttpGet httpget = new HttpGet("https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.3770239355508238");
		 System.out.println("获取二维码：Executing request " + httpget.getURI());//开始
		 String html="";
		 FileOutputStream fos;
	     try {
			CloseableHttpResponse response = httpclient.execute(httpget);
			 System.out.println(response.getStatusLine());
			 InputStream inputStream = response.getEntity().getContent();
			 File file = new File(this.iamgeImg);
			 if (!file.exists()) {
				 file.mkdirs();
			 }
			 fos = new FileOutputStream("D:\\jj\\test.jpg");
			 byte[] data = new byte[1024];
			 int len = 0;
			 while ((len = inputStream.read(data)) != -1) {
				 fos.write(data, 0, len);
			 }
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	     System.out.println(html);
	     return httpclient;
	}
	/**
	 * 检查是否登录成功
	 * @param httpclient
	 * @return
	 */
	public CloseableHttpClient checkLogin(CloseableHttpClient httpclient)
	{
		 HttpGet httpget = new HttpGet("https://ssl.ptlogin2.qq.com/ptqrlogin?webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&action=0-0-62857&mibao_css=m_webqq&t=undefined&g=1&js_type=0&js_ver=10156&login_sig=&pt_randsalt=0");
		 System.out.println("检查是否登录成：Executing request " + httpget.getURI());//开始
		 String html="";
	     try {
			CloseableHttpResponse response = httpclient.execute(httpget);
			 System.out.println(response.getStatusLine());
			 HttpEntity entity = response.getEntity();
	         html= EntityUtils.toString(entity, "utf-8");
	         //ptuiCB('0','0','http://ptlogin4.web2.qq.com/check_sig?pttype=1&uin=1069478446&service=ptqrlogin&nodirect=0&ptsigx=afde2a7fe5f26485b976c9f0f0d87c1ebf27706be0d7b9a0fbab5df1d9e5ec9fb1df62d5cdef526fa7e2df2b3ce2dd84fad270fbdfc90bdd4da0308f73a337fd&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&f_url=&ptlang=2052&ptredirect=100&aid=501004106&daid=164&j_later=0&low_login_hour=0&regmaster=0&pt_login_type=3&pt_aid=0&pt_aaid=16&pt_light=0&pt_3rd_aid=0','0','登录成功！', 'lonter');
	         if(html.indexOf("登录成功")!=-1)
	         {
	        	 this.aid=HttpTool.getaid(html);
	        	 this.loginflag=true;
	        	 int start=html.indexOf("http:");
		         int end=html.indexOf("pt_3rd_aid");
		         this.ptuiCBurl=html.substring(start, end+12);
		         System.out.println(this.ptuiCBurl);
		         HttpTool hp=new HttpTool();
		         this.ptwebqq=hp.getCookie("ptwebqq", response);
		         System.out.println("this.ptwebqq:"+this.ptwebqq);
	         }
	         
	         System.out.println(html);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	     
	     return httpclient;
	}
	public CloseableHttpClient setCookie(CloseableHttpClient httpclient)
	{
		HttpGet httpget = new HttpGet(this.getCookieurl);
		 System.out.println("获取必要的cookie：Executing request " + httpget.getURI());//开始
		 String html="";
	     try {
			CloseableHttpResponse response = httpclient.execute(httpget);
			 System.out.println(response.getStatusLine());
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	     
	     return httpclient;
	}
	public CloseableHttpClient getpsessionid(CloseableHttpClient httpclient)
	{
		HttpPost httppost = new HttpPost("http://d1.web2.qq.com/channel/login2");
		String html;
		 List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		 formparams.add(new BasicNameValuePair("r","{\"ptwebqq\":\""+this.ptwebqq+"\",\"clientid\":53999199,\"psessionid\":\"\",\"status\":\"online\"}"));
		 UrlEncodedFormEntity uefEntity;
		 try {
			uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
			 httppost.setEntity(uefEntity);  
	        System.out.println("executing request " + httppost.getURI());  
	         HttpResponse response =  httpclient.execute(httppost);
	         HttpEntity entity = response.getEntity();
	         html= EntityUtils.toString(entity, "utf-8");
	         System.out.println(html);
	         int start=html.indexOf("psessionid\":\"")+13;
	         html=html.substring(start);
	         int end=html.indexOf("\"");
	         html=html.substring(0, end);
	         this.psessionid=html;
	         System.out.println(html);
	     } catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}  
		 catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		 return httpclient;
	}
	/**
	 * 获得登录参数
	 * @param httpclient
	 * @return
	 */
	public CloseableHttpClient getPara(CloseableHttpClient httpclient)
	{
		 HttpGet httpget = new HttpGet(this.ptuiCBurl);
		 System.out.println("获得登录参数： Executing request " + httpget.getURI());//开始
		 String html="";
	     try {
			CloseableHttpResponse response = httpclient.execute(httpget);
			 System.out.println(response.getStatusLine());
			 HttpEntity entity = response.getEntity();
	         html= EntityUtils.toString(entity, "utf-8");
	         System.out.println(html);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	     return httpclient;
	}
	/**
	 * 获得消息 需要对Header进行设置！
	 * @param httpclient
	 */
	public String getmsg(CloseableHttpClient httpclient)
	{
		
			HttpPost httppost = new HttpPost("http://d1.web2.qq.com/channel/poll2");
			String html="";
			httppost.setHeader("Content-Type","application/x-www-form-urlencoded");
			httppost.setHeader("Origin","http://d1.web2.qq.com");
			httppost.setHeader("Referer","http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
			//httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");
			List<NameValuePair> formparams = new ArrayList<NameValuePair>();
			formparams.add(new BasicNameValuePair("r","{\"ptwebqq\":\""+this.ptwebqq+"\",\"clientid\":53999199,\"psessionid\":\""+this.psessionid+"\",\"key\":\"\"}"));
			UrlEncodedFormEntity uefEntity;
			try {
				uefEntity = new UrlEncodedFormEntity(formparams, "UTF-8");
				httppost.setEntity(uefEntity);  
				System.out.println("executing request " + httppost.getURI());  
				HttpResponse response =  httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				html= EntityUtils.toString(entity, "utf-8");
				System.out.println(html+"\t"+html.length());
				if(html.length()>130)
				{
					String group_code=HttpTool.getgroup_code(html);//判断群
					System.out.println("group_code:"+group_code);
					String msg=HttpTool.getmsgtext(html);
					String sendsms= Robot.postsms(msg);
					sendmsg(httpclient,sendsms,group_code);//小黄鸡鸡，不想接入删除即可
					
				}
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
	public void sendmsg(CloseableHttpClient httpclient, String sms, String group_code)
	{
		HttpPost httppost = new HttpPost("http://d1.web2.qq.com/channel/send_qun_msg2");
		httppost.setHeader("Content-Type","application/x-www-form-urlencoded");
		httppost.setHeader("Origin","http://d1.web2.qq.com");
		httppost.setHeader("Referer","http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2");
	//	httppost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/45.0.2454.93 Safari/537.36");
		String html;
		 List<NameValuePair> formparams = new ArrayList<NameValuePair>();
		 formparams.add(new BasicNameValuePair("r","{\"group_uin\":"+group_code+",\"content\":\"[\\\""+sms+"\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":165,\"clientid\":53999199,\"msg_id\":52370001,\"psessionid\":\""+this.psessionid+"\"}"));
		 System.out.println("发送的参数r："+formparams.get(0).getValue());
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
	
	}
	
	/**
	 * 此函数运行，等待二维码生成后，扫描二维码即可登陆
	 * bug：目前所有模拟登陆都存在的bug，需要先用浏览器打开smart qq，扫描登陆后再运行本程序
	 * 		即想本程序正常运行，需要先打开浏览器登录smart qq，然后再用本程序登陆，本程序登陆后
	 * 		就不用管用浏览器登录的那个了。市面上很多模拟登陆都存在这个问题哦，不止我的，这是cookie
	 * 		跨域了还是咋地，搞不懂
	 * @param args
	 */
	public static void main(String[] args)
	{
		Login lg=new Login();
		CloseableHttpClient httpclient=lg.pagemain();
		httpclient=lg.getErweima(httpclient);
		for(int i=0;;i++)
		{
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lg.checkLogin(httpclient);
			if(lg.loginflag)
				break;
		}
		
		httpclient=lg.getPara(httpclient);
		httpclient=lg.getpsessionid(httpclient);
		for(int i=0;;i++)
		{
			lg.getmsg(httpclient);
		}
		
	}
}
