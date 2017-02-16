
import java.io.IOException;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.lcdui.Graphics;

public class Player
{
	private static final int MOVE_SPEED = 2;
	private static final int SENSITIVITY = 3;
	private Sprite sprite;
	private int lives;
	private boolean isRunning;
	private String direction;
	private int ouchTimeout;
	private boolean shoot;
	private int ammo;
	private int mines;
	private int darts;
	private int currentWeapon;		//0=shotgun, 1=mines, 2=darts, 3=rocketLauncher
	private boolean dead;
	private int speedUpTimeout;
	private boolean isShielded;
	private boolean isSpeeded;
	private int charged;
	private int chargeTimeout;
	
	private int prev_x;
	private int prev_y;
	
	private Image up;
	private Image down;
	private Image left;
	private Image run_up;
	private Image run_down;
	private Image run_left;
	private Image right;
	private Image run_right;
	
	private Image shoot_up;
	private Image shoot_down;
	private Image shoot_left;
	private Image shoot_right;
	
	public void nextWeapon()
	{
		currentWeapon++;
		if(currentWeapon > 2)		//TODO Ололо! 2, пока у меня всего 3 оружия
			currentWeapon = 0;
	}
	
	public void prevWeapon()
	{
		currentWeapon--;
		if(currentWeapon < 0)		
			currentWeapon = 2;		//TODO Ололо! 2, пока у меня всего 3 оружия
	}
	
	public boolean isAlive()
	{ 
		if(dead == false)
			return true;
		else
			return false;
	}
	
	public int getCurrentWeapon()
	{ return currentWeapon; }
	
	public int getAmmo()
	{ return ammo; }
	
	public int getMines()
	{ return mines; }
	
	public int getDarts()
	{ return darts; }
	
	public void setAmmo(int dx)
	{ ammo += dx; }
	
	public void setMines(int dx)
	{ mines += dx; }
	
	public void setDarts(int dx)
	{ darts += dx; }
	
	public void setShoot(boolean bool)
	{ shoot = bool; }
	
	public boolean getShoot()
	{ return shoot; }
	
	public boolean getIs_running()
	{ return isRunning; }

	public void setIs_running(boolean is_running) 
	{ this.isRunning = is_running; }
	
	public Sprite getSprite() 
	{ return sprite; }
	
	public String getDirection()
	{ return direction; }
	
	public void setPosition(int x, int y)
	{
		if(x < 8)
			x = 8;
		if(x > 216)
			x = 216;
		if(y < 8)
			y = 8;
		if(y > 296)
			y = 296;
		
		sprite.setPosition(x, y);
	}

	public Player(int spawn_x, int spawn_y, int lives, int ammo, int mines, int darts)
	{
		if(spawn_x < 8) spawn_x = 8;
		if(spawn_x > 216) spawn_x = 216;
		if(spawn_y < 8) spawn_y = 8;
		if(spawn_y > 296) spawn_y = 296;

		prev_x = spawn_x - (spawn_x % 8);
		prev_y = spawn_y - (spawn_y % 8);

		this.lives = lives;
		dead = false;
		ouchTimeout = 0;
		speedUpTimeout = 0;
		chargeTimeout = 0;
		isRunning = false;
		shoot = false;
		this.ammo = ammo;
		this.mines = mines;
		this.darts = darts;
		currentWeapon = 0;
		charged = 0;
		
		try
		{
			up = Image.createImage("/sprite/stand_up.png");
			down = Image.createImage("/sprite/stand_down.png");
			left = Image.createImage("/sprite/stand_left.png");
			right = Image.createImage("/sprite/stand_right.png");
			run_up = Image.createImage("/sprite/run_up.png");
			run_down = Image.createImage("/sprite/run_down.png");
			run_left = Image.createImage("/sprite/run_left.png");
			run_right = Image.createImage("/sprite/run_right.png");
			
			shoot_up = Image.createImage("/sprite/shooting_up.png");
			shoot_down = Image.createImage("/sprite/shooting_down.png");
			shoot_left = Image.createImage("/sprite/shooting_left.png");
			shoot_right = Image.createImage("/sprite/shooting_right.png");
		}
		catch (IOException e)
		{
			System.out.println("NO SPRITE ALARM!!!!");
			System.out.println(e.toString());
			//ALARM
		}
		
		sprite = new Sprite(down,16,16);
		sprite.setPosition(prev_x, prev_y);
		sprite.defineCollisionRectangle(1, 1, 14, 14);
	}
	
	public void setProtection(int timeout)
	{
		if(timeout > ouchTimeout)
			ouchTimeout = timeout;
		
		if(timeout == 0)
		{
			ouchTimeout = 0;
			isShielded = false;
		}
	}
	
	public void speedUp(int timeout)
	{
		if(timeout > speedUpTimeout)
			speedUpTimeout = timeout;
		
		if(timeout == 0)
		{
			speedUpTimeout = 0;
			isSpeeded = false;
		}
	}
	
	public int getOuch_timeout() 
	{ return ouchTimeout; }
	
	public int getSpeedUpTimeout()
	{ return speedUpTimeout; }
	
	public boolean isProtected() 
	{
		if(ouchTimeout > 0)
			return true;
		else
			return false;
	}
	
