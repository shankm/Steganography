import java.util.ArrayList;


public class Mp3Data {
    ArrayList<byte[]> dataFrames;
    ArrayList<Byte> id3v2;
	byte[] id3Version;
	
	public Mp3Data() {
		dataFrames = null;
		id3v2 = null;
		id3Version = null;
	}
}
