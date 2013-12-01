package net.foxycorndog.arrowide.dialog;

import net.foxycorndog.arrowide.components.window.Window;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class OptionDialog implements Dialog
{
	private String	result;
	
	private Label	instructionLabel;
	
	private Button	yes, no, cancel;
	
	private Shell	window;
	
	private Display	display;
	
	public OptionDialog(Window parent, String windowTitle, String windowInstruction)
	{
		display = Display.getDefault();
		
		Rectangle screenBounds = display.getMonitors()[0].getBounds();
		
		window = new Shell(display, SWT.SYSTEM_MODAL | SWT.TITLE);
		window.setSize(475, 120);
		window.setLocation(screenBounds.width / 2 - window.getBounds().width / 2, screenBounds.height / 2 - window.getBounds().height / 2);
		window.setText(windowTitle);
		
		int centerX = parent.getLocation().x + parent.getSize().x / 2 - window.getSize().x / 2;
		int centerY = parent.getLocation().y + parent.getSize().y / 2 - window.getSize().y / 2;
		
		window.setLocation(centerX, centerY);
		
		instructionLabel = new Label(window, SWT.NONE);
		instructionLabel.setSize(450, 25);
		instructionLabel.setLocation(25, 20);
		instructionLabel.setText(windowInstruction);
		
		yes = new Button(window, SWT.NONE);
		yes.setSize(85, 25);
		yes.setLocation(155, 50);
		yes.setText("Yes");
		yes.addSelectionListener(buttonListener);
		
		no = new Button(window, SWT.NONE);
		no.setSize(85, 25);
		no.setLocation(250, 50);
		no.setText("No");
		no.addSelectionListener(buttonListener);

		cancel = new Button(window, SWT.NONE);
		cancel.setSize(85, 25);
		cancel.setLocation(345, 50);
		cancel.setText("Cancel");
		cancel.addSelectionListener(buttonListener);
	}
	
	private SelectionListener buttonListener = new SelectionListener()
	{
		public void widgetSelected(SelectionEvent e)
		{
			if (e.getSource() == yes)
			{
				result = "yes";
			}
			else if (e.getSource() == no)
			{
				result = "no";
			}
			else if (e.getSource() == cancel)
			{
				result = "cancel";
			}
			else
			{
				return;
			}
			
			window.dispose();
		}
		
		public void widgetDefaultSelected(SelectionEvent e)
		{
			widgetSelected(e);
		}
	};
	
	public String open()
	{
		window.open();
		
		while (!window.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		
		return result;
	}

	public void setText(String text)
	{
		
	}

	public Composite getContentPanel()
	{
		return null;
	}
}