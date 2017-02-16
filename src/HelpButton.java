import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class HelpButton
{
	private Sprite button;
	private static final int HELP_TIME = 75;
	private static final int STAR_TIME = 25;
	private int time;
	private boolean isHelp;
	
	public HelpButton(int x, int y, boolean isHelp)
	{
		Image img = null;
		
		try
		{
			img = Image.createImage("/menu/help.png");
		}
		catch (IOException e)
		{ System.out.println("NO HELP SPRITE! GO AWAY!!!"); }
		
		button = new Sprite(img, 21, 21);
		button.setPosition(x, y);
		
		this.isHelp = isHelp;
		if(isHelp == true)
		{
			button.setFrame(9);
		}
		else
		{
			button.setFrame(10);
		}
		
		time = 0;
	}
	
	public void tick()
	{
		if(isHelp == true)
		{
			if(button.getFrame() > 0)
			{
				button.prevFrame();
			}
			else
			{
				time++;

				if(time == HELP_TIME)
				{
					time = 0;
					isHelp = false;
				}
			}
		}
		else
		{
			if(button.getFrame() < button.getFrameSequenceLength()-1)
			{
				button.nextFrame();
			}
			else
			{
				time++;

				if(time == STAR_TIME)
				{
					time = 0;
					isHelp = true;
				}
			}
		}
	}
	
	public void paint(Graphics g)
	{
		button.paint(g);
	}
}
