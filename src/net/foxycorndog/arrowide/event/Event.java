package net.foxycorndog.arrowide.event;

/**
 * Class used for events that are passed to Listeners.
 * 
 * @author	Braden Steffaniak
 * @since	Apr 3, 2013 at 10:04:55 PM
 * @since	v0.2
 * @version	Apr 3, 2013 at 10:04:55 PM
 * @version	v0.2
 */
public class Event
{
	private long when;
	
	/**
	 * Construct an Event that stores the time in which the Event was
	 * created.
	 */
	public Event()
	{
		this.when = System.currentTimeMillis();
	}
	
	/**
	 * Get the time in milliseconds that the Event was created.
	 * 
	 * @return The time in milliseconds that the Event was created.
	 */
	public long getWhen()
	{
		return when;
	}
}