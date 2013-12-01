package net.foxycorndog.arrowide.components.tabmenu;

import org.eclipse.swt.graphics.Point;

import net.foxycorndog.arrowide.event.Event;

/**
 * Class that holds the information for each event that is
 * dispatched from the TabMenu.
 * 
 * @author	Braden Steffaniak
 * @since	Mar 24, 2013 at 1:45:46 AM
 * @since	v0.2
 * @version Mar 24, 2013 at 1:45:46 AM
 * @version	v0.2
 */
public class TabMenuEvent extends Event
{
	private boolean	down;
	private boolean	clicked;
	
	private int		button;
	private int		tabId;
	
	private Point	location;
	
	private TabMenu source;
	
	/**
	 * Create a TabMenuEvent with the specified source and tab id.
	 * 
	 * @param source The TabMenu that dispatched this event.
	 * @param location The location that the mouse clicked the tab at.
	 * @param tabId The id of the tab that was edited.
	 * @param button The button that the tab was pressed with.
	 * @param down Whether or not the event was triggered on mouse down.
	 * @param clicked Whether or not the tab was pressed and released
	 * 		consecutively.
	 */
	public TabMenuEvent(TabMenu source, Point location, int tabId, int button, boolean down, boolean clicked)
	{
		this.source   = source;
		
		this.location = location;
		
		this.tabId    = tabId;
		this.button   = button;
		
		this.down     = down;
		this.clicked  = clicked;
	}
	
	/**
	 * Get whether or not the tab was pressed and released
	 * consecutively.
	 * 
	 * @return Whether or not the tab was pressed and released
	 * 		consecutively.
	 */
	public boolean wasClicked()
	{
		return clicked;
	}
	
	/**
	 * Get whether or not the event was triggered on mouse down.
	 * 
	 * @return Whether or not the event was triggered on mouse down.
	 */
	public boolean wasDown()
	{
		return down;
	}
	
	/**
	 * Get the button that the tab was pressed with.
	 * 
	 * @return The button that the tab was pressed with.
	 */
	public int getButton()
	{
		return button;
	}
	
	/**
	 * Get the id of the tab that was edited.
	 * 
	 * @return The id of the tab that was edited.
	 */
	public int getTabId()
	{
		return tabId;
	}
	
	/**
	 * Get the location that the mouse clicked the tab at.
	 * 
	 * @return The location that the mouse clicked the tab at.
	 */
	public Point getLocation()
	{
		return location;
	}
	
	/**
	 * Get the TabMenu instance that was used when the event was
	 * dispatched.
	 * 
	 * @return The instance that was used when the event was dispatched.
	 */
	public TabMenu getSource()
	{
		return source;
	}
}