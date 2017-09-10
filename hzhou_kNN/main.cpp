//
//  main.cpp
//  hw2_760
//
//  Created by 周昊 on 16/10/16.
//  Copyright © 2016年 haozhou. All rights reserved.
//

#include <iostream>

#include <iostream>
#include "structure.h"
int K;
int main(int argc, const char * argv[]) {
    vector<vector<double> > data;
    vector<vector<double> > test_data;
    ifstream traindata,testdata;
    string singleline;
    traindata.open(argv[1]);
    testdata.open(argv[2]);
    K = atoi(argv[3]);
    while(getline(traindata,singleline)){
        if(singleline[0] == '@'){
            size_t pos = singleline.find_first_of(' ');
            string type = singleline.substr(1,pos-1);
            singleline.erase(0,pos+1);
            if(type == "attribute"){
                dim++;
                string tun_name;
                size_t pos = singleline.find_first_of(' ');
                tun_name = singleline.substr(0,pos);
                if(tun_name == "class"){
                    task_type = "classification";
                    size_t temp_start = singleline.find_first_of('{')+1;
                    size_t temp_end = singleline.find_last_of('}')-1;
                    string temp = singleline.substr(temp_start,temp_end-temp_start+1);
                    while(temp.length()!=0 ){
                        size_t temp_pos = temp.find_first_of(',');
                        if (temp_pos == -1)
                            temp_pos = temp.length();
                        class_table.push_back(temp.substr(0,temp_pos));
                        temp.erase(0,temp_pos+1);
                    }
                }
                if(tun_name == "response"){
                    task_type = "regression";
                }
            }
        }else if(singleline.size() != 0){
            vector<double> instance;
            for(int i=1; i < (dim+1); i++){
                size_t pos = singleline.find_first_of(',');
                if(pos == -1)
                    pos = singleline.length();
                string value = singleline.substr(0,pos);
                if(i == dim){
                    if(task_type == "regression"){
                        double temp_value;
                        istringstream(value)>>temp_value;
                        instance.push_back(temp_value);
                    }
                    if(task_type == "classification"){
                        double temp_value;
                        temp_value = find(class_table.begin(),class_table.end(),value)-class_table.begin();
                        instance.push_back(temp_value);
                    }
                }else{
                    double temp_value;
                    istringstream(value)>>temp_value;
                    instance.push_back(temp_value);
                }
                singleline.erase(0,pos+1);
            }
            data.push_back(instance);
        }
    }
    while(getline(testdata,singleline)){
        if(singleline[0] == '@'){
        }else if(singleline.size() != 0)
        {
            vector<double> instance;
            for(int i=1; i < (dim+1); i++){
                size_t pos = singleline.find_first_of(',');
                if(pos == -1)
                    pos = singleline.length();
                string value = singleline.substr(0,pos);
                if(i == dim){
                    if(task_type == "regression"){
                        double temp_value;
                        istringstream(value)>>temp_value;
                        instance.push_back(temp_value);
                    }
                    if(task_type == "classification"){
                        double temp_value;
                        temp_value = find(class_table.begin(),class_table.end(),value)-class_table.begin();
                        instance.push_back(temp_value);
                    }
                }else{
                    double temp_value;
                    istringstream(value)>>temp_value;
                    instance.push_back(temp_value);
                }
                singleline.erase(0,pos+1);
            }
            test_data.push_back(instance);
        }
    }
    double accuracy = KNN(test_data,data);
}
