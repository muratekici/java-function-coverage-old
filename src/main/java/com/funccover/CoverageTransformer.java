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
import javassist.Modifier;


// CoverageTransformer implements a class that will be used in instrumentation
public class CoverageTransformer implements ClassFileTransformer {

    // Keeps the number methods instrumented so far 
    private static int counter = 0;

    // the ClassPool object returned by getDefault() searches the default system search path
    // If a program is running on a web application server such as JBoss and Tomcat, 
    // the ClassPool object may not be able to find user classes
    // In that case, an additional class path must be registered to the ClassPool.
    // ClassPool used to compile inserted source code to bytecode
    private final ClassPool classPool = ClassPool.getDefault();

    CoverageTransformer() {
        classPool.importPackage("com.funccover.CoverageMetrics");
    }

    // transform instruments given bytecode and returns instrumented bytecode
    // if it returns null, then given class will be loaded without instrumentation
    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        
        // if we do not want to instrument given class, return null

        if(classBeingRedefined != null || Filter.check(loader, className) == false) {
            return null;
        }

        
        byte[] result = null;
        try {
            // Creates a new class with the given bytecode
            CtClass ct = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));

            // checks if the class is already loaded
            if (ct.isFrozen()) {
                ct.detach();
                return null;
            }

            // filter for instrumentation
            if (ct.isPrimitive() || ct.isArray() || ct.isAnnotation() || ct.isEnum() || ct.isInterface()) {
                ct.detach();
                return null;
            }

            // flag is true if instrumented
            boolean flag = false;
            
            // Iterates over all methods and instruments them
            for(CtMethod method: ct.getDeclaredMethods()) {
                if (method.isEmpty()) {
                    continue;
                }
               // System.out.println(loader);
                instrumentMethod(method, loader);
                flag = true;
            }

            if (flag) {
                result = ct.toBytecode();
            }

            // detach removes newly created class from cp to avoid unnecesarry memory consumption
            ct.detach();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    // Inserts a new method to CoverageMetrics and inserts setCounter call to the given method
    private static void instrumentMethod(CtMethod target, ClassLoader loader) throws CannotCompileException {
        if(isNative(target)) {
            return ;
        }
        CoverageMetrics.addCounter(target.getLongName());
        // fix the cannot compile error (no method body)
        target.insertBefore("CoverageMetrics.setCounter(" + counter + ");");
        counter++;
    }

    public static boolean isNative(CtMethod method) {
        return Modifier.isNative(method.getModifiers());
    }

}
