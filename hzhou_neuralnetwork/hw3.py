import arff
import sys
import hz_function as hzf
import numpy as np
import unit as ut
import random as rd

learning_rate = 0.1
hidden_num = 20
epoch = 50
file1 = 'lymph_train.arff'
file2 = 'lymph_test.arff'

train = arff.load(open(file1,'rb'))
test = arff.load(open(file2,'rb'))

traindata = list(train['data'])
testdata = list(test['data'])
[traindata, testdata, mean, std] = hzf.standardize(traindata,testdata)

classvalue=list(train['attributes'][-1][-1])
attribute=list(train['attributes'])

p = len(traindata[0]) - 1
rd.shuffle(traindata)

in_units = []
for i in range(p):
    if not isinstance(traindata[0][i], basestring):
        newunit = ut.unit(1)
        newunit.setWeight(0)
        in_units.append(newunit)
    else:       
        num_attr = len(attribute[i][-1])
        for j in range(num_attr):
            newunit = ut.unit(1)
            newunit.setWeight(0)
            in_units.append(newunit)

newunit = ut.unit(11)
newunit.setWeight(0)
in_units.append(newunit)
input_num= len(in_units)

hidden_units = []
if hidden_num > 0:
    for i in range(hidden_num):
        newunit = ut.unit(2)
        newunit.setWeight(input_num)
        hidden_units.append(newunit)
    newunit = ut.unit(21)
    newunit.setWeight(0)
    hidden_units.append(newunit)

if hidden_num > 0:
    out_unit = ut.unit(3)
    out_unit.setWeight(hidden_num+1)
else:
    out_unit = ut.unit(3)
    out_unit.setWeight(input_num)

cur_epoch=0
print "training process"
print "epoch\t cross_entropy\t correct\t wrong\t"
while cur_epoch < epoch:
    cee=0
    cnum=0
    wnum=0
    for item in traindata:
        trueclass = classvalue.index(item[-1])
        hzf.forwardtrain(item, attribute, hidden_num,in_units,hidden_units, out_unit)
        hzf.backPropagation(trueclass, learning_rate , attribute,hidden_num,in_units,hidden_units,out_unit)
        if out_unit.value > 0.5 :
            label = 1
        else:
            label = 0
        if out_unit.value == 1 or out_unit.value == 0:
            if label != out_unit.value :
                cee += 99999
        else:
            cee -= trueclass*np.log(out_unit.value)+(1-trueclass)*np.log(1-out_unit.value)
        if label==trueclass:
            cnum+=1
        else:
            wnum+=1
    #print "{}\t {:.12f}\t {}\t {}".format(cur_epoch+1,cee,cnum,wnum) 
    cur_epoch += 1

for i in range(100):
    thresh=(i+1)/float(100)
    correct_num = 0
    wrong_num = 0
    actual_pos = 0
    actual_false = 0
    predict_pos_con_pos = 0
    predict_pos_con_false = 0
    print "testing process"
    print "activation\t predicted\t actual"
    for item in testdata:
        trueclass = classvalue.index(item[-1])
        hzf.forwardtest(item,attribute, hidden_num,in_units,hidden_units, out_unit)
        if out_unit.value > thresh:
            label = 1
        else:
            label = 0
        #print "{:.12f}\t {}\t {}".format(out_unit.value, attribute[-1][-1][label], item[-1])
        if trueclass == label :
                correct_num += 1
        else:
            wrong_num += 1
        if trueclass == 0 :
            actual_pos += 1
            if label == 0:
                predict_pos_con_pos +=1
        else:
            actual_false += 1
            if label == 0:
                predict_pos_con_false +=1
    
    print "total correct number: {}, total incorrect num: {}".format(correct_num, wrong_num)
    print "{}\t {}".format(predict_pos_con_pos/float(actual_pos),predict_pos_con_false/float(actual_false))
