import processing.core.PApplet;
import processing.data.XML;

public class Hao_Zhou_Resolution extends DrawableTree
{
	private XML newTree;
	private boolean AndOrchange;
	public Hao_Zhou_Resolution(PApplet p, XML tree) 
	{ 
		super(p); 
		this.tree = tree; 
		dirtyTree = true;
	}
	private void itereliminateBiconditions(XML Node){	
		if (Node.getName().equals("bicondition")){
			Node.setName("and");
			XML[] children=Node.getChildren();
			XML newChild0=new XML("condition");
			newChild0.addChild(children[0]);
			newChild0.addChild(children[1]);
			Node.addChild(newChild0);
			XML newChild1=new XML("condition");
			newChild1.addChild(children[1]);
			newChild1.addChild(children[0]);
			Node.addChild(newChild1);
			for(int i=0;i<2;++i){
				Node.removeChild(children[i]);
			}
		}
		XML[] children=Node.getChildren();
		for(int i = 0; i < children.length; ++i){
			itereliminateBiconditions(children[i]);
		}
	}	
	private void itereliminateConditions(XML Node){
		if (Node.getName().equals("condition")){
			Node.setName("or");
			XML[] children=Node.getChildren();
			XML newChild0=new XML("not");
			newChild0.addChild(children[0]);
			Node.addChild(newChild0);
			Node.addChild(children[1]);
			for(int i=0;i<2;++i){
				Node.removeChild(children[i]);
			}
		}
		XML[] children=Node.getChildren();
		for(int i = 0; i < children.length; ++i){
			itereliminateConditions(children[i]);
		}
	}
	private void itermoveNegationInwards(XML Node){
		if (Node.getName().equals("not")){
			XML Child=Node.getChild(0);
			if(Child.getName().equals("and")){
				Node.setName("or");
				XML[] subchildren=Child.getChildren();
				for(int i=0;i<subchildren.length;++i){
					XML newChild=new XML("not");
					newChild.addChild(subchildren[i]);
					Node.addChild(newChild);
				}
				Node.removeChild(Child);
			}
			if(Child.getName().equals("or")){
				Node.setName("and");
				XML[] subchildren=Child.getChildren();
				for(int i=0;i<subchildren.length;++i){
					XML newChild=new XML("not");
					newChild.addChild(subchildren[i]);
					Node.addChild(newChild);
				}
				Node.removeChild(Child);
			}
			if(Child.getName().equals("not")){
				Node.setName(Child.getChild(0).getName());
				Node.removeChild(Child);
				XML[]newchildren=Child.getChild(0).getChildren();
				for(int i=0;i<newchildren.length;++i){
					Node.addChild(newchildren[i]);
				}
			}
		}
		XML[] children=Node.getChildren();
		for(int i=0;i<children.length;++i){
			itermoveNegationInwards(children[i]);
		}
	}
		
	private void iterdistributeOrsOverAnds(XML Node){
		if (Node.getName().equals("or")){
			XML[] children=Node.getChildren();
			if(children[0].getName().equals("and")){
				XML[] andchildren=children[0].getChildren();
				Node.setName("and");
				XML newChild0=new XML("or");
				newChild0.addChild(children[1]);
				newChild0.addChild(andchildren[0]);
				Node.addChild(newChild0);
				XML newChild1=new XML("or");
				newChild1.addChild(children[1]);
				newChild1.addChild(andchildren[1]);
				Node.addChild(newChild1);
				for(int k=0;k<children.length;++k){
					Node.removeChild(children[k]);
				}
				this.AndOrchange=true;
			}
			else{
				if(children[1].getName().equals("and")){
					XML[] andchildren=children[1].getChildren();
					Node.setName("and");
					XML newChild0=new XML("or");
					newChild0.addChild(children[0]);
					newChild0.addChild(andchildren[0]);
					Node.addChild(newChild0);
					XML newChild1=new XML("or");
					newChild1.addChild(children[0]);
					newChild1.addChild(andchildren[1]);
					Node.addChild(newChild1);
					for(int k=0;k<children.length;++k){
						Node.removeChild(children[k]);
					}
					this.AndOrchange=true;
				}
			}
		}
		XML[] children=Node.getChildren();
		for(int i=0;i<children.length;++i){
			iterdistributeOrsOverAnds(children[i]);
		}
	}
	public void eliminateBiconditions()
	{
		// TODO - Implement the first step in converting logic in tree to CNF:
		// Replace all biconditions with truth preserving conjunctions of conditions.
		this.itereliminateBiconditions(this.tree);
		dirtyTree=true;
	}	
	
