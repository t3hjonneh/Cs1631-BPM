public class NetworkManager
{
	ServerSocket serverSocket;
	ExecutorService threadPool;
	boolean isRunning = true;
	
	public NetworkManager()
	{
		Socket socket = null;
		threadPool = Executors.newCachedThreadPool();
		
		// initialize Parser, KnowledgeBase and ProfileManager
		// TODO: create Parser
		// TODO: create KnowledgeBase
		// TODO: create ProfileManager
		
		
		try
		{
			serverSocket = new ServerSocket(4000); // 4000 = port number
			System.out.println("Listening on socket: 4000");
			
			while (isRunning)
			{
				// Accept connection:
				socket = serverSocket.accept();
				// TODO: create Connection Manager
				threadPool.submit(new ConnectionManager(socket));
			}
		}
		catch(Exception e)
		{
			System.err.println(e.stackTrace());
		}
	}
	
	public static void main(String [] args)
	{
		new NetworkManager();
	}
}
