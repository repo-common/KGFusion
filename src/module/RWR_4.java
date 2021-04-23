package utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import java.util.Map.Entry;

public class RWR_4 {

//	public static HashMap<Integer, HashMap<Integer, Double>> tfidf_value1 = new HashMap<Integer, HashMap<Integer, Double>>();// 文档号、词、tfidf值；
	static HashMap<Integer, HashMap<Integer, Double>> RWRTT_value = new HashMap<Integer, HashMap<Integer, Double>>();
	public static HashMap<Integer, HashMap<Integer, Double>> InvertedIndex = new HashMap<Integer, HashMap<Integer, Double>>();// 词、tfidf值
	public static HashMap<Integer, Double> invertedindex = new HashMap<Integer, Double>();// 词、IDF值
	public static HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();// 词、tfidf值
//	static HashMap<Integer, HashMap<Integer, Double>> Trans_TFIDF = new HashMap<Integer, HashMap<Integer, Double>>();
	static HashMap<Integer, HashMap<Integer, Double>> probability = new HashMap<Integer, HashMap<Integer, Double>>();
	static HashMap<Integer, HashMap<Integer, Double>> probability1 = new HashMap<Integer, HashMap<Integer, Double>>();
	static HashMap<Integer, HashMap<Integer, Double>> probability2 = new HashMap<Integer, HashMap<Integer, Double>>();
	static HashMap<Integer, HashMap<Integer, Double>> probability3 = new HashMap<Integer, HashMap<Integer, Double>>();
	static HashMap<Integer, HashMap<Integer, Double>> probability4 = new HashMap<Integer, HashMap<Integer, Double>>();
	static HashMap<Integer, HashMap<Integer, Double>> probability5 = new HashMap<Integer, HashMap<Integer, Double>>();
	public static HashMap<Integer, HashMap<Integer, Double>> Normalize_TT = new HashMap<Integer, HashMap<Integer, Double>>();// 词、tfidf值
	static double c = 0.8;

	// 文档号， 单词， tfidf值, 将输入的tf-idf矩阵转换为需要的形式
	public  HashMap<Integer, HashMap<Integer, Double>> setTfIdfMatrix(double[][] tfIdfMatrix){
		HashMap<Integer, HashMap<Integer, Double>> res = new HashMap<>();
//		for (int j = 0; j < tfIdfMatrix[0].length; j++){
//			for (int i = 0; i < tfIdfMatrix.length; i++){
//				HashMap<Integer, Double> temp = new HashMap<>();
//				if (tfIdfMatrix[i][j] != 0.0){
//					temp.put(i, tfIdfMatrix[i][j]);
//					res.put(j, temp);
//				}
//
//			}
//		}

		for (int i = 0; i < tfIdfMatrix.length; i++){
			for (int j = 0; j < tfIdfMatrix[0].length; j++){
				HashMap<Integer, Double> temp = new HashMap<>();
				if (tfIdfMatrix[i][j] != 0.0){
					temp.put(j, tfIdfMatrix[i][j]);
					res.put(i, temp);
				}
			}
		}
		return res;
	}

