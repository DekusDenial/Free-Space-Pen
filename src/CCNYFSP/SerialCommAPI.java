package CCNYFSP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import gnu.io.CommPortIdentifier; 
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent; 
import gnu.io.SerialPortEventListener; 
import gnu.io.UnsupportedCommOperationException;

import java.util.Calendar;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.StringTokenizer;
import java.util.Timer;
import java.awt.Robot;
import java.awt.Toolkit;
//import java.awt.event.InputEvent;
//import java.awt.event.KeyEvent;
import java.awt.MouseInfo;
import java.awt.event.InputEvent;


public class SerialCommAPI extends Observable implements SerialPortEventListener {
	
	private SerialPort serialPort;
    private static String PORT_NAMES = null;
	private InputStream input;
	private OutputStream output;
	private int mouse_param[] =new int [3];
	private int x_bias=0, y_bias=0, z_bias=0, gy_bias=386, gx_bias=376;
	private boolean need_calibrate=true;
	private int calibration_count=0;
	private boolean first_start=true;
	private static final int TIME_OUT = 2000;
	private static int DATA_RATE = 38400;
	private BufferedReader serial_raw;
	private Calendar time;
	private long start;

	public void setBaudRate(int baudrate)
	{
		DATA_RATE=baudrate;
		//System.out.println(baudrate);
	}
	
