import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Steganography {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		Scanner in = new Scanner(System.in);
		FileInputStream fileIn = null;
		File audioFile, payloadFile, outputFile;
		String temp;
		String fileNameRoot;
		int messageLength;
		byte[] message;
		byte[] compMessage;
		boolean test = true;
		String txtFileAsString;
		List<Integer> compressedMsg;
		
		System.out.println("### Steganography/De-Steganography Tool");
		System.out.println("### developed by Matt Shank");
		System.out.println("###");
		System.out.println("### This steganography tool currently supports hiding .txt files in:");
		System.out.println("### \t[x] WAV (16-bit 44.1 kHz stereo)");
		System.out.println("### \t[x] MP3 (128kbps or 192kbps with/without ID3\n###");
		System.out.println("### For bug fixes or requested features/implementations, email mattrshank@gmail.com\n");
		
		do {
			System.out.println("Would you like to perform operations on an MP3 or a WAV?");
			System.out.print("('m' - MP3, 'w' - WAV): ");
			temp = in.nextLine();
		} while(!isValidChoice("type", temp));
		
		if(temp.equalsIgnoreCase("w")) {
			WavRead audioData = null;
			WavData wavData;
			WavData stegData;
			
			do {
				System.out.println("Which operation would you like to execute?");
				System.out.print("('s' - steg, 'd' - desteg): ");
				temp = in.nextLine();
			} while(!isValidChoice("operation", temp));
			
			if(temp.equalsIgnoreCase("s")) {
				do {
					test = true;
					System.out.print("Enter the name of the existing WAV file you would like to hide the payload in: ");
					temp = in.nextLine();
			
					audioFile = new File(temp);
					
					try {
						audioData = new WavRead(audioFile);
					} catch (IOException e) {
						System.out.println("ERROR: The file \"" + temp + "\" could not be found. Make sure this file exists and is in the correct directory. (" + System.getProperty("user.dir") + ")\n");
						test = false;
					}
				} while(!isFileType(".wav", temp) || !test);
				
				fileNameRoot = temp.substring(0, temp.length() - 4);	
				outputFile = new File(fileNameRoot + "_steg.wav");
				
				do {
					test = true;
					System.out.print("Enter the name of the payload file (must be a .txt file): ");
					temp = in.nextLine();
					
					payloadFile = new File(temp);
					
					try {
						fileIn = new FileInputStream(payloadFile);
					} catch(IOException e) {
						System.out.println("ERROR: The file \"" + temp + "\" could not be found. Make sure this file exists and is in the correct directory. (" + System.getProperty("user.dir") + ")\n");
						test = false;
					}		
				} while(!isFileType(".txt", temp) || !test);
				
				messageLength = (int)payloadFile.length();
				message = new byte[messageLength];
				fileIn.read(message);
				fileIn.close();
				
				System.out.println(messageLength);
				
				// Convert byte[] to String in order to apply LZW
				txtFileAsString = new String(message);
				
				// Compress the String with LZW compression
				compressedMsg = LZW.compress(txtFileAsString);
				System.out.println(convertIntegerListToByteArray(compressedMsg).length);
				
				wavData = audioData.readWav();
				
				stegData = stegLSB(message, wavData);
				
				writeWav(outputFile, stegData);
				
				System.out.println("\n!!! Steganography SUCCESSFUL !!!");
				System.out.println("The steganographied WAV file can be found at /" + fileNameRoot + "_steg.wav");
			}
			else if(temp.equalsIgnoreCase("d")) {
				do {
					test = true;
					System.out.print("Enter the name of the WAV file the payload is hidden in: ");
					temp = in.nextLine();
					
					audioFile = new File(temp);
					
					try {
						audioData = new WavRead(audioFile);
					} catch (IOException e) {
						System.out.println("ERROR: The file \"" + temp + "\" could not be found. Make sure this file exists and is in the correct directory. (" + System.getProperty("user.dir") + ")\n");
						test = false;
					}
				} while(!isFileType(".wav", temp) || !test);
				
				System.out.print("Enter the name of the file for holding extracted data: ");
				temp = in.nextLine();
				payloadFile = new File(temp);
				
				wavData = audioData.readWav();
				
				deStegLSB(payloadFile, wavData);
				
				System.out.println("\n!!! De-steganography SUCCESSFUL !!!");
				System.out.println("The extracted data can be found at /" + temp);
			}
		}
		else if(temp.equalsIgnoreCase("m")) {
			Mp3Read audioData = null;
			Mp3Data mp3Data;
			Mp3Data stegData;
			
			do {
				System.out.println("Which operation would you like to execute?");
				System.out.print("('s' - steg, 'd' - desteg): ");
				temp = in.nextLine();
			} while(!isValidChoice("operation", temp));
			
			if(temp.equalsIgnoreCase("s")) {
				do {
					test = true;
					System.out.print("Enter the name of the existing MP3 file you would like to hide the payload in: ");
					temp = in.nextLine();
					
					audioFile = new File(temp);
					
					try {
						audioData = new Mp3Read(audioFile);
					} catch (IOException e) {
						System.out.println("ERROR: The file \"" + temp + "\" could not be found. Make sure this file exists and is in the correct directory. (" + System.getProperty("user.dir") + ")\n");
						test = false;
					}
				} while(!isFileType(".mp3", temp) || !test);
				
				fileNameRoot = temp.substring(0, temp.length() - 4);
				outputFile = new File(fileNameRoot + "_steg.mp3");
				
				do {
					test = true;
					System.out.print("Enter the name of the payload file (must be a .txt file): ");
					temp = in.nextLine();
					
					payloadFile = new File(temp);
					
					try {
						fileIn = new FileInputStream(payloadFile);
					} catch(IOException e) {
						System.out.println("ERROR: The file \"" + temp + "\" could not be found. Make sure this file exists and is in the correct directory. (" + System.getProperty("user.dir") + ")\n");
						test = false;
					}		
				} while(!isFileType(".txt", temp) || !test);
				
				messageLength = (int)payloadFile.length();
				message = new byte[messageLength];
				fileIn.read(message);
				fileIn.close();
				
				mp3Data = audioData.readMp3();
				
				stegData = stegMp3(message, mp3Data);
				//stegData = mp3Data;
				
				writeMp3(outputFile, stegData);
				
				System.out.println("\n!!! Steganography SUCCESSFUL !!!");
				System.out.println("The steganographied MP3 file can be found at /" + fileNameRoot + "_steg.mp3");
			}
			else if(temp.equalsIgnoreCase("d")) {
				do {
					test = true;
					System.out.print("Enter the name of the MP3 file the payload is hidden in: ");
					temp = in.nextLine();
					
					audioFile = new File(temp);
					
					try {
						audioData = new Mp3Read(audioFile);
					} catch (IOException e) {
						System.out.println("ERROR: The file \"" + temp + "\" could not be found. Make sure this file exists and is in the correct directory. (" + System.getProperty("user.dir") + ")\n");
						test = false;
					}
				} while(!isFileType(".mp3", temp) || !test);
				
				System.out.print("Enter the name of the file for holding extracted data: ");
				temp = in.nextLine();
				payloadFile = new File(temp);
				
				mp3Data = audioData.readMp3();
				
				deStegMp3(payloadFile, mp3Data);
				
				System.out.println("\n!!! De-steganography SUCCESSFUL !!!");
				System.out.println("The extracted data can be found at /" + temp);
			}
		}
	}
	
	// LSB Steganography with 16-bit 44.1 kHz stereo WAV audio
	public static WavData stegLSB(byte[] message, WavData nonStegData) {
		WavData data = nonStegData;
		int byteCount = 32 * 2;
		int length = message.length;
		
		// Hide the length of the payload (an int) within the first 32 samples (16 of each channel) (excluding first 44 bytes for header) of the audio data
		for(int i = 0, j = 0; j < 32; i += 2, ++j) { //32 is the number of bits in 4 bytes (int) divided by the two channels
			data.soundData[i] = (byte)(nonStegData.soundData[i] & 0xfe);
			data.soundData[i] |= (1 & (length >>> j));
		}
		
		// Hide the payload in the audio data
		for(byte mByte : message) {
			for(int j = 0; j < 8; ++j) {
				data.soundData[byteCount] = (byte)(nonStegData.soundData[byteCount] & 0xfe);
				data.soundData[byteCount] |= (1 & (mByte >>> j));
				
				byteCount += 2;
			}
		}	
		
		data.setHeader(nonStegData.getHeader());
		
		return data;
	}
	
	public static void deStegLSB(File file, WavData stegData) throws IOException {
		WavData data = stegData;
		FileOutputStream outFile = new FileOutputStream(file);
		int length = 0;
		int byteCount = 32 * 2;
		byte temp = 0, curByte = 0;
		
		// Retrieve the length of the message
		for(int i = 0, j = 0; j < 32; i += 2, ++j) { //32 is the number of bits in 4 bytes (int) divided by the two channels
			length |= (1 & (int)data.soundData[i]) << j;
		}
		
		// Extract the payload from the audio
		for(int i = 0, j = 0; j < length * 8; i += 2, ++j) {
			temp = (byte)((stegData.soundData[i + byteCount] & 1) << j % 8);
			curByte |= temp;
			
			if(j % 8 == 7) {
				outFile.write(curByte);
				curByte = 0;
			}
		}	
		
		outFile.close();
	}
	
	// Post-encoding header steganography with 128kbps MP3s with constant bit-rate
	public static Mp3Data stegMp3(byte[] message, Mp3Data nonStegData) {
		Mp3Data data = nonStegData;
		int length = message.length;
		int frameIndex = 0;
		int byteIndex = 2;
		int bitIndex = 0;
		int counter = 0;
		
		// Hide the length of the payload in 32 usable bits of the MP3 (3 in the 1st frame, 3 in the 2nd frame,..., 2 in the 10th frame)
		for(int i = 0; i < 32; i++, counter++) { //32 is the number of bits in 4 bytes (int)
			switch(counter % 3) {
			case 0: byteIndex = 2;
					bitIndex = 7;
				break;
			case 1: byteIndex = 3;
					bitIndex = 4;
				break;
			case 2: byteIndex = 3;
					bitIndex = 5;
				break;
			}
			
			try {
				data.dataFrames.get(frameIndex * 2)[byteIndex] = (byte)(nonStegData.dataFrames.get(frameIndex * 2)[byteIndex] & ~(1 << (7 - bitIndex)));
				data.dataFrames.get(frameIndex * 2)[byteIndex] |= (1 & (length >>> i)) << (7 - bitIndex);
			}
			catch(IndexOutOfBoundsException e) {
				System.out.println("\nERROR: The data you want to hide is too large for the audio file provided. Please run the program again with less data or a larger audio file.");
				System.exit(1);
			}
			
			if(counter % 3 == 2)
				frameIndex++;
		}
		
		
		// Hide the payload in the audio data
		for(int i = 0; i < message.length; ++i) {
			for(int j = 0; j < 8; ++j, counter++) {
				switch(counter % 3) {
				case 0: byteIndex = 2;
						bitIndex = 7;
					break;
				case 1: byteIndex = 3;
						bitIndex = 4;
					break;
				case 2: byteIndex = 3;
						bitIndex = 5;
					break;
				}
				
				try {
				data.dataFrames.get(frameIndex * 2)[byteIndex] = (byte)(nonStegData.dataFrames.get(frameIndex * 2)[byteIndex] & ~(1 << (7 - bitIndex)));
				data.dataFrames.get(frameIndex * 2)[byteIndex] |= (1 & (message[i] >>> j)) << (7 - bitIndex);
				}
				catch(IndexOutOfBoundsException e) {
					System.out.println("\nERROR: The data you want to hide is too large for the audio file provided. Please run the program again with less data or a larger audio file.");
					System.exit(1);
				}
				
				if(counter % 3 == 2)
					frameIndex++;					
			}
		}
		
		return data;
	}
	
	public static void deStegMp3(File file, Mp3Data stegData) throws IOException {
		Mp3Data data = stegData;
		FileOutputStream outFile = new FileOutputStream(file);
		byte temp = 0;
		
		int length = 0;
		int frameIndex = 0;
		int byteIndex = 2;
		int bitIndex = 0;
		int counter = 0;
		
		// Extract the length of the payload from the first 32 usable bits of the MP3 (3 in the 1st frame, 3 in the 2nd frame,..., 2 in the 10th frame)
		for(int i = 0; i < 32; i++, counter++) { //32 is the number of bits in 4 bytes (int)
			switch(counter % 3) {
			case 0: byteIndex = 2;
					bitIndex = 7;
				break;
			case 1: byteIndex = 3;
					bitIndex = 4;
				break;
			case 2: byteIndex = 3;
					bitIndex = 5;
				break;
			}
			/*
			data.dataFrames.get(frameIndex * 2)[byteIndex] = (byte)(nonStegData.dataFrames.get(frameIndex * 2)[byteIndex] & ~(1 << (7 - bitIndex)));
			data.dataFrames.get(frameIndex * 2)[byteIndex] |= (1 & (length >>> i)) << (7 - bitIndex);
			*/
			
			length |= (1 & (int)(data.dataFrames.get(frameIndex * 2)[byteIndex] >>> (7 - bitIndex))) << i;
			
			if(counter % 3 == 2)
				frameIndex++;
		}
		
		System.out.println("Payload size = " + length);
		
		// Extract the payload from the audio data
		for(int i = 0; i < length; ++i) {
			for(int j = 0; j < 8; ++j, counter++) {
				switch(counter % 3) {
				case 0: byteIndex = 2;
						bitIndex = 7;
					break;
				case 1: byteIndex = 3;
						bitIndex = 4;
					break;
				case 2: byteIndex = 3;
						bitIndex = 5;
					break;
				}
				
				temp |= (1 & (byte)(stegData.dataFrames.get(frameIndex * 2)[byteIndex] >>> (7 - bitIndex))) << i;
				
				if(counter % 3 == 2)
					frameIndex++;	
			}
			outFile.write(temp);
			temp = 0;
		}
		outFile.close();
	}
	
	public static void writeMp3(File file, Mp3Data data) throws IOException {
		FileOutputStream out = new FileOutputStream(file);

		if(data.id3v2 != null) {
			for(int i = 0; i < data.id3v2.size(); ++i)
				out.write(data.id3v2.get(i));
		}
		
		for(int i = 0; i < data.dataFrames.size(); ++i)
			out.write(data.dataFrames.get(i));	
		
		out.close();
	}
	
	public static void writeWav(File file, WavData data) throws IOException {
		FileOutputStream out = new FileOutputStream(file);

		out.write(data.getHeader());		
		out.write(data.getSoundData());
		
		out.close();
	}
	
	public static boolean isValidChoice(String mode, String ch) { // mode = "type" (file type) or "operation"
		if(mode.equals("type")) {
			if(ch.equalsIgnoreCase("m"))
				return true;
			else if(ch.equalsIgnoreCase("w"))
				return true;
			else {
				System.out.println("ERROR: You entered an invalid character or string of characters: \"" + ch + "\" is not a valid argument. \n");
				return false;
			}
		}
		else if(mode.equals("operation")) {
			if(ch.equalsIgnoreCase("s"))
				return true;
			else if(ch.equalsIgnoreCase("d"))
				return true;
			else {
				System.out.println("ERROR: You entered an invalid character or string of characters: \"" + ch + "\" is not a valid argument. \n");
				return false;
			}
		}
		else
			return false;
	}
	
	public static boolean isFileType(String extension, String fileName) {
		if(fileName.substring(fileName.length() - 4, fileName.length()).equalsIgnoreCase(extension))
			return true;
		else {
			System.out.println("ERROR: The file \"" + fileName + "\" does not appear to be a " + extension + " file. Please try again with a " + extension + " file. \n");
			return false;
		}
	}
	
	public static byte[] convertIntegerListToByteArray(List<Integer> list) {
		byte[] converted = new byte[list.size() * 4]; // Number of integers in 'list' multiplied by 4 because each integer is 4 bytes
		int temp;
		
		for(int i = 0; i < list.size(); ++i) {
			temp = list.get(i);
			for(int j = 0; j < 4; ++j) {
				converted[i * 4 + j] = (byte)(0xff & (temp >>> (4 - 1 - j)));
			}
		}
		
		return converted;
	}
	
	public static List<Integer> convertByteArrayToIntegerList(byte[] bytes) {
		List<Integer> converted = new ArrayList<Integer>();
		int temp = 0;
		
		for(int i = 0; i < bytes.length; ++i) {
			
		}
		
		return converted;
	}
	
	public static boolean willDataFitInAudio(char audioType, File audio, byte[] data){
		if(audioType == 'w') {
			if(data.length > ((audio.length()/2)/8))
				return false;
			else
				return true;
		}
		else if(audioType == 'm') {
			if(data.length > ((audio.length()/2)/8))
				return false;
			else
				return true;
		}
		else
			return false;
	}
	
	public static void printBinary(short data, String caption) {
		System.out.println(caption + ": " + String.format("%8s", Integer.toBinaryString(data & 0xFFFF)).replace(' ', '0'));
	}
	
	public static void parseToByte(byte[] byteArray, String[] stringArray) {
		for(int i = 0; i < stringArray.length; ++i)
			byteArray[i] = Byte.parseByte(stringArray[i], 2);
	}
	
	
