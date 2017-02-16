
import javax.microedition.lcdui.Graphics;



public class ScoreUp
{
	private int score;
	private int time;
	private static final int LIFETIME = 20;
	private int x;
	private int y;
	private boolean killed;
	
	public ScoreUp(int score, int x, int y)
	{
		this.x = x;
		this.y = y;
		this.score = score;
		time = LIFETIME;
		killed = false;
	}
	
	public boolean isKilled()
	{ return killed; }
	
	public void paint(Graphics g, mFont font)
	{
		font.print(Integer.toString(score), g, x, y-(LIFETIME-time), 0);
		time--;
		if(time == 0)
			killed = true;
	}
}
