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
			boolean running = false;
			try
			{
				server = new Socket(ip, port);
				
				BufferedOutputStream bos = new BufferedOutputStream(server.getOutputStream());
				BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
				
				while(true)
				{
					int i = bis.read();
					byte [] b = new byte[100];
					int j = 0;
					while(i != -1)
					{
						b = add(b, i, j);
						j++;
						i = bis.read();
					}
				
					String message = new String(b);
				
					String[][] parsed = Parser.parseMessage(message, "$$$");
					int msgid = Parser.getMessageID(parsed);
					if(Parser.checkMessageID(msgid, message))
					{
						String out = Parser.reparse(Parser.reformat(parsed, Parser.parseMessage(Parser.readMessge(msgid))), "$$$");
						
						if(msgid == 24)
							running = true;
						// write out to server
						byte[] b2 = out.getBytes();
						if(running)
							bos.write(b2, 0, b2.length);
						if(msgid == 25)
							running = false;
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
	}
}
