/*** Main.java ****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-11-26                                                 *
 * Description:    Sets up the AoC solution runner. Runs, tests, or           *
 *                 benchmarks solutions as needed.                            *
\******************************************************************************/

import org.apfloat.Apfloat; // For accurate statistics calculations
import org.apfloat.ApfloatMath;
import org.apfloat.Apint;

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
    //  * If the letter "B" is present after Z, we need to benchmark the solution N times, where N
    //    is an integer of whatever length.

    if(!args[0].contains("B")) { // --- RUNNING SOLUTION ONCE ---
      long runtime = RunSolution(
          Integer.parseInt(args[0].substring(1, 5)),       // Year
          Integer.parseInt(args[0].substring(7, 9)),       // Day
          Integer.parseInt(args[0].substring(10, 11)),     // Part
          Integer.parseInt(args[0].substring(12, 13)));    // Test

      if(runtime != -1) { // In general, if something == -1 in the runner then something has gone wrong; likely an inexistent problem has been input.
        System.out.println("\n---------------------------------------------------");
        System.out.printf("Runtime: %.3f ms", runtime*.000001); // Convert to milliseconds before printing, easier to read
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
        // Process benchmarking results and statistics:
        // One about the entire list of exec times and one about the last 80% (to account for JVM warmup, optimization & stabilization)

        // --- DATA PROCESSING ---
        // last80p = "last 80%"

        // * DBG: dummy data for accuracy testing
        benchResults = new long[]{1, 4, 16, 32, 64};
        int runs = benchResults.length;







        // Make an array of length 80% of benchResults
        long[] benchResults_last80p = new long[(int)(Math.ceil(runs*0.8))];
        int runs_last80p = benchResults_last80p.length;
        // Copy the last 80% of elements of benchResults to benchResults_last80p
        System.arraycopy(benchResults, (int)(Math.floor(runs*0.2)), benchResults_last80p, 0, runs_last80p);

        // Sort the results ascending (the least element goes to position 0, etc.)
        benchResults = Arrays.stream(benchResults).sorted().toArray();
        benchResults_last80p = Arrays.stream(benchResults_last80p).sorted().toArray();

        // Statistics calculations
        long min = benchResults[0]; // Minimum
        long max = benchResults[runs-1]; // Maximum

        Apint timeSum = new Apint(0); // Sum of all runtimes
        for(int i = 0; i < runs; i++) {
          timeSum = timeSum.add(new Apint(benchResults[i]));
        }
        long mean = Long.parseLong(timeSum.divide(new Apint(runs)).toString()); // Mean

        // TEMP: use dummy values to test printing system
        //int runs = 9189;
        //long mean = 83675700;
        //long min = 53345700;
        long q1 = 81283300;
        long median = 102467700;
        long q3 = 129848400;
        //long max = 152467600;
        long stddev = 5567;
        //Apint timeSum = new Apint("123456786789"); // in nanoseconds
        Apint remainder = timeSum;
        int timeSum_h = Integer.parseInt(remainder.divide(new Apint("3600000000000")).toString());
        remainder = remainder.mod(new Apint("3600000000000"));
        short timeSum_m = (short)Integer.parseInt(remainder.divide(new Apint("60000000000")).toString());
        remainder = remainder.mod(new Apint("60000000000"));
        short timeSum_s = (short)Integer.parseInt(remainder.divide(new Apint("1000000000")).toString());
        remainder = remainder.mod(new Apint("1000000000"));
        short timeSum_ms = (short)Integer.parseInt(remainder.divide(new Apint("1000000")).toString());

        Apint timeSquaredSum = new Apint("123456786789036789");
        remainder = timeSquaredSum;
        int timeSquaredSum_h = Integer.parseInt(remainder.divide(new Apint("3600000000000")).toString());
        remainder = remainder.mod(new Apint("3600000000000"));
        short timeSquaredSum_m = (short)Integer.parseInt(remainder.divide(new Apint("60000000000")).toString());
        remainder = remainder.mod(new Apint("60000000000"));
        short timeSquaredSum_s = (short)Integer.parseInt(remainder.divide(new Apint("1000000000")).toString());
        remainder = remainder.mod(new Apint("1000000000"));
        short timeSquaredSum_ms = (short)Integer.parseInt(remainder.divide(new Apint("1000000")).toString());

        int runs_last80p = 7678;
        long mean_last80p = 83675700;
        long min_last80p = 53345700;
        long q1_last80p = 81283300;
        long median_last80p = 102467700;
        long q3_last80p = 129848400;
        long max_last80p = 152467600;
        long stddev_last80p = 5567;
        Apint timeSum_last80p = new Apint("123456786789");
        remainder = timeSum_last80p;
        int timeSum_last80p_h = Integer.parseInt(remainder.divide(new Apint("3600000000000")).toString());
        remainder = remainder.mod(new Apint("3600000000000"));
        short timeSum_last80p_m = (short)Integer.parseInt(remainder.divide(new Apint("60000000000")).toString());
        remainder = remainder.mod(new Apint("60000000000"));
        short timeSum_last80p_s = (short)Integer.parseInt(remainder.divide(new Apint("1000000000")).toString());
        remainder = remainder.mod(new Apint("1000000000"));
        short timeSum_last80p_ms = (short)Integer.parseInt(remainder.divide(new Apint("1000000")).toString());
        
        Apint timeSquaredSum_last80p = new Apint("123456786789036789");
        remainder = timeSquaredSum_last80p;
        int timeSquaredSum_last80p_h = Integer.parseInt(remainder.divide(new Apint("3600000000000")).toString());
        remainder = remainder.mod(new Apint("3600000000000"));
        short timeSquaredSum_last80p_m = (short)Integer.parseInt(remainder.divide(new Apint("60000000000")).toString());
        remainder = remainder.mod(new Apint("60000000000"));
        short timeSquaredSum_last80p_s = (short)Integer.parseInt(remainder.divide(new Apint("1000000000")).toString());
        remainder = remainder.mod(new Apint("1000000000"));
        short timeSquaredSum_last80p_ms = (short)Integer.parseInt(remainder.divide(new Apint("1000000")).toString());


        // --- PRINTING ---

        /* Expected datatypes & formats before printing begins:
         - Runs:      int, unitless
         - Mean:      long, nanoseconds
         - Min:       long, nanoseconds
         - Q1:        long, nanoseconds
         - Median:    long, nanoseconds
         - Q3:        long, nanoseconds
         - Max:       long, nanoseconds
         - Stddev:    long, nanoseconds
         - Σ(time):   Apint, nanoseconds
         - Σ(time^2): Apint, nanoseconds (technically squared, but we don't care)
        Time sums are accompanied by sets of four ints representing hours, minutes, seconds and milliseconds.
        All of the above are repeated once again for the last 80% of runs.

        Conversions:
        Nanosecond -> Microsecond: *.001
        Nanosecond -> Millisecond: *.000001
        Nanosecond -> Second     : *.000000001

        The goal is to have benchmark stats be printed in this pretty and predictable format:
        ┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓
        ┃ Benchmark results (runtime, all runs):          ┃ Benchmark results (runtime, last 80% of runs):  ┃
        ┃  * Runs     : X[...]                            ┃  * Runs     : X[...]                            ┃
        ┃  * Mean     : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃  * Mean     : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃
        ┠─────────────────────────────────────────────────╂─────────────────────────────────────────────────┨
        ┃  * Min      : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃  * Min      : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃
        ┃  * Q1       : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃  * Q1       : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃
        ┃  * Median   : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃  * Median   : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃
        ┃  * Q3       : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃  * Q3       : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃
        ┃  * Max      : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃  * Max      : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃
        ┃  * Stddev   : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃  * Stddev   : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  ┃
        ┃  * Σ(time)  : XXXXXXX.XXX s /   HHHH:MM:SS.III  ┃  * Σ(time)  : XXXXXXX.XXX s /   HHHH:MM:SS.III  ┃
        ┃  * Σ(time^2): XXXXXXXXX.X s / HHHHHH:MM:SS.III  ┃  * Σ(time^2): XXXXXXXXX.X s / HHHHHH:MM:SS.III  ┃
        ┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛
        */

        System.out.println("\n");
        System.out.println("┏━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┳━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┓");
        System.out.println("┃ Benchmark results (runtime, all runs):          ┃ Benchmark results (runtime, last 80% of runs):  ┃");
        System.out.printf ("┃  * Runs     : %-32d  ┃  * Runs     : %-32d  ┃\n", runs, runs_last80p);
        System.out.printf ("┃  * Mean     : %-11.3f ms / %-12.1f μs  ┃  * Mean     : %-11.3f ms / %-12.1f μs  ┃\n", mean*.000001, mean*.001, mean_last80p*.000001, mean_last80p*.001);
        System.out.println("┠─────────────────────────────────────────────────╂─────────────────────────────────────────────────┨");
        System.out.printf ("┃  * Min      : %-11.3f ms / %-12.1f μs  ┃  * Min      : %-11.3f ms / %-12.1f μs  ┃\n", min*.000001, min*.001, min_last80p*.000001, min_last80p*.001);
        System.out.printf ("┃  * Q1       : %-11.3f ms / %-12.1f μs  ┃  * Q1       : %-11.3f ms / %-12.1f μs  ┃\n", q1*.000001, q1*.001, q1_last80p*.000001, q1_last80p*.001);
        System.out.printf ("┃  * Median   : %-11.3f ms / %-12.1f μs  ┃  * Median   : %-11.3f ms / %-12.1f μs  ┃\n", median*.000001, median*.001, median_last80p*.000001, median_last80p*.001);
        System.out.printf ("┃  * Q3       : %-11.3f ms / %-12.1f μs  ┃  * Q3       : %-11.3f ms / %-12.1f μs  ┃\n", q3*.000001, q3*.001, q3_last80p*.000001, q3_last80p*.001);
        System.out.printf ("┃  * Max      : %-11.3f ms / %-12.1f μs  ┃  * Max      : %-11.3f ms / %-12.1f μs  ┃\n", max*.000001, max*.001, max_last80p*.000001, max_last80p*.001);
        System.out.printf ("┃  * Stddev   : %-11.3f ms / %-12.1f μs  ┃  * Stddev   : %-11.3f ms / %-12.1f μs  ┃\n", stddev*.000001, stddev*.001, stddev_last80p*.000001, stddev_last80p*.001);
        System.out.printf ("┃  * Σ(time)  : %-11.3f s /   %4d:%02d:%02d.%03d  ┃  * Σ(time)  : %-11.3f s /   %4d:%02d:%02d.%03d  ┃\n", Double.parseDouble(timeSum.multiply(new Apfloat(".000000001", 10)).toString()), timeSum_h, timeSum_m, timeSum_s, timeSum_ms, Double.parseDouble(timeSum_last80p.multiply(new Apfloat(".000000001", 10)).toString()), timeSum_last80p_h, timeSum_last80p_m, timeSum_last80p_s, timeSum_last80p_ms);
        System.out.printf ("┃  * Σ(time^2): %-11.1f s / %6d:%02d:%02d.%03d  ┃  * Σ(time^2): %-11.1f s / %6d:%02d:%02d.%03d  ┃\n", Double.parseDouble(timeSquaredSum.multiply(new Apfloat(".000000001", 10)).toString()), timeSquaredSum_h, timeSquaredSum_m, timeSquaredSum_s, timeSquaredSum_ms, Double.parseDouble(timeSquaredSum_last80p.multiply(new Apfloat(".000000001", 10)).toString()), timeSquaredSum_last80p_h, timeSquaredSum_last80p_m, timeSquaredSum_last80p_s, timeSquaredSum_last80p_ms);        System.out.println("┗━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┻━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━┛");
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

  // Median of a subarray [l, r] inclusive
  static long medianOf(long[] arr, int l, int r) {
    int len = r - l + 1;
    int mid = l + len/2;
    if (len % 2 == 0) {
      return (arr[mid] + arr[mid - 1]) / 2;
    } else {
      return arr[mid];
    }
  }

}