	public HashMap<Integer, HashMap<Integer, Double>>  entrance(double[][] tfIdfMatrix) throws IOException {
		HashMap<Integer, HashMap<Integer, Double>> Trans_TFIDF = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> tfidf_value1 = new HashMap<Integer, HashMap<Integer, Double>>();// 文档号、词、tfidf值；
		HashMap<Integer, HashMap<Integer, Double>> probability8 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability9 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability10 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability11 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability12 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability13 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability14 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability15 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability16 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability17 = new HashMap<Integer, HashMap<Integer, Double>>();

		long startTime = System.currentTimeMillis();
//		String filePath = "E:\\ASDN-Introcuction\\data1003\\Wordid_Pid.txt";

//		String filePath = "C:\\Users\\DAI\\Desktop\\Wordid_Pid.txt";//正式数据
//		HashMap<Integer,HashMap<Integer,Double>> M = txt2HashMap(filePath);

//		System.out.println("打印在txt中数据转为hashmap==="+M);//测试

		tfidf_value1 = setTfIdfMatrix(tfIdfMatrix);

//		System.out.println("将读取的hashmap赋值给tfidf_value1==="+tfidf_value1);//测试

		Trans_TFIDF = trans_matrix(tfidf_value1);// 7*3
//		System.out.println("打印测试=转置矩阵="+Trans_TFIDF);
		// System.out.println("=============");
		// TT
		Normalize_TT = matrix_multiplication(normalize(Trans_TFIDF), trans_matrix(normalize(tfidf_value1))); // yu

//		System.out.println("================测试矩阵是否准确 ================");
//		System.out.println("打印测试=转置矩阵=归一化="+normalize(Trans_TFIDF));//打印测试
//		System.out.println("归一化=  = =原来矩阵=="+(normalize(tfidf_value1)));
//		System.out.println("归一化=再转置  = =原来矩阵=="+trans_matrix(normalize(tfidf_value1)));
//		System.out.println("相乘后的结果"+Normalize_TT);

		// TTRWR
		probability1 = multiplication(Normalize_TT);// c(1-c)p=R1
		result = matrix_multiplication(tfidf_value1, probability1);//游走第一步
		print_matrix(result,"C:\\Users\\DAI\\Desktop\\res\\F2_online_" + "c0.8_1" + ".txt");

		probability2 = multiplication1(Normalize_TT);// (1-c)p
		probability3 = matrix_multiplication1(probability2, probability1);// (1-c)p*R1
		probability4 = matrix_addition(probability1, probability3);// R2=c(1-c)p+(1-c)p*R1 游走第二步

		result = matrix_multiplication(tfidf_value1, probability4);//
		print_matrix(result,"C:\\Users\\DAI\\Desktop\\res\\F2_online_" + "c0.8_2" + ".txt");

		probability5 = matrix_multiplication1(probability2, probability4);// (1-c)p*R2
		// RWRTT_value
		RWRTT_value = matrix_addition(probability1, probability5);// R3=c(1-c)P+(1-c)p*R2 游走的第三步
		// DT
		result = matrix_multiplication(tfidf_value1, RWRTT_value); //最后的值乘以TFIDF值
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("程序运行时间： " + (endTime - startTime) + "ms");
		//
		print_matrix(result,"C:\\Users\\DAI\\Desktop\\res\\F2_online_" + "c0.8_3" + ".txt");


		probability8 = matrix_multiplication1(probability2, RWRTT_value);
		probability9 = matrix_addition(probability1, probability8);
		result = matrix_multiplication(tfidf_value1, probability9);
		print_matrix(result,"C:\\Users\\DAI\\Desktop\\res\\F2_online_" + "c0.8_4" + ".txt");

		return result;
	}
	// public static int Number;
	public static void main(String[] args) throws IOException {	
		HashMap<Integer, HashMap<Integer, Double>> Trans_TFIDF = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> tfidf_value1 = new HashMap<Integer, HashMap<Integer, Double>>();// 文档号、词、tfidf值；
		HashMap<Integer, HashMap<Integer, Double>> probability8 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability9 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability10 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability11 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability12 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability13 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability14 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability15 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability16 = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, HashMap<Integer, Double>> probability17 = new HashMap<Integer, HashMap<Integer, Double>>();
		
		long startTime = System.currentTimeMillis();				
//		String filePath = "E:\\ASDN-Introcuction\\data1003\\Wordid_Pid.txt";

//		String filePath = "C:\\Users\\DAI\\Desktop\\Wordid_Pid.txt";//正式数据
//		HashMap<Integer,HashMap<Integer,Double>> M = txt2HashMap(filePath);
		
//		System.out.println("打印在txt中数据转为hashmap==="+M);//测试
		
//		tfidf_value1 = M;
		
//		System.out.println("将读取的hashmap赋值给tfidf_value1==="+tfidf_value1);//测试
		
//		Trans_TFIDF = trans_matrix(M);// 7*3
//		System.out.println("打印测试=转置矩阵="+Trans_TFIDF);
		// System.out.println("=============");
		// TT
		Normalize_TT = matrix_multiplication(normalize(Trans_TFIDF), trans_matrix(normalize(tfidf_value1))); // yu
		
//		System.out.println("================测试矩阵是否准确 ================");
//		System.out.println("打印测试=转置矩阵=归一化="+normalize(Trans_TFIDF));//打印测试
//		System.out.println("归一化=  = =原来矩阵=="+(normalize(tfidf_value1)));
//		System.out.println("归一化=再转置  = =原来矩阵=="+trans_matrix(normalize(tfidf_value1)));
//		System.out.println("相乘后的结果"+Normalize_TT);
		
		// TTRWR
		probability1 = multiplication(Normalize_TT);// c(1-c)p=R1
		result = matrix_multiplication(tfidf_value1, probability1);//游走第一步
		print_matrix(result,"C:\\Users\\DAI\\Desktop\\res\\F2_online_" + "c0.8_1" + ".txt");
		
		probability2 = multiplication1(Normalize_TT);// (1-c)p
		probability3 = matrix_multiplication1(probability2, probability1);// (1-c)p*R1
		probability4 = matrix_addition(probability1, probability3);// R2=c(1-c)p+(1-c)p*R1 游走第二步
		
		result = matrix_multiplication(tfidf_value1, probability4);//
		print_matrix(result,"C:\\Users\\DAI\\Desktop\\res\\F2_online_" + "c0.8_2" + ".txt");
		
		probability5 = matrix_multiplication1(probability2, probability4);// (1-c)p*R2
		// RWRTT_value
		RWRTT_value = matrix_addition(probability1, probability5);// R3=c(1-c)P+(1-c)p*R2 游走的第三步
		// DT
		result = matrix_multiplication(tfidf_value1, RWRTT_value); //最后的值乘以TFIDF值
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("程序运行时间： " + (endTime - startTime) + "ms");		
		// 
		print_matrix(result,"C:\\Users\\DAI\\Desktop\\res\\F2_online_" + "c0.8_3" + ".txt");
		
		
		probability8 = matrix_multiplication1(probability2, RWRTT_value);
		probability9 = matrix_addition(probability1, probability8);
		result = matrix_multiplication(tfidf_value1, probability9);
		print_matrix(result,"C:\\Users\\DAI\\Desktop\\res\\F2_online_" + "c0.8_4" + ".txt");
		
		
		
	}

