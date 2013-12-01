package net.foxycorndog.arrowide.language;

import org.eclipse.swt.graphics.Color;

public class IdentifierProperties
{
	private final int		AMOUNT;
	
	public  final Color		COLOR;
	
	public  final char		PREVIOUS_CHARS[][], NEXT_CHARS[][], SKIP_NEXT_CHARS[][], SKIP_PREV_CHARS[][];
	
	public  final String	PREFIXES[][], PREVIOUS_WORDS[][], NEXT_WORDS[][];
	
	public IdentifierProperties(char previousChars, char nextChars, Color color)
	{
		this(new char[] { previousChars }, new char[] { nextChars }, new String[] {}, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars[], char nextChars, Color color)
	{
		this(previousChars, new char[] { nextChars }, new String[] {}, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars[], Color color)
	{
		this(new char[] { previousChars }, nextChars, new String[] {}, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars[], char nextChars[], Color color)
	{
		this(previousChars, nextChars, new String[] {}, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars, String prefix, Color color)
	{
		this(new char[] { previousChars }, new char[] { nextChars }, new String[] { prefix }, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars[], char nextChars, String prefix, Color color)
	{
		this(previousChars, new char[] { nextChars }, new String[] { prefix }, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars[], String prefix, Color color)
	{
		this(new char[] { previousChars }, nextChars, new String[] { prefix }, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars, String prefixes[], Color color)
	{
		this(new char[] { previousChars }, new char[] { nextChars }, prefixes, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars[], char nextChars, String prefixes[], Color color)
	{
		this(previousChars, new char[] { nextChars }, prefixes, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars[], String prefixes[], Color color)
	{
		this(new char[] { previousChars }, nextChars, prefixes, new String[] {}, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars, String prefix, String previousWords, Color color)
	{
		this(new char[] { previousChars }, new char[] { nextChars }, new String[] { prefix }, new String[] { previousWords }, color);
	}
	
	public IdentifierProperties(char previousChars[], char nextChars, String prefix, String previousWords, Color color)
	{
		this(previousChars, new char[] { nextChars }, new String[] { prefix }, new String[] { previousWords }, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars[], String prefix, String previousWords, Color color)
	{
		this(new char[] { previousChars }, nextChars, new String[] { prefix }, new String[] { previousWords }, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars, String prefixes[], String previousWords[], Color color)
	{
		this(new char[] { previousChars }, new char[] { nextChars }, prefixes, previousWords, color);
	}
	
	public IdentifierProperties(char previousChars[], char nextChars, String prefixes[], String previousWords[], Color color)
	{
		this(previousChars, new char[] { nextChars }, prefixes, previousWords, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars[], String prefixes[], String previousWords[], Color color)
	{
		this(new char[] { previousChars }, nextChars, prefixes, previousWords, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars, String prefix, String previousWords[], Color color)
	{
		this(new char[] { previousChars }, new char[] { nextChars }, new String[] { prefix }, previousWords, color);
	}
	
	public IdentifierProperties(char previousChars[], char nextChars, String prefix, String previousWords[], Color color)
	{
		this(previousChars, new char[] { nextChars }, new String[] { prefix }, previousWords, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars[], String prefix, String previousWords[], Color color)
	{
		this(new char[] { previousChars }, nextChars, new String[] { prefix }, previousWords, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars, String prefixes[], String previousWords, Color color)
	{
		this(new char[] { previousChars }, new char[] { nextChars }, prefixes, new String[] { previousWords }, color);
	}
	
	public IdentifierProperties(char previousChars[], char nextChars, String prefixes[], String previousWords, Color color)
	{
		this(previousChars, new char[] { nextChars }, prefixes, new String[] { previousWords }, color);
	}
	
	public IdentifierProperties(char previousChars, char nextChars[], String prefixes[], String previousWords, Color color)
	{
		this(new char[] { previousChars }, nextChars, prefixes, new String[] { previousWords }, color);
	}
	
	public IdentifierProperties(char previousChars[], char nextChars[], String prefixes[], String previousWords[], Color color)
	{
		this(new char[][] { previousChars }, new char[][] { nextChars }, new char[][] {}, new char[][] {}, new String[][] { prefixes }, new String[][] { previousWords }, new String[][] {}, color);
	}
	
	public IdentifierProperties(char previousChars[][], char nextChars[][], char skipPrevChars[][], char skipNextChars[][], String prefixes[][], String previousWords[][], String nextWords[][], Color color)
	{
		this.COLOR           = color;
		
		this.PREVIOUS_CHARS  = previousChars;
		this.NEXT_CHARS      = nextChars;
		
		this.SKIP_PREV_CHARS = skipPrevChars;
		this.SKIP_NEXT_CHARS = skipNextChars;
		
		this.PREFIXES        = prefixes;
		this.PREVIOUS_WORDS  = previousWords;
		this.NEXT_WORDS      = nextWords;
		
		this.AMOUNT          = Math.max(Math.max(Math.max(Math.max(previousWords.length, prefixes.length), nextChars.length), previousChars.length), NEXT_WORDS.length);
	}
	
	public boolean isQualified(char previousChars[], char nextChars[], String currentWord, String previousWord, String nextWord)
	{
		if (AMOUNT <= 0)
		{
			return true;
		}
		
		for (int i = 0; i < AMOUNT; i++)
		{
			boolean found = previousChars == null || PREVIOUS_CHARS.length <= i || PREVIOUS_CHARS[i].length <= 0;
			
			for (int j = 0; !found && j < PREVIOUS_CHARS[i].length; j++)
			{
				for (int q = 0; q < previousChars.length; q++)
				{
					if (PREVIOUS_CHARS[i][j] == previousChars[q])
					{
						found = true;
					}
					else
					{
						if (i < SKIP_PREV_CHARS.length)
						{
							for (int n = 0; n < SKIP_PREV_CHARS[i].length; n++)
							{
								if (previousChars[q] == SKIP_PREV_CHARS[i][n])
								{
//									skipped = true;
								}
							}
						}
					}
				}
			}
			
			if (!found)
			{
				continue;
			}
			
			found = nextChars == null || NEXT_CHARS.length <= i || NEXT_CHARS[i].length <= 0;
			
			for (int j = 0; !found && j < NEXT_CHARS[i].length; j++)
			{
				for (int q = 0; q < nextChars.length; q++)
				{
					if (NEXT_CHARS[i][j] == nextChars[q])
					{
						found = true;
					}
					else
					{
						if (i < SKIP_NEXT_CHARS.length)
						{
							for (int n = 0; n < SKIP_NEXT_CHARS[i].length; n++)
							{
								if (nextChars[q] == SKIP_NEXT_CHARS[i][n])
								{
									
								}
							}
						}
					}
				}
			}
			
			if (!found)
			{
				continue;
			}
			
			found = PREVIOUS_WORDS.length <= i || PREVIOUS_WORDS[i].length <= 0;
			
			for (int j = 0; !found && j < PREVIOUS_WORDS[i].length; j++)
			{
				if (PREVIOUS_WORDS[i][j].equals(previousWord))
				{
					found = true;
				}
			}
			
			if (!found)
			{
				continue;
			}
			
			found = NEXT_WORDS.length <= i || NEXT_WORDS[i].length <= 0;
			
			for (int j = 0; !found && j < NEXT_WORDS[i].length; j++)
			{
				if (NEXT_WORDS[i][j].equals(nextWord))
				{
					found = true;
				}
			}
			
			if (!found)
			{
				continue;
			}
			
			found = PREFIXES.length <= i || PREFIXES[i].length <= 0;
			
			for (int j = 0; !found && j < PREFIXES[i].length; j++)
			{
				if (currentWord.startsWith(PREFIXES[i][j]))
				{
					found = true;
				}
			}
			
			if (!found)
			{
				continue;
			}
			
			return true;
		}
		
		return false;
	}
}