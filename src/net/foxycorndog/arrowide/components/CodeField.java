package net.foxycorndog.arrowide.components;

import static net.foxycorndog.arrowide.ArrowIDE.PROPERTIES;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import net.foxycorndog.arrowide.file.FileUtils;
import net.foxycorndog.arrowide.formatter.Formatter;
import net.foxycorndog.arrowide.language.CommentProperties;
import net.foxycorndog.arrowide.language.IdentifierProperties;
import net.foxycorndog.arrowide.language.Keyword;
import net.foxycorndog.arrowide.language.Language;
import net.foxycorndog.arrowide.language.MethodProperties;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.LineStyleEvent;
import org.eclipse.swt.custom.LineStyleListener;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

/**
 * Class that extends StyledText, but colors the text according
 * to the language.
 * 
 * @author	Braden Steffaniak
 * @since	Feb 13, 2013 at 4:53:31 PM
 * @since	v0.7
 * @version	v0.7
 */
public class CodeField extends StyledText
{
	private boolean										syntaxUpdaterRunning;
	private boolean										commentStarted, textStarted;
	private boolean										isEscape;
	private boolean										redrawReady;
	private boolean										autoUpdate;

	private char										textBeginning;

	private int											commentType, commentStartLocation;
	private int											textBeginningLocation;
	private int											lineNumberOffset;
	private int											charWidth;
	private int											numThreads;
	private int											escapeCount;
	
	private float										widthPercent, heightPercent;

	private String										text;

	private StringBuilder								commentTransText;

	private CommentProperties							commentProperties;
	private MethodProperties							methodProperties;
	private IdentifierProperties						identifierProperties;

	private Listener									identifierSelectorListener;
	
	private Thread										syntaxUpdater;

	private LineStyleListener							lineNumbers, lineSpaces, syntaxHighlighting;
	
	private LineNumberPanel								lineNumberPanel;
	
	private StyledText									lineNumberText;

	private Composite									composite;

	private CodeField									thisField;
	
	private Language									language;

	private StyleRange									styles[];

	// private ArrayList<ArrayList<Boolean>> tabs;

	private ArrayList<ContentListener>					contentListeners;
	private ArrayList<CodeFieldListener>				codeFieldListeners;
	
	private static int									ids;

	private static final String							whitespaceRegex;

	private static final char							whitespaceArray[];
	
	static
	{
//		whitespaceRegex = "[.,[ ]/*=()\r\n\t\\[\\]{};[-][+]['][\"]:[-][+]><!]";
		whitespaceRegex = "[.,[ ]/*=()\r\n\t[\\\\]\\[\\]{};[-][+]['][\"]:[-][+]><!]";
		
		whitespaceArray = new char[] { ' ', '.', ',', '/', '*', '=', '(', ')', '[', ']', '{', '}', ';', '\n', '\t', '\r', '-', '\\', '+', '\'', '"', ':', '-', '+', '>', '<', '!' };
	}
	
