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

import java.sql.*;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class Sqlite extends AbstractDataBase {
	Connection con;
	Statement stmt;

	public void connect() {
		try {
			Class.forName("org.sqlite.JDBC");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void creatDB(String name) {
		try {
			con = DriverManager.getConnection("jdbc:sqlite:" + name);
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void selectDB(String name) {
		try {
			con = DriverManager.getConnection("jdbc:sqlite:" + name + ".db");
			stmt = con.createStatement();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void createTable(String name, String[] params, String[] type) {
		if (params.length != type.length) {

			System.out.println("params.length do not equal to type.length");
		} else {
			StringBuffer sb = new StringBuffer();

			sb.append("CREATE TABLE ");
			sb.append(name);
			sb.append("(");
			for (int i = 0; i < params.length; i++) {
				sb.append(params[i]);
				sb.append(" ");
				sb.append(type[i]);
				sb.append(",");
			}

			sb.append(");");
			try {
				stmt.executeUpdate(sb.toString());
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	public void executeUpdata(String sql) {
		try {
			stmt.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String sql) {

		try {
			return stmt.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public void commit() {
		try {
			con.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void close() {
		try {
			con.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String... strings) {
		AbstractDataBase database = new Sqlite();
		database.connect();
		database.creatDB("test.db");
		String sql = "CREATE TABLE PERSONALMESSAGE " + "(FROMUIN TEXT PRIMARY KEY    NOT NULL,"
				+ "TOUIN         TEXT           NOT NULL," + "CONTENT       TEXT           NOT NULL,"
				+ "TIME          TEXT           NOT NULL)";
		database.executeUpdata(sql);

		for (int i = 1000; i < 20000; i++) {
			sql = "INSERT INTO PERSONALMESSAGE (FROMUIN, TOUIN, CONTENT, TIME)" + "VALUES('" + i
					+ "','321','ASD','DSA')";
			database.executeUpdata(sql);
		}

		ResultSet rs = database.executeQuery("SELECT * FROM PERSONALMESSAGE");
		try {
			while (rs.next()) {
				String fromuin = rs.getString("fromuin");
				String touin = rs.getString("touin");
				String content = rs.getString("content");
				String time = rs.getString("time");
				System.out.println(
						"fromuin : " + fromuin + "  touin : " + touin + " content : " + content + "  time :" + time);

			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		database.close();
	}
}
