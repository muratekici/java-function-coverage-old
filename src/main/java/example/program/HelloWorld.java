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

package example.program;

import example.program.functions.Functions;
import java.util.Scanner;

public class HelloWorld {

    public static void main(String []args) {
        System.out.println("Hello World");
        System.out.println("Enter some space seperated integers in the range [1..9] in a line in any order");
        System.out.println("Program will call the function f$number for each number");
        System.out.println("For example handler coverage.out will contain invoked function names, lets try it:");
        Scanner in = new Scanner(System.in);
        String s = in.nextLine();
        String[] splitted = s.split("\\s+");
        for (String number: splitted) {
            switch (number) {
                case "1": Functions.f1(); break; 
                case "2": Functions.f2(); break; 
                case "3": Functions.f3(); break; 
                case "4": Functions.f4(); break; 
                case "5": Functions.f5(); break; 
                case "6": Functions.f6(); break; 
                case "7": Functions.f7(); break; 
                case "8": Functions.f8(); break; 
                case "9": Functions.f9(); break; 
            }
        }
    }   
    

}
