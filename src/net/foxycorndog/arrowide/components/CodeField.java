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

import static net.foxycorndog.arrowide.ArrowIDE.DISPLAY;

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
	
	private StyleRange									keyword, method, comment, variable;

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
		
//		syntaxHighlighting = new LineStyleListener()
//	    {
//			public void lineGetStyle(LineStyleEvent event)
//			{
//				StyleRange styles[] = thisField.getStyles();
//				System.out.println("ASdf");
//				event.styles = styles;
//			}
//	    };
	    
	    syntaxUpdater = new Thread()
		{
			public void run()
			{
				
			}
		};
	    
//	    addLineStyleListener(syntaxHighlighting);
		
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
//				int off = getLastCharacterOnTheRight(getCaretOffset(), getText());
//				
//				System.out.println(getCaretOffset() + ": " + off + " : " + getText().charAt(getCaretOffset()) + " : " + getText().charAt(off) + "!");
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
				if (isPrintable(e.character) || e.keyCode == 13 || e.keyCode == 127 || e.keyCode == SWT.BS || e.keyCode == SWT.CR || e.character == '\t' || e.character == '\b')
				{
					contentChanged();
				}
				
				CodeFieldEvent event = new CodeFieldEvent(e.character, e.stateMask, e.keyCode, thisField);
				
				for (int i = codeFieldListeners.size() - 1; i >= 0; i--)
				{
					codeFieldListeners.get(i).keyPressed(event);
				}
			}

			public void keyReleased(KeyEvent e)
			{
				
			}
	    });
	    
	    keyword  = new StyleRange();
	    method   = new StyleRange();
	    comment  = new StyleRange();
	    variable = new StyleRange();
	    
