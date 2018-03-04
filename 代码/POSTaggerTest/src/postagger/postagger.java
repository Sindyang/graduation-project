package postagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;

import edu.stanford.nlp.tagger.maxent.MaxentTagger;

public class postagger {

	static String encoding = "GBK";

	static Integer Accidents_NN = 0;
	static Integer Accidents_JJ = 0;
	static Integer Accidents_VB = 0;
	static Integer Accidents_RB = 0;
	static Integer Accidents_CD = 0;

	static Integer Acts_NN = 0;
	static Integer Acts_JJ = 0;
	static Integer Acts_VB = 0;
	static Integer Acts_RB = 0;
	static Integer Acts_CD = 0;

	static Integer Celebrity_NN = 0;
	static Integer Celebrity_JJ = 0;
	static Integer Celebrity_VB = 0;
	static Integer Celebrity_RB = 0;
	static Integer Celebrity_CD = 0;

	static Integer Elections_NN = 0;
	static Integer Elections_JJ = 0;
	static Integer Elections_VB = 0;
	static Integer Elections_RB = 0;
	static Integer Elections_CD = 0;

	static Integer Financial_NN = 0;
	static Integer Financial_JJ = 0;
	static Integer Financial_VB = 0;
	static Integer Financial_RB = 0;
	static Integer Financial_CD = 0;

	static Integer Legal_NN = 0;
	static Integer Legal_JJ = 0;
	static Integer Legal_VB = 0;
	static Integer Legal_RB = 0;
	static Integer Legal_CD = 0;

	static Integer Miscellaneous_NN = 0;
	static Integer Miscellaneous_JJ = 0;
	static Integer Miscellaneous_VB = 0;
	static Integer Miscellaneous_RB = 0;
	static Integer Miscellaneous_CD = 0;

	static Integer Natura_NN = 0;
	static Integer Natura_JJ = 0;
	static Integer Natura_VB = 0;
	static Integer Natura_RB = 0;
	static Integer Natura_CD = 0;

	static Integer New_NN = 0;
	static Integer New_JJ = 0;
	static Integer New_VB = 0;
	static Integer New_RB = 0;
	static Integer New_CD = 0;

	static Integer Political_NN = 0;
	static Integer Political_JJ = 0;
	static Integer Political_VB = 0;
	static Integer Political_RB = 0;
	static Integer Political_CD = 0;

	static Integer Scandals_NN = 0;
	static Integer Scandals_JJ = 0;
	static Integer Scandals_VB = 0;
	static Integer Scandals_RB = 0;
	static Integer Scandals_CD = 0;

	static Integer Science_NN = 0;
	static Integer Science_JJ = 0;
	static Integer Science_VB = 0;
	static Integer Science_RB = 0;
	static Integer Science_CD = 0;

