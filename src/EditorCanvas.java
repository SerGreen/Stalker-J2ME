import Main;
import mGameCanvas;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.Vector;

import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.TiledLayer;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;
import javax.microedition.rms.RecordStoreFullException;
import javax.microedition.rms.RecordStoreNotFoundException;

public class EditorCanvas extends GameCanvas implements Runnable, mGameCanvas
{
	private Main parent;
	private StalkerCanvas gameCanvas;
	//private int[] map;
	
	private Image background;
	
	private boolean drawWalls;	//debug feature
	
	private int customLevelsExist;
	
	private boolean clearing;
	private boolean rendering;
	
	private Image s_coin;
	private Image s_coinDraw;
	private Image s_gem;
	private Image s_pickup;
	private Image s_plate;
	private Image s_lever;
	private Image s_spike;
	private Image s_player;
	private Image s_monster;
	private Image s_exit;
	private Image s_wall;
	private Image markerSpike;
	private Image markerWall;
	
	private Coordinates player;
	private Coordinates exit;
	
	private Cursor cursor;
	
	private int instrument;			//0=wall, 1=player, 2=exit, 3=gem, 4=coin, 5=pickup, 6=monster, 7=spike, 8=lever, 9=plate
	private boolean instrumentMenuOpened;
	private String[] instrumentNames;
	
	private TiledLayer walls;
	private Vector coins;
	private Vector gems;
	private Vector pickups;
	private Vector spikes;
	private Vector levers;
	private Vector plates;
	private Vector monsters;
	
	private boolean mTrucking;
	static final int TOTAL_BACKGROUNDS = 7;
	
	private String level;
	
	private mFont fontWhite;
	private mFont fontBlack;
	private mFont fontBlackSmall;
	private mFont fontGraySmall;
	private mFont fontWhiteSmall;
	private Vector menuPositions;
	private static final String config = "Level settings";
	private static final String test = "Test level";
	private static final String save = "Save";
	private static final String open = "Open level";
	private static final String close = "Close editor";
	private static final String newLevel = "New level";
	
	private boolean levelSettingsOpened;
	private int levelSettingsPosition;
	private Vector levelSettingsPositions;
	private int[] levelSettingsValues;
	
	private boolean menuOpened;
	private int menuPosition;
	
	private boolean spikeEdition;
	private boolean leverEdition;
	private boolean plateEdition;
	private boolean wallSelection;
	private boolean spikeSelection;
	private int selectionCalledBy = 0;	//0 = leverEditor, 1 = plateEditor
	
	private Vector spikeSelected;
	private Vector wallSelected;
	
	private Vector spikeMenuPositions;
	private int spikeMenuPosition;
	private int spikeInEdition;
	
	private static final String spikeMode = "Auto mode";
	private static final String spikeState = "Start state";
	private static final String spikeOpen = "Opened time";
	private static final String spikeClose = "Closed time";
	private static final String spikeCurrent = "Start time";
	private static final String spikeDelete = "Delete";
	
	private Vector leverMenuPositions;
	private int leverMenuPosition;
	private int leverInEdition;
	
	private Vector plateMenuPositions;
	private int plateMenuPosition;
	private int plateInEdition;
	
	private static final String leverState = "State";
	private static final String leverSpikes = "Select spikes";
	private static final String leverWalls = "Select walls";
	private static final String leverDelete = "Delete";
	
	private boolean openDialog;
	private int openDialogPosition;
	private int openDialogFolder;		//0=root, 1=build-in levels, 2=custom levels
	private int openDialogLevelSelected;
	private int openDialogShift;
	private static int openDialogSize = 24;
	private boolean deleteConfirm;
	private int buildInLevelsAmount;
	
	private boolean notification;
	private String notificationText;
	
	private boolean askRewrite;
	private int askRewritePosition;
	private int levelToRewrite;
	private Vector askRewritePositions;
	
	private boolean enterSaveName;
	private int saveNamePosition;
	private char[] newSaveName;
	private Vector saveNames;
	
	private boolean confirmExit;
	private boolean confirmNew;
	

