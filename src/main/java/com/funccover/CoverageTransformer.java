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
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Predicate;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtBehavior;
import javassist.CtClass;
import javassist.CtMethod;

public class CoverageTransformer implements ClassFileTransformer {

    private static int counter = 0;

    private static Consumer<String> registerAddCounterFunction = (s) -> { };
    private static Consumer<Integer> registerSetCounterFunction = (d) -> { };
    
    private final Predicate<String> whiteList;
    private final ClassPool classPool = ClassPool.getDefault();

    CoverageTransformer(Predicate<String> whiteList, Consumer<String> registerAddCounter, Consumer<Integer> registerSetCounter) {
        this.whiteList = whiteList;
        registerAddCounterFunction = registerAddCounter;
        registerSetCounterFunction = registerSetCounter;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        
        if (className.startsWith("com/funccover")) {
            return null;
        }

        if (!whiteList.test(className)) {
            return null;
        }
        
        byte[] result = null;
        try {
            CtClass ct = classPool.makeClass(new ByteArrayInputStream(classfileBuffer));
            if (ct.isFrozen()) {
                return null;
            }
            if (ct.isPrimitive() || ct.isArray() || ct.isAnnotation() || ct.isEnum() || ct.isInterface()) {
                return null;
            }

            boolean flag = false;
            for(CtMethod method: ct.getDeclaredMethods()) {
                if (method.isEmpty()) {
                    continue;
                }
                instrumentMethod(method);
                flag = true;
            }

            if (flag) {
                System.out.println("Instrumented: " + className);
                result = ct.toBytecode();
            }
            ct.detach();
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return result;
    }

    private static <T extends CtBehavior> void instrumentMethod(T target) throws CannotCompileException {
        addCounter(target.getLongName());
        target.insertBefore("com.funccover.CoverageTransformer.setCounter(" + counter + ");");
        counter++;
    }

    /** Used by instrumentation code. */
    public static void addCounter(final String methodName) {
        registerAddCounterFunction.accept(methodName);
    }

    public static void setCounter(final int id) {
        registerSetCounterFunction.accept(id);
    }
}
