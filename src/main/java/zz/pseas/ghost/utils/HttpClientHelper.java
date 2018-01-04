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
package zz.pseas.ghost.utils;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @date 2016年12月15日 下午10:29:21
 * @version
 * @since JDK 1.8
 */
public class HttpClientHelper {
	private static Logger log = LoggerFactory.getLogger(HttpClientHelper.class);
	private HttpClient httpclient = new DefaultHttpClient();
	private HttpContext localContext = new BasicHttpContext();
	// cookie存储用来完成登录后记录相关信息
	private BasicCookieStore basicCookieStore = new BasicCookieStore();
	// 连接超时时间10秒
	private int TIME_OUT = 10;

	public HttpClientHelper() {
		instance();
	}

	/**
	 * boolean=true支持https,false同默认构造
	 */
	public HttpClientHelper(boolean ssl) {
		instance();
		if (ssl) {
			try {
				X509TrustManager tm = new X509TrustManager() {

					public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
					}

					public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
					}

					public X509Certificate[] getAcceptedIssuers() {
						return null;
					}
				};

				SSLContext ctx = SSLContext.getInstance("TLS");
				ctx.init(null, new TrustManager[] { tm }, null);
				SSLSocketFactory ssf = new SSLSocketFactory(ctx);
				ClientConnectionManager ccm = httpclient.getConnectionManager();
				SchemeRegistry sr = ccm.getSchemeRegistry();
				sr.register(new Scheme("https", ssf, 443));
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
	}

	/**
	 * 启用cookie存储
	 */
	private void instance() {
		httpclient.getParams().setIntParameter("http.socket.timeout", TIME_OUT * 1000);
		// Cookie存储
		localContext.setAttribute("http.cookie-store", basicCookieStore);
	}

	public HttpResult get(String url, Header... headers) {
		HttpGet httpGet = new HttpGet(url);
		if (headers != null) {
			for (Header header : headers) {
				httpGet.addHeader(header);
			}
		} else {// 如不指定则使用默认
			Header header = new BasicHeader("User-Agent",
					"Mozilla/4.0 (compatible; MSIE 7.0; Windows NT 5.1;  .NET CLR 2.0.50727; .NET CLR 3.0.04506.648; .NET CLR 3.5.21022; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729; InfoPath.2)");
			httpGet.addHeader(header);
		}

		HttpResult httpResult = HttpResult.empty();
		try {
			HttpResponse httpResponse = httpclient.execute(httpGet, localContext);
			httpResult = new HttpResult(localContext, httpResponse);
		} catch (IOException e) {
			log.error(e.getMessage());
			httpGet.abort();
		}
		return httpResult;
	}

	public HttpResult post(String url, Map<String, String> data, Header... headers) {
		HttpPost httpPost = new HttpPost(url);

		String contentType = null;
		if (headers != null) {
			for (int i = 0; i < headers.length; ++i) {
				Header header = (Header) headers[i];
				if (!(header.getName().startsWith("$x-param"))) {
					httpPost.addHeader(header);
				}
				if ("Content-Type".equalsIgnoreCase(header.getName())) {
					contentType = header.getValue();
				}
			}
		}

		if (contentType != null) {
			httpPost.setHeader("Content-Type", contentType);
		} else if (data != null) {
			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
		}

		List<NameValuePair> formParams = new ArrayList<NameValuePair>();
		for (String key : data.keySet()) {
			formParams.add(new BasicNameValuePair(key, (String) data.get(key)));
		}

		HttpResult httpResult = HttpResult.empty();
		try {
			UrlEncodedFormEntity entity = new UrlEncodedFormEntity(formParams, "UTF-8");
			httpPost.setEntity(entity);
			HttpResponse response = httpclient.execute(httpPost, localContext);
			httpResult = new HttpResult(localContext, response);
		} catch (IOException e) {
			log.error(e.getMessage());
			httpPost.abort();
		}
		return httpResult;
	}

	public String getCookie(String name, String... domain) {
		String dm = "";
		if (domain != null && domain.length >= 1) {
			dm = domain[0];
		}
		for (Cookie c : basicCookieStore.getCookies()) {
			if (StringUtils.equals(name, c.getName()) && StringUtils.equals(dm, c.getDomain())) {
				return c.getValue();
			}
		}
		return null;
	}

	public void pringCookieAll() {
		for (Cookie c : basicCookieStore.getCookies()) {
			System.out.println(c);
		}
	}
}