package net.foxycorndog.arrowide.dialog;

import java.util.ArrayList;
import java.util.HashMap;

import org.eclipse.swt.widgets.Composite;

/**
 * Dialog for each of the projects' properties. Just like the Preferences
 * Panel.
 * 
 * @author	Braden Steffaniak
 * @since	Feb 20, 2013 at 6:31:56 PM
 * @since	v0.7
 * @version Feb 20, 2013 at 7:56:59 PM
 * @version	v0.7
 */
public class PropertiesDialog extends PanelledDialog
{
	private HashMap<Integer, ArrayList<DialogPanel>>	panels;
	
	public PropertiesDialog(Composite parent)
	{
		super(parent);
		
		panels = new HashMap<Integer, ArrayList<DialogPanel>>();
		
		getWindow().setResizable(false);
	}
	
	public void addDialogPanel(int types[], DialogPanel panel)
	{
		super.addDialogPanel(panel);
		
		for (int i = 0; i < types.length; i++)
		{
			if (panels.containsKey(types[i]))
			{
				panels.get(types[i]).add(panel);
			}
			else
			{
				panels.put(types[i], new ArrayList<DialogPanel>());
			}
		}
	}
	
	public String open()
	{
		return super.open();
	}
}