	public static HashMap<Integer, HashMap<Integer, Double>> multiplication(// c(1-c)p
			HashMap<Integer, HashMap<Integer, Double>> TFIDF1) throws IOException {
		HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Double> m1 = new HashMap<Integer, Double>();
		Iterator<Integer> it = TFIDF1.keySet().iterator();//
//		double c = 0.8;
		while (it.hasNext()) {
			int k1 = it.next();
			m1 = TFIDF1.get(k1);// (k1,m1)
			HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();
			Iterator<Integer> it1 = m1.keySet().iterator();//
			while (it1.hasNext()) {
				int k2 = it1.next();
				double d1 = m1.get(k2);
				double d2 = d1 * c * (1 - c);// (1-c)p*(RL)

				m3.put(k2, d2);
			}
			result.put(k1, m3);// <k1,<k2, d2>>
		}

		return result;
	}

	public static HashMap<Integer, HashMap<Integer, Double>> multiplication1(// (1-c)p
			HashMap<Integer, HashMap<Integer, Double>> TFIDF1) throws IOException {
		HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Double> m1 = new HashMap<Integer, Double>();
		Iterator<Integer> it = TFIDF1.keySet().iterator();//
//		double c = 0.8;
		while (it.hasNext()) {
			int k1 = it.next();
			m1 = TFIDF1.get(k1);// (k1,m1)
			HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();
			Iterator<Integer> it1 = m1.keySet().iterator();//
			while (it1.hasNext()) {
				int k2 = it1.next();
				double d1 = m1.get(k2);
				double d2 = d1 * (1 - c);// (1-c)p
				m3.put(k2, d2);
			}
			result.put(k1, m3);// <k1,<k2, d2>>
		}

		return result;
	}

	public static HashMap<Integer, HashMap<Integer, Double>> matrix_multiplication(// 矩阵相乘
			HashMap<Integer, HashMap<Integer, Double>> TFIDF1, HashMap<Integer, HashMap<Integer, Double>> TFIDF2) {
		// 两个矩阵相乘；
		HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Double> m1 = new HashMap<Integer, Double>();
		HashMap<Integer, Double> m2 = new HashMap<Integer, Double>();
		double d1 = 0.0;
		for (Entry<Integer, HashMap<Integer, Double>> entry : TFIDF1.entrySet()) {// ①//行i
			int k1 = entry.getKey();
			m1 = entry.getValue();// (k1,m1)通过k1行找列m1

			HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();

			for (Entry<Integer, HashMap<Integer, Double>> entry2 : TFIDF2.entrySet()) {// ②//列j
				int k2 = entry2.getKey();
				m2 = TFIDF2.get(k2);// (k2,m2)找k2行m2列
				double sum = 0;

				for (Entry<Integer, Double> it2_1 : m2.entrySet()) {// 3 //值
					int k3 = it2_1.getKey();
					try {
						d1 = m1.get(k3);// （k1,m1,d1)d1为值
						// System.out.println("d1="+d1);
					} catch (Exception e) {
						d1 = 0.0;
					}
					double d2 = m2.get(k3);// （k2,m2,d2)d2为值
					// System.out.println("d2="+d2);
					double d3 = d1 * d2; // A[i,k] * B[k,j]
					// System.out.println("d3="+d3);
					sum += d3; // C[i,j] += A[i,k] * B[k,j];
					// 加算完了第一个值
				}
				// System.out.println("sum="+sum);
				if(sum >0.0001) {
				m3.put(k2, sum);}
				// System.out.println(m3);
			}
			// System.out.println("-------------------");
			result.put(k1, m3);// 相乘后的方阵，<k1,<k2, sum1>>
		}
		return result;

		// return result;
		// System.out.println(result);
	}

