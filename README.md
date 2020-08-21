**This is not an officially supported Google product.**

# Java Function Coverage

The project aims to collect production Java function-level coverage with low overhead.

## Source Code Headers

Every file containing source code must include copyright and license
information. This includes any JS/CSS files that you might be serving out to
browsers. (This is to help well-intentioned people avoid accidental copying that
doesn't comply with the license.)

Apache header:

```
Copyright 2020 Google LLC

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## Overview

Java Function Coverage is a java agent that instruments the specified classes before load time. It collects the function coverage data for all the methods in those classes. The agent must be passed to the jvm with -javaagent option. The agent will instrument the bytecodes of the classes before they load into memory. 

## Quickstart

```bash
# Get the package from Github and install it from main directory of the package
$ mvn package
```

## How To Use It

To attach the agent you should give the agent as an argument to jvm. Agent itself also has arguments. 

* type can be "url", "jar" or "dir". 
    * If its url, path/to/handler must be an url
    * If its jar path/to/handler must be a path to a jar file  
    * If its dir path/to/handler must be a path to a directory that contains .class files (folders must be structured same as jar files)
    
* packageName.ClassName argument must be the fully qualified name of the Handler class (example.handler.Handler in the sample Handler.java) that contains a start() method and necessary constructor. An instance of this class will be created during runtime and start() method will be called at the very beginning. See the Handler directory for example implementations.  

```bash
$  java -javaagent:path/to/agent.jar="type:path/to/handler packageName.ClassName" [other args..]
```

### Example Usage

You have a jar file that you want to get production coverage data with your custom handler Handler.jar in /dir/Handler.jar directory, assume package name is custom.handler.MyImpl You want to instrument fun.jar

```bash
$ java -javaagent:path/to/agent.jar="jar:/dir/Handler.jar custom.handler.MyImpl" -jar fun.jar
```

Agent will create an instance of Handler with Metrics arraylists as parameters, then call the start() method in it. If you want to use default handler which will write the coverage data to coverage.out file every 500ms and just before jvm exits, use default handler Handler.jar in the main directory. Output coverage.out will be as follows:

```
packageName1.className1.function1:coverage1
packageName2.className2.function2:coverage2
packageName3.className3.function3:coverage3
...
```
