package net.foxycorndog.arrowide.dialog;

import org.eclipse.swt.widgets.Composite;

public interface Dialog
{
	public String open();
	
	public Composite getContentPanel();
}