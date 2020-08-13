package com.funccover;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.lang.Thread;
import java.util.ArrayList;
import java.io.File; 
import java.io.FileWriter;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;

public class Handler implements Runnable {
        
    private ArrayList<String> methodNames;
    private ArrayList<AtomicBoolean> methodCounters;
    
    public Handler(ArrayList<String> methodNames, ArrayList<AtomicBoolean> methodCounters) {
        this.methodNames = methodNames;
        this.methodCounters = methodCounters;
    }

    public void run() {
        try {
            File myObj = new File("coverage.out");
            myObj.createNewFile();
            FileWriter myWriter = new FileWriter("coverage.out");
            final int len = methodNames.size();
            for(int i = 0; i < len; i++) {
                myWriter.write(methodNames.get(i) + ":");
                if(methodCounters.get(i).get()) myWriter.write("1\n");
                else myWriter.write("0\n");    
            }
            myWriter.close();
        } catch (IOException e) {
            System.out.println("An error occurred while writing the coverage data");
            e.printStackTrace();
        }
    }
}