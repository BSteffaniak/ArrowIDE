package net.foxycorndog.arrowide.formatter;

import net.foxycorndog.arrowide.components.CodeField;
import net.foxycorndog.arrowide.language.CommentProperties;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

public class Formatter
{
	public static void indent(StyledText text)
	{
		// Save the selection at the start.
		Point sel = text.getSelection();
		int start = sel.x;
		
		String txt = text.getText();
		
		start--;
		
		while (start >= 0 && (txt.charAt(start) != '\n' && txt.charAt(start) != '\r'))
		{
			start--;
		}
		
		start++;
		
		int offset  = start;
		int offsetY = 0;
		
		while (offset < sel.y + offsetY)
		{
			text.setSelection(offset);
			text.insert("\t");
			offsetY++;
			
			int lineNum = text.getLineAtOffset(offset);
			
			offset += text.getLine(lineNum).length() + 2;
		}
		
		text.setSelection(sel.x + 1, sel.y + offsetY);
	}
	
	public static void unIndent(StyledText text)
	{
		// Save the selection at the start.
		Point sel = text.getSelection();
		int start = sel.x;
		
		int topPixel = text.getTopPixel();
		
		StringBuilder builder = new StringBuilder(text.getText());
		
		start--;
		
		while (start >= 0 && (builder.charAt(start) != '\n' && builder.charAt(start) != '\r'))
		{
			start--;
		}
		
		start++;
		
		int offset  = start;
		int offsetY = 0;
		
		StringBuilder txt = new StringBuilder(builder.subSequence(start, sel.y));
		
		builder.delete(start, sel.y);
		
		int locStart    = 0;
		int amount      = 0;
		int otherAmount = 0;
		
		int startOffset = txt.charAt(locStart) == '\t' ? 1 : 0;
		
		while (locStart < txt.length())
		{
			if (txt.charAt(locStart) == '\t')
			{
				txt.deleteCharAt(locStart);
				amount = 1;
				otherAmount += 1;
			}
			else
			{
				amount = 2;
				otherAmount += 0;
			}
			
			offsetY++;
			
			int lineNum = text.getLineAtOffset(offset);
			
			locStart += text.getLine(lineNum).length() + amount;
			
			offset = start + locStart + otherAmount;
		}
		
		builder.insert(start, txt);
		
		text.setText(builder.toString());
		
		text.setSelection(sel.x - startOffset, sel.y - otherAmount);//sel.x - 1, sel.y + offsetY);
		
		text.setTopPixel(topPixel);
	}
	
	public static void outcomment(StyledText text)
	{
		// Save the selection at the start.
		Point sel = text.getSelection();
		int start = sel.x;
		int end   = sel.y;
		
		// Save the text from the StyledText.
		String txt = text.getText();
		
		start--;

		// Put the start at the beginning of its line.
		while (start >= 0 && (txt.charAt(start) != '\n' && txt.charAt(start) != '\r'))
		{
			start--;
		}
		
		start++;

		// Put the end at the end of its line.
		while (end < txt.length() && (txt.charAt(end) != '\n' && txt.charAt(end) != '\r'))
		{
			end++;
		}
		
		int offset  = start;
		int offsetY = 0;

		// Determine whether to comment or uncomment the lines.
		boolean add = !allLinesStartWith(txt, "//", start, end);

		// Go through the lines and do the transformation.
		while (offset < end + offsetY)
		{
			int off = 0;
			
			if (add)
			{
				text.setSelection(offset);
				text.insert("//");
				off += 2;
			}
			else
			{
				int selOff = 0;
				
				text.setSelection(offset, offset + 2 + selOff);
				text.insert("");
				off -= 2;
			}
			
			offsetY += off;
			
			int lineNum = text.getLineAtOffset(offset);
			
			offset += text.getLine(lineNum).length() + 2;
		}
		
		int selEnd = sel.y + offsetY;

		// Make sure that the selection does not end on a \r or \n.
		if (!add && selEnd < 0 || selEnd >= text.getText().length())
		{
//			char c = 0;
//			
//			for (int i = selEnd; i >= 0; i--)
//			{
//				c = text.getText().charAt(i);
//				
//				if (c == '\r' || c == '\n')
//				{
//					selEnd--;
//				}
//				else
//				{
//					selEnd++;
//					break;
//				}
//			}

			char c = text.getText().charAt(selEnd - 1);
			
			if (c == '\r' || c == '\n')
			{
				selEnd--;
			}
		}
		
		int selStart = sel.x + (add ? 2 : -2);

		// Make sure the selection does not start on a \r or \n.
		if (!add && selStart < 0 || selStart >= text.getText().length())
		{
//			char c = 0;
//			
//			int length = text.getText().length();
//			
//			for (int i = selStart - 1; i < length; i++)
//			{
//				c = text.getText().charAt(i);
//				
//				if (c == '\r' || c == '\n')
//				{
//					selStart++;
//				}
//				else
//				{
//					selStart--;
//					break;
//				}
//			}
//			
			char c = text.getText().charAt(selStart - 1);
			
			if (c == '\r' || c == '\n')
			{
				selStart++;
			}
		}

		// Re select the text.
		text.setSelection(selStart, selEnd);
	}
	
