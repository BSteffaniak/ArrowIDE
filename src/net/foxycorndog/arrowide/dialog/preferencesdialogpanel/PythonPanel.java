package net.foxycorndog.arrowide.dialog.preferencesdialogpanel;

import static net.foxycorndog.arrowide.ArrowIDE.CONFIG_DATA;
import net.foxycorndog.arrowide.ArrowIDE;
import net.foxycorndog.arrowide.components.FileLocator;
import net.foxycorndog.arrowide.dialog.DialogPanel;
import net.foxycorndog.arrowide.file.FileUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class PythonPanel extends DialogPanel
{
	private FileLocator pythonLocator;
	
	public PythonPanel(Composite parent)
	{
		super(parent, "Python");
		
		FillLayout layout   = new FillLayout(SWT.VERTICAL);
		layout.marginHeight = 5;
		layout.marginWidth  = 5;
		layout.spacing      = 1;
		
		setLayout(layout);
		
		int width = getSize().x;
		
		pythonLocator = new FileLocator(this, parent.getShell(), "Python Location:", FileLocator.DIRECTORY);
		pythonLocator.setLocation(width - pythonLocator.getSize().x, 50);
		
		if (CONFIG_DATA.containsKey("python.location"))
		{
			pythonLocator.setText(CONFIG_DATA.get("python.location"));
		}
	}

	public void apply()
	{
		if (pythonLocator.getText() != null && !pythonLocator.getText().equals("") && !pythonLocator.getText().equals(CONFIG_DATA.get("python.location")))
		{
			String location = FileUtils.removeEndingSlashes(pythonLocator.getText().replace('\\', '/'));
			
			ArrowIDE.setConfigDataValue("python.location", location);
		}
	}

	public void revert()
	{
		if (CONFIG_DATA.containsKey("python.location"))
		{
			pythonLocator.setText(CONFIG_DATA.get("python.location"));
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