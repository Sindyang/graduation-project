/*
 * main.cpp
 *
 *  Created on: 2016年3月3日
 *      Author: wangsy
 */
#include<iostream>
#include<fstream>
#include<string.h>
#include<algorithm>
#include<io.h>
#include "story.h"
using namespace std;

void getfiles(string path, vector<string>& files);
string getROI(string readpath);
string getNE(string readpath);
double getdatenum(string ROI);
double getlocationnum(string ROI);
double getmoneynum(string ROI);
double getorgannum(string ROI);
double getpernum(string ROI);
double getpersonnum(string ROI);
double gettimenum(string ROI);
double getjjnum(string ROI);
double getvbnum(string ROI);
double getrbnum(string ROI);
double getcdnum(string ROI);

int main() {
	//存储每一篇报道
	vector<story> allstory;
	vector<story>::iterator it;
	vector<story>::iterator itvec;

	//存储所有单词的df
	map<string, double> worddf;
	map<string, double>::iterator itdf;

	double maxsim = 0;
	double sim = 0;
	string maxid = "";

	ifstream in("D:\\TDT\\Result\\path\\path4.txt");
	//判断文件是否能打开
	if (!in) {
		cout << "fail to open the file";
		return 0;
	}

	int count = 0;
	int num = 0;
	string readpath;
	string word;
	string ROI;
	string NE;
	double weight;

	while (!in.eof()) {
		vector<string> files;

		//创建一个新报道
		getline(in, readpath);
		story newstory(readpath);

		ROI = getROI(readpath);
		//cout << "ROI = " << ROI << endl;

		//得到该目录下所有文件
		getfiles(readpath, files);
		int size = files.size();
		//读入报道的每一个单词
		for (int i = 0; i < size; i++) {
			string s = files[i];
			NE = getNE(files[i]);
			//cout << NE << endl;
			if ((NE == "afterStem") || (NE == "nonNE") || (NE == "nn")
					|| (NE == "jj") || (NE == "vb") || (NE == "rb")
					|| (NE == "cd"))
				continue;

			fstream filename(files[i].c_str());

			if (NE == "date") {
			 weight = getdatenum(ROI);
			 } else if (NE == "location") {
			 weight = getlocationnum(ROI);
			 } else if (NE == "money") {
			 weight = getmoneynum(ROI);
			 } else if (NE == "organization") {
			 weight = getorgannum(ROI);
			 } else if (NE == "percentage") {
			 weight = getpernum(ROI);
			 } else if (NE == "person") {
			 weight = getpersonnum(ROI);
			 } else if (NE == "time") {
			 weight = gettimenum(ROI);
			 } else if (NE == "nnafterStem") {
			 weight = 0.4;
			 } else if (NE == "jjafterStem") {
			 weight = 0.4 * getjjnum(ROI);
			 } else if (NE == "vbafterStem") {
			 weight = 0.4 * getvbnum(ROI);
			 } else if (NE == "rbafterStem") {
			 weight = 0.4 * getrbnum(ROI);
			 } else if (NE == "cdafterStem") {
			 weight = 0.4 * getcdnum(ROI);
			 } else {
			 cout << "The NE is wrong" << endl;
			 }

			while (!filename.eof()) {
				getline(filename, word);
				//去除空白行
				if (word != "") {
					newstory.addword(word, 1);
				}
			}
			filename.close();
		}

		//计算每个单词的df
		//得到该报道下所有单词
		vector<string> words = newstory.getwords();
		vector<string>::iterator itwords;
		for (itwords = words.begin(); itwords != words.end(); itwords++) {
			itdf = worddf.find(*itwords);
			if (itdf == worddf.end()) {
				worddf.insert(pair<string, int>(*itwords, weight));
			} else {
				itdf->second += 1;
			}
		}

		//统计报道中的单词总数
		newstory.calnumber();
		//计算每一个单词的tf
		newstory.caltf();
		allstory.push_back(newstory);
		count++;
		//每50篇报道作为一个单位进行计算
		if (count == 50 || allstory.size() == 10000) {
			//计算tf-idf值
			for (itvec = allstory.begin(); itvec != allstory.end(); itvec++) {
				itvec->caltfidf(worddf, allstory.size());
			}
			//计算余弦相似度
			for (itvec = allstory.begin() + 50 * num; itvec != allstory.end();
					itvec++) {
				for (it = allstory.begin(); it != itvec; it++) {
					sim = itvec->calsim(it);
					if (sim > maxsim) {
						maxsim = sim;
						maxid = it->getid();
					}
				}
				itvec->getstorysim(maxsim);
				cout << maxsim << endl;
				//cout << "id = " << itvec->getid() << endl;
				//cout << "maxid = " << maxid << endl;
				maxsim = 0;
				maxid = "";
			}
			count = 0;
			num++;
		}
	}
	in.close();

	cout << "allstory = " << allstory.size() << endl;
	//输出相似度
	ofstream outfile;
	outfile.open("D:\\TDT\\Result\\EX22\\EX22_sim.txt");
	if (!outfile) {
		cout << "fail to open the file" << endl;
	}
	for (itvec = allstory.begin(); itvec != allstory.end(); itvec++) {
		itvec->putsim(outfile);
	}
	outfile.close();
	return 0;
}

