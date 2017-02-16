import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;


public class ShieldPickup extends Pickup
{
	public ShieldPickup(int x, int y)
	{
		super();
		try
		{
			Image img = Image.createImage("/sprite/shield.png");
			sprite = new Sprite(img, 16, 16);
			super.placePickup(x, y);
		} 
		catch (IOException e)
		{
			System.out.println("NO SHIELD SPRITE ALARM!!!");
		}
	}
}
