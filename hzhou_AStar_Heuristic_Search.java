import java.util.ArrayList;
import java.util.Collections;

public class Hao_Zhou_AStar 
{		
	public ArrayList<SearchPoint> frontier=new ArrayList<SearchPoint>();;
	public ArrayList<SearchPoint> explored=new ArrayList<SearchPoint>();;
	public Map.Point end;
	public Map.Point start;
	public int methodH;
	public SearchPoint stopPoint=new SearchPoint(end);
	
	// TODO - add any extra member fields that you would like here 
	
	public class SearchPoint implements Comparable<SearchPoint>
	{
		public Map.Point mapPoint;
		// TODO - add any extra member fields or methods that you would like here
		public SearchPoint parent=null;
		public SearchPoint(Map.Point start){
			this.mapPoint=start;
		}
		public SearchPoint(Map.Point current, SearchPoint parent){
			this.mapPoint = current;
			this.parent = parent;
		}
		// TODO - implement this method to return the minimum cost
		// necessary to travel from the start point to here
		public float g() 
		{
			if(!this.equals(start)){
				return (float) Math.sqrt(Math.pow(this.mapPoint.x - this.parent.mapPoint.x, 2) + Math.pow(this.mapPoint.y - this.parent.mapPoint.y, 2)) + this.parent.g();
			}
			return 0;
		}	
		
		// TODO - implement this method to return the heuristic estimate
		// of the remaining cost, based on the H parameter passed from main:
		// 0: always estimate zero, 1: manhattan distance, 2: euclidean l2 distance
		public float h()
		{
			if(methodH==0){
				return 0;
			}
			if(methodH==1){
				return Math.abs((end.x-this.mapPoint.x))+Math.abs((end.y-this.mapPoint.y));
			}
			if(methodH==2){
				return (float) Math.sqrt((end.x-this.mapPoint.x)*(end.x-this.mapPoint.x)+(end.y-this.mapPoint.y)*(end.y-this.mapPoint.y));
			}
			return -1;
		}
		
		// TODO - implement this method to return to final priority for this
		// point, which include the cost spent to reach it and the heuristic 
		// estimate of the remaining cost
		public float f()
		{
			return this.h()+this.g();
		}
		
		// TODO - override this compareTo method to help sort the points in 
		// your frontier from highest priority = lowest f(), and break ties
		// using whichever point has the lowest g()
		@Override
		public int compareTo(SearchPoint other)
		{
			if (this.f()<other.f()){
				return 1;
			}
			if((this.f()==other.f())&&(this.g()<other.g())){
				return 1;
			}
			return -1;
		}
		
		// TODO - override this equals to help you check whether your ArrayLists
		// already contain a SearchPoint referencing a given Map.Point
		@Override
		public boolean equals(Object other)
		{
			if(this.mapPoint.equals(other)){
				return true;
			}
			return false;
		}		
	}
	
	// TODO - implement this constructor to initialize your member variables
	// and search, by adding the start point to your frontier.  The parameter
	// H indicates which heuristic you should use while searching:
	// 0: always estimate zero, 1: manhattan distance, 2: euclidean l2 distance
	public Hao_Zhou_AStar(Map map, int H)
	{
		this.start=map.start;
		this.end=map.end;
		this.methodH=H;
		this.frontier.add(new SearchPoint(map.start));
	}
	
