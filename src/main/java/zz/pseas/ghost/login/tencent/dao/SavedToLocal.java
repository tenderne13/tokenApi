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
package zz.pseas.ghost.login.tencent.dao;

import zz.pseas.ghost.login.tencent.Message;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class SavedToLocal {
	Message msg;
	String databaseName = "Message.db";

	public void save(Message m) {
		System.out.println(
				"fromuin :" + m.fromUin + " touin : " + m.toUin + " content : " + m.content + " time : " + m.time);

		if (m.type.indexOf("group") >= 0)
			saveGroupMessage(m);
		else
			saveMessage(m);
	}

	private void saveGroupMessage(Message m) {
		AbstractDataBase database = new Sqlite();
		database.connect();
		database.creatDB(m.toUin + ".db");
		String createtable = "CREATE TABLE GROUPMESSAGE " + "(GROUPCODE TEXT NOT NULL," + "FROMUIN TEXT NOT NULL,"
				+ "TOUIN TEXT NOT NULL," + "CONTENT TEXT NOT NULL," + "TIME TEXT NOT NULL)";
		database.executeUpdata(createtable);

		String insertdata = "INSERT INTO GROUPMESSAGE (GROUPCODE, FROMUIN, TOUIN, CONTENT, TIME)" + "VALUES('"
				+ m.groupCode + "','" + m.fromUin + "','" + m.toUin + "','" + m.content + "','" + m.time + "')";
		System.out.println("inser: " + insertdata);
		database.executeUpdata(insertdata);

		database.close();

	}

	private void saveMessage(Message m) {
		AbstractDataBase database = new Sqlite();
		database.connect();
		database.creatDB(m.toUin + ".db");
		String createtable = "CREATE TABLE MESSAGE " + "(FROMUIN TEXT NOT NULL," + "TOUIN TEXT NOT NULL,"
				+ "CONTENT TEXT NOT NULL," + "TIME TEXT NOT NULL)";
		database.executeUpdata(createtable);

		String insertdata = "INSERT INTO  MESSAGE(FROMUIN, TOUIN, CONTENT, TIME)" + "VALUES('" + m.fromUin + "','"
				+ m.toUin + "','" + m.content + "','" + m.time + "')";
		database.executeUpdata(insertdata);
		database.close();
	}
}
