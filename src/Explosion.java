
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.Graphics;

public class Explosion 
{
	private Sprite sprite;
	private boolean active;
	
	public Explosion(int x, int y)
	{
		try
		{
			Image img = Image.createImage("/sprite/explosion.png");
			
			sprite = new Sprite(img, 16, 16);
			sprite.setPosition(x, y);
			
			active = true;
		}
		catch (Exception e)
		{
			System.out.println("NO EXPLOSION SPRITE ALARM!!!");
		}
	}
	
	public Sprite getSprite()
	{ return sprite; }
	
	public boolean getActive()
	{ return active; }
	
	public void paint(Graphics g)
	{
		sprite.paint(g);
		sprite.nextFrame();
		if(sprite.getFrame() == sprite.getFrameSequenceLength()-1)
			active = false;
	}
}
