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

import zz.pseas.ghost.login.tencent.dao.GetFriendAccount;
import zz.pseas.ghost.login.tencent.dao.SavedToLocal;
import zz.pseas.ghost.login.tuling.TLAnswer;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class Manaer {
	public Cookie cookie;
	String cookieParamName[] = { "skey", "p_skey", "pt4_token", "uin", "p_uin", "pt2gguin", };

	Manaer(Cookie cookie) {
		this.cookie = cookie;
	}

	public void run() {
		// this.receiveUserFriends();
		new GetFriendList(cookie).getFriendList();

		// this.group_name_list();
		new GetGroupList(cookie).getGroupList();

		// this.getDiscusList();
		new GetDiscussList(cookie).getDiscussList();

		this.getSelfInfo();
		this.getOnlineBuddyes();
		this.getRecentList();

		while (true) {

			receiveMessage();

		}
	}

	public int receiveMessage() {
		System.err.println("Start Receive");
		String rawUrl = "http://d1.web2.qq.com/channel/poll2";
		String referer = "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2";
		try {
			URL u = new URL(rawUrl);
			URLConnection con = u.openConnection();
			HttpURLConnection http = (HttpURLConnection) con;

			http.setRequestProperty("cookie", cookie.toString());
			if (referer != null)
				http.setRequestProperty("Referer", referer);
			http.setRequestProperty("Origin", "http://d1.web2.qq.com");
			http.setRequestProperty("Host", "d1.web2.qq.com");
			http.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:47.0) Gecko/20100101 Firefox/47.0");
			http.setDoInput(true);
			http.setDoOutput(true);

			http.setRequestMethod("POST");

			String ptwebqq = cookie.get("ptwebqq");
			String psessionid = cookie.get("psessionid");
			String r = "r={\"ptwebqq\":\"" + ptwebqq + "\",\"clientid\":" + 53999199 + ",\"psessionid\":\"" + psessionid
					+ "\",\"key\":\"\"}";

			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
			output.write(r);
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
			System.err.println("Receive finish");

			System.err.println("Start Process");
			String result = sb.toString();
			System.out.println(result);
			Message m = new MessageProcess(result).getMessage();

			System.err.println("JSON finished");
			if (m != null)
				if (m.type.indexOf("group") < 0) {
					System.out.println("Receive PersonnalMessage");
					m.fromUin = new GetFriendAccount(cookie).getaccount(m.fromUin);
					this.OnReceivePersonalMessage(m);
				} else {
					System.out.println("Receive Group : Message");
					this.OnReceiveGroupMessage(m);
				}

			System.out.println("Process finish");

			System.out.println(result);
			if (result.indexOf("to_uin") >= 0) {
				return 1;
			} else
				return 0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public int receiveUserFriends() {
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
			System.out.println(result);
			if (result.indexOf("to_uin") >= 0) {
				return 1;
			} else
				return 0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public int group_name_list() {
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
			System.err.println("group List");
			System.out.println(result);
			if (result.indexOf("to_uin") >= 0) {
				return 1;
			} else
				return 0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public int getDiscusList() {
		String rawUrl = "http://s.web2.qq.com/api/get_discus_list?";
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
		String ptwebqq = cookie.get("ptwebqq");
		String psessionid = cookie.get("psessionid");
		rawUrl = rawUrl + "clientid=53999199&psessionid=" + psessionid + "&t=1472274729901&vfwebqq="
				+ cookie.get("vfwebqq");
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

			BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
			StringBuffer sb = new StringBuffer();
			while (input.ready()) {
				String line = input.readLine();
				sb.append(line + "\n");
			}

			input.close();
			http.disconnect();
			String result = sb.toString();
			System.err.println("Dicus List");
			System.out.println(result);
			if (result.indexOf("to_uin") >= 0) {
				return 1;
			} else
				return 0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public int getSelfInfo() {
		String rawUrl = "http://s.web2.qq.com/api/get_self_info2?t=1472274729906";
		String referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
		String ptwebqq = cookie.get("ptwebqq");
		String psessionid = cookie.get("psessionid");

		try {
			URL u = new URL(rawUrl);
			URLConnection con = u.openConnection();

			HttpURLConnection http = (HttpURLConnection) con;

			http.setRequestProperty("Cookie", cookie.toString());

			if (referer != null)
				http.setRequestProperty("Referer", referer);
			http.setRequestProperty("Origin", "http://s.web2.qq.com");
			http.setRequestProperty("Host", "s.web2.qq.com");
			http.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:47.0) Gecko/20100101 Firefox/47.0");

			BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
			StringBuffer sb = new StringBuffer();
			while (input.ready()) {
				String line = input.readLine();
				sb.append(line + "\n");
			}
			input.close();
			http.disconnect();
			String result = sb.toString();
			System.err.println("Dicus List");
			System.out.println(result);
			if (result.indexOf("to_uin") >= 0) {
				return 1;
			} else
				return 0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public int getOnlineBuddyes() {
		String rawUrl = "http://d1.web2.qq.com/channel/get_online_buddies2?";
		String referer = "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2";
		String ptwebqq = cookie.get("ptwebqq");
		String psessionid = cookie.get("psessionid");
		rawUrl = rawUrl + "clientid=53999199&psessionid=" + psessionid + "&t=1472274729901&vfwebqq="
				+ cookie.get("vfwebqq");
		try {
			URL u = new URL(rawUrl);
			URLConnection con = u.openConnection();

			HttpURLConnection http = (HttpURLConnection) con;

			http.setRequestProperty("cookie", cookie.toString());

			if (referer != null)
				http.setRequestProperty("Referer", referer);
			http.setRequestProperty("Origin", "http://d1.web2.qq.com");
			http.setRequestProperty("Host", "d1.web2.qq.com");
			http.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:47.0) Gecko/20100101 Firefox/47.0");
			BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
			StringBuffer sb = new StringBuffer();
			while (input.ready()) {
				String line = input.readLine();
				sb.append(line + "\n");
			}

			input.close();
			http.disconnect();
			String result = sb.toString();
			System.err.println("Online Buddy");
			System.out.println(result);
			if (result.indexOf("to_uin") >= 0) {
				return 1;
			} else
				return 0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public int getRecentList() {
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
			String r = "r={\"vfwebqq\":\"" + cookie.get("vfwebqq") + "\",\"clientid\":" + 53999199
					+ ",\"psessionid\":\"" + psessionid + "\",\"key\":\"\"}";

			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
			System.out.println(r);
			output.write(r);
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
			System.err.println("Rencent List");
			System.out.println(result);
			if (result.indexOf("to_uin") >= 0) {
				return 1;
			} else
				return 0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public String getFriendUin(String tuin) {
		String rawUrl = "http://s.web2.qq.com/api/get_friend_uin2?";
		String referer = "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2";
		String ptwebqq = cookie.get("ptwebqq");
		String psessionid = cookie.get("psessionid");
		rawUrl = rawUrl + "type=1&tuin=" + tuin + "&t=1472274729901&vfwebqq=" + cookie.get("vfwebqq");
		try {
			URL u = new URL(rawUrl);
			URLConnection con = u.openConnection();

			HttpURLConnection http = (HttpURLConnection) con;
			http.setRequestProperty("cookie", cookie.toString());

			if (referer != null)
				http.setRequestProperty("Referer", referer);
			http.setRequestProperty("Origin", "http://d1.web2.qq.com");
			http.setRequestProperty("Host", "d1.web2.qq.com");
			http.setRequestProperty("User-Agent",
					"Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:47.0) Gecko/20100101 Firefox/47.0");
			BufferedReader input = new BufferedReader(new InputStreamReader(http.getInputStream()));
			StringBuffer sb = new StringBuffer();
			while (input.ready()) {
				String line = input.readLine();
				sb.append(line + "\n");
			}

			input.close();
			http.disconnect();
			String result = sb.toString();
			System.err.println("Online Buddy");
			System.out.println(result);
			return result;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

	public int sendPersonMessage(String tuin, String content) {
		String rawUrl = "http://d1.web2.qq.com/channel/send_buddy_msg2";
		String referer = "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2";

		try {
			URL u = new URL(rawUrl);
			URLConnection con = u.openConnection();

			HttpURLConnection http = (HttpURLConnection) con;

			System.out.println(cookie.toString());
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
			String r = "r={\"to\":" + tuin + ",\"content\":\"[\\\"" + content
					+ "\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":645,\"clientid\": "
					+ 53999199 + ",\"msg_id\":" + 33000001
					+ ",\"psessionid\":\"8368046764001d636f6e6e7365727665725f77656271714031302e3133332e34312e383400001ad00000066b026e040015808a206d0000000a406172314338344a69526d0000002859185d94e66218548d1ecb1a12513c86126b3afb97a3c2955b1070324790733ddb059ab166de6857\"}";
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
			System.out.println(r);
			output.write(r);
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
			if (result.indexOf("ok") >= 0) {
				return 1;
			} else
				return 0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;
	}

	public int sendGroupMessage(String Group_uin, String content) {
		String rawUrl = "http://d1.web2.qq.com/channel/send_qun_msg2";
		String referer = "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2";
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
			int ms_id = new Random().nextInt(Integer.MAX_VALUE);
			String r = "r={\"group_uin\":" + Group_uin + ",\"content\":\"[\\\"" + content
					+ "\\\",[\\\"font\\\",{\\\"name\\\":\\\"宋体\\\",\\\"size\\\":10,\\\"style\\\":[0,0,0],\\\"color\\\":\\\"000000\\\"}]]\",\"face\":645,\"clientid\": "
					+ 53999199 + ",\"msg_id\":" + ms_id++
					+ ",\"psessionid\":\"8368046764001d636f6e6e7365727665725f77656271714031302e3133332e34312e383400001ad00000066b026e040015808a206d0000000a406172314338344a69526d0000002859185d94e66218548d1ecb1a12513c86126b3afb97a3c2955b1070324790733ddb059ab166de6857\"}";
			BufferedWriter output = new BufferedWriter(new OutputStreamWriter(http.getOutputStream()));
			System.out.println("Group Post :::" + r);
			output.write(r);
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
			if (result.indexOf("ok") >= 0) {
				return 1;
			} else
				return 0;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 0;

	}

	public void OnReceivePersonalMessage(Message m) {
		TLAnswer tLAnswer = new TLAnswer();
		String ans = tLAnswer.ask(m.content);
		SavedToLocal s = new SavedToLocal();
		s.save(m);
		// MessageSender sender = new MessageSender(cookie);
		// sender.sendMessage(m.fromUin, ans, m.type);
	}

	public void OnReceiveGroupMessage(Message m) {
		TLAnswer tLAnswer = new TLAnswer();
		String ans = tLAnswer.ask(m.content);
		SavedToLocal s = new SavedToLocal();
		s.save(m);
		// MessageSender sender = new MessageSender(cookie);
		// sender.sendMessage(m.fromUin, ans, m.type);
	}

	public String setcookie(Map<String, String> cookie, String[] name) {
		StringBuffer sb = new StringBuffer();
		for (Entry<String, String> entry : cookie.entrySet()) {
			// System.err.println("<cookie : " + entry.getKey() + " = " +
			// entry.getValue());
			sb.append(entry.getKey());
			sb.append("=");
			sb.append(entry.getValue());
			sb.append(";");
		}
		return sb.toString();
	}
}
