package net.foxycorndog.arrowide.components;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Tracker;

public class CompositeSizer
{
	private boolean						maxXSet, maxYSet;
	
	private int							oldX, oldY, startX, startY;
	private int							minX, minY, maxX, maxY;
	
	private Composite					composite;
	
	private ArrayList<SizerListener>	listeners;
	
	public static final int				HORIZONTAL = 1, VERTICAL = 2;
	
	public CompositeSizer(final Composite parent, final int axis)
	{
		composite = new Composite(parent, SWT.NONE);
		
		listeners = new ArrayList<SizerListener>();
		
		maxX = parent.getSize().x;
		maxY = parent.getSize().y;
		
		composite.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				e.gc.setBackground(composite.getForeground());
				
				int x      = 0;
				int y      = 0; 
				int width  = 0;
				int height = 0;
				
				if (axis == VERTICAL)
				{
					int thirdWF = (int)Math.floor(composite.getSize().x / 3f);
					int thirdWC = (int)Math.ceil(composite.getSize().x / 3f);
					
					x      = thirdWC;// + (composite.getSize().x % 2);
					width  = thirdWF;
					height = composite.getSize().y;
				}
				else if (axis == HORIZONTAL)
				{
					int thirdHF = (int)Math.floor(composite.getSize().y / 3f);
					int thirdHC = (int)Math.ceil(composite.getSize().y / 3f);
					
					y       = thirdHC;// + (composite.getSize().y % 2);
					width   = composite.getSize().x;
					height  = thirdHF;
				}
				
				e.gc.fillRectangle(x, y, width, height);
			}
		});
		
		final Tracker tracker = new Tracker(parent.getShell(), SWT.NONE);
		
		tracker.addControlListener(new ControlListener()
		{
			public void controlResized(ControlEvent e)
			{
				
			}
			
			public void controlMoved(ControlEvent e)
			{
				Point loc = new Point(tracker.getRectangles()[0].x + 99999, tracker.getRectangles()[0].y + 99999);
				
				int cX   = composite.getLocation().x;
				int pX   = parent.getLocation().x;
				int cY   = composite.getLocation().y;
				int pY   = parent.getLocation().y;
				
				int cWid = composite.getSize().x;
				int pWid = parent.getSize().x;
				int cHei = composite.getSize().y;
				int pHei = parent.getSize().y;
				
				int dx   = axis == VERTICAL   ? loc.x - oldX : 0;
				int dy   = axis == HORIZONTAL ? loc.y - oldY : 0;
				
				int newX = composite.getLocation().x + dx;
				int newY = composite.getLocation().y + dy;
				
				if ((axis == VERTICAL && (((startX + loc.x >= minX || dx < 0) && (startX + loc.x + cWid < maxX || dx > 0)))) || (axis == HORIZONTAL && (((startY + loc.y >= minY || dy < 0) && (startY + loc.y + cHei < maxY || dy > 0)))))
				{
					if (axis == VERTICAL)
					{
						if (newX < minX)
						{
							newX = minX;
							dx   = newX - cX;
						}
						else if (newX + cWid >= maxX)
						{
							newX = maxX - cWid;
							dx   = newX - cX;
						}
						else
						{
							if (dx > startX + loc.x - minX)
							{
								int j = dx - (startX + loc.x - minX);
								
//								System.out.println(dx + ", " + j);
								// TODO: fix this
								dx   -= j;
								newX -= j;
							}
							else if (dx <= startX + loc.x + cWid - maxX)
							{
								int j = dx + (startX + loc.x + (cWid * 2) - maxX);
								
//								System.out.println(dx + ", " + j);
								
								dx   -= j;
								newX -= j;
							}
						}
					}
					
					if (axis == HORIZONTAL)
					{
						if (newY < minY)
						{
							newY = minY;
							dy   = newY - cY;
						}
						else if (newY + cHei >= maxY)
						{
							newY = maxY - cHei;
							dy   = newY - cY;
						}
						else
						{
							if (dy > startY + loc.y - minY)
							{
								int j = dy - (startY + loc.y - minY);
								
//								System.out.println(dy + ", " + j);
								
								dy   -= j;
								newY -= j;
							}
						}
					}
					
					if (dx != 0 || dy != 0)
					{
						composite.setLocation(newX, newY);
					
						notifyListeners(dx, dy);
					}
				}
					
				oldX = loc.x;
				oldY = loc.y;
			}
		});
		
		composite.addListener(SWT.MouseDown, new Listener()
		{
			public void handleEvent(Event e)
			{
				oldX = 0;
				oldY = 0;
				
				startX = composite.getLocation().x - e.x;
				startY = composite.getLocation().y - e.y;
				
				tracker.setRectangles(new Rectangle[] { new Rectangle(-99999, -99999, 0, 0) });
				tracker.open();
				tracker.close();
			}
		});
		
		parent.addControlListener(new ControlListener()
		{
			public void controlResized(ControlEvent e)
			{
				if (!maxXSet)
				{
					maxX = parent.getSize().x;
				}
				if (!maxYSet)
				{
					maxY = parent.getSize().y;
				}
			}
			
			public void controlMoved(ControlEvent e)
			{
				
			}
		});
	}
	
	private void notifyListeners(int dx, int dy)
	{
		for (int i = listeners.size() - 1; i >= 0; i--)
		{
			listeners.get(i).sizerMoved(dx, dy);
		}
	}
	
	public void setMinimumX(int minX)
	{
		this.minX = minX;
	}
	
	public void setMinimumY(int minY)
	{
		this.minY = minY;
	}
	
	public void setMaximumX(int maxX)
	{
		this.maxX = maxX;
		maxXSet   = true;
	}
	
	public void setMaximumY(int maxY)
	{
		this.maxY = maxY;
		maxYSet   = true;
	}
	
	public void addSizerListener(SizerListener listener)
	{
		listeners.add(listener);
	}
	
	public void setVisible(boolean visible)
	{
		composite.setVisible(visible);
	}
	
	public void setEnabled(boolean enabled)
	{
		composite.setEnabled(enabled);
	}
	
	public void setSize(int width, int height)
	{
		composite.setSize(width, height);
	}
	
	public void setLocation(int x, int y)
	{
		composite.setLocation(x, y);
	}
	
	public void setBackground(Color color)
	{
		composite.setBackground(color);
	}
	
	public void setForeground(Color color)
	{
		composite.setForeground(color);
	}
	
	public int getWidth()
	{
		return composite.getSize().x;
	}
	
	public int getHeight()
	{
		return composite.getSize().y;
	}
}