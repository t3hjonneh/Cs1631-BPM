import java.util.*;
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
	
	private static String getVal(String[][] msg, String val)
	{
		int i = 0;
		while(!msg[0][i].equals(val))
			i++;
		
		if(msg[1][i].equals("132"))
			return "131";
		else
			return msg[1][i];
	}
	
	public static String[][] reformat(String[][] msg1, String[][] msg2)
	{ // format msg1 according to msg2
		String[][] ret = new String[2][msg1[0].length];
		
		for(int i = 0; i < msg1[0].length; i++)
		{
			ret[0][i] = msg2[0][i];
			ret[1][i] = Parser.getVal(msg1, msg2[0][i]);
		}
		
		return ret;
	}
	
	public static String readMessage(int msgid)
	{
		Scanner in = new Scanner(new FileInputStream("Msg" + msgid + ".xml"));
		String ret = new String();
		while(in.hasNextLine())
			ret += in.nextLine();
		
		return ret;
	}
	
	public static int getMessageID(String[][] parsed)
	{
		int i = 0;
		while(!parsed[0][i].equals("MsgID"))
			i++;
		
		return (new Integer(parsed[1][i])).intValue();
	}

	public static boolean checkMsgID(int id, String mes)
	{
		switch(id)
		{
			case 24:
				return Parser.secondCheck(mes);
			case 25:
				return Parser.secondCheck(mes);
			case 31:
				return true;
			case 130:
				return true;
			case 132:
				return true;
		}

		return false;
	}

	private static boolean secondCheck(String mes)
	{
		// TODO: have it actually preform the check
		return true;
	}

	public static String[][] parseMessage(String message, String delimeter)
	{
		String [] items = message.split(delimiter);
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

		int start = 0;
		int end = 0;
		int offset = 0;
		
		while(true)
		{
			start = message.indexOf(key1, offset);
			if(start < 0)
				break;
			
			start += 5;
			
			end = message.indexOf(key2, start);
			
			keys.add(message.substring(start, end);
			offset = end + 6;
		}

		offset = 0;

		while(true)
		{
			start = message.indexOf(value1, offset);
			if(start < 0)
				break;
			
			start += 5;
			
			end = message.indexOf(value2, start);
			
			values.add(message.substring(start, end);
			offset = end + 6;
		}

		String [][] ret = new String[2][keys.size()];
		
		ret[0] = keys.toArray(ret[0]);
		ret[1] = values.toArray(ret[1]);

		return ret;
	}
}
