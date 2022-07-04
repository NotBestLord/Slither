import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SlitherServer extends Listener {
	private final Server serverHandler;
	private static final int tcpPort = 25585, udpPort = 25586;
	public static final int width = 90, height = 90, maxFruits = (width + height) / 4;
	private volatile boolean isRunning = true;
	private final List<Player> players = new ArrayList<>();
	private final HashMap<Integer, Player> playersByClientID = new HashMap<>();
	private final byte[] pixels;

	public SlitherServer() throws Exception {
		pixels = new byte[width*height+1];
		serverHandler = new Server(50124, 8192);
		//

		//
		Kryo serializer = serverHandler.getKryo();
		serializer.setRegistrationRequired(false);

		serverHandler.bind(tcpPort,udpPort);

		serverHandler.start();

		serverHandler.addListener(this);

		for(int i=0; i<width*height;i++){
			pixels[i] = Consts.BlankColorRGB;
		}
		pixels[pixels.length-1] = 0;
	}

	public void run() {
		long lastTime = System.nanoTime();
		double unprocessedTime = 0;
		int tps = 3;

		while (isRunning) {
			long startTime = System.nanoTime();
			long passedTime = startTime - lastTime;
			lastTime = startTime;

			unprocessedTime += passedTime / (double) Consts.NANOSECOND;
			if (unprocessedTime > 1f / tps) {
				tickUpdate();
			}
			while (unprocessedTime > 1f / tps) {
				unprocessedTime -= 1f / tps;
			}
		}
	}
	public void tickUpdate() {
		players.removeIf(player -> {
			if (!player.tickUpdate(pixels)){
				removePlayer(player);
				return true;
			}
			return false;
		});
		for(Connection connection : serverHandler.getConnections()){
			connection.sendTCP(new PixelsPacket(pixels));
		}
		for(int i=pixels[pixels.length-1]; i<maxFruits;i++){
			pixels[getBlank(pixels, 64)] = Consts.FruitColorRGB;
			pixels[pixels.length-1]++;
		}
	}

	public static int getBlank(byte[] pixels, int max){
		int out = 0,j;
		Random random = new Random();
		for(int i=0; i < max && out == 0;i++){
			j = random.nextInt(pixels.length-1);
			if(pixels[j] == Consts.BlankColorRGB){
				out = j;
			}
		}
		return out;
	}

	@Override
	public void connected(Connection connection) {
		int i = getBlank(pixels, 64);
		Player player = new Player(new Vector2i(i%width, i/height));
		players.add(player);
		playersByClientID.put(connection.getID(), player);
		connection.sendTCP(new SizePacket(width, height));
	}

	@Override
	public void disconnected(Connection connection) {
		if(playersByClientID.containsKey(connection.getID())) {
			players.get(connection.getID()).clear(pixels);
			players.remove(playersByClientID.get(connection.getID()));
			playersByClientID.remove(connection.getID());
		}
	}

	@Override
	public void received(Connection connection, Object o) {
		if(o instanceof InputPacket packet){
			playersByClientID.get(connection.getID()).handleInput(packet.getInput());
		}
	}

	public void removePlayer(Player player){
		List<Integer> ids = new ArrayList<>(playersByClientID.keySet());
		for(int id : ids){
			if(playersByClientID.get(id) == player){
				playersByClientID.remove(id);
				return;
			}
		}
	}
}