	public void eliminateConditions()
	{
		// TODO - Implement the second step in converting logic in tree to CNF:
		// Replace all conditions with truth preserving disjunctions.
		this.itereliminateConditions(this.tree);
		dirtyTree=true;
	}
		
	public void moveNegationInwards()
	{
		// TODO - Implement the third step in converting logic in tree to CNF:
		// Move negations in a truth preserving way to apply only to literals.
		this.itermoveNegationInwards(this.tree);
		dirtyTree=true;
	}
		
	public void distributeOrsOverAnds()
	{
		// TODO - Implement the fourth step in converting logic in tree to CNF:
		// Move negations in a truth preserving way to apply only to literals.
		do{
			this.AndOrchange=false;
			this.iterdistributeOrsOverAnds(this.tree);
			System.out.print(this.AndOrchange);
		}while(this.AndOrchange);
		dirtyTree=true;
	}

	private void collapseAnd(XML Node){
		XML[] children=Node.getChildren();
		for(int i=0;i<children.length;++i){
			if(children[i].getName().equals("and")){
				collapseAnd(children[i]);
			}
			else{
				if(!children[i].getName().equals("or")){
					XML newChild = new XML("or");
					newChild.addChild(children[i]);
					this.newTree.addChild(newChild);
				}
				else{
					this.newTree.addChild(children[i]);
				}
			}
		}
	}
	private void collapseOr(XML Node){
		for(int i=0;i<Node.getChildCount();++i){
			if(Node.getChild(i).getName().equals("or")){
				XML[]subchildren=Node.getChild(i).getChildren();
				for(int j=0;j<subchildren.length;++j){
					Node.addChild(subchildren[j]);
				}
				Node.removeChild(Node.getChild(i));
				i=i-1;
			}
		}
	}
	public void collapse()
	{
		// TODO - Clean up logic in tree in preparation for Resolution:
		// 1) Convert nested binary ands and ors into n-ary operators so
		// there is a single and-node child of the root logic-node, all of
		// the children of this and-node are or-nodes, and all of the
		// children of these or-nodes are literals: either atomic or negated	
		// 2) Remove redundant literals from every clause, and then remove
		// redundant clauses from the tree.
		// 3) Also remove any clauses that are always true (tautologies)
		// from your tree to help speed up resolution.
		//Step 1
		newTree=new XML("and");
		collapseAnd(this.tree);
		this.tree.removeChild(this.tree.getChild(0));
		this.tree.addChild(newTree);
		XML[] children=this.tree.getChild(0).getChildren();
		for(int i=0;i<children.length;++i){
			collapseOr(children[i]);
		}
		//Step 2
		children=this.tree.getChild(0).getChildren();
		for(int i = 0; i < children.length; i++){
			XML[] subchildren = children[i].getChildren();
			for(int j = 0; j < subchildren.length; j++){				
				children[i].removeChild(subchildren[j]);
				if (!clauseContainsLiteral(children[i], getAtomFromLiteral(subchildren[j]), isLiteralNegated(subchildren[j]))){
					children[i].addChild(subchildren[j]);
				}
			}
		}				
		children = this.tree.getChild(0).getChildren();
		for(int i = 0; i < children.length; i++){			
			this.tree.getChild(0).removeChild(children[i]);
			if(!setContainsClause(this.tree.getChild(0), children[i])){
				this.tree.getChild(0).addChild(children[i]);
			}
		}
		//Step 3
		children = this.tree.getChild(0).getChildren();
		for(int i = 0; i < children.length; i++){	
			if(clauseIsTautology(children[i])){
				this.tree.getChild(0).removeChild(children[i]);
			}
		}
		this.dirtyTree = true;
	}	
	
	public boolean applyResolution()
	{
		// TODO - Implement resolution on the logic in tree.  New resolvents
		// should be added as children to the only and-node in tree.  This
		// method should return true when a conflict is found, otherwise it
		// should only return false after exploring all possible resolvents.
		// Note: you are welcome to leave out resolvents that are always
		// true (tautologies) to help speed up your search.
		XML Parent=this.tree.getChild(0);
		int resonum;
		this.dirtyTree=true;
		do{
			resonum=Parent.getChildCount();
			XML[]children=Parent.getChildren();
			for(int i=0;i<children.length-1;++i){
				for(int j=i+1;j<children.length;++j){
					XML resolvent=resolve(children[i],children[j]);
					if(resolvent!=null){
						if(!resolvent.hasChildren()){
							return true;
						}
						if(!setContainsClause(Parent,resolvent)){
							Parent.addChild(resolvent);
						}
					}
				}
			}
		}while(Parent.getChildCount()>resonum);
		return false;
	}

