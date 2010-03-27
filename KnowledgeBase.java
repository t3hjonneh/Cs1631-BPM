public class KnowledgeBase
{
	//Borderline Isolated Systolic Hypertension
	static int BISH = 140;
	//Isolated Systolic Hypertension
	static int ISH = 160;
	//Severe Isolated Systolic Hypertension
	static int SISH = 200;
	//Diastolic High Normal
	static int DHN = 85;
	//Diastolic Mild Hypertension
	static int DMH = 90;
	//Diastolic Moderate Hypertension
	static int DModH = 105;
	//Diastolic Severe Hypertension
	static int DSH = 115;

	//Low Systolic
	static int LS = 90;
	//Low Low Systolic
	static int LLS = 60;
	//High Low Systolic
	static int HLS = 50;
	//Low Diastolic
	static int LD = 60;
	//Low Low Diastolic
	static int LLD = 40;
	//High Low Diastolic
	static int HLD = 33;


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
	
	public KnowledgeBase()
	{
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
			boolean running = true;
			try
			{
				server = new Socket(ip, port);
				
				BufferedOutputStream bos = new BufferedOutputStream(server.getOutputStream());
				BufferedInputStream bis = new BufferedInputStream(server.getInputStream());
				
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
						if(msgid == 25)
						{
							running = false;
						}
						else
						{
							String out = Parser.reparse(Parser.reformat(parsed, Parser.parseMessage(Parser.readMessge(msgid))), "$$$");
							// write out to server
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