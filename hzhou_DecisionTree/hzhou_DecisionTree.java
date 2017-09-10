import processing.core.*;
import processing.data.*;

public class Hao_Zhou_DecisionTree extends DrawableTree
{
	public Hao_Zhou_DecisionTree(PApplet p) { super(p); }
		
	// This method loads the examples from the provided filename, and
	// then builds a decision tree (stored in the inherited field: tree).
	// Each of the nodes in this resulting tree will be named after
	// either an attribute to split on (vote01, vote02, etc), or a party
	// classification (DEMOCRAT, REPUBLICAN, or possibly TIE).
	public void removewhitespace(XML xml){
		String b = "#text";
		XML[] children = xml.getChildren();
		for (int i = 0; i < children.length; ++i) {
			String name = children[i].getName();
			if ( name.equals(b) | name.contentEquals(""))
			{
				xml.removeChild(children[i]);
			}
		}
	}
	
	public void learnFromTrainingData(String filename)
	{
		// NOTE: Set the inherited field dirtyTree to true after building the
		// decision tree and storing it in the inherited field tree.  This will
		// trigger the DrawableTree's graphical rendering of the tree.
		
		XML dataset = p.loadXML(filename);
		removewhitespace(dataset);
		tree=new XML("tree");
		recursiveBuildTree(dataset,tree);
		dirtyTree=true;
	}
			
	// This method recursively builds a decision tree based on
	// the set of examples that are children of dataset.
	public void recursiveBuildTree(XML dataset, XML tree)
	{
		// NOTE: You MUST add YEA branches to your decision nodes before
		// adding NAY branches.  This will result in YEA branches being
		// child[0], which will be drawn to the left of any NAY branches.
		// The grading tests assume that you are following this convention.
		
		String splitattribute=chooseSplitAttribute(dataset);
		XML split1=new XML("dataset"), split2=new XML("dataset");
		XML[] children=dataset.getChildren();
		if(splitattribute!="None"){
			tree.setName(splitattribute);
			XML child1=tree.addChild("tree");
			XML child2=tree.addChild("tree");
			for (int i = 0; i < children.length; ++i) {
				String attributevalue=children[i].getString(splitattribute);
				if(attributevalue.equals("YEA")){
					split1.addChild(children[i]);
				}
				if(attributevalue.equals("NAY")){
					split2.addChild(children[i]);
				}
			}
			recursiveBuildTree(split1,child1);
			recursiveBuildTree(split2,child2);
		}
		else{
			tree.setName(plurality(dataset));		
		}
	}

	// This method calculates and returns the mode (most common value) among
	// the party attributes of the children examples under dataset.  If there
	// happens to be an exact tie, this method returns "TIE".
	public String plurality(XML dataset)
	{
		XML[] children = dataset.getChildren();
		int numREP=0, numDEM=0;
		String partylabel=null;
		for (int i = 0; i < children.length; ++i) {
			String party=children[i].getString("party");
			if(party.equals("REPUBLICAN")){
				numREP=numREP+1;
			}
			if(party.equals("DEMOCRAT")){
				numDEM=numDEM+1;
			}
		}
		if(numREP>numDEM){
			partylabel="REPUBLICAN";
		}
		if(numREP<numDEM){
			partylabel="DEMOCRAT";
		}
		if(numREP==numDEM){
			partylabel="TIE";
		}
		return partylabel;
	}

	// This method calculates and returns the name of the attribute that results
	// in the lowest entropy, after splitting all children examples according
	// to their value for this attribute into two separate groups: YEA vs. NAY.	
	public String chooseSplitAttribute(XML dataset)
	{
		XML firstchild = dataset.getChild("example");
		String[] attributes=firstchild.listAttributes();
		String chosenattribute="None";
		double presententropy=calculateEntropy(dataset);
		for (int i = 1; i < attributes.length; ++i) {
			double newentropy=calculatePostSplitEntropy(attributes[i],dataset);
			if(newentropy<presententropy){
				chosenattribute=attributes[i];
				presententropy=newentropy;
			}	
		}
		return chosenattribute;
	}
		
