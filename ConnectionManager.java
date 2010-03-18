public class ConnectionManager implements Runnable
{
	Socket socket;
	
	public ConnectionManager(Socket s)
	{
		socket = s;
	}
	
	public void run()
	{
		ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
		ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
	}
}