	public static HashMap<Integer, HashMap<Integer, Double>> matrix_multiplication1(// 矩阵相乘
			HashMap<Integer, HashMap<Integer, Double>> TFIDF1, HashMap<Integer, HashMap<Integer, Double>> TFIDF2) {
		// 两个矩阵相乘；
		HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
		HashMap<Integer, Double> m1 = new HashMap<Integer, Double>();
		HashMap<Integer, Double> m2 = new HashMap<Integer, Double>();
		double d1 = 0.0;
//		double c = 0.8;
		for (Entry<Integer, HashMap<Integer, Double>> entry : TFIDF1.entrySet()) {// ①//行i
			int k1 = entry.getKey();
			m1 = entry.getValue();// (k1,m1)通过k1行找列m1
			HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();
			for (Entry<Integer, HashMap<Integer, Double>> entry2 : TFIDF2.entrySet()) {// ②//列j
				int k2 = entry2.getKey();
				m2 = TFIDF2.get(k2);// (k2,m2)找k2行m2列
				double sum = 0;
				for (Entry<Integer, Double> it2_1 : m2.entrySet()) {// 3 //值
					int k3 = it2_1.getKey();
					try {
						d1 = m1.get(k3);// （k1,m1,d1)d1为值
						// System.out.println("d1="+d1);
					} catch (Exception e) {
						d1 = 0.0;
					}
					double d2 = m2.get(k3);// （k2,m2,d2)d2为值
					// System.out.println("d2="+d2);
					double d3 = d1 * d2 * (1 - c); // A[i,k] * B[k,j]
					// System.out.println("d3="+d3);
					sum += d3; // C[i,j] += A[i,k] * B[k,j];
					// 加算完了第一个值
				}
				// System.out.println("sum="+sum);
				if(sum > 0.0001) {
				m3.put(k2, sum);
				}
				// System.out.println(m3);
			}
			// System.out.println("-------------------");
			result.put(k1, m3);// 相乘后的方阵，<k1,<k2, sum1>>
		}
		return result;

		// return result;
		// System.out.println(result);
	}

	public static HashMap<Integer, HashMap<Integer, Double>> normalize(
			HashMap<Integer, HashMap<Integer, Double>> TFIDF) {
		// 矩阵行归一化

		HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
		Iterator<Integer> it = TFIDF.keySet().iterator();
		HashMap<Integer, Double> m7 = new HashMap<Integer, Double>();
		while (it.hasNext()) {
			int k1 = it.next();
			m7 = TFIDF.get(k1);
			Iterator<Integer> it_1 = m7.keySet().iterator();
			double sum = 0;
			while (it_1.hasNext()) {
				int k2 = it_1.next();
				double d1 = m7.get(k2);
				sum += d1;
			}
			Iterator<Integer> it_2 = m7.keySet().iterator();
			HashMap<Integer, Double> m8 = new HashMap<Integer, Double>();
			while (it_2.hasNext()) {
				int k3 = it_2.next();
				double d2 = m7.get(k3);
				double d3 = d2 / sum;
				m8.put(k3, d3);
			}
			result.put(k1, m8);

		}
		// System.out.println(result);
		return result;
	}

