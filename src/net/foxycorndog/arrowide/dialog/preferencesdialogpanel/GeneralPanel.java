package net.foxycorndog.arrowide.dialog.preferencesdialogpanel;

import static net.foxycorndog.arrowide.ArrowIDE.CONFIG_DATA;
import net.foxycorndog.arrowide.ArrowIDE;
import net.foxycorndog.arrowide.components.DropdownMenu;
import net.foxycorndog.arrowide.components.FileLocator;
import net.foxycorndog.arrowide.dialog.DialogPanel;
import net.foxycorndog.arrowide.file.FileUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class GeneralPanel extends DialogPanel
{
	private DropdownMenu customWindow;
	
	private FileLocator workspaceLocator;
	
	private ArrowIDE    ide;
	
	public GeneralPanel(Composite parent, ArrowIDE ide)
	{
		super(parent, "General");
		
		this.ide = ide;
		
		int width = getSize().x;
		
		customWindow = new DropdownMenu(this);
		customWindow.setSize(100, 25);
		customWindow.addItem("Default");
		customWindow.addItem("Custom");
		customWindow.setLocation(width - customWindow.getWidth() - 20, 85);
		
		Label compilerLabel = new Label(this, SWT.NONE);
		compilerLabel.setText("Window Type:");
		compilerLabel.setSize(90, 25);
		compilerLabel.setLocation(customWindow.getX() - compilerLabel.getSize().x, customWindow.getY() + 3);
		
		boolean custom = false;
		
		if (CONFIG_DATA.containsKey("window.custom"))
		{
			custom = Boolean.valueOf(CONFIG_DATA.get("window.custom"));
		}
		
		if (custom)
		{
			customWindow.setSelection("Custom");
		}
		else
		{
			customWindow.setSelection("Default");
		}
		
		workspaceLocator = new FileLocator(this, parent.getShell(), "Workspace Location:", FileLocator.DIRECTORY);
		workspaceLocator.setLocation(width - workspaceLocator.getSize().x, 50);
		
		if (CONFIG_DATA.containsKey("workspace.location"))
		{
			workspaceLocator.setText(CONFIG_DATA.get("workspace.location"));
		}
	}

	public void apply()
	{
		if (workspaceLocator.getText() != null && !workspaceLocator.getText().equals(CONFIG_DATA.get("workspace.location")))
		{
			String location = FileUtils.removeEndingSlashes(workspaceLocator.getText().replace('\\', '/'));
			
			ArrowIDE.setConfigDataValue("workspace.location", location);
		
			ide.removeAllTreeItems();
			ide.refreshFileViewer(true);
		}
		
		boolean custom = customWindow.getSelection().equals("Custom");
		
		boolean customAlreadySet = false;
		
		if (CONFIG_DATA.containsKey("window.custom"))
		{
			customAlreadySet = custom == Boolean.valueOf(CONFIG_DATA.get("window.custom"));
		}
		
		if (!customAlreadySet)
		{
			ArrowIDE.setConfigDataValue("window.custom", custom + "");
		}
	}

	public void revert()
	{
		if (CONFIG_DATA.containsKey("workspace.location"))
		{
			workspaceLocator.setText(CONFIG_DATA.get("workspace.location"));
		}
		if (CONFIG_DATA.containsKey("window.custom"))
		{
			boolean custom = false;
			
			custom = Boolean.valueOf(CONFIG_DATA.get("window.custom"));
			
			if (custom)
			{
				customWindow.setSelection("Custom");
			}
			else
			{
				customWindow.setSelection("Default");
			}
		}
	}

	public void update()
	{
		revert();
	}

	public void open()
	{
		
	}
}