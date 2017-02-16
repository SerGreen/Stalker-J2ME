
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Plate 
{
	private Sprite sprite;
	private boolean state;
	private Vector childs;
	private int popUpTimeout;
	
	public Plate(int x, int y, Vector childs)
	{
		Image img = null;
		try 
		{
			img = Image.createImage("/sprite/plate.png");
		}
		catch (IOException e) 
		{ e.printStackTrace(); }
		
		if(x < 8)
			x = 8;
		if(x > 216)
			x = 216;
		if(y < 8)
			y = 8;
		if(y > 296)
			y = 296;
		
		sprite = new Sprite(img, 8, 8);
		sprite.setPosition(x+4, y+4);
		
		popUpTimeout = 0;
		
		state = false;
		
		this.childs = childs;
	}
	
	public void activate(TiledLayer walls, Vector spikes)
	{
		popUpTimeout = 5;
		
		if(state == false)
		{
			state = true;
			sprite.setFrame(sprite.getFrameSequenceLength()-1);			
			
			for(int i=0; i<childs.size(); i++)
			{
				int x = ((Coordinates) childs.elementAt(i)).getX();
				int y = ((Coordinates) childs.elementAt(i)).getY();
				
				boolean done = false;
				
				for(int j=0; j<spikes.size(); j++)
				{
					if(((Spikes) spikes.elementAt(j)).getSprite().getX() == x && ((Spikes) spikes.elementAt(j)).getSprite().getY() == y)
					{
						((Spikes) spikes.elementAt(j)).changeState();
						done = true;
						break;
					}
				}
				
				if(done == false)
				{
					x /= 8;
					y /= 8;
					
					if(walls.getCell(x, y) == 0)
					{
						walls.setCell(x, y, 1);
					}
					else
					{
						walls.setCell(x, y, 0);
					}
				}
			}
		}
	}
	
	public void tick(TiledLayer walls, Vector spikes)
	{
		if(popUpTimeout > 0)
		{
			popUpTimeout--;
			
			if(popUpTimeout == 0)
			{
				state = false;
				sprite.setFrame(0);
				
				for(int i=0; i<childs.size(); i++)
				{
					int x = ((Coordinates) childs.elementAt(i)).getX();
					int y = ((Coordinates) childs.elementAt(i)).getY();
					
					boolean done = false;
					
					for(int j=0; j<spikes.size(); j++)
					{
						if(((Spikes) spikes.elementAt(j)).getSprite().getX() == x && ((Spikes) spikes.elementAt(j)).getSprite().getY() == y)
						{
							((Spikes) spikes.elementAt(j)).changeState();
							done = true;
							break;
						}
					}
					
					if(done == false)
					{
						x /= 8;
						y /= 8;
						
						if(walls.getCell(x, y) == 0)
						{
							walls.setCell(x, y, 1);
						}
						else
						{
							walls.setCell(x, y, 0);
						}
					}
				}
			}
		}
	}
	
	public Sprite getSprite()
	{ return sprite; }
	
	public void paint(Graphics g)
	{
		sprite.paint(g);
	}
}
