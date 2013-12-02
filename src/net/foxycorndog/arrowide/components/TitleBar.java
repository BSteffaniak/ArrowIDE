package net.foxycorndog.arrowide.components;

import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;

import java.awt.MouseInfo;

import net.foxycorndog.arrowide.color.ColorUtils;
import net.foxycorndog.arrowide.components.window.Window;
import net.foxycorndog.arrowide.components.window.WindowListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;

public class TitleBar
{
	private boolean			dragging, dontSetX, wasRestored, wasMaximized;
	
	private int				startX, startY, mouseX, mouseY, dx, dy, oldX, oldY;
	private int				flags;
	
	private String			title;
	
	private Color			hoverColor, normalColor;

	private GC				titleGC;
	
	private Composite		composite;

	private Label			closeButton, restoreButton, minimizeButton;
	private Label			iconLabel;

	private Listener		buttonListener;

	private static Image	closeImage, restoreImage, maximizeImage, minimizeImage;
	
	static
	{
		closeImage    = new Image(Display.getCurrent(), PROPERTIES.get("resources.location") + "res/images/closeimage.png");
		restoreImage  = new Image(Display.getCurrent(), PROPERTIES.get("resources.location") + "res/images/restoreimage.png");
		maximizeImage = new Image(Display.getCurrent(), PROPERTIES.get("resources.location") + "res/images/maximizeimage.png");
		minimizeImage = new Image(Display.getCurrent(), PROPERTIES.get("resources.location") + "res/images/minimizeimage.png");
	}
	
