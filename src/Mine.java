
import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Graphics;

public class Mine 
{
	private static final int BLINK_DELAY = 50;
	private Sprite sprite;
	private Image mine;
	private int tick;
	private boolean charged;
	
	public boolean getCharged()
	{ return charged; }
	
	public Sprite getSprite()
	{ return sprite; }

	public Mine(int x, int y)
	{
		charged = false;
		tick = 0;
		
		try
		{
			mine = Image.createImage("/sprite/mine.png");
		}
		catch(IOException e)
		{
			System.out.println("NO MINE SPRITE ALARM!");
		}
		
		sprite = new Sprite(mine, 16, 16);
		sprite.setPosition(x, y);
		sprite.defineCollisionRectangle(3, 3, 10, 10);
	}
	
	public void checkCharged(Player p, boolean isGameover)
	{
		if(sprite.collidesWith(p.getSprite(), false) == false || isGameover == true)
			charged = true;
	}
	
	public void tick()
	{
		tick++;
		if(tick > BLINK_DELAY)
		{
			sprite.nextFrame();
			if(sprite.getFrame() == 0)
				tick = 0;
		}
	}
	
	public void paint(Graphics g)
	{
		sprite.paint(g);
	}
	
	
}
