/*** Main.java ****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-11-26                                                 *
 * Description:    Sets up the AoC solution runner. Runs, tests, or           *
 *                 benchmarks solutions as needed.                            *
\******************************************************************************/

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

public class Main {
  static void main(String[] args) throws ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    // Arg (singular) will look like this: "yYYYY_dXXpY_Z[BN[...]]"
    //  * YYYY is the year
    //  * XX is the day (always 2 digits)
    //  * Y is the part number (always == 1 or 2)
    //  * Z is the test number (always 1 digit)
    //  * If the letter "B" is present at the end, we need to benchmark the solution N times, where N
    //    is an integer of whatever length.

    if(!args[0].contains("B")) { // No benchmarking, just running the solution once
      long runtime = RunSolution(
          Integer.parseInt(args[0].substring(1, 5)), // Year
          Integer.parseInt(args[0].substring(7, 9)),      // Day
          Integer.parseInt(args[0].substring(10, 11)),    // Part
          Integer.parseInt(args[0].substring(12, 13)));   // Test

      if(runtime != -1) { // In general, if something == -1 in the runner then something has gone wrong; likely an inexistent problem has been input.
        System.out.println("\n---------------------------------------------------");
        System.out.printf("Runtime: %.3f ms", runtime*.000001); // Convert to milliseconds before printing, easier to read
      } else {
        System.out.println("\nSomething went wrong and the solution couldn't be run. Are you sure the specified problem exists?");
      }
    } else { // We are benchmarking.
      System.out.println("Initializing benchmarking...");

      long[] benchResults = BenchmarkSolution(
          Integer.parseInt(args[0].substring(1, 5)),                           // Year
          Integer.parseInt(args[0].substring(7, 9)),                                // Day
          Integer.parseInt(args[0].substring(10, 11)),                              // Part
          Integer.parseInt(args[0].substring(12, 13)),                              // Test
          Integer.parseInt(args[0].substring(args[0].indexOf("B") + 1))); // # of iterations

      if(benchResults[0] != -1) {
        // Print benchmarking results
        System.out.println("\n---------------------------------------------------");
        benchResults = Arrays.stream(benchResults).sorted().toArray(); // Sort the results ascending
        // Get lengths of longest numbers in the array in both ms and μs for formatting purposes
        int longestMillis = String.format("%.3f", benchResults[benchResults.length - 1] * 0.000001).length();
        int longestMicros = String.format("%.1f", benchResults[benchResults.length - 1] * 0.001).length();

        // *.000001 always means we're converting ns -> ms, and *.001 means ns -> μs
        System.out.println("Benchmarking results (execution time):");
        System.out.println(" - Runs   : " + benchResults.length);
        System.out.printf (" - Lowest : %-" + longestMillis + ".3f ms / %" + longestMicros + ".1f μs\n", benchResults[0]*.000001, benchResults[0]*.001);
        System.out.printf (" - Highest: %-" + longestMillis + ".3f ms / %" + longestMicros + ".1f μs\n", benchResults[benchResults.length - 1]*.000001, benchResults[benchResults.length - 1]*.001);
      } else {
        System.out.println("\nSomething went wrong and the solution couldn't be run. Are you sure the specified problem exists?");
      }
    }

  }


  // Runs a specific solution on a certain input (test #).
  //  year,day,part: specifies the specific solution to run
  //  test: specifies the test # to run. If == 0, runs the solution on official input from
  //        adventofcode.com. In range [0,9]
  // RETURNS: the amount of time in nanoseconds the solution took to execute
  public static long RunSolution(int year, int day, int part, int test) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    if(problemDoesntExist(year, day, part, test)) { // Input validation
      return -1;
    }

    // Load input for the problem and testcase.
    List<String> input = loadInput(year, day, part, test);

    // Do some voodoo to be able to call the solution's main method
    Class<?> solutionClass = Class.forName(String.format("y%d_d%02dp%d", year, day, part));
    Method solutionMain = solutionClass.getMethod("main", List.class, boolean.class);

    // Call the solution and time it.
    long tickStart = System.nanoTime();
    solutionMain.invoke(null, input, true); // 'true' means the solution will print output
    return System.nanoTime() - tickStart; // Return execution time of entire solution
  }

  // Same as RunSolution, except this one mutes the solutions' printing output and runs them many (`iterations`) times
  // RETURNS: an array containing the amount of time each iteration took to run in nanoseconds
  public static long[] BenchmarkSolution(int year, int day, int part, int test, int iterations) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    if(problemDoesntExist(year, day, part, test) || iterations < 1) { // Input validation
      return new long[] {-1};
    }

    // Load input for the problem and testcase.
    List<String> input = loadInput(year, day, part, test);

    // Do some voodoo to be able to call the solution's main method
    Class<?> solutionClass = Class.forName(String.format("y%d_d%02dp%d", year, day, part));
    Method solutionMain = solutionClass.getMethod("main", List.class,  boolean.class);

    long[] execTimes = new long[iterations];
    for(int i = 0; i < iterations; i++) { // Run the solution `iterations` times and record execution time of each iteration
      long tickStart = System.nanoTime();
      solutionMain.invoke(null, input, false); // 'false': Making the solution itself print stuff will needlessly slow things down, so don't for benchmarking
      execTimes[i] = System.nanoTime() - tickStart;

      // Print the amount of time this iteration took to execute in both milliseconds and microseconds as both may be useful.
      System.out.printf("Iteration " +
          String.format("%-" + Integer.toString(iterations).length() + "d", i + 1).replace(' ','.') +
          ": %.3f ms / %.1f μs\n", execTimes[i]*.000001, execTimes[i]*.001);
      // Explanation of the `String.format` line in the above mess: Java's printf does not support padding chars other than 0 and space.
      // So, I have to replace the spaces produced by printf with dots myself with `.replace`.
      // The format string left-aligns the iteration number integer, then pads it to the max number of digits any iteration # will have.
    }

    return execTimes; // Return array of all execution times
  }

  // Checks if the specified problem does not exist.
  // RETURNS: true if problem DOES NOT exist, false if it DOES (false is what we _want_)
  public static boolean problemDoesntExist(int year, int day, int part, int test) {
    //  * This project is designed to contain solutions to years 2015-2025
    //  * Years before 2025 have 25 problems per year; >= 2025 have 12 problems/year
    //  * Each problem always has 2 parts
    //  * There can be up to 10 valid values of `test` for each solution, where test 0 means to run it on official input from adventofcode.com.
    return year < 2015 || year > 2025 || day < 1 || (year < 2025 ? day > 25 : day > 12) || (part != 1 && part != 2) || test < 0 || test > 9;
  }

  // Loads input from a file into a List<String>
  // RETURNS: the List<String> containing input
  public static List<String> loadInput(int year, int day, int part, int test) {
    // Input filename format: input_YYYY_DD_P_T.txt where YYYY is the year, DD is a two-digit day #,
    // P is the part number (always 1 or 2), and T is the one-digit test number. If T == 0, it runs
    // the solution on actual input and not a test.
    List<String> input = List.of(); // Empty list
    try (Stream<String> lines = Files.lines(
        Path.of(String.format("input_%d_%02d_%d_%d.txt", year, day, part, test)))) {
      input = lines.toList();
    } catch (IOException e) {
      System.out.println("\n" + e.getMessage());
      System.out.println("\nFATAL ERROR: The input file was not found.");
    }

    return input;
  }
}
