import org.joml.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private final List<Vector2i> cells = new ArrayList<>();
	private final Vector2i velocity = new Vector2i();

	public Player(Vector2i position){
		cells.add(new Vector2i(position));
	}

	public void handleInput(char c){
		velocity.set(0,0);
		switch (c) {
			case 'w' -> velocity.y = -1;
			case 's' -> velocity.y = 1;
			case 'a' -> velocity.x = -1;
			case 'd' -> velocity.x = 1;

		}
	}

	public boolean tickUpdate(byte[] pixels){
		Vector2i head = cells.get(cells.size()-1);

		int i = (cells.get(0).y * SlitherServer.height) + cells.get(0).x;

		if(cells.size() > 1){
			pixels[i] = Consts.BodyColorRGB;
		}
		else {
			pixels[i] = Consts.BlankColorRGB;
		}

		i = (head.y * SlitherServer.height) + head.x;
		pixels[i] = Consts.BlankColorRGB;

		head.set(cells.get(0)).add(velocity);

		if(head.x >= SlitherServer.width){
			head.x = 0;
		}
		if(head.x < 0){
			head.x = SlitherServer.width - 1;
		}

		if(head.y >= SlitherServer.height){
			head.y = 0;
		}
		if(head.y < 0){
			head.y = SlitherServer.height - 1;
		}
		cells.remove(cells.size()-1);
		cells.add(0,head);
		i = (cells.get(0).y * SlitherServer.height) + cells.get(0).x;
		if(pixels[i] == Consts.BodyColorRGB || pixels[i] == Consts.HeadColorRGB){
			clear(pixels);
			return false;
		}
		else if(pixels[i] == Consts.FruitColorRGB){
			cells.add(new Vector2i(cells.get(cells.size()-1)));
			pixels[pixels.length-1]--;
		}
		pixels[i] = Consts.HeadColorRGB;
		return true;
	}

	public void clear(byte[] pixels){
		for(Vector2i cell : cells){
			pixels[cell.x+(cell.y * SlitherServer.height)] = Consts.BlankColorRGB;
		}
	}
}
