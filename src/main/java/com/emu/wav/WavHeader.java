package com.emu.wav;

import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class WavHeader {
    private final String RIFF_FILE_HEADER = "RIFF";
    private int fileSize = -1;
    private final String WAVE_TYPE_HEADER = "WAVE";
    private byte[] formatChunkMarker = new byte[4];
    private int formatDataLength = -1;
    private short formatType = -1;
    private short numChannels = -1;
    private int sampleRate = -1;
    private int byteRate = -1;
    private short blockAlign = -1;
    private short bitsPerSample = -1;
    private final String DATA_CHUNK_HEADER = "data";
    private int dataSize = -1;
    private boolean parsed = false;

    public int getFileSize() { return this.fileSize; }
    public byte[] getFormatChunkMarker() { return this.formatChunkMarker; }
    public int getFormatDataLength() { return this.formatDataLength; }
    public short getFormatType() { return this.formatType; }
    public short getNumChannels() { return this.numChannels; }
    public int getSampleRate() { return this.sampleRate; }
    public int getByteRate() { return this.byteRate; }
    public short getBlockAlign() {return this.blockAlign; }
    public short getBitsPerSample() {return this.bitsPerSample; }
    public int getDataSize() {return this.dataSize; }



    public void setNumChannels(short numChannels){
        this.numChannels = numChannels;
    }

    public void setByteRate(int byteRate){
        this.byteRate = byteRate;
    }

    public void setBlockAlign(short blockAlign){
        this.blockAlign = blockAlign;
    }

    public void setFileSize(int fileSize){
        this.fileSize = fileSize;
    }

    public void setDataSize(int dataSize){
        this.dataSize = dataSize;
    }

    public byte[] getBytes(){
        ByteBuffer retBuffer = ByteBuffer.allocate(44);
        retBuffer.put(RIFF_FILE_HEADER.getBytes(StandardCharsets.UTF_8));
        retBuffer.put(ByteBuffer.allocate(4).putInt(Integer.reverseBytes(fileSize)).array());
        retBuffer.put(WAVE_TYPE_HEADER.getBytes(StandardCharsets.UTF_8));
        retBuffer.put(formatChunkMarker);
        retBuffer.put(ByteBuffer.allocate(4).putInt(Integer.reverseBytes(formatDataLength)).array());
        retBuffer.put(ByteBuffer.allocate(2).putShort(Short.reverseBytes(formatType)).array());
        retBuffer.put(ByteBuffer.allocate(2).putShort(Short.reverseBytes(numChannels)).array());
        retBuffer.put(ByteBuffer.allocate(4).putInt(Integer.reverseBytes(sampleRate)).array());
        retBuffer.put(ByteBuffer.allocate(4).putInt(Integer.reverseBytes(byteRate)).array());
        retBuffer.put(ByteBuffer.allocate(2).putShort(Short.reverseBytes(blockAlign)).array());
        retBuffer.put(ByteBuffer.allocate(2).putShort(Short.reverseBytes(bitsPerSample)).array());
        retBuffer.put(DATA_CHUNK_HEADER.getBytes(StandardCharsets.UTF_8));
        retBuffer.put(ByteBuffer.allocate(4).putInt(Integer.reverseBytes(dataSize)).array());
        return retBuffer.array();
    }


    @Override
    public String toString(){
        if(!parsed)
            return "Empty header";
        return "File Header: " +RIFF_FILE_HEADER +
                "\nFile Size: " + fileSize +
                "\nType Header: " + WAVE_TYPE_HEADER +
                "\nFormat Chunk Marker: " + new String(formatChunkMarker, StandardCharsets.UTF_8) +
                "\nFormat Data Length: " + formatDataLength +
                "\nFormat Type: " + formatType +
                "\nNum Channels: " + numChannels +
                "\nSample Rate: " + sampleRate +
                "\nByte Rate: " + byteRate +
                "\nBlock Align: " + blockAlign +
                "\nBits Per Sample: " + bitsPerSample +
                "\nData Chunk Header: " + DATA_CHUNK_HEADER +
                "\nData Size: " + dataSize;
    }

    public WavHeader(){}

    public WavHeader(WavHeader header){
        this.fileSize = header.getFileSize();
        this.formatChunkMarker = header.getFormatChunkMarker();
        this.formatDataLength = header.getFormatDataLength();
        this.formatType = header.getFormatType();
        this.numChannels = header.getNumChannels();
        this.sampleRate = header.getSampleRate();
        this.byteRate = header.getByteRate();
        this.blockAlign = header.getBlockAlign();
        this.bitsPerSample = header.getBitsPerSample();
        this.dataSize = header.getDataSize();
        this.parsed = true;
    }

    private void parseStream(InputStream waveIn, boolean ignoreExceptions) throws IOException{
        DataInputStream wD = new DataInputStream(waveIn);
        byte[] fileHeader = new byte[4];
        wD.read(fileHeader);
        if(!new String(fileHeader, StandardCharsets.UTF_8).contentEquals(RIFF_FILE_HEADER)){
            if(!ignoreExceptions)
                throw new IOException("Stream is not a WAVE file. RIFF header is missing.");
        }
        // DataInputStream is BigEndian
        this.fileSize = Integer.reverseBytes(wD.readInt());
        wD.read(fileHeader);
        if(!new String(fileHeader, StandardCharsets.UTF_8).contentEquals(WAVE_TYPE_HEADER)){
            if(!ignoreExceptions)
                throw new IOException("Stream is not a WAVE file. WAVE header is missing.");
        }
        if(wD.read(formatChunkMarker) != 4){
            if(!ignoreExceptions)
                throw new IOException("Stream is not a WAVE file. File is too small. Missing format chunk marker.");
        }
        this.formatDataLength = Integer.reverseBytes(wD.readInt());
        this.formatType = Short.reverseBytes(wD.readShort());
        this.numChannels = Short.reverseBytes(wD.readShort());
        this.sampleRate = Integer.reverseBytes(wD.readInt());
        this.byteRate = Integer.reverseBytes(wD.readInt());
        this.blockAlign = Short.reverseBytes(wD.readShort());
        this.bitsPerSample = Short.reverseBytes(wD.readShort());
        wD.read(fileHeader);
        if(!new String(fileHeader, StandardCharsets.UTF_8).contentEquals(DATA_CHUNK_HEADER)){
            if(!ignoreExceptions)
                throw new IOException("Stream is not a WAVE file. data header is missing.");
        }
        this.dataSize = Integer.reverseBytes(wD.readInt());
    }

    public WavHeader(InputStream waveIn) throws IOException {
        this.parseStream(waveIn, false);
        this.parsed = true;
    }
}
