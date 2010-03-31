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
		
			if(debug == 1)
				System.out.println("Initializing...");
				
			String[][] outMessage = Parser.parseMessage(Parser.readMessage(23));
			outMessage = Parser.setVal(outMessage, "Name", "BloodPressureMonitor");
			out.println(Parser.reparse(outMessage,"$$$"));
			out.flush();
			
			while(true)
			{
				if(debug == 1)
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
			outMessage = Parser.setVal(outMessage, "AckMsgID", Integer.toString(msgID));
		}
		
		for(int i = 0; i < message[0].length; i++)
			if(!message[0][i].equals("MsgID") && !message[0][i].equals("Description"))
				Parser.setVal(outMessage, message[0][i], message[1][i]);
		
		String out = Parser.reparse(outMessage, "$$$");
		if(debug == 1)
			System.out.println("Message Sent:\n"+out);
			
		return out;
	}
}

	
