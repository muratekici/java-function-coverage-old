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

import java.io.ByteArrayInputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;

// CoverageTransformer implements a class that will be used in instrumentation
public class CoverageTransformer implements ClassFileTransformer {

    // Keeps the number methods instrumented so far 
    private static int counter = 0;

    // Javassist claspool that keeps the classes 
    private final ClassPool classPool = ClassPool.getDefault();

    CoverageTransformer() {
        // empty
    }

    // transform instruments given bytecode and returns instrumented bytecode
    // if it returns null, then given class will be loaded without instrumentation
    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        
        // if we do not want to instrument given class, return null
        if(filter(className) == false || classBeingRedefined != null) {
            return null;
        }
        
        byte[] result = null;
        try {
            // Creates a new class with the given bytecode
            CtClass ct = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

            // checks if the class is already loaded
            if (ct.isFrozen()) {
                return null;
            }

            // filter for instrumentation
            if (ct.isPrimitive() || ct.isArray() || ct.isAnnotation() || ct.isEnum() || ct.isInterface()) {
                return null;
            }

            // flag is true if instrumented
            boolean flag = false;
            
            // Iterates over all methods and instruments them
            for(CtMethod method: ct.getDeclaredMethods()) {
                if (method.isEmpty()) {
                    continue;
                }
                instrumentMethod(method);
                flag = true;
            }

            if (flag) {
                result = ct.toBytecode();
            }
            ct.detach();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    // Inserts a new method to Metrics and inserts setCounter call to the given method
    private static <T extends CtBehavior> void instrumentMethod(T target) throws CannotCompileException {
        Metrics.addCounter(target.getLongName());
        target.insertBefore("com.funccover.Metrics.setCounter(" + counter + ");");
        counter++;
    }

    // filter returns true if we will instrument given class 
    private boolean filter(String name) {
        if (name.startsWith("com/funccover")) {
            return false;
        }
        if (name.startsWith("java") || name.startsWith("jdk") || name.startsWith("sun")) { 
            return false;
        }
        return true;
    }
}
