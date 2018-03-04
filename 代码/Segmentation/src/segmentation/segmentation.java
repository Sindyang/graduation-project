package segmentation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class segmentation {

	private static int docno_start = 0;
	private static int docno_end = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int i = 0;
		int line = 1;
		String dir;
		String s = null;
		FileWriter fw = null;
		BufferedWriter bw = null;
		String encoding = "GBK";
		InputStreamReader read;
		BufferedReader bufferedReader;
		boolean is_content = false;
		String read_path = "C:\\Users\\wangsy\\Desktop\\Code\\TDT\\TDT5 2004 TIPSTER-style\\tkn_sgm";

		// ���¸��ļ����������ļ�Ŀ¼
		File[] files = UpdateFiles(read_path);

		for (i = 0; i < files.length; i++) {
			System.out.println("filepath = " + files[i].toString());
			line = 1;
			String content = "";
			dir = ModifyPath(files[i].toString());
			// System.out.println("dir = " + dir);
			try {
				if (files[i].isFile() && files[i].exists()) {
					read = new InputStreamReader(new FileInputStream(files[i]), encoding);
					bufferedReader = new BufferedReader(read);
					is_content = false;
					while ((s = bufferedReader.readLine()) != null) {
						// ����һ���ַ���������������(ȥ��)��ͷ�ͽ�β�Ŀհ�
						s = s.trim();
						// �ж��Ƿ�Ϊ<DOC>
						if (doc_start(s)) {
							continue;
						}
						// �ж��Ƿ�ΪDOCNO
						else if (docno(s)) {
							String doc_path = s.substring(docno_start, docno_end);
							// System.out.println("doc_path1 = " + doc_path);
							doc_path = doc_path.trim();
							// ÿһƪ�������������һ���ļ���
							doc_path = dir + "\\" + doc_path;
							// System.out.println("doc_path2 = " + doc_path);

							File doc = new File(doc_path);
							if (!doc.exists()) {
								doc.createNewFile();
							}
							fw = new FileWriter(doc.getAbsoluteFile());
							bw = new BufferedWriter(fw);
						}
						// �ж��Ƿ�Ϊ<TEXT>
						else if (txt_start(s)) {
							is_content = true;
						}
						// �ж��Ƿ�Ϊ</TEXT>
						else if (txt_end(s)) {
							bw.write(content);
							bw.close();
							is_content = false;
						}
						// ��¼��������
						else if (is_content) {
							content += s + " ";
						} else if (!is_content) {
							content = "";
						} else {
							System.err.println("line: " + line + " err: " + s);
						}
					}
				} else {
					System.out.println("�Ҳ���ָ�����ļ�");
				}
			} catch (Exception e) {
				System.out.println("��ȡ�ļ����ݳ���");
				e.printStackTrace();
			}
		}
	}

	// �޸�ÿһ���ļ���·��
	public static String ModifyPath(String path) {
		int i;
		int start = 0;
		int end = 0;
		String newpath = "C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Segmentation";
		for (i = path.length() - 1; i >= 0; i--) {
			if (path.charAt(i) == '.')
				end = i;
			if (path.charAt(i) == '\\') {
				start = i + 1;
				break;
			}
		}
		String s = path.substring(start, end);
		newpath = newpath + "\\" + s;
		File f = new File(newpath);
		if (!f.exists()) {
			f.mkdirs();
		}
		return f.toString();
	}

	// ����·��
	public static File[] UpdateFiles(String path) {
		int i = 0;
		File f = null;
		// �õ���·���µ�ÿһ���ļ�
		File file = new File(path);
		File[] array = file.listFiles();
		Pattern pattern = Pattern.compile("(ARB.)|(MAN.)");

		// ���İ�������ı�����·��
		for (i = 0; i < array.length; i++) {
			f = array[i];
			Matcher match = pattern.matcher(array[i].toString());
			if (match.find()) {
				String temp = array[i].toString();
				// System.out.println("temp = " + temp);
				String temp1 = temp.replace("tkn_sgm", "mttkn_sgm");
				// System.out.println("temp1 = " + temp1);
				f = new File(temp1);
			}
			array[i] = f;
		}
		return array;
	}

	// �жϸ�ʽ�Ƿ�Ϊ<DOC>
	public static boolean doc_start(String s) {
		return s.matches("<\\s*DOC\\s*>");
	}

	// �ж��Ƿ�Ϊ<TEXT>
	public static boolean txt_start(String s) {
		return s.matches("<\\s*TEXT\\s*>");
	}

	// �ж��Ƿ�Ϊ</TEXT>
	public static boolean txt_end(String s) {
		return s.matches("</\\s*TEXT\\s*>");
	}

	// �ж��Ƿ�Ϊ<DOCNO>���õ�DOCNO
	public static boolean docno(String s) {
		Pattern p_start = Pattern.compile("<\\s*DOCNO\\s*>");
		Pattern p_end = Pattern.compile("</\\s*DOCNO\\s*>");
		Matcher m_start = p_start.matcher(s);
		Matcher m_end = p_end.matcher(s);
		boolean start = false;
		boolean end = false;

		if (m_start.find()) {
			docno_start = m_start.end();
			start = true;
		}
		if (m_end.find()) {
			docno_end = m_end.start();
			end = true;
		}
		if (start && end)
			return true;
		else
			return false;
	}
}
