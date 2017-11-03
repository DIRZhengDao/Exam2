package com.cmsz;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashSet;

/**
 * 日志分析的简单样例，请自己重新实现
 */
public class AnalyseSample implements Analyse {


	@Override
	public void doAnalyse(String path) {
		File[] logFiles = new File(path).listFiles(new FilenameFilter() {
			
			@Override
			public boolean accept(File dir, String name) {
				return name.endsWith(".log");
			}
		});
		try (BufferedReader reader0 = new BufferedReader(new FileReader(logFiles[0]));
			 BufferedReader reader1 = new BufferedReader(new FileReader(logFiles[1]));

		) {
			String tep_receive=null;
			String tep_send=null;
			HashSet<String> receive_set=new HashSet<String>();
			HashSet<String> send_set=new HashSet<String>();
			HashSet<String> fail=new HashSet<String>();
			HashSet<String> interSet = new HashSet<String>();

			/**区分状态send和fail*/
			while ((tep_send=reader1.readLine())!=null){
				String[] test=tep_send.split("#Serial:|#STATUS:");
				if (test[2].equals("SEND")) {
					send_set.add(test[1]);
				}else {
					fail.add(test[1]);
				}
			}

			/**区分状态receive和fail*/
			while ((tep_receive=reader0.readLine())!=null){
				String[] test=tep_receive.split("#Serial:|#STATUS:");
				if (test[2].equals("RECEIVE")) {
					receive_set.add(test[1]);
					//System.out.println(test[2]);
				}else {
					fail.add(test[1]);
					//System.out.println("fail1"+fail);
				}
			}

			/**send和receive取并集*/
			interSet.addAll(send_set);
			interSet.retainAll(receive_set);

			receive_set.removeAll(interSet);
			send_set.removeAll(interSet);

			/**所有失败的集合*/
			fail.addAll(send_set);
			fail.addAll(receive_set);

			String outputFileName = path + (path.endsWith(File.separator) ? "" : File.separator) + "result.txt";
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File(outputFileName)));
			writer.write("SUCCESS:"+"\n");
			for(String a:interSet){
				writer.write(a+"\n");
				//System.out.println(a);
			}
			writer.write("FAIL:"+"\n");
			for(String a:fail){
				writer.write(a+"\n");
				System.out.println(a);
			}
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
