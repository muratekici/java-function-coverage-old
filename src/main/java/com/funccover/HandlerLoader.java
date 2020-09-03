package com.funccover;

import java.io.File;
import java.net.URLClassLoader;
import java.net.MalformedURLException;
import java.net.URL;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

// HandlerLoader implements the functionality of loading and starting the Handler
class HandlerLoader {

    // Loads the given class to the memory and invokes its start() method with CoverageMetrics variables
    protected static void initializeCustomHandler(String path, String className) {

        URL url = getURL(path);
        File file = new File(path);

        // cl is the class loader to load handler
        URLClassLoader cl = null;

        // handler keeps an instance of given class
        // starts keeps the start() method
        Object handler = null;
        Method start = null;
        
        try {
            // load the class from given url
            cl = new URLClassLoader(new URL[]{url});
            Class cls = cl.loadClass(className);
            
            // Gets the constructor of given class an create an instance
            Constructor handlerConstructor = cls.getConstructor(new Class[] {
                CoverageMetrics.methodNames.getClass(),
                CoverageMetrics.methodCounters.getClass()});

            // Creates an instance of the handler with CoverageMetrics variables
            handler = handlerConstructor.newInstance(CoverageMetrics.methodNames, CoverageMetrics.methodCounters);
            
            // Gets the start method in newly created instance
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

    protected static URL getURL(String path) {

        String[] pathArgs = path.split(":", 2);

        if(pathArgs.length != 2) {
            System.out.println("invalid path " + path);
            return null;
        }
       
        // convert dir in filepath to url
        if(pathArgs[0].equals("dir")){
            File file = new File(pathArgs[1]);
            try {
                return file.toURI().toURL();
            } catch (java.net.MalformedURLException e) {
                System.out.println("malformed path " + pathArgs[1]);
                return null;
            }
        }
        // convert jar in filepath to url
        else if(pathArgs[0].equals("jar")) {
            File file = new File(pathArgs[1]);
            try {
                return new URL("jar", "","file:" + file.getAbsolutePath()+"!/");
            } catch (java.net.MalformedURLException e) {
                System.out.println("malformed path " + pathArgs[1]);
                return null;
            }
        }
        else if(pathArgs[0].equals("url")) {
            try {
                return new URL(pathArgs[1]);
            } catch(java.net.MalformedURLException e) {
                System.out.println("malformed url " + pathArgs[1]);
                return null;
            }
        }
        System.out.println("invalid path type " + pathArgs[0]);
        return null;
    }



}