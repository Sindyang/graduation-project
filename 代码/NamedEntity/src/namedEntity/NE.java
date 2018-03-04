package namedEntity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

public class NE {
	@SuppressWarnings("resource")
	public static void main(String[] args) {
		File filepath = new File("C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Result\\path\\path1.txt");
		try {
			String encoding = "GBK";
			if (filepath.isFile() && filepath.exists()) {
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(filepath), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String content = null;
				// ��ȡÿһ���ļ����ڵ�Ŀ¼
				while ((content = bufferedReader.readLine()) != null) {
					readTxtFile(content);
					System.out.println(content);
				}
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
	}

	// ��ȡ�ļ������м��
	public static void readTxtFile(String filePath) {
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			// �ж��ļ��Ƿ����
			if (file.isFile() && file.exists()) {
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				BufferedReader bufferedReader = new BufferedReader(read);
				String content = null;
				ArrayList<String> list = new ArrayList<String>();

				while ((content = bufferedReader.readLine()) != null) {
					// ����ļ�����
					list.add(content);
				}
				// ת��ΪString����
				String[] sentence = list.toArray(new String[list.size()]);

				// ����Ŀ¼
				String path = filePath.replace("Extraction", "Result\\NE_doc");
				File f = new File(path);
				if (!f.exists()) {
					f.mkdirs();
				}
				// ��������ʵ��ʶ��
				findNamedEntity(sentence, "en-ner-date.bin", f.toString() + "\\date.txt");
				findNamedEntity(sentence, "en-ner-location.bin", f.toString() + "\\location.txt");
				findNamedEntity(sentence, "en-ner-money.bin", f.toString() + "\\money.txt");
				findNamedEntity(sentence, "en-ner-organization.bin", f.toString() + "\\organization.txt");
				findNamedEntity(sentence, "en-ner-percentage.bin", f.toString() + "\\percentage.txt");
				findNamedEntity(sentence, "en-ner-person.bin", f.toString() + "\\person.txt");
				findNamedEntity(sentence, "en-ner-time.bin", f.toString() + "\\time.txt");
				read.close();
			} else {
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
	}

	public static void findNamedEntity(String sentence[], String bin, String filepath) throws IOException {
		InputStream is = new FileInputStream(bin);
		TokenNameFinderModel model = new TokenNameFinderModel(is);
		is.close();
		NameFinderME nameFinder = new NameFinderME(model);
		Span nameSpans[] = nameFinder.find(sentence);
		try {
			// ��һ��д�ļ��������캯���еĵڶ�������true��ʾ��׷����ʽд�ļ�
			FileWriter writer = new FileWriter(filepath, true);
			for (Span s : nameSpans) {
				// System.out.println(s.toString());
				int loc1 = s.toString().indexOf('.');
				int loc2 = s.toString().indexOf(')');
				// ����ʵ�����ڵ��±�
				int num1 = Integer.parseInt(s.toString().substring(1, loc1));
				int num2 = Integer.parseInt(s.toString().substring(loc1 + 2, loc2));
				for (int i = num1; i < num2; i++) {
					String obj = sentence[i];
					writer.write(obj);
					if (i != num2 - 1) {
						writer.write(" ");
					}
				}
				writer.write("\r\n");
			}
			writer.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}