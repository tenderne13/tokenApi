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
package zz.pseas.ghost.login.baidu;

import javax.swing.*;
import java.io.IOException;

/**
* @date 2016年9月14日 下午9:26:00 
* @version   
* @since JDK 1.8  
*/
public class BaiduLogInTest {

	public static void main(String[] args) {
		String testName = JOptionPane.showInputDialog("请输入用户名");
		String passWord = JOptionPane.showInputDialog("请输入密码");
		try {
			String bdCookies = BaiduLogin.getBaiduCookie(testName, passWord);
			System.out.println("baidu-cookies:");
			System.out.println(bdCookies);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
