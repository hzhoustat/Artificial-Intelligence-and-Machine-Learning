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

struct SplitData{
    double number;
    int label;
};

struct splitCmp{
    bool operator()(const SplitData& a,const SplitData& b)const{
        if(a.number < b.number || (a.number == b.number && a.label < b.label))
            return true;
        else
            return false;
    }
};

struct Node
{
    string name;
    int label;
    bool isLeaf;
    int type;
    double split_value;
    int pos_count;
    int neg_count;
    vector<string> descendant_value;
    vector<Node*> descendant;
};

struct attribute
{
	string name;
	int type;
	vector<string> value;
	size_t index;
};

struct Str_Split
{
	string name;
	int type;
	double split_value;
    size_t index;
};

Str_Split FindSplit(vector<vector<double> > data,vector<attribute> attr);
Node* FindTree(vector<vector<double> >data,Node* node, vector<attribute> attr, int parlabel);
double TestTree(vector<vector<double> >data,Node* root);
void plotTree(Node* root, int depth);
extern int M_SIZE;
extern int C_Negative;
extern int C_Positive;
extern vector<attribute> attr_table;
