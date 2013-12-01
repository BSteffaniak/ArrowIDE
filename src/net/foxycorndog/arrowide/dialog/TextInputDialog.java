package net.foxycorndog.arrowide.dialog;

import java.util.ArrayList;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextInputDialog implements Dialog
{
	private String  text;
	
	private Text    locationEditor;
	
	private Button  continueButton;
	
	private Label   errorLabel, textFieldInstructionLabel, instructionLabel;
	
	private Shell   window;
	
	private Display display;
	
	private ArrayList<DialogFilter> filters;
	
	public TextInputDialog(String windowInstruction, String textFieldInstruction)
	{
		this(windowInstruction, textFieldInstruction, "");
	}
	
	public TextInputDialog(String windowInstruction, String textFieldInstruction, Point size)
	{
		this(windowInstruction, textFieldInstruction, "", size);
	}
	
	public TextInputDialog(String windowInstruction, String textFieldInstruction, String defaultTextField)
	{
		this(windowInstruction, textFieldInstruction, defaultTextField, new Point(470, 180));
	}
	
	public TextInputDialog(String windowInstruction, String textFieldInstruction, String defaultTextField, Point size)
	{
		this.display  = Display.getDefault();
		
		filters       = new ArrayList<DialogFilter>();
		
		Rectangle screenBounds = display.getMonitors()[0].getBounds();
		
		window = new Shell(display, SWT.SYSTEM_MODAL | SWT.CLOSE);
		window.setSize(size);
		window.setLocation(screenBounds.width / 2 - window.getBounds().width / 2, screenBounds.height / 2 - window.getBounds().height / 2);
		
		instructionLabel = new Label(window, SWT.NONE);
		instructionLabel.setText(windowInstruction);
		instructionLabel.setSize(370, 20);
		instructionLabel.setLocation(100, 30);
		
		textFieldInstructionLabel = new Label(window, SWT.NONE);
		textFieldInstructionLabel.setText(textFieldInstruction);
		textFieldInstructionLabel.setSize(70, 20);
		textFieldInstructionLabel.setLocation(20, 80);
		
		errorLabel = new Label(window, SWT.NONE);
		errorLabel.setText("");
		errorLabel.setSize(370, 20);
		errorLabel.setLocation(100, 115);
		errorLabel.setForeground(new Color(display, 220, 0, 0));
		
		continueButton = new Button(window, SWT.PUSH);
		continueButton.setSize(100, 25);
		continueButton.setLocation(360, 80);
		continueButton.setText("Continue");
		
		locationEditor = new Text(window, SWT.SINGLE | SWT.BORDER);
		locationEditor.setSize(250, 20);
		locationEditor.setLocation(100, 80);
		locationEditor.setText(defaultTextField);
		locationEditor.setSelection(0, defaultTextField.length());
		
		Listener componentListener = new Listener()
		{
			public void handleEvent(Event e)
			{
				if (e.widget == continueButton)
				{
					continuePressed();
				}
			}
		};
		
		continueButton.addListener(SWT.Selection, componentListener);
		
		locationEditor.addKeyListener(new KeyListener()
		{
			public void keyPressed(KeyEvent e)
			{
				if (e.keyCode == 13)
				{
					continuePressed();
				}
			}

			public void keyReleased(KeyEvent e)
			{
				
			}
		});
		
		window.addListener(SWT.Close, new Listener()
		{
			public void handleEvent(Event event)
			{
				text = null;
			}
		});
	}
	
	private void continuePressed()
	{
		boolean wasError = false;
		
		text = locationEditor.getText();
		
		for (int i = filters.size() - 1; i >= 0; i --)
		{
			String error = null;
			
			if ((error = filters.get(i).filter(text)) != null)
			{
				this.errorLabel.setText(error);
				
				wasError = true;
				
				break;
			}
		}
		
		if (!wasError)
		{
			if (text.length() >= 0)
			{
				window.dispose();
			}
			else
			{
				errorLabel.setText("You must enter text.");
			}
		}
	}
	
	public String open()
	{
		window.open();
		
		locationEditor.setFocus();
		
		while (!window.isDisposed())
		{
			if (!display.readAndDispatch())
			{
				display.sleep();
			}
		}
		
		return text;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		setText(text, false);
	}
	
	public void setText(String text, boolean editor)
	{
		this.text = text;
		
		if (editor)
		{
			locationEditor.setText(text);
		}
	}
	
	public void addDialogFilter(DialogFilter filter)
	{
		this.filters.add(filter);
	}
	
	public Composite getContentPanel()
	{
		return window;
	}
	
	public Button getContinueButton()
	{
		return continueButton;
	}
	
	public Label getInstructionLabel()
	{
		return instructionLabel;
	}
	
	public Label getTextFieldInstructionsLabel()
	{
		return textFieldInstructionLabel;
	}
	
	public Label getErrorLabel()
	{
		return errorLabel;
	}
	
	public Text getLocationEditor()
	{
		return locationEditor;
	}
}