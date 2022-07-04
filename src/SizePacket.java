public class SizePacket {
	private int width, height;

	public SizePacket() {
	}

	public SizePacket(int width, int height) {
		this.width = width;
		this.height = height;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
