/*
 * main.cpp
 *
 *  Created on: 2016Äê3ÔÂ4ÈÕ
 *      Author: wangsy
 */

#include<iostream>
#include<fstream>
#include<algorithm>
#include<vector>
#include"story.h"
using namespace std;
bool cmp(story s1, story s2);
int main() {
	int num = 1;
	string w;
	double value;
	ifstream file;
	ofstream infile;
	vector<story> allstory;
	vector<story>::iterator it;
	file.open("D:\\TDT\\Result\\EX24\\EX24_sim.txt");
	if (file.fail()) {
		cout << "fail to open the file" << endl;
	}
	while (!file.eof()) {
		file.get();
		getline(file, w);
		file >> value;
		cout << value << endl;
		if (w != "") {
			story newstory(w, value);
			allstory.push_back(newstory);
		}
	}
	file.close();

	infile.open("D:\\TDT\\Result\\EX24\\EX24_intopic.txt");
	if (infile.fail()) {
		cout << "fail to open the infile" << endl;
	}
	sort(allstory.begin(), allstory.end(), cmp);
	for (it = allstory.begin(); it != allstory.end(); it++) {
		string s = it->getword();
		if (num <= 250) {
			infile.write(s.c_str(), s.length());
			infile << endl;
			cout << s.c_str() << endl;
		}
		num++;
	}

	cout << allstory.size() << endl;
	infile.close();
	return 0;
}

bool cmp(story s1, story s2) {
	return s1.getsim() < s2.getsim();
}

