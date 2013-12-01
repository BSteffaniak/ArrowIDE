package net.foxycorndog.arrowide.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;

public abstract class DialogPanel extends Composite
{
	private String title;
	
	public DialogPanel(Composite parent, String title)
	{
		super(parent, SWT.BORDER);
		
		int treeWidth  = 150;
		int treeHeight = 100;
		
		super.setSize(parent.getSize().x - treeWidth, parent.getSize().y - treeHeight);
		super.setLocation(treeWidth, 0);
		
		this.title = title;
	}
	
	public final void setSize(int width, int height)
	{
		
	}
	
	public final void setSize(Point size)
	{
		
	}
	
	public final void setLocation(int x, int y)
	{
		
	}
	
	public final void setLocation(Point location)
	{
		
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public abstract void apply();
	
	public abstract void revert();
	
	public abstract void update();
	
	public abstract void open();
}