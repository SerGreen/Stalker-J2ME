import java.io.IOException;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;
import javax.microedition.lcdui.game.TiledLayer;


public class Boss
{
	private Image face;
	private Image faceDead;
	private Image faceWTF;
	
	private Sprite head;
	private Sprite faceRoar;
	private Sprite torso;
	private Sprite hands;
	private Sprite leftLeg;
	private Sprite rightLeg;
	private Sprite headbump;
	private Sprite smokeLeg;
	private Sprite smokeHead;
	private Sprite smash;
	private boolean drawSmokeL;
	private boolean drawSmokeH;
	private boolean drawSmash;
	private boolean firstRoar;
	
	private int x;
	private int y;
	
	private boolean isAlive;
	private int state;			//0=walk, 1=run, 2=roar, 3=shoot, 4=foot, 5=headbump, 6=dead, 7=wtf
	private int direction;		//0=down, 1=up, 2=left, 3=right
	private int footState;
	
	private int wavingShift;
	private boolean waveDown;
	
	private static final int headSift = -11;
	private static final int handsShiftY = 4;
	private static final int handsShiftX = -5;
	private static final int legsShiftY = 11;
	
	private int slowdown;
	private int freeze;
	private int roarTime;
	private int timer;
	private int protection;
	
	private int health;
	private int maxHealth;
	
	private Vector bombs;
	private Vector explosions;
	
	public Boss(int spawn_x, int spawn_y, int health)
	{
		Image h_spr = null;
		Image r_face = null;
		Image t_spr = null;
		Image hnd_spr = null;
		Image l_spr = null;
		Image h_bmp = null;
		Image s_l = null;
		Image s_h = null;
		Image smsh = null;
		try
		{
			t_spr = Image.createImage("/boss/torso.png");
			r_face = Image.createImage("/boss/roar_face.png");
			h_spr = Image.createImage("/boss/head.png");
			face = Image.createImage("/boss/face.png");
			faceDead = Image.createImage("/boss/face_dead.png");
			faceWTF = Image.createImage("/boss/face_wtf.png");
			l_spr = Image.createImage("/boss/leg.png");
			hnd_spr = Image.createImage("/boss/hands.png");
			h_bmp = Image.createImage("/boss/head_bump.png");
			s_l = Image.createImage("/boss/smoke_leg.png");
			s_h = Image.createImage("/boss/smoke_head.png");
			smsh = Image.createImage("/boss/smash.png");
		}
		catch (IOException e)
		{ System.out.println("CANT LOAD BOSS! FAIL :("); }
		
		head = new Sprite(h_spr, 29, 15);
		faceRoar = new Sprite(r_face, 29, 15);
		torso = new Sprite(t_spr);
		hands = new Sprite(hnd_spr);
		leftLeg = new Sprite(l_spr);
		rightLeg = new Sprite(l_spr);
		headbump = new Sprite(h_bmp, 29, 15);
		smokeLeg = new Sprite(s_l, 26, 8);
		smokeHead = new Sprite(s_h, 64, 32);
		smash = new Sprite(smsh, 24, 24);
		
		x = spawn_x;
		y = spawn_y;
		
		isAlive = true;
		if(health > 0)
			this.health = health;
		else
			this.health = 20;
		
		maxHealth = this.health;
		
		state = 2;
		roarTime = 100;
		
		wavingShift = 0;
		waveDown = true;
		slowdown = 0;
		direction = 0;
		footState = 0;
		freeze = 0;
		timer = 0;
		protection = 0;
		
		drawSmokeL = false;
		drawSmokeH = false;
		drawSmash = false;
		firstRoar = true;
		
		bombs = new Vector();
		explosions = new Vector();
	}
	
	public int getX()
	{ return x+torso.getWidth()/2; }
	
	public int getY()
	{ return y+torso.getHeight()/2; }
	
	public boolean isAlive()
	{ return isAlive; }
	
	public int getProtection()
	{ return protection; }
	