	private static boolean lineStartsWith(String text, String startsWith, int start, int end)
	{
		for (int i = start; i <= end; i++)
		{
			if (text.charAt(i) == '\n')
			{
				for (int j = 0; j < startsWith.length(); j++)
				{
					int ind = i + j + 1;
					
					if (ind < text.length() && startsWith.charAt(j) != text.charAt(ind))
					{
						break;
					}
					
					if (j == startsWith.length() - 1)
					{
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	private static boolean allLinesStartWith(String text, String startsWith, int start, int end)
	{
		for (int i = start - 1; i <= end; i++)
		{
			if (i >= 0 && i < text.length() && text.charAt(i) == '\n' || i == start - 1)
			{
				for (int j = 0; j < startsWith.length(); j++)
				{
					int ind = i + j + 1;
					
					if (ind < text.length() && startsWith.charAt(j) != text.charAt(ind))
					{
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	public static void format(CodeField text)
	{
		CommentProperties commentProperties = text.getCommentProperties();
		
		if (commentProperties == null)
		{
			return;
		}
		
		StringBuilder builder = new StringBuilder(text.getText());
		
		int start  = 0;
		int offset = 0;
		
		StringBuilder tabs = new StringBuilder();
		
		while (start < builder.length())
		{
			int lineNum    = text.getLineAtOffset(offset);
			
			int lineLength = text.getLine(lineNum).length();
			
			int index      = start;
			
			int numRemoved = 0;
			
			while (((builder.charAt(index) == '\t') || (builder.charAt(index) == '\n') || (builder.charAt(index) == '\r')) && index < start + lineLength)
			{
				if (builder.charAt(index) == '\t')
				{
					builder.deleteCharAt(index);
					numRemoved++;
				}
				else
				{
					index++;
				}
			}
			
			start += lineLength + 2 - numRemoved;
			offset += lineLength + 2;
		}
		
		boolean commentStarted = false;
		boolean textStarted    = false;
		
		char textBeginning     = 0;
		
		int commentType        = 0;
		
		StringBuilder commentTransText = new StringBuilder();
		StringBuilder currentLineText  = new StringBuilder();
		
		int i = 0;
		
		while (i < builder.length())
		{
			char c = builder.charAt(i);
			
			if (!textStarted)
			{
				commentTransText.append(c);
				
				boolean isTrans =
						(!commentStarted && commentProperties.startsToStartComment(commentTransText.toString()))
						|| (commentStarted && commentProperties.startsToEndComment(commentTransText.toString()));
				
				if (!isTrans)
				{
					commentTransText = new StringBuilder();
				}
				
				int type = 0;
				
				if (!commentStarted && (type = commentProperties.startsComment(commentTransText.toString())) != 0)
				{
					commentStarted = true;
					
					commentType    = type;
					
					commentTransText = new StringBuilder();
				}
				else if (commentStarted && (type = commentProperties.endsComment(commentTransText.toString())) != 0)
				{
					commentStarted = false;
					
					commentType = 0;
					
					commentTransText = new StringBuilder();
				}
				
				if (c == '\n')
				{
					if (commentStarted && commentType == CommentProperties.SINGLE_LINE)
					{
						commentStarted = false;
						
						commentType = 0;
						
						commentTransText = new StringBuilder();
					}
				}
			}
			else if (!commentStarted)
			{
				if (c == '"' || c == '\'')
				{
					if (!textStarted)
					{
						textStarted = true;
						textBeginning = c;
					}
					else
					{
						if (c == textBeginning)
						{
							textStarted = false;
						}
					}
				}
			}
			
			if (!commentStarted && !textStarted)
			{
				currentLineText.append(c);
				
				if (c == '{')
				{
					if (currentLineText.length() > 0 && contains(currentLineText, new char[] { '\t', ' ' }, 0, currentLineText.length() - 1))
					{
						builder.insert(i, "\r\n");
					}
					else
					{
						tabs.append('\t');
					}
					
					char nextChar = nextChar(builder, i + 1, new char[] { ' ', '\t' } , commentProperties);
					
					if (nextChar != 0)
					{
						builder.insert(i + 1, "\r\n");
					}
				}
				else if (c == '}')
				{
					if (tabs.length() > 0)
					{
						tabs.deleteCharAt(0);
						
						builder.deleteCharAt(i - 1);
						
						i--;
					}
				}
				else if (c == ';')
				{
					char nextChar = nextChar(builder, i + 1, new char[] { ' ', '\t' } , commentProperties);
					
					if (nextChar != 0)
					{
						builder.insert(i + 1, "\r\n");// + tabs);
					}
				}
			}
			
			if (c == '\n')
			{
				builder.insert(i+1, tabs);
				
				i += tabs.length();
				
				currentLineText = new StringBuilder();
			}
			
			i++;
		}
		
		text.setText(builder.toString());
	}
	
	private static boolean contains(StringBuilder text, char chars[])
	{
		return contains(text, chars, 0, text.length());
	}
	
	private static boolean contains(StringBuilder text, char chars[], int start, int end)
	{
		for (int i = start; i < end; i++)
		{
			for (char c : chars)
			{
				if (text.charAt(i) == c)
				{
					return true;
				}
			}
		}
		
		return false;
	}
	
	private static char nextChar(StringBuilder text, int offset, char exceptions[], CommentProperties commentProperties)
	{
		return firstChar(text, offset, exceptions, commentProperties, 1);
	}
	
//	private static char prevChar(StringBuilder text, int offset, char exceptions[], CommentProperties commentProperties)
//	{
//		return firstChar(text, offset, exceptions, commentProperties, -1);
//	}
	
	private static char firstChar(StringBuilder text, int offset, char exceptions[], CommentProperties commentProperties, int stride)
	{
		if (stride == 0)
		{
			throw new IllegalArgumentException("Stride cannot be 0.");
		}
		
		boolean commentStarted = false;
		
		int commentType        = 0;
		
		StringBuilder commentTransText = new StringBuilder();
		
		while (offset < text.length())
		{
			// The character at the offset.
			char c = text.charAt(offset);
			
			offset += stride;
			
			if (c == '\r' || c == '\n')
			{
				// Stop iterating through the loop.
				break;
			}
			
			// Check to see if a comment started or ended.
			{
				if (stride > 0)
				{
					commentTransText.append(c);
				}
				else
				{
					commentTransText.insert(0, c);
				}
				
				boolean isTrans =
						(!commentStarted && commentProperties.startsToStartComment(commentTransText.toString()))
						|| (commentStarted && commentProperties.startsToEndComment(commentTransText.toString()));
				
				if (!isTrans)
				{
					if (!commentStarted)
					{
						return commentTransText.charAt(0);
					}

					commentTransText = new StringBuilder();
				}
				
				int type = 0;
				
				if (!commentStarted && (type = commentProperties.startsComment(commentTransText.toString())) != 0)
				{
					if (type == CommentProperties.SINGLE_LINE)
					{
						return 0;
					}
					
					commentStarted   = true;
					
					commentType      = type;
					
					commentTransText = new StringBuilder();
				}
				else if (commentStarted && (type = commentProperties.endsComment(commentTransText.toString())) != 0)
				{
					commentStarted   = false;
					
					commentType      = 0;

					commentTransText = new StringBuilder();
					
					c = 0;
				}
			}
			
			if (!commentStarted && commentTransText.length() <= 0)
			{
				boolean isException = false;
				
				// Loop through the exceptions to see if the char is an exception.
				for (char exc : exceptions)
				{
					if (c == exc)
					{
						isException = true;
						
						// Stop iterating through the loop.
						break;
					}
				}
				
				if (isException || c == 0)
				{
					// Skip the rest of the loop and move on to the next iteration.
					continue;
				}
				
				// Return the first character found other than the exceptions.
				return c;
			}
		}
		
		// Return that nothing was found.
		return 0;
	}
}