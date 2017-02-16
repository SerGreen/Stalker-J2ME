
import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Graphics;

public class Flame 
{
	private Image flame_up;
	private Image flame_down;
	private Image flame_left;
	private Image flame_right;
	private Sprite flame;
	
	private boolean shoot;

	public boolean getShoot()
	{ return this.shoot; }
	
	public Flame(int x, int y)
	{
		try
		{	
			flame_up = Image.createImage("/sprite/flame_up.png");
			flame_down = Image.createImage("/sprite/flame_down.png");
			flame_left = Image.createImage("/sprite/flame_left.png");
			flame_right = Image.createImage("/sprite/flame_right.png");
		}
		catch (IOException e)
		{
			System.out.println("NO SPRITE ALARM!!!!");
			System.out.println(e.toString());
			//ALARM
		}

		flame = new Sprite(flame_down, 16, 32);
		flame.setPosition(x, y+16);
		shoot = false;
	}
	
	public void shoot(int x, int y, String direction)
	{
		shoot = true;
		
		if (direction == "left") 
		{
			flame.setImage(flame_left, 32, 16);
			flame.setPosition(x-32, y);
		} 
		else if (direction == "right") 
		{
			flame.setImage(flame_right, 32, 16);
			flame.setPosition(x+16, y);
		} 
		else if (direction == "up") 
		{
			flame.setImage(flame_up, 16, 32);
			flame.setPosition(x, y-32);
		} 
		else if (direction == "down")
		{
			flame.setImage(flame_down, 16, 32);
			flame.setPosition(x, y+16);
		}
	}
	
	public void paint(Graphics g)
	{			
		flame.paint(g);
		flame.nextFrame();
		if(flame.getFrame() == 0)
		{
			shoot = false;
		}
	}
	
}
