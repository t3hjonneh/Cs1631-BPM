import java.util.*;
import java.io.*;
import java.lang.*;

public class Parser
{
	public Parser()
	{}
	
	public static String reparse(String [][] msg, String delimeter)
	{
		String ret = msg[0][0] + delimeter + msg[1][0];
		for(int i = 1; i < msg[0].length; i++)
			ret += delimeter + msg[0][i] + delimeter + msg[1][i];
		
		return ret;
	}
	
	public static String getVal(String[][] msg, String key)
	{
		for(int i = 0; i < msg[0].length; i++)
			if(msg[0][i].equals(key))
				return msg[1][i];
				
		return "";
	}
	
	public static String[][] setVal(String[][] msg, String key, String val)
	{
		for(int i = 0; i < msg[0].length; i++)
			if(msg[0][i].equals(key))
				msg[1][i] = val;
		
		return msg;
	}
	
	public static String readMessage(int msgid)
	{
		Scanner in;
		String ret = "";
		try
		{
			in = new Scanner(new FileInputStream("xml/g3/Msg" + msgid + ".xml"));
			ret = new String();
			while(in.hasNextLine())
				ret += in.nextLine();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		
		
		return ret;
	}
	
	public static int getMessageID(String[][] parsed)
	{
		int i = 0;
		while(!parsed[0][i].equals("MsgID"))
			i++;
		
		return (new Integer(parsed[1][i])).intValue();
	}

	public static String[][] parseMessage(String message, String delimeter)
	{
		String [] items = message.split(delimeter);
		String [][] ret = new String[2][items.length/2];
		for(int i = 0; i < items.length; i++)
			ret[i%2][i/2] = items[i];
		return ret;
	}

	public static String[][] parseMessage(String message)
	{
		String key1 = new String("<Key>");
		String key2 = new String("</Key>");
		String value1 = new String("<Value>");
		String value2 = new String("</Value>");

		ArrayList<String> keys = new ArrayList<String>();
		ArrayList<String> values = new ArrayList<String>();

		
		String msgid = "<MsgID>";
		String msgid2 = "</MsgID>";
		String desc = "<Description>";
		String desc2 = "</Description>";
		
		keys.add("MsgID");
		keys.add("Description");
		
		int start = 0;
		int end = 0;
		int offset = 0;
		
		start = message.indexOf(msgid, 0);
		start += 7;
		end = message.indexOf(msgid2, start);
		values.add(message.substring(start, end));
		start = message.indexOf(desc, 0);
		start += 13;
		end = message.indexOf(desc2, start);
		values.add(message.substring(start, end));
		
		while(true)
		{
			start = message.indexOf(key1, offset);
			if(start < 0)
				break;
			
			start += 5;
			
			end = message.indexOf(key2, start);
			
			keys.add(message.substring(start, end));
			offset = end + 6;
		}

		offset = 0;

		while(true)
		{
			start = message.indexOf(value1, offset);
			if(start < 0)
				break;
			
			start += 7;
			
			end = message.indexOf(value2, start);
			
			values.add(message.substring(start, end));
			offset = end + 8;
		}

		String [][] ret = new String[2][keys.size()];
		
		ret[0] = keys.toArray(ret[0]);
		ret[1] = values.toArray(ret[1]);

		return ret;
	}
}
