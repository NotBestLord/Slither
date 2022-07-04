import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;

public class SlitherClient extends Listener {

	private final Client clientHandler;

	private static final int tcpPort = 25585, udpPort = 25586;
	private static final String serverIp = "localhost";
	public volatile boolean isRunning = true;

	private final SlitherClientScreen screen = new SlitherClientScreen(this);

	public SlitherClient() throws IOException {
		clientHandler = new Client(8192, 50124);

		clientHandler.getKryo().setRegistrationRequired(false);

		clientHandler.addListener(this);
		//
		clientHandler.start();

		clientHandler.connect(5000, serverIp,tcpPort,udpPort);
	}

	public void run(){
		long lastTime = System.nanoTime();
		double unprocessedTime = 0;
		int tps = 1000;

		while (isRunning) {
			long startTime = System.nanoTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime / (double) Consts.NANOSECOND;
			if (unprocessedTime > 1f / tps) {
				screen.repaint();
			}
			while (unprocessedTime > 1f / tps) {
				unprocessedTime -= 1f / tps;
			}
		}
	}

	public void sendPacket(Object o){
		clientHandler.sendTCP(o);
	}

	@Override
	public void connected(Connection connection) {
		super.connected(connection);
	}

	@Override
	public void received(Connection connection, Object o) {
		if(o instanceof SizePacket packet){
			screen.setWH(packet.getWidth(), packet.getHeight());
		}
		else if(o instanceof PixelsPacket packet){
			screen.pixels = packet.getPixels();
		}
	}
}
