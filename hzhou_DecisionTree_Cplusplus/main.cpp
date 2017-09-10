//
//  main.cpp
//  hw1_760
//
//  Created by 周昊 on 16/9/25.
//  Copyright © 2016年 haozhou. All rights reserved.
//

#include <iostream>
#include "structure.h"
int M_SIZE;
int main(int argc, const char * argv[]) {
    vector<vector<double> > data;
    vector<vector<double> > test_data;
    ifstream traindata,testdata;
    string singleline;
    traindata.open(argv[1]);
    testdata.open(argv[2]);
    M_SIZE = atoi(argv[3]);
    while(getline(traindata,singleline)){
        if(singleline[0] == '@'){
            size_t pos = singleline.find_first_of(' ');
            string type = singleline.substr(1,pos-1);
            if(type == "attribute"){
                attribute attr;
                size_t pos_start = singleline.find_first_of("\'")+1;
                size_t pos_end = singleline.find_last_of("\'")-1;
                attr.name = singleline.substr(pos_start,pos_end-pos_start+1);
                if(singleline.find_first_of('{') == -1)
                    attr.type = 0;
                else{
                    attr.type = 1;
                    size_t temp_start = singleline.find_first_of('{')+2;
                    size_t temp_end = singleline.find_last_of('}')-1;
                    string temp = singleline.substr(temp_start,temp_end-temp_start+1);
                    while(temp.length()!=0 ){
                        size_t temp_pos = temp.find_first_of(',');
                        if (temp_pos == -1)
                            temp_pos = temp.length();
                        attr.value.push_back(temp.substr(0,temp_pos));
                        temp.erase(0,temp_pos+2);
                    }
                }
                if(attr.name == "class"){
                    for(int i = 0; i < attr.value.size(); i ++){
                        if(attr.value[i] == "negative")
                            C_Negative = i;
                        else if(attr.value[i] == "positive")
                            C_Positive = i;
                    }
                }
                attr.index = attr_table.size();
                attr_table.push_back(attr);
            }
        }else if(singleline.size() != 0){
            vector<double> instance;
            for(int i=0; i < attr_table.size(); i++){
                size_t pos = singleline.find_first_of(',');
                if(pos == -1)
                    pos = singleline.length();
                string value = singleline.substr(0,pos);
                if(attr_table[i].type == 0){
                    double temp_value;
                    istringstream(value)>>temp_value;
                    instance.push_back(temp_value);
                }else{
                    double temp_value;
                    temp_value = find(attr_table[i].value.begin(),attr_table[i].value.end(),value)-attr_table[i].value.begin();
                    instance.push_back(temp_value);
                }
                singleline.erase(0,pos+1);
            }
            data.push_back(instance);
        }
    }
    Node* root = new Node;
    root->isLeaf = false;
    FindTree(data,root,attr_table,0);
    plotTree(root,0);
    cout << "<Predictions for the Test Set Instances>" << endl;
    while(getline(testdata,singleline)){
        if(singleline[0] == '@'){
        }else if(singleline.size() != 0)
        {
            vector<double> instance;
            for(int i=0; i < attr_table.size(); i++){
                size_t pos = singleline.find_first_of(',');
                if(pos == -1)
                    pos = singleline.length();
                string value = singleline.substr(0,pos);
                if(attr_table[i].type == 0){
                    double temp_value;
                    istringstream(value)>>temp_value;
                    instance.push_back(temp_value);
                }else{
                    double temp_value;
                    temp_value = find(attr_table[i].value.begin(),attr_table[i].value.end(),value)-attr_table[i].value.begin();
                    instance.push_back(temp_value);
                }
                singleline.erase(0,pos+1);
            }
            test_data.push_back(instance);
        }
    }
    double accuracy=TestTree(test_data,root);
}
