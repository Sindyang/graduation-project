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
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(filepath), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String path = null;
				// ��ȡÿһ���ļ����ڵ�Ŀ¼
				while ((path = bufferedReader.readLine()) != null) {
					readTxtFile(path);
					System.out.println(path);
				}
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
	}

	public static void readTxtFile(String path) throws IOException {
		// pah2Ϊ��������ʵ���·��
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
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(readdoc), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String word = null;
				// ��ȡÿһ���ļ����ڵ�Ŀ¼
				while ((word = bufferedReader.readLine()) != null) {
					// ʹ�ÿո�
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
				// ���words������Ԫ��
				words.clear();
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
	}

	// ������ʵ�����list��
	public static void readNEFile(String path2) throws IOException {
		File file = new File(path2);
		try {
			if (file.isFile() && file.exists()) {
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String str = null;
				// ��ȡÿһ���ļ����ڵ�Ŀ¼
				while ((str = bufferedReader.readLine()) != null) {
					str.trim();
					String[] strArray = str.split(" ");
					for (String word : strArray) {
						words.add(word);
					}
				}
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
	}
}
