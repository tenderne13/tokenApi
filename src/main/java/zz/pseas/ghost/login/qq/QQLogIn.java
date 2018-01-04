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
package zz.pseas.ghost.login.qq;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import zz.pseas.ghost.login.qq.entity.HttpService;
import zz.pseas.ghost.login.qq.util.GetMIMA;
import zz.pseas.ghost.login.qq.util.RegexUtil;
import zz.pseas.ghost.login.qq.util.Security;
import zz.pseas.ghost.login.qq.util.Util;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Random;
import java.util.Scanner;

/**
* @date 2016年12月15日 下午10:36:34 
* @version   
* @since JDK 1.8  
*/
public class QQLogIn extends HttpService{
	/** QQ帐号 **/
	private String uin;
	/** QQ密码 **/
	private String password;
	/** QQ-ID **/
	private String aid;
	/** 登录验证字符串 **/
	private String login_sig;
	/** 登录状态 0=登录成功，否则失败**/
	private int status;
	/** 验证码 **/
	private String verify;
	/** 随机数 **/
	private String random;
	/** QQ空间请求的G_TK**/
	protected String g_tk;
	
	public QQLogIn(){}
	
	public QQLogIn(String uin,String password){
		this.uin = uin;
		this.password = password;
	}
	
	/**
	 * QQ登录
	 * 
	 * 前提需要数据
	 *      1. uin       QQ号
	 *      2. password  QQ密码
	 *      
	 * 局限：
	 *      暂时不支持输入验证码登录
	 *      
	 * 
	 * 1.进入登录页面
	 * 2.在该页面截取     1.aid 2.js_ver 3.login_sig
	 * 3.获取登录验证信息
	 * 4.登录
	 * 
	 * @return 是否登录成功  登录成功=0  否则=-1
	 * 
	 * @see util.RegexUtil    解析网页字符串
	 * @see util.Util         用于填充字符串工具
	 * @see util.Security  QQ密码加密
	 * 
	 */
	public int login(){
        //1.进入登录页面,获取Cookie
        //http://ui.ptlogin2.qq.com/cgi-bin/login?hide_title_bar=1&low_login=0&qlogin_auto_login=1&no_verifyimg=1&link_target=blank&appid=549000912&style=12&target=self&s_url=http%3A//qzs.qq.com/qzone/v5/loginsucc.html?para=izone&pt_qr_app=%CA%D6%BB%FAQQ%BF%D5%BC%E4&pt_qr_link=http%3A//z.qzone.com/download.html&self_regurl=http%3A//qzs.qq.com/qzone/v6/reg/index.html&pt_qr_help_link=http%3A//z.qzone.com/download.html
		String $a = "http://ui.ptlogin2.qq.com/cgi-bin/login?hide_title_bar=1&low_login=0&qlogin_auto_login=1&no_verifyimg=1&link_target=blank&appid=549000912&style=12&target=self&s_url=http%3A//qzs.qq.com/qzone/v5/loginsucc.html?para=izone&pt_qr_app=%CA%D6%BB%FAQQ%BF%D5%BC%E4&pt_qr_link=http%3A//z.qzone.com/download.html&self_regurl=http%3A//qzs.qq.com/qzone/v6/reg/index.html&pt_qr_help_link=http%3A//z.qzone.com/download.html";
		String $v = this.requestGet($a);
		String content = $v;
		
		//2.在该页面截取  1.aid 2.js_ver 3.login_sig
//        $v = RegexUtil.replaceStartEnd($v, "login_sig", "clientip");
//        $v = RegexUtil.replaceStartEnd($v, "\"", "\"");
        
        //从head中取得 login_sig  
		$v=RegexUtil.replaceStartEnd(this.cookie, "pt_login_sig=", ";");
		String $login_sig = $v;
		System.out.println("login_sig:"+$login_sig);
		
        $v = content;
        $v = RegexUtil.replaceStartEnd($v, "appid:", "lang");
        $v = RegexUtil.replaceStartEnd($v, "\"", "\"");
        String $aid = $v;
        String $uin = this.uin;
        String $password = this.password;
        
        //http://check.ptlogin2.qq.com/check?regmaster=&pt_tea=1&uin=1098622525&appid=549000912&js_ver=10152&js_type=1&login_sig=puBm08m2LHEGW3B8O08sRx9eZCbXHv1QXAL8HBCXp5i6HopB*HphNGFetWjYT917&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.3449928464833647
//       String checkUrl="http://check.ptlogin2.qq.com/check?regmaster=&pt_tea=1&uin={0}&appid={1}&js_ver=10152&js_type=1&login_sig={2}&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.3449928464833647";
//        checkUrl=Util.fillString(checkUrl,$uin, $aid,$login_sig);
//       String $checkResult = this.requestGet(checkUrl);
        
        String $action = "3-6-"+new Date().getTime();

        //3.获取登录验证信息
//        http://check.ptlogin2.qq.com/check?regmaster=&uin=949102845&appid=549000912&js_ver=10051&js_type=1&login_sig=UcU**IJ7*Tb1oqFs9-NzQ7p187P4QhmPafwtJz5JE4zMXU1mnab0L5Z6uhEeSR4d&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r=0.6652378559988166
        String $url = "http://check.ptlogin2.qq.com/check?regmaster=&pt_tea=1&uin={0}&appid={1}&js_ver={2}&js_type=1&login_sig={3}&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&r={4}";
        $url = Util.fillString($url, $uin, $aid,"10152",$login_sig,new Random().nextDouble()+"");
        $v = this.requestGet($url);
        String $status = RegexUtil.replaceStartEnd($v, "'", "'");
        String $verify = RegexUtil.replaceStartEnd($v, ",'", "'");
        System.out.println("验证码得取:=============="+$verify);
        String $hexqq = RegexUtil.replaceStartEnd($v, ",'\\\\x", "'");
        System.out.println($hexqq);
        
        // 获取$verifysession_v1 
        String $verifysession_v1=RegexUtil.replaceStartEnd(this.cookie, "ptvfsession=", ";");
        System.out.println($verifysession_v1);
        //long currenTime=System.currentTimeMillis();
        //Date date=new Date(currenTime);
        String captchaImgName="tmp-qq-captcha.jpg";
		//如果有验证码，一定要在密码加密前搞定哟
		if($verify.length()>5){
			System.out.println("请进入"+Util.root+"\\verifyTemp\\"+captchaImgName+"查看验证码，且在控制台输入验证码→回车");
        	$url = "http://captcha.qq.com/getimage?uin={0}&aid={1}&{2}";
        	$url = Util.fillString($url, $uin, $aid, new Random().nextDouble()+"");
        	
        	this.requestDownload($url, Util.root+"\\verifyTemp\\"+captchaImgName);
        	//不解释，控制台输入验证码，话又说回来，如果使用B/S架构，这里还要重构。。额，好吧。。。
    		Scanner scanner = new Scanner(System.in);
    		$verify = scanner.nextLine();
        }
        //获取加密后的密码    加密算法更改
//		$password = Security.GetPassword($hexqq,$password,$verify);
//        $password="w2G25oGjyW9Sq2FhaIzD*baOgbV00nGFZh-Ptw1n51KYo*ka1H9mBMBfEWe9vbG1srUcpmA*9D*F6ni-9kaZxXJ7ugtr4kdpMjgl4xbO1cYkIUmawe8pSi4px1A2u7*HObKFdHLnak1Quibw-EZhw4Yh5EwhtkafNRwJOqKhpDPekA0HWkSpgWDzmvV*CAvqMpmX-n*cqL9l4lxpEnIjgQ__";
		try {
			$hexqq= Security.hexchar2bin($hexqq.replace("\\x", "").toUpperCase());
			$password=GetMIMA.password($hexqq, $password, $verify);
		} catch (FailingHttpStatusCodeException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
        //4.登录
        //URL=http://ptlogin2.qq.com/login?u=8888888&p=3E8CDBE584C125C4A0E31CB3A273FA20&verifycode=zkyy&aid=549000912&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&h=1&ptredirect=0&ptlang=2052&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action=23-61-1383187338922&mibao_css=&t=1&g=1&js_ver=10051&js_type=1&login_sig=nO84d8jFFX2BsoUJjCz2Or3qHRlCB6DsLq5r*eLHFZ3yfd5lqugnE9H4d6xkEMWI&pt_rsa=0
        $url = "http://ptlogin2.qq.com/login?u={0}&p={1}&verifycode={2}&aid={3}&u1=http%3A%2F%2Fqzs.qq.com%2Fqzone%2Fv5%2Floginsucc.html%3Fpara%3Dizone&h=1&ptredirect=0&ptlang=2052&from_ui=1&dumy=&low_login_enable=0&regmaster=&fp=loginerroralert&action={4}&mibao_css=&t=1&g=1&js_ver={5}&js_type=1&login_sig={6}&pt_uistyle=12&pt_randsalt=0&pt_vcode_v1=0&pt_verifysession_v1={7}";
        $url = Util.fillString($url, $uin, $password, $verify, $aid, $action,"10152", $login_sig,$verifysession_v1);
        System.out.println($url);
    	$v = this.requestGet($url);
    	
    	$status = RegexUtil.replaceStartEnd($v, "'", "'");
    	System.out.println($v);//打印看下是否搞定了
    	
    	//5.矫正QQ的Cookie，只提取关键需要Cookie
		String cookie = this.cookie;
		$v = "uin=o0"+this.uin+"; "+("skey="+RegexUtil.replaceStartEnd(cookie, "skey=", ";")+"; ptcz="+RegexUtil.replaceStartEnd(cookie, "ptcz=", ";")+";");
		this.setCookie($v);
    	
		//设置QQ空间的g_tk
		this.g_tk = Security.GetG_TK( RegexUtil.replaceStartEnd(this.getCookie(), "skey=", ";") );
		
		//6.设置登录状态
		if("0".equals($status))
    		this.status = 0;
    	else
    		this.status = -1;
		
		return this.status;
	}
	
	/**
	 * 通过说说ID
	 * 赞一个说说
	 * 
	 * 1.需要登录状态 g_tk
	 * 2.发送1次http请求
	 * 
	 * @param curkey 说说ID  example:7b6eb61aa5616f52da7c0b00
	 * 
	 */
	public void praise(String curkey){
		if(this.status != 0) return;
		String $url = "http://w.qzone.qq.com/cgi-bin/likes/internal_dolike_app?g_tk="+this.g_tk;
		String $data = "fid={0}&opuin={1}&abstime=1383191804&active=0&appid=311&curkey=http://user.qzone.qq.com/448163451/mood/{0}&qzreferrer=http://user.qzone.qq.com/{1}&typeid=0&unikey=http://user.qzone.qq.com/448163451/mood/{0}";
		
		$data = Util.fillString($data, curkey, this.getUin());
		this.requestPost($url, $data);
	}
	
	public String getUin() {
		return uin;
	}

	public void setUin(String uin) {
		this.uin = uin;
	}


	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getAid() {
		return aid;
	}

	public void setAid(String aid) {
		this.aid = aid;
	}

	public String getLogin_sig() {
		return login_sig;
	}

	public void setLogin_sig(String login_sig) {
		this.login_sig = login_sig;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getVerify() {
		return verify;
	}

	public void setVerify(String verify) {
		this.verify = verify;
	}

	public String getRandom() {
		return random;
	}

	public void setRandom(String random) {
		this.random = random;
	}

	public String getCookie() {
		return cookie;
	}

	public void setCookie(String cookie) {
		this.cookie = cookie;
	}

	public String getG_tk() {
		return g_tk;
	}

	public void setG_tk(String g_tk) {
		this.g_tk = g_tk;
	}
	
	
}
