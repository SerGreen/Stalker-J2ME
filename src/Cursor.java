import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class Cursor
{
	private Sprite ordinary;
	private Sprite wall;
	private boolean legal;
	private boolean wallMode;
	private int x;
	private int y;
	
	public boolean isWallMode()
	{ return wallMode; }
	
	public boolean isLegal()
	{ return legal; }
	
	public int getX()
	{ return x; }
	
	public int getY()
	{ return y; }
	
	private Image cursor = null;
	private Image cursorWall = null;
	public Cursor(int x, int y)
	{
		try 
		{
			cursor = Image.createImage("/editor/cursor.png");
			cursorWall = Image.createImage("/editor/cursor_wall.png");
		}
		catch (IOException e) 
		{ e.printStackTrace(); }
		
		ordinary = new Sprite(cursor, 16, 16);
		wall = new Sprite(cursorWall, 8, 8);
		
		this.x = x;
		this.y = y;
		
		legal = true;
		wallMode = true;
		
		ordinary.setPosition(x, y);
		wall.setPosition(x, y);
	}
	
	public void changeMode()
	{
		if(wallMode == true)
		{
			wallMode = false;
			move(0, 0);
		}
		else
		{
			wallMode = true;
		}
	}
	
	public void changeLegal()
	{
		if(legal == true)
		{
			legal = false;
			ordinary.setFrame(1);
			wall.setFrame(1);
		}
		else
		{
			legal = true;
			ordinary.setFrame(0);
			wall.setFrame(0);
		}
	}
	
	public void move(int dx, int dy)
	{
		x += dx;
		y += dy;
		
		if(wallMode == true)
		{
			if(x < 8)
				x=8;
			if(x > 224)
				x=224;
			if(y < 8)
				y=8;
			if(y > 304)
				y=304;
		}
		else
		{
			if(x < 8)
				x=8;
			if(x > 216)
				x=216;
			if(y < 8)
				y=8;
			if(y > 296)
				y=296;
		}
		
		ordinary.setPosition(x, y);
		wall.setPosition(x, y);
	}
	
	public void setPlace(int x, int y)
	{
		this.x = x;
		this.y = y;
		move(0, 0);
	}
	
	public void paint(Graphics g)
	{
		if(wallMode == true)
			wall.paint(g);
		else
			ordinary.paint(g);
	}
}
