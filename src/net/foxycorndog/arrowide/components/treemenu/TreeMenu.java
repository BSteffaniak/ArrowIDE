package net.foxycorndog.arrowide.components.treemenu;

import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

public class TreeMenu extends Composite
{
	private int							idNum;
	
	private Tree						tree;

	private Composite					parent;

	private HashMap<TreeItem, Integer>	ids;
	private HashMap<Integer, TreeItem>	items;
	private HashMap<Integer, Integer>	parents;

	private ArrayList<Integer>			treeIds;

	private ArrayList<TreeMenuListener>	listeners;
	
	private Listener selectionListener, doubleClickListener;
	
	public TreeMenu(Composite parent)
	{
		super(parent, SWT.NONE);
		
		tree         = new Tree(this, SWT.H_SCROLL | SWT.V_SCROLL | (Integer)PROPERTIES.get("composite.modifiers"));
		
		treeIds      = new ArrayList<Integer>();
		
		ids          = new HashMap<TreeItem, Integer>();
		items        = new HashMap<Integer, TreeItem>();
		parents      = new HashMap<Integer, Integer>();
		
		listeners    = new ArrayList<TreeMenuListener>();
		
		doubleClickListener = new Listener()
		{
			public void handleEvent(Event e)
			{
				for (int i = listeners.size() - 1; i >= 0; i --)
				{
					TreeItem itemsSelected[] = tree.getSelection();
					
					for (int j = 0; j < itemsSelected.length; j ++)
					{
						int id = ids.get(itemsSelected[j]);
						
						if (e.button == 1)
						{
							listeners.get(i).treeItemDoubleClicked(id);
						}
					}
				}
			}
		};
		
		selectionListener = new Listener()
		{
			public void handleEvent(Event e)
			{
				for (int i = listeners.size() - 1; i >= 0; i --)
				{
					if (tree.getItem(new Point(e.x, e.y)) != null)
					{
						TreeItem itemsSelected[] = tree.getSelection();
						
						for (int j = 0; j < itemsSelected.length; j ++)
						{
							int id = ids.get(itemsSelected[j]);
							
							if (e.button == 1)
							{
								listeners.get(i).treeItemSelected(id);
							}
							else if (e.button == 3)
							{
								
							}
						}
					}
					else
					{
						tree.setSelection(new TreeItem[] {});
						
						if (e.button == 1)
						{
							listeners.get(i).treeItemSelected(0);
						}
					}
				}
			}
		};
		
		tree.addListener(SWT.MouseDoubleClick, doubleClickListener);
		tree.addListener(SWT.MouseDown, selectionListener);
	}

	public void setSelection(int id)
	{
		tree.setSelection(items.get(id));
	}
	
	public int addItem(String name)
	{
		return addItem(name, null);
	}
	
	public int addItem(String name, Image image)
	{
		return addItem(0, name, image);
	}

	public int addItem(int parentId, String name)
	{
		return addItem(parentId, name, null);
	}
	
	public int addItem(int parentId, String name, Image image)
	{
		int id = ++idNum;
		
		TreeItem item = null;
		
		if (parentId == 0)
		{
			item = new TreeItem(tree, SWT.NONE);
		}
		else
		{
			item = new TreeItem(items.get(parentId), SWT.NONE);
		}
		
		item.setText(name);
		item.setImage(image);

		ids.put(item, id);
		items.put(id, item);
		parents.put(id, 0);
		treeIds.add(id);
		
		return id;
	}

	public void removeAllItems()
	{
//		for (int )
		tree.removeAll();
		ids.clear();
		items.clear();
		parents.clear();
		treeIds.clear();
	}
	
	public void alphabetize()
	{
		
	}
	
	public int getParentId(int id)
	{
		return parents.get(id);
	}
	
	public boolean containsSubItem(int parentId, int subItemId)
	{
		boolean contains = false;
		
		if (items.containsKey(subItemId) && items.containsKey(parentId))
		{
			contains = items.get(subItemId).getParentItem() == items.get(parentId);
		}
		
		return contains;
	}
	
	public boolean containsItem(int id)
	{
		return items.containsKey(id);
	}
	
	public int getSelection()
	{
		if (tree.getSelection().length == 0)
		{
			return -1;
		}
		
		int selection = 0;
		
		selection = ids.get(tree.getSelection()[0]);
		
		return selection;
	}
	
	public void addListener(TreeMenuListener listener)
	{
		listeners.add(listener);
	}
	
	public int getWidth()
	{
		return super.getBounds().width;
	}
	
	public int getHeight()
	{
		return super.getBounds().height;
	}
	
	public void setSize(int width, int height)
	{
		super.setSize(width, height);
		tree.setSize(width, height);
	}
	
	public void setLocation(int x, int y)
	{
		super.setLocation(x, y);
	}
	
	public void setBackground(Color color)
	{
		super.setBackground(color);
		tree.setBackground(color);
	}
	
	public void setMenu(Menu menu)
	{
		tree.setMenu(menu);
	}
	
	public void removeItem(int id)
	{
//		private HashMap<Integer, HashMap<Integer, TreeItem>> subItems;
//		private HashMap<Integer, HashMap<TreeItem, String>>  subItemNames;
//		
//		private HashMap<TreeItem, Integer> ids;
//		private HashMap<Integer, TreeItem> items;
//		
//		private ArrayList<Integer> treeIds;
		
		TreeItem item = items.get(id);
		
		ids.remove(item);
		items.remove(id);
		treeIds.remove((Object)id);
		
		item.dispose();
	}
	
	public boolean contains(int id)
	{
		return ids.containsValue(id);
	}
	
	public void setTreeItemText(int id, String text)
	{
		items.get(id).setText(text);
	}
	
	public String getTreeItemText(int id)
	{
		return items.get(id).getText();
	}
}