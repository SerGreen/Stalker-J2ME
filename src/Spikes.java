
import java.io.IOException;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public class Spikes 
{
	private Sprite sprite;
	private boolean state;
	private int timeOpen;
	private int timeClosed;
	private int currentTime;
	private boolean autoMode;
	
	public Spikes(int x, int y, boolean autoMode, int openTime, int closeTime, boolean state, int startTime)
	{
		Image img = null;
		try 
		{
			img = Image.createImage("/sprite/spikes.png");
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
		this.autoMode = autoMode;
		this.state = state;
		
		if(state = true)
		{
			sprite.setFrame(sprite.getFrameSequenceLength()-1);
		}
		
		if(autoMode == false)
		{
			timeOpen = -1;
			timeClosed = -1;
			currentTime = 0;
		}
		else
		{
			if(openTime < 5)
				openTime = 5;
			if(closeTime < 5)
				closeTime = 5;
			
			timeOpen = openTime;
			timeClosed = closeTime;
			
			if(state == true && startTime > timeOpen)
				currentTime = 0;
			else
				currentTime = startTime;
			
			if(state == false && startTime > timeClosed)
				currentTime = 0;
			else
				currentTime = startTime;
		}
	}
	
	public Sprite getSprite()
	{ return sprite; }
	
	public boolean isOpened()
	{ return state; }
	
	public boolean isAuto()
	{ return autoMode; }
	
	public void changeState()
	{
		if(state == true)
			state = false;
		else
			state = true;
	}
	
	public void tick()
	{
		if(state == false)
		{
			if(sprite.getFrame() != 0)
				sprite.setFrame(sprite.getFrame()-1);
		}
		else
		{
			if(sprite.getFrame() != sprite.getFrameSequenceLength()-1)
				sprite.nextFrame();
		}
		
		if(autoMode == true)
		{
			if(state == false)
			{
				if(currentTime == timeClosed)
				{
					currentTime = 0;
					state = true;
				}
			}
			else
			{
				if(currentTime == timeOpen)
				{
					currentTime = 0;
					state = false;
				}
			}
			
			currentTime++;
		}
	}
	
	public void paint(Graphics g)
	{
		sprite.paint(g);
	}
}
