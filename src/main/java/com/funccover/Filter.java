package com.funccover;

import java.lang.ClassLoader;

class Filter {  
    protected static boolean check(ClassLoader loader, String className) {
        if(className.startsWith("com/funccover") || loader == null) {
            return false;
        }
        return true;
    }
}