/*
 * story.cpp
 *
 *  Created on: 2015��12��11��
 *      Author: wangsy
 */
#include"story.h"
story::story() {
	sim = 0;
}

story::story(string word, double sim) {
	this->word = word;
	this->sim = sim;
}

string story::getword() {
	return word;
}

double story::getsim() {
	return sim;
}
