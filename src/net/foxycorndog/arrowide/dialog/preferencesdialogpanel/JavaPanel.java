package net.foxycorndog.arrowide.dialog.preferencesdialogpanel;

import static net.foxycorndog.arrowide.ArrowIDE.CONFIG_DATA;
import net.foxycorndog.arrowide.ArrowIDE;
import net.foxycorndog.arrowide.components.FileLocator;
import net.foxycorndog.arrowide.dialog.DialogPanel;
import net.foxycorndog.arrowide.file.FileUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

public class JavaPanel extends DialogPanel
{
	private FileLocator jdkLocator;
	
	public JavaPanel(Composite parent)
	{
		super(parent, "Java");
		
		FillLayout layout   = new FillLayout(SWT.VERTICAL);
		layout.marginHeight = 5;
		layout.marginWidth  = 5;
		layout.spacing      = 1;
		
		setLayout(layout);
		
		int width = getSize().x;
		
		jdkLocator = new FileLocator(this, parent.getShell(), "JDK Location:", FileLocator.DIRECTORY);
		jdkLocator.setLocation(width - jdkLocator.getSize().x, 50);
		
		if (CONFIG_DATA.containsKey("jdk.location"))
		{
			jdkLocator.setText(CONFIG_DATA.get("jdk.location"));
		}
	}

	public void apply()
	{
		if (jdkLocator.getText() != null && !jdkLocator.getText().equals(CONFIG_DATA.get("jdk.location")))
		{
			String location = FileUtils.removeEndingSlashes(jdkLocator.getText().replace('\\', '/'));
			
			ArrowIDE.setConfigDataValue("jdk.location", location);
		}
	}

	public void revert()
	{
		if (CONFIG_DATA.containsKey("jdk.location"))
		{
			jdkLocator.setText(CONFIG_DATA.get("jdk.location"));
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