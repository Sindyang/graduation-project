/*
 * main.cpp
 *
 *  Created on: 2016年3月1日
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

	ifstream in("D:\\TDT\\Result\\path4.txt");
	//判断文件是否能打开
	if (!in) {
		cout << "fail to open the file";
		return 0;
	}

	//count记录是否到达窗口末端
	int count = 0;
	int num = 0;
	string readpath;
	string word;

	while (!in.eof()) {
		vector<string> files;

		//创建一个新报道
		getline(in, readpath);
		story newstory(readpath);

		//得到该目录下所有文档
		getfiles(readpath, files);
		int size = files.size();
		//读入报道的每一个单词
		for (int i = 0; i < size; i++) {
			//去除文档nonNE.txt
			if (i == 4)
				continue;
			fstream filename(files[i].c_str());
			//读取word
			while (!filename.eof()) {
				getline(filename, word);
				//去除空白行
				if (word != "") {
					newstory.addword(word);
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
				worddf.insert(pair<string, int>(*itwords, 1));
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
				cout << "id = " << itvec->getid() << endl;
				cout << "maxid = " << maxid << endl;
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
	outfile.open("D:\\TDT\\Result\\EX1_sim.txt");
	if (!outfile) {
		cout << "fail to open the file" << endl;
	}
	for (itvec = allstory.begin(); itvec != allstory.end(); itvec++) {
		itvec->putsim(outfile);
	}
	outfile.close();
	return 0;
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