	public void connect(String portName) {
		try {
			CommPortIdentifier portId= CommPortIdentifier.getPortIdentifier(portName);
			// open serial port, and use class name for the appName.
			if ( portId.isCurrentlyOwned())
	            System.out.println("Error: Port is currently in use");
	        else{
				serialPort = (SerialPort) portId.open(this.getClass().getName(),
						TIME_OUT);
	
				// set port parameters
				serialPort.setSerialPortParams(DATA_RATE,
						SerialPort.DATABITS_8,
						SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);
				//serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_NONE);
				// open the streams
				input = serialPort.getInputStream();
				output = serialPort.getOutputStream();
	
				// add event listeners
				serialPort.addEventListener(this);
				serialPort.notifyOnDataAvailable(true);
		        }
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	///////////////////////////////////////////////////	
		
		try
	    {
	    	serialPort.enableReceiveTimeout(10000);
	    	serialPort.enableReceiveThreshold(0);
	    }
	    catch (UnsupportedCommOperationException e)
	    {
	    	// TODO Auto-generated catch block
	    	e.printStackTrace();
	    }
	    
	  ////////////////////////////////////////////////////////  
	    
	}

	public void calibration()
	{
		need_calibrate=true;
		first_start=true;
		calibration_count=0;
		x_bias=0;
		y_bias=0;
		z_bias=0;
		gy_bias=0;
		gx_bias=0;
		
	}
	
	public boolean done_calibration()
	{return need_calibrate;} //done when false
	
	public int[] getBiases()
	{
		int biases[]={x_bias, y_bias, z_bias, gy_bias, gx_bias};
		return biases;
	}
	
	private boolean checkIsNumber(String toCheck) {
		try {
			Integer.parseInt(toCheck);
			return true;
		} catch (NumberFormatException numForEx) {
			return false;
		} 
	}
	
	public void close() {
		if (input !=null)
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		if (output !=null)
			try {
				output.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
//		if (serial_raw !=null)
//			try {
//				serial_raw.close();
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	/**
	 * Handle an event on the serial port. Read the data and print it.
	 */
	public void serialEvent(SerialPortEvent oEvent) {
		
		
		
		switch(oEvent.getEventType()) {

		case SerialPortEvent.BI:
		case SerialPortEvent.CD:
		case SerialPortEvent.CTS:
		case SerialPortEvent.DSR:
		case SerialPortEvent.FE:
		case SerialPortEvent.OE:
		case SerialPortEvent.PE:
		case SerialPortEvent.RI:
		case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
		break;
		case SerialPortEvent.DATA_AVAILABLE:
		
		//if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
			
			try {
				int available = input.available();
				Robot a = new Robot();
				serial_raw = new BufferedReader(new InputStreamReader(input));

				//byte chunk[] = new byte[available];
				//input.read(chunk, 0, available);

				//System.out.print(new String(chunk));
				
				// Displayed results are codepage dependent
				//System.out.println(available);
				String line =new String();
				
				while ((line = serial_raw.readLine()) != null) {

					 // Ghetto string consistency check
					 //if (s1.length() < 29) { arduino.skip(inStream.available()); continue; }
					 //if (s1.charAt(4) != ',' || s1.charAt(9) != ',' || s1.charAt(14) != ',' || s1.charAt(19) != ',' || s1.charAt(24) != ',') { arduino.skip(inStream.available()); continue; }
					 /*if (line.charAt(0) == '*')
					 {
						 serial_raw.skip(input.available());
						 continue;
					 }*/
					 //start=System.nanoTime();
					 StringTokenizer splitted = new StringTokenizer(line,"\t");
					 int raw_data[] = new int [9];
					 int count=0;

					 while (splitted.hasMoreTokens()) {
						 try
						 {
							int value = Integer.parseInt(splitted.nextToken());
						    raw_data[count] = value;
						 	count++;
						 }
						 catch ( Exception e)
						 {
							 serial_raw.skip(input.available());
							 continue;
						 }
					 }
					 
					 /*
					 System.out.print(raw_data[0]+"\t");
					 System.out.print(raw_data[1]+"\t");
					 System.out.print(raw_data[2]+"\t");
					 System.out.print(raw_data[3]+"\t");
					 System.out.print(raw_data[4]+"\t");
					 System.out.print(raw_data[5]+"\t");
					 System.out.print(raw_data[6]+"\t");
					 System.out.print(raw_data[7]+"\t");
					 System.out.print(raw_data[8]+"\n");
					 */
					 
					 //int signx=1, signy=-1;
					 
	//				 System.out.print(array[4]+"\t");
	//				 System.out.print(array[5]+"\t");
					 
					 if (need_calibrate)
					 {
						 if (first_start) //ignore first reading, mostly not reliable
						 {
							 first_start=false;
							 continue;
						 }
						 
							 x_bias+=raw_data[0];
							 y_bias+=raw_data[1];
							 z_bias+=raw_data[2];
							 gy_bias+=raw_data[4];
							 gx_bias+=raw_data[5];
							 
							 calibration_count++;
						 
						 
						 /*x_bias=(int)Math.round(x_bias/(double)calibration_count);
						 y_bias=(int)Math.round(y_bias/(double)calibration_count);
						 z_bias=(int)Math.round(z_bias/(double)calibration_count);
						 gy_bias=(int)Math.round(gy_bias/(double)calibration_count);
						 gx_bias=(int)Math.round(gx_bias/(double)calibration_count);
						 */

						// gy_bias=gy_bias/calibration_count;
						// gx_bias=gx_bias/calibration_count;
						 if (calibration_count==100)
						 {
							 need_calibrate=false;
							 x_bias=x_bias/calibration_count;
							 y_bias=y_bias/calibration_count;
							 z_bias=z_bias/calibration_count;
							 gy_bias=gy_bias/calibration_count;
							 gx_bias=gx_bias/calibration_count;
						//	 System.out.print(x_bias+"\t"+y_bias+"\t"+z_bias);
							 continue;
						 }
						 else
							 continue;
					 }
					 
			///////// System.out.print((int)x_bias+"\t"+(int)y_bias+"\t"+(int)z_bias+"\t"+raw_data[0]+"\t"+raw_data[1]+"\t"+raw_data[2]+"\t"+raw_data[4]+"\t"+raw_data[5]+"\n");
					 
					 setChanged();
					 notifyObservers(raw_data);
					 //time=Calendar.getInstance();
					 //System.out.println((System.nanoTime()-start)/1000000.0);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				//e.printStackTrace();
			}
			break;
		}
		// Ignore all the other eventTypes, but you should consider the other ones.
	}
}
