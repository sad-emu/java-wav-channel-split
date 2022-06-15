package com.emu.wav;

import com.emu.wav.WavHeader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class main {
    public static void main(String[] args) {
        try{
            InputStream inputWav = new FileInputStream("C:\\Users\\ted\\Documents\\Audacity\\1.wav");
            WavHeader header = new WavHeader(inputWav);
            OutputStream outStream = new FileOutputStream("C:\\Users\\ted\\Documents\\Audacity\\headervalid.bytes");
            outStream.write(header.getBytes());
            outStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        System.out.println("Enter the file path of the WAV you want to split...");
        String inPath = System.console().readLine();
        System.out.println("Enter the file path for the output file in channel 1");
        String outPath1 = System.console().readLine();
        System.out.println("Enter the file path for the output file in channel 1");
        String outPath2 = System.console().readLine();
        System.out.println("Starting...");
        try{
            FileOutputStream out1 = new FileOutputStream(outPath1);
            FileOutputStream out2 = new FileOutputStream(outPath2);
            List<OutputStream> outWavs = new ArrayList<>();
            outWavs.add(out1);
            outWavs.add(out2);
            InputStream inputWav = new FileInputStream(inPath);
            WavSplit split = new WavSplit();
            split.splitWav(inputWav, outWavs);
            System.out.println("Done!");
        } catch (Exception exc){
            System.out.print(exc);
        }
    }
}
