
import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class mFontHugeNumbers
{
	private Sprite font;
	
	public mFontHugeNumbers()
	{
		try 
		{
			Image f = Image.createImage("/font/white_huge.png");
			
			font = new Sprite(f, 20, 30);
		} 
		catch (IOException e)
		{
			System.out.println("ERROR WHILE LOADING HUUUGE FONT!!!");
		}
	}
	
	public void print(int s, Graphics g, int x, int y, int padding)
	{
		if(padding == 0)
			padding = 20;
		
		font.setPosition(x, y);
		
		for(int i=0; i<Integer.toString(s).length(); i++)
		{
			int index = Character.digit(Integer.toString(s).charAt(i), 10);
			font.setFrame(index);
			font.paint(g);
			font.move(padding, 0);
		}
	}
}
