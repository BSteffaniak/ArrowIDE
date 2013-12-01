package net.foxycorndog.arrowide.printer;

import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Display;

public class TextPrinter
{
	private Rectangle	bounds;

	private String		text;
	
	private Font		font;

	private Printer		printer;
	
	private StyleRange	styles[];
	
	private Color		colors[];
	
	public TextPrinter(PrinterData data, String text, Font font, StyleRange styles[])
	{
		this.text   = text;
		
		this.font   = font;
		
		this.styles = styles;
		
		printer = new Printer(data);
		
		bounds  = new Rectangle(0, 0, 0, 0);
	}
	
	public boolean print()
	{
		setColors();
		
		int x = bounds.x;
		int y = bounds.y;
		
		boolean printed = false;
		
		if (printer.startJob("ArrowIDE Printer"))
		{
			GC gc = new GC(printer);
			Font printerFont = new Font(printer, font.getFontData());
			gc.setFont(printerFont);
			int lineHeight = gc.getFontMetrics().getHeight();
			int offset     = 0;
			
			if (printer.startPage())
			{
				while (offset < text.length())
				{
					if (y > bounds.height + bounds.y)
					{
						printer.endPage();
						printer.startPage();
						
						y = bounds.y;
					}
					
				
					offset = printLine(text, x, y, gc, offset);
					y += lineHeight;
					x = bounds.x;
				}
				
				printer.endPage();
			}
			
			gc.dispose();
			printer.endJob();
			
			printed = true;
		}
		
		printer.dispose();
		
		return printed;
	}
	
	private int printLine(String text, int x, int y, GC gc, int offset)
	{
		StringBuilder word = new StringBuilder();
		int i = offset;
		while (x < bounds.width + bounds.x)
		{
			if (i >= text.length())
			{
				printWord(word.toString(), x, y, gc, offset);
				
				offset = text.length();
				
				break;
			}
			
			char c = text.charAt(i);
			word.append(c);
			
			if (c == ' ')
			{
				printWord(word.toString(), x, y, gc, offset);
				
				offset += word.length();
				x += gc.stringExtent(word.toString()).x;
				
				word = new StringBuilder();
			}
			else if (c == '\n')
			{
				printWord(word.toString(), x, y, gc, offset);
				offset += word.length();
				
				break;
			}
			
			i++;
		}
		
		return offset;
	}
	
	private void printWord(String word, int x, int y, GC gc, int offset)
	{
		for (int i = 0; i < word.length(); i ++)
		{
			char c = word.charAt(i);
			
			printChar(c, x, y, gc, offset);
			
			x += gc.stringExtent(c + "").x * (c == '\t' ? 4 : 1);
		}
	}
	
	private void printChar(char c, int x, int y, GC gc, int offset)
	{
		gc.setForeground(colors[offset]);
		
		gc.drawText(c + "", x, y);
		
//		System.out.print(colors[offset]);
	}
	
	private void setColors()
	{
		colors = new Color[text.length()];
		
		Color defaultColor = new Color(Display.getDefault(), 0, 0, 0);
		
		Color color        = null;
		
		for (int i = 0; i < text.length(); i ++)
		{
			for (int j = 0; j < styles.length; j ++)
			{
				StyleRange style = styles[j];
				
				if (i >= style.start && i - style.start < style.length)
				{
					color = style.foreground;
				
					if (!color.equals(defaultColor))
					{
//						System.out.println(style.foreground);
					}
					else
					{
						color = null;
					}
				}
			}
			
			if (color != null)
			{
				colors[i] = color;
			}
			else
			{
				colors[i] = defaultColor;
			}
		}
	}
	
	public void setMargins(float left, float top, float right, float bottom)
	{
		Rectangle rect = printer.getClientArea();
		Rectangle trim = printer.computeTrim(0, 0, 0, 0);
		Point dpi = printer.getDPI();
		
		int left2 = trim.x + (int)(dpi.x * left);
		if (left2 < rect.x)
		{
			left2 = rect.x;
		}
		
		int right2 = rect.width + trim.x + trim.width - (int)(dpi.x * right);
		if (right2 > rect.width)
		{
			right2 = rect.width;
		}
		
		int top2 = trim.y + (int)(dpi.y * top);
		if (top2 < rect.y)
		{
			top2 = rect.y;
		}
		
		int bottom2 = rect.height + trim.y + trim.height - (int)(dpi.y * bottom);
		if (bottom2 > rect.height)
		{
			bottom2 = rect.height;
		}
		
		bounds = new Rectangle(left2, top2, right2 - left2, bottom2 - top2);
	}
}