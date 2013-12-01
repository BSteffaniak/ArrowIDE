package net.foxycorndog.arrowide.components;

import java.awt.Color;
import java.awt.Container;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JWindow;
import javax.swing.Timer;

//package net.foxycorndog.arrowide.components;
//
//import java.awt.Color;
//import java.awt.Graphics2D;
//
//import net.foxycorndog.arrowide.file.FileUtils;
//
//import org.eclipse.swt.SWT;
//import org.eclipse.swt.events.PaintEvent;
//import org.eclipse.swt.events.PaintListener;
//import org.eclipse.swt.graphics.Font;
//import org.eclipse.swt.graphics.FontData;
//import org.eclipse.swt.graphics.Image;
//import org.eclipse.swt.graphics.ImageData;
//import org.eclipse.swt.graphics.Point;
//import org.eclipse.swt.graphics.Rectangle;
//import org.eclipse.swt.graphics.Region;
//import org.eclipse.swt.widgets.Canvas;
//import org.eclipse.swt.widgets.Display;
//import org.eclipse.swt.widgets.Label;
//import org.eclipse.swt.widgets.Monitor;
//import org.eclipse.swt.widgets.Shell;
//
//public class SplashScreen
//{
//	private long    millis;
//	private long    startTime;
//	
//	private Image   image;
//	
//	private Shell   shell;
//	
//	public SplashScreen(Image img)
//	{
//		image = img;
//		
//		int width  = image.getBounds().width;
//		int height = image.getBounds().height;
//		
//		Display display = Display.getDefault();
//		
//		Monitor monitor = display.getPrimaryMonitor();
//		final Rectangle screenBounds = monitor.getBounds();
//		
////		// Shell must be created with style SWT.NO_TRIM
////		shell = new Shell(display, SWT.NO_TRIM | SWT.ON_TOP);
////		shell.setBackground(display.getSystemColor(SWT.COLOR_BLUE));
////		shell.setAlpha(120);
////		// define a region
////		Region region = new Region();
////		Rectangle pixel = new Rectangle(0, 0, 1, 1);
////		
////		ImageData imgData = image.getImageData();
//////		int pixels[] = new int[width];
////		
////		for (int y = 0; y < height; y++)
////		{
////			
//////			imgData.getPixels(0, y, width, pixels, 0);
////			
////			for (int x = 0; x < width; x++)
////			{
////				
////				if (imgData.getAlpha(x, y) != 0)//pixels[x] != 0)
////				{
////					pixel.x = x;
////					pixel.y = y;
////					
////					region.add(pixel);
////				}
////			}
////		}
////		
////		// define the shape of the shell using setRegion
////		shell.setRegion(region);
////		Rectangle size = region.getBounds();
////		shell.setSize(size.width, size.height);
////		shell.setLocation(screenBounds.width / 2 - shell.getSize().x / 2, screenBounds.height / 2 - shell.getSize().y / 2);
////		shell.addPaintListener(new PaintListener()
////		{
////			public void paintControl(PaintEvent e)
////			{
////				Rectangle bounds = image.getBounds();
////				Point size = shell.getSize();
////				e.gc.drawImage(image, 0, 0, bounds.width, bounds.height, 0, 0, size.x, size.y);
////			}
////		});
//		
////		shell = new Shell(display, SWT.ON_TOP | SWT.NO_TRIM | SWT.NO_BACKGROUND);
////		
////		shell.setSize(image.getBounds().width, image.getBounds().height);
////		shell.setLocation(screenBounds.width / 2 - shell.getSize().x / 2, screenBounds.height / 2 - shell.getSize().y / 2);
////		
////		Label splashImage = new Label(shell, SWT.NONE);
////		splashImage.setSize(shell.getSize());
////		splashImage.setImage(image);
//		
////		shell = new Shell(display, SWT.NO_TRIM);
////        shell.setForeground(display.getSystemColor(SWT.COLOR_WHITE));
////
////        //set the transparent canvas on the shell
////        Canvas canvas = new Canvas(shell, SWT.NO_BACKGROUND);
////
////        //create an area to paint the text
////        Rectangle size = new Rectangle(0, 0, 200, 200);
////        canvas.setBounds(size);
////
////        Region region = canvas.getRegion();
////
////        //mucking about with fonts
////	    Font font = FileUtils.loadMonospacedFont(Display.getDefault(), "Liberation Mono", "res/fonts/Liberation-Mono/LiberationMono-Regular.ttf", 16, SWT.NORMAL);
////
////        FontData[] fd = font.getFontData();
////
//////        fd[0].setHeight(24);
//////        fd[0].setStyle(SWT.BOLD);
////
//////        Font bigFont = new Font(display, fd[0]);
//////        canvas.setFont(bigFont);
////        canvas.setFont(display.getSystemFont());
////
////        // define the shape of the shell using setRegion
////        shell.setRegion(region);
////        shell.setSize(size.width, size.height);
////
////        canvas.addPaintListener(new PaintListener() {
////            public void paintControl(PaintEvent e) {
////                e.gc.drawString("Hello", 10, 10, true);
////            }
////        });
//		
//		
//	}
//	
//	public void open(long millis)
//	{
////		shell.open();
//		
//		this.millis    = millis;
//		this.startTime = System.currentTimeMillis();
//	}
//	
//	public void dispose()
//	{
////		shell.dispose();
////		image.dispose();
//	}
//	
//	public void update()
//	{
////		if (!shell.isDisposed() && shell.isVisible())
////		{
////			if (System.currentTimeMillis() - startTime >= millis)
////			{
////				dispose();
////			}
////		}
//	}
//}

