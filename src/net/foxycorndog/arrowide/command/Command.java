package net.foxycorndog.arrowide.command;

import static net.foxycorndog.arrowide.ArrowIDE.CONFIG_DATA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.foxycorndog.arrowide.Program;
import net.foxycorndog.arrowide.event.ProgramListener;

import org.eclipse.swt.widgets.Display;

/**
 * Class that is used to issue a command to the Operating System.
 * 
 * @author	Braden Steffaniak
 * @since	Nov 23, 2013 at 5:32:00 PM
 * @since	v0.7.5
 * @version Nov 23, 2013 at 5:32:00 PM
 * @version	v0.7.6
 */
public class Command
{
	private String                      directory;
	
	private Program						program;
	
	private Display						display;
	
	private Thread						commandThread, commandThread2;
	
	private String						commands[];

	private ArrayList<CommandListener>	listeners;
	
    /**
     * Create a command with the specified Display instance, command,
     * and directory to issue the command in.
     * 
     * @param display The Display instance to use while issuing the
     *         Command.
     * @param command The shell command to issue to the Operating System.
     * @param directory The directory to issue the command in.
     */
	public Command(Display display, String command, String directory)
	{
		List<String> list = new ArrayList<String>();
		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(command);
		
		while (m.find())
		{
			String s = m.group(1);
			
			s = (s.startsWith("\"") || s.startsWith("'")) && (s.endsWith("\"") || s.endsWith("'")) ? s.substring(1, s.length() - 1) : s;
			
		    list.add(s);
		}
		
		init(display, list.toArray(new String[0]), directory);
	}
	
    /**
     * Create a command with the specified Display instance, commands,
     * and directory to issue the commands in.
     * 
     * @param display The Display instance to use while issuing the
     *         Commands.
     * @param commands The list of shell commands to issue to the
     *         Operating System.
     * @param directory The directory to issue the commands in.
     */
	public Command(Display display, String commands[], String directory)
	{
//		this.commands = commands;
		init(display, commands, directory);
	}
	
	private void init(Display display, String command[], String directory)
	{
		this.display   = display;
		
		this.directory = directory;
		
		this.commands  = command;
		
		listeners      = new ArrayList<CommandListener>();
	}
	
	public void execute() throws IOException
	{
		execute(null, null);
	}
	
	public void execute(String title, ProgramListener listener) throws IOException
	{
//		System.out.println(Arrays.asList(commands) + ", " + directory);
		
		ProcessBuilder builder = new ProcessBuilder(commands);
		
		if (directory != null)
		{
			builder.directory(new File(directory));
		}
		
		final File error = File.createTempFile("processError", ".txt");
		final File input = File.createTempFile("processInput", ".txt");
		
		final InputStream errorStream = new FileInputStream(error);
		final InputStream inputStream = new FileInputStream(input);
		
//		builder.redirectError(error);
//		builder.redirectInput(input);
		
		final Result     result = new Result();
		final ExecValues values = new ExecValues();
		
		final Process process = builder.start();
		
		program = new Program(process, title);
		program.setCommand(this);
		
		if (listener != null)
		{
			program.addListener(listener);
		}
		
		program.setRunning(true);
		
		commandThread = new Thread()
		{
			public void run()
			{
				Display.getDefault().asyncExec(new Runnable()
				{
					public void run()
					{
						commandThread2 = new Thread()
						{
							public void run()
							{
								values.lsr = new LogStreamReader(display, process.getInputStream(), program, CONFIG_DATA.get("workspace.location") + "/");
								values.thread = new Thread(values.lsr, "LogStreamReader");
								values.thread.start();
								
								values.reader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
								
								try
								{
									String line = null;
									
									while ((line = values.reader.readLine()) != null)
									{
										result.value = 1;
										
										if (program != null)
										{
											program.append(line + "\n");
										}
										
//										if (!values.reader.ready())
//										{
//											break;
//										}
									}
									
									if (result.value == 0)
									{
										result.value = process.waitFor();
										
										StringBuilder builder = new StringBuilder();
										
										line = null;
										
										while ((line = values.reader.readLine()) != null)
										{
											builder.append(line + "\n");
										}
										
										if (builder.length() > 0)
										{
											while (builder.charAt(builder.length() - 1) == '\n' || builder.charAt(builder.length() - 1) == '\r')
											{
												builder.deleteCharAt(builder.length() - 1);
											}
											
											program.append(builder.toString());
										}
									
										values.thread.join();
										values.reader.close();
										values.lsr.stop();
										inputStream.close();
										errorStream.close();
										
										error.delete();
										input.delete();
									}
									
									process.waitFor();
									
									process.destroy();
									
									if (display.isDisposed())
									{
										return;
									}
									
									display.syncExec(new Runnable()
									{
										public void run()
										{
											program.setRunning(false);
											
											for (int i = listeners.size() - 1; i >= 0; i --)
											{
												listeners.get(i).resultReceived(result.value);
											}
											
											for (int i = listeners.size() - 1; i >= 0; i --)
											{
												listeners.get(i).commandExecuted();
											}
										}
									});
								}
								catch (InterruptedException e)
								{
									e.printStackTrace();
								}
								catch (IOException e)
								{
									e.printStackTrace();
								}
							}
						};
						
						commandThread2.start();
					}
				});
			}
		};
		
		commandThread.start();
	}
	
	public void terminate() throws InterruptedException
	{
		commandThread.join(1);
		commandThread2.join(1);
	}
	
	public void addCommandListener(CommandListener lisetener)
	{
		listeners.add(lisetener);
	}
	
	/**
	 * Get the Program instance that this Command is using.
	 * 
	 * @return The Program instance that this Command is using.
	 */
	public Program getProgram()
	{
		return program;
	}
	
//	/**
//	 * Get the Process instance that this Command is using.
//	 * 
//	 * @return The Process instance that is Command is using.
//	 */
//	public Process getProcess()
//	{
//		return program.getProcess();
//	}

	private class Result
	{
		int value;
	}
	
	private class ExecValues
	{
		LogStreamReader	lsr;
		BufferedReader	reader;
		Thread			thread;
	}
}

/**
 * 
 * 
 * @author	Braden Steffaniak
 * @since	Mar 24, 2013 at 1:35:58 AM
 * @since	v0.1
 * @version Mar 24, 2013 at 1:35:58 AM
 * @version	v0.1
 */
class LogStreamReader implements Runnable
{
	private boolean			running;

	private String			location, line;
	
	private Program			program;

	private BufferedReader	reader;
    
	private Display			display;
	
    public LogStreamReader(Display display, InputStream is, Program program, String location)
    {
    	this.location = location;
    	
        this.reader = new BufferedReader(new InputStreamReader(is));
        
        this.display = display;
        
        this.program = program;
        
        running = true;
    }

    public void run()
    {
        try
        {
            while ((line = reader.readLine()) != null)
            {
//            	line = line.replace(location, "");
            	
            	if (running)
            	{
            		display.syncExec(new Runnable()
    				{
    					public void run()
    					{
    						if (program != null)
							{
    							program.append(line + "\n");
							}
    					}
    				});
            	}
            	else
            	{
            		return;
            	}
            }
            
            line = null;
            
            reader.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    public void stop()
    {
    	running = false;
    }
}