	public static HashMap<Integer, HashMap<Integer, Double>> trans_matrix(
			HashMap<Integer, HashMap<Integer, Double>> hm) { // 转置TFIDF矩阵得到Trans_TFIDF矩阵；
		// 转置TFIDF矩阵得到Trans_TFIDF矩阵；
		HashMap<Integer, HashMap<Integer, Double>> Trans_TFIDF = new HashMap<Integer, HashMap<Integer, Double>>();
		ArrayList<String> al = new ArrayList<String>();
		Iterator<Integer> it = hm.keySet().iterator();
		HashMap<Integer, Double> m3 = new HashMap<Integer, Double>();
		HashMap<Integer, Double> hm1 = new HashMap<Integer, Double>();

		while (it.hasNext()) {
			int k1 = it.next();
			m3 = hm.get(k1);
			Iterator<Integer> it_1 = m3.keySet().iterator();
			while (it_1.hasNext()) {
				
				int k2 = it_1.next();
				double v = m3.get(k2);
				al.add(k2 + " " + k1 + " " + v);
//				it_2.remove();//2020.1.1
			}
		}
		Collections.sort(al);
		Iterator<String> it_2 = al.iterator();
		String s1 = it_2.next().toString();//
		String s2[] = s1.split("\\s+");
		String s3 = s2[0];

		hm1.put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));
		while (it_2.hasNext()) {
			s1 = it_2.next().toString();
			s2 = s1.split("\\s+");
			String s4 = s2[0];
			if (s4.equals(s3)) {
				hm1.put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));
				it_2.remove();//2020.1.1
			} else {
				Trans_TFIDF.put(Integer.parseInt(s3), hm1);
				HashMap<Integer, Double> hm2 = new HashMap<Integer, Double>();
				hm1 = hm2;
				s3 = s4;
				hm1.put(Integer.parseInt(s2[1]), Double.parseDouble(s2[2]));

			}
			// System.out.println(Integer.parseInt(s3)+" "+Integer.parseInt(s2[1])+" "+
			// Double.parseDouble(s2[2]));
		}
		Trans_TFIDF.put(Integer.parseInt(s3), hm1);
//		System.out.println("==方法里面类中的转置==="+Trans_TFIDF);
		return Trans_TFIDF;
	
	}

	public static HashMap<Integer, HashMap<Integer, Double>> matrix_addition(
			HashMap<Integer, HashMap<Integer, Double>> probability,
			HashMap<Integer, HashMap<Integer, Double>> probability_result) {
		// 两个矩阵相加；
		HashMap<Integer, HashMap<Integer, Double>> result = new HashMap<Integer, HashMap<Integer, Double>>();
//		double c = 0.8;
		HashMap<Integer, Double> m4 = new HashMap<Integer, Double>();
		HashMap<Integer, Double> m5 = new HashMap<Integer, Double>();

		Iterator<Integer> it = probability_result.keySet().iterator();
		while (it.hasNext()) {
			int k1 = it.next();
			HashMap<Integer, Double> m6 = new HashMap<Integer, Double>();
			m4 = probability.get(k1);
			m5 = probability_result.get(k1);
			Iterator<Integer> it_1 = m4.keySet().iterator();
			while (it_1.hasNext()) {
				int k2 = it_1.next();
				double d1 = m4.get(k2);
				if(m5.get(k2) !=null) {
				double d2 = m5.get(k2);
				
				double d3 = d1 + c * d2;
				m6.put(k2, d3);}
			}
			result.put(k1, m6);
		}
		// System.out.println(result);
		return result;
	}

	public static void print_matrix(HashMap<Integer, HashMap<Integer, Double>> TFIDF, String path) throws IOException {
		/*
		 * 还需要从别的java文件的类中获得原有的文章号和单词，然后对value值进行排序；？？？？？（未实现）
		 */
		File f1 = new File(path);
		FileWriter fw1 = new FileWriter(f1);
		BufferedWriter bf1 = new BufferedWriter(fw1);
		Iterator<Integer> it_1 = TFIDF.keySet().iterator();
		HashMap<Integer, Double> hm = new HashMap<Integer, Double>();

		while (it_1.hasNext()) {
			Integer s3 = it_1.next();
			hm = TFIDF.get(s3);
			Iterator<Integer> it_2 = hm.keySet().iterator();
			while (it_2.hasNext()) {
				Integer s4 = it_2.next();
				Double s5 = hm.get(s4);
				if (s5 > 0) {
				bf1.write(s3 + "\t" + s4 + "\t" + s5 + "\r\n");
				}
				// System.out.println(s3+" "+s4+" "+s5);
			}
		}
		bf1.flush();
		bf1.close();
		fw1.close();
	}
	
	public static HashMap<Integer, HashMap<Integer, Double>> txt2HashMap(String filePath) {

//		HashMap<Integer,Double> m1 = new HashMap<Integer,Double>();

		HashMap<Integer, HashMap<Integer, Double>> hm = new HashMap<Integer, HashMap<Integer, Double>>();
		try {
			String encoding = "GBK";
			File file = new File(filePath);

			if (file.isFile() && file.exists()) {
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					String[] split = line.split("\\s+");
					if (split.length != 3) {
						continue;
					}
					int s3 = Integer.parseInt(split[0]);
					int s1 = Integer.parseInt(split[1]);
					double s2 = Double.parseDouble(split[2]);

					HashMap<Integer, Double> m1 = hm.getOrDefault(s3, new HashMap<Integer, Double>());

					m1.put(s1, s2);
					hm.put(s3, m1);
				}
				read.close();
				bufferedReader.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return hm;
	}
	


}

