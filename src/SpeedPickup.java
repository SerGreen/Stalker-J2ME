import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class SpeedPickup extends Pickup
{
	public SpeedPickup(int x, int y)
	{
		super();
		try
		{
			Image img = Image.createImage("/sprite/speed.png");
			sprite = new Sprite(img, 16, 16);
			super.placePickup(x, y);
		} 
		catch (IOException e)
		{
			System.out.println("NO SPEED SPRITE ALARM!!!");
		}
	}
}
