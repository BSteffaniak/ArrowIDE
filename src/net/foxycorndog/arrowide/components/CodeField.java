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
	private volatile boolean							syntaxHighlighterRunning;

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
	
	private Thread										syntaxHighlighter, highlightWordThread, removeHighlightThread;

	private LineStyleListener							lineNumbers, lineSpaces, syntaxHighlighting;
	
	private LineNumberPanel								lineNumberPanel;
	
	private StyledText									lineNumberText;

	private Composite									composite;

	private CodeField									thisField;
	
	private Language									language;
	
	private StyleRange									keyword[], method[], comment[], identifier[];

	// private ArrayList<ArrayList<Boolean>> tabs;

	private ArrayList<ContentListener>					contentListeners;
	private ArrayList<CodeFieldListener>				codeFieldListeners;
	
	private HashMap<String, Color>						colorCache;
	
	private static int									ids;

	private static final String							whitespaceRegex;

	private static final char							whitespaceArray[], symbolArray[];
	
	static
	{
//		whitespaceRegex = "[.,[ ]/*=()\r\n\t\\[\\]{};[-][+]['][\"]:[-][+]><!]";
		whitespaceRegex = "[.,[ ]/*=()\r\n\t[\\\\]\\[\\]{};[-][+]['][\"]:[-][+]><!]";
		
		whitespaceArray = new char[] { ' ', '\n', '\t', '\r' };
		symbolArray     = new char[] { '.', ',', '/', '*', '=', '(', ')', '[', ']', '{', '}', ';', '-', '\\', '+', '\'', '"', ':', '-', '+', '>', '<', '!', '&', '|', '#' };
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
		
		colorCache         = new HashMap<String, Color>();
		
//		syntaxHighlighting = new LineStyleListener()
//	    {
//			public void lineGetStyle(LineStyleEvent event)
//			{
//				StyleRange styles[] = thisField.getStyles();
//				System.out.println("ASdf");
//				event.styles = styles;
//			}
//	    };
	    
	    syntaxHighlighter = new Thread()
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
				highlightSyntax();
				
//				AdjacentWords o = getAdjacentWords(getCaretOffset(), getText());
//				
//				AdjacentWords a = filterAdjacentWords(getCaretOffset(), getText(), language.getKeywords(), o);
//				
//				System.out.println(a.words[0] + ", " + a.words[1]);
				
//				highlightSyntax();
				
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
	
	private int getFirstSymbolOnTheRight(int index, String text)
	{
		return getFirstSymbol(index, text, 1);
	}
	
	private int getFirstSymbolOnTheLeft(int index, String text)
	{
		return getFirstSymbol(index, text, -1);
	}
	
	private int getLastSymbolOnTheRight(int index, String text)
	{
		return getLastSymbol(index, text, 1);
	}
	
	private int getLastSymbolOnTheLeft(int index, String text)
	{
		return getLastSymbol(index, text, -1);
	}
	
	private int getFirstSymbol(int index, String text, int stride)
	{
		return getFirstToken(index, text, stride, true, symbolArray);
	}
	
	private int getLastSymbol(int index, String text, int stride)
	{
		return getLastToken(index, text, stride, true, symbolArray);
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
		return getFirstToken(index, text, stride, true, whitespaceArray);
	}
	
	private int getLastWhitespace(int index, String text, int stride)
	{
		return getLastToken(index, text, stride, true, whitespaceArray);
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
		return getFirstToken(index, text, stride, false, whitespaceArray, symbolArray);
	}
	
	private int getLastCharacter(int index, String text, int stride)
	{
		return getLastToken(index, text, stride, false, whitespaceArray, symbolArray);
	}
	
	private int getFirstSymbolspaceOnTheRight(int index, String text)
	{
		return getFirstSymbolspace(index, text, 1);
	}
	
	private int getFirstSymbolspaceOnTheLeft(int index, String text)
	{
		return getFirstSymbolspace(index, text, -1);
	}
	
	private int getLastSymbolspaceOnTheRight(int index, String text)
	{
		return getLastSymbolspace(index, text, 1);
	}
	
	private int getLastSymbolspaceOnTheLeft(int index, String text)
	{
		return getLastSymbolspace(index, text, -1);
	}
	
	private int getFirstSymbolspace(int index, String text, int stride)
	{
		return getFirstToken(index, text, stride, true, whitespaceArray, symbolArray);
	}
	
	private int getLastSymbolspace(int index, String text, int stride)
	{
		return getLastToken(index, text, stride, true, whitespaceArray, symbolArray);
	}
	
	private int getFirstToken(int index, String text, int stride, boolean checkFor, char[] ... tokenTypes)
	{
		if (index < 0 || index >= text.length())
		{
			return -1;
		}
		
		char c = text.charAt(index);
		
		while (containsChar(c, tokenTypes) != checkFor && index + stride >= 0 && index + stride < text.length())
		{
			index += stride;
			
			c = text.charAt(index);
		}
		
		if (containsChar(c, tokenTypes) == checkFor)
		{
			return index;
		}
		
		return -1;
	}
	
	private int getLastToken(int index, String text, int stride, boolean checkFor, char[] ... tokenTypes)
	{
		index = getFirstToken(index, text, stride, checkFor, tokenTypes);
		
		if (index < 0 || index >= text.length())
		{
			return -1;
		}
		
		char c = text.charAt(index);
		
		while (containsChar(c, tokenTypes) == checkFor)
		{
			index += stride;
			
			if (index < 0 || index >= text.length())
			{
				break;
			}
			
			c = text.charAt(index);
		}
		
		index -= stride;
		c      = text.charAt(index);
		
		if (containsChar(c, tokenTypes) == checkFor)
		{
			return index;
		}
		
		return -1;
	}
	
	private AdjacentWords getAdjacentWords(int index, String text)
	{
		AdjacentWords adjacentWords = new AdjacentWords();
		
		// The left adjacent word start index.
		int l = getLastCharacterOnTheLeft(index, text);
		
		// The right adjacent word start index.
		int r = getLastSymbolspaceOnTheRight(index, text) + 1;
		
		if (l < 0)
		{
			l = 0;
		}
		if (r > text.length() - 1)
		{
			r = text.length();
		}
		
		// The right whitespace -- see what I did there?
		int rightspace = getLastCharacterOnTheRight(l, text);
		
		if (rightspace < 0 || rightspace > text.length())
		{
			rightspace = text.length() - 1;
		}
		
		String word = text.substring(l, rightspace + 1);
		
		adjacentWords.indices[0] = l;
		adjacentWords.words[0]   = word;
		
		// Check the word on the right now ...
		rightspace = getLastCharacterOnTheRight(r, text);
		
		if (rightspace < 0 || rightspace > text.length())
		{
			rightspace = text.length() - 1;
		}
		
		word = text.substring(r, rightspace + 1);
		
		// If the right word isn't the same as the left.
		if (r != l)
		{
			adjacentWords.indices[1] = r;
			adjacentWords.words[1]   = word;
		}
		
		return adjacentWords;
	}
	
	private AdjacentWords filterAdjacentWords(int index, int type, String words[][], AdjacentWords adjacentWords, String text)
	{
		return filterAdjacentWords(index, type, words, new String[0], new String[0], adjacentWords, text);
	}

	private AdjacentWords filterAdjacentWords(int index, int type, AdjacentWords adjacentWords, String text)
	{
		return filterAdjacentWords(index, type, new String[1][1], new String[0], new String[0], adjacentWords, text);
	}

	private AdjacentWords filterAdjacentWords(int index, int type, String prefix[], String postfix[], AdjacentWords adjacentWords, String text)
	{
		return filterAdjacentWords(index, type, new String[1][1], prefix, postfix, adjacentWords, text);
	}
	
	private AdjacentWords filterAdjacentWords(int index, int type, String words[][], String prefix[], String postfix[], AdjacentWords adjacentWords, String text)
	{
		AdjacentWords aw = new AdjacentWords();
		
		for (int j = 0; j < words.length; j++)
		{
			for (int i = 0; i < words[j].length; i++)
			{
				String word = words[j][i];
				
				int typeIndex = j;
				
				checkWords(adjacentWords, aw, word, prefix, postfix, type, typeIndex, text);
			}
		}
		
		return aw;
	}
	
	private char[] constrictArrayOnTheLeft(int index, char arrayAllowed[], char arrayConstraints[], String text)
	{
		return constrictArray(index, arrayAllowed, arrayConstraints, -1, text);
	}
	
	private char[] constrictArrayOnTheRight(int index, char arrayAllowed[], char arrayConstraints[], String text)
	{
		return constrictArray(index, arrayAllowed, arrayConstraints, 1, text);
	}
	
	private char[] constrictArray(int index, char arrayAllowed[], char arrayConstraints[], int stride, String text)
	{
		ArrayList<Character> chars = new ArrayList<Character>();
		
		char c = 0;
		
		boolean allowed   = false;
		boolean constrict = false;
		
		do
		{
			c = text.charAt(index);
			
			allowed   = containsChar(c, arrayAllowed);
			constrict = containsChar(c, arrayConstraints);
			
			if (allowed)
			{
				chars.add(c);
			}
			
			index += stride;
		}
		while ((allowed || constrict) && index >= 0 && index < text.length());
		
		char array[] = new char[chars.size()];
		
		for (int i = chars.size() - 1; i >= 0; i--)
		{
			array[i] = chars.get(i);
		}
		
		return array;
	}
	
	private char[] reverse(char chars[])
	{
		char newArray[] = new char[chars.length];
		
		for (int i = 0; i < chars.length; i++)
		{
			newArray[i] = chars[chars.length - 1 - i];
		}
		
		return newArray;
	}
	
	private boolean isValid(String word, int index, String prefix[], String postfix[], String text)
	{
		if (index < 0)
		{
			return false;
		}
		
		int i = getFirstSymbolOnTheLeft(index, text);
		
		if (i < 0)
		{
			// If a prefix was needed and it didn't have one.
			if (prefix.length > 0)
			{
				return false;
			}
		}
		else
		{
			char   chars[] = constrictArrayOnTheLeft(i, symbolArray, whitespaceArray, text);
			
			chars = reverse(chars);
			
			String str     = String.valueOf(chars);
			
			boolean hasPrefix = prefix.length <= 0;
			
			for (int j = 0; j < prefix.length; j++)
			{
				if (str.startsWith(prefix[j]))
				{
					hasPrefix = true;
					
					// Break out of the current loop.
					break;
				}
			}
			
			if (!hasPrefix)
			{
				return false;
			}
		}
		
		i = getFirstSymbolOnTheRight(index + word.length(), text);
		
		if (i < 0)
		{
			if (postfix.length > 0)
			{
				return false;
			}
		}
		else
		{
			char chars[] = constrictArrayOnTheRight(i, symbolArray, whitespaceArray, text);
			
			String str   = String.valueOf(chars);
			
			boolean hasPostfix = postfix.length <= 0;
			
			for (int j = 0; j < postfix.length; j++)
			{
				if (str.startsWith(postfix[j]))
				{
					hasPostfix = true;
					
					if (str.startsWith(";"))
					{
						System.out.println("asdf " + word);
					}
					
					// Break out of the current loop.
					break;
				}
			}
			
			if (!hasPostfix)
			{
				return false;
			}
		}
		
		return true;
	}
	
	private void checkWords(AdjacentWords src, AdjacentWords dst, String word, String prefix[], String postfix[], int type, int typeIndex, String text)
	{
		for (int i = 0; i < src.words.length; i++)
		{
			if (word == null || word.equals(src.words[i]))
			{
				if (isValid(src.words[i], src.indices[i], prefix, postfix, text))
				{
					Color color = getColor(type, typeIndex);
					
					dst.indices[i]        = src.indices[i];
					dst.words[i]          = src.words[i];
					dst.typeIndices[i]    = typeIndex;
					dst.types[i]          = type;
					dst.colors[i]         = color;
					
					// If the words are at the same location, only do it once.
					if (src.indices[0] == src.indices[1])
					{
						// Break out of the current loop.
						break;
					}
				}
			}
		}
	}
	
	public void highlightSyntax()
	{
		String text = getText();
		
		highlightSyntax(getCaretOffset(), text);
	}
	
	public void highlightSyntax(final int caretOffset, final String text)
	{
		final AdjacentWords oldWords = getAdjacentWords(caretOffset, text);
		final AdjacentWords metWords = getAdjacentMethods(caretOffset, text, oldWords);
		final AdjacentWords idWords  = getAdjacentIdentifiers(caretOffset, text, oldWords);
		final AdjacentWords newWords = getAdjacentKeywords(caretOffset, text, oldWords);
		
		metWords.merge(idWords);
		newWords.merge(metWords);
		
		/* Highlight the words found, if any were.
		 */
		if (!newWords.isEmpty())
		{
			highlightWordThread = new Thread(new Runnable()
			{
				public void run()
				{
					for (int i = 0; i < newWords.indices.length; i++)
					{
						if (newWords.indices[i] < 0)
						{
							continue;
						}
						
						if (getStyleRangeAtOffset(newWords.indices[i]) == null)
						{
							StyleRange range = null;
							
							int typeIndex    = newWords.typeIndices[i];
							
							if (newWords.types[i] == AdjacentWords.KEYWORD)
							{
								keyword[typeIndex].start      = newWords.indices[i];
								keyword[typeIndex].length     = newWords.words[i].length();
								keyword[typeIndex].foreground = newWords.colors[i];
								
								range = keyword[typeIndex];
							}
							else if (newWords.types[i] == AdjacentWords.METHOD)
							{
								method[typeIndex].start      = newWords.indices[i];
								method[typeIndex].length     = newWords.words[i].length();
								method[typeIndex].foreground = newWords.colors[i];
								
								range = method[typeIndex];
							}
							else if (newWords.types[i] == AdjacentWords.IDENTIFIER)
							{
								identifier[typeIndex].start      = newWords.indices[i];
								identifier[typeIndex].length     = newWords.words[i].length();
								identifier[typeIndex].foreground = newWords.colors[i];
								
								range = identifier[typeIndex];
							}
							
							if (range != null)
							{
								setStyleRange(range);
							}
						}
					}
				}
			}, "Highlight Word Thread");
			
			DISPLAY.syncExec(highlightWordThread);
		}
		
		/* If we haven't already highlighted the words around us, look for
		 * methods to highlight.
		 */
		if (!newWords.isFull())
		{
			removeHighlightThread = new Thread(new Runnable()
			{
				public void run()
				{
					for (int i = 0; i < newWords.indices.length; i++)
					{
						StyleRange range = null;
						
						int        index = oldWords.indices[i];
						
						if (index < 0 || index >= text.length()) continue;
						if ((range = getStyleRangeAtOffset(index)) != null);
						else if (index <= caretOffset && index + oldWords.words[i].length() > caretOffset && (range = getStyleRangeAtOffset(caretOffset)) != null);
						else continue;
						
						if (newWords.words[i] == null && range != null)
						{
							replaceStyleRanges(range.start, oldWords.words[i].length(), new StyleRange[0]);
						}
					}
				}
			}, "Remove Highlight Thread");
			
			DISPLAY.syncExec(removeHighlightThread);
		}
	}
	
	private AdjacentWords getAdjacentKeywords(final int caretOffset, final String text)
	{
		final AdjacentWords oldWords = getAdjacentWords(caretOffset, text);
		
		return getAdjacentKeywords(caretOffset, text, oldWords);
	}
	
	private AdjacentWords getAdjacentKeywords(final int caretOffset, final String text, final AdjacentWords oldWords)
	{
		int     i              = caretOffset;
		
		String  keywords[][]   = language.getKeywords();
		
		// Check to see if any keywords are around the current caret offset.
		AdjacentWords newWords = filterAdjacentWords(i, AdjacentWords.KEYWORD, keywords, oldWords, text);
		
		return newWords;
	}
	
	private AdjacentWords getAdjacentMethods(int caretOffset, String text)
	{
		AdjacentWords oldWords = getAdjacentWords(caretOffset, text);
		
		return getAdjacentMethods(caretOffset, text, oldWords);
	}
	
	private AdjacentWords getAdjacentMethods(int caretOffset, String text, final AdjacentWords oldWords)
	{
		int i = caretOffset;
		
		// Check to see if any keywords are around the current caret offset.
		AdjacentWords newWords = filterAdjacentWords(i, AdjacentWords.METHOD, new String[0], new String[] { "(" }, oldWords, text);
		
		return newWords;
	}
	
	private AdjacentWords getAdjacentIdentifiers(int caretOffset, String text)
	{
		AdjacentWords oldWords = getAdjacentWords(caretOffset, text);
		
		return getAdjacentIdentifiers(caretOffset, text, oldWords);
	}
	
	private AdjacentWords getAdjacentIdentifiers(int caretOffset, String text, final AdjacentWords oldWords)
	{
		int i = caretOffset;
		
		// Check to see if any keywords are around the current caret offset.
		AdjacentWords newWords = filterAdjacentWords(i, AdjacentWords.IDENTIFIER, new String[0], new String[] { ";", "=" }, oldWords, text);
		
		return newWords;
	}
	
	public void highlightAllSyntax()
	{
//		if (true)return;
		if (language == null)
		{
			return;
		}
		
		final String text = getText();
		
		syntaxHighlighter = new Thread("Syntax Highlighter Thread")
		{
			public void run()
			{
				int i = getLastCharacterOnTheRight(0, text);
				
				while (i >= 0 && i < text.length() - 1 && syntaxHighlighterRunning)
				{
					highlightSyntax(i, text);
					
					i = getLastCharacterOnTheRight(i + 1, text);
					
					if (i < 0 || i >= text.length() - 1 || !syntaxHighlighterRunning)
					{
						break;
					}
					
					i = getLastCharacterOnTheRight(i + 1, text);
				}
				
				syntaxHighlighterRunning = false;
				syntaxHighlighter        = null;
			}
		};
		
		syntaxHighlighterRunning = true;
		
		syntaxHighlighter.start();
	}
	
	public void stopHighlighting() throws InterruptedException
	{
		if (syntaxHighlighter == null || highlightWordThread == null || removeHighlightThread == null)
		{
			return;
		}
		
		syntaxHighlighterRunning = false;
		
		highlightWordThread.join();
		removeHighlightThread.join();
		syntaxHighlighter.join();
	}
	
	private Color getColor(String color)
	{
		if (language == null)
		{
			return null;
		}
		
		if (colorCache.containsKey(color))
		{
			return colorCache.get(color);
		}
		
		String values[] = color.split(" ");
		
		int    rgb[]    = new int[3];
		
		for (int i = 0; i < rgb.length; i++)
		{
			if (values.length > i)
			{
				rgb[i] = Integer.valueOf(values[i]);
			}
			else
			{
				rgb[i] = rgb[i - 1];
			}
		}
		
		Color col = new Color(DISPLAY, rgb[0], rgb[1], rgb[2]);
		
		colorCache.put(color, col);
		
		return col;
	}
	
	private Color getColor(int type, int keywordIndex)
	{
		if (language == null || keywordIndex < 0 || type < 1)
		{
			return null;
		}
		
		String word = null;
		
		if (type == AdjacentWords.KEYWORD)
		{
			word = "keywords";
		}
		else if (type == AdjacentWords.METHOD)
		{
			word = "methods";
		}
		else if (type == AdjacentWords.IDENTIFIER)
		{
			word = "identifiers";
		}
		
		String col = language.getAttribute("language." + word + ">color>" + keywordIndex);
		
		return getColor(col);
	}
	
	private boolean containsChar(char key, char[] ... chars)
	{
		for (int i = 0; i < chars.length; i ++)
		{
			for (int j = 0; j < chars[i].length; j++)
			{
				if (key == chars[i][j])
				{
					return true;
				}
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
		try
		{
			stopHighlighting();
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		
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
		this.language = language;
		
		if (language == null)
		{
			return;
		}
		
		int keywords    = language.getItems("language.keywords").length;
		int methods     = language.getItems("language.methods").length;
		int comments    = language.getItems("language.comments").length;
		int identifiers = language.getItems("language.identifiers").length;
		
	    keyword    = new StyleRange[keywords];
	    method     = new StyleRange[methods];
	    comment    = new StyleRange[comments];
	    identifier = new StyleRange[identifiers];
	    
	    for (int i = 0; i < keywords; i++)
	    {
	    	keyword[i] = new StyleRange();
	    }
	    for (int i = 0; i < methods; i++)
	    {
	    	method[i] = new StyleRange();
	    }
	    for (int i = 0; i < comments; i++)
	    {
	    	comment[i] = new StyleRange();
	    }
	    for (int i = 0; i < identifiers; i++)
	    {
	    	identifier[i] = new StyleRange();
	    }
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
		private int		indices[];
		private int		typeIndices[];
		private int		types[];
		
		private String	words[];
		
		private Color	colors[];
		
		public static final int KEYWORD = 1, METHOD = 2, IDENTIFIER = 3;
		
		public AdjacentWords()
		{
			indices     = new int[] { -1, -1 };
			types       = new int[] { 0, 0 };
			typeIndices = new int[2];
			words       = new String[2];
			colors      = new Color[2];
		}
		
		public boolean isEmpty()
		{
			return size() <= 0;
		}
		
		public boolean isFull()
		{
			return size() >= 2;
		}
		
		public void clear(int index)
		{
			indices[index]     = -1;
			typeIndices[index] = -1;
			types[index]       =  0;
			words[index]       = null;
			colors[index]      = null;
		}
		
		public int size()
		{
			if (words == null)
			{
				return 0;
			}
			
			int num = 0;
			
			if (words[0] != null)
			{
				++num;
			}
			if (words[1] != null)
			{
				++num;
			}
			
			return num;
		}
		
		public void merge(AdjacentWords target)
		{
			for (int i = 0; i < colors.length; i++)
			{
				if (target.colors[i] != null && colors[i] == null)
				{
					indices[i]     = target.indices[i];
					typeIndices[i] = target.typeIndices[i];
					types[i]       = target.types[i];
					words[i]       = target.words[i];
					colors[i]      = target.colors[i];
				}
			}
		}
		
		public String toString()
		{
			if (indices != null && words != null)
			{
				return "{ " + words[0] + " at " + indices[0] + ", " + words[1] + " at " + indices[1] + " }";
			}
			
			return super.toString();
		}
	}
}