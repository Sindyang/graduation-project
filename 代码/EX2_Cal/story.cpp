/*
 * story.cpp
 *
 *  Created on: 2016��3��2��
 *      Author: wangsy
 */

#include "story.h"
#include<cmath>
#include<fstream>
story::story(string id) {
	this->id = id;
	sum = 0;
	sim = 0;
}

//��Ӵʻ�
void story::addword(string w, double weight) {
	map<string, int>::iterator itword;
	itword = word.find(w);
	//�жϸô���word���Ƿ����
	if (itword != word.end()) {
		itword->second += 1;
	} else {
		//word->second��ʾ������ֵĴ���
		wordweight.insert(pair<string, double>(w, weight));
		word.insert(pair<string, int>(w, 1));
	}
}

//������ƶ�
void story::putsim(ofstream &outfile) {
	outfile << id << endl;
	outfile << sim << endl;
}

//ͳ�ƴʻ�����
void story::calnumber() {
	map<string, int>::iterator itword;
	for (itword = word.begin(); itword != word.end(); itword++) {
		sum += itword->second;
	}
}

//ͳ�ƴʻ��tfֵ
void story::caltf() {
	map<string, int>::iterator itnum;
	double tf = 0;
	for (itnum = word.begin(); itnum != word.end(); itnum++) {
		tf = word.find(itnum->first)->second;
		tf /= sum;
		//cout << "tf = " << itnum->first << " " << tf << endl;
		wordtf.insert(pair<string, double>(itnum->first, tf));
	}
}

//����ÿһ���ʻ��tf-idfֵ
void story::caltfidf(const map<string, double> &worddf, int count) {
	map<string, double>::iterator itnum;
	double df = 0;
	double idf = 0;
	double tfidf = 0;
	double alltfidf = 0;
	//double newalltfidf = 0;
	vector<story>::iterator it;
	wordtfidf.clear();
	for (itnum = wordtf.begin(); itnum != wordtf.end(); itnum++) {
		df = worddf.find(itnum->first)->second;
		idf = log(count / df);
		//tfidf = tf*idf
		tfidf = itnum->second * idf;
		tfidf = tfidf * wordweight.find(itnum->first)->second;
		wordtfidf.insert(pair<string, double>(itnum->first, tfidf));
		alltfidf += tfidf;
	}
	//��tfidfֵ���й�һ��
	for (itnum = wordtfidf.begin(); itnum != wordtfidf.end(); itnum++) {
		itnum->second /= alltfidf;
	}
}

//�ж�ĳ�ʻ��Ƿ��ڱ����г���
int story::judgeword(string w) {
	if (word.find(w) != word.end())
		return 1;
	else
		return 0;
}

//����ĳһ���ʻ��tfidfֵ
double story::getwordtfidf(string w) {
	if (wordtfidf.find(w) != wordtfidf.end())
		return wordtfidf.find(w)->second;
	else
		return 0;
}

//�õ����������ƶ�
void story::getstorysim(double sim) {
	this->sim = sim;
}

//���ر�����id
string story::getid() {
	return id;
}

//���ر���������word
vector<string> story::getwords() {
	map<string, int>::iterator it;
	vector<string> words;
	for (it = word.begin(); it != word.end(); it++) {
		words.push_back(it->first);
	}
	return words;
}

//���㱨����hellinger���ƶ�
double story::calsim(vector<story>::iterator it) {
	double sim = 0;
	map<string, double>::iterator itnum;
	for (itnum = wordtfidf.begin(); itnum != wordtfidf.end(); itnum++) {
		sim += sqrt(
				it->getwordtfidf(itnum->first)
						* wordtfidf.find(itnum->first)->second);
	}
	return sim;
}
