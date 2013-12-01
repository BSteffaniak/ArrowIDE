package net.foxycorndog.arrowide.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import net.foxycorndog.arrowide.Program;

import org.eclipse.swt.widgets.Display;

import static java.nio.file.StandardWatchEventKinds.*;

public abstract class FileStreamReader implements Runnable
{
	private boolean			running;

	private String			line;
	
	private File			file;

	private BufferedReader	reader;
	
	private Path			directoryToWatch;
	
	private WatchService	watcherSvc;
	
	private WatchKey		watchKey;
	
    public FileStreamReader(File file) throws FileNotFoundException, IOException
    {
        running   = true;
        
        this.file = file;
        
        directoryToWatch = Paths.get(file.getParent());
        watcherSvc = FileSystems.getDefault().newWatchService();
        watchKey = directoryToWatch.register(watcherSvc, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        
        reader = new BufferedReader(new FileReader(file));
    }

    public void run()
    {
    	while (running)
    	{
            try
			{
				watchKey = watcherSvc.take();
			}
			catch (InterruptedException e)
			{
				e.printStackTrace();
				
				throw new RuntimeException();
			}
            
            for (WatchEvent<?> event : watchKey.pollEvents())
            {
                WatchEvent<Path> watchEvent = (WatchEvent<Path>)event;
                
                File f = directoryToWatch.resolve(watchEvent.context()).toFile();
                
                try
				{
					if (event.kind().name().toString().equals("ENTRY_MODIFY") && f.getCanonicalPath().equals(file.getCanonicalPath()))
					{
						String line = null;
						
						while ((line = reader.readLine()) != null)
						{
							dataReceived(line);
						}
						
						reader.close();
						
						reader = new BufferedReader(new FileReader(file));
					}
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
                
                watchKey.reset();
            }
        }
    }
    
    public void close() throws IOException
    {
    	if (reader != null)
    	{
    		reader.close();
    	}
    	
    	running = false;
    }
    
    public abstract void dataReceived(String data);
}