import java.io.*;
import java.net.*;
import java.util.*;

public class  BloodPressureMonitor
{ 
	public static void main(String []args) throws Exception
	{
		if (args.length < 1)
        {
            System.out.println("No Arguments were supplied.");
            System.out.println("Usage: java BloodPressureMonitor server_ip");
            System.exit(0);
        }

		Socket universal = new Socket(args[0], 7999);

		MsgEncoder mEncoder = new MsgEncoder();
		final MsgDecoder mDecoder = new MsgDecoder(universal.getInputStream());
		
		/* receiving thread*/
		Thread t = new Thread (new Runnable()
			{
				public void run()
				{
					KeyValueList kvInput;
					try
					{
						while(true)
						{	
							kvInput = mDecoder.getMsg();
							System.out.println("BLOOD PRESSURE!");
							System.out.println(kvInput);
						}
					}
					catch(Exception ex)
					{ System.out.println("Exception while sending to universal interface..");}
				}
			});
		t.setDaemon(true);
		t.start();

		try
		{
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			while (true)
			{
				String input = br.readLine();
				KeyValueList kvList = new KeyValueList();	
				StringTokenizer st = new StringTokenizer(input, "$$$");
				while (st.hasMoreTokens()) 
				{
					kvList.addPair(st.nextToken(), st.nextToken());
				}

				mEncoder.sendMsg(kvList, universal.getOutputStream());
			}
		}
		catch(Exception ex)
		{ System.out.println("Exception reading from standard input..");}
	}
}