//public class SplashScreen extends Frame implements ActionListener
//{
//    static void renderSplashFrame(Graphics2D g, int frame)
//    {
//        final String[] comps = {"foo", "bar", "baz"};
//        g.setComposite(AlphaComposite.Clear);
//        g.fillRect(120,140,200,40);
//        g.setPaintMode();
//        g.setColor(Color.BLACK);
//        g.drawString("Loading "+comps[(frame/5)%3]+"...", 120, 150);
//    }
//    
//    public SplashScreen()
//    {
//        super("SplashScreen demo");
//        setSize(300, 200);
//        setLayout(new BorderLayout());
//        Menu m1 = new Menu("File");
//        MenuItem mi1 = new MenuItem("Exit");
//        m1.add(mi1);
//        mi1.addActionListener(this);
//        this.addWindowListener(closeWindow);
// 
//        MenuBar mb = new MenuBar();
//        setMenuBar(mb);
//        mb.add(m1);
//        final java.awt.SplashScreen splash = java.awt.SplashScreen.getSplashScreen();
//        if (splash == null) {
//            System.out.println("SplashScreen.getSplashScreen() returned null");
//            return;
//        }
//        Graphics2D g = splash.createGraphics();
//        if (g == null) {
//            System.out.println("g is null");
//            return;
//        }
//        for(int i=0; i<100; i++)
//        {
//            renderSplashFrame(g, i);
//            splash.update();
//            
//            try
//            {
//                Thread.sleep(90);
//            }
//            catch(InterruptedException e)
//            {
//            	e.printStackTrace();
//            }
//        }
//        splash.close();
//        setVisible(true);
//        toFront();
//    }
//    public void actionPerformed(ActionEvent ae)
//    {
//        System.exit(0);
//    }
//     
//    private static WindowListener closeWindow = new WindowAdapter()
//    {
//        public void windowClosing(WindowEvent e)
//        {
//            e.getWindow().dispose();
//        }
//    };
//}

public class SplashScreen extends JWindow {

    static boolean isRegistered;
//    private static JProgressBar progressBar = new JProgressBar();
    private static SplashScreen execute;
    private static int count;
    private static Timer timer1;

