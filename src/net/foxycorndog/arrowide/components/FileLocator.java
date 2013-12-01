package net.foxycorndog.arrowide.components;

import net.foxycorndog.arrowide.dialog.FileBrowseDialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class FileLocator extends Composite
{
	private Label	instructionLabel;

	private Text	locationEditor;

	private Button	browse;
	
	public  static final int DIRECTORY = FileBrowseDialog.DIRECTORY, FILE = FileBrowseDialog.FILE, EITHER = FileBrowseDialog.EITHER;

	public FileLocator(Composite parent, Shell parentShell, String instruction, int directory)
	{
		this(parent, parentShell, instruction, "", directory);
	}
	
	public FileLocator(Composite parent, final Shell parentShell, String instruction, String defaultTextField, final int directory)
	{
		super(parent, SWT.NONE);
		
		locationEditor = new Text(this, SWT.SINGLE | SWT.BORDER);
		locationEditor.setSize(250, 20);
		locationEditor.setText(defaultTextField);
		locationEditor.setLocation(0, 2);
		locationEditor.setSelection(0, defaultTextField.length());

		browse = new Button(this, SWT.PUSH);
		browse.setSize(100, 25);
		browse.setLocation(locationEditor.getSize().x + 10, 0);
		browse.setText("Browse");

		setSize(360, 25);

		if (!instruction.equals("") || instruction != null)
		{
			instructionLabel = new Label(this, SWT.NONE);
			instructionLabel.setText(instruction);
			instructionLabel.setLocation(0, 4);
			instructionLabel.getFont().getFontData();
			
			GC gc = new GC(instructionLabel);
			instructionLabel.setSize(gc.stringExtent(instruction).x, 20);
			
			locationEditor.setLocation(instructionLabel.getSize().x + 10, 2);
			browse.setLocation(locationEditor.getSize().x + locationEditor.getLocation().x + 10, 0);
			
			setSize(browse.getLocation().x + browse.getSize().x + 20, 25);
		}
		
		final Composite thisComposite = this;
		
		Listener componentListener = new Listener()
		{
			public void handleEvent(Event e)
			{
				String location = null;
					
				if (directory == DIRECTORY || directory == EITHER)
				{
					DirectoryDialog dialog = new DirectoryDialog(parentShell, SWT.OPEN);
					
					location = dialog.open();
				}
				else if (directory == FILE)
				{
					FileDialog dialog = new FileDialog(parentShell, SWT.OPEN);
					
					location = dialog.open();
				}
				
				if (location != null)
				{
					locationEditor.setText(location);
				}
			}
		};
		
		browse.addListener(SWT.Selection, componentListener);
	}
	
	public String getText()
	{
		return locationEditor.getText();
	}
	
	public void setText(String text)
	{
		locationEditor.setText(text);
	}
}