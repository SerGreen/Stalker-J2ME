
import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;


public class Monster
{
	private int BASE_MOVE_SPEED;
	private int MOVE_SPEED;
	private double SMART_LEVEL;
	private Sprite sprite;
	private String direction;
	
	private int prev_x;
	private int prev_y;
	
	private Image up;
	private Image down;
	private Image left;
	private Image right;
	
	private Image face_up;
	private Image face_down;
	private Image face_left;
	private Image face_right;
	private Image back_up;
	private Image back_down;
	private Image back_left;
	private Image back_right;
	
	private int stunnedTimeout;
	private int slowTimeout;
	private boolean shotedInFace; 
	
	
	public Sprite getSprite() 
	{ return sprite; }
	
	public String getDirection()
	{ return direction; }

	public int getStunnedTimeout()
	{ return stunnedTimeout; }

	public void setStunnedTimeout(int stunnedTimeout) 
	{ this.stunnedTimeout = stunnedTimeout; }
	
	public void setShotedInFace(boolean isInFace)
	{ this.shotedInFace = isInFace; }

	public void setSpeed(int newSpeed)
	{
		if(newSpeed > 0)
		{
			BASE_MOVE_SPEED = newSpeed;
			MOVE_SPEED = BASE_MOVE_SPEED;
		}
	}
	
	public void slowDown(int newSpeed, int time)
	{
		if(time < 0)
			time = 0;
		
		if(newSpeed >= 0)
		{
			MOVE_SPEED = newSpeed;
			slowTimeout = time;
		}
	}
	
	public Monster(int spawn_x, int spawn_y, double smartLevel, int moveSpeed) 
	{
		if(spawn_x < 8) spawn_x = 8;
		if(spawn_x > 224) spawn_x = 224;
		if(spawn_y < 8) spawn_y = 8;
		if(spawn_y > 304) spawn_y = 304;

		prev_x = spawn_x - (spawn_x % 8);
		prev_y = spawn_y - (spawn_y % 8);
		
		stunnedTimeout = 0;
		shotedInFace = true;
		
		if(moveSpeed > 0)
		{
			BASE_MOVE_SPEED = moveSpeed;
		}
		else
		{
			BASE_MOVE_SPEED = 2;
		}
		
		MOVE_SPEED = BASE_MOVE_SPEED;
		
		if(smartLevel < 0)
			smartLevel = 0.01;
		if(smartLevel > 1)
			smartLevel = 1;
		
		SMART_LEVEL = smartLevel;
		
		try
		{
			up = Image.createImage("/sprite/zombie_up.png");
			down = Image.createImage("/sprite/zombie_down.png");
			left = Image.createImage("/sprite/zombie_left.png");
			right = Image.createImage("/sprite/zombie_right.png");
			
			face_up = Image.createImage("/sprite/zombie_shoted_up_face.png");
			face_down = Image.createImage("/sprite/zombie_shoted_down_face.png");
			face_left = Image.createImage("/sprite/zombie_shoted_left_face.png");
			face_right = Image.createImage("/sprite/zombie_shoted_right_face.png");
			back_up = Image.createImage("/sprite/zombie_shoted_up_back.png");
			back_down = Image.createImage("/sprite/zombie_shoted_down_back.png");
			back_left = Image.createImage("/sprite/zombie_shoted_left_back.png");
			back_right = Image.createImage("/sprite/zombie_shoted_right_back.png");
		}
		catch (IOException e)
		{
			System.out.println("NO PICTURE ALARM!!!!");
			System.out.println(e.toString());
			//ALARM
		}
		
		sprite = new Sprite(down,16,16);
		sprite.setPosition(prev_x, prev_y);
		
		direction = "down";
	}

	public void updateSprite()
	{	
		if(stunnedTimeout == 0)
		{
			if (direction == "left") 
			{
				sprite.setImage(left, 16, 16);
			} 
			else if (direction == "right") 
			{
				sprite.setImage(right, 16, 16);
			} 
			else if (direction == "up") 
			{
				sprite.setImage(up, 16, 16);
			} 
			else if (direction == "down")
			{
				sprite.setImage(down, 16, 16);
			}
		}
		else
		{
			if (direction == "left") 
			{
				if(shotedInFace == true)
					sprite.setImage(face_left, 16, 16);
				else
					sprite.setImage(back_left, 16, 16);
			} 
			else if (direction == "right") 
			{
				if(shotedInFace == true)
					sprite.setImage(face_right, 16, 16);
				else
					sprite.setImage(back_right, 16, 16);
			} 
			else if (direction == "up") 
			{
				if(shotedInFace == true)
					sprite.setImage(face_up, 16, 16);
				else
					sprite.setImage(back_up, 16, 16);
			} 
			else if (direction == "down")
			{
				if(shotedInFace == true)
					sprite.setImage(face_down, 16, 16);
				else
					sprite.setImage(back_down, 16, 16);
			}
		}
	}

