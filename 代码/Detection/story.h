/*
 * story.h
 *
 *  Created on: 2015��12��11��
 *      Author: wangsy
 */

#ifndef STORY_H_
#define STORY_H_

#include<iostream>
#include<string>
using namespace std;
class story {
private:
	string word;
	double sim;
public:
	story();
	story(string word, double sim);
	string getword();
	double getsim();
};

#endif /* STORY_H_ */
