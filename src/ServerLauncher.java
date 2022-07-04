public class ServerLauncher {

	public static void main(String[] args) {
		try{
			new SlitherServer().run();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
