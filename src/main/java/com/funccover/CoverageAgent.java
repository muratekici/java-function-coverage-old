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
        inst.addTransformer(new CoverageTransformer());
    }

    private static void initializeCustomHandler(String path, String className) {

        File file = new File(path);
        Object handler = null;
        Method start = null;
        URLClassLoader cl = null;

        try {
            URL url = file.toURI().toURL();
            URL[] urls = new URL[]{url};
            cl = new URLClassLoader(urls);
            Class cls = cl.loadClass(className);
            Constructor handlerConstructor = cls.getConstructor(new Class[] { Metrics.methodNames.getClass(), Metrics.methodCounters.getClass()} );
            handler = handlerConstructor.newInstance(Metrics.methodNames, Metrics.methodCounters);
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
            start.invoke(handler);
            cl.close();
        } catch (Exception e){
            e.printStackTrace();
            System.out.println("Could not invoke the start() method in " + className);
        }
    }


    private static void initializeHandler() {
        Handler handler = new Handler(Metrics.methodNames, Metrics.methodCounters);
        handler.start();
    }

}
