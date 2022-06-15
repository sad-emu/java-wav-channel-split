package com.emu.wav;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class WavSplit {

    // Split a single WAV into multiple wave file per it's channels
    // For example a stereo file will split into 2 files, a left and right side
    public static int splitWav(InputStream wavIn, List<OutputStream> wavsOut) throws IOException {
        // Parse the header of input file
        WavHeader initialHeader = new WavHeader(wavIn);
        int channelCount = initialHeader.getNumChannels();
        if(wavsOut.size() < initialHeader.getNumChannels()){
            throw new IOException("Not enough output streams for this header");
        }

        // Setup then write the new output headers
        WavHeader[] newHeaders = new WavHeader[channelCount];
        final int headerSize = initialHeader.getBytes().length;
        final int byteSize = 8;
        final int numChannels = 1;
        for(int i = 0; i < channelCount; i++){
            newHeaders[i] = new WavHeader(initialHeader);
            newHeaders[i].setNumChannels((short) numChannels);
            newHeaders[i].setDataSize(initialHeader.getDataSize() /  channelCount);
            newHeaders[i].setFileSize((initialHeader.getFileSize() - headerSize) / channelCount);
            newHeaders[i].setByteRate(initialHeader.getSampleRate() * initialHeader.getBitsPerSample() / byteSize);
            newHeaders[i].setBlockAlign((short)(newHeaders[i].getBitsPerSample() / byteSize));
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