/*
 * 
 * UNUSED METHODS
 * 
	// LSB Steganography with 16-bit 44.1 kHz stereo WAV audio
	public static WavData stegLSB(byte[] message, int length, WavData nonStegData) {
		int byteCount = 16;
		WavData data = new WavData();
		short[] left = nonStegData.getLeft();
		short[] right = nonStegData.getRight();
		short[] stegLeft = new short[left.length];
		short[] stegRight = new short[right.length];
		
		// Hide the length of the payload (an int) within the first 32 samples (16 of each channel) (excluding first 44 bytes for header) of the audio data
		for(int i = 0, j = 0; i < 16; ++i, j+=2) { //32 is the number of bits in 4 bytes (int) divided by the two channels
			stegLeft[i] = (short)(left[i] & 0xfffe);
			stegLeft[i] |= (short)(length >> j);
			
			stegRight[i] = (short)(right[i] & 0xfffe);
			stegRight[i] |= (short)(length >> j + 1);;
		}
		
		// Hide the payload in the audio data
		for(byte mByte : message) {
			for(int i = 0, j = 0; i < 8; ++i, j+=2) {
				stegLeft[byteCount] = (short)(left[byteCount] & 0xfffe);
				stegLeft[byteCount] |= (short)(mByte >> j);
				//printBinary(stegLeft[byteCount], "");
				
				stegRight[byteCount] = (short)(right[byteCount] & 0xfffe);
				stegRight[byteCount] |= (short)(mByte >> j + 1);
				//printBinary(stegRight[byteCount], "");
				
				++byteCount;
			}
		}	
		
		data.setHeader(nonStegData.getHeader());
		data.setLeft(stegLeft);
		data.setRight(stegRight);
		
		return data;
	}
*/

}