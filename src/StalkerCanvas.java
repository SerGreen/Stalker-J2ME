
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.*;
import javax.microedition.lcdui.game.*;
import javax.microedition.rms.InvalidRecordIDException;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;
import javax.microedition.rms.RecordStoreNotOpenException;

//import Editor.EditorCanvas;

public class StalkerCanvas extends GameCanvas implements Runnable, mGameCanvas
{
	private Main parent;
	private EditorCanvas editorCanvas;
	
	private boolean testMode;
	private int testMenuPosition;
	
	private boolean bossLevel;
	private Boss boss;
	private boolean bossDefeated;
	private boolean congrats;
	
	private int difficulty;
	private double mult;
	
	private static final int SPEED_UP_TIME = 600;
	private static final int SHIELD_TIME = 800;
	private static final int CHARGE_TIME = 2000;
	private static final int PICKUP_TIMEOUT = 50;
	private static final int RESUME_DELAY = 75;
	private static final String LOOSE_TEXT = "You LOOSE!!!";
	private static final String WIN_TEXT = "You WIN!!!";
	private static final int DETONATE_RADIUS = 40;
	private static final int STUNNING_TIME = 300;
	private static final String UP_MOVE = "up";
	private static final String DOWN_MOVE = "down";
	private static final String RIGHT_MOVE = "right";
	private static final String LEFT_MOVE = "left";
	private int coinsMaxAmount;
	private int gemsMaxAmount;
	private int TIME;
	private static final int ACTION_DELAY = 10;
	private static final int FRAME_DELAY = 10;
	private static final int ANIMATION_SPEED = 6;
	private int currentPickupTimeout;
	private boolean mTrucking;
	private boolean paused;
	private Player player;
	private Vector monsters;
	private Vector mines;
	private Vector flesh;
	private Vector explosions;
	private Vector bloods;
	private Vector spikes;
	private Vector levers;
	private Vector plates;
	
	//private Vector gems;
	//private Vector coins;
	//private Vector hearts;
	private Vector darts;
	private Vector pickups;
	private Vector scoreUps;
	private Image background;
	private TiledLayer walls;
	//private TiledLayer treasure;
	//private int animationState;
	private int state;
	private int score;
	private Sprite exit;
	private boolean gameOver;
	private String gameOverMessage;
	private int gemsAmount;
	private int coinsAmount;
	private int time;
	private Flame flame;
	private int actionDelay;
	//private int[] upsMap;
	private int[] map;
	private int coinsCollected;
	private int currentLevel;
	private Image heart;
	private Image shell;
	private Image mineIcon;
	private Image coin;
	private Image noShell;
	private Image noMine;
	private Image noDart;
	private Image dartIcon;
	private mFont fontWhiteSmall;
	private mFont fontWhiteLarge;
	private mFont fontGrayLarge;
	private mFont fontBlackLarge;
	private mFont fontBlackSmall;
	private mFont fontGreenSmall;
	private mFontHugeNumbers fontWhiteHugeDigits;
	private Sprite countdown;
	private int timer;
	private Sprite aura;
	private Image congratulations;
	
	private boolean fireStillPressed;
	
	private RecordStore scores;
	
	private int layer;		//0=Menu, 1=Game, 2=Shop, 3=Scores, 4=New highscore, 5=Help
	
	//Menu variables
	private int menuCurrentPosition;
	private Vector menuPositions;
	private Image menuBackground;
	private Image menuNewGame;
	private Image menuNewGameHL;
	private Image menuScores;
	private Image menuScoresHL;
	private Image menuEditor;
	private Image menuEditorHL;
	private Image menuExit;
	private Image menuExitHL;
	private Image menuResume;
	private Image menuResumeHL;
	private Image menuTopSign;
	private static final String EXIT = "Exit";
	private static final String START = "New game";
	private static final String RESUME = "Resume";
	private static final String SCORES = "Scores";
	private static final String EDITOR = "Editor";
	private int menuBackgroundShift;
	private boolean menuMovingLeft;
	private int menuTicker;
	private HelpButton help;
	
	private Image newGameBkgr;
	private Image newGameBkgr_paused;
	private Image newGameChooseSet;
	private Image newGameBuildin;
	private Image newGameCustom;
	private Image newGameMarker;
	private Image buildIn;
	private Image buildInTab;
	private Image custom;
	private int newGamePosition;
	private int buildInPosition;
	private int buildInTabPosition;
	private int customSelected;
	private int customShift;
	private int customPosition;
	private boolean buildInMode;		//true=buildIn, false=custom
	private static final int CUSTOM_MAX = 8;
	private int menuLayer;
	private int openedLevels;
	private int playedLevels;
	private int customLevels;
	private Vector saveNames;
	private Image preview;
	
	//Shop variables
	private int shopCurrentPosition;
	private Vector shopPositions;
	private Image shopBackground;
	private Image shopButton;
	private Image shopCoin;
	private Image shopShell;
	private Image shopMine;
	private Image shopHeart;
	private Image shopDart;
	//private int shopCurrentDelay;
	//private static final int SHOP_DELAY = 33;
	private static final int COST_SHELL = 6;
	private static final int COST_MINE = 11;
	private static final int COST_DART = 4;
	private static final int COST_HEART = 17;
	
	//Scores variables
	private Image scoresBackground;
	
	//Highscore variables
	private Image highscoreBackground;
	private Image highscoreButton;
	private Image highscoreArrows;
	private String highscoreName;
	private int highscoreCurrentPosition;

	private int lastPlayerLives;
	private int lastPlayerAmmo;
	private int lastPlayerMines;
	private int lastPlayerDarts;
	
	private boolean saveFound;
	private int savedLevel;
	private int savedScore;
	private int savedCoins;
	
	private Image help_bg;
	private Image helpContent;
	private Image credits;
	private Image helpIcons;
	private Image clearProgress;
	private Image clearProgressHl;
	private Image clearScores;
	private Image clearScoresHl;
	private Image deleteCustom;
	private Image deleteCustomHl;
	private Image difficultyLevel;
	private Image difficultyLevelHl;
	private Sprite difficultyLevels;
	private Sprite difficultyLevelsHL;
	private Sprite tabs;
	private int helpTabPosition;
	private int helpSettingsPosition;
	private boolean helpConfirm;
	private String helpConfirmText;
	private Image helpConfirmBg;
	private boolean renderingHelp;
	
	private Image loading;
	private boolean isLoading;

	public StalkerCanvas(Main parent) 
	{
		super(false);
		this.setFullScreenMode(true);
		
		this.parent = parent;
		//this.editorCanvas = editorCanvas;
		
		fontWhiteSmall = new mFont("white", false);
		fontWhiteLarge = new mFont("white", true);
		fontGrayLarge = new mFont("gray", true);
		fontBlackLarge = new mFont("black", true);
		fontBlackSmall = new mFont("black", false);
		fontGreenSmall = new mFont("green", false);
		fontWhiteHugeDigits = new mFontHugeNumbers();
		layer = 0;
		renderingHelp = false;
		
		isLoading = false;
		
		initMenu();
		
		Image cntdwn = null;
		try 
		{
			cntdwn = Image.createImage("/sprite/countdown.png");
		} 
		catch (IOException e1)
		{ e1.printStackTrace(); }
		
		countdown = new Sprite(cntdwn, 50, 50);
		countdown.setPosition(95, 135);
		
		testMode = false;
		
		try 
		{
			//RecordStore.deleteRecordStore("Scores");
			scores = RecordStore.openRecordStore("Scores", true, RecordStore.AUTHMODE_PRIVATE, true);
			
			if(scores.getNumRecords() == 0)
			{
				byte[] s1 = "IMPOSIBRU=100000".getBytes();
				byte[] s2 = "Superior=75000".getBytes();
				byte[] s3 = "Expert=50000".getBytes();
				byte[] s4 = "Profi=25000".getBytes();
				byte[] s5 = "Intermed=10000".getBytes();
				byte[] s6 = "Amateur=5000".getBytes();
				byte[] s7 = "Junior=2500".getBytes();
				byte[] s8 = "Novice=1250".getBytes();
				byte[] s9 = "Beginner=800".getBytes();
				byte[] s10 = "Newbie=500".getBytes();
				
				scores.addRecord(s1, 0, s1.length);
				scores.addRecord(s2, 0, s2.length);
				scores.addRecord(s3, 0, s3.length);
				scores.addRecord(s4, 0, s4.length);
				scores.addRecord(s5, 0, s5.length);
				scores.addRecord(s6, 0, s6.length);
				scores.addRecord(s7, 0, s7.length);
				scores.addRecord(s8, 0, s8.length);
				scores.addRecord(s9, 0, s9.length);
				scores.addRecord(s10, 0, s10.length);
			}
		} 
		catch (RecordStoreFullException e) 
		{ e.printStackTrace(); } 
		catch (RecordStoreNotFoundException e) 
		{ e.printStackTrace(); } 
		catch (RecordStoreException e) 
		{ e.printStackTrace(); }
		
		try
		{
			/*
			if(false)		//thing for wiping if necessary
			{
				RecordStore.deleteRecordStore("OpenedLevels");
				System.out.println("+");
			}
			*/
			
			RecordStore opened = RecordStore.openRecordStore("OpenedLevels", true, RecordStore.AUTHMODE_PRIVATE, true);
			if(opened.getNumRecords() == 0)
			{
				opened.addRecord("1".getBytes(), 0, "1".length());
				openedLevels = 1;
				opened.addRecord("1".getBytes(), 0, "1".length());
				playedLevels = 1;
				opened.addRecord("1".getBytes(), 0, "1".length());
				difficulty = 1;
			}
			else
			{
				openedLevels = Integer.parseInt(new String(opened.getRecord(1)));
				playedLevels = Integer.parseInt(new String(opened.getRecord(2)));
				difficulty = Integer.parseInt(new String(opened.getRecord(3)));
				
				//openedLevels = 12;
				//playedLevels = 12;
			}
			
			if(difficulty == 0)
				mult = 0.5;
			else if(difficulty == 1)
				mult = 1;
			else if(difficulty == 2)
				mult = 2;
			else
				mult = 4;
			
			opened.closeRecordStore();
			updateCustomLevelsAmount();
		}
		catch (RecordStoreFullException e1)
		{ e1.printStackTrace(); }
		catch (RecordStoreNotFoundException e1)
		{ e1.printStackTrace(); }
		catch (RecordStoreException e1)
		{ e1.printStackTrace(); }
	}
	
	//public boolean isRunning()
	//{ return mTrucking; }

	private void getSaveNames()
	{
		saveNames = new Vector();

		for(int i=0; i<customLevels; i++)
		{
			try
			{
				RecordStore rs = RecordStore.openRecordStore("customLevel_"+(i+1), false, RecordStore.AUTHMODE_PRIVATE, false);
				saveNames.addElement(new String(rs.getRecord(2)));
				rs.closeRecordStore();
			}
			catch (RecordStoreFullException e)
			{ e.printStackTrace(); }
			catch (RecordStoreNotFoundException e)
			{ System.out.println("No customLevel_"+(i+1)+" save!"); }
			catch (RecordStoreException e)
			{ e.printStackTrace(); }

		}
	}
	
	private String openCustomLevelAsText(int number)
	{
		String lvl = null;
		
		try
		{
			RecordStore save = RecordStore.openRecordStore("customLevel_"+Integer.toString(number), false, RecordStore.AUTHMODE_PRIVATE, false);
			lvl = new String(save.getRecord(1));
			save.closeRecordStore();
		}
		catch (RecordStoreFullException e)
		{ e.printStackTrace(); }
		catch (RecordStoreNotFoundException e)
		{ e.printStackTrace(); }
		catch (RecordStoreException e)
		{ e.printStackTrace(); }
		
		return lvl;
	}
	
	private void updateCustomLevelsAmount()
	{
		try
		{
			RecordStore settings = RecordStore.openRecordStore("CustomLevels", true, RecordStore.AUTHMODE_PRIVATE, true);
			if(settings.getNumRecords() == 0)
			{
				settings.addRecord("0".getBytes(), 0, "0".length());
				customLevels = 0;
			}
			else
			{
				customLevels = Integer.parseInt(new String(settings.getRecord(1)));
			}
			settings.closeRecordStore();

			/*
			if(false)		//for wiping
			{
				for(int i=0; i<customLevels; i++)
				{
					RecordStore.deleteRecordStore("customLevel_"+(i+1));
				}
				RecordStore.deleteRecordStore("CustomLevels");
				System.out.println("+");
			}
			*/
		}
		catch (RecordStoreFullException e1)
		{ e1.printStackTrace(); }
		catch (RecordStoreNotFoundException e1)
		{ e1.printStackTrace(); }
		catch (RecordStoreException e1)
		{ e1.printStackTrace(); }
	}

	private Image createPreview(boolean isBuildIn)
	{
		Image img = Image.createImage(62, 82);
		Graphics g = img.getGraphics();
		
		if(menuLayer == 3 || (buildInPosition+buildInTabPosition*5+1) <= playedLevels)
		{
			String level = null;

			if(isBuildIn == true)
			{
				level = getText("/levels/level_"+(buildInPosition+buildInTabPosition*5+1)+".lvl");
			}
			else
			{
				level = openCustomLevelAsText(customSelected);
			}

			String lvl = level.substring(0, 1280);
			
			g.setColor(188, 174, 159);
			g.fillRect(1, 1, 60, 80);
			
			String str = "";

			for(int j=0; j<lvl.length()-30; j++)
			{
				str = str.concat(lvl.substring(j, j+30));
				j += 31;
			}

			lvl = str;

			for(int i=0; i<lvl.length(); i++)
			{
				String s = lvl.substring(i, i+1);
				int ff = Integer.parseInt(s);

				int column = i % 30;
				int row = (i - column) / 30;

				if(ff == 1)
				{
					g.setColor(50, 50, 50);
					g.fillRect(1+column*2, 1+row*2, 2, 2);
				}
				if(ff == 7)
				{
					g.setColor(250, 190, 0);
					g.fillRect(1+column*2, 1+row*2, 4, 4);
				}
				if(ff == 3)
				{
					g.setColor(0, 170, 0);
					g.fillRect(1+column*2, 1+row*2, 4, 4);
				}
			}
			
			if((buildInPosition+buildInTabPosition*5+1) > openedLevels && menuLayer == 2)
			{
				/*
				 * filling full preview
				 * 
				int pixel[] = new int[60*80];
				for(int i=0; i<60*80; i++)
				{
					pixel[i] = ColorBB.argb(180, 80, 80, 80);
				}
				
				g.drawRGB(pixel, 0, 60, 1, 1, 60, 80, true);
				*/
				
				/*
				for(int i=1; i<61; i++)
				{
					for(int j=1; j<81; j++)
					{
						if((i%2==0 && j%2==0) == false)
						{
							int pix[] = { ColorBB.argb(180, 80, 80, 80) };
							g.drawRGB(pix, 0, 60, i, j, 1, 1, true);
						}
					}
				}
				*/
				
				g.setColor(50, 50, 50);
				g.fillRect(2, 33, 58, 14);
				fontWhiteLarge.print("LOCKED", g, 4, 35, 9);
			}
		}
		else
		{
			g.setColor(80, 80, 80);
			g.fillRoundRect(1, 1, 60, 80, 0, 0);
			fontWhiteLarge.print("LOCKED", g, 4, 35, 9);
		}
		
		g.setColor(255, 255, 255);
		g.drawRect(0, 0, 61, 81);
		
		g = null;
		
		return img;
	}
	
	private void initHelp()
	{
		Image img = null;
		Image dl = null;
		Image dlhl = null;
		try
		{
			help_bg = Image.createImage("/help/help_bg.png");
			helpContent = Image.createImage("/help/help_content.png");
			credits = Image.createImage("/help/credits.png");
			helpIcons = Image.createImage("/help/help_icons.png");
			clearProgress = Image.createImage("/help/clear_progress.png");
			clearProgressHl = Image.createImage("/help/clear_progress_hl.png");
			clearScores = Image.createImage("/help/clear_scores.png");
			clearScoresHl = Image.createImage("/help/clear_scores_hl.png");
			deleteCustom = Image.createImage("/help/delete_custom.png");
			deleteCustomHl = Image.createImage("/help/delete_custom_hl.png");
			helpConfirmBg = Image.createImage("/help/confirm.png");
			difficultyLevel = Image.createImage("/help/difficulty.png");
			difficultyLevelHl = Image.createImage("/help/difficulty_hl.png");
			img = Image.createImage("/help/tabs.png");
			dl = Image.createImage("/help/difficultyLevels.png");
			dlhl = Image.createImage("/help/difficultyLevels_HL.png");
		}
		catch (IOException e)
		{ System.out.println("HELP LOADING FAIL!!! HA-HA!"); }
		
		tabs = new Sprite(img, 240, 25);
		tabs.setPosition(0, 0);
		tabs.setFrame(0);
		img = null;
		
		difficultyLevels = new Sprite(dl, 240, 37);
		difficultyLevels.setPosition(0, 234);
		dl = null;
		
		difficultyLevelsHL = new Sprite(dlhl, 240, 37);
		difficultyLevelsHL.setPosition(0, 234);
		dlhl = null;
		
		helpTabPosition = 0;
		helpSettingsPosition = 0;
		helpConfirm = false;
		helpConfirmText = null;
		
		isLoading = false;
	}
	
	private void clearHelp()
	{
		help_bg = null;
		helpContent = null;
		credits = null;
		clearProgress = null;
		clearProgressHl = null;
		clearScores = null;
		clearScoresHl = null;
		deleteCustom = null;
		deleteCustomHl = null;
		helpConfirmBg = null;
		helpIcons = null;
		tabs = null;
		helpConfirmText = null;
		difficultyLevel = null;
		difficultyLevelHl = null;
		difficultyLevels = null;
		difficultyLevelsHL = null;
	}
	
