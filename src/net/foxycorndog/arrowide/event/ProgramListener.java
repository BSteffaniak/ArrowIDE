package net.foxycorndog.arrowide.event;

import net.foxycorndog.arrowide.Program;

/**
 * Interface used to listen for when error messages and regular messages
 * are sent from a Program. Also listens for when the Program is
 * terminated.
 * 
 * @author	Braden Steffaniak
 * @since	Apr 3, 2013 at 10:11:18 PM
 * @since	v0.2
 * @version	Apr 3, 2013 at 10:11:18 PM
 * @version	v0.2
 */
public interface ProgramListener
{
	/**
	 * Called when an error message is received from the Program's
	 * output.
	 * 
	 * @param message The error message from the Program's output.
	 */
	public void errorMessageReceived(String message);
	
	/**
	 * Called when a message is received from the Program's output.
	 * 
	 * @param message The message received from the Program's output.
	 */
	public void messageReceived(String message);
	
	/**
	 * Called whenever the Program has been started.
	 * 
	 * @param program The Program that was started.
	 */
	public void programStarted(Program program);
	
	/**
	 * Called whenever the Program has been terminated.
	 * 
	 * @param program The Program that was terminated.
	 */
	public void programTerminated(Program program);
}