	public boolean isShielded()
	{ return isShielded; }

	public void setShielded(boolean isShielded)
	{ this.isShielded = isShielded; }

	public boolean isSpeeded()
	{ return isSpeeded; }

	public void setSpeeded(boolean isSpeeded)
	{ this.isSpeeded = isSpeeded; }

	public int getCharged()
	{ return charged; }

	public void setCharged(int dCharge)
	{
		if(dCharge == 0)
		{
			charged = 0;
		}
		else
		{
			this.charged += dCharge;
			
			if(charged == 0)
			{
				chargeTimeout = 0;
			}
		}
	}
	
	public void setChargedTimeout(int timeout)
	{
		if(timeout == 0)
		{
			chargeTimeout = 0;
		}
		else
		{
			chargeTimeout = timeout;
		}
	}
	
	public int getChargedTimeout()
	{ return chargeTimeout; }

	public void tick()
	{
		if(ouchTimeout > 0)
		{
			ouchTimeout--;
			if(ouchTimeout == 0)
				isShielded = false;
		}
		
		if(speedUpTimeout > 0)
		{
			speedUpTimeout--;
			if(speedUpTimeout == 0)
				isSpeeded = false;
		}
		
		if(chargeTimeout > 0)
		{
			chargeTimeout--;
			if(chargeTimeout == 0)
				charged = 0;
		}
	}
	
	public void updateSprite()
	{	
		if(shoot == false)
		{
			if (direction == "left") 
			{
				if (isRunning)
					sprite.setImage(run_left, 16, 16);
				else
					sprite.setImage(left, 16, 16);
			} 
			else if (direction == "right") 
			{
				if (isRunning)
					sprite.setImage(run_right, 16, 16);
				else
					sprite.setImage(right, 16, 16);
			} 
			else if (direction == "up") 
			{
				if (isRunning)
					sprite.setImage(run_up, 16, 16);
				else
					sprite.setImage(up, 16, 16);
			} 
			else if (direction == "down")
			{
				if (isRunning)
					sprite.setImage(run_down, 16, 16);
				else
					sprite.setImage(down, 16, 16);
			}
		}
		else
		{
			if (direction == "left") 
			{
				sprite.setImage(shoot_left, 16, 16);
			} 
			else if (direction == "right") 
			{
				sprite.setImage(shoot_right, 16, 16);
			} 
			else if (direction == "up") 
			{
				sprite.setImage(shoot_up, 16, 16);
			} 
			else if (direction == "down")
			{
				sprite.setImage(shoot_down, 16, 16);
			}
		}
	}

	public void move(String dir, int state, TiledLayer walls)
	{
		int moveSpeed = MOVE_SPEED;
		if(speedUpTimeout > 0)
			moveSpeed++;
		
		if(dir == "left" || dir == "right" || dir == "up" || dir == "down")
		{ direction = dir; }
		
		prev_x = sprite.getX();
		prev_y = sprite.getY();
		
		if(direction == "right")
		{
			sprite.move(moveSpeed, 0);
			if(sprite.collidesWith(walls, false))
			{
				undo();
				sprite.move(moveSpeed, SENSITIVITY);
				if(sprite.collidesWith(walls, false))
				{
					undo();
					sprite.move(moveSpeed, -SENSITIVITY);
					if(sprite.collidesWith(walls, false))
					{ undo(); }
				}
			}
		}
		if(direction == "left")
		{
			sprite.move(-moveSpeed, 0);
			if(sprite.collidesWith(walls, false))
			{
				undo();
				sprite.move(-moveSpeed, SENSITIVITY);
				if(sprite.collidesWith(walls, false))
				{
					undo();
					sprite.move(-moveSpeed, -SENSITIVITY);
					if(sprite.collidesWith(walls, false))
					{ undo(); }
				}
			}
		}
		if(direction == "down")
		{
			sprite.move(0, moveSpeed);
			if(sprite.collidesWith(walls, false))
			{
				undo();
				sprite.move(SENSITIVITY, moveSpeed);
				if(sprite.collidesWith(walls, false))
				{
					undo();
					sprite.move(-SENSITIVITY, moveSpeed);
					if(sprite.collidesWith(walls, false))
					{ undo(); }
				}
			}
		}
		if(direction == "up")
		{
			sprite.move(0, -moveSpeed);
			if(sprite.collidesWith(walls, false))
			{
				undo();
				sprite.move(SENSITIVITY, -moveSpeed);
				if(sprite.collidesWith(walls, false))
				{
					undo();
					sprite.move(-SENSITIVITY, -moveSpeed);
					if(sprite.collidesWith(walls, false))
					{ undo(); }
				}
			}
		}
		
		if(state == 1)
			sprite.nextFrame();
	}
	
	public void paint(Graphics g)
	{
		updateSprite();
		sprite.paint(g);
	}
	
	public void undo()
	{
		sprite.setPosition(prev_x, prev_y);
	}

	public void setLives(int dLives) 
	{
		lives += dLives;
		if(lives < 0)
			lives = 0;
		
		if(lives == 0)
			dead = true;
	}

	public int getLives()
	{ return lives; }
}
