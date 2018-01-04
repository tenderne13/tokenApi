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
package zz.pseas.ghost.captcha;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
* @date 2016年9月14日 下午9:26:00 
* @version   
* @since JDK 1.8  
*/
public class RandomNum {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		File file = new File("result.csv");
		FileOutputStream fs = new FileOutputStream(file);
		List<Integer> pailie = new ArrayList<Integer>();
		for (int i = 100001; i < 999999 + 1; i++) {
			pailie.add(i);
		}
		Random r = new Random();
		r.setSeed(System.currentTimeMillis());
		int datasize = pailie.size();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < datasize; i++) {
			int randint = r.nextInt(pailie.size());
			Integer data = pailie.get(randint);
			sb.append(data.toString() + "\n");
			pailie.remove(randint);
			if (i % 1000 == 0)
				System.out.println(i);
		}
		fs.write(sb.toString().getBytes());
		fs.close();
	}

}
