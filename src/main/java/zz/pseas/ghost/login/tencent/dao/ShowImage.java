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

import javax.swing.*;
import java.awt.*;

/**
 * @date 2016年11月30日 下午12:03:21
 * @version
 * @since JDK 1.8
 */
public class ShowImage extends JFrame{
    /**  
	 * serialVersionUID:TODO(用一句话描述这个变量表示什么).  
	 * @since JDK 1.8  
	 */
	private static final long serialVersionUID = 1L;
	int  height = 300, width = 300;

	public ShowImage (String Path) {
        super();
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(d.width / 2 - width /2 , d.height / 2 - height / 2, width, height);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ImageIcon ic = new ImageIcon(Path);
        JPanel panel = new JPanel();
        JLabel label = new JLabel();
        label.setIcon(ic);
        panel.add(label);
        
        setContentPane(panel);
        setVisible(true);
    }
    public static void main(String ...strings) {
        ShowImage s = new ShowImage("./QR.jpg");
        try {
            Thread.sleep(7000 * 10);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        s.setVisible(false);
    }
}
