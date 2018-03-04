/*
 * story.h
 *
 *  Created on: 2016年3月2日
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
	//story所属的文件id
	string id;
	//每个story所含有的单词总数
	int sum;
	//词汇和对应的个数
	map<string, int> word;
	//词汇和对应的tf值
	map<string, double> wordtf;
	//词汇和对应的tfidf值
	map<string, double> wordtfidf;
	//词汇和对应的权重
	map<string, double> wordweight;
	//余弦相似度
	double sim;
public:
	story(string id);
	//添加词汇
	void addword(string w, double weight);
	//统计词汇总数
	void calnumber();
	//统计词汇的tf值
	void caltf();
	//计算词汇的tfidf值
	void caltfidf(const map<string, double> &worddf, int count);
	//计算报道的余弦相似度
	double calsim(vector<story>::iterator it);
	//判断某词汇是否在报道中出现
	int judgeword(string w);
	//返回某一个词汇的tfidf值
	double getwordtfidf(string w);
	//得到报道的最大相似度
	void getstorysim(double sim);
	//输出相似度
	void putsim(ofstream &outfile);
	//返回报道的id
	string getid();
	//返回报道的所有word
	vector<string> getwords();
};

#endif /* STORY_H_ */
