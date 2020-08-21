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
            System.out.println("no arguments");
            return ;
        }

        String[] tokens = args.split(" ");
        if(tokens.length != 2) {
            System.out.println("arguments are invalid");
            return ;
        }
        initializeCustomHandler(tokens[0], tokens[1]);
    
        
        // Adds out CoverageTransformer class as an insrumenter
        // transform method will be called for every class being loaded after this line
        inst.addTransformer(new CoverageTransformer());
    }

    // Loads the given class to the memory and invokes its start() method with Metrics
    private static void initializeCustomHandler(String path, String className) {

        String[] pathArgs = path.split(":", 2);

        if(pathArgs.length != 2) {
            return ;
        }
       
        URL url = null;
        
        // convert dir in filepath to url
        if(pathArgs[0].equals("dir")){
            File file = new File(pathArgs[1]);
            try {
                url = file.toURI().toURL();
            } catch (java.net.MalformedURLException e) {
                System.out.println("malformed path " + pathArgs[1]);
                return ;
            }
        }
        // convert jar in filepath to url
        else if(pathArgs[0].equals("jar")) {
            File file = new File(pathArgs[1]);
            try {
                url = new URL("jar", "","file:" + file.getAbsolutePath()+"!/");
            } catch (java.net.MalformedURLException e) {
                System.out.println("malformed path " + pathArgs[1]);
                return ;
            }
        }
        else if(pathArgs[0].equals("url")) {
            try {
                url = new URL(pathArgs[1]);
            } catch(java.net.MalformedURLException e) {
                System.out.println("malformed url " + pathArgs[1]);
                return ;
            }
        }
        else { 
            System.out.println("invalid path type " + pathArgs[0]);
            return ;
        }

        // handler keeps an instance of given class
        // starts keeps the start() method
        File file = new File(path);
        Object handler = null;
        Method start = null;
        URLClassLoader cl = null;

        try {
            // load the class from given url
            cl = new URLClassLoader(new URL[]{url});
            Class cls = cl.loadClass(className);
            // get the constructor of given class an create an instance
            Constructor handlerConstructor = cls.getConstructor(new Class[] { Metrics.methodNames.getClass(), Metrics.methodCounters.getClass()} );
            handler = handlerConstructor.newInstance(Metrics.methodNames, Metrics.methodCounters);
            // get the start method in newly created instance
            start = cls.getMethod("start");
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not load the custom handler " + className);
            return ;
        }

        if(handler == null || start == null || cl == null) {
            return ;
        }
        
        try {
            start.invoke(handler);
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Could not invoke the start() method in " + className);
        }
    }

}
