
import java.io.IOException;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Image;

public class MinePickup extends Pickup
{
	public MinePickup(int x, int y)
	{
		super();
		try
		{
			Image img = Image.createImage("/sprite/mines.png");
			sprite = new Sprite(img, 16, 16);
			super.placePickup(x, y);
		} 
		catch (IOException e)
		{
			System.out.println("NO MINES SPRITE ALARM!!!");
		}
	}
}