	public TitleBar(final Window parent, final int size, int flags)
	{
		this.flags = flags;
		
		composite = new Composite(parent.getContentPanel(), SWT.NONE);
		
		composite.setSize(parent.getClientArea().width, size);
		
		buttonListener = new Listener()
		{
			public void handleEvent(Event event)
			{
				if (event.type == SWT.MouseUp && event.button == 1)
				{
					// If the click is still on the button.
					if (event.x >= 0 && event.x < size && event.y >= 0 && event.y < size)
					{
						if (event.widget == closeButton)
						{
							parent.close();
						}
						else if (event.widget == restoreButton)
						{
							dontSetX = true;
							parent.setMaximized(!parent.isMaximized());
						}
						else if (event.widget == minimizeButton)
						{
							parent.setMinimized(true);
						}
					}
				}
				else if (event.type == SWT.MouseEnter)
				{
					Label button = (Label)event.widget;
					
					button.setBackground(hoverColor);
				}
				else if (event.type == SWT.MouseExit)
				{
					Label button = (Label)event.widget;
					
					button.setBackground(normalColor);
				}
			}
		};
		
		addButtons();
		layoutButtons();
		
		FontData fd = Display.getDefault().getSystemFont().getFontData()[0];
		fd.setHeight(14);
		composite.setFont(new Font(Display.getDefault(), fd));
		
		composite.addPaintListener(new PaintListener()
		{
			public void paintControl(PaintEvent e)
			{
				Point extent = e.gc.textExtent(title);
				
				int   width  = extent.x;
				int   height = extent.y;
				
				e.gc.drawString(title, composite.getSize().x / 2 - width / 2, composite.getSize().y / 2 - height / 2);
				
				e.gc.dispose();
			}
		});
		
		titleGC    = new GC(composite);
		setTitle(parent.getTitle());
		
		iconLabel = new Label(composite, SWT.NONE);
		setIcon(parent.getIcon());
		
		dontSetX = true;
		wasRestored = true;
		wasMaximized = parent.isMaximized();
		
		ControlListener resizeListener = new ControlListener()
		{
			public void controlResized(ControlEvent e)
			{
				composite.setSize(parent.getClientArea().width, size);
				
				layoutButtons();
				
				if (parent.isMaximized())
				{
					if (restoreButton != null)
					{
						restoreButton.setImage(restoreImage);
					}
				}
				else
				{
					if (wasMaximized && !dontSetX)
					{
						//parent.setLocation(mouseX - parent.getSize().x / 2, parent.getLocation().y);
					}
					
					if (restoreButton != null)
					{
						restoreButton.setImage(maximizeImage);
					}
				}
				
				dontSetX = false;
				
				wasRestored = false;
				
				wasMaximized = false;
				
				if (parent.isFullscreen())
				{
					if (minimizeButton != null)
					{
						minimizeButton.setEnabled(false);//.setVisible(false);
					}
					if (restoreButton != null)
					{
						restoreButton.setEnabled(false);//.setVisible(false);
					}
				}
				else
				{
					if (minimizeButton != null)
					{
						minimizeButton.setEnabled(true);//.setVisible(true);
					}
					if (restoreButton != null)
					{
						restoreButton.setEnabled(true);//.setVisible(true);
					}
				}
			}
			
			public void controlMoved(ControlEvent e)
			{
				
			}
		};
		
		parent.addControlListener(resizeListener);
		
		final Listener moveListener = new Listener()
		{
			public void handleEvent(Event e)
			{
				if (dragging && parent.isMovable())
				{
					int x = e.x;
					int y = e.y;
					
					dx = x - oldX;
					dy = y - oldY;
					
					oldX = x - dx;
					oldY = y - dy;
					
					if (parent.isMaximized())
					{
						parent.setMaximized(false);
						
						Control item = (Control)e.widget;
						
						int offsetX = item.getSize().x / 2;
						int offsetY = item.getSize().y / 2;
						
						java.awt.Point loc = MouseInfo.getPointerInfo().getLocation();
						
						parent.setLocation(loc.x - offsetX, loc.y - offsetY);
						
						oldX = offsetX;
						oldY = offsetY;
					}
					else
					{
						parent.setLocation(parent.getLocation().x + dx, parent.getLocation().y + dy);
					}
				}
			}
		};
		
		final MouseListener mouseListener = new MouseListener()
		{
			public void mouseUp(MouseEvent e)
			{
				if (e.button != 1)
				{
					return;
				}
				
				dragging = false;
				
				if (parent.getLocation().y < parent.getCurrentMonitor().getBounds().y)
				{
					wasRestored = true;
					parent.setMaximized(true);
				}
			}
			
			public void mouseDown(MouseEvent e)
			{
				if (e.button != 1)
				{
					return;
				}
				
				dragging = true;
				
				oldX = e.x;
				oldY = e.y;
			}
			
			public void mouseDoubleClick(MouseEvent e)
			{
				if (e.button != 1)
				{
					return;
				}
				
				parent.setMaximized(!parent.isMaximized());
				
				if (!parent.isMaximized() && parent.getLocation().y < 0)
				{
					parent.setLocation(parent.getLocation().x, 0);
				}
			}
		};
		
//		titleLabel.addMouseListener(mouseListener);
//		titleLabel.addListener(SWT.MouseMove, moveListener);
		composite.addMouseListener(mouseListener);
		composite.addListener(SWT.MouseMove, moveListener);
		
		parent.addWindowListener(new WindowListener()
		{
			public boolean titleChanged(String newTitle)
			{
				setTitle(newTitle);
				
				return true;
			}
			
			public boolean iconChanged(Image newIcon)
			{
				setIcon(newIcon);
				
				return true;
			}
		});
		
		composite.setBackgroundMode(SWT.INHERIT_DEFAULT);
	}
	
	private void setTitle(String title)
	{
//		int textWidth  = titleGC.textExtent(title).x;
//		int textHeight = titleGC.textExtent(title).y;
		
		this.title = title;
		
		composite.redraw();
		
//		setTitleLocation();
	}
	
//	private void setTitleLocation()
//	{
//		int textWidth  = titleLabel.getSize().x;
//		int textHeight = titleLabel.getSize().y;
//		
//		if ((flags & SWT.CENTER) != 0)
//		{
//			titleLabel.setLocation(getWidth() / 2 - textWidth / 2, 0);
//		}
//		else if ((flags & SWT.RIGHT) != 0)
//		{
//			titleLabel.setLocation(getWidth() - textWidth, 0);
//		}
//		else
//		{
//			titleLabel.setLocation(0, 0);
//		}
//	}
	