	public void setProtection(int timeout)
	{
		if(timeout >= 0)
		{
			protection = timeout;
		}
	}
	
	public void setWTF(int time)
	{
		if(state == 0 && isAlive == true)
		{
			state = 7;
			freeze = time;
		}
	}
	
	public void tick(Player player, TiledLayer walls, int difficulty)
	{
		Sprite p = player.getSprite();
		
		if(drawSmokeL == true)
		{
			smokeLeg.nextFrame();
			if(smokeLeg.getFrame() == 0)
				drawSmokeL = false;
		}
		if(drawSmokeH == true)
		{
			smokeHead.nextFrame();
			if(smokeHead.getFrame() == 0)
				drawSmokeH = false;
		}
		if(drawSmash == true)
		{
			smash.nextFrame();
			if(smash.getFrame() == 0)
				drawSmash = false;
		}
		
		slowdown++;
			if(slowdown > 7)
				slowdown = 0;
			
		if(protection > 0)
			protection--;
			
		if(isAlive == true)
		{
			timer++;
			if(timer > 9001)
				timer = 0;
			
			if(freeze > 0)
				freeze--;
			
			if(slowdown == 0 || state == 5)
			{
				if(state != 2 && state != 4)
				{
					if(waveDown == true)
					{
						wavingShift++;
						if(wavingShift == 2)
							waveDown = false;
						
						if(state == 1)
						{
							if(wavingShift > 0)
								waveDown = false;
						}
					}
					else
					{
						wavingShift--;
						if(wavingShift == 0)
							waveDown = true;
					}
				}
				else
				{
					waveDown = true;
					wavingShift = 0;
				}
	
				if(freeze == 0)
				{
					if(state == 7)
					{
						state = 0;
					}
					
					footState++;
					
					if(footState > 3)
					{
						footState = 0;
						
						if(state == 4)
							state = 0;
						
						if(state == 5)
						{
							state = 7;
							freeze = 20;
						}
					}
	
					if(state == 4)
					{
						if(footState == 0)
						{
							freeze = 20;
						}
	
						if(footState == 2)
						{
							freeze = 10;
						}
	
						if(footState == 3)
						{
							freeze = 30;
							drawSmokeL = true;
							smokeLeg.setFrame(0);
							
							int dx = torso.getX()-(p.getX()+8);
							int dy = torso.getY()-(p.getY()+8);
							double dis = Math.sqrt(dx*dx+dy*dy);
							
							if(dis < 65)
							{
								if(player.getOuch_timeout() == 0)
								{
									player.setLives(-2);
									player.setProtection(75);
								}
							}
						}
					}
					
					if(state == 5)
					{
						if(footState == 3)
						{
							freeze = 40;
							drawSmokeH = true;
							smokeHead.setFrame(0);
							
							int dx = (headbump.getX()+headbump.getWidth()/2)-(p.getX()+8);
							int dy = (headbump.getY()+headbump.getHeight()/2)-(p.getY()+8);
							double dis = Math.sqrt(dx*dx+dy*dy);
							
							if(dis < 60)
							{
								if(player.getOuch_timeout() == 0)
								{
									if(difficulty == 0 || difficulty == 1)
									{
										player.setLives(-2);
										player.setProtection(75);
									}
									else
									{
										player.setLives(-3);
										player.setProtection(75);
									}
								}
							}
						}
						
						if(y+torso.getHeight()/2 < 290)
						{
							y+=5;
						}
					}
				}
			}
			
			//System.out.println(firstRoar);
			
			if(state == 2)
			{
				faceRoar.nextFrame();
				
				if(roarTime > 0)
					roarTime--;
				
				if(roarTime == 0)
				{
					if(firstRoar == true)
					{
						firstRoar = false;
						state = 0;
					}
					else
					{
						state = 1;
						
						int dx = (torso.getX()+torso.getWidth()/2)-(p.getX()+8);
						int dy = (torso.getY()+torso.getHeight()/2)-(p.getY()+8);
						
						if(Math.abs(dx) > Math.abs(dy))
						{
							if(dx > 0)
								direction = 2;	//left
							else
								direction = 3;	//right
						}
						else
						{
							if(dy > 0)
								direction = 1;	//up
							else
								direction = 0;	//down
						}
					}
				}
			}
			else
			{
				int dx = (torso.getX()+torso.getWidth()/2)-(p.getX()+8);
				int dy = (torso.getY()+torso.getHeight()/2)-(p.getY()+8);
				double dis = Math.sqrt(dx*dx+dy*dy);
				
				//System.out.println(dis);
				
				if(dis < 50 && state == 0 && dy < 0)
				{
					state = 5;
					footState = 0;
					freeze = 10;
				}
				else if(dis < 50 && state == 0)
				{
					state = 4;
					footState = 0;
				}
			}
			
			//System.out.println(timer);
			
			if(timer % 1500 == 0)
			{
				roarTime = 60;
				state = 2;
			}
			
			if(state == 0)
			{
				if(timer%(151-head.getFrame()*20) == 0)
					shoot(p, 0);
				if(timer%(154-head.getFrame()*20) == 0)
					shoot(p, 1);
				if(timer%(157-head.getFrame()*20) == 0)
					shoot(p, 2);
			}
			

			move(p, walls, player.isAlive());
			
			
			if(checkCollision(p) == true && player.getOuch_timeout() == 0)
			{
				if(state == 1 || state == 5)
				{
					if(difficulty == 0 || difficulty == 1)
					{
						player.setLives(-3);
						player.setProtection(75);
					}
					else
					{
						player.setLives(-4);
						player.setProtection(75);
					}
				}
				else
				{
					if(difficulty == 0)
					{
						player.setLives(-1);
						player.setProtection(75);
					}
					else if(difficulty == 1)
					{
						player.setLives(-2);
						player.setProtection(75);
					}
					else
					{
						player.setLives(-3);
						player.setProtection(75);
					}
				}
			}
		}
		else
		{
			if(slowdown == 0)
			{
				if(footState < 4)
					footState++;
			}
		}
		
		
		for(int i=bombs.size()-1; i>=0; i--)
		{
			Bomb b = ((Bomb) bombs.elementAt(i));
			b.tick(walls);
			if(b.isBoom() == true)
			{
				int bX = b.getSprite().getX()-4;
				int bY = b.getSprite().getY()-4;
				
				explosions.addElement(new Explosion(bX, bY));
				
				int dx = bX-p.getX();
				int dy = bY-p.getY();
				double dis = Math.sqrt(dx*dx+dy*dy);
				
				if(dis < 40)
				{
					if(player.getOuch_timeout() == 0)
					{
						if(difficulty == 0)
						{
							player.setLives(-1);
							player.setProtection(75);
						}
						else if(difficulty == 1)
						{
							player.setLives(-1);
							player.setProtection(75);
						}
						else
						{
							player.setLives(-2);
							player.setProtection(75);
						}
					}
				}
				
				bombs.removeElementAt(i);
			}
		}
		
		for(int i=explosions.size()-1; i>=0; i--)
		{
			Explosion e = ((Explosion) explosions.elementAt(i));
			if(e.getActive() == false)
				explosions.removeElementAt(i);
		}
	}
	
	
	private void move(Sprite p, TiledLayer walls, boolean isPlayerAlive)
	{
		int mX = x+torso.getWidth()/2;
		int mY = y+torso.getHeight()/2;
		
		int pX = p.getX()+8;
		int pY = p.getY()+8;
		
		//System.out.println("mX:"+mX+" mY:"+mY+" pX:"+pX+" pY:"+pY);
		
		Random rnd = new Random();
		
		if(state == 0)
		{
			if(mY > pY && mY > 30)
			{
				if(rnd.nextInt(3) == 0)
					y--;
			}
			else
			{
				if(rnd.nextInt(3) == 0 && mY < 290)
				{
					y++;
				}
				else
				{
					if(rnd.nextInt(10) == 0 && mY > 30)
					{
						y--;
					}
				}
			}
			
			
			if(mX > pX && mX > 32)		//игрок слева от босса
			{
				if(rnd.nextInt(4) == 0)
				{
					x--;
				}
			}
			else if(mX < pX && mX < 208)
			{
				if(rnd.nextInt(4) == 0)
				{
					x++;
				}
			}
		}
		else if(state == 1)
		{
			if(direction < 2)
			{
				if(direction == 0)
				{
					if(mY < 298)
						y += 3;
					else
					{
						state = 7;
						if(isPlayerAlive == true)
							hurt(3);
						smash.setPosition(mX-10, mY-16);
						drawSmash = true;
						freeze = 50;
					}
				}
				else if(direction == 1)
				{
					if(mY > 25)
						y -= 3;
					else
					{
						state = 7;
						if(isPlayerAlive == true)
							hurt(3);
						smash.setPosition(mX-10, mY-25);
						drawSmash = true;
						freeze = 50;
					}
				}
				
				if(mX > pX && mX >32)
				{
					if(rnd.nextInt(3) != 0)
						x-=2;
				}
				else if(mX < pX && mX < 208)
				{
					if(rnd.nextInt(3) != 0)
						x+=2;
				}
			}
			else
			{
				if(direction == 2)
				{
					if(mX > 32)
						x -= 3;
					else
					{
						state = 7;
						if(isPlayerAlive == true)
							hurt(3);
						smash.setPosition(mX-24, mY-20);
						drawSmash = true;
						freeze = 50;
					}
				}
				else if(direction == 3)
				{
					if(mX < 208)
						x += 3;
					else
					{
						state = 7;
						if(isPlayerAlive == true)
							hurt(3);
						smash.setPosition(mX, mY-20);
						drawSmash = true;
						freeze = 50;
					}
				}
				
				if(mY > pY && mY >25)
				{
					if(rnd.nextInt(3) != 0)
						y-=2;
				}
				else if(mY < pY && mY < 298)
				{
					if(rnd.nextInt(3) != 0)
						y+=2;
				}
			}
		}
	}
	