	public XML resolve(XML clause1, XML clause2)
	{
		// TODO - Attempt to resolve these two clauses and return the resulting
		// resolvent.  You should remove any redundant literals from this 
		// resulting resolvent.  If there is a conflict, you will simply be
		// returning an XML node with zero children.  If the two clauses cannot
		// be resolved, then return null instead.
		XML[] children1 = clause1.getChildren();
		XML[] children2 = clause2.getChildren();		
		XML resolution = new XML("or");
		boolean resolvable = false;
		for(int i = 0; i < children1.length; i++){
			resolution.addChild(children1[i]);
		}
		for(int i = 0; i < children2.length; i++){
			resolution.addChild(children2[i]);
		}	
		XML[]resochildren=resolution.getChildren();
		for(int i = 0; i < children1.length; i++){
			if(clauseContainsLiteral(clause2, getAtomFromLiteral(children1[i]), !isLiteralNegated(children1[i]))){
				resolvable = true;
				resolution.removeChild(resochildren[i]);
				String name=getAtomFromLiteral(children1[i]);
				for(int j = 0; j < children2.length; j++){
					if(getAtomFromLiteral(children2[j]).equals(name)){
						resolution.removeChild(resochildren[children1.length+j]);
						break;
					}
				}
				break;
			}	
		}		
		if(resolvable == true){	
			XML[] children = resolution.getChildren();
			for(int i = 0; i < children.length; i++){
				resolution.removeChild(children[i]);	
				if (!clauseContainsLiteral(resolution, getAtomFromLiteral(children[i]), isLiteralNegated(children[i]))){
					resolution.addChild(children[i]);
				}
			}
			if(clauseIsTautology(resolution)){
				return null;
			}
			return resolution;
		}	
		return null;
	}	
	
	// REQUIRED HELPERS: may be helpful to implement these before collapse(), applyResolution(), and resolve()
	// Some terminology reminders regarding the following methods:
	// atom: a single named proposition with no children independent of whether it is negated
	// literal: either an atom-node containing a name, or a not-node with that atom as a child
	// clause: an or-node, all the children of which are literals
	// set: an and-node, all the children of which are clauses (disjunctions)
		
	public boolean isLiteralNegated(XML literal) 
	{ 
		// TODO - Implement to return true when this literal is negated and false otherwise.
		if(literal.getName().equals("not")){
			return true;
		}
		return false; 
	}

	public String getAtomFromLiteral(XML literal) 
	{ 
		// TODO - Implement to return the name of the atom in this literal as a string.
		String name;
		if(isLiteralNegated(literal)){
			name = literal.getChild(0).getName();
		}
		else{
			name = literal.getName();
		}
		return name;
	}	
	
	public boolean clauseContainsLiteral(XML clause, String atom, boolean isNegated)
	{
		// TODO - Implement to return true when the provided clause contains a literal
		// with the atomic name and negation (isNegated).  Otherwise, return false.	
		XML[] children = clause.getChildren();
		String name;		
		for(int i = 0; i < children.length; i++){
			if(isNegated){
				if(children[i].getName().equals("not")){
					name = children[i].getChild(0).getName();
					if(name.equals(atom)){
						return true;
					}
				}
			}
			else{
				name = children[i].getName();
				if(name.equals(atom)){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean setContainsClause(XML set, XML clause)
	{
		// TODO - Implement to return true when the set contains a clause with the
		// same set of literals as the clause parameter.  Otherwise, return false.
		XML[] clauseLiterals = clause.getChildren();
		XML[] setClauses = set.getChildren();		
		for (int i = 0; i < setClauses.length; ++i){
			if(setClauses[i].getChildCount() == clauseLiterals.length){
				int equalLiteralNum = 0;
				for (int j = 0; j < clauseLiterals.length; ++j){				
					if (clauseContainsLiteral(setClauses[i], getAtomFromLiteral(clauseLiterals[j]), isLiteralNegated(clauseLiterals[j]))){
						equalLiteralNum ++;
					}
				}
				if(equalLiteralNum == clauseLiterals.length){
					return true;
				}
			}
		}
		return false;
	}
	
	public boolean clauseIsTautology(XML clause)
	{
		// TODO - Implement to return true when this clause contains a literal
		// along with the negated form of that same literal.  Otherwise, return false.
		XML[] clauseLiterals = clause.getChildren();
		for (int i = 0; i < clauseLiterals.length; ++i){
			if (isLiteralNegated(clauseLiterals[i])){
			   if (clauseContainsLiteral(clause, getAtomFromLiteral(clauseLiterals[i]), false)){
				  return true;
			   }
			}
		}
		return false;
	}	
	
}
