//
//  haofunction.cpp
//  hw1_760
//
//  Created by 周昊 on 16/9/25.
//  Copyright © 2016年 haozhou. All rights reserved.
//

#include "structure.h"
vector<attribute> attr_table;
int C_Positive;
int C_Negative;

Str_Split FindSplit(vector<vector<double> > data, vector<attribute> attr){
    double MaxInfoGain = 0;
    vector<SplitData> splitdata;
    struct Str_Split split;
    split.type = -1;
    for( int i = 0; i < attr.size()-1; i ++){
        if(attr[i].type == 0){
            splitdata.clear();
            int total_neg = 0;	int total_pos = 0;
            size_t total = data.size();
            for(int j = 0; j < data.size(); j ++){
                struct SplitData instance;
                instance.number = data[j][attr[i].index];
                instance.label = data[j][data[j].size()-1];
                splitdata.push_back(instance);
                if(instance.label == C_Negative)
                    total_neg++;
                else
                    total_pos++;
            }
            double total_entropy = 0;
            double total_neg_port = (total_neg == 0)? 0:(double)total_neg/(double)total;
            double total_pos_port = (total_pos == 0)? 0:(double)total_pos/(double)total;
            if(total_neg_port != 0)
                total_entropy -= total_neg_port*log(total_neg_port);
            if(total_pos_port != 0)
                total_entropy -= total_pos_port*log(total_pos_port);
            total_entropy = total_entropy/log(2);
            
            int temp_neg = 0;	int temp_pos = 0;
            sort(splitdata.begin(),splitdata.end(),splitCmp());
            
            for(int j = 0; j < splitdata.size()-1; j ++){
                if(splitdata[j].label == C_Negative)
                    temp_neg++;
                else
                    temp_pos++;
                if(splitdata[j].number == splitdata[j+1].number)
                    continue;
                int temp_total = temp_neg + temp_pos;
                double entropy = 0.0;
                double entropy1 = 0.0;
                double entropy2 = 0.0;
                
                double neg_port1 = (temp_neg == 0)? 0:(double)temp_neg/(double)temp_total;
                if(neg_port1 != 0)
                    entropy1 -= neg_port1*log(neg_port1);
                double pos_port1 = (temp_pos == 0)? 0:(double)temp_pos/(double)temp_total;
                if(pos_port1 != 0)
                    entropy1 -= pos_port1*log(pos_port1);
                double first_port = (double)temp_total/(double)total;
                entropy1 = entropy1*(first_port);
                
                double neg_port2 = (total_neg - temp_neg == 0)? 0:(double)(total_neg - temp_neg)/(double)(total-temp_total);
                if(neg_port2 != 0)
                    entropy2 -= neg_port2*log(neg_port2);
                double pos_port2 = (total_pos - temp_pos == 0)? 0:(double)(total_pos - temp_pos)/(double)(total-temp_total);
                if(pos_port2 != 0)
                    entropy2 -= pos_port2*log(pos_port2);
                double second_port = 1 - first_port;
                entropy2 = entropy2*second_port;
                entropy = (entropy1+entropy2)/log(2);
                
                double info_gain = total_entropy - entropy;
                if(info_gain > MaxInfoGain){
                    split.type = 0;
                    split.name = attr[i].name;
                    split.split_value =((double)splitdata[j].number + (double)splitdata[j+1].number)/2;
                    split.index = attr[i].index;
                    MaxInfoGain = info_gain;
                }
            }
            
        }else if(attr[i].type == 1){
            double entropy = 0;
            double total_entropy = 0;
            int total = (int)data.size();
            int total_neg = 0;
            int total_pos = 0;
            int* count = new int[attr[i].value.size()];
            int* neg_count = new int[attr[i].value.size()];
            for(int j = 0; j < attr[i].value.size(); j++){
                count[j] = 0;
                neg_count[j] = 0;
            }
            for(int j = 0; j < data.size(); j++){
                count[(int)data[j][attr[i].index]]++;
                if(data[j][(int)data[j].size()-1] == C_Negative){
                    neg_count[(int)data[j][attr[i].index]]++;
                    total_neg ++;
                }
            }
            total_pos = total-total_neg;
            double total_neg_port = (double)total_neg/(double)(total);
            double total_pos_port = (double)total_pos/(double)(total);
            if(total_neg_port != 0)
                total_entropy -= total_neg_port*log(total_neg_port);
            if(total_pos_port != 0)
                total_entropy -= total_pos_port*log(total_pos_port);
            total_entropy = total_entropy/log(2);
            
            for(int j = 0; j < attr[i].value.size(); j ++){
                double neg_port = (double)neg_count[j]/(double)count[j];
                double temp_entropy = 0;
                if(count[j] != 0 && neg_port != 0)
                    temp_entropy -= neg_port*log(neg_port);
                double pos_port = 1 - neg_port;
                if(count[j] != 0 && pos_port != 0)
                    temp_entropy -= pos_port*log(pos_port);
                entropy += temp_entropy * ((double)count[j]/(double)total);
            }
            entropy = entropy/log(2);
            double info_gain = total_entropy - entropy;
            if(info_gain > MaxInfoGain){
                split.type = 1;
                split.name = attr[i].name;
                split.split_value = 0;
                split.index = attr[i].index;
                MaxInfoGain = info_gain;
            }
        }
    }
    return split;
}

