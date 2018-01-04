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
import java.util.List;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class GetGroupList {
	Cookie cookie;
	JSONObject responsedJson;

	public GetGroupList(Cookie cookie) {
		this.cookie = cookie;
	}

	private void getHtmlRespon() {
		String rawUrl = "http://s.web2.qq.com/api/get_group_name_list_mask2";
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
	} // getHtmlRespon

	public List<Group> getGroupList() {
		List<Group> grouplist = new ArrayList();
		getHtmlRespon();
		int returnCode = responsedJson.getInt("retcode");
		if (returnCode != 0)
			return null;

		JSONObject result = responsedJson.getJSONObject("result");

		JSONArray gmasklist = result.getJSONArray("gmasklist");
		JSONArray gnamelist = result.getJSONArray("gnamelist");
		JSONArray gmarklist = result.getJSONArray("gmarklist");

		return grouplist;

	}

}
