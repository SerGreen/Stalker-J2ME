
import java.io.IOException;
import java.util.Random;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.lcdui.Graphics;

public class Flesh 
{
	private static final double FRICTION = 0.1;
	private Sprite sprite;
	private double xSpeed;
	private double ySpeed;
	
	public Flesh(int x, int y)
	{
		Random rnd = new Random();
		xSpeed = rnd.nextInt(3)+1;
		ySpeed = rnd.nextInt(3)+1;
		
		if(rnd.nextInt(2) == 0)
			xSpeed *= -1;
		
		if(rnd.nextInt(2) == 0)
			ySpeed *= -1;
		
		try
		{
			Image flesh = Image.createImage("/sprite/flesh.png");
			
			sprite = new Sprite(flesh, 5, 5);
			sprite.setFrame(rnd.nextInt(3));
			
			sprite.setPosition(x+8, y+8);
		}
		catch(IOException e)
		{
			System.out.println("NO FLESH SPRITE ALARM!!!");
		}
	}
	
	public void move(TiledLayer walls)
	{
		if(xSpeed != 0 || ySpeed != 0)
		{
			int dx = (int)(xSpeed);
			int dy = (int)(ySpeed);
			
			sprite.move(dx, 0);
			if(sprite.collidesWith(walls, false) == true)
			{
				dx *= -1;
				sprite.move(dx, 0);
			}
			
			sprite.move(0, dy);
			if(sprite.collidesWith(walls, false) == true)
			{
				dy *= -1;
				sprite.move(0, dy);
			}
			
			if(xSpeed > 0)
			{
				xSpeed -= FRICTION;
				if(xSpeed <= 0.01)
					xSpeed = 0;
			}
			else
			{
				xSpeed += FRICTION;
				if(xSpeed >= -0.01)
					xSpeed = 0;
			}
			
			if(ySpeed > 0)
			{
				ySpeed -= FRICTION;
				if(ySpeed <= 0.01)
					ySpeed = 0;
			}
			else
			{
				ySpeed += FRICTION;
				if(ySpeed >= -0.01)
					ySpeed = 0;
			}
		}
	}
	
	public void paint(Graphics g)
	{
		sprite.paint(g);
	}
}
