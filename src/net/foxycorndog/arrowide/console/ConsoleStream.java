package net.foxycorndog.arrowide.console;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.OutputStream;
import java.io.PrintStream;

public class ConsoleStream extends PrintStream
{
	private String          location;
	
	private ConsoleListener listener;
	
	public ConsoleStream(final String location) throws FileNotFoundException
	{
		super(location);
		
		final ConsoleStream thisStream = this;
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
			public void run()
			{
				thisStream.close();
				
				new File(location).delete();
			}
		});
		
		this.location = location;
	}
	
	public void println(String s)
	{
		super.println(s);
		
		if (listener != null)
		{
			listener.onPrintln(s + "\n");
		}
	}
	
//	public void print(String s)
//	{
//		super.print(s);
//		
//		if (listener != null)
//		{
//			if (s.endsWith("\n"))
//			{
//				listener.onPrintln(s + '\n');
//			}
//			else
//			{
//				listener.onPrint(s);
//			}
//		}
//	}
	
	public void addConsoleListener(ConsoleListener listener)
	{
		this.listener = listener;
	}
	
	public void setOutputStream(OutputStream os)
	{
		this.out = os;
	}
	
//	public void write(int i)
//	{
//		System.out.println("writeing in int");
//	}
//	
//	public void write(byte buffer[])
//	{
//		System.out.println("writeing in 1p");
//	}
//	
//	public void write(byte buffer[], int off, int len)
//	{
//		System.out.println("writeing in 3p");
//	}
}