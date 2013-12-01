package net.foxycorndog.arrowide.components;

import net.foxycorndog.arrowide.ArrowIDE;

import org.eclipse.swt.custom.BidiSegmentEvent;
import org.eclipse.swt.custom.BidiSegmentListener;
import org.eclipse.swt.custom.CaretEvent;
import org.eclipse.swt.custom.CaretListener;
import org.eclipse.swt.custom.PaintObjectEvent;
import org.eclipse.swt.custom.PaintObjectListener;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.DragDetectEvent;
import org.eclipse.swt.events.DragDetectListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class LineNumberPanel extends Composite
{
	private int			leftMargin, rightMargin;
	
	private String		prefix, postfix;
	
	private GC			gc;
	private GC			bufferGC;
	
	private Image		buffer;
	
	private CodeField	field;
	
	private Color		backgroundColor;
	
	public LineNumberPanel(Composite parent, int param, final CodeField field)
	{
		super(parent, param);
		
		this.field = field;
		
		this.backgroundColor = new Color(Display.getDefault(), 220, 220, 220);
		
		setPrefix("");
		setPostfix("");
		
		final LineNumberPanel thisPanel = this;
		
		field.addPaintListener(new PaintListener()
		{
			int topPixel = 0;
			
			public void paintControl(PaintEvent e)
			{
				if (topPixel != field.getTopPixel())
				{
					redraw();
					
					topPixel = field.getTopPixel();
				}
			}
		});
		
		field.addControlListener(new ControlListener()
		{
			public void controlMoved(ControlEvent e)
			{
//				thisPanel.setLocation(field.getX() - thisPanel.getSize().x, field.getY());
			}

			public void controlResized(ControlEvent e)
			{
				thisPanel.setSize(thisPanel.getSize().x, field.getClientArea().height);
				field.getSize();
				
				updateSize();
			}
		});
		
		field.addModifyListener(new ModifyListener()
		{
			int lineNums = 0;
			
			public void modifyText(ModifyEvent e)
			{
				if (lineNums != field.getLineCount())
				{
					updateSize();
				}
				
				lineNums = field.getLineCount();
			}
		});
		
		field.getVerticalBar().addSelectionListener(new SelectionListener()
		{
			public void widgetSelected(SelectionEvent e)
			{
				redraw();
			}
			
			public void widgetDefaultSelected(SelectionEvent e)
			{
				widgetSelected(e);
			}
		});
		
		int charWidth = getCharWidth();
		
		setSize(charWidth, field.getClientArea().height);
		setLocation(field.getX(), field.getY());
		
		field.setLocation(field.getLocation().x + getSize().x, field.getLocation().y);
		field.setSize(field.getWidth() - getSize().x, field.getHeight());
		
		createBuffer();
		drawBuffer();
		
		addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				e.gc.drawImage(buffer, 0, -field.getTopPixel());
				
				e.gc.dispose();
			}
		});
	}
	
	private void updateSize()
	{
		int preLength = 0;
		
		if (prefix != null)
		{
			preLength = prefix.length();
		}

		int postLength = 0;
		
		if (postfix != null)
		{
			postLength = postfix.length();
		}
		
		int length   = preLength + String.valueOf(field.getLineCount()).length() + postLength;
		
		int newWidth = length * getCharWidth() + leftMargin + rightMargin;
		
		if (newWidth != getSize().x)
		{
			setSize(newWidth, getSize().y);
		}
		
		createBuffer();
		drawBuffer();
		
		redraw();
	}
	
	private void drawBuffer()
	{
		if (buffer == null)
		{
			return;
		}
		
		int height = getCharHeight();

        bufferGC.setBackground(backgroundColor);
		bufferGC.fillRectangle(0, 0, buffer.getImageData().width, buffer.getImageData().height);
		
		int charWidth = getCharWidth();

		int preLength = 0;
		
		if (prefix != null)
		{
			preLength = prefix.length();
		}

		int postLength = 0;
		
		if (postfix != null)
		{
			postLength = postfix.length();
		}
		
		int maxLength = preLength + String.valueOf(field.getLineCount()).length() + postLength;
		
		for (int i = 0; i < field.getLineCount(); i++)
		{
			int length = preLength + String.valueOf((i + 1)).length() + postLength;
			
			int dif    = maxLength - length;
			
			bufferGC.drawString(prefix + (i + 1) + postfix, dif * charWidth + leftMargin, height * i + 1);
		}
	}
	
	private void createBuffer()
	{
		if (getSize().x <= 0 || getSize().y <= 0)
		{
			return;
		}
		
		int height = Math.max(field.getLineCount() * getCharHeight(), getSize().y);
		
		PaletteData palette = new PaletteData(0xFF , 0xFF00 , 0xFF0000);
		ImageData data = new ImageData(getSize().x, height, 24, palette);
		buffer = new Image(Display.getDefault(), data);
		buffer.setBackground(backgroundColor);
		
		bufferGC = new GC(buffer);
		bufferGC.setFont(field.getFont());
		bufferGC.setBackground(backgroundColor);
	}
	
	public int getCharWidth()
	{
		GC          g         = new GC(field);
	    FontMetrics fm        = g.getFontMetrics();
	    int         charWidth = fm.getAverageCharWidth();
	    
	    g.dispose();
	    
	    return charWidth;
	}
	
	public int getCharHeight()
	{
		int offset = 0;
		
		if (ArrowIDE.PROPERTIES.get("os.name").equals("windows"))
		{
			offset = 1;
		}
		
		GC g = new GC(field);
	    FontMetrics fm = g.getFontMetrics();
	    int charHeight = fm.getHeight() + offset;
	    g.dispose();
	    
	    return charHeight;
	}
	
	public void setPrefix(String prefix)
	{
		this.prefix = prefix;
		
		updateSize();
	}
	
	public void setPostfix(String postfix)
	{
		this.postfix = postfix;
		
		updateSize();
	}
	
	public int getLeftMargin()
	{
		return leftMargin;
	}
	
	public void setLeftMargin(int leftMargin)
	{
		this.leftMargin = leftMargin;
		
		updateSize();
	}
	
	public int getRightMargin()
	{
		return rightMargin;
	}
	
	public void setRightMargin(int rightMargin)
	{
		this.rightMargin = rightMargin;
		
		updateSize();
	}
	
	public void setMargin(int leftMargin, int rightMargin)
	{
		setLeftMargin(leftMargin);
		setRightMargin(rightMargin);
		
		updateSize();
	}
}