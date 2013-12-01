package net.foxycorndog.arrowide.event;

import java.io.PrintStream;

import net.foxycorndog.arrowide.language.CompileOutput;

/**
 * Class used to pass information of an event that was created for
 * a CompilerListener.
 * 
 * @author	Braden Steffaniak
 * @since	Apr 8, 2013 at 8:26:03 PM
 * @since	v0.2
 * @version	Apr 8, 2013 at 8:26:03 PM
 * @version	v0.2
 */
public class CompilerEvent extends Event
{
	private String			fileName;
	
	private PrintStream		stream;
	
	private String			files[];
	
	private CompileOutput	outputs[];
	
	/**
	 * Construct a CompilerEvent with the specified values.
	 * 
	 * @param files The files that were compiled.
	 * @param outputs The output files that were created.
	 * @param stream The stream to print the output to.
	 * @param fileName The name of the file that was compiled.
	 */
	public CompilerEvent(String files[], CompileOutput outputs[], PrintStream stream, String fileName)
	{
		this.files    = files;
		
		this.outputs  = outputs;
		
		this.stream   = stream;
		
		this.fileName = fileName;
	}
	
	/**
	 * Get the name of the file that was compiled.
	 * 
	 * @return The name of the file that was compiled.
	 */
	public String getFileName()
	{
		return fileName;
	}
	
	/**
	 * Get the stream to print the output to.
	 * 
	 * @return The stream to print the output to.
	 */
	public PrintStream getStream()
	{
		return stream;
	}
	
	/**
	 * Get the files that were compiled.
	 * 
	 * @return The files that were compiled.
	 */
	public String[] getFiles()
	{
		return files;
	}
	
	/**
	 * Get the output files that were created.
	 * 
	 * @return The output files that were created.
	 */
	public CompileOutput[] getOutputs()
	{
		return outputs;
	}
}