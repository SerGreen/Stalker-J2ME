import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;


public class SpikesEditor implements Coordinatable
{
	private Coordinates coordinate;
	private boolean autoMode;
	private boolean state;
	private int timeOpen;
	private int timeClosed;
	private int currentTime;
	

	private Image s_spike;
	private Image s_autoSpike;
	
	public SpikesEditor(int x, int y, boolean state, boolean autoMode, int timeOpen, int timeClosed, int currentTime)
	{
		try 
		{
			s_spike = Image.createImage("/editor/spikes.png");
			s_autoSpike = Image.createImage("/editor/auto_spikes.png");
		}
		catch (IOException e) 
		{ e.printStackTrace(); }
		
		coordinate = new Coordinates(x, y);
		this.state = state;
		this.autoMode = autoMode;
		
		if(timeOpen < 20)
			timeOpen = 20;
		if(timeClosed < 20)
			timeClosed = 20;
		
		if(state == true && this.currentTime > timeOpen)
			this.currentTime = timeOpen;
		if(state == false && this.currentTime > timeClosed)
			this.currentTime = timeClosed;
		
		this.timeOpen = timeOpen;
		this.timeClosed = timeClosed;
		this.currentTime = currentTime;
	}
	
	public int getX()
	{ return coordinate.getX(); }
	
	public int getY()
	{ return coordinate.getY(); }
	
	public int getOpen()
	{ return timeOpen; }
	
	public int getClose()
	{ return timeClosed; }
	
	public int getCurrent()
	{ return currentTime; }
	
	public boolean isAuto()
	{ return autoMode; }
	
	public boolean isOpenedState()
	{ return state; }
	
	public void setAutoMode(boolean autoMode)
	{
		this.autoMode = autoMode;
	}

	public void setState(boolean state)
	{
		this.state = state;
	}

	public void setTimeOpen(int timeOpen)
	{
		this.timeOpen += timeOpen;
		if(this.timeOpen < 20)
			this.timeOpen = 20;
	}

	public void setTimeClosed(int timeClosed)
	{
		this.timeClosed += timeClosed;
		if(this.timeClosed < 20)
			this.timeClosed = 20;
	}

	public void setCurrentTime(int currentTime)
	{
		this.currentTime += currentTime;
		if(this.currentTime < 0)
			this.currentTime = 0;
		
		if(state == true && this.currentTime > timeOpen)
			this.currentTime = timeOpen;
		if(state == false && this.currentTime > timeClosed)
			this.currentTime = timeClosed;
	}

	public void paint(Graphics g)
	{
		if(autoMode == true)
			g.drawImage(s_autoSpike, coordinate.getX(), coordinate.getY(), 0);
		else
			g.drawImage(s_spike, coordinate.getX(), coordinate.getY(), 0);
	}
}
