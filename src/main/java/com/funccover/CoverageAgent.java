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

public class CoverageAgent {
    
    public static void premain(String args, Instrumentation inst) {
        Set<String> whiteList = getClassList(args);
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(new Handler(Metrics.methodNames, Metrics.methodCounters), 500, 500, TimeUnit.MILLISECONDS);
        inst.addTransformer(new CoverageTransformer(whiteList::contains, CoverageAgent::registerAddCounter, CoverageAgent::registerSetCounter));
    }

    // returns whitelisted class names
    private static Set<String> getClassList(String args) {
        if (args == null || args.equals("")) {
            throw new IllegalArgumentException("config file is invalid");
        }

        File f = new File(args);
        if (!f.exists()) {
            throw new IllegalArgumentException(args + " file does not exist");
        }

        try {
            return Files.lines(f.toPath())
                    .map(s -> s.replace('.', '/'))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to read " + args, e);
        }
    }



    private static class Metrics {
        
        static ArrayList<String> methodNames = new ArrayList<String>(); 
        static ArrayList<AtomicBoolean> methodCounters = new ArrayList<AtomicBoolean>(); 

        private static void addCounter(final String methodName) {
            methodNames.add(methodName);
            methodCounters.add(new AtomicBoolean(false));
           
        }

        private static void setCounter(final Integer index) {
            methodCounters.get(index).set(true);
        }

    }

    private static void registerAddCounter(final String methodName) {
        Metrics.addCounter(methodName);
    }

    private static void registerSetCounter(final Integer index) {
        Metrics.setCounter(index);
    }

}
