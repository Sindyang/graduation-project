/*
 * main.cpp
 *
 *  Created on: 2016年3月4日
 *      Author: wangsy
 */

#include<iostream>
#include<fstream>
#include<algorithm>
#include<vector>
using namespace std;
int main() {
	string w;
	string w1;
	string w2;
	double c = 0;
	double b = 0;
	double det = 0;
	double normdet = 0;
	double miss;
	double falsealarm;
	fstream file;
	vector<string> system;
	vector<string> intopic;
	vector<string>::iterator it;
	vector<string>::iterator itsys;
	file.open("D:\\TDT\\Result\\EX24\\EX24_intopic.txt");
	if (file.fail()) {
		cout << "fail to open the file" << endl;
		return 0;
	}
	while (!file.eof()) {
		getline(file, w);
		if (w != "")
			intopic.push_back(w);
	}
	file.close();

	for (it = intopic.begin(); it != intopic.end(); it++) {
		cout << *it << endl;
	}

	file.open("D:\\TDT\\Result\\answer.txt");
	if (file.fail()) {
		cout << "fail to open the file" << endl;
	}
	while (!file.eof()) {
		file.get();
		getline(file, w1);
		getline(file, w2);
		system.push_back(w2);
	}
	file.close();

	for (it = intopic.begin(); it != intopic.end(); it++) {
		itsys = find(system.begin(), system.end(), *it);
		if (itsys == system.end()) {
			c++;
		}
	}
	//计算miss
	cout << "c " << c << endl;
	miss = c / intopic.size();
	cout << "intopic " << intopic.size() << endl;
	//计算false
	b = system.size() - (intopic.size() - c);
	falsealarm = b / (10000 - intopic.size());
	det = 1 * miss * 0.02 + 0.1 * falsealarm * 0.98;
	normdet = det / min(1 * 0.02, 0.1 * 0.98);

	cout << "miss " << miss << endl;
	cout << "false " << falsealarm << endl;
	cout << "det " << det << endl;
	cout << "normdet " << normdet << endl;
	return 0;
}

