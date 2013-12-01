package net.foxycorndog.arrowide.dialog;

import java.io.File;

import net.foxycorndog.arrowide.file.FileUtils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Listener;

public class FileBrowseDialog extends TextInputDialog
{
	public static final int DIRECTORY = 0, FILE = 1, EITHER = 2;
	
	public FileBrowseDialog(String windowInstruction, String textFieldInstruction, int directory)
	{
		this(windowInstruction, textFieldInstruction, "", directory);
	}
	
	public FileBrowseDialog(String windowInstruction, String textFieldInstruction, String defaultTextField, final int directory)
	{
		super(windowInstruction, textFieldInstruction, defaultTextField);
		
		getContentPanel().setSize(480, 230);
		getErrorLabel().setLocation(100, 140);
		getContinueButton().setLocation(360, 115);
		
		final Button browse = new Button(getContentPanel(), SWT.PUSH);
		browse.setSize(100, 25);
		browse.setLocation(360, 80);
		browse.setText("Browse");
		
		Listener componentListener = new Listener()
		{
			public void handleEvent(Event e)
			{
				String location = null;
					
				if (directory == DIRECTORY || directory == EITHER)
				{
					DirectoryDialog dialog = new DirectoryDialog(getContentPanel().getShell(), SWT.OPEN);
					
					location = dialog.open();
				}
				else if (directory == FILE)
				{
					FileDialog dialog = new FileDialog(getContentPanel().getShell(), SWT.OPEN);
					
					location = dialog.open();
				}
				
				if (location != null)
				{
					setText(location, true);
				}
			}
		};
		
		browse.addListener(SWT.Selection, componentListener);
		
		addDialogFilter(new DialogFilter()
		{
			public String filter(String text)
			{
				String location = text.replace("\\", "/");
				
				if (location.length() <= 0)
				{
					return "You must enter the " + (directory == DIRECTORY ? "directory" : "file") + " location.";
				}
				
				location = FileUtils.removeEndingSlashes(location);
				
				File file = new File(location);
				
				if (file.exists())
				{
					boolean isDirectory = file.isDirectory();
					
					if ((directory == EITHER) || (directory != DIRECTORY && !isDirectory) || (directory == DIRECTORY && isDirectory))
					{
						setText(location);
					}
//					else if (directory == FILE && isDirectory)
//					{
//						return "Must be a file name, not a directory name.";
//					}
//					else if (directory == DIRECTORY && !isDirectory)
//					{
//						return "Must be a directory name, not a file name.";
//					}
					else
					{
						return "An unknown error has ocurred.";
					}
				}
				else
				{
					return "A " + (directory == DIRECTORY ? "directory" : "file") + " already exists at that location.";
				}
				
				return null;
			}
		});
	}
}