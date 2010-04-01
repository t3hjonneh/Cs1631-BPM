import java.util.*;
import java.io.*;
import java.lang.*;

public class Parser
{
	public Parser()
	{} // this method is completely useless because every method in this file is static
	
	// reparse takes in a message array of the type which comes out of a parseMessage method
	// it then converts this 2d array back to the message of the type which can be sent to the server
	public static String reparse(String [][] msg, String delimeter)
	{
		String ret = msg[0][0] + delimeter + msg[1][0];
		for(int i = 1; i < msg[0].length; i++)
			ret += delimeter + msg[0][i] + delimeter + msg[1][i];
		
		return ret;
	}
	
	// returns the value of a specific element from a 2d array of the type which comes out of
	// a parseMessage method
	// returns an empty string if the key does not exist
	public static String getVal(String[][] msg, String key)
	{
		for(int i = 0; i < msg[0].length; i++)
			if(msg[0][i].equals(key))
				return msg[1][i];
				
		return "";
	}
	
	// sets the value of a key in a 2d array
	public static void setVal(String[][] msg, String key, String val)
	{
		for(int i = 0; i < msg[0].length; i++)
			if(msg[0][i].equals(key))
				msg[1][i] = val;
	}
	
	// reads in the xml of the file which holds msgid
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
	
	// returns the message id of a given parsed message
	// returns -1 if the message id is not found
	public static int getMessageID(String[][] parsed)
	{
		int i = 0;
		while((!parsed[0][i].equals("MsgID")) && (i < parsed[0].length))
			i++;
		
		if(i == parsed[0].length) // note: this is a failsafe condition which should never be activated
			return -1;
		// else:
		return (new Integer(parsed[1][i])).intValue();
	}

	// parses a message from it's delimeted form used on the netwok into a 2d array
	// this 2d array is used as an input to a number of methods defined in parser
	public static String[][] parseMessage(String message, String delimeter)
	{
		String [] items = message.split(delimeter);
		String [][] ret = new String[2][items.length/2];
		for(int i = 0; i < items.length; i++)
			ret[i%2][i/2] = items[i];
		return ret;
	}

	// this parses an xml file of the type found in our g3 folder into a 2 d array
	// this 2d array is used as an input to a number of methods defined in parser
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
		
		// get the msgid and the description
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
		
		// get all keys as denoted by the tag <key>key</key>
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

		// get all the values as denoted by the tag <value>value</value> each value is assumed to be directly
		// associated with the tkey in the same order as the keys were given
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

		// store the values of the keys and their respective values in the
		// array to return
		String [][] ret = new String[2][keys.size()];
		
		ret[0] = keys.toArray(ret[0]);
		ret[1] = values.toArray(ret[1]);

		return ret;
	}
}