	private void initMenu()
	{
		try
		{
			menuBackground = Image.createImage("/menu/background.png");
			menuNewGame = Image.createImage("/menu/new_game.png");
			menuNewGameHL = Image.createImage("/menu/new_game_hl.png");
			menuScores = Image.createImage("/menu/scores.png");
			menuScoresHL= Image.createImage("/menu/scores_hl.png");
			menuExit = Image.createImage("/menu/exit.png");
			menuExitHL = Image.createImage("/menu/exit_hl.png");
			menuEditor = Image.createImage("/menu/editor.png");
			menuEditorHL = Image.createImage("/menu/editor_hl.png");
			menuResume = Image.createImage("/menu/continue.png");
			menuResumeHL = Image.createImage("/menu/continue_hl.png");
			menuTopSign = Image.createImage("/menu/top_sign.png");
			newGameBkgr  = Image.createImage("/menu/newGame_bkgr.png");
			newGameBkgr_paused  = Image.createImage("/menu/newGame_bkgr_paused.png");
			newGameChooseSet = Image.createImage("/menu/new_game_choose_level_set.png");
			newGameBuildin = Image.createImage("/menu/newGame_buildin.png");
			newGameCustom = Image.createImage("/menu/newGame_custom.png");
			newGameMarker = Image.createImage("/menu/newGame_marker.png");
			buildIn = Image.createImage("/menu/build-in.png");
			buildInTab = Image.createImage("/menu/build-in_tab.png");
			custom = Image.createImage("/menu/custom.png");
			loading = Image.createImage("/menu/loading.png");
		}
		catch (IOException e)
		{ System.out.println("MENU LOADING FAIL!"); }
		
		newGamePosition = 0;
		buildInPosition = 0;
		buildInTabPosition = 0;
		customShift = 0;
		customPosition = 0;
		buildInMode = true;
		customSelected = 1;
		menuLayer = 0;
		saveNames = new Vector();
		preview = Image.createImage(60, 80);
		
		//menuCurrentDelay = 0;
		menuPositions = new Vector();
		menuPositions.addElement(EXIT);
		menuPositions.addElement(EDITOR);
		menuPositions.addElement(SCORES);
		menuPositions.addElement(START);
		
		menuBackgroundShift = (new Random().nextInt(397))+1;
		menuMovingLeft = true;
		menuTicker = 0;
		
		saveFound = loadGame();
		
		menuCurrentPosition = menuPositions.size()-1;
		help = new HelpButton(3, 296, true);
		
		isLoading = false;
	}
	
	private void initShop()
	{
		try
		{
			shopBackground = Image.createImage("/shop/shop.png");
			shopButton = Image.createImage("/shop/next_level.png");
			shopCoin = Image.createImage("/shop/shop_coin.png");
			shopShell = Image.createImage("/shop/shop_shell.png");
			shopMine = Image.createImage("/shop/shop_mine.png");
			shopHeart = Image.createImage("/shop/shop_heart.png");
			shopDart = Image.createImage("/shop/shop_dart.png");
		}
		catch (IOException e)
		{ System.out.println("SHOP LOADING FAIL!"); }
		
		//shopCurrentDelay = 0;
		shopPositions = new Vector();
		shopPositions.addElement("+1 Round = "+COST_SHELL);
		shopPositions.addElement("+1 Mine = "+COST_MINE);
		shopPositions.addElement("+1 Dart = "+COST_DART);
		shopPositions.addElement("+1 Heart = "+COST_HEART);
		shopCurrentPosition = 0;
		
		isLoading = false;
	}
	
	private void initScores()
	{
		try
		{
			scoresBackground = Image.createImage("/scores/scores.png");
		}
		catch (IOException e)
		{ System.out.println("SCORES LOADING FAIL!"); }
		
		isLoading = false;
	}
	
	private void initHighScore()
	{
		try
		{
			highscoreBackground = Image.createImage("/scores/new_highscore.png");
			highscoreButton = Image.createImage("/scores/continue.png");
			highscoreArrows = Image.createImage("/scores/arrows.png");
		}
		catch (IOException e)
		{ System.out.println("NEW HIGHSCORE LOADING FAIL!"); }
		
		highscoreCurrentPosition = 0;
		highscoreName = "---------";
		
		isLoading = false;
	}

	private void init() 
	{
		Image au = null;
		try 
		{
			//background = Image.createImage("/background/game"+Integer.toString(new Random().nextInt(5)+1)+".png");
			exit = new Sprite(Image.createImage("/sprite/door.png"), 16, 16);
			heart = Image.createImage("/sprite/heart.png");
			shell = Image.createImage("/sprite/shell.png");
			mineIcon = Image.createImage("/sprite/mineIcon.png");
			coin = Image.createImage("/sprite/coin.png");
			noShell = Image.createImage("/sprite/no_shell.png");
			noMine = Image.createImage("/sprite/no_mine.png");
			noDart = Image.createImage("/sprite/no_dart.png");
			dartIcon = Image.createImage("/sprite/dartIcon.png");
			au = Image.createImage("/sprite/aura.png");
			congratulations = Image.createImage("/scores/congratulations.png");
		} 
		catch (IOException e) 
		{ System.out.println("ERROR WHILE INIT"); }
		
		aura = new Sprite(au, 20, 20);
		
		fireStillPressed = false;
		
		monsters = new Vector();
		mines = new Vector();
		darts = new Vector();
		flesh = new Vector();
		explosions = new Vector();
		bloods = new Vector();
		spikes = new Vector();
		levers = new Vector();
		plates = new Vector();
		//coins = new Vector();
		//gems = new Vector();
		//hearts = new Vector();
		pickups = new Vector();
		scoreUps = new Vector();
		
		//Vector v = new Vector();
		//v.addElement(new Coordinates(128, 8));
		//v.addElement(new Coordinates(128, 16));
		//v.addElement(new Coordinates(120, 144));
		//levers.addElement(new Lever(64, 8, v, false));					
		
		if(testMode == false)
		{
			walls = loadLevel(currentLevel);
			flame = new Flame(player.getSprite().getX(),player.getSprite().getY());
		}
		
		//animationState = -1;
		state = 0;
		actionDelay = ACTION_DELAY;
		currentPickupTimeout = PICKUP_TIMEOUT;
		
		if(bossLevel == true)
		{
			if(difficulty == 0)
				boss = new Boss(100, 100, 24);
			else if(difficulty == 1)
				boss = new Boss(100, 100, 28);
			else if(difficulty == 2)
				boss = new Boss(100, 100, 32);
			else
				boss = new Boss(100, 100, 32);
			
			bossDefeated = false;
		}
		else
		{
			boss = null;
		}
		
		gameOver = false;
		
		isLoading = false;
	}
	
	private void clear()
	{
		//player = null;
		flame = null;
		monsters = null;
		mines = null;
		darts = null;
		flesh = null;
		walls = null;
		explosions = null;
		bloods = null;
		spikes = null;
		levers = null;
		plates= null;
		//treasure = null;
		background = null;
		exit = null;
		heart = null;
		shell = null;
		mineIcon = null;
		//coins = null;
		//gems = null;
		//hearts = null;
		pickups = null;
		scoreUps = null;
		aura = null;
		
		if(bossLevel == true)
			boss = null;
	}
	
	private void createPlayer(int x, int y, int lives, int ammo, int mines, int darts)
	{
		player = null;
		player = new Player(x, y, lives, ammo, mines, darts);
	}


	public void start() 
	{
		mTrucking = true;
		//paused = false;
		Thread t = new Thread(this);
		t.start();
	}
	
	private void reset() 
	{
		clear();
		init();
	}

	public void pause() 
	{
		menuPositions.addElement(RESUME);
		paused = true;
		timer = RESUME_DELAY;
		menuCurrentPosition++;
		layer = 0;
		menuLayer = 0;
	}
	
	private void resume()
	{
		menuPositions.removeElementAt(menuPositions.size()-1);
		//paused = false;
		menuCurrentPosition--;
		layer = 1;
	}
	
	public void stop() 
	{
		mTrucking = false;
		parent.destroyApp(true);
	}
	
	public int getCurrentLevel()
	{ return currentLevel; }
	
	public void saveGame()
	{
		deleteSave();
		
		try 
		{
			RecordStore save = RecordStore.openRecordStore("Save", true, RecordStore.AUTHMODE_PRIVATE, true);
			String s = String.valueOf(currentLevel) + "," + String.valueOf(savedScore) + "," + String.valueOf(lastPlayerLives) + "," + String.valueOf(lastPlayerAmmo) + "," + String.valueOf(lastPlayerMines) + "," + String.valueOf(savedCoins) + "," + String.valueOf(lastPlayerDarts + "," + String.valueOf(buildInMode));
			save.addRecord(s.getBytes(), 0, s.length());
			save.closeRecordStore();
		}
		catch (RecordStoreFullException e) 
		{ e.printStackTrace(); }
		catch (RecordStoreNotFoundException e) 
		{ e.printStackTrace(); }
		catch (RecordStoreException e) 
		{ e.printStackTrace(); }
	}

	private boolean loadGame()
	{
		boolean result = false;
		
		lastPlayerLives = 5;
		lastPlayerAmmo = 3;
		lastPlayerMines = 3;
		lastPlayerDarts = 3;
		savedCoins = 0;
		savedScore = 0;
		savedLevel = 1;
		
		try 
		{
			RecordStore save = RecordStore.openRecordStore("Save", false, RecordStore.AUTHMODE_PRIVATE, false);
			byte[] buffer = save.getRecord(1);
			save.closeRecordStore();
			
			String s = new String(buffer);
			System.out.println(s);
			
			int index = s.indexOf(",");
			savedLevel = Integer.parseInt(s.substring(0, index));
			int indexTwo = s.indexOf(",", index+1);
			savedScore = Integer.parseInt(s.substring(index+1, indexTwo));
			index = indexTwo;
			indexTwo = s.indexOf(",", index+1);
			lastPlayerLives = Integer.parseInt(s.substring(index+1, indexTwo));
			index = indexTwo;
			indexTwo = s.indexOf(",", index+1);
			lastPlayerAmmo = Integer.parseInt(s.substring(index+1, indexTwo));
			index = indexTwo;
			indexTwo = s.indexOf(",", index+1);
			lastPlayerMines = Integer.parseInt(s.substring(index+1, indexTwo));
			index = indexTwo;
			indexTwo = s.indexOf(",", index+1);
			savedCoins = Integer.parseInt(s.substring(index+1, indexTwo));
			index = indexTwo;
			indexTwo = s.indexOf(",", index+1);
			lastPlayerDarts = Integer.parseInt(s.substring(index+1, indexTwo));
			if(s.substring(indexTwo+1, s.length()).compareTo("true") == 0)
			{
				buildInMode = true;
			}
			else
			{
				buildInMode = false;
			}
			menuPositions.addElement(RESUME);
			
			result = true;
		}
		catch (RecordStoreNotFoundException e) 
		{ System.out.println("Me dispiace, no such record store"); }
		catch (RecordStoreException e)  
		{ e.printStackTrace(); }
		
		return result;
	}
	
	private void deleteSave()
	{
		/*
		lastPlayerLives = 5;
		lastPlayerAmmo = 3;
		lastPlayerMines = 3;
		lastPlayerDarts = 3;
		savedScore = 0;
		savedCoins = 0;
		savedLevel = 1;
		*/
		
		try 
		{
			RecordStore.deleteRecordStore("Save");
		}
		catch (RecordStoreNotFoundException e) 
		{ System.out.println("Me dispiace, no such record store"); }
		catch (RecordStoreException e)  
		{ e.printStackTrace(); }
	}
	
	public void run() 
	{
		Graphics g = getGraphics();

		while (mTrucking == true) 
		{
			if(layer == 0)
			{
				menuInput();
				help.tick();
				renderMenu(g);
			}
			
			if(layer == 2)
			{
				//shopInput();
				renderShop(g);
			}
			
			if(layer == 3)
			{
				//scoresInput();
				renderScores(g);
			}
			
			if(layer == 4)
			{
				//highscoreInput();
				renderHighscore(g);
			}
			
			if(layer == 5)
			{
				renderingHelp = true;
				renderHelp(g);
				renderingHelp = false;
			}
			
			if(layer == 1)
			{
				if(paused == false)
				{
					input();
					
					for(int i=0; i<monsters.size(); i++)
					{ 
						((Monster) monsters.elementAt(i)).move(walls, player.getSprite(), monsters, state, 0);
					}
		
					if(currentPickupTimeout == 0 && time % 5 == 0)
					{
						tryCreatePickup(0.015);
					}
		
					for(int i=0; i<flesh.size(); i++)
					{ 
						((Flesh) flesh.elementAt(i)).move(walls);
					}
					
					render(g);
					
					for(int i=explosions.size()-1; i>=0; i--)
					{
						if(((Explosion) explosions.elementAt(i)).getActive() == false)
							explosions.removeElementAt(i);
					}
					
					 
					tick();
					animateTreasure();
						
					if(gameOver == false)
					{
						if(player.isAlive() == false)
							gameOver(false);
					}
					
					if(bossLevel == false)
					{
						if(gemsAmount == gemsMaxAmount)
						{ exit.setFrame(1); }
					}
					else
					{
						if(boss.isAlive() == false)
						{
							exit.setFrame(1);
							
							if(bossDefeated == false)
							{
								bossDefeated = true;
								int dScore = (int) (1000 * mult);
								scoreUps.addElement(new ScoreUp(dScore, boss.getX(), boss.getY()));
								score += dScore;
							}
						}
					}
					
					
					
					try 
					{
						Thread.sleep(FRAME_DELAY);
					} 
					catch (InterruptedException ie) 
					{
						// SLEEPING FAIL!!!
					}
				}
				else
				{
					if(testMode == false)
					{
						timer--;
						if(timer == 0)
						{
							paused = false;
						}
					}
					
					render(g);
				}
			}
		}
	}
	

	
	private void menuInput() 
	{
		
		int keyStates = getKeyStates();
		
		/*
		if ((keyStates & UP_PRESSED) != 0) 
		{
			menuCurrentDelay = MENU_DELAY;
			menuCurrentPosition++;
			if(menuCurrentPosition > menuPositions.size()-1)
			{ menuCurrentPosition = menuPositions.size()-1; }
		}
		else if ((keyStates & DOWN_PRESSED) != 0) 
		{
			menuCurrentDelay = MENU_DELAY;
			menuCurrentPosition--;
			if(menuCurrentPosition < 0)
			{ menuCurrentPosition = 0; }
		}
		*/
		
		if ((keyStates & FIRE_PRESSED) != 0) 
		{
			if(fireStillPressed == false)
			{	
				fireStillPressed = true;
				//menuCurrentDelay = MENU_DELAY;
				/*
				if(((String) menuPositions.elementAt(menuCurrentPosition)) == START)
				{
					try 
					{
						RecordStore.deleteRecordStore("Save");
					}
					catch (RecordStoreNotFoundException e) 
					{ System.out.println("Me dispiace, no such record store"); }
					catch (RecordStoreException e)  
					{ e.printStackTrace(); }
				
					resetGame();		
					
					reset();
					
					if(paused == true)
					{
						resume();
						menuCurrentPosition++;		//<- костыль
					}
					else
					{
						paused = true;
					}
				}
				*/
				if(menuLayer == 0)
				{
					if(((String) menuPositions.elementAt(menuCurrentPosition)) == START)
					{
						updateCustomLevelsAmount();
						
						if(customLevels > 0)
						{
							menuLayer = 1;
							newGamePosition = 0;
						}
						else
						{
							menuLayer = 2;
							buildInPosition = 0;
							buildInTabPosition = 0;
							preview = createPreview(true);
						}
					}
				}
				else if(menuLayer == 1)
				{
					if(newGamePosition == 0)
					{
						menuLayer = 2;
						buildInPosition = 0;
						preview = createPreview(true);
					}
					else if(newGamePosition == 1)
					{
						updateCustomLevelsAmount();
						getSaveNames();
						customPosition = 0;
						customShift = 0;
						customSelected = 1;
						menuLayer = 3;
						preview = createPreview(false);
					}
				}
				else if(menuLayer == 2 || menuLayer == 3)
				{
					int level;
					if(menuLayer == 2)
					{
						level = buildInPosition+buildInTabPosition*5+1;
						buildInMode = true;
					}
					else
					{
						level = customSelected;
						buildInMode = false;
					}
					
					if(level <= openedLevels || buildInMode == false)
					{
						isLoading = true;
						Graphics g = getGraphics();
						renderMenu(g);
						
						deleteSave();
						
						currentLevel = level;
						
						resetGame();
						reset();
						
						if(paused == true)
						{
							resume();
							menuCurrentPosition++;		//<- костыль
						}
						else
						{
							paused = true;
						}
					}
				}
					
					/*
					else if(((String) menuPositions.elementAt(menuCurrentPosition)) == EXIT)
					{
						stop();
					}
					*/
				
				/*else */
				if(((String) menuPositions.elementAt(menuCurrentPosition)) == RESUME)
				{
					updateCustomLevelsAmount();
					getSaveNames();
					fireStillPressed = true;
					
					if(saveFound == true)
					{
						saveFound = false;
						menuPositions.removeElementAt(menuPositions.size()-1);
						menuCurrentPosition = menuPositions.size()-1;
						coinsCollected = savedCoins;
						score = savedScore;
						currentLevel = savedLevel;
						layer = 1;
						timer = RESUME_DELAY;
						paused = true;
						
						createPlayer(8, 8, lastPlayerLives, lastPlayerAmmo, lastPlayerMines, lastPlayerDarts);
						
						reset();
					}
					else
					{
						actionDelay = ACTION_DELAY;
						resume();
					}
					
				}
				
					/*
					else if(((String) menuPositions.elementAt(menuCurrentPosition)) == SCORES)
					{
						//menuCurrentDelay = MENU_DELAY;
						initScores();
						layer = 3;
					}
					*/
			}
		}
		else
		{
			fireStillPressed = false;
		}
		
	}

