
public class Coordinates implements Coordinatable
{
	private int x;
	private int y;
	
	public Coordinates(int x, int y)
	{
		if(x < 8)
			x = 8;
		if(x > 224)
			x = 224;
		if(y < 8)
			y = 8;
		if(y > 304)
			y = 304;
		
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{ return x; }
	
	public int getY()
	{ return y; }
	
	public void setX(int x)
	{ this.x = x; }
	
	public void setY(int y)
	{ this.y = y; }
	
	public void print()
	{
		System.out.println("("+x+";"+y+")");
	}
}