vector<vector<double> > pruneDataNumerical(vector<vector<double> > data, int split_index, double split_value, int flag){
    vector<vector<double> > prunedata;
    for(int i = 0; i < data.size(); i ++){
        if(flag == -1 && data[i][split_index] <= split_value ){
            prunedata.push_back(data[i]);
        }
        if(flag == 1 && data[i][split_index] > split_value){
            prunedata.push_back(data[i]);
        }
    }
    return prunedata;
}

vector<vector<double> > pruneDataNominal(vector<vector<double> >data, int split_index, int index){
    vector<vector<double> > prunedata;
    for(int i = 0; i < data.size(); i ++){
        if( data[i][split_index] == index ){
            prunedata.push_back(data[i]);
        }
    }
    return prunedata;
    
}

vector<attribute> pruneAttr(vector<attribute> attr, int index){
    vector<attribute> pruneattr;
    for(int i = 0; i < attr.size(); i ++){
        if(attr[i].index == index)
            continue;
        pruneattr.push_back(attr[i]);
    }
    return pruneattr;
}

bool pureclass(vector<vector<double> > data){
    int label = 0;
    for(int i = 0; i < data.size(); i++){
        label += data[i][data[i].size()-1];
    }
    if(label == data.size()*C_Negative || label == data.size()*C_Positive)
        return true;
    return false;
}

int CountNegInstances(vector<vector<double> > data){
    int neg = 0;
    for(int i = 0; i < data.size(); i++){
        if(data[i][data[i].size()-1] == C_Negative)
            neg++;
    }
    return neg;
}

int GetAttrIndex(vector<attribute> attr, string name){
    int i = 0;
    for(i = 0; i < attr.size()-1; i++){
        if(attr[i].name == name)
            return i;
    }
    return -1;
}

Node* FindTree(vector<vector<double> >data, Node* node, vector<attribute> attr, int parlabel){
    if(data.size() == 0 || attr.size() == 0){
        node->isLeaf = true;
        node->label = 0;
        return node;
    }
    if(pureclass(data)){
        node->isLeaf = true;
        node->label = data[0][data[0].size()-1];
        return node;
    }
    struct Str_Split split = FindSplit(data, attr);
    if(data.size() < M_SIZE || split.type == -1){
        int num_neg = 0;
        for(int i = 0; i < data.size(); i ++){
            if(data[i][data[i].size()-1] == C_Negative)
                num_neg ++;
        }
        node->isLeaf = true;
        if(num_neg > data.size() - num_neg){
            node->label = 0;
        }
        if(num_neg < data.size() - num_neg){
            node->label = 1;
        }
        if(num_neg == data.size() - num_neg){
            node->label = parlabel;
        }
        return node;
    }
    node->name = split.name;
    node->type = split.type;
    node->split_value = split.split_value;
    
    int neglabel=CountNegInstances(data);
    if(neglabel > data.size()-neglabel){
        parlabel = 0;
    }
    if(neglabel < data.size()-neglabel){
        parlabel = 1;
    }
    if(neglabel == data.size()-neglabel){
        parlabel = parlabel;
    }
    
    if(node->type == 0){
        Node* newNode1 = new Node;
        newNode1->isLeaf = false;
        vector<vector<double> > prunedata1 = pruneDataNumerical(data,(int)split.index,node->split_value,-1);
        newNode1->neg_count = CountNegInstances(prunedata1);
        newNode1->pos_count = (int)prunedata1.size() - newNode1->neg_count;
        node->descendant.push_back(FindTree(prunedata1,newNode1,attr,parlabel));
        Node* newNode2 = new Node;
        newNode2->isLeaf = false;
        vector<vector<double> > prunedata2 = pruneDataNumerical(data,(int)split.index,node->split_value,1);
        newNode2->neg_count = CountNegInstances(prunedata2);
        newNode2->pos_count = (int)prunedata2.size() - newNode2->neg_count;
        node->descendant.push_back(FindTree(prunedata2,newNode2,attr,parlabel));
    }else if(node->type == 1){
        for(int i = 0; i < attr_table[split.index].value.size(); i ++){
            Node* newNode = new Node;
            newNode->isLeaf = false;
            vector<vector<double> > prunedata = pruneDataNominal(data,(int)split.index,i);
            newNode->neg_count = CountNegInstances(prunedata);
            newNode->pos_count = (int)prunedata.size() - newNode->neg_count;
            vector<attribute> pruneattr = pruneAttr(attr,(int)split.index);
            node->descendant.push_back(FindTree(prunedata,newNode,pruneattr,parlabel));
            node->descendant_value.push_back(attr_table[split.index].value[i]);
        }
    }
    return node;
}

