
import java.io.*;
import java.net.*;
import java.util.*;

public class SISComponentBase implements ComponentBase
{
	private Socket client;
    
    SISComponentBase(Socket cl)
	{
		client = cl;
    }
    
    synchronized public void processMsg(KeyValueList kvList) throws Exception
	{
    	int MsgID = Integer.parseInt(kvList.getValue("MsgID"));
    	ArrayList<Socket> sendlist;
    	ArrayList<String> inmsgs, outmsgs;
    	CompMsgControl tempcmc;
    	String tempstr;

		switch(MsgID)
		{
		case 20://Add Comp Msg Control List, Unrecognized component cannot be connected
			tempstr = kvList.getValue("Name");
			if(!SISServer.CompExistCMC(tempstr))
			{
				tempcmc = new CompMsgControl();
				tempcmc.compname = tempstr;
				inmsgs = kvList.getValueLike("InputMsgID");
				outmsgs = kvList.getValueLike("OutputMsgID");
				if(inmsgs!=null)
					for(int i=0; i<inmsgs.size(); i++)
						tempcmc.InMsgs.add(Integer.parseInt(inmsgs.get(i)));
				if(outmsgs!=null)
					for(int i=0; i<outmsgs.size(); i++)
						tempcmc.OutMsgs.add(Integer.parseInt(outmsgs.get(i)));
				SISServer.cmc.add(tempcmc);
				SISServer.createhistory.add(kvList);
			}
			else
				System.out.println("Definition of component "+tempstr+" already exists.");
			break;
		case 21://Add Comp Msg Control List, Unrecognized component cannot be connected
			tempstr = kvList.getValue("Name");
			if(!SISServer.CompExistCMC(tempstr))
			{
				tempcmc = new CompMsgControl();
				tempcmc.compname = tempstr;
				inmsgs = kvList.getValueLike("InputMsgID");
				outmsgs = kvList.getValueLike("OutputMsgID");
				if(inmsgs!=null)
					for(int i=0; i<inmsgs.size(); i++)
						tempcmc.InMsgs.add(Integer.parseInt(inmsgs.get(i)));
				if(outmsgs!=null)
					for(int i=0; i<outmsgs.size(); i++)
						tempcmc.OutMsgs.add(Integer.parseInt(outmsgs.get(i)));
				SISServer.cmc.add(tempcmc);
				SISServer.createhistory.add(kvList);
			}
			else
				System.out.println("Definition of component "+tempstr+" already exists.");
			break;
		case 22:
			SISServer.TurnOff(kvList.getValue("Name"));
			break;
		case 24:
			System.out.println("Unexpected MsgID 24");
			break;
		case 25:
			System.out.println("Unexpected MsgID 25");
			break;
		case 26:
			System.out.println("Unexpected MsgID 26");
			break;
		default:
			for(int i=0; i<SISServer.cmc.size(); i++)
			{
				if(SISServer.cmc.get(i).InMsgs.contains(MsgID))
				{
					//Turn on components if necessary
					SISServer.TurnOn(SISServer.cmc.get(i).compname);
					sendlist = SISServer.FindSocketCSM(SISServer.cmc.get(i).compname);
					if(sendlist != null)
						for(int j=0; j<sendlist.size(); j++)
							SendToComponent(kvList, sendlist.get(j));
				}
			}
			break;
		}
    }

	public void SendToComponent(KeyValueList kvList, Socket component)
	{
		try
		{
			MsgEncoder mEncoder = new MsgEncoder();
			if(component != null)
				mEncoder.sendMsg(kvList, component.getOutputStream());
		}
		catch(Exception ex)
		{ System.out.println("Problem sending to component!");}
	}
}

