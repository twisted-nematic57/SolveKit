# Note: This repo is WIP as of the latest commit.

# SolveKit
SolveKit is a convenient runner and benchmarker for Java solutions to programming problems, with seamless integration into IntelliJ IDEA. It enables users to quickly design new solutions to programming problems and run, test, and benchmark them on different inputs quickly without having to worry about the testing and benchmarking infrastructure.


## Overview
SolveKit is a template project for IntelliJ IDEA. It contains several packages, one for each platform it supports. The user is expected to create classes under these packages that implement solutions to programming problems from the following platforms:

* [Advent of Code](https://adventofcode.com/)
* [LeetCode](https://leetcode.com/)
* [Project Euler](https://projecteuler.net/)
* [Codeforces](https://codeforces.com/)*
* [AtCoder](https://atcoder.jp/home)* ([Problem List](https://kenkoooo.com/atcoder/#/table/))
* [Sphere Online Judge (aka SPOJ)](https://www.spoj.com/problems/classical/)*
* [UVa Online Judge](https://onlinejudge.org/index.php?option=com_onlinejudge&Itemid=8)*

###### `*`: The platform does not support Java 25, which is the language version that this project is configured to use. Make sure to stick to the appropriate feature set supported by that platform and the specific problem you're solving.

Each solution can be run, tested on different sets of inputs (if applicable), and benchmarked using Run/Debug configurations through IntelliJ IDEA. All the infrastructure required to do that is implemented by SolveKit. You just have to worry about providing input correctly (if applicable) and writing your solution!


## How to Use
1. Clone this repo. Open it in IntelliJ IDEA. If necessary, go to File -> Project Settings and change the "SDK" setting to your Java 25 JDK.
2. Under the package that matches the name of the platform you're creating a solution for, create a new Java class. Name it something meaningful, preferably an identifier for the problem you're solving.
   * e.g., a class called `y2025_d07p1` in package `AdventOfCode` for "Day 7, Part 1 of Advent of Code 2025".
3. Write your solution based on the sample solution for your platform. There are sample solutions in each platform's module. They solve the first and simplest problem of each platform, and illustrate what SolveKit expects of your solutions.
   * **Advent of Code:** [Day 1, Part 1 of 2015](https://adventofcode.com/2015/day/1)
   * **LeetCode:** [Problem #1 ("Two Sum")](https://leetcode.com/problems/two-sum/)
   * **Project Euler:** [Problem #1 ("Multiples of 3 or 5")](https://projecteuler.net/problem=1)
   * **Codeforces:** [Problem #1A ("A. Theatre Square")](https://codeforces.com/problemset/problem/1/A)
   * **AtCoder:** [ABC424 - A - Isosceles](https://atcoder.jp/contests/abc424/tasks/abc424_a)
   * **SPOJ:** [Life, the Universe, and Everything](https://www.spoj.com/problems/TEST/)
   * **UVa Online Judge**: [100 - The 3n + 1 problem](https://onlinejudge.org/index.php?option=com_onlinejudge&Itemid=8&category=3&page=show_problem&problem=36)
4. If your platform requires you to accept input data at runtime, and *it isn't LeetCode*:
   * In the respective platform's subdirectory in [`inputs`](./inputs), you must create plaintext files that adhere to the following name format: `i_{ProblemSpecifier}_{Test#}.txt`
   * All input file names start with `i_` and end with `.txt`.
   * `{ProblemSpecifier}` is the **name of the solution class** that will use that file as input.
   * `{Test#}` is a **one-digit integer (0-9)** that specifies the test number. This field allows you to specify a different input for your solution without having to repeatedly modify the same file to change testcases. Test number 0 is the case that is accessed when you use the "Run Solution" Run/Debug configuration.
   * e.g., `inputs/AdventOfCode/i_y2015_d01p1_0.txt` contains input for the solution to Day 7, Part 1 of Advent of Code 2025, assuming my solution class for that problem is named `y2025_d07p1` under the `AdventOfCode` class.

Every time before clicking the Run button, click anywhere in the editor window containing your solution's source code. If you ever get strange string errors that occur outside your solution code, try clicking in the solution source code window and try again.
 * To **run** your solution, set the current Run/Debug Configuration to "Run Solution". Then, click the Run button.
 * To **test** your solution on a different piece of input, be sure that you've correctly set input files as described in step 4 above. Then, set your Run/Debug configuration to "Run Test #N" where N is the test number you want to run. Then, click the Run button.
 * To **benchmark** your solution, set your Run/Debug configuration to "Benchmark Solution". There will be two prompts that pop up before the solution is run. In order, here's what the prompts ask for:
   * The test # you want to benchmark the solution on. (In many situations, runtimes vary based on content and length of inputs.)
   * The number of times you want the solution to be run. SolveKit will run your solution that many times and print statistics on all runtimes. It will also print statistics on only the last 80% of runs, as JVM warmup, optimization & stabilization must be accounted for. 
   
   There is also a benchmarking Run/Debug configuration called "Benchmark Solution -> CSV". It behaves the same as the regular benchmarking config, but it will also save a CSV file containing the amount of time, in nanoseconds, each run of the solution took. The CSV will be stored in [`inputs`](./inputs), as that is the configured CWD of SolveKit, and it'll be named `runtimes_T.csv` where T is the current Unix timestamp in seconds. The CSV will just be one giant column of numbers, where the top element is the first run.

**Important benchmarking note:** Console output is infamously slow. Comment out all code that causes console output to get meaningful benchmark results.


### What format should my solutions be in?
There are some website-specific (I call it "platform-specific") differences, but generally:
 * Keep each solution contained in its own single source file. To do this, make only the class that contains the main method public. Where the code flows from the main method is platform-dependent.
 * The name of the class that contains the main method for each solution must follow a certain pattern or otherwise correspond with an identifier for the problem that the solution is for. This makes it easy to keep track of what solution it is at a glance.


### Platform-Specific Differences
 * For **Advent of Code** and **Project Euler:** You can essentially use whatever code structure you like as long as each solution stays contained in one file. **Remember that this "one-file rule" is applicable to all platforms.** Remember to print your output at the end!
   * For Advent of Code, input will be provided in an array of strings as the first and only argument to your main method.
 * For **LeetCode:**
   * As LeetCode does not provide full testcases to the public, you're going to be running your code on the short tests that they make publicly available.
   * So, in the main method, you are to create a switch case that sets your input variables to different values depending on the test #. If you aren't using a certain test # in the switch, just leave it unhandled.
   * Write the method they expect you to write below the main method. Name it what they name it and copy its parameter names & types exactly - you defined these earlier in the main method.
     * If it's a special question that requires you to implement an entire class, then write the class using the `class ... {` form they expect below the solution's public class. In the next step, call it the way that LeetCode says they will call it from the main method in the public class.
   * Now at the very end of the main method, call the method you wrote with the arguments you set in the switch case.
 * For **Project Euler:** Use whatever code structure you like, following the one-file rule. There will be no "input" for Project Euler problems because they heavily emphasize mathematical rigor over large amounts of input processing. Since there is no scope for varying inputs, Project Euler solutions will also be insensitive to test numbers at runtime, i.e. the same code will run in the same way regardless of what test # you've specifed.
 * For **Codeforces, AtCoder, SPOJ** and **UVa Online Judge:**
   * All of those platforms expect you to submit solutions in one file using the standard `public class Main { public static void main(...` format. When developing your solutions with SolveKit, name the class something other than Main, preferably an identifier for the problem you're solving. Then, when you submit your solution, just remember to change the public class's name back to Main.
   * All of those platforms also use stdin/stdout for I/O. Input will be sent to stdin from text files in the `inputs` directory by SolveKit. You can do output the same way you usually do.


## LLM Disclaimer
Some Large Language Models (often incorrectly referred to as "artificial intelligence") were used to assist in the creation of this project.
 * IntelliJ IDEA's Inline Code Completion tool was used to speed up development. No original ideas were generated by this; it simply predicted what I was going to write and got it correct 60% of the time.
 * Other miscellaneous LLMs were used to quickly understand new things about Java that I did not know about before. I am a novice and I asked questions about best practices and how a professional would implement certain things. I then cross-checked that with other sources manually. **Large, contiguous blocks of code generated by LLMs are not present in this project.**

An LLM was never used to generate significant amounts of code that I already know how to write. I never copy-paste things from an LLM without thoroughly understanding the "thought process" behind it and the new code's implications.

**LLMs should never be asked to directly solve any problems originating from any programming puzzle site/contest. The creator of this project condemns such acts.**


## Licensing
All code in this repository was written by me except the occasional Stack Overflow snippet (cited when appropriate) and the occasional LLM-produced trick. All is distributed under the [MIT License.](./LICENSE)
