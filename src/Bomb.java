import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;


public class Bomb
{
	private Sprite sprite;
	private int time;
	private int maxTime;
	private int vX;
	private int vY;
	
	private boolean BOOM;
	
	public Bomb(int x, int y, int vx, int vy, int time)
	{
		Image img = null;
		try
		{
			img = Image.createImage("/boss/bomb.png");
		}
		catch (IOException e)
		{ System.out.println("NO BOMB SPRITE!"); }
		
		sprite = new Sprite(img, 8, 8);
		sprite.setPosition(x, y);
		
		vX = vx;
		vY = vy;
		
		this.time = time;
		maxTime = this.time;
		
		BOOM = false;
	}
	
	public void tick(TiledLayer walls)
	{
		if(time > 0)
		{
			time--;
			int index = 7-time/(maxTime/8);
			if(index < 0)
				index = 0;
			sprite.setFrame(index);
			
			move(walls);
		}
		else
			BOOM = true;
	}
	
	private void move(TiledLayer walls)
	{
		sprite.move(vX, 0);
		if(sprite.collidesWith(walls, false) == true)
		{
			vX = -vX;
			sprite.move(vX, 0);
		}
		
		sprite.move(0, vY);
		if(sprite.collidesWith(walls, false) == true)
		{
			vY = -vY;
			sprite.move(0, vY);
		}
	}
	
	public boolean isBoom()
	{ return BOOM; }
	
	public Sprite getSprite()
	{ return sprite; }
	
	public void render(Graphics g)
	{
		sprite.paint(g);
	}
}
