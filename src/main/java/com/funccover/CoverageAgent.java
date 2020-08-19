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

package com.funccover;

import java.lang.instrument.Instrumentation;
import java.io.File;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;


public class CoverageAgent {
    
    // premain method starts executing by the jvm before main method
    // it initializes the Handler and instrumenter
    public static void premain(String args, Instrumentation inst) {
        if(args == null || args == "") {
            initializeHandler();
        }
        else {
            String[] tokens = args.split(" ");
            if(tokens.length != 2) {
                System.out.println("arguments are invalid");
                return ;
            }
            initializeCustomHandler(tokens[0], tokens[1]);
        }
        
        // Adds out CoverageTransformer class as an insrumenter
        // transform method will be called for every class being loaded after this line
        inst.addTransformer(new CoverageTransformer());
    }

    // Loads the given class to the memory and invokes its start() method with Metrics
    private static void initializeCustomHandler(String path, String className) {

        // handler keeps an instance of given class
        // starts keeps the start() method
        File file = new File(path);
        Object handler = null;
        Method start = null;
        URLClassLoader cl = null;

        try {
            // Creates a URLClassLoader with given url
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            cl = new URLClassLoader(urls);
            
            // Loads the className from given class path
            Class cls = cl.loadClass(className);

            // Gets the constructor for given class with necessary parameters
            Constructor handlerConstructor = cls.getConstructor(new Class[] { Metrics.methodNames.getClass(), Metrics.methodCounters.getClass()} );
            // Constructs a new instance of given class with Metrics variables
            handler = handlerConstructor.newInstance(Metrics.methodNames, Metrics.methodCounters);
            // Sets the start method
            start = cls.getMethod("start");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load the custom handler " + className);
            return ;
        }

        if(handler == null || start == null) {
            return ;
        }
        
        try {
            // invokes the start method of constructed handler then closes the URLClassLoader
            start.invoke(handler);
            // We won't close the cl because some other classes in the classpath of cl may be loaded after some time
            // cl.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Could not invoke the start() method in " + className);
        }
    }

    // Initializes the handler with default one
    private static void initializeHandler() {
        Handler handler = new Handler(Metrics.methodNames, Metrics.methodCounters);
        handler.start();
    }

}
