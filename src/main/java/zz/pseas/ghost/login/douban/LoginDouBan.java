/**
 *
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
 */
package zz.pseas.ghost.login.douban;
/**   
* @date 2016年12月16日 下午9:26:00 
* @version   
* @since JDK 1.8  
*/

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import zz.pseas.ghost.utils.HttpClientUtil;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @date 2016年12月15日 下午10:29:21
 * @version
 * @since JDK 1.8
 */

/**
 * 模拟登录豆瓣网
 * @author hutao
 * @info 需要先设定好自己的昵称（主函数里），当然，这里可以做解析去获取对应的值，这里就不再赘述了，前面煎蛋网爬妹子图已经实现
 * 		还有，用户名，密码为了保密，也设计成从控制台输入
 */
public class LoginDouBan {
	private String url="https://accounts.douban.com/login"; //登录地址
	HttpClient httpClient=HttpClientUtil.getHttpClient();   //其实这就是你的浏览器
	String form_email=""; //用户名
	String form_password="";   //密码
	static String uname="";  //昵称
	
	/**
	 * 请求函数
	 */
	public void requestDouBan(){
		GetMethod getMethod=null;   //Get 请求
		PostMethod postMethod=null; //POST 请求
		String info="";  //存储页面信息

		try {
			while(true){
				//-----第一次请求  获取验证码地址和获取请求时验证码所对应的token参数
				getMethod=new GetMethod("https://www.douban.com/j/misc/captcha");
				int stateCode = httpClient.executeMethod(getMethod);
				if(stateCode==200){
					info=getMethod.getResponseBodyAsString();
					Map<String, String> list=getCaptchaId(info);  //
					String captcha_id=list.get("token");
					String yzm="";  //验证码   需要手动设置（前提把图片下载到本地）
					String img_url="https:"+list.get("url");
	  				downImage(img_url);  //调用下载图片的方法
	  				BufferedReader buff=new BufferedReader(new InputStreamReader(System.in));
	  				System.out.println("请输入用户名：");
	  				form_email=buff.readLine();
	  				System.out.println("请输入密码：");
	  				form_password=buff.readLine();
	  				System.out.println("请输入验证码：");
	  				yzm=buff.readLine();
	  				
	  				//-----------第二次请求 会返回302状态码 （抓包分析）
					postMethod=new PostMethod(url);
					postMethod.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
					postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded");
					postMethod.addRequestHeader("Host", "accounts.douban.com");
					postMethod.addRequestHeader("Origin", "https://accounts.douban.com");
					postMethod.addRequestHeader("Referer", "https://accounts.douban.com/login");
					postMethod.addRequestHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
					
					postMethod.addParameter("source", "None");
					postMethod.addParameter("redir", "https://www.douban.com/");
					postMethod.addParameter("form_email", form_email);
					postMethod.addParameter("form_password",form_password);
					postMethod.addParameter("captcha-solution", yzm);
					postMethod.addParameter("captcha-id", captcha_id);
					postMethod.addParameter("login", "登录");
					//设置提交统一编码方式
					postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "utf-8");
					
					stateCode=httpClient.executeMethod(postMethod);
					if(stateCode==302){
						info=postMethod.getResponseBodyAsString();
						//如果返回content中包含error 可能是验证码错误，可能是用户名输入错误，抓包分析错误代码
						if(StringUtils.contains(info, "\"r\":false") || StringUtils.contains(info, "error=1012")){
							System.out.println("用户名输入错误！");
							continue;  //输入错误就继续循环
						}else if(StringUtils.contains(info, "error=1013")){
							System.out.println("密码输入错误！");
							continue;
						}else if(StringUtils.contains(info, "error=1011")){
							System.out.println("验证码输入错误！");
							continue;
						}else{
							//-------第三次请求 此时为了迎合页面跳转到首页，如果返回的页面包含自己对应的用户名昵称，正面登陆成功
							getMethod=new GetMethod("https://www.douban.com/");
							stateCode=httpClient.executeMethod(getMethod);
							if(stateCode==200){
								info=getMethod.getResponseBodyAsString();
								String str = String.format("%s的帐号", uname);
								if(StringUtils.contains(info, str)){ //引号内是自己的昵称
									System.out.println("恭喜"+form_email+" 您已登录成功！");
									break;
								}else{
									System.out.println("请耐心检查你的错误！（右上角昵称是否一致！）"); 
									continue;
								}
							}
						}
					}
				}
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 解析并获取Captcha-id值（token值）和图片URL
	 * @param info  首页Html
	 * @return Captcha-id值 图片URL 集合
	 * @throws JSONException 
	 */
	private Map<String,String> getCaptchaId(String info) throws JSONException{
		Map<String,String> mapList=new HashMap<String, String>();
		JSONObject obj=new JSONObject(info);
		Iterator<String> it=obj.keys();
		String key="";  //键
		String value="";//值
		while(it.hasNext()){
			key=it.next();
			value=obj.getString(key);
			mapList.put(key, value);
		}
		return mapList;
	}
	
	/**
	 * 根据url下载图片
	 * @param url 获取到的验证码图片的路径
	 */
	private void downImage(String url){
		File fileDir=new File("./resources/captcha/douban");  //验证码存放的路径
		if(!fileDir.exists()){
			fileDir.mkdirs();
		}
		File file=new File("./resources/captcha/douban/douban-captcha.png");
		if(file.exists()){
			file.delete();
		}
		InputStream input = null;
		FileOutputStream out= null;
		GetMethod get=new GetMethod(url);
		try {
			httpClient.executeMethod(get);
			input = get.getResponseBodyAsStream();
			int i=-1;
			byte[] byt=new byte[1024];
			out=new FileOutputStream(file);
			while((i=input.read(byt))!=-1){
				out.write(byt);
			}
			System.out.println("验证码图片已下载成功！请在\""+fileDir.getPath()+"\"路径下查看...");
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 主函数
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		LoginDouBan login=new LoginDouBan();
		uname="*****2016"; //在这里设定好昵称 
		login.requestDouBan();
	}
}

