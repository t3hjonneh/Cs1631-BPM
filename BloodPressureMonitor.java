import java.io.*;
import java.net.*;
import java.util.*;

public class  BloodPressureMonitor
{ 
	static int debug = 0;
	
	public static void main(String []args) throws Exception
	{
		if(args.length > 0)
			for(String var : args)
				if(var.equals("-debug"))
					debug = 1;
				
		new BloodPressureMonitor();
	}
	
	public BloodPressureMonitor()
	{
		String ip = "127.0.0.1";
		int port = 7999;
		Socket server;
		PrintWriter out;
		BufferedReader in;
		String message;
		
		try
		{
			server = new Socket(ip, port);
			
			out = new PrintWriter(server.getOutputStream());
			in = new BufferedReader(new InputStreamReader(server.getInputStream()));
		
			System.out.println("Initializing...");
			out.println("MsgID$$$23$$$Name$$$BloodPressureMonitor$$$Passcode$$$****");
			out.flush();
			
			while(true)
			{
				System.out.println("Waiting...");
				message = in.readLine();
				
				if(debug == 1)
					System.out.println("Message Recieved:\n"+message);
				
				String[][] parsed = Parser.parseMessage(message, "[$][$][$]");
				int msgid = Parser.getMessageID(parsed);
				
				out.println(getMessage(msgid, parsed));
				out.flush();
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	private String getMessage(int msgID, String[][] message)
	{
		String[][] outMessage = new String[0][0];
		if(msgID == 31)
			outMessage = Parser.parseMessage(Parser.readMessage(133));
		else if(msgID == 130)
			outMessage = Parser.parseMessage(Parser.readMessage(134));
		else if(msgID == 132)
			outMessage = Parser.parseMessage(Parser.readMessage(131));
		else
		{
			outMessage = Parser.parseMessage(Parser.readMessage(26));
			for(int i = 0; i < outMessage[0].length; i++)
				if(outMessage[0][i].equals("AckMsgID"))
					outMessage[1][i] = Integer.toString(msgID);
		}
			
		for(int i = 0; i < message[0].length; i++)
			for(int j = 0; j < outMessage[0].length; j++)
			{
				if(message[0][i].equals(outMessage[0][j]) && !message[0][i].equals("MsgID") && !message[0][i].equals("Description"))
					outMessage[1][j] = message[1][i];
			}
		
		String out = Parser.reparse(outMessage, "$$$");
		if(debug == 1)
			System.out.println("Message Sent:\n"+out);
			
		return out;
	}
}

	
