import arff
import numpy as np

def standardize(traindata,testdata):
    p = len(traindata[0]) - 1
    n = len(traindata)
    mean = np.zeros(p)
    std = np.zeros(p)
    for i in range(p):
        if not isinstance(traindata[0][i], basestring):
            temp = []
            for j in range(n):
                temp.append(traindata[j][i])
            mean[i] = np.mean(temp)
            std[i] = np.std(temp)
        else:
            mean[i] = -10000
            std[i] = -10000
    for i in range(p):
        if mean[i] != -10000:
            for j in range(n):
                traindata[j][i] = (traindata[j][i] - mean[i])/std[i]            
            for k  in range(len(testdata)):
                testdata[k][i] = (testdata[k][i] - mean[i])/std[i]
    return [traindata, testdata, mean, std]


def forwardtrain(item,attribute, hidden_num,in_units,hidden_units, out_unit):
    input_num = len(in_units)
    j = 0
    for i in range(len(item)-1):
        if isinstance(item[i], basestring):
            num_attr = len(attribute[i][-1])
            pos = attribute[i][-1].index(item[i])
            for k in range(num_attr):
                if k == pos:
                    in_units[j].setValue(1)
                else:
                    in_units[j].setValue(0)
                j = j + 1
        else:
            in_units[j].setValue(item[i])
            j = j + 1
    in_units[-1].setValue(1)
    if hidden_num > 0:
        for i in range(hidden_num):
            weight = hidden_units[i].weight
            temp = 0
            for j in range(input_num):
                temp += in_units[j].value * weight[j]
            hidden_units[i].setValue(1/(1+np.exp(-temp)))
        hidden_units[-1].setValue(1)
        temp = 0
        for i in range(hidden_num+1):
            temp += hidden_units[i].value *out_unit.weight[i]
        out_unit.setValue(1/(1+np.exp(-temp)))
    else:
        temp = 0
        for i in range(input_num):
            temp += in_units[i].value * out_unit.weight[i]
        out_unit.setValue(1/(1+np.exp(-temp)))

def backPropagation(trueclass, learning_rate, attribute,hidden_num,in_units,hidden_units,out_unit):
    if hidden_num > 0:
        delta = out_unit.value - trueclass
        for i in range(len(hidden_units)):
            out_unit.weight[i] -= learning_rate * delta * hidden_units[i].value
        for j in range(len(hidden_units)-1):
            deltah = hidden_units[j].value * (1 - hidden_units[j].value) * delta * out_unit.weight[j]
            for k in range(len(in_units)):
                hidden_units[j].weight[k] -= learning_rate * deltah * in_units[k].value
    else:
        delta = out_unit.value - trueclass
        for i in range(len(in_units)):
            out_unit.weight[i] -= learning_rate * delta * in_units[i].value


def forwardtest(item,attribute, hidden_num,in_units,hidden_units, out_unit):
    input_num = len(in_units)
    j = 0
    for i in range(len(item)-1):
        if isinstance(item[i], basestring):
            num_value = len(attribute[i][-1])
            pos = attribute[i][-1].index(item[i])
            for k in range(num_value):
                if k == pos:
                    in_units[j].setValue(1)
                else:
                    in_units[j].setValue(0)
                j = j + 1
        else:
            in_units[j].setValue(item[i])
            j = j + 1
    in_units[-1].setValue(1)
    if hidden_num > 0:
        for i in range(hidden_num):
            weight = hidden_units[i].weight
            temp = 0
            for j in range(input_num):
                temp += in_units[j].value * weight[j]
            hidden_units[i].setValue(temp)
        hidden_units[-1].setValue(1)
        temp = 0
        for i in range(hidden_num+1):
            temp += hidden_units[i].value *out_unit.weight[i]       
        out_unit.setValue(1/(1+np.exp(-temp)))
    else:
        temp = 0
        for i in range(input_num):
            temp += in_units[i].value * out_unit.weight[i]
        out_unit.setValue(1/(1+np.exp(-temp)))
