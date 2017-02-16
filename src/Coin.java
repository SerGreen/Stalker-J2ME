
import java.io.IOException;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Image;

public class Coin extends Pickup
{
	public Coin(int x, int y)
	{
		super();
		try
		{
			Image img = Image.createImage("/sprite/coins.png");
			sprite = new Sprite(img, 16, 16);
			super.placePickup(x, y);
		} 
		catch (IOException e)
		{
			System.out.println("NO COIN SPRITE ALARM!!!");
		}
	}
}