	private void shoot(Sprite p, int i)
	{
		//System.out.println("Pew!");
		
		Random rnd = new Random();
		
		int mX = torso.getX() + torso.getWidth()/2;
		int mY = torso.getY() + torso.getHeight()/2;
		
		int dx = mX - p.getX()+8;
		int dy = mY - p.getY()+8;
		
		int vX = 0;
		int vY = 0;
		
		if(Math.abs(dx) > Math.abs(dy))
		{
			if(dx > 0)
				vX = -1;
			else
				vX = 1;
			
			vY = i-1;
		}
		else
		{
			if(dy > 0)
				vY = -1;
			else
				vY = 1;
			
			vX = i-1;
		}
		
		int time = rnd.nextInt(80-(head.getFrame()*3))+60+(head.getFrame()*6);
		
		bombs.addElement(new Bomb(mX, mY, vX, vY, time));
	}
	
	public void hurt(int dH)
	{
		if(dH < 0)
			dH = -dH;
		
		health -= dH;
		
		if(health <= 0)
		{
			health = 0;
			isAlive = false;
			footState = 0;
			state = 6;
		}
	}
	
	public boolean checkCollision(Sprite s)
	{
		if(s.collidesWith(torso, false))
			return true;
		if(state == 5)
		{
			if(s.collidesWith(headbump, false))
				return true;
		}
		else
		{
			if(s.collidesWith(head, false))
				return true;
		}
		if(s.collidesWith(hands, false))
			return true;
		if(s.collidesWith(leftLeg, false))
			return true;
		if(s.collidesWith(rightLeg, false))
			return true;
		
		return false; 
	}
	
