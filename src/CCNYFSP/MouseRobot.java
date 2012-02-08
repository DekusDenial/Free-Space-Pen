package CCNYFSP;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

public class MouseRobot implements Observer{
	private Robot mouse;
	private int cur_x;
	private int cur_y;
	private int screenwidth=Toolkit.getDefaultToolkit().getScreenSize().width;
	private int screenheight=Toolkit.getDefaultToolkit().getScreenSize().height;
	private int both_pressed=0;
	private double linear_pixel_scale=120.0;
	private double nonlinear_exponent=1.3;
	private int x_bias, y_bias, z_bias, gy_bias=386, gx_bias=376;
	//private boolean need_calibrate=true;
	//private int calibration_count=0;
	//private boolean first_start=true;
	private double x=0.0, y=0.0;
	private boolean was_left_pressed=false;
	private boolean was_right_pressed=false;
	private boolean gesture_trigger=false;
	private boolean was_gesture=false;
	private boolean seq1=false, seq2=false;

	
	public MouseRobot(int[] biases) throws AWTException
	{
		//System.out.println(Arrays.toString(biases));
		mouse=new Robot();
		x_bias=biases[0];
		y_bias=biases[1];
		z_bias=biases[2];
		gy_bias=biases[3];
		gx_bias=biases[4];
	}
	
	public int[] getMouseBiases()
	{
		int temp[]=new int[5];
		temp[0]=x_bias;
		temp[1]=y_bias;
		temp[2]=z_bias;
		temp[3]=gy_bias;
		temp[4]=gx_bias;
		return temp;
	}
	
	public void setDPI(int dpi)
	{
		linear_pixel_scale=(double)dpi;
	}
	
	public void update(Observable t, Object o) {
		int temp[]=(int[]) o;
		if (!gesture_trigger)
			feedRobot(temp[0], temp[1], temp[2], temp[5], temp[4], temp[6]);
		else
			gestureTrigger(temp[0], temp[1], temp[2], temp[5], temp[4], temp[6]);
	}
	
	public void gestureTrigger(int x_acc, int y_acc, int z_acc, int dx, int dy, int clicks)
	{
		if (clicks==3)
		{
			if (!was_gesture){
				if (y_acc<=350)
				{
					mouse.keyPress(KeyEvent.VK_CONTROL);
					mouse.keyPress(KeyEvent.VK_EQUALS);
					mouse.keyRelease(KeyEvent.VK_EQUALS);
					mouse.keyRelease(KeyEvent.VK_CONTROL);
					mouse.delay(10);
					was_gesture=true;
				}
				if (y_acc>=768)
				{
					mouse.keyPress(KeyEvent.VK_CONTROL);
					mouse.keyPress(KeyEvent.VK_MINUS);
					mouse.keyRelease(KeyEvent.VK_MINUS);
					mouse.keyRelease(KeyEvent.VK_CONTROL);
					mouse.delay(10);
					was_gesture=true;
				}
				if (x_acc>=600)
				{
					seq1=true;
				}
				
				if (x_acc<=360 && seq1 )
				{
					mouse.keyPress(KeyEvent.VK_CONTROL);
					mouse.keyPress(KeyEvent.VK_A);
					mouse.keyRelease(KeyEvent.VK_A);
					mouse.keyRelease(KeyEvent.VK_CONTROL);
					mouse.delay(10);
					seq1=false;
					was_gesture=true;
				}
			}
		}
		else
			{
			gesture_trigger=false;
			was_gesture=false;
			}
	}
		
	public void feedRobot(int x_acc, int y_acc, int z_acc, int dx, int dy, int clicks)
	{
		 if ((dx-gx_bias)<0)
			 x=linear_pixel_scale*Math.pow(((Math.abs(dx-gx_bias))/(double)gx_bias), nonlinear_exponent);
		 else
			 x=-linear_pixel_scale*Math.pow(((Math.abs(dx-gx_bias))/(double)gx_bias), nonlinear_exponent);
		 
		 if ((dy-gy_bias)<0)
			 y=-linear_pixel_scale*Math.pow(((Math.abs(dy-gy_bias))/(double)gy_bias), nonlinear_exponent);
		 else
			 y=linear_pixel_scale*Math.pow(((Math.abs(dy-gy_bias))/(double)gy_bias), nonlinear_exponent);
		 
		 cur_x=MouseInfo.getPointerInfo().getLocation().x;
		 cur_y=MouseInfo.getPointerInfo().getLocation().y;
							 				 
		 mouse.mouseMove(cur_x+(int)Math.round(x), cur_y+(int)Math.round(y));
		 
		 if ((clicks==3) || (clicks==2))
		 {
			 if (!was_left_pressed)
			 {
				 mouse.mousePress(InputEvent.BUTTON1_MASK);
				 was_left_pressed=true;
			 }
			 if (was_left_pressed && was_right_pressed)
			 {
				 both_pressed++;
				 if (both_pressed==100) // special function trigger
				 {
					 mouse.mouseMove(screenwidth/2, screenheight/2);
					 gesture_trigger=true;
					 both_pressed=0;
				 }
			 }		 
		 }
		 else
		 {
			 both_pressed=0;
			 if(was_left_pressed)
			 {
				 mouse.mouseRelease(InputEvent.BUTTON1_MASK);
				 was_left_pressed=false;
			 }
		 }
		 
		 if ((clicks==3) || (clicks==1))
		 {
			 if (clicks==3 && !was_right_pressed)
			 {
				 mouse.mousePress(InputEvent.BUTTON1_MASK);
				 mouse.mouseRelease(InputEvent.BUTTON1_MASK);
				 mouse.mousePress(InputEvent.BUTTON1_MASK);
				 mouse.mouseRelease(InputEvent.BUTTON1_MASK);
				 was_right_pressed=true;
			 }
			 else if (!was_right_pressed)
			 {
				 mouse.mousePress(InputEvent.BUTTON3_MASK);
				 was_right_pressed=true;
			 }					 
		 }
		 else
		 {
			 both_pressed=0;
			 if(was_right_pressed)
			 {
				 if (!was_left_pressed)
				 {
					 mouse.mouseRelease(InputEvent.BUTTON3_MASK);
					 was_right_pressed=false;
				 }
				 else
					 was_right_pressed=false;
				
												 
			 }
		 }
	}

}