	/**
	 * Instantiates all of the variables needed for the CodeField.
	 * 
	 * @param comp The parent Composite to place it in.
	 */
	public CodeField(final Composite comp)
	{
		super(comp, SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | (Integer)PROPERTIES.get("composite.modifiers"));
		
		thisField = this;
		
		this.composite     = comp;
		
		contentListeners   = new ArrayList<ContentListener>();
		codeFieldListeners = new ArrayList<CodeFieldListener>();
		
		syntaxHighlighting = new LineStyleListener()
	    {
			public void lineGetStyle(LineStyleEvent event)
			{
				StyleRange styles[] = thisField.getStyles();
				
				event.styles = styles;
			}
	    };
	    
	    syntaxUpdater = new Thread()
		{
			public void run()
			{
				
			}
		};
	    
	    addLineStyleListener(syntaxHighlighting);
		
		setText("");
		setBounds(new Rectangle(0, 0, 100, 100));
	    setLayoutData(new GridData(SWT.RIGHT, SWT.TOP, true, true, 1, 1));
	    
	    int fontSize = 10;
	    
	    if (PROPERTIES.get("os.name").equals("macosx"))
	    {
	    	fontSize = 15;
	    }
	    
	    Font f = FileUtils.loadMonospacedFont(Display.getDefault(), "courier new", PROPERTIES.get("resources.location") + "res/fonts/CECOUR.ttf", fontSize, SWT.NORMAL);
	    setFont(f);
	    
//	    tabs = new ArrayList<ArrayList<Boolean>>();
//	    tabs.add(new ArrayList<Boolean>());
	    setTabs(4);
//		setAlwaysShowScrollBars(false);
	    
//	    highlightSyntax();
	    
	    redrawReady = true;
	    
		comp.addControlListener(new ControlListener()
		{
			public void controlResized(ControlEvent e)
			{
				if (autoUpdate)
				{
					updateSize();
				}
			}
			
			public void controlMoved(ControlEvent e)
			{
				
			}
		});
	    
	    identifierSelectorListener = new Listener()
	    {
			public void handleEvent(final Event e)
			{
				
			}
	    };
	    
	    addListener(SWT.MouseDown, identifierSelectorListener);
	    addListener(SWT.KeyDown, identifierSelectorListener);
	    
		addTraverseListener(new TraverseListener()
		{
			public void keyTraversed(TraverseEvent e)
			{
				if (e.detail == SWT.TRAVERSE_TAB_PREVIOUS)
				{
					e.doit = false;
				}
			}
		});
	    
	    addVerifyKeyListener(new VerifyKeyListener()
		{
			public void verifyKey(VerifyEvent e)
			{
//				Point range = getSelection();
//				
//				int lines = getLineAtOffset(range.y) - getLineAtOffset(range.x);
//				
//				if (e.character == '\t' && lines > 0)
//				{
//					if ((e.stateMask & SWT.SHIFT) != 0)
//					{
//						Formatter.unIndent(thisField);
//					}
//					else
//					{
//						Formatter.indent(thisField);
//					}
//					
//					e.doit = false;
//				}
//				else if (e.character == '/' && (e.stateMask & (Integer)PROPERTIES.get("key.control")) != 0)
//				{
//					Formatter.outcomment(thisField);
//					
//					e.doit = false;
//				}
//				else if (e.character == 6 && (e.stateMask & (Integer)PROPERTIES.get("key.control")) != 0 && (e.stateMask & SWT.SHIFT) != 0)
//				{
//					Formatter.format(thisField);
//
//					contentChanged();
//				}
//				else if (e.character == 1 && (e.stateMask & (Integer)PROPERTIES.get("key.control")) != 0)
//				{
//					int pos = getTopPixel();
//					
//					selectAll();
//					
//					setTopPixel(pos);
//				}
			}
		});
	    
	    addKeyListener(new KeyListener()
	    {
			public void keyPressed(KeyEvent e)
			{
				if (isPrintable(e.character) || e.keyCode == 13 || e.keyCode == 127 || e.keyCode == SWT.BS || e.keyCode == SWT.CR || e.character == '\t')
				{
					contentChanged();
				}
				
				CodeFieldEvent event = new CodeFieldEvent(e.character, e.stateMask, e.keyCode, thisField);
				
				for (int i = codeFieldListeners.size() - 1; i >= 0; i --)
				{
					codeFieldListeners.get(i).keyPressed(event);
				}
			}

			public void keyReleased(KeyEvent e)
			{
				
			}
	    });
	}
	
	public void select()
	{
//		Display.getDefault().syncExec(new Runnable()
//		{
//			public void run()
//			{
				identifierSelectorListener.handleEvent(null);
//			}
//		});
	}
	
	public void highlightSyntax()
	{
		
	}
	
	private boolean containsChar(char chars[], char key)
	{
		for (int i = 0; i < chars.length; i ++)
		{
			if (key == chars[i])
			{
				return true;
			}
		}
		
		return false;
	}
	
	public boolean isPrintableChar(char c)
	{
	    Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
	    
	    return ((!Character.isISOControl(c)) &&
	            c != java.awt.event.KeyEvent.CHAR_UNDEFINED &&
	            block != null &&
	            block != Character.UnicodeBlock.SPECIALS) ||
	            (c == '\n' || c == '\t' || c == '\r');
	}
	
	private boolean isPrintable(char c)
	{
//		Character.UnicodeBlock block = Character.UnicodeBlock.of(c);
//		
//		return (!Character.isISOControl(c) && block != null && block != Character.UnicodeBlock.SPECIALS);
		
		return (((int)c >= 32 && (int)c < 127));
	}
	
	private void contentChanged()
	{
		for (int i = contentListeners.size() - 1; i >= 0; i--)
		{
			ContentEvent event = new ContentEvent();
			
			event.setSource(this);
			
			contentListeners.get(i).contentChanged(event);
		}
		
		highlightSyntax();
	}
	
	public void setText(String text)
	{
		setText(text, false);
	}
	
	public void setText(String text, boolean loaded)
	{
		setText(text, loaded, true);
	}
	
	public void setText(String text, boolean loaded, boolean parse)
	{
		super.setText(text);
		
		redraw();
	}
	
	public String getRawText()
	{
		return getText().replace(new Character((char)13), ' ');
	}
	
	public String getWritableText()
	{
		return getText();//.replace("\n", "\r\n");
	}
	
	public int getX()
	{
		return getBounds().x;
	}
	
	public int getY()
	{
		return getBounds().y;
	}
	
	public int getWidth()
	{
		int width = getBounds().width;
		
		return width;
	}
	
	public int getHeight()
	{
		return getBounds().height;
	}
	
	public Rectangle getBounds()
	{
		Rectangle bounds = super.getBounds();
		
		if (lineNumberPanel != null)
		{
			bounds.width += lineNumberPanel.getSize().x;
			bounds.x     -= lineNumberPanel.getSize().x;
		}
		
		return bounds;
	}
	
