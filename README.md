# AoC in Java 25
This repository contains solutions I made for [Advent of Code](https://adventofcode.com/) problems with Java 25.

This repo is WIP. Latest completed solution: **2015 Day 1 Part 1**


## Solution Runner

Outside the solutions there is an extensively developed solution runner and benchmarker that is designed to integrate seamlessly with IntelliJ IDEA. It works as such:
* Open any solution file, e.g. [`Solutions/src/main/java/y2015_d01p1.java`](./Solutions/src/main/java/y2015_d01p1.java) and keep it in focus before you try to run the solution.
* Download your problem input from [adventofcode.com](https://adventofcode.com/2015/day/1). Save it to a file called `inputs/input_2015_01_1_0.txt`.
* From the "Run/Debug Configurations" area on the title bar, run the solution with the "Run Solution" configuration. If all goes well, it should just work. (You might have to specify where your JDK 25 is, I don't know for sure.)
* Other configurations:
  * "Run Test #N" makes the solution runner pass a different input to the solution. N = the number at the end of the input file's name, so test #1's input would be `inputs/input_2015_01_1_1.txt` (emphasis on the 1 at the end). Useful for testing the solution on smaller, debuggable inputs before setting it loose on the real thing.
  * "Benchmark Solution" will pop up a dialog asking you for an integer >= 1. It will run the solution that many times on the same piece of non-testing input and give you statistics on the amount of time that the solution took to run across many iterations.


## Disclaimer
Outside help was used to create these solutions, primarily Oracle's documentation and occasionally an LLM when a search engine couldn't understand my question properly. I also used an LLM to debug code that was behaving wrongly due to behavior that I didn't know existed in Java, e.g. 2015 Day 3, Part 1 where I was unknowingly passing a reference to an array rather than an array itself.

I will never use an LLM to generate large amounts of code that I already know how to write. I have put in my best efforts to ensure this project remains a benchmark of my coding skills while also allowing myself to use emerging technologies to learn and search things quicker. I never copy-paste things from an LLM without thoroughly understanding the "thought process" behind it.


## Licensing
You're free to fork this repo and clear all the solution files and create your own, as long as you respect the terms below.

All code in this repository was written by me, except the occasional Stack Overflow snippet (cited when appropriate) or LLM-produced trick. All is distributed under the [GPLv3 License.](./LICENSE)
