package idst274;

import java.io.File;

import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.SoundSensor;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
import lejos.util.Delay;

public class Intiate {

	public static void main(String[] args) {

		// Status codes to send to the back legs of the dog
		int moveForward = 2;
		int moveBackwards = 3;
		int turn = 4;

		// Connect to back legs. It should already be listening for front
		// section code
		BTInitiate btc = new BTInitiate("K14CS"); // K14CS is name of back legs
													// nxt brick

		UltrasonicSensor uS = new UltrasonicSensor(SensorPort.S1);
		int distance;
		btc.connect();

		Thread threadSensors = new Thread(new Sensors());
		// Thread threadDrive = new Thread(new Drive());
		threadSensors.start();
		// threadDrive.start();

		
		int i = 0; 
		while (true) {
			distance = uS.getDistance();

			// Decide what back legs should do here
			if (distance < 50) {

				// Send move code to back legs
				btc.sendInt(moveBackwards);
			} else {
				// Send move code to back legs
				btc.sendInt(moveForward);				
			}
			if(i % 1000 == 0){
				btc.sendInt(turn);
			}
			i++;
		}

	}

	/**
	 * TODO Integrate this into build
	 */
	// public static void findNear (){
	// int defaultAngle = 0;
	// Motor.C.rotateTo(defaultAngle - 80);
	// double nearest = us.getDistance();
	// int nearestAngle = 0;
	//
	// for(int i = 0; i < 16; i++){
	// double currentDistance = us.getDistance();
	// Motor.C.rotate(10);
	// if (currentDistance < nearest){
	// nearest = currentDistance;
	// nearestAngle = i;
	//
	// };
	//
	// }
	//
	// Motor.C.rotate (-(160 - (10*nearestAngle)));
	// }

}

class Sensors implements Runnable {
	public void run() {
		int thresholdSound;
		int currentSoundValue;
		int biggestSound;
		int smallestSound;
		TouchSensor ts = new TouchSensor(SensorPort.S3);
		SoundSensor ss = new SoundSensor(SensorPort.S4);
		ss.setDBA(true);

		File soundHappy = new File("dog_puppy.wav");
		File soundSad = new File("dog_whine.wav");
		Sound.setVolume(75);

		boolean isTailDown = false;
		while (true) {
			biggestSound = 0;
			smallestSound = 100;
			int[] soundValues = new int[600];

			for (int i = 0; i < 600; i++) {
				currentSoundValue = ss.readValue();
				soundValues[i] = currentSoundValue;
				if (soundValues[i] > biggestSound) {
					biggestSound = soundValues[i];
				}
				if (soundValues[i] < smallestSound) {
					smallestSound = soundValues[i];
				}
			}

			thresholdSound = (biggestSound - smallestSound);
			System.out.println("Sound threshhold: " + thresholdSound);
			// Happy reaction
			if (ts.isPressed()) {
				Sound.playSample(soundHappy);
				ears(1);
				isTailDown = wagTailHappy(isTailDown);
			}

			// Sad reaction
			if (thresholdSound > 30) {
				// Loud case
				System.out.println("Loud sound case");
				Sound.playSample(soundSad);
				ears(-1);
				isTailDown = wagTailSad(isTailDown);
			} else if (thresholdSound <= 10) {
				// Quiet case
				System.out.println("Quiet sound case");
				// do something
			} else {
				// Average case, do nothing
				System.out.println("Average sound case");
			}
			Delay.msDelay(50);
		}

	}

	/**
	 * Moves the ears based off current position and received mood
	 * 
	 * @param mood
	 * @param currentPosition
	 * @return
	 */
	public static void ears(int mood) {
		// Happy
		if (mood == 1) {
			Motor.B.rotate(145, true);
			Delay.msDelay(500);
			Motor.B.rotate(-145, true);
			Delay.msDelay(500);
		}

		// Sad
		if (mood == -1) {
			Motor.B.rotate(-145, true);
			Delay.msDelay(500);
			Motor.B.rotate(145, true);
			Delay.msDelay(500);
		}
		
	}

	public static boolean wagTailHappy(boolean down) {

		// If down move tail up for waging
		if (down) {
			Motor.C.setSpeed(400);
			Motor.C.rotate(100);
			Delay.msDelay(500);
		}

		// Start Waging
		for (int i = 0; i < 5; i++) {
			Motor.C.rotate(30);
			Delay.msDelay(50);
			Motor.C.rotate(-30);
			Delay.msDelay(50);
		}
		return false;
	}

	public static boolean wagTailSad(boolean down) {

		// If down move tail up for waging
		if (!down) {
			Motor.C.setSpeed(400);
			Motor.C.rotate(-100);
			Delay.msDelay(500);
		}
		return true;
	}
}