	private void resetGame()
	{
		coinsCollected = 0;
		score = 0;
		//currentLevel = 1;
		layer = 1;
		timer = RESUME_DELAY;
		
		if(difficulty == 0)
		{ createPlayer(8, 8, 10, 5, 5, 5); }
		if(difficulty == 1)
		{ createPlayer(8, 8, 5, 3, 3, 3); }
		if(difficulty == 2)
		{ createPlayer(8, 8, 3, 2, 1, 1); }
		if(difficulty == 3)
		{ createPlayer(8, 8, 1, 1, 0, 0); }
	}
	
	
	private void addScore(int value, String name) 
	{
		int[] currentScores = new int[10];
		String[] currentNames = new String[10]; 
		byte[] record;
		String s;
		String v;
		String n;
		
		for(int i=0; i<10; i++)
		{
			try 
			{
				record = scores.getRecord(i+1);
				s = new String(record);
				n = s.substring(0, s.indexOf("="));
				v = s.substring(s.indexOf("=")+1, s.length());

				currentNames[i] = n;
				currentScores[i] = Integer.parseInt(v);
			} 
			catch (RecordStoreNotOpenException e) 
			{ e.printStackTrace(); } 
			catch (InvalidRecordIDException e) 
			{ e.printStackTrace(); }
			catch (RecordStoreException e) 
			{ e.printStackTrace(); }
		}
		
		int index = 0;
		for(int i=0; i<10; i++)
		{
			if(value > currentScores[i])
			{
				index = i;
				break;
			}
		}
		
		for(int i=9; i>=index+1; i--)
		{
			currentScores[i] = currentScores[i-1];
			currentNames[i] = currentNames[i-1];
		}
		currentScores[index] = value;
		currentNames[index] = name;
		
		
		for(int i=0; i<10; i++)
		{
			try 
			{
				String str = currentNames[i]+"="+String.valueOf(currentScores[i]);
				scores.setRecord(i+1, str.getBytes(), 0, str.length());
			} 
			catch (RecordStoreNotOpenException e) 
			{ e.printStackTrace(); } 
			catch (InvalidRecordIDException e) 
			{ e.printStackTrace(); }
			catch (RecordStoreException e) 
			{ e.printStackTrace(); }
		}
	}

	private boolean checkHighscore(int value)
	{
		boolean isNewHighscore = false;
		
		int[] currentScores = new int[10]; 
		byte[] record;
		String s;
		String v;
		
		for(int i=0; i<10; i++)
		{
			try 
			{
				record = scores.getRecord(i+1);
				s = new String(record);
				v = s.substring(s.indexOf("=")+1, s.length());
				
				currentScores[i] = Integer.parseInt(v);
			} 
			catch (RecordStoreNotOpenException e) 
			{ e.printStackTrace(); } 
			catch (InvalidRecordIDException e) 
			{ e.printStackTrace(); }
			catch (RecordStoreException e) 
			{ e.printStackTrace(); }
		}
		
		for(int i=0; i<10; i++)
		{
			if(value > currentScores[i])
			{
				isNewHighscore = true;
				break;
			}
		}
		
		return isNewHighscore;
	}
	
	private void input() 
	{
		int keyStates = getKeyStates();
			
		if(!gameOver)
		{
			if ((keyStates & LEFT_PRESSED) != 0) 
			{
				player.move(LEFT_MOVE, state, walls);
				player.setIs_running(true);
			}
			
			else if ((keyStates & RIGHT_PRESSED) != 0) 
			{
				player.move(RIGHT_MOVE, state, walls);
				player.setIs_running(true);
			}
			else if ((keyStates & DOWN_PRESSED) != 0) 
			{
				player.move(DOWN_MOVE, state, walls);
				player.setIs_running(true);
			}
			else if ((keyStates & UP_PRESSED) != 0) 
			{
				player.move(UP_MOVE, state, walls);
				player.setIs_running(true);
			}
			else
			{
				player.setIs_running(false);
			}
			
			if ((keyStates & FIRE_PRESSED) != 0) 
			{
				if(actionDelay == 0)
				{
					switch(player.getCurrentWeapon())
					{
						case 0:
							if(player.getAmmo() > 0)
							{
								shoot();
								
								actionDelay = ACTION_DELAY;
							}
							break;
							
						case 1:
							if(player.getMines() > 0)
							{
								mines.addElement(new Mine(player.getSprite().getX(), player.getSprite().getY()));
								player.setMines(-1);
								actionDelay = ACTION_DELAY;
							}
							break;
							
						case 2:
							if(player.getDarts() > 0)
							{
								player.setDarts(-1);
								
								String dir = player.getDirection();
								int direction = 0;
								int x = 0;
								int y = 0;
								
								if(dir == "left")
									direction = 0;
								else if(dir == "up")
									direction = 1;
								else if(dir == "right")
									direction = 2;
								else if(dir == "down")
									direction = 3;
								
								switch(direction)
								{
									case 0:
										x = player.getSprite().getX()-5;
										y = player.getSprite().getY()+6;
										break;
									case 1:
										x = player.getSprite().getX()+6;
										y = player.getSprite().getY()-5;
										break;
									case 2:
										x = player.getSprite().getX()+16;
										y = player.getSprite().getY()+6;
										break;
									case 3:
										x = player.getSprite().getX()+6;
										y = player.getSprite().getY()+16;
										break;
								}
								
								darts.addElement(new Dart(x, y, 5, direction));
								actionDelay = ACTION_DELAY;
							}
							break;
					}
				}
			}
		}
		else
		{
			if ((keyStates & FIRE_PRESSED) != 0)
			{
				if(actionDelay == 0)
				{
					if(congrats == true)
					{
						congrats = false;
						
						if(checkHighscore(score) == true)
						{
							initHighScore();
							layer = 4;
						}
						else
						{
							currentLevel = 1;
							coinsCollected = 0;
							score = 0;
							layer = 0;
							menuLayer = 0;
							clear();
							
						}
						
						reset();
					}
					else
					{
						if(testMode == false)
						{
							if(player.isAlive() == true)
							{
								String ss = null;
	
								if(buildInMode == true)
								{
									try
									{
										ss = getText("/levels/level_"+(currentLevel+1)+".lvl");
									}
									catch(NullPointerException e)
									{e.printStackTrace();}
								}
								else
								{
									if(currentLevel < customLevels)
										ss = "spike!";
								}
								
								if(ss != null)
								{
									initShop();
									layer = 2;
								}
								else 
								{
									deleteSave();
									congrats = true;
									actionDelay = ACTION_DELAY;
								}
							}
							else
							{
								deleteSave();						

								if(checkHighscore(score) == true)
								{
									initHighScore();
									layer = 4;
								}
								else
								{
									currentLevel = 1;
									coinsCollected = 0;
									score = 0;
									layer = 0;
									menuLayer = 0;
									clear();
								}
								
								reset();
							}
	
							
						}
						else
						{
							testMode= false;
							mTrucking = false;
							parent.changeCanvas(editorCanvas);
							editorCanvas = null;
							layer = 0;
							menuLayer = 0;
							clear();
						}
					}
				}
			}
		}
		 
	}
	
	protected void keyPressed(int keyCode) 
	{	
		super.keyPressed(keyCode);
		
		int keyStates = getGameAction(keyCode);
		
		if(layer == 1)
		{
			//System.out.println(keyCode);
			
			if(keyCode == KEY_POUND || keyCode == -7)	//# and right shift
			{
				player.nextWeapon();
			}
			
			if(keyCode == KEY_STAR)
			{
				player.prevWeapon();
			}
			
			if(keyCode == 48)	//0
			{
				if(gameOver == false)
				{
					for(int i=0; i<levers.size(); i++)
					{
						int l_x = ((Lever) levers.elementAt(i)).getSprite().getX()+8;
						int l_y = ((Lever) levers.elementAt(i)).getSprite().getY()+8;

						int p_x = player.getSprite().getX()+8;
						int p_y = player.getSprite().getY()+8;

						double distance = Math.sqrt(Math.abs((p_x-l_x)*(p_x-l_x))+(p_y-l_y)*(p_y-l_y));

						if(distance < 16)
						{
							((Lever) levers.elementAt(i)).changeState(walls, spikes);
						}
					}
				}
			}
			
			if(keyCode == -6)	//left shift
			{
				if(gameOver == false)
				{
					if(testMode == false)
					{
						pause();
					}
					else
					{
						paused = true;
						testMenuPosition = 0;
					}
				}
			}
			
			if(keyCode == -5) //fire
			{
				if(testMode == true && paused == true && gameOver == false)
				{
					if(testMenuPosition == 0)
					{
						paused = false;
						actionDelay = ACTION_DELAY;
					}
					else
					{
						mTrucking = false;
						parent.changeCanvas(editorCanvas);
						editorCanvas = null;
						paused = false;
						testMode= false;
						layer = 0;
						clear();
					}
				}
			}
			
			if(keyCode == -1)
			{
				if(testMode == true && paused == true && gameOver == false)
				{
					if(testMenuPosition > 0)
					{
						testMenuPosition--;
					}
				}
			}
			
			if(keyCode == -2)
			{
				if(testMode == true && paused == true && gameOver == false)
				{
					if(testMenuPosition < 1)
					{
						testMenuPosition++;
					}
				}
			}
		}
		
		else if(layer == 3)
		{
			if (keyStates == FIRE) 
			{
				layer = 0;
				menuLayer = 0;
				fireStillPressed = true;
				clear();
			}
		}
		
		else if(layer == 2)
		{
				if (keyStates == DOWN) 
				{
					shopCurrentPosition++;
					if(shopCurrentPosition > shopPositions.size())
					{ shopCurrentPosition = shopPositions.size(); }
				}
				else if (keyStates == UP) 
				{
					shopCurrentPosition--;
					if(shopCurrentPosition < 0)
					{ shopCurrentPosition = 0; }
				}
				
				if (keyStates == FIRE) 
				{
					if(shopCurrentPosition == 0)
					{
						if(coinsCollected >= COST_SHELL)
						{
							coinsCollected -= COST_SHELL;
							player.setAmmo(1);
						}
					}
					
					else if(shopCurrentPosition == 1)
					{
						if(coinsCollected >= COST_MINE)
						{
							coinsCollected -= COST_MINE;
							player.setMines(1);
						}
					}
					
					else if(shopCurrentPosition == 2)
					{
						if(coinsCollected >= COST_DART)
						{
							coinsCollected -= COST_DART;
							player.setDarts(1);
						}
					}
					
					else if(shopCurrentPosition == 3)
					{
						if(coinsCollected >= COST_HEART)
						{
							coinsCollected -= COST_HEART;
							player.setLives(1);
						}
					}
					
					else if(shopCurrentPosition == shopPositions.size())
					{
						isLoading = true;
						
						lastPlayerLives = player.getLives();
						lastPlayerAmmo = player.getAmmo();
						lastPlayerMines = player.getMines();
						lastPlayerDarts = player.getDarts();
						savedScore = score;
						savedCoins = coinsCollected;
						
						currentLevel++;
						
						if(buildInMode == true)
						{
							if(currentLevel > openedLevels && (currentLevel-1) % 5 == 0)
							{
								try
								{
									RecordStore opened = RecordStore.openRecordStore("OpenedLevels", false, RecordStore.AUTHMODE_PRIVATE, true);
									opened.setRecord(1, String.valueOf(currentLevel).getBytes(), 0, String.valueOf(currentLevel).length());
									opened.closeRecordStore();
									openedLevels = currentLevel;
								}
								catch (RecordStoreFullException e1)
								{ e1.printStackTrace(); }
								catch (RecordStoreNotFoundException e1)
								{ e1.printStackTrace(); }
								catch (RecordStoreException e1)
								{ e1.printStackTrace(); }
							}
							
							if(currentLevel > playedLevels)
							{
								try
								{
									RecordStore opened = RecordStore.openRecordStore("OpenedLevels", false, RecordStore.AUTHMODE_PRIVATE, true);
									opened.setRecord(2, String.valueOf(currentLevel).getBytes(), 0, String.valueOf(currentLevel).length());
									opened.closeRecordStore();
									playedLevels = currentLevel;
								}
								catch (RecordStoreFullException e1)
								{ e1.printStackTrace(); }
								catch (RecordStoreNotFoundException e1)
								{ e1.printStackTrace(); }
								catch (RecordStoreException e1)
								{ e1.printStackTrace(); }
							}
						}
						
						timer = RESUME_DELAY;
						paused = true;
						reset();
						layer = 1;
					}
				}
		}
		
		else if(layer == 5)
		{
			if(helpConfirm == false)
			{
				if (keyStates == LEFT) 
				{
					if(helpTabPosition > 0)
					{
						helpTabPosition--;
						tabs.prevFrame();
						helpSettingsPosition = 0;
					}
				}
				else if (keyStates == RIGHT) 
				{
					if(helpTabPosition < 2)
					{
						helpTabPosition++;
						tabs.nextFrame();
						helpSettingsPosition = 0;
					}
				}
				else if (keyStates == UP) 
				{
					if(helpTabPosition == 1)
					{
						if(helpSettingsPosition > 0)
							helpSettingsPosition--;
					}
				}
				else if (keyStates == DOWN) 
				{
					if(helpTabPosition == 1)
					{
						if(helpSettingsPosition < 3)
							helpSettingsPosition++;
					}
				}
				if(keyStates == FIRE)
				{
					if(helpTabPosition == 1)
					{
						if(helpSettingsPosition == 3)
						{
							difficulty++;
							if(difficulty > 3)
								difficulty = 0;
							
							if(difficulty == 0)
								mult = 0.5;
							else if(difficulty == 1)
								mult = 1;
							else if(difficulty == 2)
								mult = 2;
							else
								mult = 4;
							
							try
							{
								RecordStore opened = RecordStore.openRecordStore("OpenedLevels", false, RecordStore.AUTHMODE_PRIVATE, true);
								opened.setRecord(3, Integer.toString(difficulty).getBytes(), 0, Integer.toString(difficulty).length());								
								opened.closeRecordStore();
							}
							catch (RecordStoreFullException e1)
							{ e1.printStackTrace(); }
							catch (RecordStoreNotFoundException e1)
							{ e1.printStackTrace(); }
							catch (RecordStoreException e1)
							{ e1.printStackTrace(); }
						}
						else
						{
							if(helpSettingsPosition == 0)
								helpConfirmText = "reset high scores?";
							else if(helpSettingsPosition == 1)
								helpConfirmText = "reset game progress?";
							else if(helpSettingsPosition == 2)
								helpConfirmText = "delete all custom levels?";

							helpConfirm = true;
						}
					}
				}
				if(keyCode == -6)	//left shift
				{
					fireStillPressed = true;
					layer = 0;
					boolean clearing = true;
					while(clearing == true)
					{
						if(renderingHelp == false)
						{
							clearHelp();
							clearing = false;
						}
					}
				}
			}
			else
			{
				if(keyCode == -6)	//left shift
				{
					helpConfirm = false;
					
					if(helpSettingsPosition == 0)
					{
						try 
						{
							scores.closeRecordStore();
							RecordStore.deleteRecordStore("Scores");
							scores = RecordStore.openRecordStore("Scores", true, RecordStore.AUTHMODE_PRIVATE, true);

							byte[] s1 = "IMPOSIBRU=100000".getBytes();
							byte[] s2 = "Superior=75000".getBytes();
							byte[] s3 = "Expert=50000".getBytes();
							byte[] s4 = "Profi=25000".getBytes();
							byte[] s5 = "Intermed=10000".getBytes();
							byte[] s6 = "Amateur=5000".getBytes();
							byte[] s7 = "Junior=2500".getBytes();
							byte[] s8 = "Novice=1250".getBytes();
							byte[] s9 = "Beginner=800".getBytes();
							byte[] s10 = "Newbie=500".getBytes();

							scores.addRecord(s1, 0, s1.length);
							scores.addRecord(s2, 0, s2.length);
							scores.addRecord(s3, 0, s3.length);
							scores.addRecord(s4, 0, s4.length);
							scores.addRecord(s5, 0, s5.length);
							scores.addRecord(s6, 0, s6.length);
							scores.addRecord(s7, 0, s7.length);
							scores.addRecord(s8, 0, s8.length);
							scores.addRecord(s9, 0, s9.length);
							scores.addRecord(s10, 0, s10.length);
						} 
						catch (RecordStoreFullException e) 
						{ e.printStackTrace(); } 
						catch (RecordStoreNotFoundException e) 
						{ e.printStackTrace(); } 
						catch (RecordStoreException e) 
						{ e.printStackTrace(); }
					}
					else if(helpSettingsPosition == 1)
					{
						openedLevels = 1;
						playedLevels = 1;
						
						try
						{
							deleteSave();
							if(paused == true)
							{
								menuPositions.removeElementAt(menuPositions.size()-1);
								menuCurrentPosition = menuPositions.size()-1;
								currentLevel = 1;
								coinsCollected = 0;
								difficulty = 1;
								mult = 1;
								score = 0;
								paused = false;
								clear();
							}

							RecordStore opened = RecordStore.openRecordStore("OpenedLevels", false, RecordStore.AUTHMODE_PRIVATE, true);
							opened.setRecord(1, "1".getBytes(), 0, "1".length());
							opened.setRecord(2, "1".getBytes(), 0, "1".length());
							opened.setRecord(3, "1".getBytes(), 0, "1".length());
							opened.closeRecordStore();
							
							updateCustomLevelsAmount();
						}
						catch (RecordStoreFullException e1)
						{ e1.printStackTrace(); }
						catch (RecordStoreNotFoundException e1)
						{ e1.printStackTrace(); }
						catch (RecordStoreException e1)
						{ e1.printStackTrace(); }
					}
					else if(helpSettingsPosition == 2)
					{
						try
						{
							for(int i=0; i<customLevels; i++)
							{
								RecordStore.deleteRecordStore("customLevel_"+(i+1));
							}

							RecordStore settings = RecordStore.openRecordStore("CustomLevels", false, RecordStore.AUTHMODE_PRIVATE, true);
							settings.setRecord(1, "0".getBytes(), 0, "0".length());
							settings.closeRecordStore();
							customLevels = 0;
						}
						catch (RecordStoreFullException e1)
						{ e1.printStackTrace(); }
						catch (RecordStoreNotFoundException e1)
						{ e1.printStackTrace(); }
						catch (RecordStoreException e1)
						{ e1.printStackTrace(); }
					}
				}
				if(keyCode == -7)	//rightShift
				{
					helpConfirm = false;
				}
			}
		}
		
		else if(layer == 0)
		{
			if(keyCode == KEY_STAR)
			{
				if(menuLayer == 0)
				{
					isLoading = true;
					Graphics g = getGraphics();
					renderMenu(g);
					initHelp();
					layer = 5;
				}
			}
			
			if (keyStates == RIGHT) 
			{
				if(menuLayer == 2)
				{
					if(buildInTabPosition < 4)
					{
						buildInTabPosition++;
						buildInPosition = 0;
						preview = createPreview(true);
					}
				}
			}
			else if (keyStates == LEFT) 
			{
				if(menuLayer == 2)
				{
					if(buildInTabPosition > 0)
					{
						buildInTabPosition--;
						buildInPosition = 0;
						preview = createPreview(true);
					}
				}
			}
			else if (keyStates == UP) 
			{
				if(menuLayer == 0)
				{
					menuCurrentPosition++;
					if(menuCurrentPosition > menuPositions.size()-1)
					{ menuCurrentPosition = menuPositions.size()-1; }
				}
				else if(menuLayer == 1)
				{
					if(newGamePosition > 0)
					{
						newGamePosition--;
					}
				}
				else if(menuLayer == 2)
				{
					if(buildInPosition > 0)
					{
						buildInPosition--;
						preview = createPreview(true);
					}
				}
				else if(menuLayer == 3)
				{
					if(customPosition > 0)
					{
						customPosition--;
						customSelected--;
						preview = createPreview(false);
					}
					else if(customShift > 0)
					{
						customShift--;
						customSelected--;
						preview = createPreview(false);
					}
				}
			}
			else if (keyStates == DOWN) 
			{
				if(menuLayer == 0)
				{
					menuCurrentPosition--;
					if(menuCurrentPosition < 0)
					{ menuCurrentPosition = 0; }
				}
				else if(menuLayer == 1)
				{
					if(newGamePosition < 1)
					{
						newGamePosition++;
					}
				}
				else if(menuLayer == 2)
				{
					if(buildInPosition < 4)
					{
						buildInPosition++;
						preview = createPreview(true);
					}
				}
				else if(menuLayer == 3)
				{
					if(customPosition < Math.min(CUSTOM_MAX-1, customLevels-1))
					{
						customPosition++;
						customSelected++;
						preview = createPreview(false);
					}
					else if(customSelected < customLevels)
					{
						customShift++;
						customSelected++;
						preview = createPreview(false);
					}
				}
			}
			
			if(keyCode == -6)	//left shift
			{
				if(menuLayer == 0)
				{
					isLoading = true;
					Graphics g = getGraphics();
					renderMenu(g);
					
					initHelp();
					layer = 5;
				}
				else if(menuLayer == 1)
				{
					menuLayer = 0;
					newGamePosition = 0;
				}
				else if(menuLayer == 2 || menuLayer == 3)
				{
					if(customLevels > 0)
					{
						menuLayer = 1;
						newGamePosition = 0;
					}
					else
					{
						menuLayer = 0;
					}
				}
			}

			if (keyStates == FIRE) 
			{
				if(menuLayer == 0)
				{
					/*
					if(((String) menuPositions.elementAt(menuCurrentPosition)) == START)
					{
						coinsCollected = 0;
						score = 0;
						currentLevel = 1;
						layer = 1;

						createPlayer(8, 8, 5, 3, 3);
						reset();

						if(paused == true)
						{
							resume();
							menuCurrentPosition++;		//<- костыль
						}
					}
					 */
					
					if(((String) menuPositions.elementAt(menuCurrentPosition)) == EXIT)
					{
						if(currentLevel > 1)
						{
							saveGame();
						}

						stop();
					}

					/*
					else if(((String) menuPositions.elementAt(menuCurrentPosition)) == RESUME)
					{
						actionDelay = ACTION_DELAY;
						resume();
					}
					 */

					else if(((String) menuPositions.elementAt(menuCurrentPosition)) == SCORES)
					{
						initScores();
						layer = 3;
					}
					else if(((String) menuPositions.elementAt(menuCurrentPosition)) == EDITOR)
					{
						EditorCanvas canvas = new EditorCanvas(parent, this);
						mTrucking = false;
						timer = RESUME_DELAY;
						parent.changeCanvas(canvas);
					}
				}

			}
		}
		
		else if(layer == 4)
		{
			if (keyStates == RIGHT) 
			{
				highscoreCurrentPosition++;
				if(highscoreCurrentPosition > 9)
				{ highscoreCurrentPosition = 0; }
			}
			else if (keyStates == LEFT) 
			{
				highscoreCurrentPosition--;
				if(highscoreCurrentPosition < 0)
				{ highscoreCurrentPosition = 9; }
			}
			else if (keyStates == UP) 
			{
				if(highscoreCurrentPosition != 9)
				{
					char c = highscoreName.charAt(highscoreCurrentPosition);
					if(c == '-')
					{
						c = 'a';
					}
					else if(c == 'z')
					{
						c = '-';
					}
					else
					{
						c = (char)((int)c+1);
					}
					
					String start = highscoreName.substring(0, highscoreCurrentPosition);
					String end = highscoreName.substring(highscoreCurrentPosition+1, highscoreName.length());
					highscoreName = start + c + end;
				}
			}
			else if (keyStates == DOWN) 
			{
				if(highscoreCurrentPosition != 9)
				{
					char c = highscoreName.charAt(highscoreCurrentPosition);
					if(c == '-')
					{
						c = 'z';
					}
					else if(c == 'a')
					{
						c = '-';
					}
					else
					{
						c = (char)((int)c-1);
					}
					
					String start = highscoreName.substring(0, highscoreCurrentPosition);
					String end = highscoreName.substring(highscoreCurrentPosition+1, highscoreName.length());
					highscoreName = start + c + end;
				}
			}
			
			if (keyStates == FIRE) 
			{
				if(highscoreCurrentPosition == 9)
				{
					isLoading = true;
					
					highscoreName = highscoreName.replace('-', ' ');
					
					addScore(score, highscoreName);
					
					initScores();
					layer = 3;
					currentLevel = 1;
					coinsCollected = 0;
					score = 0;
				}
				else
				{
					highscoreCurrentPosition = 9;
				}
			}
		}
	}


