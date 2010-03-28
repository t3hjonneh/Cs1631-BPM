import java.util.*;
import java.io.*;
import java.net.*;

public class KnowledgeBase
{
	//Borderline Isolated Systolic Hypertension
	int BISH = 140;
	//Isolated Systolic Hypertension
	int ISH = 160;
	//Severe Isolated Systolic Hypertension
	int SISH = 200;
	//Diastolic High Normal
	int DHN = 85;
	//Diastolic Mild Hypertension
	int DMH = 90;
	//Diastolic Moderate Hypertension
	int DModH = 105;
	//Diastolic Severe Hypertension
	int DSH = 115;

	//Low Systolic
	int LS = 90;
	//Low Low Systolic
	int LLS = 60;
	//High Low Systolic
	int HLS = 50;
	//Low Diastolic
	int LD = 60;
	//Low Low Diastolic
	int LLD = 40;
	//High Low Diastolic
	int HLD = 33;

	public String BloodPressureDiagnosis(int Systolic, int Diastolic)
	{
		int conditionD = 0;
		if(Diastolic >= DSH)
		{
			conditionD = 4;
		}
		else if(Diastolic >= DModH)
		{
			conditionD = 3;
		}
		else if(Diastolic >= DMH)
		{
			conditionD = 2;
		}
		else if(Diastolic >= DHN)
		{
			conditionD = 1;
		}

		int conditionS = 0;
		if(Systolic >= SISH)
		{
			conditionS = 4;
		}
		else if(Systolic >= ISH)
		{
			conditionS = 3;
		}
		else if(Systolic >= BISH)
		{
			conditionS = 2;
		}
		
		String ret = "Normal Blood Pressure : Recheck In 2 Years.";
		if(conditionD != 0 || conditionS != 0)
		{
			if(conditionD < 2 && conditionS > 1)
			{
				if(conditionS == 4)
				{
					ret = "Isolated Systolic Hypertension : Medicated Therapy.";
				}
				else if(conditionS == 3)
				{
					ret = "Isolated Systolic Hypertension : Confirm Within 2 Months. Therapy If Confirmed.";
				}
				else
				{
					ret = "Borderline Isolated Systolic Hypertension : Confirm Within 2 Months.";
				}
			}
			else
			{
				if(conditionS == 4)
				{
					ret = "Severe Hypertension : Medicated Therapy.";
				}
				else if(conditionS == 3)
				{
					ret = "Moderate Hypertension : Therapy.";
				}
				else if(conditionS == 2)
				{
					ret = "Mild Hypertension : Confirm within 2 months.";
				}
				else
				{
					ret = "High Normal : Recheck Within 1 Year.";
				}
				
			}
		}
		else if(Diastolic <= HLD || Systolic <= HLS)
		{
			ret = "High Hypotension : Medicated Therapy.";
		}
		else if(Diastolic <= LLD || Systolic <= LLS)
		{
			ret = "Low Hypotension : Therapy.";
		}
		else if(Diastolic <= LD || Systolic <= LS)
		{
			ret = "Hypotension : Confirm Within 2 Months.";
		}
		
		return ret;
	}
	
	private String getConfirm(int msgID)
	{
		return "MsgID$$$26$$$Description$$$Acknowledgement$$$AckMsgID$$$" + msgID + "$$$Yes$$$Name$$$BloodPressureMonitorKnowledgeBase";
	}
	
	private String output(String diagnosis, int systolic, int diastolic)
	{
		String[] tempdiag = diagnosis.split(" : ");
		return "MsgID$$$132$$$Description$$$Blood Pressure Alert with Diagnosis$$$Systolic$$$Systolic$$$" + systolic + "$$$Diastolic$$$" + diastolic
			+ "$$$Pulse$$$5$$$Alert Type$$$Blood Pressure Alert$$$Diagnosis$$$" + tempdiag[0] + "$$$Recommended Course of Action$$$" + tempdiag[1]
			+ "$$$DateTime$$$" + Date().toString();
	}
	
	private int[] getStolic(String[][] parsed)
	{
		int[] ret = new int[2];
		int i = 0;
		while((!parsed[0][i].equals("Systolic")) && (!parsed[0][i].equals("Diastolic")))
			i++;
		
		if(parsed[0][i].equals("Systolic"))
			ret[0] = Integer.parseInt(parsed[1][i]);
		
		if(parsed[0][i].equals("Diastolic"))
			ret[1] = Integer.parseInt(parsed[1][i]);
		
		i++;
		
		while((!parsed[0][i].equals("Systolic")) && (!parsed[0][i].equals("Diastolic")))
			i++;
		
		if(parsed[0][i].equals("Systolic"))
			ret[0] = Integer.parseInt(parsed[1][i]);
		
		if(parsed[0][i].equals("Diastolic"))
			ret[1] = Integer.parseInt(parsed[1][i]);
		
		return ret;
	}
	
	public KnowledgeBase()
	{
		Scanner S = new Scanner(new FileInputStream("knowledgebase.txt"));
		while(S.hasNextLine())
		{
			String temp = S.nextLine();
			if(temp.indexOf("//") == -1)
			{
				String[] tempArr = temp.split(":");
				if(tempArr[0] == "BISH")
					BISH = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "ISH")
					ISH = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "SISH")
					SISH = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "DHN")
					DHN = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "DMH")
					DMH = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "DModH")
					DModH = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "DSH")
					DSH = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "LS")
					LS = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "LLS")
					LLS = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "HLS")
					HLS = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "LD")
					LD = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "LLD")
					LLD = Integer.parseInt(tempArr[2]);
				if(tempArr[0] == "HLD")
					HLD = Integer.parseInt(tempArr[2]);
			}
		}
		commThread T = new commThread();
		T.setDaemon(true);
		T.start();
		
		while(true);
	}
	
	public static void main(String [] args)
	{
		new KnowledgeBase();
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
		
		public void run()
		{
			try
			{
				server = new Socket(ip, port);
				
				BufferedOutputStream bos = new BufferedOutputStream(server.getOutputStream());
				BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
				boolean running = true;
				boolean doSomething = false;
				while(running)
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
						String out = null;
						// String out = Parser.reparse(Parser.reformat(parsed, Parser.parseMessage(Parser.readMessge(msgid))), "$$$");
						// write out to server
						switch(msgid)
						{
							case 24:
								doSomething = true;
								out = getConfirm(msgid);
								break;
							case 25:
								doSomething = false;
								out = getConfirm(msgid);
								byte[] b2 = out.getBytes();
								bos.write(b2, 0, b2.length);
								break;
							case 31:
								int[] stolic = getStolic(parsed);
								out = output(bloodPressureDiagnosis(stolic[0], stolic[1]), stolic[0], stolic[1]);
								break;
							case 130:
								int[] stolic = getStolic(parsed);
								out = output(bloodPressureDiagnosis(stolic[0], stolic[1]), stolic[0], stolic[1]);
								break;
						}
						
						if((doSomething) && (out != null))
						{
							byte[] b2 = out.getBytes();
							bos.write(b2, 0, b2.length);
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