
import java.util.Random;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Graphics;

public class Blood 
{
	private Sprite sprite;
	
	public Blood(int x, int y)
	{
		try
		{
			Image img = Image.createImage("/sprite/blood.png");
			sprite = new Sprite(img, 16, 16);
			sprite.setPosition(x, y);
			Random rnd = new Random();
			sprite.setFrame(rnd.nextInt(3));
		}
		catch (Exception e)
		{
			System.out.println("NO BLOOD SPRITE ALARM!!!");
			e.printStackTrace();
		}
	}
	
	public void paint(Graphics g)
	{
		sprite.paint(g);
	}
}