	public void setBounds(int x, int y, int width, int height)
	{
		if (lineNumberText != null)
		{
			lineNumberText.setLocation(x, y);
		}
		
		if (lineNumberPanel != null)
		{
			lineNumberPanel.setLocation(x, y);
			
			super.setLocation(x + lineNumberPanel.getSize().x, y);
			super.setSize(width - lineNumberPanel.getSize().x, height);
		}
		else
		{
			super.setLocation(x, y);
			super.setSize(width, height);
		}
		
		updatePercent();
	}
	
	public Point getSize()
	{
		Point size = super.getSize();
		
		if (lineNumberPanel != null)
		{
			size.x += lineNumberPanel.getSize().x;
		}
		
		return size;
	}
	
	private Point getSuperSize()
	{
		return super.getSize();
	}
	
	/**
	 * Overridden method that set the size of the CodeField.
	 * 
	 * @param width The new width of the CodeField.
	 * @param height The new height of the CodeField.
	 */
	public void setSize(int width, int height)
	{
		setBounds(getX(), getY(), width, height);
	}
	
	private void setSuperSize(int width, int height)
	{
		super.setSize(width, height);
	}
	
	public Point getLocation()
	{
		Point location = super.getLocation();
		
		if (lineNumberPanel != null)
		{
			location.x = lineNumberPanel.getLocation().x;
		}
		
		return location;
	}
	
	private Point getSuperLocation()
	{
		return super.getLocation();
	}
	
	public void setLocation(int x, int y)
	{
		setBounds(x, y, getWidth(), getHeight());
	}
	
	private void setSuperLocation(int x, int y)
	{
		super.setLocation(x, y);
	}
	
	public CommentProperties getCommentProperties()
	{
		return commentProperties;
	}
	
	public MethodProperties getMethodProperties()
	{
		return methodProperties;
	}
	
	public IdentifierProperties getIdentifierProperties()
	{
		return identifierProperties;
	}
	
	public Language getLanguage()
	{
		return language;
	}
	
	public void setLanguage(Language language)
	{
		this.language        = language;
		
		highlightSyntax();
	}
	
	public void addContentListener(ContentListener listener)
	{
		contentListeners.add(listener);
	}
	
	public void addCodeFieldListener(CodeFieldListener listener)
	{
		codeFieldListeners.add(listener);
	}
	
//	public void setSize(int width, int height)
//	{
//		if (lineNumberText != null)
//		{
//			lineNumberText.setSize((new String(getLineCount() + ".").length()) * charWidth, height - getHorizontalBar().getSize().y + 1);
//		}
//		
//		super.setSize(width, height);
//	}
	
	public void paste()
	{
		String before = getText();
		
		super.paste();
		
		if (!getText().equals(before))
		{
			contentChanged();
		}
	}
	
	public StyleRange[] getStyles()
	{
		return styles;
	}
	
	public void setShowLineNumbers(boolean show)
	{
		if (show)
		{
			lineNumberPanel = new LineNumberPanel(getParent(), SWT.NONE, this);
			lineNumberPanel.setMargin(1, 5);
			
			lineNumberPanel.addControlListener(new ControlListener()
			{
				public void controlResized(ControlEvent e)
				{
					int offset = (lineNumberPanel.getLocation().x + lineNumberPanel.getSize().x) - thisField.getSuperLocation().x;
					
					setSuperLocation(getSuperLocation().x + offset, getSuperLocation().y);
					setSuperSize(getSuperSize().x - offset, getSuperSize().y);
				}
				
				public void controlMoved(ControlEvent e)
				{
					
				}
			});
		}
		else
		{
			lineNumberPanel = null;
			
			if (lineNumberText != null)
			{
				lineNumberText.removeLineStyleListener(lineNumbers);
				removeLineStyleListener(lineSpaces);
				lineNumberText.dispose();
				lineNumberText = null;
			}
		}
	}
	
	/**
	 * Set whether this CodeField should auto update its size whenever
	 * its parent Composite is resized.
	 * 
	 * @param autoUpdate Whether this CodeField should auto update its
	 * 		size whenever its parent Composite is resized.
	 */
	public void setAutoUpdate(boolean autoUpdate)
	{
		this.autoUpdate = autoUpdate;
	}
	
	/**
	 * Update the percent values for this CodeField.
	 */
	private void updatePercent()
	{
		float width  = getWidth();
		float height = getHeight();
		
		widthPercent  = width  / composite.getSize().x;
		heightPercent = height / composite.getSize().y;
	}
	
	/**
	 * Update the size of this CodeField according to the percent values.
	 */
	private void updateSize()
	{
		int width  = Math.round(widthPercent * composite.getSize().x);
		int height = Math.round(heightPercent * composite.getSize().y);
		
		setSize(width, height);
	}
}