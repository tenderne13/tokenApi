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

import com.jhlabs.image.ScaleFilter;
import zz.pseas.ghost.captcha.svm.svm_predict;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;

/**   
* @date 2016年9月14日 下午9:26:00 
* @version   
* @since JDK 1.8  
*/
public class ImagePreProcess5 {

	public static int isBlack(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() <= 300) {
			return 1;
		}
		return 0;
	}

	public static int isWhite(int colorInt) {
		Color color = new Color(colorInt);
		if (color.getRed() + color.getGreen() + color.getBlue() > 300) {
			return 1;
		}
		return 0;
	}

	public static void getSvmdata() throws Exception {
		File dir = new File("resources\\train5");
		File dataFile = new File("resources\\train5\\data.txt");
		FileOutputStream fs = new FileOutputStream(dataFile);
		File[] files = dir.listFiles();
		for (File file : files) {
			if (!file.getName().endsWith(".jpg"))
				continue;
			BufferedImage imgdest = ImageIO.read(file);
			fs.write((file.getName().charAt(0) + " ").getBytes());
			int index = 1;
			for (int x = 0; x < imgdest.getWidth(); ++x) {
				for (int y = 0; y < imgdest.getHeight(); ++y) {
					fs.write((index++ + ":" + isBlack(imgdest.getRGB(x, y)) + " ")
							.getBytes());
				}
			}
			fs.write("\r\n".getBytes());
		}
		fs.close();
	}

	public static void scaleTraindata() throws Exception {
		File dir = new File("resources\\temp5");
		File dataFile = new File("resources\\train5\\data.txt");
		FileOutputStream fs = new FileOutputStream(dataFile);
		File[] files = dir.listFiles();
		for (File file : files) {
			BufferedImage img = ImageIO.read(file);
			ScaleFilter sf = new ScaleFilter(16, 16);
			BufferedImage imgdest = new BufferedImage(16, 16, img.getType());
			imgdest = sf.filter(img, imgdest);
			ImageIO.write(imgdest, "JPG", new File("resources\\train5\\" + file.getName()));
			fs.write((file.getName().charAt(0) + " ").getBytes());
			int index = 1;
			for (int x = 0; x < imgdest.getWidth(); ++x) {
				for (int y = 0; y < imgdest.getHeight(); ++y) {
					fs.write((index++ + ":" + isBlack(imgdest.getRGB(x, y)))
							.getBytes());
				}
			}
			fs.write("\r\n".getBytes());
		}
		fs.close();

	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		// scaleTraindata();
		// getSvmdata();
		// svm_train train = new svm_train();
		// train.run(new String[]{new
		// File("train5\\data.txt").getAbsolutePath()});
		svm_predict.main(new String[] {
				new File("resources\\train5\\data.txt").getAbsolutePath(),
				new File("resources\\train5\\data.txt.model").getAbsolutePath(),
				"resources\\train5\\output.txt" });
	}
}
