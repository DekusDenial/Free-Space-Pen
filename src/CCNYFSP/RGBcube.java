package CCNYFSP;

import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Observable;
import java.util.Observer;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import processing.core.PApplet;

public class RGBcube extends JFrame implements Observer
{
	private Cube rgb=new Cube();
	private boolean is_cube_open =false;
	private Observable observed;
	private int x_bias, y_bias, z_bias, gy_bias=386, gx_bias=376;
	private double  Q_angle = 0.002, Q_gyro  = 0.1, R_angle	= 0.3;
	private Arithmetics.RK4 integrator=new Arithmetics.RK4();
	private Arithmetics.KalmanFilter pitchKF= new Arithmetics.KalmanFilter((float)Q_angle, (float)Q_gyro, (float)R_angle);
	//private Arithmetics.KalmanFilter rollKF= new Arithmetics.KalmanFilter((float)Q_angle, (float)Q_gyro, (float)R_angle);
	
	public boolean isCubeOpen(){return is_cube_open;}
	
	public void setOpen(boolean opened)
	  {
		  is_cube_open=true;
	  }
	public void setBiases(int[] biases)
	{
		x_bias=biases[0];
		y_bias=biases[1];
		z_bias=biases[2];
		gy_bias=biases[3];
		gx_bias=biases[4];
		//System.out.println(Arrays.toString(biases));
	}
	
	public RGBcube()
	{
		super("RGB Cube DEMO");
		setSize(400, 430);
		setVisible(false);
		setLocation(600, 100);
		setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("pencil.gif")));
		setLayout(new BorderLayout());
		add(rgb, BorderLayout.CENTER);
		rgb.init();
		
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
		      public void windowOpened(WindowEvent e) {
		      }

		      public void windowClosing(WindowEvent e) {
		        
		    	//rgb.exit();
		    	is_cube_open=false;
		        setVisible(false);        
		        RGBcube.this.dispose();
		        if (!(observed == null))
		        	observed.deleteObserver(RGBcube.this);
		       }
		    });
	}

		
	public static class Cube extends PApplet{
		
		/*public Cube()
		{
			PApplet.main(new String[] { "CCNYFSP.RGBcube" });
		}*/
		
		float xmag, ymag = 0;
		static float roll, pitch, yaw;
		float newXmag, newYmag = 0; 
		int width=400, height=400;
		
		public void setRotation(float roll, float pitch, float yaw)
		{
			this.roll=roll;
			this.pitch=pitch;
			this.yaw=yaw;
		}
		 
		public void setup() 
		{ 
		  size(width, height, P3D); 
		  noStroke(); 
		  colorMode(RGB, 1); 
		} 
		 
		public void draw() 
		{ 
		  background(0);
		  
		  pushMatrix(); 
		 
		  translate(width/2, height/2, -30); 
		  
		  newXmag = mouseX/(float)(width) * TWO_PI;
		  newYmag = mouseY/(float)(height) * TWO_PI;
		  
		  float diff = xmag-newXmag;
		  if (abs(diff) >  0.01) { xmag -= diff/4.0; }
		  
		  diff = ymag-newYmag;
		  if (abs(diff) >  0.01) { ymag -= diff/4.0; }
		  
		  //rotateX(-ymag); 
		  //rotateY(-xmag); 
		  
		  //System.out.println(ymag+"\t"+xmag);
		  //rotateZ(-zmag);
		  
		  rotateX(-pitch); 
		  rotateZ(-roll); 
		  rotateY(yaw);
		  
		  scale(90);
		  beginShape(QUADS);
	
		  fill(0, 1, 1); vertex(-1,  1,  1);
		  fill(1, 1, 1); vertex( 1,  1,  1);
		  fill(1, 0, 1); vertex( 1, -1,  1);
		  fill(0, 0, 1); vertex(-1, -1,  1);
	
		  fill(1, 1, 1); vertex( 1,  1,  1);
		  fill(1, 1, 0); vertex( 1,  1, -1);
		  fill(1, 0, 0); vertex( 1, -1, -1);
		  fill(1, 0, 1); vertex( 1, -1,  1);
	
		  fill(1, 1, 0); vertex( 1,  1, -1);
		  fill(0, 1, 0); vertex(-1,  1, -1);
		  fill(0, 0, 0); vertex(-1, -1, -1);
		  fill(1, 0, 0); vertex( 1, -1, -1);
	
		  fill(0, 1, 0); vertex(-1,  1, -1);
		  fill(0, 1, 1); vertex(-1,  1,  1);
		  fill(0, 0, 1); vertex(-1, -1,  1);
		  fill(0, 0, 0); vertex(-1, -1, -1);
	
		  fill(0, 1, 0); vertex(-1,  1, -1);
		  fill(1, 1, 0); vertex( 1,  1, -1);
		  fill(1, 1, 1); vertex( 1,  1,  1);
		  fill(0, 1, 1); vertex(-1,  1,  1);
	
		  fill(0, 0, 0); vertex(-1, -1, -1);
		  fill(1, 0, 0); vertex( 1, -1, -1);
		  fill(1, 0, 1); vertex( 1, -1,  1);
		  fill(0, 0, 1); vertex(-1, -1,  1);
	
		  endShape();
		  
		  popMatrix(); 
		}
	}

	/*public static void main(String arg[])
	{new RGBcube();}
*/
	
	public void update(Observable t, Object o) {
		int temp[]=(int[]) o;
		observed=t;
		double roll=0.0, pitch=0.0, yaw=0.0, x_g, y_g, z_g;
		x_g=(double) (temp[0]-476);
		y_g=(double) (temp[1]-545);
		z_g=(double) (temp[2]-503);
		pitch=Math.atan2(x_g,Math.sqrt(y_g*y_g+z_g*z_g));
		roll=Math.atan2(y_g,Math.sqrt(x_g*x_g+z_g*z_g));
		pitchKF.predict((((temp[4]-gy_bias)*3.27/1023.0)/0.008)*Math.PI/180.0, temp[7]/1000.0);
		pitch=pitchKF.update(pitch);
		//System.out.print(pitchKF.x_angle*180.0/Math.PI+"\t");
		//rollKF.predict(dotAngle, temp[7]/1000.0);
		yaw=integrator.computeRK4((((temp[5]-gx_bias)*3.27/1023.0)/0.008)*temp[7]/1000.0)*Math.PI/180.0;
		//System.out.println(pitch*180.0/Math.PI);
		//integrator.computeRK4(((temp[5]-376)*3.27/1023.0)*temp[7]/1000.0);
		//System.out.println(yaw*180.0/Math.PI);
		rgb.setRotation((float)roll, (float)pitch, (float)yaw);
	}
}
