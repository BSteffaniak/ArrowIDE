package net.foxycorndog.arrowide.components;

import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

public class DropdownMenu
{
	private int							idNum;

	private Combo						dropdown;

	private HashMap<String, Integer>	ids;
	private HashMap<Integer, String>	names;

	public DropdownMenu(Composite parent)
	{
		dropdown = new Combo(parent, SWT.DROP_DOWN | SWT.BORDER | SWT.READ_ONLY);

		ids = new HashMap<String, Integer>();
		names = new HashMap<Integer, String>();
	}

	public int getX()
	{
		return dropdown.getLocation().x;
	}

	public int getY()
	{
		return dropdown.getLocation().y;
	}

	public Point getLocation()
	{
		return dropdown.getLocation();
	}

	public void setLocation(int x, int y)
	{
		dropdown.setLocation(x, y);
	}

	public void setLocation(Point location)
	{
		dropdown.setLocation(location.x, location.y);
	}

	public int getWidth()
	{
		return dropdown.getSize().x;
	}

	public int getHeight()
	{
		return dropdown.getSize().y;
	}

	public Point getSize()
	{
		return dropdown.getSize();
	}

	public void setSize(int width, int height)
	{
		dropdown.setSize(width, height);
	}

	public void setSize(Point size)
	{
		dropdown.setSize(size.x, size.y);
	}

	public void setSelection(String s)
	{
		dropdown.select(ids.get(s));
	}

	public int addItem(String name)
	{
		int id = idNum++;

		dropdown.add(name);

		ids.put(name, id);
		names.put(id, name);

		return id;
	}

	public String getSelection()
	{
		String name = names.get(dropdown.getSelectionIndex());

		return name;
	}
}
