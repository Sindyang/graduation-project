/*
 * story.h
 *
 *  Created on: 2016��3��2��
 *      Author: wangsy
 */

#ifndef STORY_H_
#define STORY_H_
#include<iostream>
#include<string>
#include<map>
#include<vector>
#include<fstream>
using namespace std;
class story {
private:
	//story�������ļ�id
	string id;
	//ÿ��story�����еĵ�������
	int sum;
	//�ʻ�Ͷ�Ӧ�ĸ���
	map<string, int> word;
	//�ʻ�Ͷ�Ӧ��tfֵ
	map<string, double> wordtf;
	//�ʻ�Ͷ�Ӧ��tfidfֵ
	map<string, double> wordtfidf;
	//�ʻ�Ͷ�Ӧ��Ȩ��
	map<string, double> wordweight;
	//�������ƶ�
	double sim;
public:
	story(string id);
	//��Ӵʻ�
	void addword(string w, double weight);
	//ͳ�ƴʻ�����
	void calnumber();
	//ͳ�ƴʻ��tfֵ
	void caltf();
	//����ʻ��tfidfֵ
	void caltfidf(const map<string, double> &worddf, int count);
	//���㱨�����������ƶ�
	double calsim(vector<story>::iterator it);
	//�ж�ĳ�ʻ��Ƿ��ڱ����г���
	int judgeword(string w);
	//����ĳһ���ʻ��tfidfֵ
	double getwordtfidf(string w);
	//�õ�������������ƶ�
	void getstorysim(double sim);
	//������ƶ�
	void putsim(ofstream &outfile);
	//���ر�����id
	string getid();
	//���ر���������word
	vector<string> getwords();
};

#endif /* STORY_H_ */