	private String getText(String path)
	{
        //DataInputStream dis = new DataInputStream(getClass().getResourceAsStream(path));
        
        InputStreamReader is = new InputStreamReader(getClass().getResourceAsStream(path));
        char[] cbuff = new char[5000];
        try 
        {
			is.read(cbuff);
			is.close();
		}
        catch (IOException e1) 
        {
			e1.printStackTrace();
		}
        
        return new String(cbuff);
        /*
        StringBuffer strBuff = new StringBuffer();
        int ch = 0;
        String str = "";
        try 
        {
            while ((ch = dis.read()) != -1)
            {
            	System.out.println((char)ch);
            	//strBuff.append((char)ch);
            	str = str + (char)ch;
            }
            dis.close();
        } 
        catch (Exception e)
        {
            System.err.println("ERROR in getText() " + e);
            return null;
        }
        
        return str;//strBuff.toString();
        */
	}
	
	
	private TiledLayer loadLevel(int levelNumber)
	{
		double SMART_LEVEL = 0.3;
		int zomb_moveSpeed = 2;
		TIME = 500;
		
		Image walls_tile = null;
		try
		{
			walls_tile = Image.createImage("/sprite/wall_tile.png");
		}
		catch (IOException ioe)
		{
			System.out.println("NO WALL TILE ALARM!!!");
		}
		
		TiledLayer level = new TiledLayer(30, 40, walls_tile, 8, 8);
	    
		//*********LOADING WALLS MAP*********
		map = new int[1200];
		
		String lvlInfo = null;
		String lvl = null;
		String leftInfo = null;
		
		if(buildInMode == true)
		{
			lvlInfo = getText("/levels/level_"+levelNumber+".lvl");
		}
		else
		{
			lvlInfo = openCustomLevelAsText(levelNumber);
		}
		
		//System.out.println("Length="+lvlInfo.length());
		//System.out.println("lvl:\n"+lvlInfo);
		
		lvl = lvlInfo.substring(0, 1280);
		
		leftInfo = lvlInfo.substring(1280, lvlInfo.length());
		//System.out.println("leftInfo:\n"+leftInfo);
		//lvl = lvl.concat("!");
		
		String str = "";
		
		for(int j=0; j<lvl.length()-30; j++)
		{
			str = str.concat(lvl.substring(j, j+30));
			j += 31;
		}
		
		lvl = str;
		
		for(int i=0; i<lvl.length(); i++)
		{
			String s = lvl.substring(i, i+1);
			map[i] = Integer.parseInt(s);
		}
		
		
		if(leftInfo.indexOf("background=") != -1)
		{
			int index = leftInfo.indexOf("background=")+"background=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			try 
			{
				background = Image.createImage("/background/game"+Integer.toString(i)+".png");
			} 
			catch (IOException e) 
			{ System.out.println("ERROR WHILE BACKGROUND LOADING"); }
		}
		else
		{
			try 
			{
				background = Image.createImage("/background/game"+Integer.toString(new Random().nextInt(5)+1)+".png");
			} 
			catch (IOException e) 
			{ System.out.println("ERROR WHILE BACKGROUND LOADING"); }
		}
		
		
		if(leftInfo.indexOf("smartLevel=") != -1)
		{
			int index = leftInfo.indexOf("smartLevel=")+"smartLevel=".length();
			SMART_LEVEL = Double.parseDouble(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			
			if(difficulty > 1)
			{
				SMART_LEVEL = Math.min(1, SMART_LEVEL+0.1);
			}
			else if(difficulty == 0)
			{
				SMART_LEVEL = Math.max(0, SMART_LEVEL-0.1);
			}
		}
		
		if(leftInfo.indexOf("monstersSpeed=") != -1)
		{
			int index = leftInfo.indexOf("monstersSpeed=")+"monstersSpeed=".length();
			zomb_moveSpeed = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
		}
		
		if(leftInfo.indexOf("timeLimit=") != -1)
		{
			int index = leftInfo.indexOf("timeLimit=")+"timeLimit=".length();
			TIME = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
		}
		time = TIME;
		
		if(leftInfo.indexOf("gems=") != -1)
		{
			int index = leftInfo.indexOf("gems=")+"gems=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			gemsMaxAmount = i;
		}
		else
		{
			gemsMaxAmount = 5;
		}
		
		if(leftInfo.indexOf("coins=") != -1)
		{
			int index = leftInfo.indexOf("coins=")+"coins=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			coinsMaxAmount = i;
		}
		else
		{
			coinsMaxAmount = 20;
		}
		
		if(leftInfo.indexOf("bossLevel=") != -1)
		{
			int index = leftInfo.indexOf("bossLevel=")+"bossLevel=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			if(i == 1)
				bossLevel = true;
			else
				bossLevel = false;
		}
		else
		{
			bossLevel = false;
		}
		
		//System.out.println(SMART_LEVEL);
		
		if(leftInfo.indexOf("#AUTO-SPIKES#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#AUTO-SPIKES#")+"#AUTO-SPIKES#".length()+1) + "@@".length();
			String autoSpikess = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] sp = splitString(autoSpikess, "@");
			
			for(int i=0; i<sp.length; i++)
			{
				String[] spike = splitString(sp[i], ",");
				
				int r_x = Integer.parseInt(spike[0]);
				int r_y = Integer.parseInt(spike[1]);
				int r_open = Integer.parseInt(spike[2]);
				int r_close = Integer.parseInt(spike[3]);
				boolean r_state;
				if(spike[4].equalsIgnoreCase("false"))
					r_state = false;
				else
					r_state = true;
				int r_start = Integer.parseInt(spike[5]);
				
				spikes.addElement(new Spikes(r_x, r_y, true, r_open, r_close, r_state, r_start));
			}
		}
		
		
		Coordinates spikeSwitch[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#MANUAL-SPIKES#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#MANUAL-SPIKES#")+"#MANUAL-SPIKES#".length()+1) + "@@".length();
			String manualSpikess = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] fsp = splitString(manualSpikess, "@");
			boolean[] empty = new boolean[fsp.length];
			for(int i=0; i<fsp.length; i++)
			{
				if(fsp[i].length() < 3)
					empty[i] = true;
				else
				{
					empty[i] = false;
				}
			}
			spikeSwitch = new Coordinates[fsp.length][];
			
