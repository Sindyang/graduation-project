/*
 * main.cpp
 *
 *  Created on: 2016��3��1��
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
	//�洢ÿһƪ����
	vector<story> allstory;
	vector<story>::iterator it;
	vector<story>::iterator itvec;

	//�洢���е��ʵ�df
	map<string, double> worddf;
	map<string, double>::iterator itdf;

	double maxsim = 0;
	double sim = 0;
	string maxid = "";

	ifstream in("D:\\TDT\\Result\\path4.txt");
	//�ж��ļ��Ƿ��ܴ�
	if (!in) {
		cout << "fail to open the file";
		return 0;
	}

	//count��¼�Ƿ񵽴ﴰ��ĩ��
	int count = 0;
	int num = 0;
	string readpath;
	string word;

	while (!in.eof()) {
		vector<string> files;

		//����һ���±���
		getline(in, readpath);
		story newstory(readpath);

		//�õ���Ŀ¼�������ĵ�
		getfiles(readpath, files);
		int size = files.size();
		//���뱨����ÿһ������
		for (int i = 0; i < size; i++) {
			//ȥ���ĵ�nonNE.txt
			if (i == 4)
				continue;
			fstream filename(files[i].c_str());
			//��ȡword
			while (!filename.eof()) {
				getline(filename, word);
				//ȥ���հ���
				if (word != "") {
					newstory.addword(word);
				}
			}
			filename.close();
		}

		//����ÿ�����ʵ�df
		//�õ��ñ��������е���
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

		//ͳ�Ʊ����еĵ�������
		newstory.calnumber();
		//����ÿһ�����ʵ�tf
		newstory.caltf();
		allstory.push_back(newstory);
		count++;
		//ÿ50ƪ������Ϊһ����λ���м���
		if (count == 50 || allstory.size() == 10000) {
			//����tf-idfֵ
			for (itvec = allstory.begin(); itvec != allstory.end(); itvec++) {
				itvec->caltfidf(worddf, allstory.size());
			}

			//�����������ƶ�
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
	//������ƶ�
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
	//�ļ����
	long hFile = 0;
	//�ļ���Ϣ
	struct _finddata_t fileinfo;
	string p;
	if ((hFile = _findfirst(p.assign(path).append("\\*").c_str(), &fileinfo))
			!= -1) {
		do {
			//�����Ŀ¼,����֮
			//�������,�����б�
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
