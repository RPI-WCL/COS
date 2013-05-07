package src.profiling_agent.monitors;

import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class LatencyMonitor extends Thread {
	private int port = 22222;
	private ServerSocket sensorSocket;

	public LatencyMonitor() {
		String newPort = System.getProperty("lmp");
		if (newPort != null) port = new Integer(newPort).intValue();
	}
	
	public void run() {
		if (sensorSocket == null) {
			// Create a server socket.
			try {
				sensorSocket = new ServerSocket( port );
			} catch (IOException e) {
				System.err.println("Latency Monitor error:");
				System.err.println("\tCould not start monitor.");
				System.err.println("\tException: " + e);
				return;
			}
			
			ServerSocket passive = sensorSocket;
			int i = 0;

			while (true) {
				try {
					final Socket incoming = passive.accept();
					Runnable handlerThread = new Runnable() {
						public void run() { processRequest(incoming); }
					};	
					new Thread(handlerThread, "Handler Loop " + i).start();
				} catch (IOException e) {
					System.err.println("Delay Sensor error: ");
					System.err.println("\tFailed to accept connection.");
					System.err.println("\teException: " + e);
				}
				i++;
			}
		}
	}
	
	public void processRequest(Socket socket) {
//		System.out.println("Received ping request from " + socket.getInetAddress() + ":" + socket.getLocalPort());
		try {
			ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
			ObjectOutputStream oos = null;
			
			while(true) {
				//Read one byte and send back one byte
				ois.readByte();
				
				if (oos == null) oos = new ObjectOutputStream(socket.getOutputStream());

				oos.writeByte(0);
				oos.flush();
			}
		} catch(EOFException e) {
//			System.err.println("Latency Monitor Error:");
//			System.err.println("\tSocket reset by peer");
//			System.err.println("\tException: " + e);
//			e.printStackTrace();
		} catch (Exception e) {
//			System.err.println("Latency Monitor Error:");
//			System.err.println("\tException: " + e);
//			e.printStackTrace();
		}
	}
}
