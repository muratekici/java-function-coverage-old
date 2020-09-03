//Copyright 2020 Google LLC

//Licensed under the Apache License, Version 2.0 (the "License");
//you may not use this file except in compliance with the License.
//You may obtain a copy of the License at
//
//    https://www.apache.org/licenses/LICENSE-2.0
//
//Unless required by applicable law or agreed to in writing, software
//distributed under the License is distributed on an "AS IS" BASIS,
//WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//See the License for the specific language governing permissions and
//limitations under the License.

package example.handler;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.lang.Thread;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.ArrayList;
import java.io.File; 
import java.io.FileWriter;

public class Handler {

    private ArrayList<String> methodNames;
    private ArrayList<Boolean> methodCounters;    

    // To avoid Runner and LastCall tryin to write to file at the same time
    // one can use shutdown and awaitTermination functions in the scheduler
    // so that scheduler will not schedule any other tasks and 
    // LastCall will be blocked until scheduler finishes its already scheduled task
    // One can also use shutdownNow method to cancel all the tasks
    // shutdownNow is used here
    ScheduledExecutorService scheduler;
    
    // Constructor that gets the Metrics fields
    public Handler(ArrayList<String> methodNames, ArrayList<Boolean> methodCounters) {
        this.methodNames = methodNames;
        this.methodCounters = methodCounters;
    }

    // start function will be invoked at the beginning
    // it creates a scheduler that will call a runnable every 500ms
    public void start() {
                
        scheduler = Executors.newScheduledThreadPool(1, new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
               Thread thread = Executors.defaultThreadFactory().newThread(runnable);
               thread.setDaemon(true);
               return thread;
            }
        });
        //scheduler.scheduleWithFixedDelay(new Runner(), 500, 500, TimeUnit.MILLISECONDS);
        Runtime.getRuntime().addShutdownHook(new LastCall());
    }

    // Runner implements a Runnable that will write the coverage data
    // run() will be called by scheduler every 500ms
    public class Runner implements Runnable {

        public void run() {
            
            try {
                File myObj = new File("coverage.out");
                myObj.createNewFile();
                FileWriter myWriter = new FileWriter("coverage.out");
                final int len = methodNames.size();
                for(int i = 0; i < len; i++) {
                    myWriter.write(methodNames.get(i) + ":");
                    if(methodCounters.get(i)) myWriter.write("1\n");
                    else myWriter.write("0\n");    
                }
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred while writing the coverage data");
                e.printStackTrace();
            }
        } 
    }

    public class LastCall extends Thread {

        public void run() {

            scheduler.shutdownNow();
            
            try {
                File myObj = new File("coverage.out");
                myObj.createNewFile();
                FileWriter myWriter = new FileWriter("coverage.out");
                final int len = methodNames.size();
                for(int i = 0; i < len; i++) {
                    myWriter.write(methodNames.get(i) + ":");
                    if(methodCounters.get(i)) myWriter.write("1\n");
                    else myWriter.write("0\n");    
                }
                myWriter.close();
            } catch (IOException e) {
                System.out.println("An error occurred while writing the coverage data");
                e.printStackTrace();
            }
        } 
    }

}