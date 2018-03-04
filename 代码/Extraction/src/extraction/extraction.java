package extraction;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.InvalidFormatException;

public class extraction {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Matcher m;
		int line = 0;
		String encoding = "GBK";
		String readpath = "C:\\Users\\wangsy\\Desktop\\Code\\TDT\\�����\\TDT5 2004\\TDT2004.topic_rel";
		File file = new File(readpath);
		Pattern p_topicid = Pattern.compile("topicid=\\d+");
		Pattern p_docno = Pattern.compile("docno=[A-Z]+\\d+.\\d+.\\d+");
		Pattern p_fileid = Pattern.compile("fileid=\\d+_\\d+_\\d+_[A-Z]+_[A-Z]+");
		String topicid = null;
		String docno = null;
		String fileid = null;
		String ROI = null;
		try {
			if (file.isFile() && file.exists()) {
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String content = null;
				while ((content = bufferedReader.readLine()) != null) {
					line++;
					// �õ�topicid
					m = p_topicid.matcher(content);
					if (m.find()) {
						topicid = m.group().substring(8);
						// System.out.println("topicid = " + topicid);
						if (topicid == "55002") {
							System.exit(0);
						}
						// �õ�docno
						m = p_docno.matcher(content);
						if (m.find()) {
							docno = m.group().substring(6);
						} else {
							System.err.println("line: " + line + " docno not found!");
						}
						// �õ�fileid
						m = p_fileid.matcher(content);
						if (m.find()) {
							fileid = m.group().substring(7);
							Pattern p_chn = Pattern.compile("CHN");
							m = p_chn.matcher(fileid);
							if (m.find()) {
								fileid = m.replaceAll("MAN");
							}
						} else {
							System.err.println("line: " + line + " fileid not found!");
						}
						// �õ�ROI
						ROI = FindROI(topicid);
						// ��ȡ���ļ��е�����
						readfile(fileid, docno, ROI);
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

	public static String FindROI(String topicid) {
		String ROI = null;
		Matcher m = null;
		Pattern p_topicid = Pattern.compile(topicid);
		String path = "C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Result\\topic_ROI.txt";
		try {
			String encoding = "GBK";
			File file = new File(path);
			if (file.exists() && file.isFile()) {
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(file), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String content = null;
				// �����ļ����ݵõ�topicid��Ӧ��ROI
				while ((content = bufferedReader.readLine()) != null) {
					content = content.trim();
					m = p_topicid.matcher(content);
					if (m.find()) {
						ROI = content.substring(16);
						break;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
		return ROI;
	}

	public static void readfile(String fileid, String docno, String ROI) {
		String encoding = "GBK";
		FileWriter fw = null;
		BufferedWriter bw = null;
		String readpath = "C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Segmentation\\" + fileid + "\\" + docno;
		try {
			File readfile = new File(readpath);
			if (readfile.exists() && readfile.isFile()) {
				// ���ǵ������ʽ
				InputStreamReader read = new InputStreamReader(new FileInputStream(readfile), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);

				// ����Ŀ¼
				File f = new File("C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Extraction\\" + ROI + "\\" + fileid);
				if (!f.exists()) {
					f.mkdirs();
				}
				// �����ļ�
				File doc = new File(f.toString() + "\\" + docno);
				if (!doc.exists()) {
					doc.createNewFile();
				}
				System.out.println(doc.getAbsolutePath());
				fw = new FileWriter(doc.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				String content = null;
				// д���ļ�
				while ((content = bufferedReader.readLine()) != null) {
					String[] words = null;
					// ���зִ�
					words = Tokenize(content);
					for (String word : words) {
						bw.write(word);
						bw.newLine();
					}
				}
				bw.close();
			} else {
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
	}

	// ���зִ�
	public static String[] Tokenize(String content) throws InvalidFormatException, IOException {
		InputStream is = new FileInputStream("en-token.bin");
		TokenizerModel model = new TokenizerModel(is);
		Tokenizer tokenizer = new TokenizerME(model);
		String tokens[] = tokenizer.tokenize(content);
		is.close();
		return tokens;
	}
}
