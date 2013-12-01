package net.foxycorndog.arrowide.components.menubar;

public interface DropdownMenuListener
{
	public void itemSelected(String text);
	
	public void itemHovered(String text);
	
	public void itemUnhovered(String text);
}