//	    setRedraw(false);
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
	
	private int getFirstWhitespaceOnTheRight(int index, String text)
	{
		return getFirstWhitespace(index, text, 1);
	}
	
	private int getFirstWhitespaceOnTheLeft(int index, String text)
	{
		return getFirstWhitespace(index, text, -1);
	}
	
	private int getLastWhitespaceOnTheRight(int index, String text)
	{
		return getLastWhitespace(index, text, 1);
	}
	
	private int getLastWhitespaceOnTheLeft(int index, String text)
	{
		return getLastWhitespace(index, text, -1);
	}
	
	private int getFirstWhitespace(int index, String text, int stride)
	{
		return getFirstToken(index, text, stride, false);
	}
	
	private int getLastWhitespace(int index, String text, int stride)
	{
		return getLastToken(index, text, stride, false);
	}
	
	private int getFirstCharacterOnTheRight(int index, String text)
	{
		return getFirstCharacter(index, text, 1);
	}
	
	private int getFirstCharacterOnTheLeft(int index, String text)
	{
		return getFirstCharacter(index, text, -1);
	}
	
	private int getLastCharacterOnTheRight(int index, String text)
	{
		return getLastCharacter(index, text, 1);
	}
	
	private int getLastCharacterOnTheLeft(int index, String text)
	{
		return getLastCharacter(index, text, -1);
	}
	
	private int getFirstCharacter(int index, String text, int stride)
	{
		return getFirstToken(index, text, stride, true);
	}
	
	private int getLastCharacter(int index, String text, int stride)
	{
		return getLastToken(index, text, stride, true);
	}
	
	public int getFirstToken(int index, String text, int stride, boolean character)
	{
		char c = text.charAt(index);
		
		while (containsChar(whitespaceArray, c) == character && index + stride >= 0 && index + stride < text.length())
		{
			index += stride;
			
			c = text.charAt(index);
		}
		
		if (containsChar(whitespaceArray, c) != character)
		{
			return index;
		}
		
		return -1;
	}
	
	private int getLastToken(int index, String text, int stride, boolean character)
	{
		index  = getFirstToken(index, text, stride, character);
		
		if (index <= -1)
		{
			return -1;
		}
		
		char c = text.charAt(index);
		
		while (containsChar(whitespaceArray, c) != character && index + stride >= 0 && index + stride < text.length())
		{
			index += stride;
			
			c = text.charAt(index);
		}
		
		index -= stride;
		c      = text.charAt(index);
		
		if (containsChar(whitespaceArray, c) != character)
		{
			return index;
		}
		
		return -1;
	}
	
	private AdjacentWords checkAdjacent(int index, String text, String words[])
	{
		AdjacentWords adjacentWords = new AdjacentWords();
		
		adjacentWords.indices = new int[] { -1, -1 };
		adjacentWords.words   = new String[2];
		
		int l = 0;
		int r = 0;
		int w = 0;
		
		// The left adjacent word start index.
		if (index > 0)
		{
			l = getLastCharacterOnTheLeft(index - 1, text);
			w = getFirstWhitespaceOnTheRight(index - 1, text);
		}
		
		// The right adjacent word start index.
		r = getFirstCharacterOnTheRight(index, text);
		
		if (l < 0)
		{
			l = 0;
		}
		if (r < 0)
		{
			r = text.length() - 1;
		}
		
		for (int i = 0; i < words.length; i++)
		{
			String word = words[i];
			
			System.out.println(l + ", " + r + " " + text.charAt(l) + ", " + text.charAt(r));
			
			if (text.regionMatches(l, word, 0, word.length()) && l + word.length() >= w)
			{
				adjacentWords.indices[0] = l;
				adjacentWords.words[0]   = word;
			}
			else if (text.regionMatches(r, word, 0, word.length()) && w < r)
			{
				adjacentWords.indices[1] = r;
				adjacentWords.words[1]   = word;
			}
		}
		
		if (adjacentWords.indices[0] < 0 && adjacentWords.indices[1] < 0)
		{
			return null;
		}
		
		return adjacentWords;
	}
	
	public void highlightSyntax()
	{
		String text = getText();
		
		highlightSyntax(getCaretOffset(), text);
	}
	
	public void highlightSyntax(int caretOffset, String text)
	{
		int     i              = caretOffset;
		
		String  keywords[]     = new String[] { "test", "pool" };
		
		AdjacentWords index    = null;
		
		if (language != null)
		{
			if (language.getKeywords().length > 0)keywords = language.getKeywords();//rm this 'if' stmt
		}
		
		// Check to see if any keywords are around the current caret offset.
		index = checkAdjacent(i, text, keywords);
		
		if (index != null)
		{
			int indices[] = index.indices;
			
			for (int j = 0; j < indices.length; j++)
			{
				int charIndex = indices[j];
				
				if (charIndex < 0)
				{
					continue;
				}
				
				if (getStyleRangeAtOffset(charIndex) == null)
				{
					keyword.start      = charIndex;
					keyword.length     = index.words[j].length();
					keyword.foreground = new Color(DISPLAY, 255, 50, 50);
					
					setStyleRange(keyword);
				}
				else
				{
					index.indices[j] = -1;
					index.words[j]   = null;
				}
//				System.out.println("KEYWORD " + index);
			}
			
			if (index.words[0] == null ^ index.words[1] == null)
			{
				int l  = 0;
				int ml = 0;
				int mr = 0;
				int r  = 0;
				
				if (caretOffset > 0)
				{
					l  = getLastCharacterOnTheLeft(caretOffset - 1, text);
					ml = getFirstWhitespaceOnTheRight(l, text) - 1;
				}
				
				mr = getFirstCharacterOnTheRight(ml + 1, text);
				r  = getLastCharacterOnTheRight(mr, text);
				
				if (l < 0)
				{
					l = 0;
				}
				
				if (r < 0)
				{
					r = text.length() - 1;
				}
//				System.out.println((int)text.charAt(caretOffset - 1) + " " + (int)text.charAt(caretOffset - 2) + " " + (int)text.charAt(caretOffset - 3) + " " + (int)text.charAt(caretOffset - 4));
//				System.out.println(l + " " + ml + " " + mr + " " + r + " : " + caretOffset);
//				if (caretOffset > charIndex)
//				{
//					//
//				}
//				else if (text.regionMatches(l, keywords[indices[0]], 0, ml - l + 1))
//				{
//					System.out.println("r1");
//					replaceStyleRanges(mr, r - l + 1, new StyleRange[0]);
//				}
//				else
//				{
//					System.out.println("r2");
//					replaceStyleRanges(l, ml - l + 1, new StyleRange[0]);
//				}
			}
		}
		
		if (index == null || index.isEmpty())
		{
			int l = 0;
			int r = 0;
			
			if (caretOffset > 0)
			{
				l = getFirstWhitespaceOnTheLeft(caretOffset - 1, text) + 1;
				r = getLastCharacterOnTheRight(caretOffset - 1, text);
			}
			
			if (l < 0)
			{
				l = 0;
			}
			if (r < 0)
			{
				r = text.length() - 1;
			}
			
			replaceStyleRanges(l, r - l + 1, new StyleRange[0]);
		}
	}
	
	public void highlightAllSyntax()
	{
		String text = getText();
		
		int i = getFirstWhitespaceOnTheRight(0, text);
		
		while (i < text.length() - 1 && i > 0)
		{
//			if (i < 1000)
//			System.out.println(i + ", " + text.charAt(i));
			
			highlightSyntax(i, text);
			
			i = getFirstCharacterOnTheRight(i + 1, text);
			
			if (i < 0)
			{
				break;
			}

			i = getFirstWhitespaceOnTheRight(i + 1, text);
		}
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
		
		refresh();
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
		
//		System.out.println(lineNumberPanel.getSize());
		
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
	
	public void setSize(Point size)
	{
		setBounds(getX(), getY(), size.x, size.y);
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
	
	public void refresh()
	{
		refresh(0, getCharCount(), false);
	}
	
	public void refresh(int start, int length, boolean clearBackground)
	{
//		setRedraw(true);
		
		redrawRange(start, length, clearBackground);
		
//		setRedraw(true);
//		setRedraw(false);
	}
	
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
		
		Point size = composite.getSize();
		
		float x = width  / size.x;
		float y = height / size.y;
		
		if (x < 1 - 0.15f)
		{
			widthPercent = x;
		}
		if (y < 1 - 0.15f)
		{
			heightPercent = y;
		}
	}
	
	/**
	 * Update the size of this CodeField according to the percent values.
	 */
	private void updateSize()
	{
		Point size = composite.getSize();
		
		int width  = Math.round(widthPercent * size.x);
		int height = Math.round(heightPercent * size.y);
		
		setSize(width, height);
	}
	
	private class AdjacentWords
	{
		int		indices[];
		
		String	words[];
		
		public boolean isEmpty()
		{
			return indices == null || indices.length < 2 || indices[0] == -1 && indices[1] == -1;
		}
		
		public String toString()
		{
			if (indices != null && words != null)
			{
				return "{ " + words[0] + ": " + indices[0] + ", " + words[1] + ": " + indices[1] + " }";
			}
			
			return super.toString();
		}
	}
}