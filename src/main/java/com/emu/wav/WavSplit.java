package com.emu.wav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class WavSplit {

    private static boolean matches(byte[] array1, byte[] array2){
        for(int i = 0; i < array1.length; i++){
            if(array1[i] != array2[i])
                return false;
        }
        return true;
    }

    public static int splitWav(InputStream wavIn, List<OutputStream> wavsOut) throws IOException {
        // Parse the header of input file
        WavHeader initialHeader = new WavHeader(wavIn);
        int channelCount = initialHeader.getNumChannels();
        if(wavsOut.size() < initialHeader.getNumChannels()){
            throw new IOException("Not enough output streams for this header");
        }

        // Setup then write the new output headers
        WavHeader[] newHeaders = new WavHeader[channelCount];
        for(int i = 0; i < channelCount; i++){
            newHeaders[i] = new WavHeader(initialHeader);
            newHeaders[i].setNumChannels((short) 1);
            newHeaders[i].setDataSize(initialHeader.getDataSize() /  channelCount);
            newHeaders[i].setFileSize((initialHeader.getFileSize() - 44) / channelCount);
            newHeaders[i].setByteRate(initialHeader.getSampleRate() * 1 * initialHeader.getBitsPerSample() / 8);
            newHeaders[i].setBlockAlign((short)(1 * newHeaders[i].getBitsPerSample() / 8));
            wavsOut.get(i).write(newHeaders[i].getBytes());
        }

        // Start reading the input stream, writing the outputs as we go
        final int sampleSize = (initialHeader.getBitsPerSample()/8) * channelCount;
        byte[] sample = new byte[sampleSize];
        int sampleParts = initialHeader.getBitsPerSample()/8;
        for(int i = 0; i < initialHeader.getDataSize(); i+= sampleSize){
            wavIn.read(sample);
            for(int x = 0; x < channelCount; x ++){
                wavsOut.get(x).write(sample, x*sampleParts, sampleParts);
            }
        }

        return channelCount;
    }
}
