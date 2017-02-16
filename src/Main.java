
import javax.microedition.midlet.*;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;

public class Main extends MIDlet
{

	private mGameCanvas gCanvas;
	private Display display = Display.getDisplay(this);

	public Main() 
	{
		gCanvas = new StalkerCanvas(this);
	}
	
	public void changeCanvas(mGameCanvas canvas)
	{
		gCanvas = canvas;
		startApp();
	}
	
	public void startApp() 
	{
		gCanvas.start();
		display.setCurrent((Displayable) gCanvas);
	}

	public void pauseApp() 
	{
		display.flashBacklight(1000);
		gCanvas.pause();
		destroyApp(true);
	}

	public void destroyApp(boolean unconditional) 
	{
		if(gCanvas.getCurrentLevel() > 1)
		{
			gCanvas.saveGame();
		}
		
		display.setCurrent(null);
		notifyDestroyed();
	}
}