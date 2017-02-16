
import java.io.IOException;
import java.util.Vector;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;



public class Dart 
{
	private Sprite sprite;
	private int speed;
	private int direction;
	private boolean destroyed;
	
	public Dart(int x, int y, int speed, int direction)		//direction=0 left, =1 up, =2 right, =3 down
	{
		if(speed < 0)
			speed = 5;
		
		if(direction < 0 || direction > 3)
			direction = 0;
		
		Image img = null;
		try 
		{
			img = Image.createImage("/sprite/dart.png");
		} 
		catch (IOException e)
		{ e.printStackTrace(); }
		
		this.speed = speed;
		this.direction = direction;
		destroyed = false;
		
		switch(direction)
		{
			case 0:
				img = Image.createImage(img, 0, 0, img.getWidth(), img.getHeight(), Sprite.TRANS_ROT270);
				break;
				
			case 2:
				img = Image.createImage(img, 0, 0, img.getWidth(), img.getHeight(), Sprite.TRANS_ROT90);
				break;
				
			case 3:
				img = Image.createImage(img, 0, 0, img.getWidth(), img.getHeight(), Sprite.TRANS_ROT180);
				break;
		}
		
		sprite = new Sprite(img);
		sprite.setPosition(x, y);
	}
	
	public boolean isDestroyed()
	{ return destroyed; }
	
	public Sprite getSprite()
	{ return sprite; }
	
	public int move(TiledLayer walls, Vector monsters)
	{
		int score = 0;
		
		int dx = 0;
		int dy = 0;
		
		switch(direction)
		{
			case 0:
				dx = -speed;
				break;
				
			case 1:
				dy = -speed;
				break;
				
			case 2:
				dx = speed;
				break;
				
			case 3:
				dy = speed;
				break;
		}
		
		sprite.move(dx, dy);
		
		if(sprite.collidesWith(walls, false) == true)
		{
			destroyed = true;
		}
		
		for(int i=0; i<monsters.size(); i++)
		{
			if(sprite.collidesWith(((Monster) monsters.elementAt(i)).getSprite(), false) == true)
			{
				if(((Monster) monsters.elementAt(i)).getStunnedTimeout() == 0)
				{
					destroyed = true;
					((Monster) monsters.elementAt(i)).setSpeed(1);
					score += 20;
				}
			}
		}
		
		return score;
	}
	
	public void paint(Graphics g)
	{
		sprite.paint(g);
	}
}
