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

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class GetFriendList {
	Cookie cookie;
	JSONObject responsedJson;

	public GetFriendList(Cookie cookie) {
		this.cookie = cookie;
	}

	//
	private void getHtmlRespon() {
		String rawUrl = "http://s.web2.qq.com/api/get_user_friends2";
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";

		try {
			URL u = new URL(rawUrl);
			URLConnection con = u.openConnection();

			HttpURLConnection http = (HttpURLConnection) con;

			http.setRequestProperty("cookie", cookie.toString());

			if (referer != null)
				http.setRequestProperty("Referer", referer);

			http.setRequestProperty("Origin", "http://s.web2.qq.com");
			http.setRequestProperty("Host", "s.web2.qq.com");
			http.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:47.0) Gecko/20100101 Firefox/47.0");

			http.setDoInput(true);
			http.setDoOutput(true);
			http.setRequestMethod("POST");

			String ptwebqq = cookie.get("ptwebqq");
			String psessionid = cookie.get("psessionid");

			String r = "r={\"ptwebqq\":\"" + ptwebqq + "\",\"clientid\":" + 53999199 + ",\"psessionid\":\"" + psessionid
					+ "\",\"key\":\"\"}";

			long uin = Long.parseLong(cookie.get("uin").replace("o", ""));
			String param = "r={\"vfwebqq\":\"" + cookie.get("vfwebqq") + "\",\"hash\":\""
					+ HashCode.gethashCode(uin, ptwebqq) + "\"}";

			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));

			System.out.println(param);
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

			responsedJson = new JSONObject(result);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}// getHtmlRespon

	public List<Contact> getFriendList() {
		List<Contact> contacts = new ArrayList();
		this.getHtmlRespon();
		if (responsedJson == null)
			return null;

		int resultCode = responsedJson.getInt("retcode");

		if (resultCode == 0) {
			JSONObject result = responsedJson.getJSONObject("result");
			JSONArray friends = result.getJSONArray("friends");
			JSONArray marknames = result.getJSONArray("marknames");
			JSONArray categories = result.getJSONArray("categories");
			JSONArray vipinfo = result.getJSONArray("vipinfo");
			JSONArray info = result.getJSONArray("info");

			Map<String, String> map_cate = new HashMap();
			Map<String, String> map_vip = new HashMap();
			Map<String, String> map_mark = new HashMap();
			Map<String, String> map_nick = new HashMap();
			Map<String, String> map_isvip = new HashMap();

			for (int i = 0; i < marknames.length(); i++) {
				String uin = ((Object) marknames.getJSONObject(i).get("uin")).toString();
				String markname = (String) marknames.getJSONObject(i).get("markname");
				map_mark.put(uin, markname);
			}

			for (int i = 0; i < categories.length(); i++) {
				String index = (String) categories.getJSONObject(i).get("index");
				String name = (String) categories.getJSONObject(i).get("name");
				map_cate.put(index, name);
			}

			for (int i = 0; i < vipinfo.length(); i++) {
				String u = (String) vipinfo.getJSONObject(i).get("u");
				String viplevel = (String) vipinfo.getJSONObject(i).get("vip_level");
				String isvip = (String) vipinfo.getJSONObject(i).get("is_vip");
				map_vip.put(u, viplevel);
				map_isvip.put(u, isvip);
			}

			for (int i = 0; i < info.length(); i++) {
				String uin = (String) info.getJSONObject(i).get("index");
				String nick = (String) info.getJSONObject(i).get("name");
				map_cate.put(uin, nick);
			}

			for (int i = 0; i < friends.length(); i++) {
				Contact contact = new Contact();
				contact.uin = (String) friends.getJSONObject(i).get("uin");
				contact.category = (String) friends.getJSONObject(i).get("categories");
				contact.isVip = map_isvip.get(contact.uin);
				contact.markName = map_mark.get(contact.uin);
				contact.nickName = map_nick.get(contact.uin);
				contact.vipLevel = map_vip.get(contact.uin);

				// contact.QQ = ;

				contacts.add(contact);
			}

			return contacts;

		} // if
		return null;
	}

	public static void main(String... strings) {

		StringBuilder sb = new StringBuilder();
		BufferedReader input = null;
		try {
			input = new BufferedReader(new InputStreamReader(new FileInputStream("./data.txt"), "utf-8"));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			while (input.ready()) {
				sb.append(input.readLine());
				sb.append('\n');
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(sb.toString());
		JSONObject resultJSON = new JSONObject(sb.toString());

		int resultCode = resultJSON.getInt("retcode");

		if (resultCode == 0) {
			JSONObject result = resultJSON.getJSONObject("result");
			JSONArray friends = result.getJSONArray("friends");

			JSONObject fri = friends.getJSONObject(0);
			Object flag = fri.get("flag");
			Object uin = fri.get("uin");
			Object categories = fri.get("categories");

			System.out.println(flag + " : " + uin + " : " + categories);

		} // if

	}
}
