package get_nonNE;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class get_nonNE {

	static String encoding = "GBK";
	static ArrayList<String> words = new ArrayList<>();

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File filepath = new File("C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Result\\path\\path1.txt");
		try {
			if (filepath.isFile() && filepath.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(filepath), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String path = null;
				// 读取每一个文件所在的目录
				while ((path = bufferedReader.readLine()) != null) {
					readTxtFile(path);
					System.out.println(path);
				}
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}

	public static void readTxtFile(String path) throws IOException {
		// pah2为含有命名实体的路径
		String path2 = path.replace("Extraction", "NE_doc");
		readNEFile(path2 + "\\date.txt");
		readNEFile(path2 + "\\location.txt");
		readNEFile(path2 + "\\money.txt");
		readNEFile(path2 + "\\organization.txt");
		readNEFile(path2 + "\\percentage.txt");
		readNEFile(path2 + "\\person.txt");
		readNEFile(path2 + "\\time.txt");
		File readdoc = new File(path);
		File writedoc = new File(path2 + "\\nonNE.txt");

		if (!writedoc.exists()) {
			writedoc.createNewFile();
		}
		FileWriter fw = new FileWriter(writedoc.getAbsoluteFile());
		BufferedWriter bw = new BufferedWriter(fw);
		try {
			if (readdoc.isFile() && readdoc.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(readdoc), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String word = null;
				// 读取每一个文件所在的目录
				while ((word = bufferedReader.readLine()) != null) {
					// 使用空格
					String newword = word.replaceAll("\\pP|\\pS", " ");
					newword = newword.trim();
					int index = words.indexOf(newword);

					if (index == -1) {
						if (newword.equals("")) {

						} else {
							bw.write(newword);
							bw.newLine();
						}
					}
				}
				bw.close();
				// 清除words中所有元素
				words.clear();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}

	// 将命名实体读入list中
	public static void readNEFile(String path2) throws IOException {
		File file = new File(path2);
		try {
			if (file.isFile() && file.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String str = null;
				// 读取每一个文件所在的目录
				while ((str = bufferedReader.readLine()) != null) {
					str.trim();
					String[] strArray = str.split(" ");
					for (String word : strArray) {
						words.add(word);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
	}
}