string getROI(string readpath) {
	int i = readpath.find("2003");
	string ROI = readpath.substr(11, i - 12);
	return ROI;
}

string getNE(string readpath) {
	int i = readpath.rfind("\\");
	string NE = readpath.substr(i + 1, readpath.length() - i - 5);
	return NE;
}

double getdatenum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.73;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.62;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.64;
	} else if (ROI == "Elections") {
		weight = 0.86;
	} else if (ROI == "Financial News") {
		weight = 0.61;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.60;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.50;
	} else if (ROI == "Natural Disasters") {
		weight = 0.95;
	} else if (ROI == "New Laws") {
		weight = 0.498;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.39;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.74;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.55;
	} else if (ROI == "Sports News") {
		weight = 0.67;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double getlocationnum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 1;
	} else if (ROI == "Acts of Violence or War") {
		weight = 1;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.91;
	} else if (ROI == "Elections") {
		weight = 0.96;
	} else if (ROI == "Financial News") {
		weight = 1;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 1;
	} else if (ROI == "Miscellaneous News") {
		weight = 1;
	} else if (ROI == "Natural Disasters") {
		weight = 1;
	} else if (ROI == "New Laws") {
		weight = 0.25;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 1;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.69;
	} else if (ROI == "Science and Discovery News") {
		weight = 1;
	} else if (ROI == "Sports News") {
		weight = 0.79;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double getmoneynum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.01;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.01;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.01;
	} else if (ROI == "Elections") {
		weight = 0.005;
	} else if (ROI == "Financial News") {
		weight = 0.01;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.01;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.01;
	} else if (ROI == "Natural Disasters") {
		weight = 0.01;
	} else if (ROI == "New Laws") {
		weight = 0.18;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.005;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.07;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.03;
	} else if (ROI == "Sports News") {
		weight = 0.01;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double getorgannum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.46;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.52;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.52;
	} else if (ROI == "Elections") {
		weight = 0.83;
	} else if (ROI == "Financial News") {
		weight = 0.37;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.64;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.38;
	} else if (ROI == "Natural Disasters") {
		weight = 0.30;
	} else if (ROI == "New Laws") {
		weight = 1;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.44;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.63;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.37;
	} else if (ROI == "Sports News") {
		weight = 0.42;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double getpernum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.01;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.01;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.005;
	} else if (ROI == "Elections") {
		weight = 0.09;
	} else if (ROI == "Financial News") {
		weight = 0.31;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.008;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.01;
	} else if (ROI == "Natural Disasters") {
		weight = 0.01;
	} else if (ROI == "New Laws") {
		weight = 0.04;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.005;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.01;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.06;
	} else if (ROI == "Sports News") {
		weight = 0.008;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double getpersonnum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.51;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.63;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 1;
	} else if (ROI == "Elections") {
		weight = 1;
	} else if (ROI == "Financial News") {
		weight = 0.43;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.74;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.37;
	} else if (ROI == "Natural Disasters") {
		weight = 0.35;
	} else if (ROI == "New Laws") {
		weight = 0.503;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.60;
	} else if (ROI == "Scandals Hearings") {
		weight = 1;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.26;
	} else if (ROI == "Sports News") {
		weight = 1;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double gettimenum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.10;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.06;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.03;
	} else if (ROI == "Elections") {
		weight = 0.05;
	} else if (ROI == "Financial News") {
		weight = 0.001;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.01;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.05;
	} else if (ROI == "Natural Disasters") {
		weight = 0.13;
	} else if (ROI == "New Laws") {
		weight = 0.02;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.03;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.05;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.008;
	} else if (ROI == "Sports News") {
		weight = 0.05;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double getjjnum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.12;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.12;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.15;
	} else if (ROI == "Elections") {
		weight = 0.16;
	} else if (ROI == "Financial News") {
		weight = 0.12;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.13;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.13;
	} else if (ROI == "Natural Disasters") {
		weight = 0.13;
	} else if (ROI == "New Laws") {
		weight = 0.13;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.15;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.12;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.16;
	} else if (ROI == "Sports News") {
		weight = 0.11;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double getvbnum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.3;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.32;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.33;
	} else if (ROI == "Elections") {
		weight = 0.31;
	} else if (ROI == "Financial News") {
		weight = 0.29;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.32;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.29;
	} else if (ROI == "Natural Disasters") {
		weight = 0.32;
	} else if (ROI == "New Laws") {
		weight = 0.39;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.28;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.32;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.3;
	} else if (ROI == "Sports News") {
		weight = 0.32;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double getrbnum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.03;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.03;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.04;
	} else if (ROI == "Elections") {
		weight = 0.03;
	} else if (ROI == "Financial News") {
		weight = 0.04;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.03;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.03;
	} else if (ROI == "Natural Disasters") {
		weight = 0.03;
	} else if (ROI == "New Laws") {
		weight = 0.05;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.02;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.04;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.02;
	} else if (ROI == "Sports News") {
		weight = 0.03;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

double getcdnum(string ROI) {
	double weight;
	if (ROI == "Accidents") {
		weight = 0.1;
	} else if (ROI == "Acts of Violence or War") {
		weight = 0.05;
	} else if (ROI == "Celebrity and Human Interest News") {
		weight = 0.04;
	} else if (ROI == "Elections") {
		weight = 0.06;
	} else if (ROI == "Financial News") {
		weight = 0.09;
	} else if (ROI == "Legal Criminal Cases") {
		weight = 0.05;
	} else if (ROI == "Miscellaneous News") {
		weight = 0.05;
	} else if (ROI == "Natural Disasters") {
		weight = 0.16;
	} else if (ROI == "New Laws") {
		weight = 0.06;
	} else if (ROI == "Political and Diplomatic Meetings") {
		weight = 0.03;
	} else if (ROI == "Scandals Hearings") {
		weight = 0.06;
	} else if (ROI == "Science and Discovery News") {
		weight = 0.06;
	} else if (ROI == "Sports News") {
		weight = 0.17;
	} else {
		cout << "The ROI is wrong" << endl;
	}
	return weight;
}

void getfiles(string path, vector<string>& files) {
	//文件句柄
	long hFile = 0;
	//文件信息
	struct _finddata_t fileinfo;
	string p;
	if ((hFile = _findfirst(p.assign(path).append("\\*").c_str(), &fileinfo))
			!= -1) {
		do {
			//如果是目录,迭代之
			//如果不是,加入列表
			if ((fileinfo.attrib & _A_SUBDIR)) {
				if (strcmp(fileinfo.name, ".") != 0
						&& strcmp(fileinfo.name, "..") != 0)
					getfiles(p.assign(path).append("\\").append(fileinfo.name),
							files);
			} else {
				files.push_back(
						p.assign(path).append("\\").append(fileinfo.name));
			}
		} while (_findnext(hFile, &fileinfo) == 0);
		_findclose(hFile);
	}
}

