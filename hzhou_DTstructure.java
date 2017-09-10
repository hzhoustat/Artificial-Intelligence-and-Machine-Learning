import processing.core.*;
import processing.data.*;

public class hzhou_Assmt01 extends PApplet
{		
	private XML data;
	private boolean keyAlreadyPressed;
	
	public void removewhitespace(XML xml){
		String b = "#text", d = "box", e = "move";
		XML[] children = xml.getChildren();
		for (int i = 0; i < children.length; i++) {
			String name = children[i].getName();
			if ( name.equals(b) | name.contentEquals(""))
			{
				xml.removeChild(children[i]);
			}
			if(name.equals(d)){
				removewhitespace(children[i]);
			}
			if(name.equals(e)){
				removewhitespace(children[i]);
			}
		}
	}
	public void loadBoxes(String filename)
	{
		// TODO: implement this method in step 1
		try{
		data = loadXML(filename);
		removewhitespace(data);}
		catch(Exception e){
			System.err.println("Wrong file address");
		}
	}
	public static boolean useLoop(String[] arr, String targetValue) {
	    for(String s: arr){
	        if(s.equals(targetValue))
	            return true;
	    }
	    return false;
	}

	public void drawBoxes(XML xml, int x, int y)
	{
		// TODO: implement this method in step 2
		try{
		String b = "box", d = "move";
		int x1 = x, y1 = y;
		XML[] children = xml.getChildren();
		for (int i = 0; i < children.length; ++i) {
			String name = children[i].getName();
			System.out.println(name);
			if ( name.equals(b))
			{
				rect(x,y,20,20);
			}
			if(name.equals(d)){
				String[] h;
				h=children[i].listAttributes();
				if(useLoop(h,"x")){
					x1=x+children[i].getInt("x");
				}
				if(useLoop(h,"y")){
					y1=y+children[i].getInt("y");
				}
				drawBoxes(children[i],x1,y1);
			}
		}
		}catch(Exception e){
			System.err.println("NO input");
		}
	}
	
	public void doubleMoves(XML xml) 
	{
		// TODO: implement this method in step 3
		try{
		String d="move";
		XML[] children = xml.getChildren();
		for (int i = 0; i < children.length; i++) {
			String name = children[i].getName();
			if(name.equals(d)){
				String[] h;
				h=children[i].listAttributes();
				if(useLoop(h,"x")){
					children[i].setInt("x",2*children[i].getInt("x"));
				}
				if(useLoop(h,"y")){
					children[i].setInt("y",2*children[i].getInt("y"));
				}
				doubleMoves(children[i]);
			}
		}
		}catch(Exception e){
			System.err.println("NO input");
		}
	}
	
	private void addNewChild(XML xml, int xx, int yy){
		XML newChild = new XML("move");
		newChild.setInt("x", xx);
		newChild.setInt("y", yy);
		newChild.addChild("box");
		xml.addChild(newChild);
	}
	
	public void doubleBoxes(XML xml)
	{
		// TODO: implement this method in step 4	
		try{
		String b="box", d="move";
		XML[] children = xml.getChildren();
		for (int i = 0; i < children.length; i++) {
			String name = children[i].getName();
			if(name.equals(d)){
				doubleBoxes(children[i]);
			}
		}
		for (int i = 0; i < children.length; i++) {
			String name = children[i].getName();
			if ( name.equals(b)){
				xml.removeChild(children[i]);
				addNewChild(xml, 10,-10);
				addNewChild(xml, -10,-10);
				addNewChild(xml, -10,10);
				addNewChild(xml, 10,10);
		    }
		}
		}catch(Exception e){
			System.err.println("NO input");
		}
	}
	
	// tie key press events to calling the functions above:
	// 1 - loadBoxes
	// 2 - drawBoxes
	// 3 - doubleMoves
	// 4 - doubleBoxes
	public void draw()
	{
		if(keyPressed)
		{
			if(keyAlreadyPressed == false)
			{
				switch(key)
				{
				case '1':
					loadBoxes("boxData.xml");
					break;
				case '2':
					background( 255 );
					drawBoxes(data, width/2, height/2);
					save("output.png");
					break;
				case '3':
					doubleMoves(data);
					break;
				case '4':
					doubleBoxes(data);
					break;
				}
			}
			keyAlreadyPressed = true;
		}
		else
			keyAlreadyPressed = false;
	}

	// basic processing setup: window size and background color
	public void setup()
	{
		size(800, 600);
		background( 255 );
		data = null;
		keyAlreadyPressed = true;
	}
		
	// run as an Application instead of as an Applet
	public static void main(String[] args) 
	{
		String thisClassName = new Object(){}.getClass().getEnclosingClass().getName();
		PApplet.main( new String[] { thisClassName } );
	}
}


