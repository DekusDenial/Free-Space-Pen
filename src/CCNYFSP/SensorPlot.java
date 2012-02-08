package CCNYFSP;


//Dynamic Grapher
//teus.java
//
//
//Created by Matt on 27/12/09.
//

import java.awt.*; //window
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*; //drawing

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.LineAndShapeRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Millisecond;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import gnu.io.*;      //serial connection, rxtx
import java.io.*;	  //input/output streams
import java.util.*;   //for string tokenizer

public class SensorPlot extends JFrame implements Observer{

	//private JFrame frame = new JFrame("Dynamic Data Graph");
	private TimeSeries series1 = new TimeSeries("X Accelerometer");
	private TimeSeries series2 = new TimeSeries("Y Accelerometer");
	private TimeSeries series3 = new TimeSeries("Z Accelerometer");
	private TimeSeries series4 = new TimeSeries("X Axis Gyroscope");
	private TimeSeries series5 = new TimeSeries("Y Axis Gyroscope");
	private TimeSeries series6 = new TimeSeries("Z Axis Gyroscope");
	private TimeSeriesCollection dataset = new TimeSeriesCollection();
	private TimeSeriesCollection dataset2 = new TimeSeriesCollection();
	private boolean is_plot_open =false;
	private Observable observed;
	public boolean isPlotOpen(){return is_plot_open;}
	
  @SuppressWarnings("deprecation")
  
  //public setObservable(Observable t)
  
  public void setOpen(boolean opened)
  {
	  is_plot_open=true;
  }
  
public SensorPlot()
  {
	  setVisible(false);
	  setLocation(100, 100);
	  setTitle("Dynamic Data Graph");
	  setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("pencil.gif")));
	  //setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	  setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
	  addWindowListener(new WindowAdapter() {
	      public void windowOpened(WindowEvent e) {
	      }

	      public void windowClosing(WindowEvent e) {
	        is_plot_open=false;
	        setVisible(false);
	        //SensorPlot.this.removeAll();	        
	        SensorPlot.this.dispose();
	        if (!(observed == null))
	        	observed.deleteObserver(SensorPlot.this);
	       }
	    });
	  
      dataset.addSeries(series1);
      dataset.addSeries(series2);
      dataset.addSeries(series3);
      
      dataset.addSeries(series4);
      dataset.addSeries(series5);
      dataset.addSeries(series6); 

      series1.setMaximumItemCount(100);
      series2.setMaximumItemCount(100);
      series3.setMaximumItemCount(100);
      series4.setMaximumItemCount(100);
      series5.setMaximumItemCount(100);
      series6.setMaximumItemCount(100);

      final JFreeChart chart = ChartFactory.createTimeSeriesChart(
              "Sensors Fusion Output",
              "Time Elapsed",
              "10-bit ADC value (0-1023)",
              dataset,
              true,
              false,
              false
          );
          final XYPlot plot = chart.getXYPlot();
          ((XYLineAndShapeRenderer)plot.getRenderer()).setStroke(new BasicStroke(2f, BasicStroke.JOIN_ROUND, BasicStroke.JOIN_BEVEL));
          //plot.getRenderer().setSeriesPaint(0, Color.MAGENTA);
          ValueAxis axis = plot.getDomainAxis();
          axis.setAutoRange(true);
          axis.setFixedAutoRange(1000.0);  // 30 seconds
          axis = plot.getRangeAxis();
          axis.setRange(0.0, 1024.0);

          ChartPanel chartPanel = new ChartPanel(chart);
          final JPanel content = new JPanel(new BorderLayout());
          content.add(chartPanel);
          chartPanel.setPreferredSize(new java.awt.Dimension(450, 340));

          getContentPane().add(content, BorderLayout.CENTER);

          /*final JFreeChart chart2 = ChartFactory.createTimeSeriesChart(
                  "Gyroscope Outputs",
                  "Time",
                  null,
                  dataset2,
                  true,
                  false,
                  false
              );
              final XYPlot plot2 = chart2.getXYPlot();
              ValueAxis axis2 = plot2.getDomainAxis();
              axis2.setAutoRange(true);
              axis2.setFixedAutoRange(1000.0);  // 30 seconds
              axis2 = plot2.getRangeAxis();
              axis2.setRange(0.0, 1024.0);

              ChartPanel chartPanel2 = new ChartPanel(chart2);
              final JPanel content2 = new JPanel(new BorderLayout());
              content2.add(chartPanel2);
              chartPanel2.setPreferredSize(new java.awt.Dimension(700, 340));

            getContentPane().add(content2, BorderLayout.SOUTH);
					*/
            pack();
      		//setVisible(true);
      		setResizable(false);
  }
	
         
			//serial input buffer flush
  

@Override
public void update(Observable t, Object o)
{
	observed=t;
	int temp[]=(int[]) o;

	//final Millisecond now = new Millisecond();
	 //System.out.println("Now = " + now.toString());
	 series1.add(new Millisecond(),temp[0], false);
	 series2.add(new Millisecond(),temp[1], false);
	 series3.add(new Millisecond(),temp[2], true);
	 series4.add(new Millisecond(),temp[3], false);
	 series5.add(new Millisecond(),temp[4], false);
	 series6.add(new Millisecond(),temp[5], true);
	
	 //System.out.println(series1.getItemCount());
	 //System.out.println(now.getMillisecond());
	
		//Display the window.
	
		try
		{
			Thread.sleep(1); // do nothing for t milliseconds (refresh rate limiter, no flicker)
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
		}
	
	
	}
  }
