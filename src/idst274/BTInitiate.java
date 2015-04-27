package idst274;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import javax.bluetooth.RemoteDevice;

import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

/**
 * Class to start a bluetooth connection to another brick. Basic code came from
 * LeJOS site.
 * 
 * @author Steven Huss-Lederman
 * @version April 17, 2015
 *
 */
public class BTInitiate {
	// true if should information output and false if not.
	boolean printInfo;
	// For bluetooth connection.
	NXTConnection connection;
	// For input/receive from other brick.
	DataInputStream dis;
	// For output/send from other brick.
	DataOutputStream dos;
	// Name of kit to connect to.
	String brickName;
	// Strings to describe situation of connection.
	final static String CONNECTING = "Connecting";
	final static String CONNECTED = "Connected";
	final static String CLOSING = "Closing...";

	/**
	 * For connecting to another brick. Sets output to true.
	 * 
	 * @param brickName
	 *            name of other brick
	 */
	public BTInitiate(String brickName) {
		this.brickName = brickName;
		printInfo = true;
	}

	/**
	 * For outputting information. Controlled by doOutput when true/on by
	 * default.
	 * 
	 * @param info
	 *            what to print.
	 */
	private void output(String info) {
		if (printInfo) {
			System.out.println(info);
		}
	}

	/**
	 * Controls if output is done.
	 * 
	 * @param value
	 *            true for seeing output and false if not.
	 */
	public void doOutput(boolean value) {
		printInfo = value;
	}

	/**
	 * Starts connection to other brick.
	 * 
	 * @return true if succeeded and false otherwise
	 */
	public boolean connect() {
		// Get info based on brick name.
		output(CONNECTING);
		RemoteDevice btrd = Bluetooth.getKnownDevice(brickName);
		if (btrd == null) {
			// failed
			output("No such device");
			return false;
		} else {
			// Get connection
			connection = Bluetooth.connect(btrd);
			if (connection == null) {
				// failed
				output("Connect fail");
				return false;
			} else {
				System.out.println(CONNECTED);
			}
			// Create input/receive and output/send streams.
			dis = connection.openDataInputStream();
			dos = connection.openDataOutputStream();
			return true;
		}
	}

	/**
	 * Receives an int from the other brick.
	 * 
	 * @return value received
	 * @throws IOException
	 *             if error occurs in receive
	 */
	public int receiveInt() throws IOException {
		return dis.readInt();
	}

	/**
	 * Sends an int to the other brick.
	 * 
	 * @param value
	 *            value to send
	 * @return true if value sent and otherwise false
	 */
	public boolean sendInt(int value) {
		try {
			// Send value and flush pipe
			dos.writeInt(value);
			dos.flush();
			return true;
		} catch (IOException ioe) {
			// Failed.
			output("send of " + value + " failed");
			return false;
		}
	}

	/**
	 * Closing all resources. Do when done with connectionl
	 * 
	 * @throws IOException
	 *             If fails.
	 */
	public void done() throws IOException {
		// Close receive/send streams.
		dis.close();
		dos.close();
		// End connection.
		output(CLOSING);
		connection.close();
	}
}
