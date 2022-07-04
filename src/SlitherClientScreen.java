import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class SlitherClientScreen extends JPanel implements KeyListener {

	private int width = 1, height = 1;
	public byte[] pixels = new byte[(width*height)+1];
	private final SlitherClient client;
	private int horizontalRatio, verticalRatio;
	private final JFrame frame;

	public SlitherClientScreen(SlitherClient client){
		this.client = client;
		frame = new JFrame("Slither.py");
		frame.setSize(720,740);
		frame.add(this);
		frame.addKeyListener(this);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowStateChanged(WindowEvent e) {
				horizontalRatio = frame.getWidth() / width;
				verticalRatio = frame.getHeight() / height;
			}
		});
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}

	public void setWH(int width, int height){
		this.width = width;
		this.height = height;
		horizontalRatio = frame.getWidth() / width;
		verticalRatio = frame.getHeight() / height;
	}

	@Override
	public void paint(Graphics g) {
		for(int i=0; i<pixels.length-1;i++){
			if (pixels[i] == Consts.HeadColorRGB) {
				g.setColor(Consts.HeadColor);
			}
			else if (pixels[i] == Consts.BodyColorRGB) {
				g.setColor(Consts.BodyColor);
			}
			else if (pixels[i] == Consts.FruitColorRGB) {
				g.setColor(Consts.FruitColor);
			}
			else{
				g.setColor(Consts.BlankColor);
			}
			g.fillRect((i%width)*horizontalRatio, 20+(i/height)*verticalRatio, horizontalRatio, verticalRatio);
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(e.getKeyChar() == KeyEvent.VK_ESCAPE){
			client.isRunning = false;
		}
		switch (e.getKeyChar()){
			case 'w' -> client.sendPacket(new InputPacket('w'));
			case 'a' -> client.sendPacket(new InputPacket('a'));
			case 's' -> client.sendPacket(new InputPacket('s'));
			case 'd' -> client.sendPacket(new InputPacket('d'));
			case KeyEvent.VK_ESCAPE -> client.isRunning = false;
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {

	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
