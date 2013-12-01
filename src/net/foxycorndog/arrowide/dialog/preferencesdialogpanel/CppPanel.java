package net.foxycorndog.arrowide.dialog.preferencesdialogpanel;

import static net.foxycorndog.arrowide.ArrowIDE.CONFIG_DATA;
import net.foxycorndog.arrowide.ArrowIDE;
import net.foxycorndog.arrowide.components.FileLocator;
import net.foxycorndog.arrowide.dialog.DialogPanel;
import net.foxycorndog.arrowide.file.FileUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class CppPanel extends DialogPanel
{
	private FileLocator gppLocator, gccLocator;
	
	public CppPanel(Composite parent)
	{
		super(parent, "C++");
		
		FillLayout layout   = new FillLayout(SWT.VERTICAL);
		layout.marginHeight = 5;
		layout.marginWidth  = 5;
		layout.spacing      = 1;
		
		setLayout(layout);
		
		int width = getSize().x;
		
		gppLocator = new FileLocator(this, parent.getShell(), "G++ Location:", FileLocator.FILE);
		gppLocator.setLocation(width - gppLocator.getSize().x, 50);
		
		gccLocator = new FileLocator(this, parent.getShell(), "Gcc Location:", FileLocator.FILE);
		gccLocator.setLocation(width - gccLocator.getSize().x, 85);
		
		if (CONFIG_DATA.containsKey("g++.location"))
		{
			gppLocator.setText(CONFIG_DATA.get("g++.location"));
		}
	}

	public void apply()
	{
		if (gppLocator.getText() != null && !gppLocator.getText().equals("") && !gppLocator.getText().equals(CONFIG_DATA.get("g++.location")))
		{
			String location = FileUtils.removeEndingSlashes(gppLocator.getText().replace('\\', '/'));
			
			ArrowIDE.setConfigDataValue("g++.location", location);
		}
	}

	public void revert()
	{
		if (CONFIG_DATA.containsKey("g++.location"))
		{
			gppLocator.setText(CONFIG_DATA.get("g++.location"));
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