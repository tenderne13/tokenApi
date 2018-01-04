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
package zz.pseas.ghost.login.tencent;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class MessageSender {
	private Cookie cookie;
	private static String URLPERSON = "http://d1.web2.qq.com/channel/send_buddy_msg2";
	private static String URLGROUP = "http://d1.web2.qq.com/channel/send_qun_msg2";
	private static String REFERER = "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2";
	private static String ORIGIN = "http://s.web2.qq.com";
	private static String HOST = "s.web2.qq.com";
	private static String AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:47.0) Gecko/20100101 Firefox/47.0";

	MessageSender(Cookie cookie) {
		this.cookie = cookie;
	}

	public void sendMessage(String toUin, String content, String type) {
		Sender sender = new Sender(toUin, content, type);
		Thread thread = new Thread(sender);
		thread.start();
	}

	class Sender implements Runnable {
		String toUin, content, type;

		Sender(String toUin, String content, String type) {
			this.toUin = toUin;
			this.content = content;
			this.type = type;
		}

		public void run() {
			String param;
			String url;
			String psessionid = cookie.get("psessionid");
			String groupParam = "r={\"group_uin\":" + toUin + ",\"content\":\"[\\\"" + content
					+ "\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":645,\"clientid\": "
					+ 53999199 + ",\"msg_id\":" + 33000001 + ",\"psessionid\":\"" + psessionid + "\"}";
			String personParam = "r={\"to\":" + toUin + ",\"content\":\"[\\\"" + content
					+ "\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":645,\"clientid\": "
					+ 53999199 + ",\"msg_id\":" + 33000001 + ",\"psessionid\":\"" + psessionid + "\"}";
			if (type.indexOf("group") >= 0) {
				param = groupParam;
				url = URLGROUP;
			} else {
				param = personParam;
				url = URLPERSON;
			}

			try {
				URL u = new URL(url);
				URLConnection con = u.openConnection();
				HttpURLConnection http = (HttpURLConnection) con;

				http.setRequestProperty("Cookie", cookie.toString());
				http.setRequestProperty("Origin", ORIGIN);
				http.setRequestProperty("Host", HOST);
				http.setRequestProperty("User-Agent", AGENT);
				http.setRequestProperty("Referer", REFERER);

				http.setDoInput(true);
				http.setDoOutput(true);
				http.setRequestMethod("POST");

				BufferedWriter output = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
				// System.out.println(param);
				output.write(param);
				output.flush();
				output.close();

				BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
				StringBuffer sb = new StringBuffer();
				while (input.ready()) {
					String line = input.readLine();
					sb.append(line + "\n");
				}
				input.close();
				http.disconnect();
				String result = sb.toString();
				System.out.println(result);

			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}
