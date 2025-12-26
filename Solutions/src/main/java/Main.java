/*** Main.java ****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-11-26                                                 *
 * Description:    Sets up the solution runner by recognizing the platform    *
 *                 type and setting inputs. Runs, tests, or benchmarks        *
 *                 solutions using reflection.                                *
\******************************************************************************/

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.Map;

public class Main {
  static void main(String[] args) {
    // Arg (singular) will look like this: "{Platform}.{Specifier}-Z[BN[...][S]]"
    //  * {Platform} is a valid Java package name. It is the name of the platform that hosted the programming problem
    //    to which the solution is implemented. Supported values: "AdventOfCode", "LeetCode", "ProjectEuler",
    //    "Codeforces", "AtCoder", "SPOJ", "UVa".
    //  * {Specifier} is a valid Java class name. It is the name of the class in which there is a main method which we
    //    must execute. It should be the same as the identifier for the problem that the solution is written for.
    //  * Z is the test number (always 1 digit, 0-9)
    //  * If the letter B is present after Z, we need to benchmark the solution N times, where N is an integer in the
    //    range [0, (2^31)-1].
    //  * If the letter S is at the end and B is also present, benchmark timing data will be saved to a CSV in ./inputs.

    final boolean saveBenchResultsToCSV;
    if(args[0].charAt(args[0].length() - 1) == 'S') { // We must save benchmark results to CSV if there is an S at the end of the arg
      saveBenchResultsToCSV = true;
      args[0] = args[0].substring(0, args[0].length()-1); // Chop off the S to make code down the line look a little cleaner
    } else {
      saveBenchResultsToCSV = false;
    }

    Map<String, PlatformHandler> handlers = Map.of(
        "AdventOfCode", new AdventOfCodePlatformHandler(),
        "LeetCode", new LeetCodePlatformHandler(),
        "ProjectEuler", new ProjectEulerPlatformHandler(),
        "Codeforces", new CodeforcesPlatformHandler(),
        "AtCoder", new AtCoderPlatformHandler(),
        "SPOJ", new SPOJPlatformHandler(),
        "UVa", new UVaPlatformHandler()
    );

    final int testNum;
    final boolean benchmarking;
    final String platformName;
    try {
      // Extract the platform name (only occurs before the ".")
      platformName = args[0].substring(0, args[0].indexOf("."));

      // Extract the one-digit test number from the character that comes after "-"
      testNum = Integer.parseInt(args[0].substring(args[0].indexOf("-")+1, args[0].indexOf("-")+2));

      // Extract the benchmark flag from the part of the string after the "-"
      benchmarking = args[0].substring(args[0].indexOf("-")+1).contains("B");
    } catch(Exception e) {
      System.out.println("Error parsing arguments.\nIf you are in IntelliJ IDEA, try clicking anywhere in the solution " +
          "source code window and try again.\n\nError details:\n" + e.getMessage());
      return;
    }

    PlatformHandler handler = handlers.get(platformName);

    try {
      // Input validation
      if(testNum < 0 || testNum > 9) { // Is the test # in [0, 9]?
        throw new IllegalSpecifierException("Invalid test number. Test numbers must be integers in the range [0, 9].");
      }

      if(!handlers.containsKey(platformName)) { // Is the requested platform invalid?
        throw new IllegalSpecifierException("Invalid platform name. Platform name must be one of the following:\n" +
            String.join(", ", handlers.keySet())); // Print all valid platforms
      }

      SolutionSpecifier thisSolution = new SolutionSpecifier(
          args[0].substring(args[0].indexOf(".") + 1, args[0].indexOf("-")),
          testNum
      );

      // Solution running
      if(!benchmarking) { // We are running the solution only once
        long runtime = handler.runSolution(thisSolution);
        System.out.println("\n---------------------------------------------------");
        System.out.printf("Runtime: %.1f μs / %.3f ms", UnitConverter.ns_us(runtime), UnitConverter.ns_ms(runtime));
      } else { // Benchmarking
        /* Extract the number of benchmarking iterations from the command line arg:
           The number will always occur 2 spaces after the dash and ends at the end of the arg because we chopped
           the S off earlier, if it was there to begin with.
         */
        int benchmarkingIterations = Integer.parseInt(args[0].substring(args[0].indexOf("-")+3));
        if(benchmarkingIterations <= 2) { // Statistical calculation code glitches if there are less than 3 data points
          throw new IllegalSpecifierException("Benchmarking iterations must be > 2");
        }

        long[] benchmarkRuntimes = handler.benchmarkSolution(thisSolution, benchmarkingIterations);

        System.out.println("Computing statistics...");

        // Make an array of length 80% of benchmarkRuntimes
        long[] benchmarkRuntimes_last80p = new long[(int)(Math.ceil(benchmarkRuntimes.length*0.8))];
        // Copy the last 80% of elements of benchmarkRuntimes to benchmarkRuntimes_last80p
        System.arraycopy(benchmarkRuntimes, (int)(Math.floor(benchmarkRuntimes.length*0.2)), benchmarkRuntimes_last80p, 0, benchmarkRuntimes_last80p.length);

        // Compute statistical variables on our runtime data
        Statistics allRuns = new Statistics(benchmarkRuntimes);
        Statistics last80p = new Statistics(benchmarkRuntimes_last80p);

        // Repeat info about the solution being benchmarked
        System.out.println("\nBenchmarking results for solution " + platformName + "." + thisSolution.name() + ":");

        // Print the pretty stats table
        BenchmarkReporter.showBenchmarkResults(allRuns, last80p);

        // If we're supposed to save the data to a CSV, then save it
        try {
          if(saveBenchResultsToCSV) {
            long now = Instant.now().getEpochSecond(); // Current Unix timestamp
            BenchmarkReporter.saveToCSV(benchmarkRuntimes, now);
            System.out.println("\nBenchmark results saved to runtimes_" + now + ".csv in inputs directory.");
          }
        } catch (IOException e) {
          System.out.println("Error: Couldn't save benchmark results to CSV. Error details:\n" + e.getMessage() + "\n");
        }
      }
    } catch(IOException e) {
      System.out.println("\nError: The input file couldn't be opened. Does it exist?\n" + e.getMessage());
    } catch(IllegalSpecifierException e) {
      System.out.println("\nError: Incorrect arguments were provided to SolveKit.\n\nDetails:\n" + e.getMessage() +
          "\n\nIf you are in IntelliJ IDEA, try clicking anywhere in the solution source code window and try again.");
    } catch(Exception e) {
      System.out.println("\nError: Failed to run the solution. If you are in IntelliJ IDEA, try clicking anywhere in the" +
          " solution source code window and try again.\n\nError details:\n" + e.getMessage());
    }
  }
}