	public EditorCanvas(Main parent, StalkerCanvas gameCanvas) 
	{
		super(false);
		this.setFullScreenMode(true);
		
		this.parent = parent;
		this.gameCanvas = gameCanvas;
		
		//System.out.println("I'm in!");
		
		try
		{
			RecordStore settings = RecordStore.openRecordStore("CustomLevels", true, RecordStore.AUTHMODE_PRIVATE, true);
			if(settings.getNumRecords() == 0)
			{
				settings.addRecord("0".getBytes(), 0, "0".length());
				customLevelsExist = 0;
			}
			else
			{
				customLevelsExist = Integer.parseInt(new String(settings.getRecord(1)));
			}
			settings.closeRecordStore();
			
			/*
			if(false)		//for wiping
			{
				for(int i=0; i<customLevelsExist; i++)
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
		
		//levelSettingsValues[0] = 1;
		//levelSettingsValues[1] = 30;
		
		drawWalls = true;
		
		rendering = false;
		clearing = false;
		
		menuOpened = false;
		menuPosition = 0;
		
		fontWhite = new mFont("white", true);
		fontBlack = new mFont("black", true);
		fontBlackSmall = new mFont("black", false);
		fontGraySmall = new mFont("gray", false);
		fontWhiteSmall = new mFont("white", false);
		
		Image wall = null;
		try 
		{
			background = Image.createImage("/background/game1.png");
			s_coin = Image.createImage("/editor/coin.png");
			s_coinDraw = Image.createImage(Image.createImage("/sprite/coins.png"), 0, 0, 16, 16, 0);
			s_gem = Image.createImage(Image.createImage("/sprite/gems.png"), 0, 0, 16, 16, 0);
			s_pickup = Image.createImage("/editor/pickup.png");
			s_spike = Image.createImage("/editor/spikes.png");
			s_plate = Image.createImage("/editor/plate.png");
			s_lever = Image.createImage(Image.createImage("/sprite/lever.png"), 0, 0, 16, 16, 0);
			wall = Image.createImage(Image.createImage("/sprite/wall_tile.png"), 0, 0, 8, 8, 0);
			s_player = Image.createImage("/sprite/stand_down.png");
			s_monster = Image.createImage("/editor/zombie.png");
			s_exit = Image.createImage(Image.createImage("/sprite/door.png"), 0, 0, 16, 16, 0);
			s_wall = Image.createImage("/editor/wall.png");
			markerSpike = Image.createImage("/editor/marker.png");
			markerWall = Image.createImage("/editor/marker_small.png");
		}
		catch (IOException e) 
		{ e.printStackTrace(); }
		
		cursor = new Cursor(8, 8);
		instrument = 0;
		instrumentMenuOpened = false;
		instrumentNames = new String[] { "Wall", "Player", "Exit", "Gem", "Coin", "Pickup", "Zombie", "Spikes", "Lever", "Pressure plate" };
		
		walls = new TiledLayer(30, 40, wall, 8, 8);
		
		for(int i=0; i<30; i++)
		{
			for(int j=0; j<40; j++)
			{
				if(j==0 || j==39)
				{
					walls.setCell(i, j, 1);
				}
				
				if(i==0 || i==29)
				{
					walls.setCell(i, j, 1);
				}
			}
		}
		
		coins = new Vector();
		gems = new Vector();
		pickups = new Vector();
		spikes = new Vector();
		levers = new Vector();
		plates = new Vector();
		monsters = new Vector();
		
		menuPositions = new Vector();
		menuPositions.addElement(config);
		menuPositions.addElement(test);
		menuPositions.addElement(newLevel);
		menuPositions.addElement(save);
		menuPositions.addElement(open);
		menuPositions.addElement(close);
		
		spikeEdition = false;
		spikeMenuPosition = 0;
		spikeInEdition = -1;
		
		spikeMenuPositions = new Vector();
		spikeMenuPositions.addElement(spikeMode);
		spikeMenuPositions.addElement(spikeState);
		spikeMenuPositions.addElement(spikeOpen);
		spikeMenuPositions.addElement(spikeClose);
		spikeMenuPositions.addElement(spikeCurrent);
		spikeMenuPositions.addElement(spikeDelete);
		
		leverEdition = false;
		leverInEdition = -1;
		leverMenuPosition = 0;
		
		leverMenuPositions = new Vector();
		leverMenuPositions.addElement(leverState);
		leverMenuPositions.addElement(leverSpikes);
		leverMenuPositions.addElement(leverWalls);
		leverMenuPositions.addElement(leverDelete);
		
		plateEdition = false;
		plateMenuPosition = 0;
		plateInEdition = -1;
		
		plateMenuPositions = new Vector();
		plateMenuPositions.addElement(leverSpikes);
		plateMenuPositions.addElement(leverWalls);
		plateMenuPositions.addElement(leverDelete);
		
		wallSelection = false;
		spikeSelection = false;
		spikeSelected = null;
		wallSelected = null;
		
		openDialog = false;
		openDialogFolder = 0;
		openDialogPosition = 0;
		openDialogLevelSelected = 1;
		openDialogShift = 1;
		deleteConfirm = false;
		buildInLevelsAmount = -1;
		
		notification = false;
		notificationText = "";
		
		askRewrite = false;
		askRewritePosition = 0;
		levelToRewrite = 0;
		askRewritePositions = new Vector();
		askRewritePositions.addElement("Choose another name");
		askRewritePositions.addElement("Rewrite");
		askRewritePositions.addElement("Cansel");
		
		levelSettingsPositions = new Vector();
		levelSettingsPositions.addElement("Background");
		levelSettingsPositions.addElement("Zombie smartness");
		levelSettingsPositions.addElement("Zombie speed");
		levelSettingsPositions.addElement("Gems amount");
		levelSettingsPositions.addElement("Coins amunt");
		levelSettingsPositions.addElement("Time limit");
		
		enterSaveName = false;
		saveNamePosition = 0;
		newSaveName = "----------".toCharArray();
		saveNames = new Vector();
		
		levelSettingsValues = new int[] { 1, 30, 2, 5, 20, 500 };
	}
	
	private void clear()
	{
		clearing = true;
		while(clearing == true)
		{
			if(rendering == false)
			{
				coins = new Vector();
				gems = new Vector();
				pickups = new Vector();
				spikes = new Vector();
				levers = new Vector();
				plates = new Vector();
				monsters = new Vector();
				player = null;
				exit = null;
				
				for(int i=0; i<walls.getRows(); i++)
				{
					for(int j=0; j<walls.getColumns(); j++)
					{
						if(i == 0 || i == walls.getRows()-1 || j == 0 || j == walls.getColumns()-1)
							walls.setCell(j, i, 1);
						else
							walls.setCell(j, i, 0);
					}
				}
				
				levelSettingsValues = new int[] { 1, 30, 2, 5, 20, 500 };
				
				clearing = false;
			}
		}
	}

	public void run() 
	{
		Graphics g = getGraphics();

		while (mTrucking == true) 
		{
			if(clearing == false)
			{
				rendering = true;
				render(g);
				rendering = false;
			}
		}
	}

	public void start() 
	{
		mTrucking = true;
		Thread t = new Thread(this);
		t.start();
	}
	
	private void warning(String message)
	{
		notification = true;
		notificationText = message;
	}
	
	public int getCurrentLevel()
	{ return 0; }
	
	public void saveGame()
	{ /*DO NOTHING!!! MUAHAHAHAHA!!!!!*/ }
	
	private void getSaveNames()
	{
		clearing = true;

		while(clearing == true)
		{
			if(rendering == false)
			{
				saveNames = new Vector();

				for(int i=0; i<customLevelsExist; i++)
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

				clearing = false;
			}
		}
	}
	
	protected void keyPressed(int keyCode) 
	{
		super.keyPressed(keyCode);
		
		if(keyCode == -1)	//up
		{
			//System.out.println("up");
			if(notification == true)
			{ }
			else if(deleteConfirm == true)
			{ }
			else if(confirmExit == true)
			{ }
			else if(confirmNew == true)
			{ }
			else if(askRewrite == true)
			{
				if(askRewritePosition > 0)
					askRewritePosition--;
			}
			else if(openDialog == true)
			{
				if(openDialogFolder == 0)
				{
					if(openDialogPosition > 0)
						openDialogPosition--;
				}
				else if(openDialogPosition > 0)
				{
					openDialogPosition--;
					openDialogLevelSelected--;
				}
				else
				{
					if(openDialogLevelSelected > 1)
					{
						openDialogLevelSelected--;
						openDialogShift--;
					}
				}
			}
			else if(enterSaveName == true)
			{
				if(newSaveName[saveNamePosition] == '-')
					newSaveName[saveNamePosition] = 'a';
				else if (newSaveName[saveNamePosition] == 'z')
					newSaveName[saveNamePosition] = '0';
				else if(newSaveName[saveNamePosition] == '9')
					newSaveName[saveNamePosition] = '-';
				else
					newSaveName[saveNamePosition] = (char)((int)newSaveName[saveNamePosition]+1);
			}
			else if(menuOpened == true)
			{
				if(menuPosition > 0)
					menuPosition--;
			}
			else if(instrumentMenuOpened == true)
			{
				instrument-=5;
				if(instrument < 0)
					instrument = 0;
			}
			else if(spikeEdition == true)
			{
				spikeMenuPosition--;
				if(spikeMenuPosition < 0)
					spikeMenuPosition = 0;
			}
			else if(leverEdition == true)
			{
				leverMenuPosition--;
				if(leverMenuPosition < 0)
					leverMenuPosition = 0;
			}
			else if(plateEdition == true)
			{
				plateMenuPosition--;
				if(plateMenuPosition < 0)
					plateMenuPosition = 0;
			}
			else if(levelSettingsOpened == true)
			{
				levelSettingsPosition--;
				if(levelSettingsPosition < 0)
					levelSettingsPosition = 0;
			}
			else
			{
				cursor.move(0, -8);
				checkCursorLegal();
			}
		}
		
		if(keyCode == -2)	//down
		{
			//System.out.println("down");
			if(notification == true)
			{ }
			else if(deleteConfirm == true)
			{ }
			else if(confirmExit == true)
			{ }
			else if(confirmNew == true)
			{ }
			else if(askRewrite == true)
			{
				if(askRewritePosition < 2)
					askRewritePosition++;
			}
			else if(openDialog == true)
			{
				if(openDialogFolder == 1)
				{
					if(openDialogPosition < Math.min(openDialogSize-1, buildInLevelsAmount-1))
					{
						openDialogPosition++;
						openDialogLevelSelected++;
					}

					else if(openDialogLevelSelected < buildInLevelsAmount)
					{
						openDialogLevelSelected++;
						openDialogShift++;
					}
				}
				else if(openDialogFolder == 2)
				{
					if(openDialogPosition < Math.min(openDialogSize-1, customLevelsExist-1))
					{
						openDialogPosition++;
						openDialogLevelSelected++;
					}

					else if(openDialogLevelSelected < customLevelsExist)
					{
						openDialogLevelSelected++;
						openDialogShift++;
					}
				}
				else
				{
					if(openDialogPosition < 1)
						openDialogPosition++;
				}
			}
			else if(enterSaveName == true)
			{
				if(newSaveName[saveNamePosition] == '-')
					newSaveName[saveNamePosition] = '9';
				else if (newSaveName[saveNamePosition] == '0')
					newSaveName[saveNamePosition] = 'z';
				else if(newSaveName[saveNamePosition] == 'a')
					newSaveName[saveNamePosition] = '-';
				else
					newSaveName[saveNamePosition] = (char)((int)newSaveName[saveNamePosition]-1);
			}
			else if(menuOpened == true)
			{
				if(menuPosition < menuPositions.size()-1)
					menuPosition++;
			}
			else if(instrumentMenuOpened == true)
			{
				instrument+=5;
				if(instrument > 9)
					instrument = 9;
			}
			else if(spikeEdition == true)
			{
				spikeMenuPosition++;
				if(spikeMenuPosition > spikeMenuPositions.size()-1)
					spikeMenuPosition = spikeMenuPositions.size()-1;
			}
			else if(leverEdition == true)
			{
				leverMenuPosition++;
				if(leverMenuPosition > leverMenuPositions.size()-1)
					leverMenuPosition = leverMenuPositions.size()-1;
			}
			else if(plateEdition == true)
			{
				plateMenuPosition++;
				if(plateMenuPosition > plateMenuPositions.size()-1)
					plateMenuPosition = plateMenuPositions.size()-1;
			}
			else if(levelSettingsOpened == true)
			{
				levelSettingsPosition++;
				if(levelSettingsPosition > levelSettingsPositions.size()-1)
					levelSettingsPosition = levelSettingsPositions.size()-1;
			}
			else
			{
				cursor.move(0, 8);
				checkCursorLegal();
			}
		}
		
		if(keyCode == -3)	//left
		{
			//System.out.println("left");
			if(notification == true)
			{ }
			else if(openDialog == true)
			{ }
			else if(askRewrite == true)
			{ }
			else if(confirmExit == true)
			{ }
			else if(confirmNew == true)
			{ }
			else if(enterSaveName == true)
			{
				if(saveNamePosition > 0)
					saveNamePosition--;
				else
					saveNamePosition = 9;
			}
			else if(menuOpened == false)
			{
				if(instrumentMenuOpened == true)
				{
					instrument--;
					if(instrument < 0)
						instrument = 0;
				}
				else if(spikeEdition == true)
				{
					if(spikeMenuPosition == 0)
						((SpikesEditor) spikes.elementAt(spikeInEdition)).setAutoMode(false);
					else if(spikeMenuPosition == 1)
						((SpikesEditor) spikes.elementAt(spikeInEdition)).setState(false);
					else if(((SpikesEditor) spikes.elementAt(spikeInEdition)).isAuto() == true)
					{
						if(spikeMenuPosition == 2)
							((SpikesEditor) spikes.elementAt(spikeInEdition)).setTimeOpen(-10);
						else if(spikeMenuPosition == 3)
							((SpikesEditor) spikes.elementAt(spikeInEdition)).setTimeClosed(-10);
						else if(spikeMenuPosition == 4)
							((SpikesEditor) spikes.elementAt(spikeInEdition)).setCurrentTime(-10);
					}
				}
				else if(leverEdition == true)
				{
					if(leverMenuPosition == 0)
						((LeverEditor) levers.elementAt(leverInEdition)).setState(false);
				}
				else if(plateEdition == true)
				{ }	
				else if(levelSettingsOpened == true)
				{
					if(levelSettingsPosition == 0)
					{
						levelSettingsValues[0]--;
						if(levelSettingsValues[0] < 1)
							levelSettingsValues[0] = 1;
						
						try
						{
							background = Image.createImage("/background/game"+Integer.toString(levelSettingsValues[0])+".png");
						}
						catch (IOException e)
						{ System.out.println("NO SUCH GAME BACKGROUND!"); }
					}
					else if(levelSettingsPosition == 1)
					{
						levelSettingsValues[1] -= 5;
						if(levelSettingsValues[1] < 0)
							levelSettingsValues[1] = 0;
					}
					else if(levelSettingsPosition == 2)
					{
						levelSettingsValues[2] --;
						if(levelSettingsValues[2] < 1)
							levelSettingsValues[2] = 1;
					}
					else if(levelSettingsPosition == 3)
					{
						levelSettingsValues[3]--;
						if(levelSettingsValues[3] < 1)
							levelSettingsValues[3] = 1;
					}
					else if(levelSettingsPosition == 4)
					{
						levelSettingsValues[4]--;
						if(levelSettingsValues[4] < 0)
							levelSettingsValues[4] = 0;
					}
					else if(levelSettingsPosition == 5)
					{
						levelSettingsValues[5] -= 25;
						if(levelSettingsValues[5] < 50)
							levelSettingsValues[5] = 50;
					}
				}
				else
				{
					cursor.move(-8, 0);
					checkCursorLegal();
				}
			}
		}
		
		if(keyCode == -4)	//right
		{
			//System.out.println("right");
			if(notification == true)
			{ }
			else if(openDialog == true)
			{ }
			else if(askRewrite == true)
			{ }
			else if(confirmExit == true)
			{ }
			else if(confirmNew == true)
			{ }
			else if(enterSaveName == true)
			{
				if(saveNamePosition < 9)
					saveNamePosition++;
				else
					saveNamePosition = 0;
			}
			else if(menuOpened == false)
			{
				if(instrumentMenuOpened == true)
				{
					instrument++;
					if(instrument > 9)
						instrument = 9;
				}
				else if(spikeEdition == true)
				{
					if(spikeMenuPosition == 0)
						((SpikesEditor) spikes.elementAt(spikeInEdition)).setAutoMode(true);
					else if(spikeMenuPosition == 1)
						((SpikesEditor) spikes.elementAt(spikeInEdition)).setState(true);
					else if(((SpikesEditor) spikes.elementAt(spikeInEdition)).isAuto() == true)
					{
						if(spikeMenuPosition == 2)
							((SpikesEditor) spikes.elementAt(spikeInEdition)).setTimeOpen(10);
						else if(spikeMenuPosition == 3)
							((SpikesEditor) spikes.elementAt(spikeInEdition)).setTimeClosed(10);
						else if(spikeMenuPosition == 4)
							((SpikesEditor) spikes.elementAt(spikeInEdition)).setCurrentTime(10);
					}
				}
				else if(leverEdition == true)
				{
					if(leverMenuPosition == 0)
						((LeverEditor) levers.elementAt(leverInEdition)).setState(true);
				}
				else if(plateEdition == true)
				{ }	
				else if(levelSettingsOpened == true)
				{
					if(levelSettingsPosition == 0)
					{
						levelSettingsValues[0]++;
						if(levelSettingsValues[0] > TOTAL_BACKGROUNDS)
							levelSettingsValues[0] = TOTAL_BACKGROUNDS;
						
						try
						{
							background = Image.createImage("/background/game"+Integer.toString(levelSettingsValues[0])+".png");
						}
						catch (IOException e)
						{ System.out.println("NO SUCH GAME BACKGROUND!"); }
					}
					else if(levelSettingsPosition == 1)
					{
						levelSettingsValues[1] += 5;
						if(levelSettingsValues[1] > 100)
							levelSettingsValues[1] = 100;
					}
					else if(levelSettingsPosition == 2)
					{
						levelSettingsValues[2]++;
						if(levelSettingsValues[2] > 4)
							levelSettingsValues[2] = 4;
					}
					else if(levelSettingsPosition == 3)
					{
						levelSettingsValues[3]++;
						if(levelSettingsValues[3] > 50)
							levelSettingsValues[3] = 50;
					}
					else if(levelSettingsPosition == 4)
					{
						levelSettingsValues[4]++;
						if(levelSettingsValues[4] > 50)
							levelSettingsValues[4] = 50;
					}
					else if(levelSettingsPosition == 5)
					{
						levelSettingsValues[5] += 25;
						if(levelSettingsValues[5] > 5000)
							levelSettingsValues[5] = 5000;
					}
				}
				else
				{
					cursor.move(8, 0);
					checkCursorLegal();
				}
			}
		}
		
		if(keyCode == -5)	//fire
		{
			//System.out.println("fire");
			if(notification == true)
			{
				notification = false;
			}
			else if(deleteConfirm == true)
			{ }
			else if(confirmExit == true)
			{ }
			else if(confirmNew == true)
			{ }
			else if(levelSettingsOpened == true)
			{
				levelSettingsOpened = false;
			}
			else if(askRewrite == true)
			{
				if(askRewritePosition == 0)
				{
					askRewrite = false;
					enterSaveName = true;
				}
				else if(askRewritePosition == 1)
				{
					askRewrite = false;
					rewriteLevel(levelToRewrite);
				}
				else if(askRewritePosition == 2)
				{
					askRewrite = false;
				}
			}
			else if(openDialog == true)
			{
				if(openDialogFolder == 0)
				{
					openDialogFolder = openDialogPosition+1;
					openDialogPosition = 0;
					openDialogLevelSelected = 1;
					openDialogShift = 1;
					
					if(openDialogFolder == 2)
					{
						getSaveNames();
					}
					
					if(openDialogFolder == 1)
					{
						buildInLevelsAmount = getBuildInLevelsAmount();
					}
				}
				else if(openDialogFolder == 1)
				{
					if(buildInLevelsAmount != 0)
					{
						openDialog = false;
						try
						{
							InputStreamReader is = new InputStreamReader(getClass().getResourceAsStream("/levels/level_"+(openDialogLevelSelected)+".lvl"));
							char[] cbuff = new char[5000];

							is.read(cbuff);
							is.close();
							level = new String(cbuff);

							clearing = true;
							while(clearing == true)
							{
								if(rendering == false)
								{
									//System.out.println("Started");
									loadLevel(level);
									//System.out.println("Finished");		
									/* Костыль какой-то. Из-за неведанного бага при первой загрузке любого уровня, кроме второго, программа вылетает.
									 * Если сначала загрузить второй, то остальные начинают загружаться. Если нету вывода строк Started и Closed, то и второй не загружается. Бред... 
									 * 
									 * UPD: больше не работает. Теперь нужно отключать на время рендер стен с помощью (*)
									 */
									clearing = false;
								}
							}
							
							menuOpened = false;
						}
						catch (IOException e)
						{ e.printStackTrace(); }
					}
				}
				else if(openDialogFolder == 2)
				{
					if(customLevelsExist == 0)
					{
						openDialogFolder = 0;
					}
					else
					{
						//ЭКШОН!!! Actually open level
						openDialog = false;
						try
						{
							RecordStore save = RecordStore.openRecordStore("customLevel_"+Integer.toString(openDialogLevelSelected), false, RecordStore.AUTHMODE_PRIVATE, false);
							level = new String(save.getRecord(1));
							save.closeRecordStore();
							
							clearing = true;
							while(clearing == true)
							{
								if(rendering == false)
								{
									loadLevel(level);
									clearing = false;
								}
							}
							
							menuOpened = false;
						}
						catch (RecordStoreFullException e)
						{ e.printStackTrace(); }
						catch (RecordStoreNotFoundException e)
						{ e.printStackTrace(); }
						catch (RecordStoreException e)
						{ e.printStackTrace(); }
					}
				}
			}
			else if(enterSaveName == true)
			{ }
			else if(menuOpened == true)
			{
				if(menuPositions.elementAt(menuPosition) == close)
				{
					confirmExit = true;
					menuOpened = false;
				}
				else if(menuPositions.elementAt(menuPosition) == config)
				{
					levelSettingsOpened = true;
					menuOpened = false;
					levelSettingsPosition = 0;
				}
				else if(menuPositions.elementAt(menuPosition) == test)
				{
					if(compile() == true)
					{
						gameCanvas.test(level, this);
						mTrucking = false;
						parent.changeCanvas(gameCanvas);
						menuOpened = false;
					}
				}
				else if(menuPositions.elementAt(menuPosition) == newLevel)
				{
					menuOpened = false;
					confirmNew = true;
				}
				else if(menuPositions.elementAt(menuPosition) == save)
				{
					menuOpened = false;
					if(checkSaveAbility() == true)
					{
						enterSaveName = true;
						newSaveName = "----------".toCharArray();
						saveNamePosition = 0;
					}
				}
				else if(menuPositions.elementAt(menuPosition) == open)
				{
					openDialog = true;
					menuOpened = false;
					openDialogFolder = 0;
					openDialogPosition = 0;
				}
			}
			else if(instrumentMenuOpened == true)
			{
				instrumentMenuOpened = false;
				checkCursorLegal();
				if(instrument == 0)
				{	
					if(cursor.isWallMode() == false)
						cursor.changeMode();
				}
				else
				{
					if(cursor.isWallMode() == true)
						cursor.changeMode();
				}
			}
			else if(spikeEdition == true)
			{
				if(spikeMenuPosition == 5)
				{
					for(int i=0; i<spikes.size(); i++)
					{
						if(((SpikesEditor) spikes.elementAt(i)).getX() == ((SpikesEditor) spikes.elementAt(spikeInEdition)).getX() && ((SpikesEditor) spikes.elementAt(i)).getY() == ((SpikesEditor) spikes.elementAt(spikeInEdition)).getY())
						{
							spikes.removeElementAt(i);
							spikeMenuPosition = 0;
							
							for(int j=0; j<levers.size(); j++)
							{
								((LeverEditor) levers.elementAt(j)).checkChildSpikes(spikes);
							}
							for(int j=0; j<plates.size(); j++)
							{
								((PlateEditor) plates.elementAt(j)).checkChildSpikes(spikes);
							}
							
							spikeEdition = false;
							break;
						}
					}
				}
				else
				{
					spikeMenuPosition = 0;
					spikeEdition = false;
				}
			}
			else if(leverEdition == true)
			{
				if(leverMenuPosition == 3)
				{
					for(int i=0; i<levers.size(); i++)
					{
						if(((LeverEditor) levers.elementAt(i)).getX() == ((LeverEditor) levers.elementAt(leverInEdition)).getX() && ((LeverEditor) levers.elementAt(i)).getY() == ((LeverEditor) levers.elementAt(leverInEdition)).getY())
						{
							levers.removeElementAt(i);
							leverMenuPosition = 0;
							leverEdition = false;
							break;
						}
					}
				}
				else if(leverMenuPosition == 1)
				{
					spikeSelection = true;
					selectionCalledBy = 0;	//by leverEditor
					spikeSelected = ((LeverEditor) levers.elementAt(leverInEdition)).getChildsSpikes();
					instrument = 7;
					checkCursorLegal();
					leverEdition = false;
				}
				else if(leverMenuPosition == 2)
				{
					wallSelection = true;
					selectionCalledBy = 0;	//by leverEditor
					wallSelected = ((LeverEditor) levers.elementAt(leverInEdition)).getChildsWalls();
					instrument = 0;
					cursor.changeMode();
					checkCursorLegal();
					leverEdition = false;
				}
				else
				{
					leverMenuPosition = 0;
					leverEdition = false;
				}
			}
			else if(plateEdition == true)
			{
				if(plateMenuPosition == 2)
				{
					for(int i=0; i<plates.size(); i++)
					{
						if(((PlateEditor) plates.elementAt(i)).getX() == ((PlateEditor) plates.elementAt(plateInEdition)).getX() && ((PlateEditor) plates.elementAt(i)).getY() == ((PlateEditor) plates.elementAt(plateInEdition)).getY())
						{
							plates.removeElementAt(i);
							plateMenuPosition = 0;
							plateEdition = false;
							break;
						}
					}
				}
				else if(plateMenuPosition == 0)
				{
					spikeSelection = true;
					selectionCalledBy = 1;	//by plateEditor
					spikeSelected = ((PlateEditor) plates.elementAt(plateInEdition)).getChildsSpikes();
					instrument = 7;
					checkCursorLegal();
					plateEdition = false;
				}
				else if(plateMenuPosition == 1)
				{
					wallSelection = true;
					selectionCalledBy = 1;	//by plateEditor
					wallSelected = ((PlateEditor) plates.elementAt(plateInEdition)).getChildsWalls();
					instrument = 0;
					cursor.changeMode();
					checkCursorLegal();
					plateEdition = false;
				}
				else
				{
					plateMenuPosition = 0;
					plateEdition = false;
				}
			}
			else if(spikeSelection == true)
			{
				boolean del = false;

				for(int i=0; i<spikeSelected.size(); i++)
				{
					if(((Coordinates) spikeSelected.elementAt(i)).getX() == cursor.getX() && ((Coordinates) spikeSelected.elementAt(i)).getY() == cursor.getY())
					{
						del = true;
						spikeSelected.removeElementAt(i);
						break;
					}
				}

				if(del == false)
				{
					for(int i=0; i<spikes.size(); i++)
					{
						if(((SpikesEditor) spikes.elementAt(i)).getX() == cursor.getX() && ((SpikesEditor) spikes.elementAt(i)).getY() == cursor.getY())
						{
							spikeSelected.addElement(new Coordinates(cursor.getX(), cursor.getY()));
						}
					}
				}
			}
			else if(wallSelection == true)
			{
				if(cursor.isLegal() == true)
				{
					boolean del = false;

					for(int i=0; i<wallSelected.size(); i++)
					{
						if(((Coordinates) wallSelected.elementAt(i)).getX() == cursor.getX() && ((Coordinates) wallSelected.elementAt(i)).getY() == cursor.getY())
						{
							del = true;
							wallSelected.removeElementAt(i);
							break;
						}
					}

					if(del == false)
					{
						wallSelected.addElement(new Coordinates(cursor.getX(), cursor.getY()));
					}
				}
			}
			else
			{
				if(cursor.isLegal() == true)
				{
					
					
					switch(instrument)
					{
						case 0:
							clearing = true;

							while(clearing == true)
							{
								if(rendering == false)
								{
									int x = cursor.getX()/8;
									int y = cursor.getY()/8;
									//System.out.println("x:"+walls.getCell(x, y));
									//System.out.println("y:"+y);

									if(walls.getCell(x, y) == 1)
										walls.setCell(x, y, 0);
									else
										walls.setCell(x, y, 1);

									clearing = false;
								}
							}
							break;
							
						case 1:
							if(player != null)
							{
								player.setX(cursor.getX());
								player.setY(cursor.getY());
							}
							else
							{
								player = new Coordinates(cursor.getX(), cursor.getY());
							}
							break;
							
						case 2:
							if(exit != null)
							{
								exit.setX(cursor.getX());
								exit.setY(cursor.getY());
							}
							else
							{
								exit = new Coordinates(cursor.getX(), cursor.getY());
							}
							
							for(int i=0; i<levers.size(); i++)
							{
								((LeverEditor) levers.elementAt(i)).checkChildWalls(new Coordinates(exit.getX(), exit.getY()));
							}
							for(int i=0; i<plates.size(); i++)
							{
								((PlateEditor) plates.elementAt(i)).checkChildWalls(new Coordinates(exit.getX(), exit.getY()));
							}
							break;
							
						case 3:
							boolean del = false;
							for(int i=0; i<gems.size(); i++)
							{
								if(((Coordinates) gems.elementAt(i)).getX() == cursor.getX() && ((Coordinates) gems.elementAt(i)).getY() == cursor.getY())
								{
									del = true;
									gems.removeElementAt(i);
									break;
								}
							}
							
							if(del == false)
								gems.addElement(new Coordinates(cursor.getX(), cursor.getY()));
							break;
							
						case 4:
							del = false;
							for(int i=0; i<coins.size(); i++)
							{
								if(((Coordinates) coins.elementAt(i)).getX() == cursor.getX() && ((Coordinates) coins.elementAt(i)).getY() == cursor.getY())
								{
									del = true;
									coins.removeElementAt(i);
									break;
								}
							}
							
							if(del == false)
								coins.addElement(new Coordinates(cursor.getX(), cursor.getY()));
							break;
							
						case 5:
							del = false;
							for(int i=0; i<pickups.size(); i++)
							{
								if(((Coordinates) pickups.elementAt(i)).getX() == cursor.getX() && ((Coordinates) pickups.elementAt(i)).getY() == cursor.getY())
								{
									del = true;
									pickups.removeElementAt(i);
									break;
								}
							}
							
							if(del == false)
								pickups.addElement(new Coordinates(cursor.getX(), cursor.getY()));
							break;
							
						case 6:
							del = false;
							for(int i=0; i<monsters.size(); i++)
							{
								if(((Coordinates) monsters.elementAt(i)).getX() == cursor.getX() && ((Coordinates) monsters.elementAt(i)).getY() == cursor.getY())
								{
									del = true;
									monsters.removeElementAt(i);
									break;
								}
							}
							
							if(del == false)
								monsters.addElement(new Coordinates(cursor.getX(), cursor.getY()));
							break;
							
						case 7:
							boolean edit = false;
							for(int i=0; i<spikes.size(); i++)
							{
								if(((SpikesEditor) spikes.elementAt(i)).getX() == cursor.getX() && ((SpikesEditor) spikes.elementAt(i)).getY() == cursor.getY())
								{
									edit = true;
									spikeInEdition = i;
									spikeEdition = true;
									break;
								}
							}
							
							if(edit == false)
							{
								spikes.addElement(new SpikesEditor(cursor.getX(), cursor.getY(), true, false, 0, 0, 0));
								spikeInEdition = spikes.size()-1;
								spikeEdition = true;
							}
							
							for(int i=0; i<levers.size(); i++)
							{
								((LeverEditor) levers.elementAt(i)).checkChildWalls(new Coordinates(cursor.getX(), cursor.getY()));
							}
							for(int i=0; i<plates.size(); i++)
							{
								((PlateEditor) plates.elementAt(i)).checkChildWalls(new Coordinates(cursor.getX(), cursor.getY()));
							}
							break;
							
						case 8:
							edit = false;
							for(int i=0; i<levers.size(); i++)
							{
								if(((LeverEditor) levers.elementAt(i)).getX() == cursor.getX() && ((LeverEditor) levers.elementAt(i)).getY() == cursor.getY())
								{
									edit = true;
									leverInEdition = i;
									leverMenuPosition = 0;
									leverEdition = true;
									break;
								}
							}
							
							if(edit == false)
							{
								levers.addElement(new LeverEditor(cursor.getX(), cursor.getY(), true));
								leverInEdition = levers.size()-1;
								leverMenuPosition = 0;
								leverEdition = true;
							}
							
							try
							{
								for(int i=0; i<levers.size()-1; i++)
								{
									((LeverEditor) levers.elementAt(i)).checkChildWalls(new Coordinates(cursor.getX(), cursor.getY()));
								}
								for(int i=0; i<plates.size(); i++)
								{
									((PlateEditor) plates.elementAt(i)).checkChildWalls(new Coordinates(cursor.getX(), cursor.getY()));
								}
							}
							catch(NullPointerException e)
							{
								e.printStackTrace();
							}
							break;
							
						case 9:
							edit = false;
							for(int i=0; i<plates.size(); i++)
							{
								if(((PlateEditor) plates.elementAt(i)).getX() == cursor.getX() && ((PlateEditor) plates.elementAt(i)).getY() == cursor.getY())
								{
									edit = true;
									plateInEdition = i;
									plateMenuPosition = 0;
									plateEdition = true;
									break;
								}
							}
							
							if(edit == false)
							{
								plates.addElement(new PlateEditor(cursor.getX(), cursor.getY()));
								plateInEdition = plates.size()-1;
								plateMenuPosition = 0;
								plateEdition = true;
							}
							
							for(int i=0; i<levers.size(); i++)
							{
								((LeverEditor) levers.elementAt(i)).checkChildWalls(new Coordinates(cursor.getX(), cursor.getY()));
							}
							for(int i=0; i<plates.size()-1; i++)
							{
								((PlateEditor) plates.elementAt(i)).checkChildWalls(new Coordinates(cursor.getX(), cursor.getY()));
							}
							break;	
					}
				}
			}
		}
		
		if(keyCode == KEY_POUND)
		{
			//System.out.println("pound");
			if(notification == true)
			{ }
			else if(openDialog == true && openDialogFolder == 2)
			{
				deleteConfirm = true;
			}
		}
		
		if(keyCode == KEY_STAR)
		{
			//System.out.println("star");
			if(drawWalls == true)
				drawWalls = false;
			else
				drawWalls = true;
			
			if(notification == true)
			{ }
			else if(menuOpened == false)
			{
				
			}
		}
		
		if(keyCode == 48)		//0
		{
			//System.out.println("zero");
		}
		
		if(keyCode == -6)		//leftShift
		{
			//System.out.println("left shift");
			if(notification == true)
			{ }
			if(askRewrite == true)
			{ }
			else if(levelSettingsOpened == true)
			{
				levelSettingsOpened = false;
			}
			else if(instrumentMenuOpened == true)
			{ }
			else if(plateEdition == true || spikeEdition == true || wallSelection == true || spikeSelection == true || leverEdition == true)
			{ }
			else if(confirmExit == true)
			{
				confirmExit = false;
				mTrucking = false;
				parent.changeCanvas(gameCanvas);
			}
			else if(confirmNew == true)
			{
				confirmNew = false;
				clear();
				menuOpened = false;
			}
			else if(deleteConfirm == true)
			{
				deleteConfirm = false;

				try
				{
					RecordStore.deleteRecordStore("customLevel_"+Integer.toString(openDialogLevelSelected));

					for(int i=openDialogLevelSelected; i<customLevelsExist; i++)
					{
						RecordStore donor = RecordStore.openRecordStore("customLevel_"+Integer.toString(i+1), false, RecordStore.AUTHMODE_PRIVATE, false);
						RecordStore acceptor = RecordStore.openRecordStore("customLevel_"+Integer.toString(i), true, RecordStore.AUTHMODE_PRIVATE, true);

						String str = new String(donor.getRecord(1));
						acceptor.addRecord(str.getBytes(), 0, str.length());
						str = new String(donor.getRecord(2));
						acceptor.addRecord(str.getBytes(), 0, str.length());

						donor.closeRecordStore();
						acceptor.closeRecordStore();

						RecordStore.deleteRecordStore("customLevel_"+Integer.toString(i+1));
					}

					customLevelsExist--;
					RecordStore settings = RecordStore.openRecordStore("CustomLevels", false, RecordStore.AUTHMODE_PRIVATE, true);
					String str = Integer.toString(customLevelsExist);
					settings.setRecord(1, str.getBytes(), 0, str.length());
					settings.closeRecordStore();


				}
				catch (RecordStoreFullException e)
				{ e.printStackTrace(); }
				catch (RecordStoreNotFoundException e)
				{ e.printStackTrace(); }
				catch (RecordStoreException e)
				{ e.printStackTrace(); }

				getSaveNames();
				clearing = false;
			}
			else if(openDialog == true)
			{
				if(openDialogFolder == 0)
				{
					openDialog = false;
				}
				else
				{
					openDialogFolder = 0;
					openDialogPosition = 0;
				}
			}
			else if(enterSaveName == true)
			{
				enterSaveName = false;
			}
			else if(menuOpened == true)
			{
				menuOpened = false;
			}
			else
			{
				menuOpened = true;
				menuPosition = 0;
			}
		}
		
		if(keyCode == -7)		//rightShift
		{
			//System.out.println("right shift");
			if(notification == true)
			{ }
			if(askRewrite == true)
			{ }
			else if(confirmExit == true)
			{
				confirmExit = false;
			}
			else if(confirmNew == true)
			{
				confirmNew = false;
			}
			else if(deleteConfirm == true)
			{
				deleteConfirm = false;
			}
			else if(openDialog == true)
			{ }
			else if(enterSaveName == true)
			{
				//ЭКШОН!!! Сохранение тут.
				enterSaveName = false;
				menuOpened = false;
				
				getSaveNames();
				String name = new String(newSaveName).replace('-', ' ');
				boolean match = false;
				
				for(int i=0; i<saveNames.size(); i++)
				{
					if(((String) saveNames.elementAt(i)).equals(name) == true)
					{
						match = true;
						levelToRewrite = i+1;
						break;
					}
				}
				
				if(match == true)
				{
					askRewrite = true;
					askRewritePosition = 0;
				}
				else
					saveLevel(name);
			}
			else if(menuOpened == false)
			{
				if(levelSettingsOpened == true)
				{
					levelSettingsOpened = false;
				}
				else if(spikeEdition == true)
				{
					spikeEdition = false;
				}
				else if(leverEdition == true)
				{
					leverEdition = false;
				}
				else if(plateEdition == true)
				{
					plateEdition = false;
				}
				else if(spikeSelection == true)
				{
					spikeSelection = false;
					
					if(selectionCalledBy == 0)
					{
						leverEdition = true;
						instrument = 8;
						cursor.setPlace(((LeverEditor) levers.elementAt(leverInEdition)).getX(), ((LeverEditor) levers.elementAt(leverInEdition)).getY());
						((LeverEditor) levers.elementAt(leverInEdition)).setChildsSpikes(spikeSelected);
					}
					else
					{
						plateEdition = true;
						instrument = 9;
						cursor.setPlace(((PlateEditor) plates.elementAt(plateInEdition)).getX(), ((PlateEditor) plates.elementAt(plateInEdition)).getY());
						((PlateEditor) plates.elementAt(plateInEdition)).setChildsSpikes(spikeSelected);
					}
				}
				else if(wallSelection == true)
				{
					wallSelection = false;
					
					if(selectionCalledBy == 0)
					{
						leverEdition = true;
						instrument = 8;
						cursor.changeMode();
						cursor.setPlace(((LeverEditor) levers.elementAt(leverInEdition)).getX(), ((LeverEditor) levers.elementAt(leverInEdition)).getY());
						((LeverEditor) levers.elementAt(leverInEdition)).setChildsWalls(wallSelected);
					}
					else
					{
						plateEdition = true;
						instrument = 9;
						cursor.changeMode();
						cursor.setPlace(((PlateEditor) plates.elementAt(plateInEdition)).getX(), ((PlateEditor) plates.elementAt(plateInEdition)).getY());
						((PlateEditor) plates.elementAt(plateInEdition)).setChildsWalls(wallSelected);
					}
				}
				else if(instrumentMenuOpened == true)
				{
					instrumentMenuOpened = false;
					checkCursorLegal();
					if(instrument == 0)
					{	
						if(cursor.isWallMode() == false)
							cursor.changeMode();
					}
					else
					{
						if(cursor.isWallMode() == true)
							cursor.changeMode();
					}
				}
				else
				{
					instrumentMenuOpened = true;
				}
			}
		}
		
		//System.out.println("levelSelected="+openDialogLevelSelected);
	}
	
	private void saveLevel(String name)
	{
		compile();
		
		try
		{
			customLevelsExist++;
			RecordStore save = RecordStore.openRecordStore("customLevel_"+Integer.toString(customLevelsExist), true, RecordStore.AUTHMODE_PRIVATE, true);
			save.addRecord(level.getBytes(), 0, level.length());
			save.addRecord(name.getBytes(), 0, newSaveName.length);
			save.closeRecordStore();

			RecordStore settings = RecordStore.openRecordStore("CustomLevels", false, RecordStore.AUTHMODE_PRIVATE, true);
			String str = Integer.toString(customLevelsExist);
			settings.setRecord(1, str.getBytes(), 0, str.length());
			settings.closeRecordStore();
		}
		catch (RecordStoreFullException e)
		{ e.printStackTrace(); }
		catch (RecordStoreNotFoundException e)
		{ e.printStackTrace(); }
		catch (RecordStoreException e)
		{ e.printStackTrace(); }
	}
	
	private void rewriteLevel(int index)		//if level names are equal and user want to rewrite level. index - 1-based index of level (customLevel_X)
	{
		compile();
		try
		{
			RecordStore save = RecordStore.openRecordStore("customLevel_"+Integer.toString(index), false, RecordStore.AUTHMODE_PRIVATE, true);	//this level should exist, so create if necessary is false
			save.setRecord(1, level.getBytes(), 0, level.length());
			//no need to change name, it is the same
			save.closeRecordStore();

			//no need to change amount of custom levels too
		}
		catch (RecordStoreFullException e)
		{ e.printStackTrace(); }
		catch (RecordStoreNotFoundException e)
		{ e.printStackTrace(); }
		catch (RecordStoreException e)
		{ e.printStackTrace(); }
	}
	
	private void checkCursorLegal()
	{
		boolean legal = true;
		
		if(instrument == 0)
		{
			for(int i=0; i<monsters.size(); i++)
			{
				int x = cursor.getX();
				int y = cursor.getY();

				int mX = ((Coordinates) monsters.elementAt(i)).getX();
				int mY = ((Coordinates) monsters.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8)
				{
					legal = false;
					break;
				}
			}

			for(int i=0; i<spikes.size(); i++)
			{
				int x = cursor.getX();
				int y = cursor.getY();

				int mX = ((SpikesEditor) spikes.elementAt(i)).getX();
				int mY = ((SpikesEditor) spikes.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8)
				{
					legal = false;
					break;
				}
			}

			for(int i=0; i<levers.size(); i++)
			{
				int x = cursor.getX();
				int y = cursor.getY();

				int mX = ((LeverEditor) levers.elementAt(i)).getX();
				int mY = ((LeverEditor) levers.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8)
				{
					legal = false;
					break;
				}
			}

			for(int i=0; i<plates.size(); i++)
			{
				int x = cursor.getX();
				int y = cursor.getY();

				int mX = ((PlateEditor) plates.elementAt(i)).getX();
				int mY = ((PlateEditor) plates.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8)
				{
					legal = false;
					break;
				}
			}

			for(int i=0; i<gems.size(); i++)
			{
				int x = cursor.getX();
				int y = cursor.getY();

				int mX = ((Coordinates) gems.elementAt(i)).getX();
				int mY = ((Coordinates) gems.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8)
				{
					legal = false;
					break;
				}
			}

			for(int i=0; i<coins.size(); i++)
			{
				int x = cursor.getX();
				int y = cursor.getY();

				int mX = ((Coordinates) coins.elementAt(i)).getX();
				int mY = ((Coordinates) coins.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8)
				{
					legal = false;
					break;
				}
			}

			for(int i=0; i<pickups.size(); i++)
			{
				int x = cursor.getX();
				int y = cursor.getY();

				int mX = ((Coordinates) pickups.elementAt(i)).getX();
				int mY = ((Coordinates) pickups.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8)
				{
					legal = false;
					break;
				}
			}

			if(player != null)
			{
				int x = cursor.getX();
				int y = cursor.getY();

				int mX = player.getX();
				int mY = player.getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8)
				{
					legal = false;
				}
			}

			if(exit != null)
			{
				int x = cursor.getX();
				int y = cursor.getY();

				int mX = exit.getX();
				int mY = exit.getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8)
				{
					legal = false;
				}
			}
		}
		else if(instrument == 1 || instrument == 2 || instrument == 7 || instrument == 8 || instrument == 6)
		{
			int x = cursor.getX();
			int y = cursor.getY();
			if(walls.getCell(x/8, y/8) == 1 ||
					walls.getCell(x/8+1, y/8+1) == 1 ||
					walls.getCell(x/8, y/8+1) == 1 ||
					walls.getCell(x/8+1, y/8) == 1)
			{
				legal = false;
			}
			
			for(int i=0; i<monsters.size(); i++)
			{
				int mX = ((Coordinates) monsters.elementAt(i)).getX();
				int mY = ((Coordinates) monsters.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
					if(instrument == 6 && x == mX && y == mY)
						legal = true;
					break;
				}
			}

			for(int i=0; i<spikes.size(); i++)
			{
				int mX = ((SpikesEditor) spikes.elementAt(i)).getX();
				int mY = ((SpikesEditor) spikes.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
					if(instrument == 7 && x == mX && y == mY)
					{
						if(spikeEdition == false)
						{
							legal = true;
						}
						else
						{
							if(((SpikesEditor) spikes.elementAt(i)).isAuto() == false)
								legal = true;
						}
					}
					break;
				}
			}

			for(int i=0; i<levers.size(); i++)
			{
				int mX = ((LeverEditor) levers.elementAt(i)).getX();
				int mY = ((LeverEditor) levers.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
					if(instrument == 8 && x == mX && y == mY)
						legal = true;
					break;
				}
			}

			for(int i=0; i<plates.size(); i++)
			{
				int mX = ((PlateEditor) plates.elementAt(i)).getX();
				int mY = ((PlateEditor) plates.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
					break;
				}
			}

			for(int i=0; i<gems.size(); i++)
			{
				int mX = ((Coordinates) gems.elementAt(i)).getX();
				int mY = ((Coordinates) gems.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
					break;
				}
			}

			for(int i=0; i<coins.size(); i++)
			{
				int mX = ((Coordinates) coins.elementAt(i)).getX();
				int mY = ((Coordinates) coins.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
					break;
				}
			}

			for(int i=0; i<pickups.size(); i++)
			{
				int mX = ((Coordinates) pickups.elementAt(i)).getX();
				int mY = ((Coordinates) pickups.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
					break;
				}
			}

			if(player != null && instrument != 1)
			{
				int mX = player.getX();
				int mY = player.getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
				}
			}

