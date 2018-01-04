package zz.pseas.ghost.login.qq.interfaced;

import java.net.HttpURLConnection;
import java.net.ProtocolException;
/**   
* @date 2016年12月15日 下午10:36:34 
* @version   
* @since JDK 1.8  
*/

/**
 * <p>提供在请求前后，设置请求参数的接/p>
 * @category  类名
 * @since     1.0
 * 
 */
public interface SetHttpConnection{
	public String before(HttpURLConnection httpConn) throws ProtocolException;
	public String after(HttpURLConnection httpConn);
}