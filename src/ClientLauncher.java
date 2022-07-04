public class ClientLauncher {

	public static void main(String[] args) {
		try{
			new SlitherClient().run();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
