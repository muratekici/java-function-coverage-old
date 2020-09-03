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

package example.handler;

import java.util.ArrayList;

public class SimpleHandler {

    private ArrayList<String> methodNames;
    private ArrayList<Boolean> methodCounters;    

    // This constuctor must be implemented
    public ExampleHandler(ArrayList<String> methodNames, ArrayList<Boolean> methodCounters) {
        this.methodNames = methodNames;
        this.methodCounters = methodCounters;
    }

    // Start method must be implemented, it will be called by the agent
    public void start() {
        // you can do anything here with methodNames and methodCounters
        // they point to static variables in Metrics so you can assume they are up to date
        System.out.println("Handler started executing");
        Runner runner = new Runner("google");
        runner.hello();
    }

    // Some random class definition
    public class Runner {

        private String str;

        public Runner(String name) {
            str = name;
        }

        public void hello() {
            System.out.println("hello " + str);
        }
    }

}