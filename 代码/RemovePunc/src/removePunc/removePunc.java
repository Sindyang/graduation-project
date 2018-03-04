package removePunc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

public class removePunc {

	static String encoding = "GBK";

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File file = new File("C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Result\\path\\path2.txt");
		try {
			if (file.isFile() && file.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String path = null;
				// 读取每一个文件所在的目录
				while ((path = bufferedReader.readLine()) != null) {
					String writepath = path.replace("Result\\NE_doc", "NE_doc");
					File f = new File(writepath);
					if (!f.exists()) {
						f.mkdirs();
					}
					readTxtFile(path + "\\date.txt");
					readTxtFile(path + "\\location.txt");
					readTxtFile(path + "\\money.txt");
					readTxtFile(path + "\\organization.txt");
					readTxtFile(path + "\\percentage.txt");
					readTxtFile(path + "\\person.txt");
					readTxtFile(path + "\\time.txt");
					System.out.println(path);
				}
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}

	public static void readTxtFile(String readpath) {
		// path2为含有命名实体的路径
		String writepath = readpath.replace("Result\\NE_doc", "NE_doc");
		File readdoc = new File(readpath);

		try {
			// 创建该目录
			File writedoc = new File(writepath);
			if (!writedoc.exists()) {
				writedoc.createNewFile();
			}
			FileWriter fw = new FileWriter(writedoc.getAbsoluteFile());
			BufferedWriter bw = new BufferedWriter(fw);

			if (readdoc.isFile() && readdoc.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(readdoc), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String word = null;
				String newword = null;
				// 读取每一个文件所在的目录
				while ((word = bufferedReader.readLine()) != null) {
					newword = word.replaceAll("\\pP|\\pS", " ");
					bw.write(newword);
					bw.newLine();
				}
				bw.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}
}
