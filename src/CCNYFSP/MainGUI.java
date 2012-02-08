package CCNYFSP;

import gnu.io.*;
import gnu.io.PortInUseException;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.awt.event.*;


public class MainGUI extends JFrame implements Observer
{
	private JFrame about=new JFrame("About");
	private JTextArea serial_console = new JTextArea("Please search for a serial port...\n");
	private MouseRobot fsp_mouse;
	private JToggleButton port_control= new JToggleButton("Open Port");
	private JToggleButton mouse_trigger= new JToggleButton("Mouse Trigger");
	private JButton search_for_ports=new JButton("Search For Ports");
	private JButton clear_terminal=new JButton("Clear Terminal");
	private JMenuBar menubar=new JMenuBar();
	private boolean is_about_open=false;
	private String portList[]=new String[5];
	private JComboBox portbox =new JComboBox();
	private int baudrate=38400;
	private boolean data_logging=false;
	
	private SerialCommAPI myserial=new SerialCommAPI();
	private SensorPlot plots=new SensorPlot();
	private RGBcube cube=new RGBcube();
	
	
	public class mymenu extends JMenu
	{
		public mymenu(String label, char mnem)
		{
			super(label);
			this.setBackground(Color.BLACK);
			this.setForeground(new Color(3329330));
		}
	}
	
	
	
	public class TextAreaPrintStream extends PrintStream {
		private JTextArea ta;
		TextAreaPrintStream(JTextArea ta) {
			super(System.out);
		this.ta = ta;
		}
		//... and now implement every single method in PrintStream.
		public void print(String s) {
		ta.append(s);
		}
		public void println(String s) {
		ta.append(s);
		ta.append("\n");
		}
		public void print(int i) {
		ta.append(Integer.toString(i));
		}
		// ... There are lots of methods to implement, but this is the basic gist of it:
		}
	
	public Observable getCurrentObservable()
	{
		return (Observable)myserial;
	}

	public void project_about()
	{
		
		about.setVisible(true);
		about.setSize(505, 300);
		about.getContentPane().setBackground(Color.BLACK);
		about.setLocationRelativeTo((Component)this);
		JLabel img_about=new JLabel();
		img_about.setIcon(new ImageIcon(getClass().getResource("1.jpg")));
		about.add("North",img_about);
		JTextArea detail=new JTextArea();
		detail.setText("EE Senior Design Fall 2010 --- 2nd Semester\n");
		detail.append("Team Members:\nMilton Herrera\nSilan Shrestha\nYung Lam\n");
		detail.setEditable(false);
		detail.setBackground(Color.BLACK);
		detail.setForeground(new Color(3329330));
		about.add("Center",detail);
		about.pack();
		is_about_open=true;
		about.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
	    about.addWindowListener(new WindowAdapter() {
	      public void windowOpened(WindowEvent e) {
	      }

	      public void windowClosing(WindowEvent e) {
	        is_about_open=false;
	        about.setVisible(false);
	        about.dispose();
	       }
	    });
	}
	
	public void list_port()
	{
		portbox.removeAllItems();
        portList[0]="Select Port";
        portbox.addItem("Select Port");
        
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();
        portEnum = CommPortIdentifier.getPortIdentifiers();
        int numports = 0;
        while ( portEnum.hasMoreElements() ) 
        {
        	CommPortIdentifier portIdentifier = (CommPortIdentifier) portEnum.nextElement();
            numports++;
            portList[numports]=portIdentifier.getName();
            System.out.println(portIdentifier.getName());
            portbox.addItem(portIdentifier.getName());
        }
        //make the actual port list only as long as necessary
        serial_console.setText("Search is done!\n");	
	}
	
	public void print(String msg)
	{
		serial_console.append(msg);
	}
	
	
	
	class MyScrollBarUI extends BasicScrollBarUI {
		// this draws scroller
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
		      g.setColor(new Color(3329330));
		      //c.setBorder(BorderFactory.createLineBorder(new Color(3329330), 1));
		      g.fillRect((int)thumbBounds.getX(),(int)thumbBounds.getY(),
		                 (int)thumbBounds.getWidth(),(int)thumbBounds.getHeight());
		    }   
		    
