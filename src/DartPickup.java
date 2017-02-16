
import java.io.IOException;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Image;

public class DartPickup extends Pickup
{
	public DartPickup(int x, int y)
	{
		super();
		try
		{
			Image img = Image.createImage("/sprite/darts.png");
			sprite = new Sprite(img, 16, 16);
			super.placePickup(x, y);
		} 
		catch (IOException e)
		{
			System.out.println("NO DARTS SPRITE ALARM!!!");
		}
	}
}