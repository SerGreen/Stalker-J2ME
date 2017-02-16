import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class LeverEditor implements Coordinatable
{
	private Coordinates coordinate;
	private boolean state;
	private Vector childsSpikes;
	private Vector childsWalls;
	
	private Image s_lever_up;
	private Image s_lever_down;
	
	public LeverEditor(int x, int y, boolean state)
	{
		try 
		{
			s_lever_up = Image.createImage(Image.createImage("/sprite/lever.png"), 0, 0, 16, 16, 0);
			s_lever_down = Image.createImage(Image.createImage("/sprite/lever.png"), 16, 0, 16, 16, 0);
		}
		catch (IOException e) 
		{ e.printStackTrace(); }
		
		coordinate = new Coordinates(x, y);
		this.state = state;
		
		childsSpikes = new Vector();
		childsWalls = new Vector();
	}
	
	public int getX()
	{ return coordinate.getX(); }
	
	public int getY()
	{ return coordinate.getY(); }
	
	public boolean getState()
	{ return state; }
	
	public Vector getChildsSpikes()
	{ return childsSpikes; }

	public Vector getChildsWalls()
	{ return childsWalls; }
	
	public void setState(boolean state)
	{
		this.state = state;
	}
	
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
		if(state == true)
		{
			g.drawImage(s_lever_up, coordinate.getX(), coordinate.getY(), 0);
		}
		else
		{
			g.drawImage(s_lever_down, coordinate.getX(), coordinate.getY(), 0);
		}
		
	}
}