			if(exit != null && instrument != 2)
			{
				int mX = exit.getX();
				int mY = exit.getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
				}
			}
		}
		else if(instrument == 3 || instrument == 4 || instrument == 5 || instrument == 9)
		{
			int x = cursor.getX();
			int y = cursor.getY();
			if(walls.getCell(x/8, y/8) == 1 ||
					walls.getCell(x/8+1, y/8+1) == 1 ||
					walls.getCell(x/8, y/8+1) == 1 ||
					walls.getCell(x/8+1, y/8) == 1)
			{
				legal = false;
			}
			
			for(int i=0; i<monsters.size(); i++)
			{
				int mX = ((Coordinates) monsters.elementAt(i)).getX();
				int mY = ((Coordinates) monsters.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY)
				{
					legal = false;
					break;
				}
			}

			if(instrument == 9)
			{
				for(int i=0; i<spikes.size(); i++)
				{
					int mX = ((SpikesEditor) spikes.elementAt(i)).getX();
					int mY = ((SpikesEditor) spikes.elementAt(i)).getY();

					if(x == mX && y == mY)
					{
						legal = false;
						if(instrument == 7 && x == mX && y == mY)
							legal = true;
						break;
					}
				}
			}

			for(int i=0; i<levers.size(); i++)
			{
				int mX = ((LeverEditor) levers.elementAt(i)).getX();
				int mY = ((LeverEditor) levers.elementAt(i)).getY();

				if(x == mX && y == mY)
				{
					legal = false;
					if(instrument == 8 && x == mX && y == mY)
						legal = true;
					break;
				}
			}

			for(int i=0; i<plates.size(); i++)
			{
				int mX = ((PlateEditor) plates.elementAt(i)).getX();
				int mY = ((PlateEditor) plates.elementAt(i)).getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
					if(instrument == 9 && x == mX && y == mY)
						legal = true;
					break;
				}
			}

			for(int i=0; i<gems.size(); i++)
			{
				int mX = ((Coordinates) gems.elementAt(i)).getX();
				int mY = ((Coordinates) gems.elementAt(i)).getY();

				if(x == mX && y == mY)
				{
					legal = false;
					if(instrument == 3)
						legal = true;
					break;
				}
			}

			for(int i=0; i<coins.size(); i++)
			{
				int mX = ((Coordinates) coins.elementAt(i)).getX();
				int mY = ((Coordinates) coins.elementAt(i)).getY();

				if(x == mX && y == mY)
				{
					legal = false;
					if(instrument == 4)
						legal = true;
					break;
				}
			}

			for(int i=0; i<pickups.size(); i++)
			{
				int mX = ((Coordinates) pickups.elementAt(i)).getX();
				int mY = ((Coordinates) pickups.elementAt(i)).getY();

				if(instrument != 5)
				{
					if(x == mX && y == mY)
					{
						legal = false;
						break;
					}
				}
				else
				{
					if(x == mX+8 && y == mY ||
							x == mX && y == mY+8 ||
							x == mX+8 && y == mY+8 ||
							x+8 == mX && y == mY ||
							x == mX && y+8 == mY ||
							x+8 == mX && y+8 == mY ||
							x+8 == mX && y-8 == mY ||
							x-8 == mX && y+8 == mY)
					{
						legal = false;
						break;
					}	
				}
			}

			if(player != null)
			{
				int mX = player.getX();
				int mY = player.getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
				}
			}

			if(exit != null)
			{
				int mX = exit.getX();
				int mY = exit.getY();

				if(x == mX && y == mY ||
						x == mX+8 && y == mY ||
						x == mX && y == mY+8 ||
						x == mX+8 && y == mY+8 ||
						x+8 == mX && y == mY ||
						x == mX && y+8 == mY ||
						x+8 == mX && y+8 == mY ||
						x+8 == mX && y-8 == mY ||
						x-8 == mX && y+8 == mY)
				{
					legal = false;
				}
			}
		}

		if(cursor.isLegal() != legal)
		{
			cursor.changeLegal();
		}
	}
	
	private void render(Graphics g) 
	{
		g.drawImage(background, 0, 0, 0);
		
		if(drawWalls == true)
			walls.paint(g);
		
		if(player != null)
		{ g.drawImage(s_player, player.getX(), player.getY(), 0); }
		
		if(exit != null)
		{ g.drawImage(s_exit, exit.getX(), exit.getY(), 0); }
		
		for(int i=0; i<gems.size(); i++)
		{
			int x = ((Coordinates) gems.elementAt(i)).getX();
			int y = ((Coordinates) gems.elementAt(i)).getY();
			
			g.drawImage(s_gem, x, y, 0);
		}
		
		for(int i=0; i<spikes.size(); i++)
		{
			((SpikesEditor) spikes.elementAt(i)).paint(g);
		}
		
		for(int i=0; i<coins.size(); i++)
		{
			int x = ((Coordinates) coins.elementAt(i)).getX();
			int y = ((Coordinates) coins.elementAt(i)).getY();
			
			g.drawImage(s_coinDraw, x, y, 0);
		}
		
		for(int i=0; i<pickups.size(); i++)
		{
			int x = ((Coordinates) pickups.elementAt(i)).getX();
			int y = ((Coordinates) pickups.elementAt(i)).getY();
			
			g.drawImage(s_pickup, x, y, 0);
		}
		
		for(int i=0; i<monsters.size(); i++)
		{
			int x = ((Coordinates) monsters.elementAt(i)).getX();
			int y = ((Coordinates) monsters.elementAt(i)).getY();
			
			g.drawImage(s_monster, x, y, 0);
		}
		
		for(int i=0; i<levers.size(); i++)
		{
			((LeverEditor) levers.elementAt(i)).paint(g);
		}
		
		for(int i=0; i<plates.size(); i++)
		{
			((PlateEditor) plates.elementAt(i)).paint(g);
		}
		
		
		cursor.paint(g);
		

		if(spikeSelection == false && wallSelection == false && plateEdition == false && leverEdition == false && spikeEdition == false && openDialog == false && deleteConfirm == false && enterSaveName == false && askRewrite == false && notification == false && menuOpened == false && levelSettingsOpened == false)
		{
			if(instrumentMenuOpened == true)
			{
				fontWhiteSmall.print("Confirm", g, 194, 313, 6);
			}
			else
			{
				fontWhiteSmall.print("Menu", g, 4, 313, 6);
				fontWhiteSmall.print("Instruments", g, 170, 313, 6);
			}
		}
		
		if(spikeSelection == true)
		{
			for(int i=0; i<spikeSelected.size(); i++)
			{
				g.drawImage(markerSpike, ((Coordinates) spikeSelected.elementAt(i)).getX(), ((Coordinates) spikeSelected.elementAt(i)).getY(), 0);
			}
		}
		
		if(wallSelection == true)
		{
			for(int i=0; i<wallSelected.size(); i++)
			{
				g.drawImage(markerWall, ((Coordinates) wallSelected.elementAt(i)).getX(), ((Coordinates) wallSelected.elementAt(i)).getY(), 0);
			}
		}
		
		
		if(plateEdition == true)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 35;
			int w = 100;
			
			g.setColor(0xffffff);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);

			g.fillRoundRect(scrW/2-w/2+3, scrH-scrH/2-h/2+4+plateMenuPosition*8, w-6, 7, 3, 3);

			for(int i=0; i<plateMenuPositions.size(); i++)
			{
				if(i != plateMenuPosition)
				{
					fontBlackSmall.print(((String) plateMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
				}
				else
				{
					fontWhiteSmall.print(((String) plateMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
				}
			}
		}
		
		if(leverEdition == true)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 40;
			int w = 120;
			
			g.setColor(0xffffff);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);

			g.fillRoundRect(scrW/2-w/2+3, scrH-scrH/2-h/2+4+leverMenuPosition*8, w-6, 7, 3, 3);

			for(int i=0; i<leverMenuPositions.size(); i++)
			{
				if(i != leverMenuPosition)
				{
					fontBlackSmall.print(((String) leverMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
				}
				else
				{
					fontWhiteSmall.print(((String) leverMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
				}
			}


			if(leverMenuPosition == 0)
			{
				if(((LeverEditor) levers.elementAt(leverInEdition)).getState() == true)
					fontWhiteSmall.print("up", g, scrW/2-w/2+90, scrH/2-h/2+5, 6);
				else
					fontWhiteSmall.print("down", g, scrW/2-w/2+90, scrH/2-h/2+5, 6);
			}
			else
			{
				if(((LeverEditor) levers.elementAt(leverInEdition)).getState() == true)
					fontBlackSmall.print("up", g, scrW/2-w/2+90, scrH/2-h/2+5, 6);
				else
					fontBlackSmall.print("down", g, scrW/2-w/2+90, scrH/2-h/2+5, 6);
			}
		}
		
		
		if(spikeEdition == true)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 55;
			int w = 125;
			
			g.setColor(0xffffff);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			
			g.fillRoundRect(scrW/2-w/2+3, scrH-scrH/2-h/2+4+spikeMenuPosition*8, w-6, 7, 3, 3);
			
			if(((SpikesEditor) spikes.elementAt(spikeInEdition)).isAuto() == false)
			{
				for(int i=0; i<spikeMenuPositions.size(); i++)
				{
					if(i != spikeMenuPosition)
					{
						if((i < 2 || i > 4))
						{
							fontBlackSmall.print(((String) spikeMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
						}
						else
						{
							fontGraySmall.print(((String) spikeMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
						}
					}
					else
					{
						if((i < 2 || i > 4) && ((SpikesEditor) spikes.elementAt(spikeInEdition)).isAuto() == false)
						{
							fontWhiteSmall.print(((String) spikeMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
						}
						else
						{
							fontGraySmall.print(((String) spikeMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
						}
					}
				}
			}
			else
			{
				for(int i=0; i<spikeMenuPositions.size(); i++)
				{
					if(i != spikeMenuPosition)
					{
						fontBlackSmall.print(((String) spikeMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
					}
					else
					{
						fontWhiteSmall.print(((String) spikeMenuPositions.elementAt(i)), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
					}
				}
			}
			
			if(spikeMenuPosition == 0)
			{
				if(((SpikesEditor) spikes.elementAt(spikeInEdition)).isAuto() == true)
					fontWhiteSmall.print("auto", g, scrW/2-w/2+85, scrH/2-h/2+5, 6);
				else
					fontWhiteSmall.print("manual", g, scrW/2-w/2+85, scrH/2-h/2+5, 6);
			}
			else
			{
				if(((SpikesEditor) spikes.elementAt(spikeInEdition)).isAuto() == true)
					fontBlackSmall.print("auto", g, scrW/2-w/2+85, scrH/2-h/2+5, 6);
				else
					fontBlackSmall.print("manual", g, scrW/2-w/2+85, scrH/2-h/2+5, 6);
			}

			if(spikeMenuPosition == 1)
			{
				if(((SpikesEditor) spikes.elementAt(spikeInEdition)).isOpenedState() == true)
					fontWhiteSmall.print("opened", g, scrW/2-w/2+85, scrH/2-h/2+13, 6);
				else
					fontWhiteSmall.print("closed", g, scrW/2-w/2+85, scrH/2-h/2+13, 6);
			}
			else
			{
				if(((SpikesEditor) spikes.elementAt(spikeInEdition)).isOpenedState() == true)
					fontBlackSmall.print("opened", g, scrW/2-w/2+85, scrH/2-h/2+13, 6);
				else
					fontBlackSmall.print("closed", g, scrW/2-w/2+85, scrH/2-h/2+13, 6);
			}

			if(((SpikesEditor) spikes.elementAt(spikeInEdition)).isAuto() == true)
			{
				if(spikeMenuPosition == 2)
				{
					fontWhiteSmall.print(Integer.toString(((SpikesEditor) spikes.elementAt(spikeInEdition)).getOpen()), g, scrW/2-w/2+85, scrH/2-h/2+21, 6);
				}
				else
				{
					fontBlackSmall.print(Integer.toString(((SpikesEditor) spikes.elementAt(spikeInEdition)).getOpen()), g, scrW/2-w/2+85, scrH/2-h/2+21, 6);
				}
				if(spikeMenuPosition == 3)
				{
					fontWhiteSmall.print(Integer.toString(((SpikesEditor) spikes.elementAt(spikeInEdition)).getClose()), g, scrW/2-w/2+85, scrH/2-h/2+29, 6);
				}
				else
				{
					fontBlackSmall.print(Integer.toString(((SpikesEditor) spikes.elementAt(spikeInEdition)).getClose()), g, scrW/2-w/2+85, scrH/2-h/2+29, 6);
				}
				if(spikeMenuPosition == 4)
				{
					fontWhiteSmall.print(Integer.toString(((SpikesEditor) spikes.elementAt(spikeInEdition)).getCurrent()), g, scrW/2-w/2+85, scrH/2-h/2+37, 6);
				}
				else
				{
					fontBlackSmall.print(Integer.toString(((SpikesEditor) spikes.elementAt(spikeInEdition)).getCurrent()), g, scrW/2-w/2+85, scrH/2-h/2+37, 6);
				}
			}
			else
			{
				fontGraySmall.print(Integer.toString(((SpikesEditor) spikes.elementAt(spikeInEdition)).getOpen()), g, scrW/2-w/2+85, scrH/2-h/2+21, 6);
				fontGraySmall.print(Integer.toString(((SpikesEditor) spikes.elementAt(spikeInEdition)).getClose()), g, scrW/2-w/2+85, scrH/2-h/2+29, 6);
				fontGraySmall.print(Integer.toString(((SpikesEditor) spikes.elementAt(spikeInEdition)).getCurrent()), g, scrW/2-w/2+85, scrH/2-h/2+37, 6);
			}
		}
		
		
		if(openDialog == true)
		{
			int scrH = 320;
			int scrW = 240;
			int h = -1;
			int w = 130;
			if(openDialogFolder == 0)
				h = 22;
			else if(openDialogFolder == 1)
			{
				if(buildInLevelsAmount == 0)
					h = 14;
				else
					h = Math.min(200, buildInLevelsAmount*8+6);
			}
			else if(openDialogFolder == 2)
			{
				if(customLevelsExist == 0)
					h = 14;
				else 
					h = Math.min(200, customLevelsExist*8+6);
			}
			
			g.setColor(0xffffff);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			
			g.fillRoundRect(scrW/2-w/2+3, scrH-scrH/2-h/2+4+openDialogPosition*8, w-6, 7, 3, 3);
			
			
			if(openDialogFolder == 0)
			{
				if(openDialogPosition == 0)
				{
					fontWhiteSmall.print("Build-in levels", g, scrW/2-w/2+5, scrH/2-h/2+5, 6);
					fontBlackSmall.print("Custom levels", g, scrW/2-w/2+5, scrH/2-h/2+13, 6);
				}
				else
				{
					fontBlackSmall.print("Build-in levels", g, scrW/2-w/2+5, scrH/2-h/2+5, 6);
					fontWhiteSmall.print("Custom levels", g, scrW/2-w/2+5, scrH/2-h/2+13, 6);
				}
			}
			else if(openDialogFolder == 1)
			{
				if(buildInLevelsAmount == 0)
				{
					fontWhiteSmall.print("Levels vanished magically", g, scrW/2-w/2+5, scrH/2-h/2+5, 6);
				}
				else for(int i=0; i<Math.min(openDialogSize, buildInLevelsAmount); i++)
				{
					if(i != openDialogPosition)
					{
						fontBlackSmall.print("level "+Integer.toString(openDialogShift+i), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
					}
					else
					{
						fontWhiteSmall.print("level "+Integer.toString(openDialogShift+i), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
					}
				}
			}
			else if(openDialogFolder == 2)
			{
				if(customLevelsExist == 0)
				{
					fontWhiteSmall.print("No saved levels yet", g, scrW/2-w/2+5, scrH/2-h/2+5, 6);
				}
				else for(int i=0; i<Math.min(openDialogSize, saveNames.size()); i++)
				{
					if(i != openDialogPosition)
					{
						fontBlackSmall.print("level "+Integer.toString(openDialogShift+i), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
						fontBlackSmall.print((String)saveNames.elementAt(openDialogShift+i-1), g, scrW/2-w/2+65, scrH/2-h/2+5+i*8, 6);
					}
					else
					{
						fontWhiteSmall.print("level "+Integer.toString(openDialogShift+i), g, scrW/2-w/2+5, scrH/2-h/2+5+i*8, 6);
						fontWhiteSmall.print((String)saveNames.elementAt(openDialogShift+i-1), g, scrW/2-w/2+65, scrH/2-h/2+5+i*8, 6);
					}
					
					String d = "# - delete level";
					fontWhiteSmall.print(d, g, scrW-d.length()*6-6, scrH-7, 6);
				}
			}
		}
		
		if(deleteConfirm)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 50;
			int w = 190;
			
			g.setColor(0xffbbbb);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			
			String m = "delete "+((String) saveNames.elementAt(openDialogLevelSelected-1)).trim()+" permanently?";
			fontBlackSmall.print("Are you sure you want to", g, scrW/2-72, scrH/2-h/2+5, 6);
			fontBlackSmall.print(m, g, scrW/2-m.length()/2*6, scrH/2-h/2+12, 6);
			fontBlackSmall.print("Yep", g, scrW/2-w/2+15, scrH/2+h/2-10, 6);
			fontBlackSmall.print("NO WAY!", g, scrW/2+w/2-57, scrH/2+h/2-10, 6);
		}
		
		if(confirmExit)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 50;
			int w = 190;
			
			g.setColor(0xffeeee);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			
			String m = "close level editor?";
			fontBlackSmall.print("Are you sure you want to", g, scrW/2-72, scrH/2-h/2+5, 6);
			fontBlackSmall.print(m, g, scrW/2-m.length()/2*6, scrH/2-h/2+12, 6);
			fontBlackSmall.print("Yes", g, scrW/2-w/2+15, scrH/2+h/2-10, 6);
			fontBlackSmall.print("Cansel", g, scrW/2+w/2-51, scrH/2+h/2-10, 6);
		}
		
		if(confirmNew)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 50;
			int w = 200;
			
			g.setColor(0xffeeee);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			
			String m = "close this and create new level?";
			fontBlackSmall.print("Are you sure you want to", g, scrW/2-72, scrH/2-h/2+5, 6);
			fontBlackSmall.print(m, g, scrW/2-m.length()/2*6, scrH/2-h/2+12, 6);
			fontBlackSmall.print("Yes", g, scrW/2-w/2+15, scrH/2+h/2-10, 6);
			fontBlackSmall.print("Cansel", g, scrW/2+w/2-51, scrH/2+h/2-10, 6);
		}
		
		if(enterSaveName)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 50;
			int w = 150;
			
			g.setColor(0xffffff);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			
			fontBlackSmall.print("Enter name of save file", g, scrW/2-69, scrH/2-h/2+5, 6);
			
			fontBlackSmall.print(new String(newSaveName), g, scrW/2-30, scrH/2-h/2+17, 6);
			g.fillRect(scrW/2-31+saveNamePosition*6, scrH/2-h/2+16, 6, 7);
			fontWhiteSmall.print(new String(new char[] {newSaveName[saveNamePosition]}), g, scrW/2-30+saveNamePosition*6, scrH/2-h/2+17, 6);
			
			fontBlackSmall.print("Cansel", g, scrW/2-w/2+15, scrH/2+h/2-10, 6);
			fontBlackSmall.print("Okay", g, scrW/2+w/2-39, scrH/2+h/2-10, 6);
		}
		
		if(askRewrite)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 50;
			int w = 150;
			
			g.setColor(0xffffdd);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			
			String message = "Level with this name";
			String message2 = "already exist!";
			fontBlackSmall.print(message, g, scrW/2-message.length()/2*6, scrH/2-h/2+5, 6);
			fontBlackSmall.print(message2, g, scrW/2-message2.length()/2*6, scrH/2-h/2+12, 6);
			
			g.fillRoundRect(scrW/2-w/2+3, scrH/2-h/2+24+askRewritePosition*8, w-6, 7, 3, 3);
			
			for(int i=0; i<askRewritePositions.size(); i++)
			{
				if(askRewritePosition == i)
				{
					fontWhiteSmall.print((String) askRewritePositions.elementAt(i), g, scrW/2-w/2+6, scrH/2-h/2+25+i*8, 6);
				}
				else
				{
					fontBlackSmall.print((String) askRewritePositions.elementAt(i), g, scrW/2-w/2+6, scrH/2-h/2+25+i*8, 6);
				}
			}
		}
		
		
		if(notification)					//Хорошо бы нотификейшены сделать отдельным классом, но лень
		{
			int scrH = 320;
			int scrW = 240;
			int h = 50;
			int w = notificationText.length()*6 + 10;
			
			g.setColor(0xffffdd);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			
			fontBlackSmall.print(notificationText, g, scrW/2-(notificationText.length()*6)/2, scrH/2-h/2+10, 6);
			fontBlackSmall.print("Okay", g, scrW/2-12, scrH/2+h/2-10, 6);
		}
		
		if(instrumentMenuOpened == true)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 60;
			int w = 142;
			
			g.setColor(0xffffff);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.fillRoundRect(scrW-scrW/2-instrumentNames[instrument].length()*6/2-6, scrH-scrH/2-h/2-12, instrumentNames[instrument].length()*6+10, 9, 5, 5);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.drawRoundRect(scrW-scrW/2-instrumentNames[instrument].length()*6/2-6, scrH-scrH/2-h/2-12, instrumentNames[instrument].length()*6+10, 9, 5, 5);
			
			fontBlackSmall.print(instrumentNames[instrument], g, scrW-scrW/2-instrumentNames[instrument].length()*5/2-3, scrH-scrH/2-h/2-10, 6);
			
			g.drawImage(s_wall, scrW/2-w/2+10, scrH/2-h/2+10, 0);
			g.drawImage(s_player, scrW/2-w/2+37, scrH/2-h/2+10, 0);
			g.drawImage(s_exit, scrW/2-w/2+63, scrH/2-h/2+10, 0);
			g.drawImage(s_gem, scrW/2-w/2+89, scrH/2-h/2+10, 0);
			g.drawImage(s_coin, scrW/2-w/2+115, scrH/2-h/2+10, 0);
			g.drawImage(s_pickup, scrW/2-w/2+10, scrH/2-h/2+36, 0);
			g.drawImage(s_monster, scrW/2-w/2+37, scrH/2-h/2+36, 0);
			g.drawImage(s_spike, scrW/2-w/2+63, scrH/2-h/2+36, 0);
			g.drawImage(s_lever, scrW/2-w/2+89, scrH/2-h/2+36, 0);
			g.drawImage(s_plate, scrW/2-w/2+115, scrH/2-h/2+36, 0);
			
			int insY = instrument / 5;
			int insX = instrument % 5;
			
			g.drawRoundRect(scrW/2-w/2+8+insX*26, scrH/2-h/2+8+insY*26, 20, 20, 5, 5);
			//g.fillRoundRect(8+offsetX, scrH+offsetY-(h-(menuPosition*15))+9, w-16, 12, 5, 5);
		}
		
		if(levelSettingsOpened == true)
		{
			int scrH = 320;
			int scrW = 240;
			int h = 48;
			int w = 145;
			
			g.setColor(0xffffff);
			g.fillRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(scrW/2-w/2, scrH/2-h/2, w, h, 10, 10);

			g.fillRoundRect(scrW/2-w/2+3, scrH-scrH/2-h/2+4+levelSettingsPosition*7, w-6, 7, 3, 3);

			for(int i=0; i<levelSettingsPositions.size(); i++)
			{
				if(i == levelSettingsPosition)
				{
					fontWhiteSmall.print((String)levelSettingsPositions.elementAt(i), g, scrW/2-w/2+5, scrH/2-h/2+5+i*7, 6);
					fontWhiteSmall.print(Integer.toString(levelSettingsValues[i]), g, scrW/2-w/2+115, scrH/2-h/2+5+i*7, 6);
				}
				else
				{
					fontBlackSmall.print((String)levelSettingsPositions.elementAt(i), g, scrW/2-w/2+5, scrH/2-h/2+5+i*7, 6);
					fontBlackSmall.print(Integer.toString(levelSettingsValues[i]), g, scrW/2-w/2+115, scrH/2-h/2+5+i*7, 6);
				}
			}
		}
		
		if(menuOpened == true)
		{
			int scrH = 320;
			int h = 105;
			int w = 160;
			int offsetX = 0;
			int offsetY = -4;
			
			g.setColor(0xffffff);
			g.fillRoundRect(0+offsetX, scrH-h+offsetY, w+offsetX, h+offsetY, 10, 10);
			g.setColor(0x000000);
			g.drawRoundRect(0+offsetX, scrH-h+offsetY, w+offsetX, h+offsetY, 10, 10);
			
			g.fillRoundRect(8+offsetX, scrH+offsetY-(h-(menuPosition*15))+9, w-16, 12, 5, 5);
			
			for(int i=0; i<menuPositions.size(); i++)
			{
				if(i == menuPosition)
					fontWhite.print(((String) menuPositions.elementAt(i)), g, 10+offsetX, scrH+offsetY-(h-(i*15))+10, 0);
				else
					fontBlack.print(((String) menuPositions.elementAt(i)), g, 10+offsetX, scrH+offsetY-(h-(i*15))+10, 0);
			}
		}
		
		flushGraphics();
	}
	
	private int getBuildInLevelsAmount()
	{
		int amount = 0;
		InputStreamReader is = null;
		boolean go = true;
		
		while(go)
		{
			try
			{
				is = new InputStreamReader(getClass().getResourceAsStream("/levels/level_"+(amount+1)+".lvl"));
				amount++;
			}
			catch (NullPointerException e)
			{
				go = false;
			}
		}
		
		if(is != null)
			is = null;
		
		return amount;
	}
	
	private boolean checkSaveAbility()
	{
		if(coins.size() < levelSettingsValues[4])
		{
			warning("Please add "+(levelSettingsValues[4]-coins.size())+" more coins");
			return false;
		}
		else if(gems.size() < levelSettingsValues[3])
		{
			warning("Please add "+(levelSettingsValues[3]-gems.size())+" more gems");
			return false;
		}
		else if(exit == null)
		{
			warning("Please add exit");
			return false;
		}
		else if(player == null)
		{
			warning("Please add player");
			return false;
		}
		
		return true;
	}
	
	private boolean compile()
	{
		if(checkSaveAbility() == false)
			return false;
		else
		{
			level = "";

			for(int j=0; j<40; j++)
			{
				for(int i=0; i<30; i++)
				{
					if(walls.getCell(i, j) == 0)
					{
						level = level + "0";
					}
					else
					{
						level = level + "1";
					}
				}

				level = level + "\r\n";
			}

			int x = player.getX()/8;
			int y = player.getY()/8;
			level = level.substring(0, y*32+x) + "3" + level.substring(y*32+x+1, level.length());

			x = exit.getX()/8;
			y = exit.getY()/8;
			level = level.substring(0, y*32+x) + "7" + level.substring(y*32+x+1, level.length());

			for(int i=0; i<monsters.size(); i++)
			{
				x = ((Coordinates) monsters.elementAt(i)).getX()/8;
				y = ((Coordinates) monsters.elementAt(i)).getY()/8;
				level = level.substring(0, y*32+x) + "9" + level.substring(y*32+x+1, level.length());
			}

			for(int i=0; i<coins.size(); i++)
			{
				x = ((Coordinates) coins.elementAt(i)).getX()/8;
				y = ((Coordinates) coins.elementAt(i)).getY()/8;
				level = level.substring(0, y*32+x) + "2" + level.substring(y*32+x+1, level.length());
			}

			for(int i=0; i<gems.size(); i++)
			{
				x = ((Coordinates) gems.elementAt(i)).getX()/8;
				y = ((Coordinates) gems.elementAt(i)).getY()/8;
				level = level.substring(0, y*32+x) + "5" + level.substring(y*32+x+1, level.length());
			}

			for(int i=0; i<pickups.size(); i++)
			{
				x = ((Coordinates) pickups.elementAt(i)).getX()/8;
				y = ((Coordinates) pickups.elementAt(i)).getY()/8;
				level = level.substring(0, y*32+x) + "6" + level.substring(y*32+x+1, level.length());
			}

			level = level + "background=" + Integer.toString(levelSettingsValues[0]) + "\r";
			level = level + "smartLevel=" + Double.toString((double)levelSettingsValues[1]/(double)100) + "\r";
			level = level + "monstersSpeed=" + Integer.toString(levelSettingsValues[2]) + "\r";
			level = level + "gems=" + Integer.toString(levelSettingsValues[3]) + "\r";
			level = level + "coins=" + Integer.toString(levelSettingsValues[4]) + "\r";
			level = level + "timeLimit=" + Integer.toString(levelSettingsValues[5]) + "\r";

			int autoSpikesCount = 0;
			for(int i=0; i<spikes.size(); i++)
			{
				if(((SpikesEditor) spikes.elementAt(i)).isAuto() == true)
					autoSpikesCount++;
			}

			if(autoSpikesCount > 0)
			{
				level = level + "\n#AUTO-SPIKES#\n@@";
				for(int i=0; i<spikes.size(); i++)
				{
					if(((SpikesEditor) spikes.elementAt(i)).isAuto() == true)
					{
						level = level + Integer.toString(((SpikesEditor) spikes.elementAt(i)).getX()) + ",";
						level = level + Integer.toString(((SpikesEditor) spikes.elementAt(i)).getY()) + ",";
						level = level + Integer.toString(((SpikesEditor) spikes.elementAt(i)).getOpen()) + ",";
						level = level + Integer.toString(((SpikesEditor) spikes.elementAt(i)).getClose()) + ",";
						if(((SpikesEditor) spikes.elementAt(i)).isOpenedState() == true)
							level = level + "true,";
						else
							level = level + "false,";
						level = level + Integer.toString(((SpikesEditor) spikes.elementAt(i)).getCurrent()) + "@";
					}
				}
				level = level + "@\n#AUTO-SPIKES#\n";
			}

			if(levers.size() > 0)
			{
				level = level + "\n#MANUAL-SWITCH#\n@@";
				for(int i=0; i<levers.size(); i++)
				{
					level = level + Integer.toString(((LeverEditor) levers.elementAt(i)).getX()) + ",";
					level = level + Integer.toString(((LeverEditor) levers.elementAt(i)).getY()) + ",";
					if(((LeverEditor) levers.elementAt(i)).getState() == true)
						level = level + "true@";
					else
						level = level + "false@";
				}
				level = level + "@\n#MANUAL-SWITCH#\n";
			}

			int manualSpikesCount = 0;
			for(int i=0; i<levers.size(); i++)
			{
				manualSpikesCount += ((LeverEditor) levers.elementAt(i)).getChildsSpikes().size();	
			}

			if(manualSpikesCount > 0)
			{
				level = level + "\n#MANUAL-SPIKES#\n@@";
				for(int i=0; i<levers.size(); i++)
				{
					Vector childs = ((LeverEditor) levers.elementAt(i)).getChildsSpikes();

					if(childs.size() == 0)
					{
						level = level + " ";
					}
					else
					{
						for(int j=0; j<childs.size(); j++)
						{
							int index = -1;
							for(int k=0; k<spikes.size(); k++)
							{
								if(((SpikesEditor) spikes.elementAt(k)).getX() == ((Coordinates) childs.elementAt(j)).getX() && ((SpikesEditor) spikes.elementAt(k)).getY() == ((Coordinates) childs.elementAt(j)).getY())
								{
									index = k;
									break;
								}
							}

							level = level + Integer.toString(((SpikesEditor) spikes.elementAt(index)).getX()) + ",";
							level = level + Integer.toString(((SpikesEditor) spikes.elementAt(index)).getY()) + ",";
							if(((SpikesEditor) spikes.elementAt(index)).isOpenedState() == true)
								level = level + "true";
							else
								level = level + "false";

							if(j != childs.size()-1)
								level = level + ";";
						}
					}

					level = level + "@";
				}
				level = level + "@\n#MANUAL-SPIKES#\n";
			}

			int leversWalls = 0;
			for(int i=0; i<levers.size(); i++)
			{
				leversWalls += ((LeverEditor) levers.elementAt(i)).getChildsWalls().size();
			}

			if(leversWalls > 0)
			{
				level = level + "\n#WALL-SWITCH#\n@@";
				for(int i=0; i<levers.size(); i++)
				{
					Vector childs = ((LeverEditor) levers.elementAt(i)).getChildsWalls();

					if(childs.size() == 0)
					{
						level = level + " ";
					}
					else
					{
						for(int j=0; j<childs.size(); j++)
						{
							level = level + Integer.toString(((Coordinates) childs.elementAt(j)).getX()) + ",";
							level = level + Integer.toString(((Coordinates) childs.elementAt(j)).getY());

							if(j != childs.size()-1)
								level = level + ";";
						}
					}

					level = level + "@";
				}
				level = level + "@\n#WALL-SWITCH#\n";
			}

			if(plates.size() > 0)
			{
				level = level + "\n#PREASURE-PLATE#\n@@";
				for(int i=0; i<plates.size(); i++)
				{
					level = level + Integer.toString(((PlateEditor) plates.elementAt(i)).getX()) + ",";
					level = level + Integer.toString(((PlateEditor) plates.elementAt(i)).getY()) + "@";
				}
				level = level + "@\n#PREASURE-PLATE#\n";
			}

			manualSpikesCount = 0;
			for(int i=0; i<plates.size(); i++)
			{
				manualSpikesCount += ((PlateEditor) plates.elementAt(i)).getChildsSpikes().size();	
			}

			if(manualSpikesCount > 0)
			{
				level = level + "\n#PLATE-SPIKES#\n@@";
				for(int i=0; i<plates.size(); i++)
				{
					Vector childs = ((PlateEditor) plates.elementAt(i)).getChildsSpikes();

					if(childs.size() == 0)
					{
						level = level + " ";
					}
					else
					{
						for(int j=0; j<childs.size(); j++)
						{
							int index = -1;
							for(int k=0; k<spikes.size(); k++)
							{
								if(((SpikesEditor) spikes.elementAt(k)).getX() == ((Coordinates) childs.elementAt(j)).getX() && ((SpikesEditor) spikes.elementAt(k)).getY() == ((Coordinates) childs.elementAt(j)).getY())
								{
									index = k;
									break;
								}
							}

							level = level + Integer.toString(((SpikesEditor) spikes.elementAt(index)).getX()) + ",";
							level = level + Integer.toString(((SpikesEditor) spikes.elementAt(index)).getY()) + ",";
							if(((SpikesEditor) spikes.elementAt(index)).isOpenedState() == true)
								level = level + "true";
							else
								level = level + "false";

							if(j != childs.size()-1)
								level = level + ";";
						}
					}

					level = level + "@";
				}
				level = level + "@\n#PLATE-SPIKES#\n";
			}

			int platesWalls = 0;
			for(int i=0; i<plates.size(); i++)
			{
				platesWalls += ((PlateEditor) plates.elementAt(i)).getChildsWalls().size();
			}

			if(platesWalls > 0)
			{
				level = level + "\n#WALL-PLATE#\n@@";
				for(int i=0; i<plates.size(); i++)
				{
					Vector childs = ((PlateEditor) plates.elementAt(i)).getChildsWalls();

					if(childs.size() == 0)
					{
						level = level + " ";
					}
					else
					{
						for(int j=0; j<childs.size(); j++)
						{
							level = level + Integer.toString(((Coordinates) childs.elementAt(j)).getX()) + ",";
							level = level + Integer.toString(((Coordinates) childs.elementAt(j)).getY());

							if(j != childs.size()-1)
								level = level + ";";
						}
					}

					level = level + "@";
				}
				level = level + "@\n#WALL-PLATE#\n";
			}

			System.out.println(level);
			System.out.println(level.length());

			return true;
		}
	}

	public void pause() 
	{ }	
	
	private void loadLevel(String lvlInfo)
	{
		//walls = new TiledLayer(30, 40, s_wall, 8, 8);
	    
		clear();
		
		//*********LOADING WALLS MAP*********
		int[] map = new int[1200];
	
		String lvl = null;
		String leftInfo = null;
		
		lvl = lvlInfo.substring(0, 1280);
		
		leftInfo = lvlInfo.substring(1280, lvlInfo.length());
		
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
		
		//System.out.println(leftInfo);
		
		if(leftInfo.indexOf("background=") != -1)
		{
			int index = leftInfo.indexOf("background=")+"background=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			try 
			{
				background = Image.createImage("/background/game"+Integer.toString(i)+".png");
				levelSettingsValues[0] = i;
			} 
			catch (IOException e) 
			{ System.out.println("ERROR WHILE BACKGROUND LOADING"); }
		}
		else
		{
			try 
			{
				background = Image.createImage("/background/game"+Integer.toString(new Random().nextInt(5)+1)+".png");
				levelSettingsValues[0] = 1;
			} 
			catch (IOException e) 
			{ System.out.println("ERROR WHILE BACKGROUND LOADING"); }
		}
		
		
		if(leftInfo.indexOf("smartLevel=") != -1)
		{
			int index = leftInfo.indexOf("smartLevel=")+"smartLevel=".length();
			levelSettingsValues[1] = (int) Math.floor((Double.parseDouble(leftInfo.substring(index, leftInfo.indexOf("\r", index)))*100));
		}
		
		if(leftInfo.indexOf("#AUTO-SPIKES#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#AUTO-SPIKES#")+"#AUTO-SPIKES#".length()+1) + "@@".length();
			String autoSpikess = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] sp = gameCanvas.splitString(autoSpikess, "@");
			
			for(int i=0; i<sp.length; i++)
			{
				String[] spike = gameCanvas.splitString(sp[i], ",");
				
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
				
				spikes.addElement(new SpikesEditor(r_x, r_y, r_state, true, r_open, r_close, r_start));
			}
		}
		
		if(leftInfo.indexOf("monstersSpeed=") != -1)
		{
			int index = leftInfo.indexOf("monstersSpeed=")+"monstersSpeed=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			levelSettingsValues[2] = i;
		}
		
		if(leftInfo.indexOf("gems=") != -1)
		{
			int index = leftInfo.indexOf("gems=")+"gems=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			levelSettingsValues[3] = i;
		}
		else
		{
			levelSettingsValues[3] = 5;
		}
		
		if(leftInfo.indexOf("coins=") != -1)
		{
			int index = leftInfo.indexOf("coins=")+"coins=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			levelSettingsValues[4] = i;
		}
		else
		{
			levelSettingsValues[4] = 20;
		}
		
		if(leftInfo.indexOf("timeLimit=") != -1)
		{
			int index = leftInfo.indexOf("timeLimit=")+"timeLimit=".length();
			int i = Integer.parseInt(leftInfo.substring(index, leftInfo.indexOf("\r", index)));
			levelSettingsValues[5] = i;
		}
		else
		{
			levelSettingsValues[5] = 500;
		}
		
		
		Coordinates spikeSwitch[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#MANUAL-SPIKES#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#MANUAL-SPIKES#")+"#MANUAL-SPIKES#".length()+1) + "@@".length();
			String manualSpikess = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] fsp = gameCanvas.splitString(manualSpikess, "@");
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
					String[] sp = gameCanvas.splitString(fsp[j], ";");
					Coordinates swtch[] = new Coordinates[sp.length];

					for(int i=0; i<sp.length; i++)
					{
						String[] spike = gameCanvas.splitString(sp[i], ",");

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
							if(((SpikesEditor) spikes.elementAt(q)).getX() == r_x && ((SpikesEditor) spikes.elementAt(q)).getY() == r_y)
							{
								add = false;
							}
						}
						
						if(add == true)
						{
							spikes.addElement(new SpikesEditor(r_x, r_y, r_state, false, 0, 0, 0));
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
			
			String[] fsp = gameCanvas.splitString(wallSwtch, "@");
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
					String[] wsw = gameCanvas.splitString(fsp[j], ";");
					Coordinates swtch[] = new Coordinates[wsw.length];

					for(int i=0; i<wsw.length; i++)
					{
						String[] wall = gameCanvas.splitString(wsw[i], ",");

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
			
			String[] sp = gameCanvas.splitString(manualSwitchess, "@");
			
			for(int i=0; i<sp.length; i++)
			{
				String[] swtch = gameCanvas.splitString(sp[i], ",");
				
				int r_x = Integer.parseInt(swtch[0]);
				int r_y = Integer.parseInt(swtch[1]);
				boolean r_state;
				if(swtch[2].equalsIgnoreCase("false"))
					r_state = false;
				else
					r_state = true;
				
				Vector childsSpikes = new Vector();
				Vector childsWalls = new Vector();
				
				if(spikeSwitch.length > i)
				{
					for(int j=0; j<spikeSwitch[i].length; j++)
					{
						childsSpikes.addElement(spikeSwitch[i][j]);
					}
				}
				
				if(wallSwitch.length > i)
				{
					for(int j=0; j<wallSwitch[i].length; j++)
					{
						childsWalls.addElement(wallSwitch[i][j]);
					}
				}
				
				LeverEditor le = new LeverEditor(r_x, r_y, r_state);
				le.setChildsSpikes(childsSpikes);
				le.setChildsWalls(childsWalls);
				levers.addElement(le);
			}
		}
		
		
		
		Coordinates spikePlate[][] = new Coordinates[0][];
		if(leftInfo.indexOf("#PLATE-SPIKES#") != -1)
		{
			int tempA = leftInfo.indexOf("@@", leftInfo.indexOf("#PLATE-SPIKES#")+"#PLATE-SPIKES#".length()+1) + "@@".length();
			String plateSpikes = leftInfo.substring(tempA, leftInfo.indexOf("@@", tempA+1));
			
			String[] fsp = gameCanvas.splitString(plateSpikes, "@");
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
					String[] sp = gameCanvas.splitString(fsp[j], ";");
					Coordinates plate[] = new Coordinates[sp.length];

					for(int i=0; i<sp.length; i++)
					{
						String[] spike = gameCanvas.splitString(sp[i], ",");

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
							if(((SpikesEditor) spikes.elementAt(k)).getX() == r_x && ((SpikesEditor) spikes.elementAt(k)).getY() == r_y)
								newSpike = false;
						}

						if(newSpike == true)
						{
							spikes.addElement(new SpikesEditor(r_x, r_y, r_state, false, 0, 0, 0));
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
			
			String[] fsp = gameCanvas.splitString(wallPlt, "@");
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
					String[] wpl = gameCanvas.splitString(fsp[j], ";");
					Coordinates plate[] = new Coordinates[wpl.length];

					for(int i=0; i<wpl.length; i++)
					{
						String[] wall = gameCanvas.splitString(wpl[i], ",");

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
			
			String[] sp = gameCanvas.splitString(preasurePlates, "@");
			
			for(int i=0; i<sp.length; i++)
			{
				String[] plate = gameCanvas.splitString(sp[i], ",");
				
				int r_x = Integer.parseInt(plate[0]);
				int r_y = Integer.parseInt(plate[1]);
				
				Vector childsSpikes = new Vector();
				Vector childsWalls = new Vector();
				
				if(spikePlate.length > i)
				{
					for(int j=0; j<spikePlate[i].length; j++)
					{
						childsSpikes.addElement(spikePlate[i][j]);
					}
				}
				
				if(wallPlate.length > i)
				{
					for(int j=0; j<wallPlate[i].length; j++)
					{
						childsWalls.addElement(wallPlate[i][j]);
					}
				}
				
				PlateEditor pe = new PlateEditor(r_x, r_y);
				pe.setChildsSpikes(childsSpikes);
				pe.setChildsWalls(childsWalls);
				plates.addElement(pe);
			}
		}
		
		
		//***********************************
	    
	    for (int i = 0; i < map.length; i++)
	    {
			int column = i % 30;
			int row = (i - column) / 30;
			
			if(map[i] == 1)
				walls.setCell(column, row, 1);
			
			if(map[i] == 0)
				walls.setCell(column, row, 0);
			
			if(map[i] == 7)
			{
				exit = new Coordinates(column*8, row*8);
			}
			
			if(map[i] == 3)
			{
				player = new Coordinates(column*8, row*8);
			}
			
			if(map[i] == 9)
			{
				monsters.addElement(new Coordinates(column*8, row*8));
			}
			
			if(map[i] == 2)
	    	{
	    		coins.addElement(new Coordinates(column*8, row*8));
	    	}
			
			if(map[i] == 5)
	    	{
	    		gems.addElement(new Coordinates(column*8, row*8));
	    	}
			
			if(map[i] == 6)
	    	{
	    		pickups.addElement(new Coordinates(column*8, row*8));
	    	}
	    }
	}
}