    public SplashScreen(String location, final long millis)
    {
    	ImageIcon image = new ImageIcon(location);
    	
    	int width  = image.getIconWidth();
    	int height = image.getIconHeight();
    	
        Container container = getContentPane();
        container.setLayout(null);

        JPanel panel = new JPanel();
        panel.setBorder(BorderFactory.createEmptyBorder());//new javax.swing.border.EtchedBorder());
        panel.setBackground(new Color(255, 255, 255));
        panel.setSize(width, height);
        panel.setLayout(null);
        container.add(panel);

        JLabel label = new JLabel(image);
        label.setSize(width, height);
        panel.add(label);

//        progressBar.setMaximum(50);
//        progressBar.setBounds(55, 180, 250, 15);
//        container.add(progressBar);
//        loadProgressBar();
        setSize(width, height);
        setLocationRelativeTo(null);
        setAlwaysOnTop(true);
        setBackground(new Color(0,0,0,0));
//        setOpacity(0.8f);
        setVisible(true);
        
        new Thread()
        {
        	public void run()
        	{
        		try
        		{
        			Thread.sleep(millis);
        			
        			setVisible(false);
        		}
        		catch (InterruptedException e)
        		{
        			e.printStackTrace();
        		}
        	}
        }.start();
    }
}

//class JReflectionFrame extends JFrame
//{
//	private JPanel  reflectionPanel;
//	
//	private JWindow reflection;
//	
//	private JReflectionFrame thisFrame;
//	
//	public JReflectionFrame(String title)
//	{
//	    super(title);
//	    
//	    thisFrame = this;
//	    
//	    reflection = new JWindow();
//	    reflectionPanel = new JPanel() {
//	       @Override
//	       protected void paintComponent(Graphics g)
//	       {
//	          // paint the reflection of the main window
//	          paintReflection(g);
//	       }
//	    };
//	    // mark the panel as non-double buffered and non-opaque
//	    // to make it translucent.
//	    reflectionPanel.setDoubleBuffered(false);
//	    reflectionPanel.setOpaque(false);
//	
//	    reflection.setLayout(new BorderLayout());
//	    reflection.add(reflectionPanel, BorderLayout.CENTER);
//
//	    // install custom repaint manager to force re-painting
//	    // the reflection when something in the main window is
//	    // repainted
//	    RepaintManager.setCurrentManager(new ReflectionRepaintManager());
//	    
//	    this.addComponentListener(new ComponentAdapter() {
//	         @Override
//	         public void componentHidden(ComponentEvent e) {
//	            reflection.setVisible(false);
//	         }
//
//	         @Override
//	         public void componentMoved(ComponentEvent e) {
//	            // update the reflection location
//	            reflection.setLocation(getX(), getY() + getHeight());
//	         }
//
//	         @Override
//	         public void componentResized(ComponentEvent e) {
//	            // update the reflection size and location
//	            reflection.setSize(thisFrame.getWidth(), getHeight());
//	            reflection.setLocation(getX(), getY() + getHeight());
//	         }
//
//	         @Override
//	         public void componentShown(ComponentEvent e) {
//	            reflection.setVisible(true);
//
//	            // if the reflection window is opaque, mark
//	            // it as per-pixel translucent
//	            if (com.sun.awt.AWTUtilities.isWindowOpaque(reflection)) {
//	               com.sun.awt.AWTUtilities.setWindowOpaque(reflection, false);
//	            }
//	         }
//	      });
//
//	      this.addWindowListener(new WindowAdapter() {
//	         @Override
//	         public void windowActivated(WindowEvent e) {
//	            // force showing the reflection window
//	            reflection.setAlwaysOnTop(true);
//	            reflection.setAlwaysOnTop(false);
//	         }
//	      });
//	
//	    // initialize the reflection size and location
//	    reflection.setSize(getSize());
//	    reflection.setLocation(getX(), getY() + getHeight());
//	    reflection.setVisible(true);
//	
//	   
//	 }
//}
//
//class ReflectionRepaintManager extends RepaintManager
//{
//    public void addDirtyRegion(JComponent c, int x, int y, int w, int h)
//    {
//       Window win = SwingUtilities.getWindowAncestor(c);
//       
//       if (win instanceof JReflectionFrame) {
//          // mark the entire root pane to be repainted
//          JRootPane rp = ((JReflectionFrame) win).getRootPane();
//          super.addDirtyRegion(rp, 0, 0, rp.getWidth(), rp.getHeight());
//
//          // workaround bug 6670649 - should call reflection.repaint()
//          // but that will not repaint the panel
//          reflectionPanel.repaint();
//       }
//       else
//       {
//          super.addDirtyRegion(c, x, y, w, h);
//       }
//    }
// }