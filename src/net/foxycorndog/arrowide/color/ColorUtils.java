package net.foxycorndog.arrowide.color;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

/**
 * Class that is used to manipulate and store color data.
 * 
 * @author	Braden Steffaniak
 * @since	Nov 23, 2013 at 5:24:00 PM
 * @since	v0.7.5
 * @version Nov 23, 2013 at 5:24:00 PM
 * @version	v0.7.6
 */
public class ColorUtils
{
    /**
     * Method used to darken the specified color the specified
     * amount over all.
     * 
     * @param color The color to use as a reference to darken.
     * @param amount The 256 bit value to darken the color by.
     * @return The darkened Color instance.
     */
	public static Color darken(Color color, int amount)
	{
		return darken(color, amount, amount, amount);
	}
	
    /**
     * Method used to darken the specified color the specified
     * amount toward each color bit.
     * 
     * @param color The color to use as a reference to darken.
     * @param rAmount The 256 bit value to darken the red bit by.
     * @param gAmount The 256 bit value to darken the green bit by.
     * @param bAmount The 256 bit value to darken the blue bit by.
     * @return The darkened Color instance.
     */
	public static Color darken(Color color, int rAmount, int gAmount, int bAmount)
	{
		return lighten(color, -rAmount, -gAmount, -bAmount);
	}
	
    /**
     * Method used to lighten the specified color the specified
     * amount over all.
     * 
     * @param color The color to use as a reference to lighten.
     * @param amount The 256 bit value to lighten the color by.
     * @return The lightened Color instance.
     */
	public static Color lighten(Color color, int amount)
	{
		return lighten(color, amount, amount, amount);
	}
	
    /**
     * Method used to lighten the specified color the specified
     * amount toward each color bit.
     * 
     * @param color The color to use as a reference to lighten.
     * @param rAmount The 256 bit value to lighten the red bit by.
     * @param gAmount The 256 bit value to lighten the green bit by.
     * @param bAmount The 256 bit value to lighten the blue bit by.
     * @return The lightened Color instance.
     */
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