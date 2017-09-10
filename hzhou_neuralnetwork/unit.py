import numpy as np

class unit:
    def __init__(self, utype):
        self.value = None
        self.weight = []
        self.utype = utype
        # 1, input node
        # doesn't have weight
        # 11, input bias
        # doesn't have weight
        # 2, hidden node
        # have weight linking input
        # 21, hidden bias
        # doesn't have weight
        # 3, output node
        # have weight linking hidden nodes

    def setWeight(self,l):
        if self.utype in [1, 11, 21]:
            self.weight = None
        else:
            self.weight = np.random.uniform(low=-0.01,high=0.01,size=l)

        
    def setValue(self, x):
        if self.utype == 11 or self.utype == 21:
            self.value =  1
        else:
            self.value = x














