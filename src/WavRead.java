import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class WavRead {
	
    private FileInputStream stream;
    File file;
    int length;

	byte[] header = new byte[44]; //Array for holding the header
	byte[] soundData;
	
	int[] temp = new int[2]; //Buffer for reading in WAV data
	
    public WavRead(File audioFile) throws IOException {
        file = audioFile;
    	stream = new FileInputStream(file);
    	
    	stream.read(header);
    	
    	// Get the length (in bytes) of the audio data
    	for(int i = 3, j = 43; i >= 0; --i, --j)
    		length += header[j] << 8*i;
    
    	soundData = new byte[length];
    }
	
	public WavData readWav() throws IOException {	
		WavData data = new WavData();
		
		stream.read(soundData);
		
		data.setHeader(header);
		data.setSoundData(soundData);
		
		return data;
	}
	
	public byte[] getHeader() {
		return header;
	}
}