	// This method calculates and returns the entropy that results after 
	// splitting the children examples of dataset into two groups based
	// on their YEA vs. NAY value for the specified attribute.
	public double calculatePostSplitEntropy(String attribute, XML dataset)
	{		
		XML[] children = dataset.getChildren();
		XML split1=new XML("dataset"),split2=new XML("dataset");
		double postsplitentropy=0;
		double n1=0,n2=0,split1entropy=0,split2entropy=0;
		for (int i = 0; i < children.length; ++i) {
			String YorN=children[i].getString(attribute);
			if(YorN.equals("YEA")){
				split1.addChild(children[i]);
				n1=n1+1;
			}
			if(YorN.equals("NAY")){
				split2.addChild(children[i]);
				n2=n2+1;
			}
			
		}
		if(n1!=0){
			split1entropy=calculateEntropy(split1);
		}
		if(n2!=0){
			split2entropy=calculateEntropy(split2);
		}
		postsplitentropy=n1/(n1+n2)*split1entropy+n2/(n1+n2)*split2entropy;
		//postsplitentropy=n1/(n1+n2)*calculateEntropy(split1)+n2/(n1+n2)*calculateEntropy(split2);
		return postsplitentropy;
	}
	
	// This method calculates and returns the entropy for the children examples
	// of a single dataset node with respect to which party they belong to.
	public double log(double x,double e){
		double result=0;
		if(x!=0){
			result=Math.log(x)/Math.log(e);
		}
		return result;
	}
	public double calculateEntropy(XML dataset)
	{
		XML[] children = dataset.getChildren();
		double entropy=0, n=children.length, m=0;
		for (int i = 0; i < children.length; ++i) {
			String party=children[i].getString("party");
			if(party.equals("REPUBLICAN")){
				m=m+1;
			}
		}
		if(n!=0){
			entropy=Math.max(-m/n*log(m/n,2)-(n-m)/n*log((n-m)/n,2),0);
		}
		return entropy;
	}

	// This method calculates and returns the entropy of a Boolean random 
	// variable that is true with probability q (as on page 704 of the text).
	// Don't forget to use the limit, when q makes this formula unstable.
	public static double B(double q)
	{
		double B=0;
		if(q >0 && q<1){
			B=-(q*Math.log(q)/Math.log(2)+(1-q)*Math.log(1-q)/Math.log(2));
		}	
		return B;
	}

	// This method loads and runs an entire file of examples against the 
	// decision tree, and returns the percentage of those examples that this
	// decision tree correctly predicts.
	public double runTests(String filename)
	{
		XML testset=p.loadXML(filename);
		removewhitespace(testset);
		XML[] children=testset.getChildren();
		double m=0;
		double n=children.length;
		for(int i=0;i<children.length;++i){
			String predictlabel=predict(children[i],tree);
			String truelabel=children[i].getString("party");
			//System.out.println(predictlabel+"is"+truelabel+"\n");
			if(predictlabel.equals(truelabel)){
				m=m+1;
			}
		}
		return m/n;
	}
	
	// This method runs a single example through the decision tree, and then 
	// returns the party that this tree predicts the example to belonging to.
	// If this example contains a party attribute, it should be ignored here.	
	public String predict(XML example, XML decisionTree)
	{
		String h=null,YorN;
		String predictlabel=null;
		XML[] children=decisionTree.getChildren();
		h=decisionTree.getName();
		if((!h.equals("REPUBLICAN"))&(!h.equals("DEMOCRAT"))&(!h.equals("TIE"))){
			YorN=example.getString(h);
			if(YorN.equals("YEA")){
				predictlabel=predict(example,children[0]);
			}
			if(YorN.equals("NAY")){
				predictlabel=predict(example,children[1]);
			}
		}
		else {
			predictlabel=h;
		}
		return predictlabel;
	}
	public static boolean useLoop(String[] arr, String targetValue) {
	    for(String s: arr){
	        if(s.equals(targetValue))
	            return true;
	    }
	    return false;
	}
}