		// this draws scroller background
		    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
		      g.setColor(Color.BLACK);
		      g.fillRect((int)trackBounds.getX(),(int)trackBounds.getY(),
		                 (int)trackBounds.getWidth(),(int)trackBounds.getHeight());
		    }
		    
		// and methods creating scrollbar buttons
		    protected JButton createDecreaseButton(int orientation) {
		      JButton button = super.createDecreaseButton(orientation);
		      button.setBackground(Color.BLACK);
		      button.setBorder(BorderFactory.createLineBorder(new Color(3329330), 1));
		      return button;
		    }
		    
		    protected JButton createIncreaseButton(int orientation) {
		      JButton button = super.createIncreaseButton(orientation);
		      button.setBackground(Color.BLACK);
		      //button.getComponent(0).setBackground(new Color(3329330));
		      button.setBorder(BorderFactory.createLineBorder(new Color(3329330), 1));
		      return button;
		    }
		}
	
	
	
	
	public MainGUI() 
	{
		myserial.addObserver(this);
		setTitle("CCNY 6DOF Free Space Pen Application Interface");
		//getRootPane().setWindowDecorationStyle(JRootPane.INFORMATION_DIALOG);
		setLocation(250, 100);
		
		Cursor cursor = Toolkit.getDefaultToolkit().createCustomCursor(Toolkit.getDefaultToolkit().getImage(getClass().getResource("pencil.gif")), new Point(0,0), "Pencil");
		this.getRootPane().setCursor(cursor);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("pencil.gif")));
		setSize(607, 400);
	    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    setLayout(null); 
	    setResizable(false);
	    //setUndecorated(true);
	    getContentPane().setBackground(Color.BLACK);
	    //getContentPane().setLayout(new FlowLayout());
	    menubar.setBackground(Color.BLACK);
	    //menubar.setForeground(new Color(3329330));
	    menubar.setBorder(null);
	    JMenu menu = new JMenu("Demos");
	    menu.setMnemonic('d');
	    menu.setForeground(new Color(3329330));
	    menubar.add(menu);
	    //menu.setForeground(new Color(3329330));
	    
	    JMenu demo_options=new JMenu("Types");
	    demo_options.setMnemonic('t');
	    JCheckBoxMenuItem logging=new JCheckBoxMenuItem("Data Logging", false);
	    logging.setMnemonic('D');
	    logging.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed (ActionEvent e)
	    	{
	    		data_logging=!data_logging;
	    	}
	    });
	    demo_options.add(logging);
	    
	    JMenuItem types=new JMenuItem("Cube", 'C');
	    types.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed (ActionEvent e)
	    	{
	    		if (!cube.isCubeOpen())
	    		{
	    			
	    			cube=new RGBcube();
	    			myserial.addObserver(cube);
	    			cube.setBiases(myserial.getBiases());
	    			cube.setOpen(true);
	    			cube.setVisible(true);
	    			plots.setResizable(true);
	    		}
	    		else
	    			cube.requestFocus();
	    	}
	    });
	    demo_options.add(types);
	    types=new JMenuItem("Real Time Plot", 'R');
	    types.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed (ActionEvent e)
	    	{
	    		if (!plots.isPlotOpen())
	    		{
	    			plots=new SensorPlot();
	    			plots.setOpen(true);
	    			//plots.setObservable(myserial);
	    			myserial.addObserver(plots);
	    			plots.setVisible(true);
	    			plots.setResizable(true);
	    		}
	    		else
	    			plots.requestFocus();
	    	}
	    });
	    demo_options.add(types);
	    
	    types=new JMenuItem("M$ Paint", 'P');
	    types.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed (ActionEvent e)
	    	{
	    		try {
					Runtime.getRuntime().exec("mspaint");
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	    	}
	    });
	    demo_options.add(types);
	    menu.add(demo_options);
	    //demo_options.setForeground(new Color(3329330));
	    
	    JMenu setting=new JMenu("Setting");
	    setting.setMnemonic('t');
	    setting.setForeground(new Color(3329330));
	    menubar.add(setting);
	    
	    menu = new JMenu("Serial");
	    menu.setMnemonic('s');
	    setting.add(menu);
	    	    
	    JMenu baud_menu=new JMenu("Baud Rate");
	    baud_menu.setMnemonic('b');
	    ButtonGroup choose = new ButtonGroup();
	    JCheckBoxMenuItem rates=new JCheckBoxMenuItem("9600", false);
	    baud_menu.add(rates);
	    choose.add(rates);
	    rates.addActionListener(new BaudRateListener());
	    rates=new JCheckBoxMenuItem("19200", false);
	    baud_menu.add(rates);
	    choose.add(rates);
	    rates.addActionListener(new BaudRateListener());
	    rates=new JCheckBoxMenuItem("28800", false);
	    baud_menu.add(rates);
	    choose.add(rates);
	    rates.addActionListener(new BaudRateListener());
	    rates=new JCheckBoxMenuItem("38400", true);
	    baud_menu.add(rates);
	    choose.add(rates);
	    rates.addActionListener(new BaudRateListener());
	    rates=new JCheckBoxMenuItem("57600", false);
	    baud_menu.add(rates);
	    choose.add(rates);
	    rates.addActionListener(new BaudRateListener());
	    rates=new JCheckBoxMenuItem("115200", false);
	    baud_menu.add(rates);
	    choose.add(rates);
	    rates.addActionListener(new BaudRateListener());
	    rates=new JCheckBoxMenuItem("230400", false);
	    baud_menu.add(rates);
	    choose.add(rates);
	    rates.addActionListener(new BaudRateListener());
	    rates=new JCheckBoxMenuItem("460800", false);
	    baud_menu.add(rates);
	    choose.add(rates);
	    rates.addActionListener(new BaudRateListener());
	    rates=new JCheckBoxMenuItem("921600", false);
	    baud_menu.add(rates);
	    choose.add(rates);
	    rates.addActionListener(new BaudRateListener());
	    menu.add(baud_menu);
	    
	    menu=new JMenu("Mouse Pointer");
	    menu.setMnemonic('m');
	    setting.add(menu);
	    
	    ButtonGroup mouse_dpi = new ButtonGroup();
	    JCheckBoxMenuItem dpi=new JCheckBoxMenuItem("Low Sensitivity, High Precision", false);
	    menu.add(dpi);
	    mouse_dpi.add(dpi);
	    dpi.addActionListener(new DPIListener(120/2));
	    dpi=new JCheckBoxMenuItem("Normal Sensitivity, Normal Precision", true);
	    menu.add(dpi);
	    mouse_dpi.add(dpi);
	    dpi.addActionListener(new DPIListener(120));
	    dpi=new JCheckBoxMenuItem("High Sensitivity, Low Precision", false);
	    menu.add(dpi);
	    mouse_dpi.add(dpi);
	    dpi.addActionListener(new DPIListener(120*2));
	    
	    
	    /*EditListener l = new EditListener();
	    JMenuItem mi;
	    
	    mi = menu.add(new JMenuItem("Cut", 't'));
	    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, Event.CTRL_MASK));
	    mi.addActionListener(l);
	    mi = menu.add(new JMenuItem("Copy", 'c'));
	    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK));
	    mi.addActionListener(l);
	    mi = menu.add(new JMenuItem("Paste", 'p'));
	    mi.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK));
	    mi.addActionListener(l);*/
	    	    
	    menu = new JMenu("Help");
	    menu.setMnemonic('h');
	    menu.setForeground(new Color(3329330));
	    menubar.add(menu);
	    JMenuItem mi=menu.add(new JMenuItem("About", 'a'));
	    mi.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed (ActionEvent e)
	    	{
	    		if (!is_about_open)
	    			project_about();
	    		else
	    			about.requestFocus();
	    	}
	    });
	    
	    mi =menu.add(new JMenuItem("Exit",'x'));
	    mi.addActionListener(new ActionListener() {
	      public void actionPerformed(ActionEvent e) {
	        //System.out.println("Exit performed");
	    	  MainGUI.this.setVisible(false);
	    	  MainGUI.this.dispose();
	    	  System.exit(0);
	      }
	    });
	   
	    setJMenuBar(menubar);
	    
	    JLabel imglabel = new JLabel();
	    imglabel.setIcon(new ImageIcon(getClass().getResource("2.jpg")));
	    
	    imglabel.setBackground(Color.BLACK);
	    add(imglabel);
	    imglabel.setBounds(5, 2, 600, 111);
	   
	    
	    
	    add(search_for_ports);
	    search_for_ports.setBounds(2, 120, 130, 20);
	    search_for_ports.setBackground(Color.black);
	    search_for_ports.setForeground(new Color(3329330));
	    search_for_ports.setRequestFocusEnabled(false);
	    search_for_ports.setFocusable(false);
	    search_for_ports.addMouseListener(new MouseListener()
	    {
	    	@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
	    	}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if (search_for_ports.isEnabled())
				serial_console.setText("Searching for available ports...\n");
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
			}
	    });
	    search_for_ports.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed (ActionEvent e)
	    	{
	    		list_port();
	    	}
	    });
        
	    //JPanel panel=new JPanel();
	    //panel.add(portbox);
	    add(portbox);
	    portbox.setBounds(134, 120, 100, 20);
	    portbox.addItem("Empty");
	    
	    add(port_control);
	    port_control.setBounds(236, 120, 100, 20);
	    port_control.setBackground(Color.black);
	    port_control.setForeground(new Color(3329330));
	    port_control.setRequestFocusEnabled(false);
	    port_control.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed (ActionEvent e)
	    	{
	    		if (port_control.getText().equals("Close Port"))
	    		{
	    			myserial.close();
	    			serial_console.append("Port is closed\n");
	    			portbox.setEnabled(true);
	    			search_for_ports.setEnabled(true);
	    			port_control.setText("Open Port");
	    			myserial.deleteObserver(fsp_mouse);
	    			myserial.deleteObserver(plots);
	    			myserial.deleteObserver(cube);
	    			mouse_trigger.setText("Mouse Trigger");
	    			mouse_trigger.setSelected(false);
	    		}
	    		else
	    		{
	    		if (portbox.getSelectedItem().equals("Select Port") || portbox.getSelectedItem().equals("Empty") )
	    			{
	    				serial_console.append("Please select a valid port\n");
	    				port_control.setSelected(false);
	    			}
				else
					try {
						if (CommPortIdentifier.getPortIdentifier((String) portbox.getSelectedItem()).isCurrentlyOwned())
							serial_console.append("Port is currently opened\n");
						else
						{
							port_control.setText("Close Port");
							portbox.setEnabled(false);
							search_for_ports.setEnabled(false);
							serial_console.append("Opening "+portbox.getSelectedItem()+"\n");
							try{
							myserial.connect((String)portbox.getSelectedItem());
							}
							catch ( Exception e1 ) {
						        e1.printStackTrace();
						        serial_console.append("The port is probably in use currently by other application!!!\nPlease check ownership!!!\n");
						        port_control.setText("Open Port");
						        port_control.setSelected(false);
						        portbox.setEnabled(true);
						        search_for_ports.setEnabled(true);
							}
						}
					} catch (NoSuchPortException e1) {
						e1.printStackTrace();
					}
	    		}
	    	}
	    });
	    
	    add(mouse_trigger);
	    mouse_trigger.setBounds(470, 120, 130, 20);
	    mouse_trigger.setBackground(Color.BLACK);
	    mouse_trigger.setForeground(new Color(3329339));
	    mouse_trigger.setRequestFocusEnabled(false);
	    mouse_trigger.addActionListener(new ActionListener()
	    {
	    	public void actionPerformed (ActionEvent e)
	    	{
	    		if (mouse_trigger.getText().equals("Disable Mouse"))
	    		{
	    			mouse_trigger.setText("Mouse Trigger");
	    			myserial.deleteObserver(fsp_mouse);
	    			
	    		}
	    		else if (port_control.isSelected() && port_control.getText().equals("Close Port"))
	    		{
	    			try {
	    				myserial.calibration();
	    				while(myserial.done_calibration()){;}
						fsp_mouse=new  MouseRobot(myserial.getBiases());
						myserial.addObserver(fsp_mouse);
						mouse_trigger.setText("Disable Mouse");
					} catch (AWTException e1) {
						e1.printStackTrace();
					}
	    		}
	    		else
	    		{
	    			mouse_trigger.setSelected(false);
	    			serial_console.setText("************************** Warning ************************\n");
	    			serial_console.append("*  In order to trigger FSP Mouse Function, the device is  *\n");
	    			serial_console.append("*  needed to be connected with its associated serial port *\n");
	    			serial_console.append("***********************************************************\n");
	    		}
	    	}
	    });
	    
	    add(clear_terminal);
	    clear_terminal.setBounds(338, 120, 130, 20);
	    clear_terminal.setBackground(Color.black);
	    clear_terminal.setForeground(new Color(3329330));
	    clear_terminal.setRequestFocusEnabled(false);
	    clear_terminal.addActionListener(new ActionListener(){
	    	public void actionPerformed (ActionEvent e)
	    	{
	    		serial_console.setText(null);
	    	}
	    });
	    
	    JScrollPane consolescroll=new JScrollPane(serial_console);
	    consolescroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
	    consolescroll.getViewport().setBackground(Color.BLACK);
	    consolescroll.getVerticalScrollBar().setBackground(Color.BLACK);
	    consolescroll.setBackground(Color.black);
	    add(consolescroll);
	    //serial_console.setRows(1000);
	    //serial_console.setCaretColor(new Color(3329330));
	    serial_console.setBackground(Color.BLACK);
	    serial_console.setForeground(new Color(3329330));
	    serial_console.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
	    serial_console.setLineWrap(true);
	    serial_console.setEditable(false);
	    serial_console.setAutoscrolls(true);
	    serial_console.setCaretPosition(serial_console.getDocument().getLength());
	    ((DefaultCaret) serial_console.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
	    serial_console.append("Baud Rate is preconfigured to be 38400 Kbps\n");
	    serial_console.append("Data bits: 8\tParity: None\tStop bit: 1\n");
	    //consolescroll.getVerticalScrollBar().setBorder(BorderFactory.createLineBorder(Color.red));
	    consolescroll.getVerticalScrollBar().setUI(new MyScrollBarUI());
	    //consolescroll.getVerticalScrollBar().getComponent(0).setBackground(Color.BLACK);
	    //consolescroll.getVerticalScrollBar().getComponent(1).setBackground(Color.BLACK);
	    consolescroll.setVisible(true);
		consolescroll.setBounds(2,140, 598, 210);
		consolescroll.setBorder(null);
	   // pack();
		
	    //setSize(607, 400);
	    setVisible(true);
	  }

	  private class EditListener implements ActionListener {
	    public void actionPerformed(ActionEvent e) {
	      System.out.println(e.getActionCommand());
	      if (e.getActionCommand().equals(new String("About")))
	    	  System.out.println("open about page/design description");
	    }
	  }
	  
	  private class BaudRateListener implements ActionListener {
		    public void actionPerformed(ActionEvent e) {
		      serial_console.append("Baud Rate is changed to "+e.getActionCommand()+" Ksps\n");
		      myserial.setBaudRate(Integer.parseInt(e.getActionCommand()));
		      }
		  }
	  
	  private class DPIListener implements ActionListener {
		  private int dpi;
		  public DPIListener(int dpi){this.dpi=dpi;}
		    public void actionPerformed(ActionEvent e) {
		      serial_console.append("Mouse pointer setting is changed to "+e.getActionCommand()+"\n");
		      fsp_mouse.setDPI(dpi);
		      }
		  }

	  public static void main(String arg[])  throws Exception {
	    new MainGUI();
	  }

	@Override
	public void update(Observable t, Object o) {
		int temp[]=(int[]) o;
		
//		for (; i<temp.length-1;i++)
//		{
//			serial_console.append(temp[i]+"\t");
//		}
//		serial_console.append(temp[i]+"\n");
		if (data_logging)
			serial_console.append(Arrays.toString(temp).replaceAll("[\\[\\]]", "").replaceAll("[,]", "\t")+"\n");
		else
			serial_console.setText("The port is opened, now please select a DEMO from the menu.\n");
		//plots.update(temp);
	}
}

