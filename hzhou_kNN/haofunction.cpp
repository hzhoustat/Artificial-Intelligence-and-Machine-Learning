//
//  haofunction.cpp
//  hw2_760
//
//  Created by 周昊 on 16/10/16.
//  Copyright © 2016年 haozhou. All rights reserved.
//

#include "structure.h"
vector<string> class_table;
string task_type;
int dim=0;

double KNN(vector<vector<double> > test_data, vector<vector<double> > data){
    double predict_right = 0;
    cout << fixed;
    cout << setprecision(6);
    cout << "k value : " << K << "\n";
    for(int i = 0; i < test_data.size(); i ++){
        double predict_value = 0;
        vector <double> K_pos (K,-1);
        vector <double> K_dist (K,10000);
        for (int k = 0; k < K; k ++){
            K_dist[k] += (double) k;
        }
        for (int j =0; j < data.size(); j ++){
            double cur_dist = 0;
            for (int pos = 0; pos < (dim-1); pos ++){
                cur_dist += pow((test_data[i][pos]-data[j][pos]),2);
            }
            cur_dist = sqrt(cur_dist);
            int k = K;
            for (int pos = K-1; pos > -1; pos --){
                if(cur_dist > K_dist[pos] | cur_dist == K_dist[pos]){
                    break;
                }
                k = pos;
            }
            if (k < K){
                for (int pos = K-1; pos > k; pos --){
                    K_pos[pos] = K_pos [pos-1];
                    K_dist[pos] = K_dist[pos-1];
                }
                K_pos[k]=j;
                K_dist[k]=cur_dist;
            }
        }
        if(task_type == "regression"){
            for (int k = 0; k < K; k++){
                predict_value += data[K_pos[k]][dim-1];
            }
            predict_value = predict_value/K;
            predict_right += fabs(predict_value-test_data[i][dim-1]);
            cout << "Predicted value : " << predict_value << "\t\t Actual Value : " << test_data[i][dim-1] << "\n";
        }
        else if(task_type == "classification"){
            vector <int> count (class_table.size(),0);
            for (int k = 0; k < K; k++){
                count[data[K_pos[k]][dim-1]]++;
            }
            int max_class = 0;
            int tie=0;
            for (int pos = 1; pos < class_table.size(); pos++){
                if(count[pos] > count[max_class]){
                    max_class = pos;
                    tie=0;
                }else if (count[pos] == count[max_class]){
                    tie=1;
                }
            }
            if(tie == 0){
                predict_value = max_class;
            }else{
                predict_value = max_class;
                /* if(K_dist[0] < K_dist[1]){
                    predict_value = data[K_pos[0]][dim-1];
                }else{
                    predict_value = 0;
                }*/
            }
            if(predict_value == test_data[i][dim-1])
                predict_right++;
            cout << "Predicted class : " << class_table[predict_value] << "\t\t Actual class : " << class_table[test_data[i][dim-1]] << "\n";
        }
    }
    if(task_type == "regression"){
        predict_right=1/(double)test_data.size()*predict_right;
        cout << setprecision(16);
        cout << "Mean absolute error : " << predict_right << "\n";
        cout << "Total number of instances : " << test_data.size() << "\n";
    }else if(task_type == "classification"){
        cout << "Number of correctly classified instances : " << (int)predict_right << "\n";
        cout << "Total number of instances : " << test_data.size() << "\n";
        cout << setprecision(16);
        cout << "Accuracy : " << predict_right/(double)test_data.size() << "\n";
    }
    return(predict_right);
}