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
package zz.pseas.ghost.login.jiandan;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import zz.pseas.ghost.utils.HttpClientUtil;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**   
* @date 2016年12月15日 下午10:29:21 
* @version   
* @since JDK 1.8  
*/
public class GetJianDan {
	private static String url = "http://jandan.net/ooxx"; // 煎蛋网入口URL
	HttpClient httpClient = HttpClientUtil.getHttpClient();

	/**
	 * 请求页面
	 * 
	 * @param url
	 * @return 请求返回页面的HTML
	 */
	private String getIndexHtml(String url) {
		GetMethod getMethod = null;
		String info = "";
		getMethod = new GetMethod(url); // 请求URL
		getMethod
				.addRequestHeader("Accept",
						"text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8");
		getMethod
				.addRequestHeader(
						"User-Agent",
						"Mozilla/5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/49.0.2623.112 Safari/537.36");
		try {
			int statcode = httpClient.executeMethod(getMethod);
			if (statcode == 200) {
				info = getMethod.getResponseBodyAsString();
				// 解析首次请求的页面
			}
		} catch (HttpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return info;
	}

	/**
	 * 此方法用Jsoup解析网页
	 * 
	 * @param info
	 *            页面信息
	 * @return 页面妹子图片列表
	 */
	private List<String> printInfo(String info) {
		List<String> imgUrl_list = new ArrayList<String>();
		Document doc = Jsoup.parse(info);
		Elements eles = doc.select("div[class=text]").select("a[class=view_img_link]");
		for (int i = 0; i < eles.size(); i++) {
			String imgUrl = eles.get(i).attr("href");
			imgUrl_list.add(imgUrl);
		}
		return imgUrl_list;
	}
	
	/**
	 * 解析并获取下一页
	 * @param info 网页
	 * @return 下一页的地址
	 */
	private void getNextPage(String info){
		Document doc= Jsoup.parse(info);
		url=doc.select("div[class=cp-pagenavi]").select("a[class=previous-comment-page]").attr("href");
	}
	
	/**
	 * 根据图片地址读取图片并保存到指定文件下
	 * @param imgUrl_list
	 * @throws IOException 
	 */
	private void getImgSaveFile(List<String> imgUrl_list) throws IOException {
		InputStream inputStream = null;
		FileOutputStream outStream = null;
		File dir=new File("./jiandan");
		try {
			for (String str_url : imgUrl_list) {
				//这里用自带请求功能请求图片地址获取流
				URL url = new URL(str_url);
				HttpURLConnection conn=(HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				//设置超时时间为5秒
				conn.setConnectTimeout(50000);
				inputStream=conn.getInputStream();
				byte[] data=readInputStream(inputStream);
				if(!dir.exists()){
					dir.mkdirs();
				}
				File imgFile=new File("./jiandan/" + System.currentTimeMillis() + ".jpg");
				outStream=new FileOutputStream(imgFile);
				outStream.write(data);
			}
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			if(outStream!=null)
				outStream.close();
			if(inputStream!=null){
				inputStream.close();
			}
			
		}
	}
	
	/**
	 * 把InputStream流写成字节数组
	 * @param inputStream 输入流
	 * @return 输出字节数组
	 * @throws IOException IO异常
	 */
	private byte[] readInputStream(InputStream inputStream) throws IOException{
		ByteArrayOutputStream outStream=new ByteArrayOutputStream();
		byte[] buffer=new byte[1024];  //每次读1024个字节
		byte[] byt=null;
		int len=0;
		try {
			while((len=inputStream.read(buffer))!=-1){
				outStream.write(buffer,0,len);
			}
			byt=outStream.toByteArray();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			inputStream.close();
			outStream.close();
		}
		return byt;
	}
	
	/**
	 * 控制中心
	 */
	private void console() {
		int maxPage=100;
		String info="";
		System.out.println("准备抓取第1页的妹子..........");
		//控制抓取页面的数量
		for(int i=0;i<maxPage;i++){
			info = getIndexHtml(url);
			getNextPage(info);
			List<String> imgUrl_list = printInfo(info);
			try {
				getImgSaveFile(imgUrl_list);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				if(i<maxPage-1){
					System.out.println("先暂停5秒吧...");
					System.out.println("准备抓取第"+(i+1+1)+"页的妹子..........");
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					System.out.println("抓取结束，去欣赏美图吧！");
				}
			}
		}
	}
	
	/**
	 * 主方法
	 * @param args
	 */
	public static void main(String[] args) {
		GetJianDan jiandan = new GetJianDan();
		jiandan.console();
	}
}

