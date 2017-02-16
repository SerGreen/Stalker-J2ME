
import java.io.IOException;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

public class mFont
{
	private Sprite font;
	private boolean isLarge;
	
	public mFont(String color, boolean isLarge)
	{
		try 
		{
			Image f;
			String s = "/font/";
			int size;
			
			this.isLarge = isLarge;
			
			s = s + color;
			
			if(isLarge == true)
			{
				s = s + "_large";
				size = 10;
			}
			else
			{
				size = 5;
			}
			
			s = s + ".png";
			
			
			f = Image.createImage(s);
			
			font = new Sprite(f, size, size);
		} 
		catch (IOException e)
		{
			System.out.println("ERROR WHILE LOADING FONT!!!");
			System.out.println("color="+color);
			System.out.println("isLarge="+isLarge);
		}
	}
	
	public void print(String s, Graphics g, int x, int y, int padding)
	{
		if(padding == 0)
		{
			if(isLarge == true)
				padding = 10;
			else
				padding = 5;
		}
		
		font.setPosition(x, y);
		
		for(int i=0; i<s.length(); i++)
		{
			int index = getIndex(s.charAt(i));
			font.setFrame(index);
			font.paint(g);
			font.move(padding, 0);
		}
	}
	
	private int getIndex(char c)
	{
		int i = (int) c;
		
		if(i >= 65 && i <= 90)
			return i-65;
		
		if(i >= 97 && i <= 122)
			return i-97;
		
		if(i >= 48 && i <= 57)
			return i-22;
		
		if(i == 33)
			return 36;
		
		if(i == 63)
			return 37;
		
		if(i == 32)
			return 38;
		
		if(i == 46)
			return 39;
		
		if(i == 44)
			return 40;
		
		if(i == 58)
			return 41;
		
		if(i == 45)
			return 42;
		
		if(i == 43)
			return 43;
		
		if(i == 61)
			return 44;
		
		if(i == 35)
			return 45;
		
		if(i == 42)
			return 46;
		
		return 26;
	}
}
