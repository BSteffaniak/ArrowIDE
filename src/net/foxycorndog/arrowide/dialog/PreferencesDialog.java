package net.foxycorndog.arrowide.dialog;

import org.eclipse.swt.widgets.Composite;

/**
 * Dialog for each of the IDE's properties. Just like the Properties
 * Panel.
 * 
 * @author	Braden Steffaniak
 * @since	Feb 20, 2013 at 6:32:47 PM
 * @since	v0.7
 * @version	v0.7
 */
public class PreferencesDialog extends PanelledDialog
{
	public PreferencesDialog(Composite parent)
	{
		super(parent);
		
		getWindow().setResizable(false);
	}
}