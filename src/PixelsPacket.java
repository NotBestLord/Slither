public class PixelsPacket {
	private byte[] pixels;

	public PixelsPacket() {}

	public PixelsPacket(byte[] pixels) {
		this.pixels = pixels;
	}

	public byte[] getPixels() {
		return pixels;
	}
}
