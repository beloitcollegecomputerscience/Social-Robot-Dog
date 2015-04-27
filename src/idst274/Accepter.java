package idst274;

import java.io.IOException;
import java.util.Random;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

public class Accepter {

	public static void main(String[] args) throws IOException {
		// Create Blue tooth connection accept object
		BTAccept btc = new BTAccept();
		// Start waiting for connection
		btc.connect();
		// Show blue tooth output for debugging
		btc.doOutput(true);

		// Init UltraSonic sensor
		UltrasonicSensor uS = new UltrasonicSensor(SensorPort.S1);

		// Event Loop
		while (true) {

			// Get receive code from front of bot
			int recieveCode = btc.receiveInt();

			//Handle different received codes
			switch (recieveCode) {
			//Forward
			case 2:
				moveForward();
				break;
			//Backwards
			case 3:
				moveBackwards();
				break;
			case 4:
				turn();
				break;
			}
		}

	}
	
	/**
	 * Moves the dogs back legs forward
	 */
	private static void moveForward() {
		Motor.B.setSpeed(300);
		Motor.C.setSpeed(300);
		Motor.B.rotate(180, true);
		Motor.C.rotate(180, true);
	}
	
	/**
	 * Moves the dogs back legs backwards
	 */
	private static void moveBackwards() {
		Motor.B.setSpeed(700);
		Motor.C.setSpeed(700);
		Motor.B.rotate(-180, true);
		Motor.C.rotate(-180, true);
	}
	
	private static void turn(){
		Random rand = new Random();
		rand.nextInt();
		if(rand.nextInt() > 0){
			Motor.B.flt(true);
			Motor.C.setSpeed(700);
			Motor.C.rotate(-180, true);
		}else{
			Motor.C.flt(true);
			Motor.B.setSpeed(700);
			Motor.B.rotate(300, true);
		}
		Delay.msDelay(600);
	}
}
