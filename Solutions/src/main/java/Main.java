/*** Main.java ****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-11-26                                                 *
 * Description:    Sets up the solution runner by recognizing the platform    *
 *                 type and setting inputs. Runs, tests, or benchmarks        *
 *                 solutions using reflection.                                *
\******************************************************************************/

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

public class Main {
  static void main(String[] args) throws Exception {
    // Arg (singular) will look like this: "yYYYY_dXXpY_Z[BN[...]]"
    //  * YYYY is the year
    //  * XX is the day (always 2 digits)
    //  * Y is the part number (always == 1 or 2)
    //  * Z is the test number (always 1 digit)
    //  * If the letter "B" is present after Z, we need to benchmark the solution N times, where N
    //    is an integer in the range [0, (2^31)-1].

    // TODO: later when benchmarking code is factored out, pass this as an arg to a method in BenchmarkReporter
    boolean saveBenchResultsToCSV;
    if(args[0].contains("S")) { // We must save benchmark results to CSV
      saveBenchResultsToCSV = true;
      args[0] = args[0].replace("S", ""); // To make code down the line look a little cleaner
    } else {
      saveBenchResultsToCSV = false;
    }

    if(!args[0].contains("B")) { // --- RUNNING SOLUTION ONCE ---
      long runtime = RunSolution(
          Integer.parseInt(args[0].substring(1, 5)),       // Year
          Integer.parseInt(args[0].substring(7, 9)),       // Day
          Integer.parseInt(args[0].substring(10, 11)),     // Part
          Integer.parseInt(args[0].substring(12, 13)));    // Test

      if(runtime != -1) { // In general, if something == -1 in the runner then something has gone wrong; likely an inexistent problem has been input.
        System.out.println("\n---------------------------------------------------");
        System.out.printf("Runtime: %.3f ms", UnitConverter.ns_ms(runtime));
      } else {
        System.out.println("\nSomething went wrong and the solution couldn't be run. Are you sure the specified problem exists?");
      }
    } else { // --- BENCHMARKING ---
      System.out.println("Initializing benchmarking...");

      long[] benchResults = BenchmarkSolution(
          Integer.parseInt(args[0].substring(1, 5)),                      // Year
          Integer.parseInt(args[0].substring(7, 9)),                      // Day
          Integer.parseInt(args[0].substring(10, 11)),                    // Part
          Integer.parseInt(args[0].substring(12, 13)),                    // Test
          Integer.parseInt(args[0].substring(args[0].indexOf("B") + 1))); // # of iterations

      if(benchResults[0] != -1) {
        System.out.println("\n---------------------------------------------------");
        System.out.println("Calculating statistics...\n");
        // Process benchmarking results and statistics:
        // One about the entire list of exec times and one about the last 80% (to account for JVM warmup, optimization & stabilization)

        // If we're supposed to save the data to a CSV, then save it
        try {
          if(saveBenchResultsToCSV) {
            long now = Instant.now().getEpochSecond(); // Current Unix timestamp
            BenchmarkReporter.saveToCSV(benchResults, now);
            System.out.println("Benchmark results saved to runtimes_" + now + ".csv in inputs directory.\n");
          }
        } catch (IOException e) {
          System.out.println("Couldn't save benchmark results to CSV. Error details:\n" + e.getMessage() + "\n");
        }

        // Make an array of length 80% of benchResults
        long[] benchResults_last80p = new long[(int)(Math.ceil(benchResults.length*0.8))];
        // Copy the last 80% of elements of benchResults to benchResults_last80p
        System.arraycopy(benchResults, (int)(Math.floor(benchResults.length*0.2)), benchResults_last80p, 0, benchResults_last80p.length);

        // Compute statistical variables on our runtime data
        Statistics allRuns = new Statistics(benchResults);
        Statistics last80p = new Statistics(benchResults_last80p);

        // Print the pretty stats table
        BenchmarkReporter.showBenchmarkResults(allRuns, last80p);
      } else {
        System.out.println("\nThe solution couldn't be run. Are you sure the specified solution exists?");
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

    // Do some reflection voodoo to be able to call the solution's main method
    Class<?> solutionClass = Class.forName(String.format("y%d_d%02dp%d", year, day, part));
    Method solutionMain = solutionClass.getMethod("main", List.class, boolean.class);
    solutionMain.setAccessible(true); // Make reflection-based method calling faster (you SHOULD NOT BE USING --add-opens)

    long[] execTimes = new long[iterations];
    for(int i = 0; i < iterations; i++) { // Run the solution `iterations` times and record execution time of each iteration
      long tickStart = System.nanoTime();
      solutionMain.invoke(null, input, false); // 'false': Making the solution itself print stuff will needlessly slow things down, so don't for benchmarking
      execTimes[i] = System.nanoTime() - tickStart;

      // Print the amount of time this iteration took to execute in both milliseconds and microseconds as both may be useful.
      // The format string left-aligns the iteration number integer, then pads it to the max number of digits any iteration # will have.
      System.out.printf("Iteration " +
          String.format("%-" + Integer.toString(iterations).length() + "d", i + 1) +
          ": %.3f ms / %.1f µs\n", UnitConverter.ns_ms(execTimes[i]), UnitConverter.ns_us(execTimes[i]));
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
