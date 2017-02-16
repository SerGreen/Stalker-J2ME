
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Graphics;


public class Pickup
{
	protected Sprite sprite;
	private final static int SLOWDOWN = 7;
	private int slowdown;
	
	public Pickup()
	{
		slowdown = 0;
	}
	
	protected void placePickup(int x, int y)
	{
		if(x < 8)
			x = 8;
		if(x > 216)
			x = 216;
		if(y < 8)
			y = 8;
		if(y > 296)
			y = 296;
		
		sprite.setPosition(x, y);
	}
	
	public Sprite getSprite()
	{ return sprite; }
	
	public void tick()
	{
		
		if(slowdown > 0)
			slowdown--;
		
		if(slowdown == 0)
		{
			sprite.nextFrame();	
			slowdown = SLOWDOWN;
		}
	}
	
	public void paint(Graphics g)
	{
		sprite.paint(g);
	}
}
