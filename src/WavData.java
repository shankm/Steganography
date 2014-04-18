
public class WavData {
	private byte[] header;
	private short[] left;
	private short[] right;
	byte[] soundData;
	
	public WavData() {

	}
	
	public byte[] getHeader() {
		return header;
	}
	
	public short[] getLeft() {
		return left;
	}
	
	public short[] getRight() {
		return right;
	}
	
	public byte[] getSoundData() {
		return soundData;
	}
	
	public void setHeader(byte[] x) {
		header = x;
	}
	
	public void setLeft(short[] x) {
		left = x;
	}
	
	public void setRight(short[] x) {
		right = x;
	}
	
	public void setSoundData(byte[] x) {
		soundData = x;
	}
}
