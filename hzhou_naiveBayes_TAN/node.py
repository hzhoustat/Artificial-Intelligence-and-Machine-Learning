import numpy as np

class node:
    def __init__(self):
        self.value = None
        self.own_prob = []
        self.parent = None
        self.parent_edge_prob = None
    def setValue(self, x):
        self.value = x
    def setown_prob(self,prob):
        self.own_prob.append(prob)
    def getown_prob(self,y):
        return self.own_prob[y][self.value]
    def setparent(self, x):
        self.parent = x
    def setparent_edge_prob(self,prob):
        self.parent_edge_prob = prob
    def getparent_edge_prob(self,y,attr_nodes):
        return self.parent_edge_prob[y][attr_nodes[self.parent].value][self.value]
        
