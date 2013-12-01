package net.foxycorndog.arrowide.components.window;

import org.eclipse.swt.graphics.Image;

public interface WindowListener
{
	public boolean titleChanged(String newTitle);

	public boolean iconChanged(Image newIcon);
}