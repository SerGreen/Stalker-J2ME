
import java.io.IOException;

import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Image;

public class ShellPickup extends Pickup
{
	public ShellPickup(int x, int y)
	{
		super();
		try
		{
			Image img = Image.createImage("/sprite/shells.png");
			sprite = new Sprite(img, 16, 16);
			super.placePickup(x, y);
		} 
		catch (IOException e)
		{
			System.out.println("NO SHELLS SPRITE ALARM!!!");
		}
	}
}