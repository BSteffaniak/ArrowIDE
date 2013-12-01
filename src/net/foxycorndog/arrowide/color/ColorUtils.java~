package net.foxycorndog.arrowide.color;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

public class ColorUtils
{
	public static Color darken(Color color, int amount)
	{
		return darken(color, amount, amount, amount);
	}
	
	public static Color darken(Color color, int rAmount, int gAmount, int bAmount)
	{
		return lighten(color, -rAmount, -gAmount, -bAmount);
	}
	
	public static Color lighten(Color color, int amount)
	{
		return lighten(color, amount, amount, amount);
	}
	
	public static Color lighten(Color color, int rAmount, int gAmount, int bAmount)
	{
		int r = color.getRed()   + rAmount;
		int g = color.getGreen() + gAmount;
		int b = color.getBlue()  + bAmount;
		
		r = r > 255 ? 255 : r;
		g = g > 255 ? 255 : g;
		b = b > 255 ? 255 : b;
		
		r = r < 0 ? 0 : r;
		g = g < 0 ? 0 : g;
		b = b < 0 ? 0 : b;
		
		return new Color(Display.getDefault(), r, g, b);
	}
}