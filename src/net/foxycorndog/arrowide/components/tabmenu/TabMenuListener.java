package net.foxycorndog.arrowide.components.tabmenu;

/**
 * Class that is used to be implemented and called whenever a tab menu
 * event is dispatched.
 * 
 * @author	Braden Steffaniak
 * @since	Mar 24, 2013 at 1:49:03 AM
 * @since	v0.1
 * @version Mar 24, 2013 at 1:49:03 AM
 * @version	v0.2
 */
public interface TabMenuListener
{
	public boolean tabClosing(TabMenuEvent event);
	
	public void tabClosed(TabMenuEvent event);
	
	public void tabSelected(TabMenuEvent event);
	
	public void tabDoubleClicked(TabMenuEvent event);
}