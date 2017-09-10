import numpy as np
import math

def Laplace_estimate(subset):
    curset = subset
    curset += 1
    total_count = np.sum(curset)
    curset /=total_count
    return curset

    
def Calculate_edge_prob (dataset,attribute,i,j):
    n = len(dataset)
    sub_i_num = len(attribute[i][-1])
    sub_j_num = len(attribute[j][-1])
    sub_edge_Matrix = np.zeros((sub_i_num,sub_j_num))
    for s in range(n):
            for k1 in range(sub_i_num):
                for k2 in range(sub_j_num):
                    if dataset[s][i] == attribute[i][-1][k1] and dataset[s][j] == attribute[j][-1][k2]:
                        sub_edge_Matrix[k1,k2] += 1
    prob_Matrix = Laplace_estimate(sub_edge_Matrix)
    return prob_Matrix
def Calculate_edge_prob_plus_y(dataset,attribute,i,j,classvalue):
    n = len(dataset)
    sub_i_num = len(attribute[i][-1])
    sub_j_num = len(attribute[j][-1])
    sub_edge_Matrix = np.zeros((2,sub_i_num,sub_j_num))
    for s in range(n):
        for k1 in range(sub_i_num):
            for k2 in range(sub_j_num):
                if dataset[s][i] == attribute[i][-1][k1] and dataset[s][j] == attribute[j][-1][k2] and dataset[s][-1] == classvalue[0]:
                    sub_edge_Matrix[0,k1,k2] += 1
    for s in range(n):
        for k1 in range(sub_i_num):
            for k2 in range(sub_j_num):
                if dataset[s][i] == attribute[i][-1][k1] and dataset[s][j] == attribute[j][-1][k2] and dataset[s][-1] == classvalue[1]:
                    sub_edge_Matrix[1,k1,k2] += 1
    prob_Matrix = Laplace_estimate(sub_edge_Matrix)
    return prob_Matrix

def Calculate_condition_edge_prob(traindata,attribute,classvalue,i,j):
    n = len(traindata)
    sub_i_num = len(attribute[i][-1])
    sub_j_num = len(attribute[j][-1])
    sub_edge_Matrix = np.zeros((2,sub_j_num,sub_i_num))
    for s in range(n):
        for k1 in range(sub_i_num):
            for k2 in range(sub_j_num):
                if traindata[s][i] == attribute[i][-1][k1] and traindata[s][j] == attribute[j][-1][k2] and traindata[s][-1] == classvalue[0]:
                    sub_edge_Matrix[0,k2,k1] += 1
    for s in range(n):
        for k1 in range(sub_i_num):
            for k2 in range(sub_j_num):
                if traindata[s][i] == attribute[i][-1][k1] and traindata[s][j] == attribute[j][-1][k2] and traindata[s][-1] == classvalue[1]:
                    sub_edge_Matrix[1,k2,k1] += 1
    prob_Matrix = np.zeros((2,sub_j_num,sub_i_num))
    for y in range(2):
        for k2 in range(sub_j_num):
            prob_Matrix[y,k2] = Laplace_estimate(sub_edge_Matrix[y,k2])
    return prob_Matrix

def Generate_mutual_inform(dataset,attribute,attr_nodes,classvalue,Y_prior_node):
    Y0 = []
    Y1 = []
    for i in range(len(dataset)):
        if dataset[i][-1] == classvalue[0]:
            Y0.append(dataset[i])
        elif dataset[i][-1] == classvalue[1]:
            Y1.append(dataset[i])
    p = len(dataset[0])-1
    MI_matrix = np.zeros((p,p))
    ### I use upper triangle to store edge weight ###
    for i in range(p-1):
        sub_i_num = len(attribute[i][-1])
        for j in range(i+1,p):
            sub_j_num = len(attribute[j][-1])
            y_value = 0
            prob_joint_on_edge = Calculate_edge_prob(Y0,attribute,i,j)
            prob_joint_on_edge_plus_y = Calculate_edge_prob_plus_y(dataset,attribute,i,j,classvalue)
            for k1 in range(sub_i_num):
                for k2 in range(sub_j_num):
                    MI_matrix[i,j] += prob_joint_on_edge_plus_y[0,k1,k2]*math.log(prob_joint_on_edge[k1,k2]/
                    (attr_nodes[i].own_prob[y_value][k1]*attr_nodes[j].own_prob[y_value][k2]),2)
            y_value = 1
            prob_joint_on_edge = Calculate_edge_prob(Y1,attribute,i,j)
            for k1 in range(sub_i_num):
                for k2 in range(sub_j_num):
                    MI_matrix[i,j] += prob_joint_on_edge_plus_y[1,k1,k2]*math.log(prob_joint_on_edge[k1,k2]/
                    (attr_nodes[i].own_prob[y_value][k1]*attr_nodes[j].own_prob[y_value][k2]),2)                    
    return MI_matrix

def Generate_tree_edge(mutual_inform):
    p = len(mutual_inform)
    nodes_passed = []
    edges = []
    nodes_stored = range(p)
    nodes_passed.append(0)
    nodes_stored.remove(0)
    for iteration in range(p-1):
        max_edge = []
        next_node = None
        max_weight = 0
        for i in range(len(nodes_passed)):
            for j in range(len(nodes_stored)):
                cur_weight = mutual_inform[nodes_passed[i],nodes_stored[j]]
                if cur_weight > max_weight:
                    max_weight = cur_weight
                    max_edge = [nodes_passed[i],nodes_stored[j]]
                    next_node = nodes_stored[j]
        edges.append(max_edge)
        nodes_passed.append(next_node)
        nodes_stored.remove(next_node)
    return edges


