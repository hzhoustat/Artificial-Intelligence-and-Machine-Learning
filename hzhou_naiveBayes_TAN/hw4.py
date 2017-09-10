import arff
import sys
import numpy as np
import random as rd
import node as nd
import hz_function as fun

file1 = sys.argv[1]
file2 = sys.argv[2]
method = sys.argv[3]

#file1 = 'lymph_train.arff'
#file2 = 'lymph_test.arff'
#file1 = 'vote_train.arff'
#file2 = 'vote_test.arff'
#method = 't'

train = arff.load(open(file1,'rb'))
test = arff.load(open(file2,'rb'))

traindata = list(train['data'])
testdata = list(test['data'])

classvalue=list(train['attributes'][-1][-1])
attribute=list(train['attributes'])

p = len(traindata[0]) - 1

######### Set attribute structure ##########
attr_nodes = []
for i in range(p):
    newnode = nd.node()
    attr_nodes.append(newnode)
    
Y_prior_node = nd.node()

######## Train Naive Bayes #########
if method == 'n':
    fun.trainNB(traindata,attribute,classvalue,attr_nodes,Y_prior_node)
    for i in range(p):
        print "{} {}".format(attribute[i][0],'class')

print ""

####### Train TAN ##################
if method == 't':
    fun.trainNB(traindata,attribute,classvalue,attr_nodes,Y_prior_node)
    fun.trainTAN(traindata,attribute,classvalue,attr_nodes,Y_prior_node)
    print "{} {}".format(attribute[0][0],'class')
    for i in range(1,p):
        print "{} {} {}".format(attribute[i][0],attribute[attr_nodes[i].parent][0],'class')
        
print ""        
            
######## Test Naive Bayes ##########
if method == 'n':
    correct_num = 0
    for item in testdata:
        trueclass = item[-1]
        [predictclass,posterior] = fun.testNB(item,attribute,classvalue,attr_nodes,Y_prior_node)
        print "{} {} {:.12f}".format(predictclass,trueclass,posterior)
        if predictclass == trueclass:
            correct_num += 1
    print ""
    print correct_num

######## Test TAN ##########
if method == 't':
    correct_num = 0
    for item in testdata:
        trueclass = item[-1]       
        [predictclass,posterior] = fun.testTAN(item,attribute,classvalue,attr_nodes,Y_prior_node)
        print "{} {} {:.12f}".format(predictclass,trueclass,posterior)
        if predictclass == trueclass:
            correct_num += 1
    print ""
    print correct_num



