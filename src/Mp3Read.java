import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

/* 
http://www.javazoom.net/mp3spi/documents.html

http://www.multiweb.cz/twoinches/mp3inside.htm
  
Frame header has this structure (each letter is one bit):

AAAAAAAA   AAABBCCD   EEEEFFGH   IIJJKLMM 

A	  	Frame synchronizer
All bits are set to 1. It is used for finding the beginning of frame. But these values can occur many times in binary file so you should test next values from header for validity (eg. bitrate bits arent 1111, sampling rate frequency isnt 11 etc.). But you can never be 100% sure if you find a header.
Next method is to find the first header and then go through all frames - almost exact, but time consuming.
Be careful with the first frame! It doesn't have to start at the first Byte in file. Either TAG v2 can be included or file can contains of some crap at the beginning.
Anyway - to find a header is a little problem.
  
B	  	MPEG version ID
00	  	MPEG Version 2.5 (not an official standard)
01	  	reserved
10	  	MPEG Version 2
11	  	MPEG Version 1
In most MP3 files these value should be 11.
  
C	  	Layer
00	  	reserved
01	  	Layer III
10	  	Layer II
11	  	Layer I
In most MP3 files these value should be 01 (because MP3 = MPEG 1 Layer 3).
  
D	  	CRC Protection
0	  	Protected by CRC
1	  	Not protected
Frames may have some form of check sum - CRC check. CRC is 16 bit long and, if exists, it follows frame header. And then comes audio data.
But almost all MP3 files doesnt contain CRC.
  
E	  	Bitrate index
0000	  	free
0001	  	32
0010	  	40
0011	  	48
0100	  	56
0101	  	64
0110	  	80
0111	  	96
1000	  	112
1001	  	128
1010	  	160
1011	  	192
1100	  	224
1101	  	256
1110	  	320
1111	  	bad
All values are in kbps.
  
F	  	Sampling rate frequency index
00	  	44100
01	  	48000
10	  	32000
11	  	reserved
All values are in Hz. In most MP3 files these value should be 00.
Note: Sample frequency 44100 means that one second of audio information is hacked to 44100 pieces. And each 1/44100 sec. is audio value taken and encoded into digital form.
  
G	  	Padding
0	  	Frame is not padded
1	  	Frame is padded
Padding is used to fit the bitrates exactly. If you use frames with length 417 Bytes (for 128kbps) it doesnt give exact data flow 128kbps. So you can set Padding and add one extra Byte at the end of some frames to create exact 128kbps.
  
H	  	Private bit
It can be freely used for specific needs of an application, eg. it can execute some application specific events.
No special meaning, forget it.
  
I	  	Channel
00	  	Stereo	  	Similar to Dual mono, 2 channels, but bitrate can be different for each one and is coded dynamically. Eg. if channel 1 is silent, the second one will get higher bitrate.
01	  	Joint Stereo	  	Mostly used in MP3. One channel is common (mid) and is used mainly for common and lower tones. The second is (side) channel for encoding differences between normal channels.
Note: Stereo effect is listenable properly only for higher tones because for lower ones is length of sound wave so long that you are not able to distinguish phase move.
10	  	Dual	  	Also known as Dual mono; 2 separate channels.
11	  	Mono (single channel	  	Normal mono.
  
J	  	Mode extension (only if Joint Stereo is set)
  	Intensity Stereo	  	MS Stereo
00		off						off
01		on						off
10		off						on
11		on						on
Tells which mode for JointStereo is used.
  
K	  	Copyright
0	  	Audio is not copyrighted
1	  	Audio is copyrighted
No special use.
  
L	  	Original
0	  	Copy of original media
1	  	Original media
No special use.
  
M	  	Emphasis
00	  	None
01	  	50/15
10	  	reserved
11	  	CCIT J.17
Tells if there are emphasised frequencies above cca. 3.2 kHz.

 */

/*		
 * ArrayList		
 * POTENTIALLY USABLE BITS: 
 * 				H	- 3.8 (byte 3, bit 8)
 * 			   (J)	- 4.3-4.4
 * 				K	- 4.5
 * 				L	- 4.6
 */

public class Mp3Read {

    private FileInputStream stream;
    File file;
    int length;
    ArrayList<byte[]> frames = new ArrayList<byte []>();
	
	final int NUM_OF_FRAMES = 50000; // I have no idea how to calculate how many frames are in a file yet (Surprising lack of info)
	final int NUM_OF_BYTES_PER_FRAME = 418; // 128kbps: 417, 192kbps: 626  REPLACE LATER WITH CALCULATED VALUE
	byte[][] soundData = new byte[NUM_OF_FRAMES][NUM_OF_BYTES_PER_FRAME];
	
	public Mp3Read(File audioFile) throws IOException {	
		file = audioFile;	
		stream = new FileInputStream(file);  	
	}
	
    public Mp3Data readMp3() throws IOException {
		Mp3Data data = new Mp3Data();
		byte[] temp;
    	
		/*
    	for(int i = 0; i < NUM_OF_FRAMES; ++i) {
			stream.read(soundData[i]);
		}
		*/   	
    	while(stream.available() > 0) {
			temp = new byte[NUM_OF_BYTES_PER_FRAME];
    		stream.read(temp);
    		frames.add(temp);
		}
    	
    	//data.dataFrames = soundData;
    	data.dataFrames = frames;
    	
    	return data;
    }
}