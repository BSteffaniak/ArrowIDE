package net.foxycorndog.arrowide;

import java.util.ArrayList;

import net.foxycorndog.arrowide.command.Command;
import net.foxycorndog.arrowide.event.ProgramListener;

/**
 * Class used to organize the data that is used for each Program that
 * is ran.
 * 
 * @author	Braden Steffaniak
 * @since	Mar 23, 2013 at 11:11:12 PM
 * @since	v0.2
 * @version Mar 23, 2013 at 11:11:12 PM
 * @version	v0.2
 */
public class Program
{
	private boolean			running;
	
	private int				id;
	
	private String			name;
	
	private StringBuilder	text;
	
	private Process 		process;
	
	private Command			command;
	
	private ArrayList<ProgramListener>	listeners;
	
	/**
	 * Create a Program with the specified Process.
	 * 
	 * @param process The Process that is linked with this Program.
	 */
	public Program(Process process, String name)
	{
		this.process   = process;
		
		this.name      = name;
		
		this.text      = new StringBuilder();
		
		this.listeners = new ArrayList<ProgramListener>();
	}
	
	/**
	 * Get whether or not the program is running.
	 * 
	 * @return Whether or not the program is running.
	 */
	public boolean isRunning()
	{
		return running;
	}
	
	/**
	 * Set whether or not the program is running.
	 * 
	 * @param running Whether or not the program is running.
	 */
	public void setRunning(boolean running)
	{
		this.running = running;
		
		if (!running)
		{
			for (int i = listeners.size() - 1; i >= 0; i --)
			{
				listeners.get(i).programTerminated(this);
			}
		}
		else
		{
			for (int i = listeners.size() - 1; i >= 0; i --)
			{
				listeners.get(i).programStarted(this);
			}
		}
	}
	
	public void terminate() throws InterruptedException
	{
		listeners.clear();
		
		process.destroy();
		
		if (command != null)
		{
			command.terminate();
		}
		
		setRunning(false);
	}
	
	public Command getCommand()
	{
		return command;
	}
	
	public void setCommand(Command command)
	{
		this.command = command;
	}
	
	/**
	 * Get the tab id that this program is associated with.
	 * 
	 * @return The tab id that this program is associated with.
	 */
	public int getId()
	{
		return id;
	}
	
	/**
	 * Set the tab id that this program is associated with.
	 * 
	 * @param id The tab id that this program is associated with.
	 */
	public void setId(int id)
	{
		this.id = id;
	}
	
	/**
	 * Add the specified listener to the Program.
	 * 
	 * @param listener The ProgramListener to add.
	 */
	public void addListener(ProgramListener listener)
	{
		this.listeners.add(listener);
	}
	
	/**
	 * Get the name of the Program.
	 * 
	 * @return The name of the Program.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * Get the process that is linked with this Program.
	 * 
	 * @return The process that is linked with this Program.
	 */
	public Process getProcess()
	{
		return process;
	}
	
	/**
	 * Get the text of the Program's console.
	 * 
	 * @return The text of the Program's console.
	 */
	public String getText()
	{
		return text.toString();
	}
	
	/**
	 * Append a String to the text of the Program.
	 * 
	 * @param text The String to append.
	 */
	public void append(String text)
	{
		this.text.append(text);
		
		for (int i = listeners.size() - 1; i >= 0; i --)
		{
			listeners.get(i).messageReceived(text);
		}
	}
}