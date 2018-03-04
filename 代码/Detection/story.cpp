/*
 * story.cpp
 *
 *  Created on: 2015Äê12ÔÂ11ÈÕ
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
