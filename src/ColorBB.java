import javax.microedition.lcdui.Image;

public final class ColorBB 
{
	public static int argb(int A, int R, int G, int B)
	{     
		byte[] colorByteArr = { (byte) A, (byte) R, (byte) G, (byte) B };
		return byteArrToInt(colorByteArr);
	}

	public static final int byteArrToInt(byte[] colorByteArr)
	{
		return (colorByteArr[0] << 24) + ((colorByteArr[1] & 0xFF) << 16) + ((colorByteArr[2] & 0xFF) << 8) + (colorByteArr[3] & 0xFF);
	}
	
	public static int getPixelColor(Image img, int x, int y)
	{
		int[] aux = new int[1];
		img.getRGB(aux, 0, img.getWidth(), x, y, 1, 1);

		return aux[0];
	}
	
	public static int[] getPixelColorArray(Image img, int x, int y)
	{
		int[] aux = new int[1];
		img.getRGB(aux, 0, img.getWidth(), x, y, 1, 1);
		
		int[] p = new int[4];
		p[0] = (int)((aux[0]&0xFF000000)>>>24); 	//Opacity level
		p[1] = (int)((aux[0]&0x00FF0000)>>>16); 	//Red level
		p[2] = (int)((aux[0]&0x0000FF00)>>>8); 		//Green level
		p[3] = (int)(aux[0]&0x000000FF); 			//Blue level

		return p;
	}
}