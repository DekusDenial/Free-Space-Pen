package CCNYFSP;

public class Arithmetics {
		
	public static class KalmanFilter
	{
		double x_angle, x_bias;
		double P_00, P_01, P_10, P_11;
		double Q_angle, Q_gyro;
		double R_angle;
		
		public KalmanFilter(float Q_angle, float Q_gyro, float R_angle)
		{
			this.Q_angle=Q_angle;
			this.Q_gyro=Q_gyro;
			this.R_angle=R_angle;
			this.P_00=0.0;
			this.P_01=0.0;
			this.P_10=0.0;
			this.P_11=0.0;
		}
		
		void predict(double dotAngle, double dt)
		{
			this.x_angle += dt * (dotAngle - this.x_bias);
			this.P_00 += -1 * dt * (this.P_10 + this.P_01) + dt*dt * this.P_11 + this.Q_angle;
			this.P_01 += -1 * dt * this.P_11;
			this.P_10 += -1 * dt * this.P_11;
			this.P_11 += this.Q_gyro;
		}
		
		double update(double angle_m)
		{
			final double y = angle_m - this.x_angle;
			final double S = this.P_00 + this.R_angle;
			final double K_0 = this.P_00 / S;
			final double K_1 = this.P_10 / S;

			this.x_angle += K_0 * y;
		  	this.x_bias  += K_1 * y;
		  
		  	this.P_00 -= K_0 * this.P_00;
		  	this.P_01 -= K_0 * this.P_01;
		  	this.P_10 -= K_1 * this.P_00;
		  	this.P_11 -= K_1 * this.P_01;
		  
		  	return this.x_angle;
		}
	}
	
	public static class RK4
	{
		double val_i_3;
		double val_i_2;
		double val_i_1;
		double previous;
		
		public RK4()
		{
			this.val_i_1=0.0;
			this.val_i_2=0.0;
			this.val_i_3=0.0;
			this.previous=0.0;
		}
		
		double computeRK4(double val_i_0) 
		{
			this.previous += 0.16667*(this.val_i_3 + 2*this.val_i_2 + 2*this.val_i_1 + val_i_0);
			this.val_i_3 = this.val_i_2;
			this.val_i_2 = this.val_i_1;
			this.val_i_1 = val_i_0;
			return this.previous;
		}
	}

}
