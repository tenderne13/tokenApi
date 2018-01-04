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

import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpHostConnectException;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpecProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BestMatchSpecFactory;
import org.apache.http.impl.cookie.BrowserCompatSpecFactory;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.util.List;
/**   
* @date 2016年9月14日 下午9:26:00 
* @version   
* @since JDK 1.8  
*/

/**
 * HttpClient工具类
 */
public class HttpClientUtil {



    private static HttpClientContext contextStoreInit(CookieStore cookieStore){
        HttpClientContext httpClientContext = HttpClientContext.create();
        Registry<CookieSpecProvider> registry = RegistryBuilder
                .<CookieSpecProvider> create()
                .register(CookieSpecs.BEST_MATCH, new BestMatchSpecFactory())
                .register(CookieSpecs.BROWSER_COMPATIBILITY,
                        new BrowserCompatSpecFactory()).build();
        httpClientContext.setCookieSpecRegistry(registry);
        httpClientContext.setCookieStore(cookieStore);
        return httpClientContext;
    }

    private static CloseableHttpClient clientInit(){
        RequestConfig globalConfig = RequestConfig.custom()
                .setCookieSpec(CookieSpecs.BROWSER_COMPATIBILITY)
                .build();
        CloseableHttpClient httpClient = HttpClients.custom()
                .setDefaultRequestConfig(globalConfig)
                .build();
        return httpClient;
    }

    public static String doGet(String url, CookieStore cookieStore){
        CloseableHttpClient httpClient = clientInit();
        HttpClientContext context = contextStoreInit(cookieStore);
        HttpGet httpGet = new HttpGet(url);
        try {
            CloseableHttpResponse response = httpClient.execute(httpGet, context);
            return EntityUtils.toString(response.getEntity(),"UTF-8");
        } catch (IOException e) {
            System.out.println("----->"+e);
            return null;
        }
    }
	/**
	 * 执行request并返回网页内容
	 * @param httpClient HttpClient客户端
	 * @param context 上下文
	 * @param request 请求
	 * @param encoding 字符编码
	 * @param isPrintConsole 是否打印到控制台
     * @return 网页内容
     */
	public static String getWebPage(CloseableHttpClient httpClient
			, HttpClientContext context
			, HttpRequestBase request
			, String encoding
			, boolean isPrintConsole){
		CloseableHttpResponse response = null;
		try {
			response = httpClient.execute(request,context);
		} catch (HttpHostConnectException e){
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("status---" + response.getStatusLine().getStatusCode());
        List<Cookie> cookies = context.getCookieStore().getCookies();
        for(Cookie cookie : cookies){
            System.out.println(cookie.getName()+":"+cookie.getValue());
        }
        BufferedReader rd = null;
		StringBuilder webPage = null;
		try {
			rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent(),encoding));
			String line = "";
			webPage = new StringBuilder();
			while((line = rd.readLine()) != null) {
				webPage.append(line);
				if(isPrintConsole){
					System.out.println(line);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		request.releaseConnection();
		return webPage.toString();
	}
	/**
	 * 下载图片
	 * @param fileURL 文件地址
	 * @param path 保存路径
	 * @param saveFileName 文件名，包括后缀名
	 * @param isReplaceFile 若存在文件时，是否还需要下载文件
	 */
	public static void downloadFile(CloseableHttpClient httpClient
			, HttpClientContext context
			, String fileURL
			, String path
			, String saveFileName
			, Boolean isReplaceFile){
		try{
			HttpGet request = new HttpGet(fileURL);
			CloseableHttpResponse response = httpClient.execute(request,context);
			System.out.println("status:" + response.getStatusLine().getStatusCode());
			File file =new File(path);
			//如果文件夹不存在则创建
			if  (!file .exists()  && !file .isDirectory()){
				//logger.info("//不存在");
				file.mkdirs();
			} else{
				System.out.println("//目录存在");
			}
			file = new File(path + saveFileName);
			if(!file.exists() || isReplaceFile){
				//如果文件不存在，则下载
				try {
					OutputStream os = new FileOutputStream(file);
					InputStream is = response.getEntity().getContent();
					byte[] buff = new byte[(int) response.getEntity().getContentLength()];
					while(true) {
						int readed = is.read(buff);
						if(readed == -1) {
							break;
						}
						byte[] temp = new byte[readed];
						System.arraycopy(buff, 0, temp, 0, readed);
						os.write(temp);
						System.out.println("文件下载中....");
					}
					is.close();
					os.close();
					System.out.println(fileURL + "--文件成功下载至" + path + saveFileName);

				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else{
				System.out.println(path);
				System.out.println("该文件存在！");
			}
			request.releaseConnection();
		} catch(IllegalArgumentException e){
			System.out.println("连接超时...");

		} catch(Exception e1){
			e1.printStackTrace();
		}
	}
	/**
	 * 有bug 慎用
	 * unicode转化String
	 * @return
     */
	public static String decodeUnicode(String dataStr) {
		int start = 0;
		int end = 0;
		final StringBuffer buffer = new StringBuffer();
		while (start > -1) {
			start = dataStr.indexOf("\\u", start - (6 - 1));
			if (start == -1){
				break;
			}
			start = start + 2;
			end = start + 4;
			String tempStr = dataStr.substring(start, end);
			String charStr = "";
			charStr = dataStr.substring(start, end);
			char letter = (char) Integer.parseInt(charStr, 16); // 16进制parse整形字符串。
			dataStr = dataStr.replace("\\u" + tempStr, letter + "");
			start = end;
		}
		System.out.println(dataStr);
		return dataStr;
	}
}
