package idst274;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import lejos.nxt.comm.Bluetooth;
import lejos.nxt.comm.NXTConnection;

/**
 * Class to accept a bluetooth connection from another brick. Basic code came
 * from LeJOS site.
 * 
 * @author Steven Huss-Lederman
 * @version April 17, 2015
 *
 */
public class BTAccept {
	// true if should information output and false if not.
	boolean printInfo;
	// For bluetooth connection.
	NXTConnection connection;
	// For input/receive from other brick.
	DataInputStream dis;
	// For output/send from other brick.
	DataOutputStream dos;
	// Strings to describe situation of connection.
	final static String CONNECTED = "Connected";
	final static String WAITING = "Waiting...";
	final static String CLOSING = "Closing...";

	/**
	 * For accepting connection from another brick. Sets output to true.
	 */
	public BTAccept() {
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
	 * Waits and connects to another brick.
	 * 
	 * @return always true
	 */
	public boolean connect() {
		// Connect to other brick where waits/blocks until happens.
		output(WAITING);
		connection = Bluetooth.waitForConnection();
		output(CONNECTED);
		// Set up read and send streams.
		dis = connection.openDataInputStream();
		dos = connection.openDataOutputStream();
		return true;
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