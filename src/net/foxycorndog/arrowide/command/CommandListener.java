package net.foxycorndog.arrowide.command;

/**
 * Class that is used to listen to Commands.
 * 
 * @author	Braden Steffaniak
 * @since	Mar 20, 2013 at 7:14:06 PM
 * @since	v0.1
 * @version Mar 20, 2013 at 7:14:06 PM
 * @version	v0.1
 */
public interface CommandListener
{
	public void resultReceived(int result);
	
	public void commandExecuted();
}