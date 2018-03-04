package removeStopword;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class removeStopword {

	static String encoding = "GBK";
	static ArrayList<String> stopwords = new ArrayList<>();

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub

		File file = new File("D:\\TDT\\stopword.txt");
		// 读入stopword
		try {
			if (file.isFile() && file.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String word = null;
				// 读取每一个文件所在的目录
				while ((word = bufferedReader.readLine()) != null) {
					stopwords.add(word);
				}
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}

		// path3 = D:\TDT\NE_doc\Acts of Violence or
		// War\20030401_0113_1041_AFP_ARB\AFA20030401.0113.0007
		File path = new File("C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Result\\path\\path3.txt");
		try {
			if (path.isFile() && path.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(path), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String filepath = null;
				// 读取每一个文件所在的目录
				while ((filepath = bufferedReader.readLine()) != null) {
					// 创建目录写入新报道
					String writepath = filepath.replace("NE_doc", "DOC");
					File f = new File(writepath);
					if (!f.exists()) {
						f.mkdirs();
					}
					removestopword(filepath + "\\date.txt");
					removestopword(filepath + "\\location.txt");
					removestopword(filepath + "\\money.txt");
					removestopword(filepath + "\\nonNE.txt");
					removestopword(filepath + "\\organization.txt");
					removestopword(filepath + "\\percentage.txt");
					removestopword(filepath + "\\person.txt");
					removestopword(filepath + "\\time.txt");
					System.out.println(filepath);
				}
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}

	static void removestopword(String path) {
		File doc = new File(path);
		String writepath = path.replace("NE_doc", "DOC");

		try {
			// 创建该目录
			File writedoc = new File(writepath);
			if (!writedoc.exists()) {
				writedoc.createNewFile();
			}
			FileWriter fw = new FileWriter(writedoc.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			if (doc.isFile() && doc.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(doc), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String word = null;
				// 读取每一个文件所在的目录
				while ((word = bufferedReader.readLine()) != null) {
					String s = word.toLowerCase();
					int index = stopwords.indexOf(s);
					if (index == -1) {
						bw.write(word);
						bw.newLine();
					}
				}
				bw.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}
}
