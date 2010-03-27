import java.io.*;
import java.net.*;
import java.util.*;

public class  BloodPressureMonitor
{ 
	public static void main(String []args) throws Exception
	{
		if (args.length < 1)
        {
            System.out.println("No Arguments were supplied.");
            System.out.println("Usage: java BloodPressureMonitor server_ip");
            System.exit(0);
        }

		new BloodPressureMonitor();
	}
	
	public BloodPressureMonitor()
	{
		commThread T = new commThread();
		T.setDaemon(true);
		T.start();
		
		while(true);
	}
	
	private class commThread extends Thread
	{
		private String ip = "127.0.0.1";
		private int port = 7999;
		private Socket server;
		
		public void commThread()
		{
		
		}
		
		public void commThread(String newIp, int newPort)
		{
			ip = newIp;
			port = newPort;
		}
		
		public void run()
		{
			try
			{
				server = new Socket(ip, port);
				
				//BufferedOutputStream bos = new BufferedOutputStream(server.getOutputStream());
				BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
				while(true)
				{
					byte[] b = new byte[100];
					bis.read(b);
					String input = new String(b);
					System.out.println(input);
				}
				
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
