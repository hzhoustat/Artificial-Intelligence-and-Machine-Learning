//
//  structure.h
//  hw2_760
//
//  Created by 周昊 on 16/10/16.
//  Copyright © 2016年 haozhou. All rights reserved.
//

#include <iostream>
#include <math.h>
#include <iomanip>
#include <algorithm>
#include <cfloat>
#include <assert.h>
#include <string>
#include <sstream>
#include <vector>
#include <fstream>
#include <cstdlib>

using namespace std;

extern vector<string> class_table;
extern string task_type;
extern int dim;
extern int K;
extern double KNN(vector<vector<double> > test_data,vector<vector<double> > data);