import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class PlateEditor implements Coordinatable
{
	private Coordinates coordinate;
	private Vector childsSpikes;
	private Vector childsWalls;
	
	private Image s_plate;
	
	public PlateEditor(int x, int y)
	{
		try 
		{
			s_plate = Image.createImage(Image.createImage("/sprite/plate.png"), 0, 0, 8, 8, 0);
		}
		catch (IOException e) 
		{ e.printStackTrace(); }
		
		coordinate = new Coordinates(x, y);
		
		childsSpikes = new Vector();
		childsWalls = new Vector();
	}
	
	public int getX()
	{ return coordinate.getX(); }
	
	public int getY()
	{ return coordinate.getY(); }
	
	public Vector getChildsSpikes()
	{ return childsSpikes; }

	public Vector getChildsWalls()
	{ return childsWalls; }
	
	public void setChildsSpikes(Vector childs)
	{
		childsSpikes = childs;
	}
	
	public void setChildsWalls(Vector childs)
	{
		childsWalls = childs;
	}
	
	public void checkChildSpikes(Vector spikes)
	{
		for(int i=childsSpikes.size()-1; i>=0; i--)
		{
			boolean del = true;
			for(int j=0; j<spikes.size(); j++)
			{
				if(((SpikesEditor) spikes.elementAt(j)).getX() == ((Coordinates) childsSpikes.elementAt(i)).getX() && ((SpikesEditor) spikes.elementAt(j)).getY() == ((Coordinates) childsSpikes.elementAt(i)).getY())
				{
					del = false;
					break;
				}
			}
			
			if(del == true)
			{
				childsSpikes.removeElementAt(i);
			}
		}
	}
	
	public void checkChildWalls(Coordinatable c)
	{
		for(int i=childsWalls.size()-1; i>=0; i--)
		{
			boolean del = false;
			
			if(((Coordinates) childsWalls.elementAt(i)).getX() == c.getX() && ((Coordinates) childsWalls.elementAt(i)).getY() == c.getY() ||
					((Coordinates) childsWalls.elementAt(i)).getX() == c.getX()+8 && ((Coordinates) childsWalls.elementAt(i)).getY() == c.getY() ||
					((Coordinates) childsWalls.elementAt(i)).getX() == c.getX() && ((Coordinates) childsWalls.elementAt(i)).getY() == c.getY()+8 ||
					((Coordinates) childsWalls.elementAt(i)).getX() == c.getX()+8 && ((Coordinates) childsWalls.elementAt(i)).getY() == c.getY()+8)
			{
				del = true;
			}
			
			if(del == true)
			{
				childsWalls.removeElementAt(i);
			}
		}
	}

	public void paint(Graphics g)
	{
		g.drawImage(s_plate, coordinate.getX()+4, coordinate.getY()+4, 0);
	}
}
