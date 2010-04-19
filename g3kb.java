import java.io.*;
import java.net.*;
import java.util.*;

public class g3kb
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
	static boolean debug = false;
	static boolean running = true;

	public static void main(String []args) throws Exception
	{
		if(args.length > 0)
			for(String var : args) // for each loop? well it certainly does allow for some rather strange stuff
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

		f = new File("g3kb.txt");
		if(!f.exists())
		{
			System.out.println("Knowledgebase not found.");
			System.out.println("Will create Knowledgebase.");
			createKB(f);
		}

		new g3kb();
	}
	
	public g3kb()
	{ // method houses the primary driver for the rest of the program
		String ip = "127.0.0.1";
		int port = 7999;
		Socket server;
		PrintWriter out;
		BufferedReader in;
		String message;

		Scanner S;
		try
		{ // open up the knowledge base text and use it to update the conditions in the knowledgebase
			S = new Scanner(new FileInputStream("g3kb.txt"));
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
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		try
		{ // connect to the server
			server = new Socket(ip, port);
			
			out = new PrintWriter(server.getOutputStream());
			in = new BufferedReader(new InputStreamReader(server.getInputStream()));
		
			if(debug)
				System.out.println("Initializing...");
			// get message 23 and send it to the server
			String[][] outMessage = Parser.parseMessage(Parser.readMessage(23));
			Parser.setVal(outMessage, "Name", "g3kb");
			out.println(Parser.reparse(outMessage,"$$$"));
			out.flush();
			
			while(running)
			{ // read in messages from the server
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
	
	// gets
	private String getMessage(int msgID, String[][] message)
	{
		String[][] outMessage = new String[0][0];
		int systolic = 0;
		int diastolic = 0;
		
		if(msgID == 45)
		{ // get the patient information
			outMessage = Parser.parseMessage(Parser.readMessage(26));
		}
		else if(msgID == 130)
		{ // get the systolic and diastolic measurements
			outMessage = Parser.parseMessage(Parser.readMessage(131));
			systolic = Integer.parseInt(Parser.getVal(message, "Systolic"));
			diastolic = Integer.parseInt(Parser.getVal(message, "Diastolic"));
		}
		else
		{ // acknowledge whatever was sent to us
			outMessage = Parser.parseMessage(Parser.readMessage(26));
			Parser.setVal(outMessage, "AckMsgID", Integer.toString(msgID));
		}
		
		// set value of the message to whatever was sent to us
		for(int i = 0; i < message[0].length; i++)
			if(!message[0][i].equals("MsgID") && !message[0][i].equals("Description"))
				Parser.setVal(outMessage, message[0][i], message[1][i]);
		
		if(systolic > 0)
		{ // if there is a pulse, get reading and parse the output
			String[] diagnosis = bloodPressureDiagnosis(systolic, diastolic).split(" : ");
			Parser.setVal(outMessage, "Diagnosis",  diagnosis[0]);
			Parser.setVal(outMessage, "Suggestions",  diagnosis[1]);
		}
		// format the message to output
		String out = Parser.reparse(outMessage, "$$$");
		if(debug)
			System.out.println("Message Sent:\n"+out);
			
		return out;
	}
	
	public String bloodPressureDiagnosis(int Systolic, int Diastolic)
	{ // makes a diagnosis and returns it
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
					ret = "Severe Isolated Systolic Hypertension : Medicated Therapy.";
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

	private void createKB(File f)
	{
		PrintWriter pw = new PrintWriter(new FileOutputStream(f));
		String[] tmp = new String[27];
		tmp[0] = "//Borderline Isolated Systolic Hypertension";
		tmp[1] = "BISH:140";
		tmp[2] = "//Isolated Systolic Hypertension";
		tmp[3] = "ISH:160";
		tmp[4] = "//Severe Isolated Systolic Hypertension";
		tmp[5] = "SISH:200";
		tmp[6] = "//Diastolic High Normal";
		tmp[7] = "DHN:85";
		tmp[8] = "//Diastolic Mild Hypertension";
		tmp[9] = "DMH:90";
		tmp[10] = "//Diastolic Moderate Hypertension";
		tmp[11] = "DModH:105";
		tmp[12] = "//Diastolic Severe Hypertension";
		tmp[13] = "DSH:115";
		tmp[14] = "";
		tmp[15] = "//Low Systolic";
		tmp[16] = "LS:90";
		tmp[17] = "//Low Low Systolic";
		tmp[18] = "LLS:60";
		tmp[19] = "//High Low Systolic";
		tmp[20] = "HLS:50";
		tmp[21] = "//Low Diastolic";
		tmp[22] = "LD:60";
		tmp[23] = "//Low Low Diastolic";
		tmp[24] = "LLD:40";
		tmp[25] = "//High Low Diastolic";
		tmp[26] = "HLD:33";

		for(int i = 0; i < 27; i++)
			pw.print(tmp[i]);

		pw.close();
	}
}

	
