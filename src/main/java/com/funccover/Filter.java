package com.funccover;

import java.lang.ClassLoader;

class Filter {  
    protected static boolean check(ClassLoader loader, String className) {
        if(className.startsWith("com/funccover") || loader == null) {
            return false;
        }
        while(loader != null) {
            if(loader == CoverageMetrics.class.getClassLoader()) {
                return true;
            }
            loader = loader.getParent();
        }
        if(CoverageMetrics.class.getClassLoader() == null) {
            return true;
        }
        return false;
    }
}
