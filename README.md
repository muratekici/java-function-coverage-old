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

You can use the default handler
```bash
$ java -javaagent:path/to/agent.jar [other args...]
```

You can use a custom handler, first argument is the path to custom handlers directory, second argument is the full class name. Agent will invode start() method in given class.
```bash
$ java -javaagent:path/to/agent.jar="path/to/handler/directory packageName.ClassName" [other args...]
```

### Example Usage

You have a jar file that you want to get production coverage data with your custom handler Handler.class in /dir/custom/handler directory, assume package name is custom.handler. You want to instrument fun.jar

```bash
$ java -javaagent:path/to/agent.jar="/dir custom.handler.Handler" -jar fun.jar
```

Agent will create an instance of Handler with Metric arrays as parameters, then call the start function in it. If you want to use default handler which will write the coverage data to coverage.out wile every 500ms, use:
```bash
$ java -javaagent:path/to/agent.jar -jar fun.jar
```

```
packageName1.className1.function1:coverage1
packageName2.className2.function2:coverage2
packageName3.className3.function3:coverage3
...
```