	private void setIcon(Image icon)
	{
//		if (icon != null)
//		{
//			int width  = getHeight() - 2;
//			int height = getHeight() - 2;
//			
//			Image scaled = new Image(Display.getDefault(), icon.getImageData());
//			ImageData id = scaled.getImageData().scaledTo(width, height);
//			
//			scaled = new Image(Display.getDefault(), id);
//			
////			GC gc = new GC(scaled);
////			gc.setAntialias(SWT.ON);
////			gc.setInterpolation(SWT.HIGH);
////			gc.drawImage(icon, 0, 0, icon.getBounds().width, icon.getBounds().height, 0, 0, width, height);
////			gc.dispose();
////			
////			scaled = new Image(Display.getDefault(), scaled.getImageData());
//			
//			iconLabel.setImage(scaled);
//			iconLabel.setSize(width, height);
//			iconLabel.setLocation(3, 1);
//			iconLabel.setBackground(composite.getBackground());
//		}
	}
	
	private void addButtons()
	{
		if ((flags & SWT.CLOSE) != 0)
		{
			closeButton = new Label(composite, SWT.NONE);
			closeButton.setSize(getHeight(), getHeight());
			closeButton.setImage(closeImage);
			
			closeButton.addListener(SWT.MouseUp, buttonListener);
			closeButton.addListener(SWT.MouseEnter, buttonListener);
			closeButton.addListener(SWT.MouseExit, buttonListener);
		}
		
		if ((flags & SWT.MIN) != 0)
		{
			minimizeButton = new Label(composite, SWT.PUSH | SWT.FLAT);
			minimizeButton.setSize(getHeight(), getHeight());
			minimizeButton.setImage(minimizeImage);
			
			minimizeButton.addListener(SWT.MouseUp, buttonListener);	
			minimizeButton.addListener(SWT.MouseEnter, buttonListener);
			minimizeButton.addListener(SWT.MouseExit, buttonListener);
		}
		
		if ((flags & SWT.MAX) != 0)
		{
			restoreButton = new Label(composite, SWT.PUSH | SWT.FLAT);
			restoreButton.setSize(getHeight(), getHeight());
			restoreButton.setImage(restoreImage);
			
			restoreButton.addListener(SWT.MouseUp, buttonListener);	
			restoreButton.addListener(SWT.MouseEnter, buttonListener);
			restoreButton.addListener(SWT.MouseExit, buttonListener);
		}
		
		setForeground(new Color(Display.getDefault(), 40, 40, 40));
		setBackground(new Color(Display.getDefault(), 140, 140, 140));
	}
	
	private void layoutButtons()
	{
		int offset = 0;
		
		if (PROPERTIES.get("os.name").equals("macosx"))
		{
			if (closeButton != null && closeButton.isVisible())
			{
				closeButton.setLocation(offset, 0);
				
				offset += getHeight() + 1;
			}
			if (minimizeButton != null && minimizeButton.isVisible())
			{
				minimizeButton.setLocation(offset, 0);
				
				offset += getHeight() + 1;
			}
			if (restoreButton != null && restoreButton.isVisible())
			{
				restoreButton.setLocation(offset, 0);
				
				offset += getHeight() + 1;
			}
		}
		else
		{
			offset += closeButton.getSize().x;
			
			if (closeButton != null)// && closeButton.isVisible())
			{
				closeButton.setLocation(getWidth() - offset, 0);
				
				offset += closeButton.getSize().x + 1;
			}
			if (restoreButton != null)// && restoreButton.isVisible())
			{
				restoreButton.setLocation(getWidth() - offset, 0);
				
				offset += restoreButton.getSize().x + 1;
			}
			if (minimizeButton != null)// && minimizeButton.isVisible())
			{
				minimizeButton.setLocation(getWidth() - offset, 0);
				
				offset += minimizeButton.getSize().x + 1;
			}
		}
	}
	
	public Color getBackground()
	{
		return composite.getBackground();
	}
	
	public void setBackground(Color color)
	{
		composite.setBackground(color);
	}
	
	public Color getForeground()
	{
		return normalColor;
	}
	
	public void setForeground(Color color)
	{
		normalColor = color;

		setButtonsColor(color);
		
		hoverColor = ColorUtils.darken(color, 20);
	}
	
	private void setButtonsColor(Color color)
	{
		if (closeButton != null)
		{
			closeButton.setBackground(color);
		}
		if (restoreButton != null)
		{
			restoreButton.setBackground(color);
		}
		if (minimizeButton != null)
		{
			minimizeButton.setBackground(color);
		}
	}
	
	public int getX()
	{
		return composite.getLocation().x;
	}
	
	public int getY()
	{
		return composite.getLocation().y;
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