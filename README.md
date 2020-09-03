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

### Example Usage with Bazel
This repository implements a simple program to test the coverage tool.

First build funccover agent

```bash
$ bazel build //src/main/java/com/funccover:funccover_deploy.jar
```

Then build the example handler in the repository

```bash
$ bazel build //src/main/java/example/handler:Handler
```

Then finally build the example program binary with the agent and handler

```bash
$ bazel build --jvmopt="-javaagent:agent_path='jar:handler_path example.handler.Handler' "  //src/main/java/example/program:HelloWorld 

Here agent_path is bazel-bin/src/main/java/com/funccover/funccover_deploy.jar, 
handler_path is bazel-bin/src/main/java/example/handler/libHandler.jar
```

This will generate an executable inside ```bazel-bin/src/main/java/example/program/HelloWorld```.
When you run it, it will ask you to enter numbers in range [1-9] in a line then it will call ```f$number``` function for each number you entered.
coverage data will be saved to ```coverage.out```
