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

		// 更新该文件夹下所有文件目录
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
						// 返回一个字符串副本，并忽略(去除)开头和结尾的空白
						s = s.trim();
						// 判断是否为<DOC>
						if (doc_start(s)) {
							continue;
						}
						// 判断是否为DOCNO
						else if (docno(s)) {
							String doc_path = s.substring(docno_start, docno_end);
							// System.out.println("doc_path1 = " + doc_path);
							doc_path = doc_path.trim();
							// 每一篇报道单独存放在一个文件中
							doc_path = dir + "\\" + doc_path;
							// System.out.println("doc_path2 = " + doc_path);

							File doc = new File(doc_path);
							if (!doc.exists()) {
								doc.createNewFile();
							}
							fw = new FileWriter(doc.getAbsoluteFile());
							bw = new BufferedWriter(fw);
						}
						// 判断是否为<TEXT>
						else if (txt_start(s)) {
							is_content = true;
						}
						// 判断是否为</TEXT>
						else if (txt_end(s)) {
							bw.write(content);
							bw.close();
							is_content = false;
						}
						// 记录正文内容
						else if (is_content) {
							content += s + " ";
						} else if (!is_content) {
							content = "";
						} else {
							System.err.println("line: " + line + " err: " + s);
						}
					}
				} else {
					System.out.println("找不到指定的文件");
				}
			} catch (Exception e) {
				System.out.println("读取文件内容出错");
				e.printStackTrace();
			}
		}
	}

	// 修改每一个文件的路径
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

	// 更改路径
	public static File[] UpdateFiles(String path) {
		int i = 0;
		File f = null;
		// 得到该路径下的每一个文件
		File file = new File(path);
		File[] array = file.listFiles();
		Pattern pattern = Pattern.compile("(ARB.)|(MAN.)");

		// 更改阿语和中文报道的路径
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

	// 判断格式是否为<DOC>
	public static boolean doc_start(String s) {
		return s.matches("<\\s*DOC\\s*>");
	}

	// 判断是否为<TEXT>
	public static boolean txt_start(String s) {
		return s.matches("<\\s*TEXT\\s*>");
	}

	// 判断是否为</TEXT>
	public static boolean txt_end(String s) {
		return s.matches("</\\s*TEXT\\s*>");
	}

	// 判断是否为<DOCNO>并得到DOCNO
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
