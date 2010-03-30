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
	
	private String getConfirm(int msgID)
	{
		return "MsgID$$$26$$$Description$$$Acknowledgement$$$AckMsgID$$$" + msgID + "$$$YesNo$$$Yes$$$Name$$$BloodPressureMonitorKnowledgeBase";
	}
	
	private byte[] add(byte[] b, int i, int offset)
	{
		if(offset == b.length)
		{
			byte[] ret = new byte[b.length*2];
			for(int j = 0; j < b.length; j++)
				ret[j] = b[j];
			
			b = ret;
		}
		
		b[offset] = (byte)i;
		
		return b;
	}
	
	private String[][] parseFrom31(String[][] parsed, String[][] msg133)
	{
		return parsed;
	}
	
	private String[][] parseFrom130(String[][] parsed, String[][] msg133)
	{
		int i = 0;
		for(; !parsed[0][i].equals("MsgID"); i++);
		
		parsed[1][i] = "133";
		
		return parsed;
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
				
				PrintWriter pw = new PrintWriter(server.getOutputStream());
				BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
				
				while(true)
				{
					int i = bis.read();
					byte [] b = new byte[100];
					int j = 0;
					while(i != -1 && ((char)i) != '\n')
					{
						b = add(b, i, j);
						j++;
						i = bis.read();
					}
					b[j-1] = 0;
					String message = new String(b);
					System.out.println("***Message Recieved***");
					System.out.println(message);
					System.out.println("\n");
				
					String[][] parsed = Parser.parseMessage(message, "[$][$][$]");
					int msgid = Parser.getMessageID(parsed);
					if(Parser.checkMsgID(msgid, message))
					{
						String out = "";
						String [][] msg133;
						
						switch(msgid)
						{
							case 24:
								running = true;
								out = getConfirm(msgid);
								break;
							case 25:
								running = false;
								out = getConfirm(msgid);
								pw.println(out);
								pw.flush();
								break;
							case 31:
								msg133 = Parser.parseMessage(Parser.readMessage(133));
								out = Parser.reparse(parseFrom130(parsed, msg133), "$$$"); // parseFrom31(parsed, msg133), "$$$");
								break;
							case 130:
								msg133 = Parser.parseMessage(Parser.readMessage(133));
								out = Parser.reparse(parseFrom130(parsed, msg133), "$$$");
								break;
						}
						
						
						// write out to server
						if(running)
						{
							pw.println(out);
							pw.flush();
							System.out.println("***Message Sent***");
							System.out.println(out);
							System.out.println("\n");
						}
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
