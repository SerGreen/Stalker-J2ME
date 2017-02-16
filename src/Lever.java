
import java.io.IOException;
import java.util.Vector;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Lever 
{
	private Sprite sprite;
	private boolean state;
	private Vector childs;
	
	public Lever(int x, int y, Vector childs, boolean state)
	{
		Image img = null;
		try 
		{
			img = Image.createImage("/sprite/lever.png");
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
		
		sprite = new Sprite(img, 16, 16);
		sprite.setPosition(x, y);
		
		this.state = state;
		
		if(state == false)
			sprite.setFrame(sprite.getFrameSequenceLength()-1);
		else
			sprite.setFrame(0);
		
		this.childs = childs;
	}
	
	public void changeState(TiledLayer walls, Vector spikes)
	{
		if(state == true)
		{
			state = false;
			sprite.setFrame(sprite.getFrameSequenceLength()-1);
		}
		else
		{
			state = true;
			sprite.setFrame(0);
		}
		
		
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
	
	public Sprite getSprite()
	{ return sprite; }
	
	public void paint(Graphics g)
	{
		sprite.paint(g);
	}
}
