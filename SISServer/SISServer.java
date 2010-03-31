
/*
 * Originally written by flying@cs.pitt.edu for VC server
 * Simplified and modified to serve as SIS Server by yaosun@cs.pitt.edu 
 * 
 */
import java.io.*;
import java.net.*;
import java.util.*;

/* 
  Class KeyValueList:
    List of (Key, Value) pair--the basic format of message
    keys: MsgID and Description are required for any messages
*/
  
class KeyValueList
{
	private Vector keys;
	private Vector values;
   
	/* Constructor */
	public KeyValueList()
	{
		keys = new Vector();
		values = new Vector();
	}
   
	/* Look up the value given key, used in getValue() */
   
	public int lookupKey(String strKey)
	{
		for(int i=0; i < keys.size(); i++)
		{
			String k = (String) keys.elementAt(i);
			if (strKey.equals(k)) 
				return i;
		} 
		return -1;
	}
   
	/* add new (key,value) pair to list */
   
	public boolean addPair(String strKey,String strValue)
	{
		return (keys.add(strKey) && values.add(strValue));
	}
   
	/* get the value given key */
   
	public String getValue(String strKey)
	{
		int index=lookupKey(strKey);
		if (index==-1) 
			return null;
		return (String) values.elementAt(index);
	} 
	
	public void setValue(int index, String val)
	{
		if(index >= 0 && index < size())
			values.set(index, val);
	}

	/* Show whole list */
	public String toString()
	{
		String result = new String();
		for(int i=0; i<keys.size(); i++)
		{
       		result+=(String) keys.elementAt(i)+":"+(String) values.elementAt(i)+"\n";
		} 
		return result;
	}
   
	public int size()
	{ 
		return keys.size(); 
	}
   
	/* get Key or Value by index */
	public String keyAt(int index){ return (String) keys.elementAt(index);}
	public String valueAt(int index){ return (String) values.elementAt(index);}
	
	public ArrayList<String> getValueLike(String key)
	{
		String temp;
		ArrayList<String> results = new ArrayList<String>();
		for(int i=0; i < keys.size(); i++)
		{
			temp = (String) keys.elementAt(i);
			if (temp.contains(key)) 
				results.add((String) values.elementAt(i));
		}
		if(results.size() == 0)
			return null;
		return results;
	}
}

/*
  Class MsgEncoder:
      Serialize the KeyValue List and Send it out to a Stream.
*/
class MsgEncoder
{
	private PrintStream printOut;
	/*If you would like to write msg interpreter your self, read below*/
	/* Default of delimiter in system is $$$ */
	private final String delimiter = "$$$";
   
	public MsgEncoder(){}
   
	/* Encode the Key Value List into a string and Send it out */
   
	public void sendMsg(KeyValueList kvList, OutputStream out) throws IOException
	{
		PrintStream printOut= new PrintStream(out);
		if (kvList == null) 
			return;
		String outMsg= new String();
		for(int i=0; i<kvList.size(); i++)
		{
     		if (outMsg.equals(""))
     			outMsg = kvList.keyAt(i) + delimiter + kvList.valueAt(i);
     		else
     			outMsg += delimiter + kvList.keyAt(i) + delimiter + kvList.valueAt(i);
		}
		//System.out.println(outMsg);
		printOut.println(outMsg);
	}
}

/*
  Class MsgDecoder:
     Get String from input Stream and reconstruct it to 
     a Key Value List.
*/

class MsgDecoder 
{
	private BufferedReader bufferIn;
	private final String delimiter = "$$$";
   
	public MsgDecoder(InputStream in)
	{
		bufferIn  = new BufferedReader(new InputStreamReader(in));	
	}
   
	/*
     get String and output KeyValueList
	*/
   
	public KeyValueList getMsg() throws IOException
	{
		String strMsg= bufferIn.readLine();
       
		if (strMsg==null) 
			return null;
       
		KeyValueList kvList = new KeyValueList();	
		StringTokenizer st = new StringTokenizer(strMsg, delimiter);
		while (st.hasMoreTokens()) 
		{
			kvList.addPair(st.nextToken(), st.nextToken());
		}
		return kvList;
	}
   
}

/*The class where msgs are processed*/
interface ComponentBase
{
    public void processMsg(KeyValueList kvList) throws Exception;
}

/*
    Class InterfaceServer    
        Set up a socket server waiting for the remote to connect.
*/

/*Map between comp and socket*/
class CompSocketMap
{
	String compname;
	Socket compsocket;
	Boolean active;
	
	CompSocketMap(String instr, Socket insocket)
	{
		compname = instr;
		compsocket = insocket;
		active = false;
	}
}

/*The comp access control of msgs*/
class CompMsgControl
{
	String compname;
	ArrayList<Integer> InMsgs= new ArrayList<Integer>();
	ArrayList<Integer> OutMsgs= new ArrayList<Integer>();
}

public class SISServer
{	
	private static final int port = 7999;
	private static Socket client;
	private static Socket component;
	static Vector<CompSocketMap> csm = new Vector<CompSocketMap>() ;
	static Vector<CompMsgControl> cmc = new Vector<CompMsgControl>();
	private static SISComponentBase siscompbase;
	static Vector<KeyValueList> createhistory = new Vector<KeyValueList>();
	
	/*Find socket by comp name*/
	public static ArrayList<Socket> FindSocketCSM(String in)
	{
		ArrayList<Socket> results = new ArrayList<Socket>();
		for(int i=0; i<csm.size(); i++)
			if(csm.get(i).compname.equals(in))
				results.add(csm.get(i).compsocket);
		if(results.size() == 0)
			return null;
		return results;
	}
	
	/*Find socket by comp name*/
	public static String FindCompCSM(Socket in)
	{
		for(int i=0; i<csm.size(); i++)
			if(csm.get(i).compsocket.equals(in))
				return csm.get(i).compname;
		return null;
	}
	
