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

package zz.pseas.ghost.login.email;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import zz.pseas.ghost.utils.HttpClientHelper;
import zz.pseas.ghost.utils.HttpResult;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

/**
* @date 2017年1月16日 下午12:21:58 
* @version   
* @since JDK 1.8  
*/
public class Email163Login {
	public static final String sessionInit = "http://email.163.com";
	public static final String loginUrl = "https://mail.163.com/entry/cgi/ntesdoor?funcid=loginone&language=-1&passtype=1&iframe=1&product=mail163&from=web&df=email163&race=51_51_16_bj&module=&uid=qh4343222tuxizhen@163.com&style=-1&net=n&skinid=null";
	// 联系人地址
	public static final String contactListUrl = "http://mail.163.com/contacts/call.do?uid=qh4343222tuxizhen@163.com&sid={0}&from=webmail&cmd=newapi.getContacts&vcardver=3.0&ctype=all&attachinfos=yellowpage,frequentContacts&freContLim=20";

	public static String login(String account, String password) {
		HttpClientHelper httpClientHelper = new HttpClientHelper(true);

		// 目的是得到 csrfToken 类似
		HttpResult httpResult = httpClientHelper.get(sessionInit);

		// 拼装登录参数
		Map<String, String> data = new HashMap<String, String>();
		data.put("url2", "http://mail.163.com/errorpage/err_163.htm");
		data.put("savelogin", "0");
		data.put("username", account);
		data.put("password", password);

		// 点击登录
		httpResult = httpClientHelper.post(loginUrl, data, setHeader());
		// 点击登录返回内容
		System.out.println(httpResult.getHtml().trim());

		// 提取sid
		Document doc = Jsoup.parse(httpResult.getHtml());
		String sessionId = doc.select("script").html().split("=")[2];
		sessionId = sessionId.split("&")[0];
		System.out.println("sessionId:" + sessionId);

		// 测试网址
		data.clear();
		data.put("order", "[{\'field':'N',\'desc\':\'false\'}]");
		httpResult = httpClientHelper.post(MessageFormat.format(contactListUrl, sessionId), data, setQueryHeader(sessionId));

		// httpResult =
		// httpClientHelper.post("https://reg.163.com/account/accountInfo.jsp",
		// data, setQueryHeader(sessionId));
		// return Jsoup.parse(httpResult.getHtml()).select("li.clear").text();
		return httpResult.getHtml();
	}

	public static Header[] setHeader() {
		Header[] result = { new BasicHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"), new BasicHeader("Accept", "application/x-ms-application, image/jpeg, application/xaml+xml, image/gif, image/pjpeg, application/x-ms-xbap, */*"), new BasicHeader("Accept-Encoding", "gzip, deflate"), new BasicHeader("Accept-Language", "zh-CN"), new BasicHeader("Cache-Control", "no-cache"),
				new BasicHeader("Connection", "Keep-Alive"), new BasicHeader("Content-Type", "application/x-www-form-urlencoded"), new BasicHeader("Host", "mail.163.com"), new BasicHeader("Referer", "http://email.163.com") };
		return result;
	}

	public static Header[] setQueryHeader(String sessionId) {
		Header[] result = { new BasicHeader("User-Agent", "Mozilla/5.0 (compatible; MSIE 9.0; Windows NT 6.1; Trident/5.0)"), new BasicHeader("Accept", "text/javascript"), new BasicHeader("Accept-Encoding", "gzip, deflate"), new BasicHeader("Accept-Language", "zh-CN"), new BasicHeader("Cache-Control", "no-cache"), new BasicHeader("Connection", "Keep-Alive"), new BasicHeader("Content-Type", "application/x-www-form-urlencoded"),
				new BasicHeader("Host", "mail.163.com"), new BasicHeader("Referer", "http://mail.163.com/js6/main.jsp?sid=" + sessionId + "&df=email163") };
		return result;
	}
}