def trainNB(traindata,attribute,classvalue,attr_nodes,Y_prior_node):
    p = len(traindata[0]) - 1
    n = len(traindata)
    Y0 = []
    Y1 = []
    for i in range(n):
        if traindata[i][-1] == classvalue[0]:
            Y0.append(traindata[i])
        elif traindata[i][-1] == classvalue[1]:
            Y1.append(traindata[i])
    n0 = len(Y0)
    n1 = len(Y1)
    Y_prior_node.setown_prob(float(n0+1)/(n0+n1+2))
    Y_prior_node.setown_prob(float(n1+1)/(n0+n1+2))
    for i in range(p):
        subset_num = len(attribute[i][-1])
        subset_count = np.zeros(subset_num)
        for j in range(n0):
            for k in range(subset_num):
                if Y0[j][i] == attribute[i][-1][k]:
                    subset_count[k] += 1
        subset_prob = Laplace_estimate(subset_count)
        subset_prob = subset_prob.tolist()
        attr_nodes[i].setown_prob(subset_prob)  
    for i in range(p):
        subset_num = len(attribute[i][-1])
        subset_count = np.zeros(subset_num)
        for j in range(n1):
            for k in range(subset_num):
                if Y1[j][i] == attribute[i][-1][k]:
                    subset_count[k] += 1
        subset_prob = Laplace_estimate(subset_count)
        attr_nodes[i].setown_prob(subset_prob)  
        
def testNB(item,attribute,classvalue,attr_nodes,Y_prior_node):
    posterior_vector = []
    p = len(item)-1
    for i in range(p):
        subset_num = len(attribute[i][-1])
        for k in range(subset_num):
            if item[i] == attribute[i][-1][k]:
                attr_nodes[i].setValue(k)
    #Y_prior_node.setValue(0)
    prob = Y_prior_node.own_prob[0]
    for i in range(p):
        prob *=attr_nodes[i].getown_prob(0)
    posterior_vector.append(prob)
    #Y_prior_node.setValue(1)
    prob = Y_prior_node.own_prob[1]
    for i in range(p):
        prob *=attr_nodes[i].getown_prob(1)
    posterior_vector.append(prob)
    standard_constant = sum(posterior_vector)
    predictclass = None
    if posterior_vector[0] < posterior_vector[1]:
        predictclass = classvalue[1]
        posterior = posterior_vector[1]/standard_constant
    else:
        predictclass = classvalue[0]
        posterior = posterior_vector[0]/standard_constant
    return [predictclass,posterior]


def trainTAN(traindata,attribute,classvalue,attr_nodes,Y_prior_node):
    mutual_inform = Generate_mutual_inform(traindata,attribute,attr_nodes,classvalue,Y_prior_node)
    p = len(mutual_inform)
    for j in range(p-1):
        for i in range(j+1,p):
            mutual_inform[i,j] = mutual_inform[j,i]
    tree_edges = Generate_tree_edge(mutual_inform)
    nodes_passed = [0]
    edges_stored = tree_edges
    while len(nodes_passed) > 0:
        cur_node = nodes_passed[0]
        remove_edges = []
        for iteration in range(len(edges_stored)):
            cur_edge = edges_stored[iteration]
            if cur_edge[0] == cur_node:
                attr_nodes[cur_edge[1]].setparent(cur_node)
                nodes_passed.append(cur_edge[1])
                remove_edges.append(cur_edge)
            if cur_edge[1] == cur_node:
                attr_nodes[cur_edge[0]].setparent(cur_node)
                nodes_passed.append(cur_edge[0])
                remove_edges.append(cur_edge)
        for iteration in range(len(remove_edges)):
            edges_stored.remove(remove_edges[iteration])
        nodes_passed.remove(cur_node)
    for i in range(1,p):
        j = attr_nodes[i].parent
        prob = Calculate_condition_edge_prob(traindata,attribute,classvalue,i,j)
        prob = prob.tolist()
        attr_nodes[i].setparent_edge_prob(prob)

def testTAN(item,attribute,classvalue,attr_nodes,Y_prior_node):
    posterior_vector = []
    p = len(item)-1
    for i in range(p):
        subset_num = len(attribute[i][-1])
        for k in range(subset_num):
            if item[i] == attribute[i][-1][k]:
                attr_nodes[i].setValue(k)
    #Y_prior_node.setValue(0)
    prob = Y_prior_node.own_prob[0]
    prob *= attr_nodes[0].getown_prob(0)
    for i in range(1,p):
        prob *= attr_nodes[i].getparent_edge_prob(0,attr_nodes)
    posterior_vector.append(prob)
    #Y_prior_node.setValue(1)
    prob = Y_prior_node.own_prob[1]
    prob *= attr_nodes[0].getown_prob(1)
    for i in range(1,p):
        prob *= attr_nodes[i].getparent_edge_prob(1,attr_nodes)
    posterior_vector.append(prob)
    standard_constant = sum(posterior_vector)
    predictclass = None
    if posterior_vector[0] < posterior_vector[1]:
        predictclass = classvalue[1]
        posterior = posterior_vector[1]/standard_constant
    else:
        predictclass = classvalue[0]
        posterior = posterior_vector[0]/standard_constant
    return [predictclass,posterior]