	/*Turn on comps by comp name*/
	public static void TurnOn(String in)
	{
		PrintStream printOut;
		for(int i=0; i<csm.size(); i++)
			if(csm.get(i).compname.equals(in) && csm.get(i).active == false)
			{
				try {
					printOut= new PrintStream(csm.get(i).compsocket.getOutputStream());
					printOut.println("MsgID$$$24");
					csm.get(i).active = true;
				} catch (IOException e) {
					System.out.println("Component "+csm.get(i).compname+" connection error!");
				}
			}
	}
	
	/*Turn off comps by comp name*/
	public static void TurnOff(String in)
	{
		PrintStream printOut;
		for(int i=0; i<csm.size(); i++)
			if(csm.get(i).compname.equals(in) && csm.get(i).active == true)
			{
				try {
					printOut= new PrintStream(csm.get(i).compsocket.getOutputStream());
					printOut.println("MsgID$$$25");
					csm.get(i).active = false;
				} catch (IOException e) {
					System.out.println("Component "+csm.get(i).compname+" connection error!");
				}
			}
	}
	
	public static boolean CompExistCMC(String name)
	{
		for(int i=0; i<cmc.size(); i++)
			if(cmc.get(i).compname.equals(name))
				return true;
		return false;
	}
	
	public static void main(String[] args) throws Exception
	{
		final ServerSocket server = new ServerSocket(port);
		
		Thread t = new Thread( new Runnable()
			{
				public void run()
				{
					try
					{
						client = server.accept();
						new ClientSession();

						while(true)
						{
							component = server.accept();
							new ComponentSession(component);
						}
					}
					catch ( IOException ioe )
					{ System.out.println("Server Exception!"); }
				}
			});
		t.setDaemon( true );
		t.start();
		
		System.out.println( "Hit Enter to stop.\n" );
		try { System.in.read(); } catch( Throwable thr ) {};
	}
	
	/*Superuser version of component session, no checks needed for its sent msgs*/
	private static class ClientSession implements Runnable
	{
		private Thread t;
		public ClientSession()
		{
			siscompbase = new SISComponentBase(client);

			t = new Thread( this );
			t.setDaemon( true );
			t.start();
		}

		public void run()
		{
			try
			{
				MsgDecoder mDecoder= new MsgDecoder(client.getInputStream());
				KeyValueList kvInput;
				do
				{
					kvInput = mDecoder.getMsg();
					if(Integer.parseInt(kvInput.getValue("MsgID")) != 23)
						siscompbase.processMsg(kvInput);
				}
				while (kvInput != null);
			}
			catch (SocketException se)
			{
				 System.out.println("Connection was Closed by Client");
			}	
			catch ( IOException ioe )
			{
				System.out.println("SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
			}
			catch (Exception ex)
			{
				System.out.println("Exception");
			}
		}
	}

	private static class ComponentSession implements Runnable
	{
		private Socket compsocket;
		private int msgID;
		private boolean First23 = true;
		private boolean validcompname = false;
		private Thread t;
		private String compname = null;
		PrintStream printOut;
		
		public ComponentSession(Socket in)
		{
			t = new Thread( this );
			t.setDaemon( true );
			t.start();
			compsocket = in;
		}

		public void run()
		{
			try
			{
				MsgDecoder mDecoder= new MsgDecoder(compsocket.getInputStream());
				MsgEncoder mEncoder= new MsgEncoder();
				KeyValueList kvInput;
				do
				{
					kvInput = mDecoder.getMsg();
					if (kvInput != null) 
					{
						if(ValidateSender(kvInput))//validate sender
						{
							msgID = Integer.parseInt(kvInput.getValue("MsgID"));
							if(msgID == 23)
							{
								if(First23)
								{
									First23 = false;
									for(int i=0; i<SISServer.cmc.size(); i++)
										if(SISServer.cmc.get(i).compname.equals(kvInput.getValue("Name")))
											validcompname = true;
									if(validcompname)
									{
										compname = kvInput.getValue("Name");
										SISServer.csm.add(new CompSocketMap(compname, compsocket));
										try {
											printOut= new PrintStream(compsocket.getOutputStream());
											printOut.println("MsgID$$$26");
											SISServer.csm.get(SISServer.csm.size()-1).active = true;
										} catch (IOException e) {
											System.out.println("Component "+compname+" connection error!");
										}
									}
									else
									{
										//TODO: Error Msg to Component
										compsocket.close();
										t.join();
									}
								}
								mEncoder.sendMsg(kvInput, client.getOutputStream());
							}
							else
							{
								siscompbase.processMsg(kvInput);
								mEncoder.sendMsg(kvInput, client.getOutputStream());
							}
						}
					}
				}
				while (kvInput != null);
			}
			catch (SocketException se)
			{
				 System.out.println("Connection was Closed by Client");
			}	
			catch ( IOException ioe )
			{
				System.out.println("SERVER INTERNAL ERROR: IOException: " + ioe.getMessage());
			}
			catch (Exception ex)
			{
				System.out.println("Exception");
			}
		}
		
		//validate sender to see if they sent msgs within their privilege
		boolean ValidateSender(KeyValueList kvlist)
		{
			int msgID = Integer.parseInt(kvlist.getValue("MsgID"));
			if(msgID == 23)
				return true;
			if(compname == null)
				return false;
			int index = -1;
			for(int i=0; i<SISServer.cmc.size(); i++)
				if(SISServer.cmc.get(i).compname.equals(compname))
				{
					index = i;
					break;
				}
			if(index == -1)
				return false;
			if(SISServer.cmc.get(index).OutMsgs.contains(msgID))
				return true;
			return false;
		}
	}
}
