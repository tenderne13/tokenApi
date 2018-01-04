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

import java.util.Date;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class MessageProcess {
	private String JSONResult;

	MessageProcess(String result) {
		JSONResult = result;
	}

	public Message getMessage() {
		JSONObject json = new JSONObject(JSONResult);
		if (json.has("result")) {
			JSONArray jarray = json.getJSONArray("result");
			JSONObject p = jarray.getJSONObject(0);
			String type = p.getString("poll_type");
			if (type.indexOf("group_message") >= 0) {
				return parseGroupMassage(JSONResult);

			} else
				return parseMessage(JSONResult);
		}
		return null;
	}

	private Message parseMessage(String content) {
		JSONObject json = new JSONObject(JSONResult);

		JSONArray jarray = json.getJSONArray("result");
		JSONObject p = jarray.getJSONObject(0);
		String type = p.getString("poll_type");
		JSONObject va = p.getJSONObject("value");
		JSONArray contentArray = va.getJSONArray("content");
		Object Messagecontent = contentArray.get(1);
		Object from_uin = va.get("from_uin");
		Object to_uin = va.get("to_uin");
		Message m = new Message();
		m.type = type;
		m.content = Messagecontent.toString();
		m.fromUin = from_uin.toString();
		m.toUin = to_uin.toString();
		m.time = new Date().toString();
		return m;

	}

	private Message parseGroupMassage(String content) {
		JSONObject json = new JSONObject(JSONResult);
		System.out.println(content);
		JSONArray jarray = json.getJSONArray("result");
		JSONObject p = jarray.getJSONObject(0);
		String type = p.getString("poll_type");
		JSONObject va = p.getJSONObject("value");
		JSONArray contentArray = va.getJSONArray("content");
		Object Messagecontent = contentArray.get(1);
		Object from_uin = va.get("from_uin");
		Object to_uin = va.get("to_uin");
		Object sendUin = va.get("send_uin");
		Message m = new Message();
		m.type = type;
		m.content = Messagecontent.toString();
		m.groupCode = from_uin.toString();
		m.toUin = to_uin.toString();
		m.time = new Date().toString();
		m.fromUin = sendUin.toString();
		return m;

	}
}