	// TODO - implement this method to explore the single highest priority
	// and lowest f() SearchPoint from your frontier.  This method will be 
	// called multiple times from Main to help you visualize the search.
	// This method should not do anything, if your search is complete.
	public void exploreNextNode() 
	{
		if(!this.isComplete()){
			int chosen=0;
			SearchPoint ChosenPoint;
			ChosenPoint=this.frontier.get(0);
			for (int i = 0; i < this.frontier.size(); i++) {
				SearchPoint part = this.frontier.get(i);
				if(this.frontier.get(chosen).compareTo(part)==-1){
					chosen=i;
				};
			}
			ChosenPoint=this.frontier.get(chosen);
			this.frontier.remove(chosen);
			this.explored.add(ChosenPoint);
			for (int i = 0; i < ChosenPoint.mapPoint.neighbors.size(); i++) {
				  int IsExplored=-1;
				  int IsFrontier=-1;
				  for(int j=0;j<this.explored.size();j++){
					  if(this.explored.get(j).equals(ChosenPoint.mapPoint.neighbors.get(i))){
						  IsExplored=j;
					  }
				  }
				  for(int j=0;j<this.frontier.size();j++){
					  if(this.frontier.get(j).equals(ChosenPoint.mapPoint.neighbors.get(i))){
						  IsFrontier=j;
					  }
				  }				  
				  if((IsExplored==-1) && (IsFrontier==-1)){
					  this.frontier.add(new SearchPoint(ChosenPoint.mapPoint.neighbors.get(i),ChosenPoint));
				  }
				  if(IsFrontier>-1){
					  Map.Point OldPt=this.frontier.get(IsFrontier).mapPoint, NewPt=ChosenPoint.mapPoint;			  
					  if(this.frontier.get(IsFrontier).g()>ChosenPoint.g()+(float) Math.sqrt(Math.pow(OldPt.x-NewPt.x,2)+Math.pow(OldPt.y-NewPt.y,2))){
						  this.frontier.remove(IsFrontier);
						  this.frontier.add(new SearchPoint(ChosenPoint.mapPoint.neighbors.get(i),ChosenPoint));
					  }
				  }
				  if(IsExplored>-1){
					  Map.Point OldPt=this.explored.get(IsExplored).mapPoint, NewPt=ChosenPoint.mapPoint;	
					  if(this.explored.get(IsExplored).g()>ChosenPoint.g()+(float) Math.sqrt(Math.pow(OldPt.x-NewPt.x,2)+Math.pow(OldPt.y-NewPt.y,2))){
						  this.explored.remove(IsExplored);
						  this.frontier.add(new SearchPoint(ChosenPoint.mapPoint.neighbors.get(i),ChosenPoint));
					  }
				  }
			}
		}
	}

	// TODO - implement this method to return an ArrayList of Map.Points
	// that represents the SearchPoints in your frontier.
	public ArrayList<Map.Point> getFrontier()
	{
		ArrayList<Map.Point> mapfrontier=new ArrayList<Map.Point>();
		for (SearchPoint part : this.frontier) {
			  mapfrontier.add(part.mapPoint);
			}
		return mapfrontier;
	}
	
	// TODO - implement this method to return an ArrayList of Map.Points
	// that represents the SearchPoints that you have explored.
	public ArrayList<Map.Point> getExplored()
	{
		ArrayList<Map.Point> mapexplored=new ArrayList<Map.Point>();
		for (SearchPoint part : this.explored) {
			  mapexplored.add(part.mapPoint);
			}
		return mapexplored;
	}

	// TODO - implement this method to return true only after a solution
	// has been found, or you have determined that no solution is possible.
	public boolean isComplete()
	{
		for(int i=0;i<this.frontier.size();i++){
			if(this.frontier.get(i).equals(end)){
				this.stopPoint=this.frontier.get(i);
				float minf=this.frontier.get(0).f();
				for(int j=0;j<this.frontier.size();j++){
					if(minf>this.frontier.get(j).f()){
						minf=this.frontier.get(j).f();
					}
				}
				if(!(stopPoint.f()>minf)){
					return true;
				}
			}
		}
		if (this.frontier.size() == 0){
			return true;
		}
		return false;
	}

	// TODO - implement this method to return an ArrayList of the Map.Points
	// that are along the path that you have found from the start to end.  
	// These points must be in the ArrayList in the order that they are 
	// traversed while moving along the path that you have found.
	public ArrayList<Map.Point> getSolution()
	{
		ArrayList<Map.Point> solution = new ArrayList<Map.Point>();
		solution.add(this.stopPoint.mapPoint);
		SearchPoint trackback = this.stopPoint;

		while(trackback.parent != null){
			solution.add(trackback.parent.mapPoint);
			trackback = trackback.parent;
		}
		
		ArrayList<Map.Point> forward = new ArrayList<Map.Point>();
		for (int i = 0; i < solution.size(); i++){
			forward.add(solution.get(solution.size()-1-i));
		}
		return forward;
	}	
}