			for(int j=0; j<fsp.length; j++)
			{
				if(empty[j] == false)
				{
					String[] sp = splitString(fsp[j], ";");
					Coordinates swtch[] = new Coordinates[sp.length];

					for(int i=0; i<sp.length; i++)
					{
						String[] spike = splitString(sp[i], ",");

						int r_x = Integer.parseInt(spike[0]);
						int r_y = Integer.parseInt(spike[1]);
						boolean r_state;
						if(spike[2].equalsIgnoreCase("false"))
							r_state = false;
						else
							r_state = true;

						boolean add = true;
						for(int q=0;q<spikes.size(); q++)
						{
							if(((Spikes) spikes.elementAt(q)).getSprite().getX() == r_x && ((Spikes) spikes.elementAt(q)).getSprite().getY() == r_y)
							{
								add = false;
							}
						}
						
						if(add == true)
						{
							spikes.addElement(new Spikes(r_x, r_y, false, 0, 0, r_state, 0));
						}
						
						swtch[i] = new Coordinates(r_x, r_y);
					}

					spikeSwitch[j] = swtch;
				}
				else
				{
					spikeSwitch[j] = new Coordinates[0];
				}
			}
		}
		
		
		Coordinates wallSwitch[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#WALL-SWITCH#") != -1)
		{
			int tempB = leftInfo.indexOf("@@", leftInfo.indexOf("#WALL-SWITCH#")+"#WALL-SWITCH#".length()+1) + "@@".length();
			String wallSwtch = leftInfo.substring(tempB, leftInfo.indexOf("@@", tempB+1));
			
			String[] fsp = splitString(wallSwtch, "@");
			boolean[] empty = new boolean[fsp.length];
			for(int i=0; i<fsp.length; i++)
			{
				if(fsp[i].length() < 3)
					empty[i] = true;
				else
				{
					empty[i] = false;
				}
			}
			wallSwitch = new Coordinates[fsp.length][];
			
			for(int j=0; j<fsp.length; j++)
			{
				if(empty[j] == false)
				{
					String[] wsw = splitString(fsp[j], ";");
					Coordinates swtch[] = new Coordinates[wsw.length];

					for(int i=0; i<wsw.length; i++)
					{
						String[] wall = splitString(wsw[i], ",");

						int r_x = Integer.parseInt(wall[0]);
						int r_y = Integer.parseInt(wall[1]);

						swtch[i] = new Coordinates(r_x, r_y);
					}

					wallSwitch[j] = swtch;
				}
				else
				{
					wallSwitch[j] = new Coordinates[0];
				}
			}
		}
		
		if(leftInfo.indexOf("#MANUAL-SWITCH#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#MANUAL-SWITCH#")+"#MANUAL-SWITCH#".length()+1) + "@@".length();
			String manualSwitchess = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] sp = splitString(manualSwitchess, "@");
			
			for(int i=0; i<sp.length; i++)
			{
				String[] swtch = splitString(sp[i], ",");
				
				int r_x = Integer.parseInt(swtch[0]);
				int r_y = Integer.parseInt(swtch[1]);
				boolean r_state;
				if(swtch[2].equalsIgnoreCase("false"))
					r_state = false;
				else
					r_state = true;
				
				Vector childs = new Vector();
				
				if(spikeSwitch.length > i)
				{
					for(int j=0; j<spikeSwitch[i].length; j++)
					{
						childs.addElement(spikeSwitch[i][j]);
					}
				}
				
				if(wallSwitch.length > i)
				{
					for(int j=0; j<wallSwitch[i].length; j++)
					{
						childs.addElement(wallSwitch[i][j]);
					}
				}
				
				levers.addElement(new Lever(r_x, r_y, childs, r_state));
			}
		}
		
		
		
		Coordinates spikePlate[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#PLATE-SPIKES#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#PLATE-SPIKES#")+"#PLATE-SPIKES#".length()+1) + "@@".length();
			String plateSpikes = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] fsp = splitString(plateSpikes, "@");
			boolean[] empty = new boolean[fsp.length];
			for(int i=0; i<fsp.length; i++)
			{
				if(fsp[i].length() < 3)
					empty[i] = true;
				else
				{
					empty[i] = false;
				}
			}
			
			spikePlate = new Coordinates[fsp.length][];

			for(int j=0; j<fsp.length; j++)
			{
				if(empty[j] == false)
				{
					String[] sp = splitString(fsp[j], ";");
					Coordinates plate[] = new Coordinates[sp.length];

					for(int i=0; i<sp.length; i++)
					{
						String[] spike = splitString(sp[i], ",");

						int r_x = Integer.parseInt(spike[0]);
						int r_y = Integer.parseInt(spike[1]);
						boolean r_state;
						if(spike[2].equalsIgnoreCase("false"))
							r_state = false;
						else
							r_state = true;


						boolean newSpike = true;
						for(int k=0; k<spikes.size(); k++)
						{
							if(((Spikes) spikes.elementAt(k)).getSprite().getX() == r_x && ((Spikes) spikes.elementAt(k)).getSprite().getY() == r_y)
								newSpike = false;
						}

						if(newSpike == true)
						{
							spikes.addElement(new Spikes(r_x, r_y, false, 0, 0, r_state, 0));
						}

						plate[i] = new Coordinates(r_x, r_y);
					}

					spikePlate[j] = plate;
				}
				else
				{
					spikePlate[j] = new Coordinates[0];
				}
			}
		}
		
		
		Coordinates wallPlate[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#WALL-PLATE#") != -1)
		{
			int tempB = leftInfo.indexOf("@@", leftInfo.indexOf("#WALL-PLATE#")+"#WALL-PLATE#".length()+1) + "@@".length();
			String wallPlt = leftInfo.substring(tempB, leftInfo.indexOf("@@", tempB+1));
			
			String[] fsp = splitString(wallPlt, "@");
			boolean[] empty = new boolean[fsp.length];
			for(int i=0; i<fsp.length; i++)
			{
				if(fsp[i].length() < 3)
					empty[i] = true;
				else
				{
					empty[i] = false;
				}
			}
			wallPlate = new Coordinates[fsp.length][];

			for(int j=0; j<fsp.length; j++)
			{
				if(empty[j] == false)
				{
					String[] wpl = splitString(fsp[j], ";");
					Coordinates plate[] = new Coordinates[wpl.length];

					for(int i=0; i<wpl.length; i++)
					{
						String[] wall = splitString(wpl[i], ",");

						int r_x = Integer.parseInt(wall[0]);
						int r_y = Integer.parseInt(wall[1]);

						plate[i] = new Coordinates(r_x, r_y);
					}

					wallPlate[j] = plate;
				}
				else
				{
					wallPlate[j] = new Coordinates[0];
				}
			}
		}
		
		if(leftInfo.indexOf("#PREASURE-PLATE#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#PREASURE-PLATE#")+"#PREASURE-PLATE#".length()+1) + "@@".length();
			String preasurePlates = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] sp = splitString(preasurePlates, "@");
			
			for(int i=0; i<sp.length; i++)
			{
				String[] plate = splitString(sp[i], ",");
				
				int r_x = Integer.parseInt(plate[0]);
				int r_y = Integer.parseInt(plate[1]);
				
				Vector childs = new Vector();
				
				if(spikePlate.length > i)
				{
					for(int j=0; j<spikePlate[i].length; j++)
					{
						childs.addElement(spikePlate[i][j]);
					}
				}
				
				if(wallPlate.length > i)
				{
					for(int j=0; j<wallPlate[i].length; j++)
					{
						childs.addElement(wallPlate[i][j]);
					}
				}
				
				plates.addElement(new Plate(r_x, r_y, childs));
			}
		}
		
		
		//***********************************
		
		//*********LOADING TREASURES AND HEARTS MAP*********
		/*
		upsMap = new int[300];

		String ups = getText("/levels/level_"+levelNumber+".ups");
		ups = ups.concat("!");

		String upsStr = "";

		for(int j=0; j<ups.length()-14; j++)
		{	
			upsStr = upsStr.concat(ups.substring(j, j+14));
			j += 15;
		}

		ups = upsStr;
		
		for(int i=0; i<ups.length(); i++)
		{
			String s = ups.substring(i, i+1);
			upsMap[i] = Integer.parseInt(s);
		}
		*/
		//**************************************************
		
	    Random rnd = new Random();
	    
	    for (int i = 0; i < map.length; i++)
	    {
			int column = i % 30;
			int row = (i - column) / 30;
			
			if(map[i] == 1)
				level.setCell(column, row, rnd.nextInt(4)+1);
			
			if(map[i] == 7)
				exit.setPosition(column*8, row*8);
			
			if(map[i] == 3)
				player.setPosition(column*8, row*8);
			
			if(map[i] == 9)
				monsters.addElement(new Monster(column*8, row*8, SMART_LEVEL, zomb_moveSpeed));
	    }
	    
	    gemsAmount = gemsMaxAmount;
		coinsAmount = coinsMaxAmount;
	    
		//System.out.println("gemsAmount="+gemsAmount);
		//System.out.println("coinsAmount="+coinsAmount);
		
	    //**************SETTING UP TREASURES******************
	    int totalCoins = 0;
	    int totalGems = 0;
	    for(int i=0; i<map.length; i++)
	    {
	    	if(map[i] == 2)
	    		totalCoins++;
	    	if(map[i] == 5)
	    		totalGems++;
	    }
	    
	    while(coinsAmount + gemsAmount > 0)
	    {
		    for (int i = 0; i < map.length; i++)
		    {  	
		    	if(map[i] == 2)
		    	{
		    		if(coinsAmount > 0)
		    		{
		    			if(rnd.nextInt(totalCoins) == 0)
		    			{
							int column = i % 30;
							int row = (i - column) / 30;
							boolean allow = true;
							
							for(int j=0; j<pickups.size(); j++)
							{
								if(((Pickup)pickups.elementAt(j)).getSprite().getX() == column*8 && ((Pickup)pickups.elementAt(j)).getSprite().getY() == row*8)
								{
									allow = false;
									break;
								}
							}
							
							if(allow == true)
							{
								pickups.addElement(new Coin(column*8, row*8));
								coinsAmount--;
							}
		    			}
		    		}
		    	}
		    	if(map[i] == 5)
		    	{
		    		if(gemsAmount > 0)
		    		{
		    			if(rnd.nextInt(totalGems) == 0)
		    			{
							int column = i % 30;
							int row = (i - column) / 30;
							boolean allow = true;

							for(int j=0; j<pickups.size(); j++)
							{
								if(((Pickup)pickups.elementAt(j)).getSprite().getX() == column*8 && ((Pickup)pickups.elementAt(j)).getSprite().getY() == row*8)
								{
									allow = false;
									break;
								}
							}
							
							if(allow == true)
							{
								pickups.addElement(new Gem(column*8, row*8));
								gemsAmount--;
							}
		    			}
		    		}
		    	}
		    }
	    }
	    
	    return level;
		
	}
	
	private TiledLayer loadLevel(String lvlInfo)
	{
		double SMART_LEVEL = 0.3;
		int zomb_moveSpeed = 2;
		TIME = 500;
		
		Image walls_tile = null;
		try
		{
			walls_tile = Image.createImage("/sprite/wall_tile.png");
		}
		catch (IOException ioe)
		{
			System.out.println("NO WALL TILE ALARM!!!");
		}
		
		TiledLayer level = new TiledLayer(30, 40, walls_tile, 8, 8);
	    
		//*********LOADING WALLS MAP*********
		map = new int[1200];
	
		String lvl = null;
		String leftInfo = null;
		
		//System.out.println("Length="+lvlInfo.length());
		//System.out.println("lvl:\n"+lvlInfo);
		
		lvl = lvlInfo.substring(0, 1280);
		
		leftInfo = lvlInfo.substring(1280, lvlInfo.length());
		//System.out.println("leftInfo:\n"+leftInfo);
		//lvl = lvl.concat("!");
		
		String str = "";
		
		for(int j=0; j<lvl.length()-30; j++)
		{
			str = str.concat(lvl.substring(j, j+30));
			j += 31;
		}
		
		lvl = str;
		
		for(int i=0; i<lvl.length(); i++)
		{
			String s = lvl.substring(i, i+1);
			map[i] = Integer.parseInt(s);
		}
		
		
		if(leftInfo.indexOf("background=") != -1)
		{
			int index = leftInfo.indexOf("background=")+"background=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			try 
			{
				background = Image.createImage("/background/game"+Integer.toString(i)+".png");
			} 
			catch (IOException e) 
			{ System.out.println("ERROR WHILE BACKGROUND LOADING"); }
		}
		else
		{
			try 
			{
				background = Image.createImage("/background/game"+Integer.toString(new Random().nextInt(5)+1)+".png");
			} 
			catch (IOException e) 
			{ System.out.println("ERROR WHILE BACKGROUND LOADING"); }
		}
		
		
		if(leftInfo.indexOf("smartLevel=") != -1)
		{
			int index = leftInfo.indexOf("smartLevel=")+"smartLevel=".length();
			SMART_LEVEL = Double.parseDouble(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			
			if(difficulty > 1)
			{
				SMART_LEVEL = Math.min(1, SMART_LEVEL+0.1);
			}
			else if(difficulty == 0)
			{
				SMART_LEVEL = Math.max(0, SMART_LEVEL-0.1);
			}
		}
		
		if(leftInfo.indexOf("monstersSpeed=") != -1)
		{
			int index = leftInfo.indexOf("monstersSpeed=")+"monstersSpeed=".length();
			zomb_moveSpeed = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
		}
		
		if(leftInfo.indexOf("timeLimit=") != -1)
		{
			int index = leftInfo.indexOf("timeLimit=")+"timeLimit=".length();
			TIME = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
		}
		time = TIME;
		
		if(leftInfo.indexOf("gems=") != -1)
		{
			int index = leftInfo.indexOf("gems=")+"gems=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			gemsMaxAmount = i;
		}
		else
		{
			gemsMaxAmount = 5;
		}
		
		if(leftInfo.indexOf("coins=") != -1)
		{
			int index = leftInfo.indexOf("coins=")+"coins=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			coinsMaxAmount = i;
		}
		else
		{
			coinsMaxAmount = 20;
		}
		
		//System.out.println(SMART_LEVEL);
		
		if(leftInfo.indexOf("#AUTO-SPIKES#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#AUTO-SPIKES#")+"#AUTO-SPIKES#".length()+1) + "@@".length();
			String autoSpikess = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] sp = splitString(autoSpikess, "@");
			
			for(int i=0; i<sp.length; i++)
			{
				String[] spike = splitString(sp[i], ",");
				
				int r_x = Integer.parseInt(spike[0]);
				int r_y = Integer.parseInt(spike[1]);
				int r_open = Integer.parseInt(spike[2]);
				int r_close = Integer.parseInt(spike[3]);
				boolean r_state;
				if(spike[4].equalsIgnoreCase("false"))
					r_state = false;
				else
					r_state = true;
				int r_start = Integer.parseInt(spike[5]);
				
				spikes.addElement(new Spikes(r_x, r_y, true, r_open, r_close, r_state, r_start));
			}
		}
		
		
		Coordinates spikeSwitch[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#MANUAL-SPIKES#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#MANUAL-SPIKES#")+"#MANUAL-SPIKES#".length()+1) + "@@".length();
			String manualSpikess = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] fsp = splitString(manualSpikess, "@");
			boolean[] empty = new boolean[fsp.length];
			for(int i=0; i<fsp.length; i++)
			{
				if(fsp[i].length() < 3)
					empty[i] = true;
				else
				{
					empty[i] = false;
				}
			}
			spikeSwitch = new Coordinates[fsp.length][];
			
			for(int j=0; j<fsp.length; j++)
			{
				if(empty[j] == false)
				{
					String[] sp = splitString(fsp[j], ";");
					Coordinates swtch[] = new Coordinates[sp.length];

					for(int i=0; i<sp.length; i++)
					{
						String[] spike = splitString(sp[i], ",");

						int r_x = Integer.parseInt(spike[0]);
						int r_y = Integer.parseInt(spike[1]);
						boolean r_state;
						if(spike[2].equalsIgnoreCase("false"))
							r_state = false;
						else
							r_state = true;

						boolean add = true;
						for(int q=0;q<spikes.size(); q++)
						{
							if(((Spikes) spikes.elementAt(q)).getSprite().getX() == r_x && ((Spikes) spikes.elementAt(q)).getSprite().getY() == r_y)
							{
								add = false;
							}
						}
						
						if(add == true)
						{
							spikes.addElement(new Spikes(r_x, r_y, false, 0, 0, r_state, 0));
						}
						
						swtch[i] = new Coordinates(r_x, r_y);
					}

					spikeSwitch[j] = swtch;
				}
				else
				{
					spikeSwitch[j] = new Coordinates[0];
				}
			}
		}
		
		
		Coordinates wallSwitch[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#WALL-SWITCH#") != -1)
		{
			int tempB = leftInfo.indexOf("@@", leftInfo.indexOf("#WALL-SWITCH#")+"#WALL-SWITCH#".length()+1) + "@@".length();
			String wallSwtch = leftInfo.substring(tempB, leftInfo.indexOf("@@", tempB+1));
			
			String[] fsp = splitString(wallSwtch, "@");
			boolean[] empty = new boolean[fsp.length];
			for(int i=0; i<fsp.length; i++)
			{
				if(fsp[i].length() < 3)
					empty[i] = true;
				else
				{
					empty[i] = false;
				}
			}
			wallSwitch = new Coordinates[fsp.length][];
			
			for(int j=0; j<fsp.length; j++)
			{
				if(empty[j] == false)
				{
					String[] wsw = splitString(fsp[j], ";");
					Coordinates swtch[] = new Coordinates[wsw.length];

					for(int i=0; i<wsw.length; i++)
					{
						String[] wall = splitString(wsw[i], ",");

						int r_x = Integer.parseInt(wall[0]);
						int r_y = Integer.parseInt(wall[1]);

						swtch[i] = new Coordinates(r_x, r_y);
					}

					wallSwitch[j] = swtch;
				}
				else
				{
					wallSwitch[j] = new Coordinates[0];
				}
			}
		}
		
		if(leftInfo.indexOf("#MANUAL-SWITCH#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#MANUAL-SWITCH#")+"#MANUAL-SWITCH#".length()+1) + "@@".length();
			String manualSwitchess = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] sp = splitString(manualSwitchess, "@");
			
			for(int i=0; i<sp.length; i++)
			{
				String[] swtch = splitString(sp[i], ",");
				
				int r_x = Integer.parseInt(swtch[0]);
				int r_y = Integer.parseInt(swtch[1]);
				boolean r_state;
				if(swtch[2].equalsIgnoreCase("false"))
					r_state = false;
				else
					r_state = true;
				
				Vector childs = new Vector();
				
				if(spikeSwitch.length > i)
				{
					for(int j=0; j<spikeSwitch[i].length; j++)
					{
						childs.addElement(spikeSwitch[i][j]);
					}
				}
				
				if(wallSwitch.length > i)
				{
					for(int j=0; j<wallSwitch[i].length; j++)
					{
						childs.addElement(wallSwitch[i][j]);
					}
				}
				
				levers.addElement(new Lever(r_x, r_y, childs, r_state));
			}
		}
		
		
		
		Coordinates spikePlate[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#PLATE-SPIKES#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#PLATE-SPIKES#")+"#PLATE-SPIKES#".length()+1) + "@@".length();
			String plateSpikes = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] fsp = splitString(plateSpikes, "@");
			boolean[] empty = new boolean[fsp.length];
			for(int i=0; i<fsp.length; i++)
			{
				if(fsp[i].length() < 3)
					empty[i] = true;
				else
				{
					empty[i] = false;
				}
			}
			
			spikePlate = new Coordinates[fsp.length][];

			for(int j=0; j<fsp.length; j++)
			{
				if(empty[j] == false)
				{
					String[] sp = splitString(fsp[j], ";");
					Coordinates plate[] = new Coordinates[sp.length];

					for(int i=0; i<sp.length; i++)
					{
						String[] spike = splitString(sp[i], ",");

						int r_x = Integer.parseInt(spike[0]);
						int r_y = Integer.parseInt(spike[1]);
						boolean r_state;
						if(spike[2].equalsIgnoreCase("false"))
							r_state = false;
						else
							r_state = true;


						boolean newSpike = true;
						for(int k=0; k<spikes.size(); k++)
						{
							if(((Spikes) spikes.elementAt(k)).getSprite().getX() == r_x && ((Spikes) spikes.elementAt(k)).getSprite().getY() == r_y)
								newSpike = false;
						}

						if(newSpike == true)
						{
							spikes.addElement(new Spikes(r_x, r_y, false, 0, 0, r_state, 0));
						}

						plate[i] = new Coordinates(r_x, r_y);
					}

					spikePlate[j] = plate;
				}
				else
				{
					spikePlate[j] = new Coordinates[0];
				}
			}
		}
		
		
		Coordinates wallPlate[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#WALL-PLATE#") != -1)
		{
			int tempB = leftInfo.indexOf("@@", leftInfo.indexOf("#WALL-PLATE#")+"#WALL-PLATE#".length()+1) + "@@".length();
			String wallPlt = leftInfo.substring(tempB, leftInfo.indexOf("@@", tempB+1));
			
			String[] fsp = splitString(wallPlt, "@");
			boolean[] empty = new boolean[fsp.length];
			for(int i=0; i<fsp.length; i++)
			{
				if(fsp[i].length() < 3)
					empty[i] = true;
				else
				{
					empty[i] = false;
				}
			}
			wallPlate = new Coordinates[fsp.length][];

			for(int j=0; j<fsp.length; j++)
			{
				if(empty[j] == false)
				{
					String[] wpl = splitString(fsp[j], ";");
					Coordinates plate[] = new Coordinates[wpl.length];

					for(int i=0; i<wpl.length; i++)
					{
						String[] wall = splitString(wpl[i], ",");

						int r_x = Integer.parseInt(wall[0]);
						int r_y = Integer.parseInt(wall[1]);

						plate[i] = new Coordinates(r_x, r_y);
					}

					wallPlate[j] = plate;
				}
				else
				{
					wallPlate[j] = new Coordinates[0];
				}
			}
		}
		
		if(leftInfo.indexOf("#PREASURE-PLATE#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#PREASURE-PLATE#")+"#PREASURE-PLATE#".length()+1) + "@@".length();
			String preasurePlates = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] sp = splitString(preasurePlates, "@");
			
			for(int i=0; i<sp.length; i++)
			{
				String[] plate = splitString(sp[i], ",");
				
				int r_x = Integer.parseInt(plate[0]);
				int r_y = Integer.parseInt(plate[1]);
				
				Vector childs = new Vector();
				
				if(spikePlate.length > i)
				{
					for(int j=0; j<spikePlate[i].length; j++)
					{
						childs.addElement(spikePlate[i][j]);
					}
				}
				
				if(wallPlate.length > i)
				{
					for(int j=0; j<wallPlate[i].length; j++)
					{
						childs.addElement(wallPlate[i][j]);
					}
				}
				
				plates.addElement(new Plate(r_x, r_y, childs));
			}
		}
		
		
		//***********************************
		
		//*********LOADING TREASURES AND HEARTS MAP*********
		/*
		upsMap = new int[300];

		String ups = getText("/levels/level_"+levelNumber+".ups");
		ups = ups.concat("!");

		String upsStr = "";

		for(int j=0; j<ups.length()-14; j++)
		{	
			upsStr = upsStr.concat(ups.substring(j, j+14));
			j += 15;
		}

		ups = upsStr;
		
		for(int i=0; i<ups.length(); i++)
		{
			String s = ups.substring(i, i+1);
			upsMap[i] = Integer.parseInt(s);
		}
		*/
		//**************************************************
		
	    Random rnd = new Random();
	    
	    for (int i = 0; i < map.length; i++)
	    {
			int column = i % 30;
			int row = (i - column) / 30;
			
			if(map[i] == 1)
				level.setCell(column, row, rnd.nextInt(4)+1);
			
			if(map[i] == 7)
				exit.setPosition(column*8, row*8);
			
			if(map[i] == 3)
				player.setPosition(column*8, row*8);
			
			if(map[i] == 9)
				monsters.addElement(new Monster(column*8, row*8, SMART_LEVEL, zomb_moveSpeed));
	    }
	    
	    gemsAmount = gemsMaxAmount;
		coinsAmount = coinsMaxAmount;
	    
	    //**************SETTING UP TREASURES******************
	    int totalCoins = 0;
	    int totalGems = 0;
	    for(int i=0; i<map.length; i++)
	    {
	    	if(map[i] == 2)
	    		totalCoins++;
	    	if(map[i] == 5)
	    		totalGems++;
	    }
	    
	    while(coinsAmount + gemsAmount > 0)
	    {
		    for (int i = 0; i < map.length; i++)
		    {  	
		    	if(map[i] == 2)
		    	{
		    		if(coinsAmount > 0)
		    		{
		    			if(rnd.nextInt(totalCoins) == 0)
		    			{
							int column = i % 30;
							int row = (i - column) / 30;
							boolean allow = true;
							
							for(int j=0; j<pickups.size(); j++)
							{
								if(((Pickup)pickups.elementAt(j)).getSprite().getX() == column*8 && ((Pickup)pickups.elementAt(j)).getSprite().getY() == row*8)
								{
									allow = false;
									break;
								}
							}
							
							if(allow == true)
							{
								pickups.addElement(new Coin(column*8, row*8));
								coinsAmount--;
							}
		    			}
		    		}
		    	}
		    	if(map[i] == 5)
		    	{
		    		if(gemsAmount > 0)
		    		{
		    			if(rnd.nextInt(totalGems) == 0)
		    			{
							int column = i % 30;
							int row = (i - column) / 30;
							boolean allow = true;

							for(int j=0; j<pickups.size(); j++)
							{
								if(((Pickup)pickups.elementAt(j)).getSprite().getX() == column*8 && ((Pickup)pickups.elementAt(j)).getSprite().getY() == row*8)
								{
									allow = false;
									break;
								}
							}
							
							if(allow == true)
							{
								pickups.addElement(new Gem(column*8, row*8));
								gemsAmount--;
							}
		    			}
		    		}
		    	}
		    }
	    }
	    
	    return level;
		
	}
	
	public void test(String level, EditorCanvas editorCanvas)
	{
		testMode = true;
		this.editorCanvas = editorCanvas;
		resetGame();
		init();	
		walls = loadLevel(level);
		flame = new Flame(player.getSprite().getX(),player.getSprite().getY());
		
	}
	
	public String[] splitString(String str, String splitter)
	{
		Vector lines = new Vector();
		
		int index_1 = str.indexOf(splitter);
		int index_2 = -1;
		
		if(index_1 != -1)
		{
			lines.addElement(str.substring(0, index_1));
			
			index_1 += splitter.length();
			index_2 = str.indexOf(splitter, index_1+1);
					
			while(index_2 != -1)
			{
				lines.addElement(str.substring(index_1, index_2));
				
				index_1 = index_2+splitter.length();
				index_2 = str.indexOf(splitter, index_1+1);
			}
			
			lines.addElement(str.substring(index_1, str.length()));
		}
		else
		{
			lines.addElement(str);
		}
		
		String[] result = new String[lines.size()];
		for(int i=0; i<lines.size(); i++)
		{
			result[i] = ((String) lines.elementAt(i));
		}
		
		return result;
	}

	/*
	private TiledLayer setUpTreasures()
	{
			Image treasure_tile = null;
			try
			{
				treasure_tile = Image.createImage("/sprite/treasure_tile.png");
			}
			catch (IOException ioe)
			{
				
			}
			
			TiledLayer treasure = new TiledLayer(14, 20, treasure_tile, 16, 16);
			
			for(int i=1; i<25; i++)
				treasure.createAnimatedTile(i);
		    
			int[] map = new int[300];
			for(int i=0; i<map.length; i++)
			{
				if(upsMap[i] == 1)
					map[i] = -1;
				
				else if(upsMap[i] == 2)
					map[i] = -10;
				
				else if(upsMap[i] == 3)
					map[i] = -17;
				
				else map[i] = upsMap[i];
			}
		    
		    Random rnd = new Random();
		    while(coinsAmount + gemsAmount > 0)
		    {
			    for (int i = 0; i < map.length; i++)
			    {  	
			    	if(map[i] == -1)
			    	{
			    		if(coinsAmount > 0)
			    		{
			    			if(rnd.nextInt(2) == 1)
			    			{
								int column = i % 14;
								int row = (i - column) / 14;
								if(treasure.getCell(column, row) == 0)
								{
									treasure.setCell(column, row, map[i]);
									coinsAmount--;
								}
								treasure.setCell(column,row,map[i]);
			    			}
			    		}
			    	}
			    	if(map[i] == -10)
			    	{
			    		if(gemsAmount > 0)
			    		{
			    			if(rnd.nextInt(2) == 1)
			    			{
								int column = i % 14;
								int row = (i - column) / 14;
								if(treasure.getCell(column, row) == 0)
								{
									treasure.setCell(column, row, map[i]);
									gemsAmount--;
								}
			    			}
			    		}
			    	}
			    }
		    }
		    
		    treasure.move(8, 8);
		    
		    return treasure;
			
	}
	*/
	
	private void tryCreatePickup(double chance)
	{
		if(chance <= 0)
			chance = 0.01;
		if(chance > 1 && chance <= 100)
			chance /= 100;
		else if(chance > 100)
			chance = 1;
		
		int totalPlaces = 0;
		for(int i=0; i<map.length; i++)
		{
			if(map[i] == 6)
				totalPlaces++;
		}
		
		Random rnd = new Random();
		double value = rnd.nextDouble();
		
		double tryChance = rnd.nextDouble(); 
		//System.out.println("tryChance="+tryChance+"   chance="+chance+"   result="+(tryChance > chance));
		
		if(tryChance < chance)
		{
			for (int i = 0; i < map.length; i++)
		    {
				if(map[i] == 6 && rnd.nextInt(totalPlaces) == 0)
    			{
					int column = i % 30;
					int row = (i - column) / 30;
					
					boolean allow = true;
					
					for(int j=0; j<pickups.size(); j++)
					{
						if(((Pickup)pickups.elementAt(j)).getSprite().getX() == column*8 && ((Pickup)pickups.elementAt(j)).getSprite().getY() == row*8)
						{
							allow = false;
							break;
						}
					}
					
					if(allow == true)
					{
						/* Shell = 9;
						 * Dart = 8;
						 * Mine = 8;
						 * Heart = 8;
						 * Shield = 3;
						 * Speed = 4;
						 * Charge = 3;
						 */
						
						float[] weights = { 9, 8, 8, 8, 3, 4, 3 };
						int summ = 0;
						for(int w=0; w<weights.length; w++)
							summ += weights[w];
						for(int w=0; w<weights.length; w++)
							weights[w] /= summ;
						
						float carret = 0;
						
						for(int w=0; w<weights.length; w++)
						{
							carret += weights[w];
							if(value <= carret)
							{
								switch(w)
								{
									case 0:
										pickups.addElement(new ShellPickup(column*8, row*8));
										break;
										
									case 1:
										pickups.addElement(new DartPickup(column*8, row*8));
										break;
										
									case 2:
										pickups.addElement(new MinePickup(column*8, row*8));
										break;
										
									case 3:
										pickups.addElement(new Heart(column*8, row*8));
										break;
										
									case 4:
										pickups.addElement(new ShieldPickup(column*8, row*8));
										break;
										
									case 5:
										pickups.addElement(new SpeedPickup(column*8, row*8));
										break;
										
									case 6:
										pickups.addElement(new ChargePickup(column*8, row*8));
										break;
								}
								
								currentPickupTimeout = PICKUP_TIMEOUT;
								break;
							}
						}
						
						break;
					}
    			}
		    }
		}
		
		//return false;
	}
	
	private void checkCollision()
	{	
		if(bossLevel == true)
		{
			for(int j = mines.size()-1; j>=0; j--)
			{
				if(boss.checkCollision(((Mine) mines.elementAt(j)).getSprite()) == true)
				{
					if(((Mine) mines.elementAt(j)).getCharged() == true)
					{
						int mineX = ((Mine) mines.elementAt(j)).getSprite().getX();
						int mineY = ((Mine) mines.elementAt(j)).getSprite().getY();
						
						mines.removeElementAt(j);
						explosions.addElement(new Explosion(mineX, mineY));
						detonate(mineX, mineY);
						break;
					}
				}
			}
			
			for(int i=darts.size()-1; i>=0; i--)
			{
				if(boss.checkCollision(((Dart) darts.elementAt(i)).getSprite()) == true)
				{
					boss.setWTF(20);
					int dScore = (int) (10*mult);
					scoreUps.addElement(new ScoreUp(dScore, ((Dart) darts.elementAt(i)).getSprite().getX(), ((Dart) darts.elementAt(i)).getSprite().getY()));
					darts.removeElementAt(i);
				}	
			}
		}
		
		for(int i=pickups.size()-1; i>=0; i--)
		{
			if(player.getSprite().collidesWith(((Pickup) pickups.elementAt(i)).getSprite(), false))
			{
				int dScore = 0;
				int scoreX = ((Pickup) pickups.elementAt(i)).getSprite().getX();
				int scoreY = ((Pickup) pickups.elementAt(i)).getSprite().getY();
				
				if(pickups.elementAt(i).getClass() == Coin.class)
				{
					dScore = 10;
					coinsAmount++;
					coinsCollected++;
					pickups.removeElementAt(i);
				}
				
				else if(pickups.elementAt(i).getClass() == Gem.class)
				{
					dScore = 50;
					gemsAmount++;
					pickups.removeElementAt(i);
				}
				
				else if(pickups.elementAt(i).getClass() == Heart.class)
				{
					dScore = 20;
					player.setLives(1);
					pickups.removeElementAt(i);
				}
				
				else if(pickups.elementAt(i).getClass() == SpeedPickup.class)
				{
					dScore = 25;
					player.speedUp(SPEED_UP_TIME);
					player.setSpeeded(true);
					pickups.removeElementAt(i);
				}
				
				else if(pickups.elementAt(i).getClass() == ShieldPickup.class)
				{
					dScore = 25;
					player.setProtection(SHIELD_TIME);
					player.setShielded(true);
					pickups.removeElementAt(i);
				}
				else if(pickups.elementAt(i).getClass() == ChargePickup.class)
				{
					dScore = 25;
					player.setCharged(1);
					player.setChargedTimeout(CHARGE_TIME);
					pickups.removeElementAt(i);
				}
				
				else if(pickups.elementAt(i).getClass() == MinePickup.class)
				{
					dScore = 5;
					player.setMines(1);
					pickups.removeElementAt(i);
				}
				
				else if(pickups.elementAt(i).getClass() == ShellPickup.class)
				{
					dScore = 5;
					player.setAmmo(1);
					pickups.removeElementAt(i);
				}
				
				else if(pickups.elementAt(i).getClass() == DartPickup.class)
				{
					dScore = 5;
					player.setDarts(1);
					pickups.removeElementAt(i);
				}
				
				dScore = (int) (dScore * mult);
				
				score += dScore;
				scoreUps.addElement(new ScoreUp(dScore, scoreX, scoreY));
			}
		}
		
		/*
		if(player.getSprite().collidesWith(treasure, false))
		{
			int col = (player.getSprite().getX()-8)/16;
			int row = (player.getSprite().getY()-8)/16;
			
			collect(col, row);
			treasure.setCell(col, row, 0);
			if((col+1) < 14)
			{	
				collect(col+1, row);
				treasure.setCell(col+1, row, 0);	
			}
			if((row+1) < 20)
			{
				collect(col, row+1);
				treasure.setCell(col, row+1, 0);
			}
			if((col+1) < 14 && (row+1) < 20)
			{
				collect(col+1, row+1);
				treasure.setCell(col+1, row+1, 0);
			}
		}
		*/
		
		if(player.getSprite().collidesWith(exit, false))
		{
			if(exit.getFrame() == 1 && gameOver == false)
				gameOver(true);
			exit.setFrame(0);
		}
		
		if(gameOver == false)
		{
			for(int i=monsters.size()-1; i>=0; i--)
			{
				if(player.getSprite().collidesWith(((Monster) monsters.elementAt(i)).getSprite(), false) == true)
				{
					if(player.getCharged() > 0 && ((Monster) monsters.elementAt(i)).getStunnedTimeout() == 0)
					{
						int mx = ((Monster) monsters.elementAt(i)).getSprite().getX();
						int my = ((Monster) monsters.elementAt(i)).getSprite().getY();
						player.setCharged(-1);
						int dScore = (int) (100 * mult);
						score += dScore;
						scoreUps.addElement(new ScoreUp(dScore, mx, my));
						bloods.addElement(new Blood(mx, my));
						
						for(int k=0; k<5; k++)
							flesh.addElement(new Flesh(mx, my));
						
						monsters.removeElementAt(i);
					}
					else if(player.isProtected() == false && ((Monster) monsters.elementAt(i)).getStunnedTimeout() == 0)
					{
						player.setLives(-1);
						player.setProtection(50);	
					}	
				}
			}
		}
		
		
		if(player.getSprite().collidesWith(walls, false) == true)
		{
			if(player.isProtected() == false && gameOver == false)
			{
				player.setLives(-1);
				
				if(player.getLives() == 0)
				{
					bloods.addElement(new Blood(player.getSprite().getX(), player.getSprite().getY()));
				}
				else
				{
					player.setProtection(50);
				}
			}
		}
		
		for(int i = monsters.size()-1; i>=0; i--)
		{
			if(((Monster) monsters.elementAt(i)).getSprite().collidesWith(walls, false) == true)
			{
				//int mX = ((Monster) monsters.elementAt(i)).getSprite().getX();
				//int mY = ((Monster) monsters.elementAt(i)).getSprite().getY();
				
				//bloods.addElement(new Blood(mX, mY));
				
				((Monster) monsters.elementAt(i)).setStunnedTimeout(50);
				((Monster) monsters.elementAt(i)).slowDown(1, 300);
				//monsters.removeElementAt(i);
			}
		}
		
		for(int i = monsters.size()-1; i>=0; i--)
		{
			for(int j = mines.size()-1; j>=0; j--)
			{
				if(((Monster) monsters.elementAt(i)).getSprite().collidesWith(((Mine) mines.elementAt(j)).getSprite(), false) == true)
				{
					if(((Mine) mines.elementAt(j)).getCharged() == true && ((Monster) monsters.elementAt(i)).getStunnedTimeout() == 0)
					{
						int mineX = ((Mine) mines.elementAt(j)).getSprite().getX();
						int mineY = ((Mine) mines.elementAt(j)).getSprite().getY();
						
						mines.removeElementAt(j);
						explosions.addElement(new Explosion(mineX, mineY));
						detonate(mineX, mineY);
						break;
					}
				}
			}
			
		}
		
		for(int j = mines.size()-1; j>=0; j--)
		{
			if(player.getSprite().collidesWith(((Mine) mines.elementAt(j)).getSprite(), false) == true && gameOver == false)
			{
				if(((Mine) mines.elementAt(j)).getCharged() == true)
				{
					int mineX = ((Mine) mines.elementAt(j)).getSprite().getX();
					int mineY = ((Mine) mines.elementAt(j)).getSprite().getY();
					
					mines.removeElementAt(j);
					explosions.addElement(new Explosion(mineX, mineY));
					detonate(mineX, mineY);
					break;
				}
			}
		}
		
		for(int j = 0; j < spikes.size(); j++)
		{
			if(player.getSprite().collidesWith(((Spikes) spikes.elementAt(j)).getSprite(), false) == true && ((Spikes) spikes.elementAt(j)).isOpened() == true && player.isProtected() == false && gameOver == false)
			{
				player.setLives(-1);
				player.setProtection(50);
				//flesh.addElement(new Flesh(player.getSprite().getX(), player.getSprite().getY()));
			}
			
			for(int i = monsters.size()-1; i>=0; i--)
			{
				if(((Monster) monsters.elementAt(i)).getSprite().collidesWith(((Spikes) spikes.elementAt(j)).getSprite(), false) == true && ((Spikes) spikes.elementAt(j)).isOpened() == true)
				{
					((Monster) monsters.elementAt(i)).slowDown(1, 10);
				}
				
			}
		}
		
		for(int j = 0; j < plates.size(); j++)
		{
			if(player.getSprite().collidesWith(((Plate) plates.elementAt(j)).getSprite(), false) == true)
			{
				((Plate) plates.elementAt(j)).activate(walls, spikes);
			}
			
			for(int i = monsters.size()-1; i>=0; i--)
			{
				if(((Monster) monsters.elementAt(i)).getSprite().collidesWith(((Plate) plates.elementAt(j)).getSprite(), false) == true)
				{
					((Plate) plates.elementAt(j)).activate(walls, spikes);
				}
			}
		}
		
		for(int i = 0; i<explosions.size(); i++)
		{
			for(int j = mines.size()-1; j>=0; j--)
			{
				if(((Explosion) explosions.elementAt(i)).getSprite().collidesWith(((Mine) mines.elementAt(j)).getSprite(), false) == true)
				{
					int mineX = ((Mine) mines.elementAt(j)).getSprite().getX();
					int mineY = ((Mine) mines.elementAt(j)).getSprite().getY();
						
					mines.removeElementAt(j);
					explosions.addElement(new Explosion(mineX, mineY));
					detonate(mineX, mineY);
				}
			}
		}
	}
	
	private void detonate(int blowX, int blowY)
	{
		if(bossLevel == true)
		{
			if(boss.getProtection() == 0)
			{
				int dx = boss.getX() - blowX;
				int dy = boss.getY() - blowY;
				
				double dis = Math.sqrt(dx*dx+dy*dy);
				
				if(dis <= DETONATE_RADIUS)
				{
					boss.hurt(2);
					
					boss.setProtection(75);
					boss.setWTF(20);
					
					int dScore = (int) (75*mult);
					scoreUps.addElement(new ScoreUp(dScore, blowX, blowY));
				}
			}
		}
		
		for(int i = monsters.size()-1; i>=0; i--)
		{
			Monster m = ((Monster) monsters.elementAt(i));
			int mX = m.getSprite().getX();
			int mY = m.getSprite().getY();
			int dx = mX - blowX;
			int dy = mY - blowY;
			
			boolean directContact = false;
			for(int e=0; e<explosions.size(); e++)
			{
				if(m.getSprite().collidesWith(((Explosion) explosions.elementAt(e)).getSprite(), false) == true)
				{
					bloods.addElement(new Blood(mX, mY));

					for(int k=0; k<5; k++)
						flesh.addElement(new Flesh(mX, mY));

					int dScore = (int) (150 * mult);
					scoreUps.addElement(new ScoreUp(dScore, mX, mY));
					score += dScore;
					monsters.removeElementAt(i);
					directContact = true;
					break;
				}
			}

			if(directContact == true)
			{
				continue;
			}
			else
			{
				if(dy < DETONATE_RADIUS && dy > -DETONATE_RADIUS && dx > -10 && dx < 10)
				{
					boolean succsess = true;
					int x = blowX+8;
					
					if(dy < 0)
					{
						for(int j = blowY; j>m.getSprite().getY(); j--)
						{
							int row = j/8;
							int col = x/8;
	
							if(walls.getCell(col,row) != 0)
								succsess = false;
						}
					}
					else
					{
						for(int j = blowY; j<m.getSprite().getY(); j++)
						{
							int row = j/8;
							int col = x/8;

							if(walls.getCell(col,row) != 0)
								succsess = false;
						}
					}

					if(succsess)
					{
						bloods.addElement(new Blood(mX, mY));

						for(int k=0; k<5; k++)
							flesh.addElement(new Flesh(mX, mY));

						int dScore = (int) (150 * mult);
						scoreUps.addElement(new ScoreUp(dScore, mX, mY));
						score += dScore;
						monsters.removeElementAt(i);
						continue;
					}
				}

				if(dy < 10 && dy > -10 && dx > -DETONATE_RADIUS && dx < DETONATE_RADIUS)
				{
					boolean succsess = true;
					int y = blowY+8;
					
					if(dx < 0)
					{
						for(int j = blowX; j>m.getSprite().getX(); j--)
						{
							int row = y/8;
							int col = j/8;
	
							if(walls.getCell(col,row) != 0)
								succsess = false;
						}
					}
					else
					{
						for(int j = blowX; j<m.getSprite().getX(); j++)
						{
							int row = y/8;
							int col = j/8;
	
							if(walls.getCell(col,row) != 0)
								succsess = false;
						}
					}

					if(succsess)
					{
						bloods.addElement(new Blood(mX, mY));

						for(int k=0; k<5; k++)
							flesh.addElement(new Flesh(mX, mY));

						int dScore = (int) (150 * mult);
						scoreUps.addElement(new ScoreUp(dScore, mX, mY));
						score += dScore;
						monsters.removeElementAt(i);
						continue;
					}
				}
			}
		}

		if(gameOver == false)
		{
			int mX = player.getSprite().getX();
			int mY = player.getSprite().getY();
			int dx = mX - blowX;
			int dy = mY - blowY;

			boolean directContact = false;
			for(int e=0; e<explosions.size(); e++)
			{
				if(player.getSprite().collidesWith(((Explosion) explosions.elementAt(e)).getSprite(), false) == true  && player.isProtected() == false)
				{
					player.setLives(-5);
					player.setProtection(75);
					
					System.out.println("Direct");
					
					if(player.isAlive() == false)
					{
						bloods.addElement(new Blood(mX, mY));

						for(int k=0; k<5; k++)
							flesh.addElement(new Flesh(mX, mY));
					}

					directContact = true;
					break;
				}
			}

			if(directContact == false)
			{

				if(dy < DETONATE_RADIUS && dy > -DETONATE_RADIUS && dx > -10 && dx < 10 && player.isProtected() == false)
				{
					boolean succsess = true;
					int x = blowX+8;
					
					if(dy < 0)
					{
						for(int j = blowY; j>player.getSprite().getY(); j--)
						{
							int row = j/8;
							int col = x/8;
	
							if(walls.getCell(col,row) != 0)
								succsess = false;
						}
					}
					else
					{
						for(int j = blowY; j<player.getSprite().getY(); j++)
						{
							int row = j/8;
							int col = x/8;
	
							if(walls.getCell(col,row) != 0)
								succsess = false;
						}
					}

					if(succsess)
					{
						player.setLives(-5);
						player.setProtection(75);

						System.out.println("Y contact");
						
						if(player.isAlive() == false)
						{
							bloods.addElement(new Blood(mX, mY));

							for(int k=0; k<5; k++)
								flesh.addElement(new Flesh(mX, mY));
						}
					}
				}

				else if(dy < 10 && dy > -10 && dx > -DETONATE_RADIUS && dx < DETONATE_RADIUS && player.isProtected() == false)
				{
					boolean succsess = true;
					int y = blowY+8;
					
					if(dx < 0)
					{
						for(int j = blowX; j>player.getSprite().getX(); j--)
						{
							int row = y/8;
							int col = j/8;
	
							if(walls.getCell(col,row) != 0)
								succsess = false;
						}
					}
					else
					{
						for(int j = blowX; j<player.getSprite().getX(); j++)
						{
							int row = y/8;
							int col = j/8;
	
							if(walls.getCell(col,row) != 0)
								succsess = false;
						}
					}

					if(succsess)
					{
						player.setLives(-5);
						player.setProtection(75);

						System.out.println("X contact");
						
						if(player.isAlive() == false)
						{
							bloods.addElement(new Blood(mX, mY));

							for(int k=0; k<5; k++)
								flesh.addElement(new Flesh(mX, mY));
						}
					}
				}
			}
		}

	}
	
	private void shoot()
	{
		player.setShoot(true);
		player.setAmmo(-1);
		flame.shoot(player.getSprite().getX(), player.getSprite().getY(), player.getDirection());
		
		if(bossLevel == true)
		{
			int dx = player.getSprite().getX() - boss.getX();
			int dy = player.getSprite().getY() - boss.getY();
			
			boolean success = false;
			
			if(boss.getProtection() == 0)
			{
				if(player.getDirection() == "up")
				{
					if(dy > 0 && dy < 64 && dx < 20 && dx > -20)
					{
						success = true;
					}
				}
				
				if(player.getDirection() == "down")
				{
					if(dy < 0 && dy > -64 && dx < 20 && dx > -20)
					{
						success = true;
					}
				}
				
				if(player.getDirection() == "left")
				{
					if(dy < 20 && dy > -20 && dx > 0 && dx < 64)
					{
						success = true;
					}
				}
				
				if(player.getDirection() == "right")
				{
					if(dy < 20 && dy > -20 && dx < 0 && dx > -64)
					{
						success = true;
					}
				}
				
				if(success == true)
				{
					boss.hurt(1);
					
					boss.setProtection(75);
					boss.setWTF(20);
					
					int dScore = (int) (30*mult);
					scoreUps.addElement(new ScoreUp(dScore, boss.getX(), boss.getY()));
				}
			}
		}
		
		for(int i=0; i<monsters.size(); i++)
		{
			int dx = player.getSprite().getX() - ((Monster) monsters.elementAt(i)).getSprite().getX();
			int dy = player.getSprite().getY() - ((Monster) monsters.elementAt(i)).getSprite().getY();
			
			if(player.getDirection() == "up")
			{
				if(dy > 0 && dy < 64 && dx < 10 && dx > -10)
				{
					boolean succsess = true;
					int x = player.getSprite().getX()+8;
					for(int j = player.getSprite().getY()+8; j>((Monster) monsters.elementAt(i)).getSprite().getY(); j--)
					{
						int row = j/8;
						int col = x/8;
						
						if(walls.getCell(col,row) != 0)
							succsess = false;
					}
					
					if(succsess)
					{
						((Monster) monsters.elementAt(i)).setStunnedTimeout(STUNNING_TIME);
						((Monster) monsters.elementAt(i)).setShotedInFace(((Monster) monsters.elementAt(i)).getDirection() == "down");
						int dScore = (int) (75 * mult);
						score += dScore;
						scoreUps.addElement(new ScoreUp(dScore, ((Monster) monsters.elementAt(i)).getSprite().getX(), ((Monster) monsters.elementAt(i)).getSprite().getY()));
					}
				}
			}
			
			if(player.getDirection() == "down")
			{
				if(dy < 0 && dy > -64 && dx < 10 && dx > -10)
				{
					boolean succsess = true;
					int x = player.getSprite().getX()+8;
					for(int j = player.getSprite().getY()+8; j<((Monster) monsters.elementAt(i)).getSprite().getY(); j++)
					{
						int row = j/8;
						int col = x/8;
						
						if(walls.getCell(col,row) != 0)
							succsess = false;
					}
					
					if(succsess)
					{
						((Monster) monsters.elementAt(i)).setStunnedTimeout(STUNNING_TIME);
						((Monster) monsters.elementAt(i)).setShotedInFace(((Monster) monsters.elementAt(i)).getDirection() == "up");
						int dScore = (int) (75 * mult);
						score += dScore;
						scoreUps.addElement(new ScoreUp(dScore, ((Monster) monsters.elementAt(i)).getSprite().getX(), ((Monster) monsters.elementAt(i)).getSprite().getY()));
					}
				}
			}
			
			if(player.getDirection() == "left")
			{
				if(dy < 10 && dy > -10 && dx > 0 && dx < 64)
				{
					boolean succsess = true;
					int y = player.getSprite().getY()+8;
					for(int j = player.getSprite().getX()+8; j>((Monster) monsters.elementAt(i)).getSprite().getX(); j--)
					{
						int row = y/8;
						int col = j/8;
						
						if(walls.getCell(col,row) != 0)
							succsess = false;
					}
					
					if(succsess)
					{
						((Monster) monsters.elementAt(i)).setStunnedTimeout(STUNNING_TIME);
						((Monster) monsters.elementAt(i)).setShotedInFace(((Monster) monsters.elementAt(i)).getDirection() == "right");
						int dScore = (int) (75 * mult);
						score += dScore;
						scoreUps.addElement(new ScoreUp(dScore, ((Monster) monsters.elementAt(i)).getSprite().getX(), ((Monster) monsters.elementAt(i)).getSprite().getY()));
					}
				}
			}
			
			if(player.getDirection() == "right")
			{
				if(dy < 10 && dy > -10 && dx < 0 && dx > -64)
				{
					boolean succsess = true;
					int y = player.getSprite().getY()+8;
					for(int j = player.getSprite().getX()+8; j<((Monster) monsters.elementAt(i)).getSprite().getX(); j++)
					{
						int row = y/8;
						int col = j/8;
						
						if(walls.getCell(col,row) != 0)
							succsess = false;
					}
					
					if(succsess)
					{
						((Monster) monsters.elementAt(i)).setStunnedTimeout(STUNNING_TIME);
						((Monster) monsters.elementAt(i)).setShotedInFace(((Monster) monsters.elementAt(i)).getDirection() == "left");
						int dScore = (int) (75 * mult);
						score += dScore;
						scoreUps.addElement(new ScoreUp(dScore, ((Monster) monsters.elementAt(i)).getSprite().getX(), ((Monster) monsters.elementAt(i)).getSprite().getY()));
					}
				}
			}
			
		}
		
	}
	
	private void gameOver(boolean isWin) 
	{
		gameOver = true;
		
		score += player.getLives()*100*mult;
		score += time*mult;
		score += player.getAmmo()*25*mult;
		score += player.getMines()*50*mult;
		score += player.getDarts()*10*mult;
		
		if(player.isAlive() == true)
		{
			if(player.isSpeeded() == true)
			{
				int dScore = (int) (player.getSpeedUpTimeout() / 3 * mult);
				player.speedUp(0);
				scoreUps.addElement(new ScoreUp(dScore, 80, 310));
				score += dScore;
			}
			
			if(player.isShielded() == true)
			{
				int dScore = (int) (player.getOuch_timeout() / 4 * mult);
				player.setProtection(0);
				scoreUps.addElement(new ScoreUp(dScore, 120, 310));
				score += dScore;
			}
			
			if(player.getCharged() > 0)
			{
				int dScore = (int) (player.getChargedTimeout() / 10 * player.getCharged() * mult);
				player.setChargedTimeout(0);
				player.setCharged(0);
				scoreUps.addElement(new ScoreUp(dScore, 160, 310));
				score += dScore;
			}
		}
		
		if(isWin)
			gameOverMessage = WIN_TEXT;
		else
			gameOverMessage = LOOSE_TEXT; 
	}

	/*
	private void collect(int col, int row)
	{
		int tileIndex = treasure.getCell(col, row);
		
		if(tileIndex == -1)
		{
			score += 10;
			coinsAmount++;
			coinsCollected++;
		}
		if(tileIndex == -10)
		{
			score += 50;
			gemsAmount++;
		}
		if(tileIndex == -17)
		{
			score += 20;
			heartExist = false;
			player.setLives(1);
		}
	}
	*/
	
	private void animateTreasure()
	{
		/*
		treasure.setAnimatedTile(-1, animationState*(-1));
		treasure.setAnimatedTile(-10, animationState*(-1)+8);
		treasure.setAnimatedTile(-17, animationState*(-1)+16);
		
		if(state == 1)
			animationState = ((animationState) % 8)-1;
		*/
		
		for(int i=0; i<pickups.size(); i++)
		{
			((Pickup) pickups.elementAt(i)).tick();
		}
	}
	
	
	private void tick()
	{
		checkCollision();
		state = state % ANIMATION_SPEED;	
		state++;
		
		if(actionDelay > 0)
			actionDelay--;
		
		if(gameOver == false)
		{
			player.tick();
		}
		
		if(bossLevel == true)
		{
			boss.tick(player, walls, difficulty);
		}
		
		for(int i=0; i<monsters.size(); i++)
		{
			if(((Monster) monsters.elementAt(i)).getStunnedTimeout() > 0)
			{ 
				boolean canStand = true;
				for(int j=0; j<monsters.size(); j++)
				{
					if(j != i)
					{
						if(((Monster) monsters.elementAt(i)).getSprite().collidesWith(((Monster) monsters.elementAt(j)).getSprite(), false) && ((Monster) monsters.elementAt(j)).getStunnedTimeout() == 0)
							canStand = false;
					}
				}
				
				if(canStand == true)
				{
					((Monster) monsters.elementAt(i)).setStunnedTimeout(((Monster) monsters.elementAt(i)).getStunnedTimeout()-1);
				}
			}
		}
		
		for(int i=0; i<mines.size(); i++)
		{
			if(((Mine) mines.elementAt(i)).getCharged() == false)
				((Mine) mines.elementAt(i)).checkCharged(player, gameOver);
			
			((Mine) mines.elementAt(i)).tick();
		}
		
		for(int i=scoreUps.size()-1; i>=0; i--)
		{
			if(((ScoreUp) scoreUps.elementAt(i)).isKilled() == true)
			{
				scoreUps.removeElementAt(i);
			}
		}
		
		for(int i=darts.size()-1; i>=0; i--)
		{
			if(((Dart) darts.elementAt(i)).isDestroyed() == true)
			{
				darts.removeElementAt(i);
			}
			else
			{
				int dScore = (int) (((Dart) darts.elementAt(i)).move(walls, monsters) * mult);
				score += dScore;
				if(dScore != 0)
				{
					scoreUps.addElement(new ScoreUp(dScore, ((Dart) darts.elementAt(i)).getSprite().getX(), ((Dart) darts.elementAt(i)).getSprite().getY()));
				}
			}
		}
		
		for(int i=0; i<spikes.size(); i++)
		{
			((Spikes) spikes.elementAt(i)).tick();
		}
		
		for(int i=0; i<plates.size(); i++)
		{
			((Plate) plates.elementAt(i)).tick(walls, spikes);
		}
		
		player.setShoot(flame.getShoot());
		
		if(state == 1 && time > 0 && gameOver == false)
		{
			time--;
			if(currentPickupTimeout > 0)
				currentPickupTimeout--;
		}
	}
	
	
	private void renderHelp(Graphics g)
	{
		g.drawImage(help_bg, 0, 0, 0);
		tabs.paint(g);
		g.drawImage(helpIcons, 0, 0, 0);
		
		if(helpTabPosition == 0)
		{
			g.drawImage(helpContent, 0, 0, 0);
		}
		else if(helpTabPosition == 1)
		{
			if(helpSettingsPosition == 0)
			{
				g.drawImage(clearScoresHl, 0, 47, 0);
				g.drawImage(clearProgress, 0, 108, 0);
				g.drawImage(deleteCustom, 0, 171, 0);
				g.drawImage(difficultyLevel, 0, 234, 0);
			}
			else if(helpSettingsPosition == 1)
			{
				g.drawImage(clearScores, 0, 47, 0);
				g.drawImage(clearProgressHl, 0, 108, 0);
				g.drawImage(deleteCustom, 0, 171, 0);
				g.drawImage(difficultyLevel, 0, 234, 0);
			}
			else if(helpSettingsPosition == 2)
			{
				g.drawImage(clearScores, 0, 47, 0);
				g.drawImage(clearProgress, 0, 108, 0);
				g.drawImage(deleteCustomHl, 0, 171, 0);
				g.drawImage(difficultyLevel, 0, 234, 0);
			}
			else if(helpSettingsPosition == 3)
			{
				g.drawImage(clearScores, 0, 47, 0);
				g.drawImage(clearProgress, 0, 108, 0);
				g.drawImage(deleteCustom, 0, 171, 0);
				g.drawImage(difficultyLevelHl, 0, 234, 0);
			}
			
			if(helpSettingsPosition == 3)
			{
				difficultyLevelsHL.setFrame(difficulty);
				difficultyLevelsHL.paint(g);
			}
			else
			{
				difficultyLevels.setFrame(difficulty);
				difficultyLevels.paint(g);
			}
			
			if(helpConfirm == true)
			{
				g.drawImage(helpConfirmBg, 0, 0, 0);
				
				String str = "Are you sure you want to";
				fontWhiteSmall.print(str, g, 120-(str.length()*6/2), 147, 6);
				fontWhiteSmall.print(helpConfirmText, g, 120-(helpConfirmText.length()*6/2), 162, 6);
				str = "This cannot be undone!";
				fontWhiteSmall.print(str, g, 120-(str.length()*6/2), 177, 6);
			}
		}
		else if(helpTabPosition == 2)
		{
			g.drawImage(credits, 0, 0, 0);
			fontBlackLarge.print("v"+parent.getAppProperty("MIDlet-Version"), g, 96, 190, 8);
		}
		
		flushGraphics();
	}
	
	private void renderMenu(Graphics g) 
	{
		if(menuLayer == 0)
		{
			int startX = 23;
			int startY = 131;

			g.setColor(255, 255, 255);
			Image subImg = Image.createImage(menuBackground, menuBackgroundShift, 0, 240, 320, 0);
			g.drawImage(subImg, 0, 0, 0);
			//g.drawImage(menuBackground, 0, 0, 0);
			subImg = null;
			
			help.paint(g);
			
			if(menuTicker == 0)
			{
				if(menuMovingLeft == true)
				{
					menuBackgroundShift--;
					if(menuBackgroundShift == 0)
					{
						menuMovingLeft = false;
					}
				}
				else
				{
					menuBackgroundShift++;
					if(menuBackgroundShift == 399)
					{
						menuMovingLeft = true;
					}
				}
			}

			menuTicker++;
			if(menuTicker == 3)
				menuTicker = 0;

			g.drawImage(menuTopSign, 0, 0, 0);

			//g.fillRect(startX, startY+((menuPositions.size()-menuCurrentPosition-1)*20), ((String) menuPositions.elementAt(menuCurrentPosition)).length()*10, 10);

			for(int i=menuPositions.size()-1; i>=0; i--)
			{
				if(menuPositions.elementAt(i) == START)
				{
					if(i == menuCurrentPosition)
					{
						g.drawImage(menuNewGameHL, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
					else
					{
						g.drawImage(menuNewGame, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
				}

				if(menuPositions.elementAt(i) == SCORES)
				{
					if(i == menuCurrentPosition)
					{
						g.drawImage(menuScoresHL, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
					else
					{
						g.drawImage(menuScores, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
				}

				if(menuPositions.elementAt(i) == EDITOR)
				{
					if(i == menuCurrentPosition)
					{
						g.drawImage(menuEditorHL, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
					else
					{
						g.drawImage(menuEditor, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
				}

				if(menuPositions.elementAt(i) == EXIT)
				{
					if(i == menuCurrentPosition)
					{
						g.drawImage(menuExitHL, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
					else
					{
						g.drawImage(menuExit, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
				}

				if(menuPositions.elementAt(i) == RESUME)
				{
					if(i == menuCurrentPosition)
					{
						g.drawImage(menuResumeHL, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
					else
					{
						g.drawImage(menuResume, startX, startY+((menuPositions.size()-i-1)*35), 0);
					}
				}

				//fontBlackLarge.print(((String) menuPositions.elementAt(i)), g, startX, startY+((menuPositions.size()-i-1)*20), 10);
			}

			//fontWhiteSmall.print("version "+parent.getAppProperty("MIDlet-Version"), g, 165, 304, 5);
		}
		else
		{
			if(paused == true)
			{
				g.drawImage(newGameBkgr_paused, 0, 0, 0);
			}
			else
			{
				g.drawImage(newGameBkgr, 0, 0, 0);
			}
			
			if(menuLayer == 1)
			{
				g.drawImage(newGameChooseSet, 0, 80, 0);
				g.drawImage(newGameMarker, 0, 145+newGamePosition*40, 0);
				g.drawImage(newGameBuildin, 0, 146, 0);
				g.drawImage(newGameCustom, 0, 185, 0);
			}
			else if(menuLayer == 2)
			{
				g.drawImage(buildIn, 0, 0, 0);
				g.drawImage(buildInTab, 14+buildInTabPosition*45, 79, 0);
				fontWhiteHugeDigits.print(12345, g, 20, 80, 45);

				for(int i=0; i<5; i++)
				{
					int level = i+buildInTabPosition*5+1;
					if(level <= openedLevels)
					{
						fontWhiteLarge.print("Level "+(level), g, 50, 135+i*20, 10);
					}
					else
					{
						fontGrayLarge.print("Level "+(level), g, 50, 135+i*20, 10);
					}
				}

				g.setColor(255, 255, 255);
				g.drawRoundRect(47, 132+buildInPosition*20, 84, 14, 5, 5);

				g.drawImage(preview, 135, 140, 0);
			}
			else if(menuLayer == 3)
			{
				g.drawImage(custom, 0, 0, 0);

				for(int i=0; i<Math.min(CUSTOM_MAX, saveNames.size()); i++)
				{
					int level = i+customShift;
					fontWhiteLarge.print((String) saveNames.elementAt(level), g, 50, 84+i*20, 10);
				}

				g.setColor(255, 255, 255);
				g.drawRoundRect(47, 82+customPosition*20, 104, 14, 5, 5);

				g.drawImage(preview, 155, 120, 0);
			}
		}

		if(isLoading == true)
		{
			g.drawImage(loading, 0, 140, 0);
		}
		
		flushGraphics();
	}

	private void renderShop(Graphics g)
	{	
		int buttonX = 109;
		int buttonY = 278;
		
		int startX = 22;
		int startY = 90;
		
		g.setColor(255,255,255);
		g.drawImage(shopBackground, 0, 0, 0);
		
		g.drawImage(shopShell, startX, startY-3, 0);
		g.drawImage(shopMine, startX, startY+17, 0);
		g.drawImage(shopDart, startX, startY+37, 0);
		g.drawImage(shopHeart, startX, startY+57, 0);
		
		if(shopCurrentPosition == shopPositions.size())
		{
			g.drawImage(shopButton, buttonX, buttonY, 0);
		}
		else
		{
			g.fillRect(startX+50, startY+(shopCurrentPosition*20), ((String) shopPositions.elementAt(shopCurrentPosition)).length()*10, 10);
		}
		
		fontWhiteLarge.print("x"+player.getAmmo(), g, startX+17, startY, 10);
		fontWhiteLarge.print("x"+player.getMines(), g, startX+17, startY+20, 10);
		fontWhiteLarge.print("x"+player.getDarts(), g, startX+17, startY+40, 10);
		fontWhiteLarge.print("x"+player.getLives(), g, startX+17, startY+60, 10);
		
		for(int i=0; i<shopPositions.size(); i++)
		{
			if(i == shopCurrentPosition)
				fontBlackLarge.print(((String) shopPositions.elementAt(i)), g, startX+50, startY+(i*20), 10);
			else
				fontWhiteLarge.print(((String) shopPositions.elementAt(i)), g, startX+50, startY+(i*20), 10);
			
			g.drawImage(shopCoin, startX+54+(((String) shopPositions.elementAt(i)).length()*10), startY+(i*20)-3, 0);
		}
		
		fontWhiteLarge.print(Integer.toString(coinsCollected), g, 54, 289, 9);
		
		if(isLoading == true)
		{
			g.drawImage(loading, 0, 140, 0);
		}
		
		flushGraphics();
	}
	
	private void renderScores(Graphics g)
	{
		int nameX = 47;
		int scoreX = 153;
		int startY = 103;
		int stepY = 20;
		
		g.drawImage(scoresBackground, 0, 0, 0);
		
		byte[] record;
		String s;
		String name;
		String value;
		int divideIndex;
		
		for(int i=0; i<10; i++)
		{
			
			try 
			{
				record = scores.getRecord(i+1);
				s = new String(record);
				divideIndex = s.indexOf("=");
				name = s.substring(0, divideIndex);
				value = s.substring(divideIndex+1, s.length());
				
				fontWhiteLarge.print(name, g, nameX, startY+(i*stepY), 10);
				fontWhiteLarge.print(value, g, scoreX, startY+(i*stepY), 10);
			} 
			catch (RecordStoreNotOpenException e) 
			{ e.printStackTrace(); } 
			catch (InvalidRecordIDException e) 
			{ e.printStackTrace(); }
			catch (RecordStoreException e) 
			{ e.printStackTrace(); }
		}
		
		if(isLoading == true)
		{
			g.drawImage(loading, 0, 140, 0);
		}
		
		flushGraphics();
	}
	
	private void renderHighscore(Graphics g)
	{
		int buttonX = 63;
		int buttonY = 278;
		
		int scoreX = 44;
		int scoreY = 167;
		
		int nameX = 122;
		int nameY = 249;
		
		g.drawImage(highscoreBackground, 0, 0, 0);
		
		fontWhiteHugeDigits.print(score, g, scoreX, scoreY, 0);
		
		if(highscoreCurrentPosition == 9)
		{
			g.drawImage(highscoreButton, buttonX, buttonY, 0);
		}
		else
		{
			g.drawImage(highscoreArrows, nameX+1+highscoreCurrentPosition*10, nameY-5, 0);
		}
		
		fontWhiteLarge.print(highscoreName, g, nameX, nameY, 10);
		
		if(isLoading == true)
		{
			g.drawImage(loading, 0, 140, 0);
		}
		
		flushGraphics();
	}
	
	private void render(Graphics g) 
	{
		//g.setFont(Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL));
		
		g.drawImage(background, 0, 0, 0);
		
		for(int i=0; i<bloods.size(); i++)
		{
			((Blood) bloods.elementAt(i)).paint(g);
		}
		
		for(int i=0; i<flesh.size(); i++)
		{
			((Flesh) flesh.elementAt(i)).paint(g);
		}
		
		exit.paint(g);
		
		for(int i=0; i<spikes.size(); i++)
		{
			((Spikes) spikes.elementAt(i)).paint(g);
		}
		
		for(int i=0; i<levers.size(); i++)
		{
			((Lever) levers.elementAt(i)).paint(g);
		}
		
		for(int i=0; i<plates.size(); i++)
		{
			((Plate) plates.elementAt(i)).paint(g);
		}
		
		for(int i=0; i<monsters.size(); i++)
		{
			if(((Monster) monsters.elementAt(i)).getStunnedTimeout() != 0)		//под слоем с сокровищами и игроком рисуем обездвиженных монстров
				((Monster) monsters.elementAt(i)).paint(g);
		}
		
		for(int i=0; i<mines.size(); i++)
		{
			((Mine) mines.elementAt(i)).paint(g);
		}
		
		//treasure.paint(g);
		
		for(int i=0; i<pickups.size(); i++)
		{
			((Pickup) pickups.elementAt(i)).paint(g);
		}
		
		if(player.getOuch_timeout() % 2 == 0 && player.isAlive() && gameOver == false)
			player.paint(g);
		
		if(player.getCharged() > 0)
		{
			aura.setPosition(player.getSprite().getX()-2, player.getSprite().getY()-2);
			aura.paint(g);
			aura.nextFrame();
		}
		
		for(int i=0; i<darts.size(); i++)
		{
			((Dart) darts.elementAt(i)).paint(g);
		}
		
		for(int i=0; i<monsters.size(); i++)
		{
			if(((Monster) monsters.elementAt(i)).getStunnedTimeout() == 0)		//сверху игрока рисуем только движущихся монстров
				((Monster) monsters.elementAt(i)).paint(g);
		}
		
		walls.paint(g);
		
		if(bossLevel == true)
		{
			boss.render(g);
		}
		
		if(flame.getShoot() == true)
			flame.paint(g);
		
		if(player.isSpeeded() == true)
		{
			g.setColor(255, 220, 0);
			g.fillRect(50, 316, (int)(155*((float)player.getSpeedUpTimeout()/SPEED_UP_TIME)), 1);
		}
		
		if(player.isShielded() == true)
		{
			g.setColor(10, 205, 250);
			g.fillRect(50, 317, (int)(155*((float)player.getOuch_timeout()/SHIELD_TIME)), 1);
		}
		
		if(player.getCharged() > 0)
		{
			g.setColor(235, 0, 0);
			g.fillRect(50, 318, (int)(155*((float)player.getChargedTimeout()/CHARGE_TIME)), 1);
		}
		
		//font = new mFont(false, false);
		fontWhiteSmall.print(Integer.toString(time), g, 180, 2, 5);
		fontWhiteSmall.print("score: "+score, g, 80, 2, 5);
		//g.setColor(0xffffff);
		//g.drawString(Integer.toString(time), 180, -2, 0);
		//g.drawString("score: "+score, 80, -2, 0);
		//g.setColor(0x000000);
		
		for(int i=0; i<explosions.size(); i++)
		{
			((Explosion) explosions.elementAt(i)).paint(g);
		}
		
		
		
		for(int i=0; i<scoreUps.size(); i++)
		{
			((ScoreUp) scoreUps.elementAt(i)).paint(g, fontGreenSmall);
		}
		
		if(paused == true && gameOver == false)
		{
			if(testMode == false)
			{
				int frame = 2 - (timer / (RESUME_DELAY / 3));
				if(frame < 0)
					frame = 0;
				if(frame > 2)
					frame = 2;
				countdown.setFrame(frame);
				countdown.paint(g);
				
				if(buildInMode == true)
				{
					g.setColor(0xffffff);
					g.fillRect(70, 110, 100, 20);
					g.setColor(0x000000);
					g.drawRect(70, 110, 100, 20);
					
					int chap = (int) (Math.ceil((double) currentLevel / 5));
					int lev = currentLevel - (chap-1)*5;
					
					fontBlackLarge.print("Level "+chap+"-"+lev, g, 75, 115, 10);
				}
				else
				{
					g.setColor(0xffffff);
					g.fillRect(60, 110, 110, 20);
					g.setColor(0x000000);
					g.drawRect(60, 110, 110, 20);
					
					String nam = ((String) saveNames.elementAt(currentLevel-1)).trim();
					fontBlackLarge.print(nam, g, 115-nam.length()*5, 115, 10);
				}
			}
			else
			{
				int scrH = 320;
				int h = 38;
				int w = 100;
				int offsetX = 0;
				int offsetY = -4;
				
				g.setColor(0xffffff);
				g.fillRoundRect(0+offsetX, scrH-h+offsetY, w+offsetX, h, 10, 10);
				g.setColor(0x000000);
				g.drawRoundRect(0+offsetX, scrH-h+offsetY, w+offsetX, h, 10, 10);
				
				g.fillRoundRect(3+offsetX, scrH+offsetY-(h-(testMenuPosition*15))+4, w-6, 12, 5, 5);
				
				if(testMenuPosition == 0)
				{
					fontWhiteLarge.print("Resume", g, 5+offsetX, scrH+offsetY-h+5, 0);
					fontBlackLarge.print("To editor", g, 5+offsetX, scrH+offsetY-(h-15)+5, 0);
				}
				else
				{
					fontBlackLarge.print("Resume", g, 5+offsetX, scrH+offsetY-h+5, 0);
					fontWhiteLarge.print("To editor", g, 5+offsetX, scrH+offsetY-(h-15)+5, 0);
				}
				
			}
		}
		
		//***********INTERFACE******************
		int lives = player.getLives();
		int currentWeapon = player.getCurrentWeapon();
		int ammo = player.getAmmo();
		int mines = player.getMines();
		int darts = player.getDarts();
		
		for(int i=0; i<lives; i++)
		{
			g.drawImage(heart, 5+i*6, 2, 0);
		}
		
		switch(currentWeapon)
		{
			case 0:
				if(ammo == 0)
				{
					g.drawImage(noShell, 0, 9, 0);
				}
				else
				{
					for(int i=0; i<ammo; i++)
					{
						g.drawImage(shell, 0, 9+i*7, 0);
					}
				}
				break;
			
			case 1:
				if(mines == 0)
				{
					g.drawImage(noMine, 0, 9, 0);
				}
				else
				{
					for(int i=0; i<mines; i++)
					{
						g.drawImage(mineIcon, 0, 9+i*7, 0);
					}
				}
				break;
				
			case 2:
				if(darts == 0)
				{
					g.drawImage(noDart, 0, 9, 0);
				}
				else
				{
					for(int i=0; i<darts; i++)
					{
						g.drawImage(dartIcon, 0, 9+i*7, 0);
					}
				}
				break;
		}
		
		g.drawImage(coin, 30, 313, 0);
		fontWhiteSmall.print(Integer.toString(coinsCollected), g, 37, 314, 5);
		//g.drawImage(coin, 2, 296, 0);
		//fontWhiteSmall.print(Integer.toString(coinsCollected), g, 1, 304, 5);
		
		fontWhiteSmall.print("Menu", g, 2, 314, 5);
		fontWhiteSmall.print("Weapon", g, 208, 314, 5);
		//g.setColor(0xffffff);
		//g.drawString(Integer.toString(coinsCollected), 12, 310, 0);
		//g.setColor(0x000000);
		//**************************************
		
		
		//mFont f = new mFont(false);
		//f.print("Hello, Stalker!", g, 120, 200);
		
		if(gameOver)
		{
			//int w = this.getWidth();
			//int h = this.getHeight();
			int w = 240;
			int h = 320;
			
			g.setColor(0xffffff);
			g.fillRoundRect(w/2-100, h/2-25, 200, 80, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(w/2-100, h/2-25, 200, 80, 10, 10);

			//font = new mFont(true, true);
			fontBlackLarge.print(gameOverMessage, g, w/2-55, h/2-15, 10);
			fontBlackLarge.print("Your score: "+score, g, w/2-80, h/2, 10);
			
			//font = new mFont(true, false);
			fontBlackSmall.print("Press OK to continue", g, w/2-45, h/2+25, 5);
			//g.drawString(gameOverMessage, w/2-30, h/2-15, 0);
			//g.drawString("Your score: "+score, w/2-35, h/2-5, 0);
			
			//g.drawString("Press OK to continue", w/2-40, h/2+25, 0);
		}
		
		if(congrats == true)
		{
			g.drawImage(congratulations, 0, 0, 0);
		}
		
		if(isLoading == true)
		{
			g.drawImage(loading, 0, 140, 0);
		}
		
		flushGraphics();
	}
}