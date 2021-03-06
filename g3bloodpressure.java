// see Knowledgebase
import java.io.*;
import java.net.*;
import java.util.*;

public class  g3bloodpressure
{ 
	static boolean debug = false;
	
	public static void main(String []args) throws Exception
	{
		if(args.length > 0)
			for(String var : args)
				if(var.equals("-debug"))
					debug = true;
		
		File f = new File("g3/");
        	if(!f.exists() || !f.isDirectory())
		{
			f.mkdir();
			if(debug)
				System.out.println("directory g3 was created");
			System.out.println("xml files not found. Please place xml files in g3 directory which was just created.");
			System.exit(1);
		}

		new g3bloodpressure();
	}
	
	public g3bloodpressure()
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
		
			if(debug)
				System.out.println("Initializing...");
				
			String[][] outMessage = Parser.parseMessage(Parser.readMessage(23));
			Parser.setVal(outMessage, "Name", "g3bloodpressure");
			out.println(Parser.reparse(outMessage,"$$$"));
			out.flush();
			
			while(true)
			{
				if(debug)
					System.out.println("Waiting...");
					
				message = in.readLine();
				
				if(debug)
					System.out.println("Message Recieved:\n"+message);
				
				String[][] parsed = Parser.parseMessage(message, "[$][$][$]");
				int msgid = Parser.getMessageID(parsed);
				
				if(msgid != 26)
				{
					out.println(getMessage(msgid, parsed));
					out.flush();
				}
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
			outMessage = Parser.parseMessage(Parser.readMessage(130));
		else if(msgID == 131)
			outMessage = Parser.parseMessage(Parser.readMessage(32));
		else
		{
			outMessage = Parser.parseMessage(Parser.readMessage(26));
			Parser.setVal(outMessage, "AckMsgID", Integer.toString(msgID));
		}
		
		for(int i = 0; i < message[0].length; i++)
			if(!message[0][i].equals("MsgID") && !message[0][i].equals("Description"))
				Parser.setVal(outMessage, message[0][i], message[1][i]);
		
		String out = Parser.reparse(outMessage, "$$$");
		if(debug)
			System.out.println("Message Sent:\n"+out);
		
		return out;
	}
}

	