	public void render(Graphics g)
	{
		for(int i=bombs.size()-1; i>=0; i--)
		{
			((Bomb) bombs.elementAt(i)).render(g);
		}
		
		for(int i=explosions.size()-1; i>=0; i--)
		{
			((Explosion) explosions.elementAt(i)).paint(g);
		}
		
		//rendering legs
		if(state == 0)
		{
			if(footState == 0)
			{
				leftLeg.setPosition(x+2, y+legsShiftY-1);
				rightLeg.setPosition(x+22, y+legsShiftY+1);
			}
			else if(footState == 1 || footState == 3)
			{
				leftLeg.setPosition(x+2, y+legsShiftY);
				rightLeg.setPosition(x+22, y+legsShiftY);
			}
			else
			{
				leftLeg.setPosition(x+2, y+legsShiftY+1);
				rightLeg.setPosition(x+22, y+legsShiftY-1);
			}
		}
		else if(state == 1)
		{
			if(footState == 0 || footState == 2)
			{
				leftLeg.setPosition(x+2, y+legsShiftY-1);
				rightLeg.setPosition(x+22, y+legsShiftY);
			}
			else
			{
				leftLeg.setPosition(x+2, y+legsShiftY);
				rightLeg.setPosition(x+22, y+legsShiftY-1);
			}
		}
		else if(state == 4)
		{
			if(footState < 3)
			{
				leftLeg.setPosition(x+2, y+legsShiftY);
				rightLeg.setPosition(x+22, y+legsShiftY-footState);
			}
			else
			{
				leftLeg.setPosition(x+2, y+legsShiftY);
				rightLeg.setPosition(x+22, y+legsShiftY+3);
			}
		}
		else
		{
			leftLeg.setPosition(x+2, y+legsShiftY);
			rightLeg.setPosition(x+22, y+legsShiftY);
		}
		
		leftLeg.paint(g);
		rightLeg.paint(g);
		
		
		if(drawSmokeL == true)
		{
			smokeLeg.setPosition(rightLeg.getX()-8, rightLeg.getY());
			smokeLeg.paint(g);
		}
		if(drawSmokeH == true)
		{
			smokeHead.setPosition(headbump.getX()-17, headbump.getY());
			smokeHead.paint(g);
		}
		
		
		//rendering torso and hands
		if(isAlive == true)
		{
			torso.setPosition(x, y);
			torso.paint(g);
			if(state != 5 && state != 7)
				hands.setPosition(x+handsShiftX, y+handsShiftY+wavingShift);
			else
				hands.setPosition(x+handsShiftX, y+handsShiftY);
			hands.paint(g);
			
			//rendering head
			if(state != 5)
			{
				int headIndex = 5-(int)(Math.ceil((float)health/((float)maxHealth/5)));
				head.setFrame(headIndex);
				if(state != 7)
					head.setPosition(x+2, y+headSift+wavingShift);
				else
					head.setPosition(x+2, y+headSift);
				head.paint(g);
			}
			else
			{
				int index = Math.max(footState-1, 0);
				index = Math.min(index, 2);
				headbump.setFrame(index);
				headbump.setPosition(x+2, y+headSift+footState*6);
				headbump.paint(g);
			}
		}
		else
		{
			torso.setPosition(x, y+footState);
			torso.paint(g);
			hands.setPosition(x+handsShiftX, y+handsShiftY+footState*2);
			hands.paint(g);
			
			head.setFrame(5);
			head.setPosition(x+2, y+headSift+footState*2);
			head.paint(g);
		}
		
		
		if(state != 5)
		{
			if(isAlive == true)
			{
				if(state == 2)
				{
					faceRoar.setPosition(x+2, y+headSift);
					faceRoar.paint(g);
				}
				else if(state == 7)
				{
					g.drawImage(faceWTF, x+2, y+headSift, 0);
				}
				else
				{
					g.drawImage(face, x+2, y+headSift+wavingShift, 0);
				}
			}
			else
			{
				g.drawImage(faceDead, x+2, y+headSift+footState*2+2, 0);
			}
		}
		
		if(drawSmash == true)
		{
			smash.paint(g);
		}
	}
}
