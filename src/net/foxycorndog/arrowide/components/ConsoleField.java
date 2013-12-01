package net.foxycorndog.arrowide.components;

import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;
import net.foxycorndog.arrowide.file.FileUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

public class ConsoleField extends StyledText
{
//	private boolean textUpdated;
//
//	private ArrayList<String> appendQueue;
	
	public ConsoleField(Composite parent)
	{
		super(parent, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | (Integer)PROPERTIES.get("composite.modifiers"));
		
//		appendQueue           = new ArrayList<String>();
	    
		int fontSize = 10;
		
	    if (PROPERTIES.get("os.name").equals("macosx"))
	    {
	    	fontSize = 15;
	    }
		
		Font f = FileUtils.loadMonospacedFont(Display.getDefault(), "courier new", PROPERTIES.get("resources.location") + "res/fonts/CECOUR.ttf", fontSize, SWT.NORMAL);
	    setFont(f);
		
		addPaintListener(new PaintListener()
	    {
			public void paintControl(PaintEvent e)
			{
//				synchronized (appendQueue)
//				{
//					while (appendQueue.size() > 0)
//					{
//						append(appendQueue.remove(0), true);
//					}
//				}
			}
	    });
	}
	
	public int getWidth()
	{
		return getBounds().width;
	}
	
	public int getHeight()
	{
		return getBounds().height;
	}
	
	public void append(String string)
	{
		super.append(string);

		moveTextToTop();
	}
	
	public void setText(String text)
	{
		super.setText(text);

		moveTextToTop();
	}
	
	private void moveTextToTop()
	{
		int lines = getHeight() / getLineHeight();
		
		setTopIndex(getLineCount() - lines);
	}
	
//	public void append(String s)
//	{
//		append(s, false);
//	}
//	
//	public void append(String s, boolean sup)
//	{
//		if (sup)
//		{
//			super.append(s);
//			redraw();
//		}
//		else
//		{
//			synchronized (appendQueue)
//			{
//				appendQueue.add(s);
//			}
//			
//			textUpdated = true;
//		}
//	}
//	
//	public void updateText()
//	{
//		if (textUpdated)
//		{
//			redraw();
//			
//			textUpdated = false;
//		}
//	}
}