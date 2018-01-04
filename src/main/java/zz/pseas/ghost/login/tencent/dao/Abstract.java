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

import zz.pseas.ghost.login.tencent.Cookie;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class Abstract {
    String url;
    String t = "1473083159323",tuin , vfwebqq;
    String Host = "s.web2.qq.com";
    String Referer = "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1";
    String User_Agent = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10.12; rv:48.0) Gecko/20100101 Firefox/48.0";
    Cookie cookie;
    
    Abstract(Cookie cookie) {
        this.cookie = cookie;
        vfwebqq = cookie.get("vfwebqq");
    }
}