	public void move(TiledLayer walls, Sprite player, Vector monsters, int state, int iteration)
	{
		if(stunnedTimeout == 0)
		{
			if(slowTimeout > 0)
			{
				slowTimeout--;
				if(slowTimeout == 0)
				{
					MOVE_SPEED = BASE_MOVE_SPEED;
				}
			}
			
			tryMove(walls, player);
			
			if(state == 1)
				sprite.nextFrame();
			
			//логика бота:
			int x = sprite.getX();
			int y = sprite.getY();
			
			int dx = 0;
			int dy = 0;
			
			if(direction == "left")
				dx = -MOVE_SPEED;
			if(direction == "right")
				dx = MOVE_SPEED;
			if(direction == "up")
				dy = -MOVE_SPEED;
			if(direction == "down")
				dy = MOVE_SPEED;
			
			Random rnd = new Random();
			
			sprite.move(dx, dy);
			
			boolean collidesWithMonster = false;
			for(int i=0; i<monsters.size(); i++)
			{
				if(sprite.getX() != ((Monster) monsters.elementAt(i)).getSprite().getX() ||
				   sprite.getY() != ((Monster) monsters.elementAt(i)).getSprite().getY())
				{
					if(sprite.collidesWith(((Monster) monsters.elementAt(i)).getSprite(), false) == true)
					{ 
						if(((Monster) monsters.elementAt(i)).getStunnedTimeout() == 0)
							collidesWithMonster = true;
					}
				}
			}
			if(sprite.collidesWith(walls, false) || collidesWithMonster)
				{
					sprite.setPosition(x,y);
					if(direction == "left")
						{
							if(rnd.nextDouble() < SMART_LEVEL)
							{
								if(player.getY() < sprite.getY())
									direction = "up";
								else
									direction = "down";
							}
							else
							{
								if(rnd.nextDouble() < 0.5)
									direction = "up";
								else direction = "down";
							}
						}
					else if(direction == "up")
						{
							if(rnd.nextDouble() < SMART_LEVEL)
							{
								if(player.getX() < sprite.getX())
									direction = "left";
								else
									direction = "right";
							}
							else
							{
								if(rnd.nextDouble() < 0.5)
									direction = "right";
								else direction = "left";
							}
						}
					else if(direction == "right")
						{
							if(rnd.nextDouble() < SMART_LEVEL)
							{
								if(player.getY() < sprite.getY())
									direction = "up";
								else
									direction = "down";
							}
							else
							{
								if(rnd.nextDouble() < 0.5)
									direction = "down";
								else direction = "up";
							}
						}
					else if(direction == "down")
						{
							if(rnd.nextDouble() < SMART_LEVEL)
							{
								if(player.getX() < sprite.getX())
									direction = "left";
								else
									direction = "right";
							}
							else
							{
								if(rnd.nextDouble() < 0.5)
									direction = "left";
								else direction = "right";
							}
						}
					
					if(iteration < 4)
						move(walls, player, monsters, state, iteration+1);
				}
		}
	}
	
	private void tryMove(TiledLayer walls, Sprite player)
	{
		int x = sprite.getX();
		int y = sprite.getY();
		
		Random rnd = new Random();
		
		int p_x = player.getX();
		int p_y = player.getY();
		
		int upleft_side = 0;
		int downright_side = 0;
		
		if(direction == "left" || direction == "right")
		{
			sprite.move(0,-5);			//передвинем сначала его вверх и проверим допустим ли ход
										//двигаем на 5, т.к. некоторые проходы могут быть немного шире, чем да.
			if(!sprite.collidesWith(walls, false))
			{ upleft_side++; }			//если нет столкновения, значит проход в эту сторону разрешён
			sprite.setPosition(x,y);	//вернули спрайт на место и теперь проверим в другую сторону
			
			sprite.move(0,5);
			if(!sprite.collidesWith(walls, false))
			{ downright_side++; }
			sprite.setPosition(x,y);
			
			if(p_y != y)
			{
				if(y > p_y && upleft_side == 1)
				{
					if(rnd.nextDouble() < SMART_LEVEL)
					{
						direction = "up";
					}
				}
				else if(y < p_y && downright_side == 1)
				{
					if(rnd.nextDouble() < SMART_LEVEL)
					{
						direction = "down";
					}
				}
				else
				{
					int seed = rnd.nextInt(3);
					if(seed == 0 && upleft_side == 1)
					{ direction = "up"; }
					if(seed == 1 && downright_side == 1)
					{ direction = "down"; }
				}
			}
			else
			{
				int seed = rnd.nextInt(3);
				if(seed == 0 && upleft_side == 1)
				{ direction = "up"; }
				if(seed == 1 && downright_side == 1)
				{ direction = "down"; }
			}
		}
		else 
		{
			sprite.move(-5, 0);
			if (!sprite.collidesWith(walls, false))
			{
				upleft_side++;
			}
			sprite.setPosition(x, y);

			sprite.move(5, 0);
			if (!sprite.collidesWith(walls, false)) 
			{
				downright_side++;
			}
			sprite.setPosition(x, y);

			if (p_x != x) 
			{
				if (x > p_x && upleft_side == 1) 
				{
					if (rnd.nextDouble() < SMART_LEVEL)
					{
						direction = "left";
					}
				} 
				else if (x < p_x && downright_side == 1) 
				{
					if (rnd.nextDouble() < SMART_LEVEL) 
					{
						direction = "right";
					}
				}
				else 
				{
					int seed = rnd.nextInt(3);
					if (seed == 0 && upleft_side == 1) 
					{
						direction = "left";
					}
					if (seed == 1 && downright_side == 1) 
					{
						direction = "right";
					}
				}
			}
			else 
			{
				int seed = rnd.nextInt(3);
				if (seed == 0 && upleft_side == 1)
				{
					direction = "left";
				}
				if (seed == 1 && downright_side == 1) 
				{
					direction = "right";
				}
			}
		}

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

}