	static Integer Sports_NN = 0;
	static Integer Sports_JJ = 0;
	static Integer Sports_VB = 0;
	static Integer Sports_RB = 0;
	static Integer Sports_CD = 0;

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		File filepath = new File("C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Result\\path\\path4.txt");
		try {
			if (filepath.isFile() && filepath.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(filepath), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);
				String path = null;

				File path5 = new File("C:\\Users\\wangsy\\Desktop\\Code\\TDT\\Result\\path\\path5.txt");
				if (!path5.exists()) {
					path5.createNewFile();
				}
				FileWriter fwpath5 = new FileWriter(path5.getAbsoluteFile());
				BufferedWriter bwpath5 = new BufferedWriter(fwpath5);

				// 读取每一个文件所在的目录
				while ((path = bufferedReader.readLine()) != null) {
					readfile(path, bwpath5);
					System.out.println(path);
				}
				bwpath5.close();

				System.out.println("Accidents_NN = " + Accidents_NN);
				System.out.println("Accidents_JJ = " + Accidents_JJ);
				System.out.println("Accidents_VB = " + Accidents_VB);
				System.out.println("Accidents_RB = " + Accidents_RB);
				System.out.println("Accidents_CD = " + Accidents_CD);

				System.out.println("Acts_NN = " + Acts_NN);
				System.out.println("Acts_JJ = " + Acts_JJ);
				System.out.println("Acts_VB = " + Acts_VB);
				System.out.println("Acts_RB = " + Acts_RB);
				System.out.println("Acts_CD = " + Acts_CD);

				System.out.println("Celebrity_NN = " + Celebrity_NN);
				System.out.println("Celebrity_JJ = " + Celebrity_JJ);
				System.out.println("Celebrity_VB = " + Celebrity_VB);
				System.out.println("Celebrity_RB = " + Celebrity_RB);
				System.out.println("Celebrity_CD = " + Celebrity_CD);

				System.out.println("Elections_NN = " + Elections_NN);
				System.out.println("Elections_JJ = " + Elections_JJ);
				System.out.println("Elections_VB = " + Elections_VB);
				System.out.println("Elections_RB = " + Elections_RB);
				System.out.println("Elections_CD = " + Elections_CD);

				System.out.println("Financial_NN = " + Financial_NN);
				System.out.println("Financial_JJ = " + Financial_JJ);
				System.out.println("Financial_VB = " + Financial_VB);
				System.out.println("Financial_RB = " + Financial_RB);
				System.out.println("Financial_CD = " + Financial_CD);

				System.out.println("Legal_NN = " + Legal_NN);
				System.out.println("Legal_JJ = " + Legal_JJ);
				System.out.println("Legal_VB = " + Legal_VB);
				System.out.println("Legal_RB = " + Legal_RB);
				System.out.println("Legal_CD = " + Legal_CD);

				System.out.println("Miscellaneous_NN = " + Miscellaneous_NN);
				System.out.println("Miscellaneous_JJ = " + Miscellaneous_JJ);
				System.out.println("Miscellaneous_VB = " + Miscellaneous_VB);
				System.out.println("Miscellaneous_RB = " + Miscellaneous_RB);
				System.out.println("Miscellaneous_CD = " + Miscellaneous_CD);

				System.out.println("Natura_NN = " + Natura_NN);
				System.out.println("Natura_JJ = " + Natura_JJ);
				System.out.println("Natura_VB = " + Natura_VB);
				System.out.println("Natura_RB = " + Natura_RB);
				System.out.println("Natura_CD = " + Natura_CD);

				System.out.println("New_NN = " + New_NN);
				System.out.println("New_JJ = " + New_JJ);
				System.out.println("New_VB = " + New_VB);
				System.out.println("New_RB = " + New_RB);
				System.out.println("New_CD = " + New_CD);

				System.out.println("Political_NN = " + Political_NN);
				System.out.println("Political_JJ = " + Political_JJ);
				System.out.println("Political_VB = " + Political_VB);
				System.out.println("Political_RB = " + Political_RB);
				System.out.println("Political_CD = " + Political_CD);

				System.out.println("Scandals_NN = " + Scandals_NN);
				System.out.println("Scandals_JJ = " + Scandals_JJ);
				System.out.println("Scandals_VB = " + Scandals_VB);
				System.out.println("Scandals_RB = " + Scandals_RB);
				System.out.println("Scandals_CD = " + Scandals_CD);

				System.out.println("Science_NN = " + Science_NN);
				System.out.println("Science_JJ = " + Science_JJ);
				System.out.println("Science_VB = " + Science_VB);
				System.out.println("Science_RB = " + Science_RB);
				System.out.println("Science_CD = " + Science_CD);

				System.out.println("Sports_NN = " + Sports_NN);
				System.out.println("Sports_JJ = " + Sports_JJ);
				System.out.println("Sports_VB = " + Sports_VB);
				System.out.println("Sports_RB = " + Sports_RB);
				System.out.println("Sports_CD = " + Sports_CD);

			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错1");
			e.printStackTrace();
		}
	}

	public static void readfile(String path, BufferedWriter bwpath5) {
		MaxentTagger tagger = new MaxentTagger("models/english-bidirectional-distsim.tagger");
		String writepath = path.replace("\\nonNE.txt", "");
		int i = path.indexOf("2003");
		String ROI = path.substring(37, i - 1);
		System.out.println(ROI);
		try {
			File doc = new File(path);
			if (doc.isFile() && doc.exists()) {
				// 考虑到编码格式
				InputStreamReader read = new InputStreamReader(new FileInputStream(doc), encoding);
				@SuppressWarnings("resource")
				BufferedReader bufferedReader = new BufferedReader(read);

				File nn = new File(writepath + "\\nn.txt");
				if (!nn.exists()) {
					nn.createNewFile();
				}
				FileWriter fwnn = new FileWriter(nn.getAbsoluteFile());
				BufferedWriter bwnn = new BufferedWriter(fwnn);

				File jj = new File(writepath + "\\jj.txt");
				if (!jj.exists()) {
					jj.createNewFile();
				}
				FileWriter fwjj = new FileWriter(jj.getAbsoluteFile());
				BufferedWriter bwjj = new BufferedWriter(fwjj);

				File vb = new File(writepath + "\\vb.txt");
				if (!vb.exists()) {
					vb.createNewFile();
				}
				FileWriter fwvb = new FileWriter(vb.getAbsoluteFile());
				BufferedWriter bwvb = new BufferedWriter(fwvb);

				File rb = new File(writepath + "\\rb.txt");
				if (!rb.exists()) {
					rb.createNewFile();
				}
				FileWriter fwrb = new FileWriter(rb.getAbsoluteFile());
				BufferedWriter bwrb = new BufferedWriter(fwrb);

				File cd = new File(writepath + "\\cd.txt");
				if (!cd.exists()) {
					cd.createNewFile();
				}
				FileWriter fwcd = new FileWriter(cd.getAbsoluteFile());
				BufferedWriter bwcd = new BufferedWriter(fwcd);

				String s = null;
				Boolean nnflag = true;
				Boolean jjflag = true;
				Boolean vbflag = true;
				Boolean rbflag = true;
				Boolean cdflag = true;
				// 读取每一个文件所在的目录
				while ((s = bufferedReader.readLine()) != null) {
					s = s.replace("  ", " ");
					String[] words = s.split(" ");
					for (String word : words) {
						String tagged = tagger.tagString(word);
						i = tagged.indexOf('_');
						String pos = tagged.substring(i + 1, tagged.length());
						pos = pos.trim();
						// System.out.println("pos = " + pos);
						String newword = word.substring(0, i);
						// System.out.println("word = " + word);
						// 名词
						if (pos.equals("NN") || pos.equals("NNP") || pos.equals("NNS")) {
							bwnn.write(newword);
							bwnn.newLine();
							calcount(ROI, "NN");
							if (nnflag) {
								bwpath5.write(writepath + "\\nn.txt");
								bwpath5.newLine();
								nnflag = false;
							}
						}
						// 形容词
						else if (pos.equals("JJ")) {
							bwjj.write(newword);
							bwjj.newLine();
							calcount(ROI, "JJ");
							if (jjflag) {
								bwpath5.write(writepath + "\\jj.txt");
								bwpath5.newLine();
								jjflag = false;
							}
						}
						// 动词
						else if (pos.equals("VBD") || pos.equals("VBN") || pos.equals("VBZ") || pos.equals("VBG")
								|| pos.equals("VB")) {
							bwvb.write(newword);
							bwvb.newLine();
							calcount(ROI, "VB");
							if (vbflag) {
								bwpath5.write(writepath + "\\vb.txt");
								bwpath5.newLine();
								vbflag = false;
							}
						}
						// 副词
						else if (pos.equals("RB")) {
							bwrb.write(newword);
							bwrb.newLine();
							calcount(ROI, "RB");
							if (rbflag) {
								bwpath5.write(writepath + "\\rb.txt");
								bwpath5.newLine();
								rbflag = false;
							}
						}
						// 数字
						else if (pos.equals("CD")) {
							bwcd.write(newword);
							bwcd.newLine();
							calcount(ROI, "CD");
							if (cdflag) {
								bwpath5.write(writepath + "\\cd.txt");
								bwpath5.newLine();
								cdflag = false;
							}
						}
					}
				}
				bwnn.close();
				bwjj.close();
				bwvb.close();
				bwrb.close();
				bwcd.close();
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错2");
			e.printStackTrace();
		}
	}

	public static void calcount(String ROI, String pos) {
		if (ROI.equals("Accidents")) {
			if (pos.equals("NN")) {
				Accidents_NN++;
			} else if (pos.equals("JJ")) {
				Accidents_JJ++;
			} else if (pos.equals("VB")) {
				Accidents_VB++;
			} else if (pos.equals("RB")) {
				Accidents_RB++;
			} else if (pos.equals("CD")) {
				Accidents_CD++;
			}
		} else if (ROI.equals("Acts of Violence or War")) {
			if (pos.equals("NN")) {
				Acts_NN++;
			} else if (pos.equals("JJ")) {
				Acts_JJ++;
			} else if (pos.equals("VB")) {
				Acts_VB++;
			} else if (pos.equals("RB")) {
				Acts_RB++;
			} else if (pos.equals("CD")) {
				Acts_CD++;
			}

		} else if (ROI.equals("Celebrity and Human Interest News")) {
			if (pos.equals("NN")) {
				Celebrity_NN++;
			} else if (pos.equals("JJ")) {
				Celebrity_JJ++;
			} else if (pos.equals("VB")) {
				Celebrity_VB++;
			} else if (pos.equals("RB")) {
				Celebrity_RB++;
			} else if (pos.equals("CD")) {
				Celebrity_CD++;
			}

		} else if (ROI.equals("Elections")) {
			if (pos.equals("NN")) {
				Elections_NN++;
			} else if (pos.equals("JJ")) {
				Elections_JJ++;
			} else if (pos.equals("VB")) {
				Elections_VB++;
			} else if (pos.equals("RB")) {
				Elections_RB++;
			} else if (pos.equals("CD")) {
				Elections_CD++;
			}

		} else if (ROI.equals("Financial News")) {
			if (pos.equals("NN")) {
				Financial_NN++;
			} else if (pos.equals("JJ")) {
				Financial_JJ++;
			} else if (pos.equals("VB")) {
				Financial_VB++;
			} else if (pos.equals("RB")) {
				Financial_RB++;
			} else if (pos.equals("CD")) {
				Financial_CD++;
			}

		} else if (ROI.equals("Legal Criminal Cases")) {
			if (pos.equals("NN")) {
				Legal_NN++;
			} else if (pos.equals("JJ")) {
				Legal_JJ++;
			} else if (pos.equals("VB")) {
				Legal_VB++;
			} else if (pos.equals("RB")) {
				Legal_RB++;
			} else if (pos.equals("CD")) {
				Legal_CD++;
			}

		} else if (ROI.equals("Miscellaneous News")) {
			if (pos.equals("NN")) {
				Miscellaneous_NN++;
			} else if (pos.equals("JJ")) {
				Miscellaneous_JJ++;
			} else if (pos.equals("VB")) {
				Miscellaneous_VB++;
			} else if (pos.equals("RB")) {
				Miscellaneous_RB++;
			} else if (pos.equals("CD")) {
				Miscellaneous_CD++;
			}

		} else if (ROI.equals("Natural Disasters")) {
			if (pos.equals("NN")) {
				Natura_NN++;
			} else if (pos.equals("JJ")) {
				Natura_JJ++;
			} else if (pos.equals("VB")) {
				Natura_VB++;
			} else if (pos.equals("RB")) {
				Natura_RB++;
			} else if (pos.equals("CD")) {
				Natura_CD++;
			}

		} else if (ROI.equals("New Laws")) {
			if (pos.equals("NN")) {
				New_NN++;
			} else if (pos.equals("JJ")) {
				New_JJ++;
			} else if (pos.equals("VB")) {
				New_VB++;
			} else if (pos.equals("RB")) {
				New_RB++;
			} else if (pos.equals("CD")) {
				New_CD++;
			}

		} else if (ROI.equals("Political and Diplomatic Meetings")) {
			if (pos.equals("NN")) {
				Political_NN++;
			} else if (pos.equals("JJ")) {
				Political_JJ++;
			} else if (pos.equals("VB")) {
				Political_VB++;
			} else if (pos.equals("RB")) {
				Political_RB++;
			} else if (pos.equals("CD")) {
				Political_CD++;
			}

		} else if (ROI.equals("Scandals Hearings")) {
			if (pos.equals("NN")) {
				Scandals_NN++;
			} else if (pos.equals("JJ")) {
				Scandals_JJ++;
			} else if (pos.equals("VB")) {
				Scandals_VB++;
			} else if (pos.equals("RB")) {
				Scandals_RB++;
			} else if (pos.equals("CD")) {
				Scandals_CD++;
			}

		} else if (ROI.equals("Science and Discovery News")) {
			if (pos.equals("NN")) {
				Science_NN++;
			} else if (pos.equals("JJ")) {
				Science_JJ++;
			} else if (pos.equals("VB")) {
				Science_VB++;
			} else if (pos.equals("RB")) {
				Science_RB++;
			} else if (pos.equals("CD")) {
				Science_CD++;
			}

		} else if (ROI.equals("Sports News")) {
			if (pos.equals("NN")) {
				Sports_NN++;
			} else if (pos.equals("JJ")) {
				Sports_JJ++;
			} else if (pos.equals("VB")) {
				Sports_VB++;
			} else if (pos.equals("RB")) {
				Sports_RB++;
			} else if (pos.equals("CD")) {
				Sports_CD++;
			}
		}
	}
}