void plotTree(Node* root, int depth){
    assert(root != 0);
    assert(root->isLeaf == false);
    cout << fixed;
    cout << setprecision(6);
    if(root->type == 0){
        for(int i = 0; i <depth; i++)
            cout << "|\t";
        cout << root->name << " <= " << root->split_value << " [" << root->descendant[0]->neg_count << " " << root->descendant[0]->pos_count << "]";
        if(root->descendant[0]->isLeaf)
            cout << ": "<< attr_table[attr_table.size()-1].value[root->descendant[0]->label]<<endl;
        else{
            cout << endl;
            plotTree(root->descendant[0],depth+1);
        }
        for(int i = 0; i <depth; i++)
            cout << "|\t";
        cout << root->name << " > " << root->split_value << " [" << root->descendant[1]->neg_count << " " << root->descendant[1]->pos_count << "]";
        if(root->descendant[1]->isLeaf)
            cout << ": "<< attr_table[attr_table.size()-1].value[root->descendant[1]->label] << endl;
        else{
            cout << endl;
            plotTree(root->descendant[1],depth+1);
        }
    }else{
        assert(root->descendant_value.size() != 1);
        for(int i = 0; i < root->descendant_value.size(); i++){
            for(int j = 0; j <depth; j++)
                cout << "|\t";
            cout << root->name << " = " << root->descendant_value[i] << " [" << root->descendant[i]->neg_count << " " << root->descendant[i]->pos_count << "]";
            assert(root->descendant[i] != 0);
            if(root->descendant[i]->isLeaf){
                cout << ": "<< attr_table[attr_table.size()-1].value[root->descendant[i]->label] <<endl;
            }else{
                cout << endl;
                plotTree(root->descendant[i],depth+1);
            }
        }
    }
}

double TestTree(vector<vector<double> >data, Node* root){
    int predict_right = 0;
    for(int i = 0; i < data.size(); i ++){
        Node* cur_root = root;
        while(cur_root != NULL){
            if(cur_root->isLeaf){
                if(cur_root->label == data[i][attr_table.size()-1])
                    predict_right++;
                cout << i+1 << ":" << " " << "Actual:" << " " << attr_table[attr_table.size()-1].value[data[i][attr_table.size()-1]] << " " << "Predicted:" << " " <<attr_table[attr_table.size()-1].value[cur_root->label] << endl;
                break;
            }
            int split_index = GetAttrIndex(attr_table,cur_root->name);
            assert(split_index != -1);
            if(cur_root->type == 0){
                if(data[i][split_index] <= cur_root->split_value)
                    cur_root = cur_root->descendant[0];
                else
                    cur_root = cur_root->descendant[1];
            }else{
                for(int j = 0; j < cur_root->descendant_value.size(); j ++){
                    if(data[i][split_index] == j){
                        cur_root = cur_root->descendant[j];
                        continue;
                    }
                }
            }
        }
    }
    cout << "Number of correctly classified:" << predict_right << " " << "Total number of test instances:" << data.size() << endl;
    return (double)predict_right/(double)data.size();
}

