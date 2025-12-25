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

public class Main {
  static void main(String[] args) throws Exception {
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

    final String[] supportedPlatforms = new String[] {"AdventOfCode", "LeetCode", "ProjectEuler", "Codeforces",
        "AtCoder", "SPOJ", "UVa"};

    final boolean saveBenchResultsToCSV;
    if(args[0].contains("S")) { // We must save benchmark results to CSV
      saveBenchResultsToCSV = true;
      args[0] = args[0].replace("S", ""); // To make code down the line look a little cleaner
    } else {
      saveBenchResultsToCSV = false;
    }

    final int testNum;
    final boolean benchmarking;
    final String platformName;
    try {
      testNum = Integer.parseInt(args[0].substring(args[0].indexOf("-")+1, args[0].indexOf("-")+2));
      benchmarking = args[0].contains("B");
      platformName = args[0].substring(0, args[0].indexOf("."));
    } catch(Exception e) {
      System.out.println("Error parsing arguments.\n\nError details:\n" + e.getMessage());
      return;
    }


    try {
      // Input validation
      if(testNum < 0 || testNum > 9) { // Is the test # in [0, 9]?
        throw new IllegalSpecifierException("Invalid test number. Test numbers must be integers in the range [0, 9].");
      }

      if(!(Arrays.stream(supportedPlatforms).toList().contains(platformName))) { // Is the requested platform invalid?
        throw new IllegalSpecifierException("Invalid platform name. Platform name must be one of the following:\n" +
            "\"AdventOfCode\", \"LeetCode\", \"ProjectEuler\", \"Codeforces\", \"AtCoder\", \"SPOJ\", \"UVa\"");
      }

      SolutionSpecifier thisSolution = new SolutionSpecifier(
          args[0].substring(args[0].indexOf(".") + 1, args[0].indexOf("-")),
          testNum
      );

      // Solution running
      if(!benchmarking) { // Not benchmarking, running only once
        long runtime = 0; // in nanoseconds
        switch (platformName) {
          case "AdventOfCode":
            AdventOfCodePlatformHandler aoc = new AdventOfCodePlatformHandler();
            runtime = aoc.runSolution(thisSolution);

            // The solution will be responsible for printing its own output.
            break;
          case "LeetCode":
            LeetCodePlatformHandler lc = new LeetCodePlatformHandler();
            runtime = lc.runSolution(thisSolution);

            // The main method of the solution is responsible for printing a return value.
            break;
          case "ProjectEuler":
            break;
          case "Codeforces":
            break;
          case "AtCoder":
            break;
          case "SPOJ":
            break;
          case "UVa":
            break;
        }

        System.out.println("\n---------------------------------------------------");
        System.out.printf("Runtime: %.1f μs / %.3f ms", UnitConverter.ns_us(runtime), UnitConverter.ns_ms(runtime));

      } else { // Benchmarking mode

        int benchmarkingIterations = Integer.parseInt(args[0].substring(args[0].indexOf("B") + 1));
        if(benchmarkingIterations <= 2) {
          throw new IllegalSpecifierException("Benchmarking iterations must be > 2");
        }

        long[] benchmarkRuntimes = new long[benchmarkingIterations];

        switch (platformName) {
          case "AdventOfCode":
            AdventOfCodePlatformHandler aoc = new AdventOfCodePlatformHandler();
            benchmarkRuntimes = aoc.benchmarkSolution(thisSolution, benchmarkingIterations);
            break;
          case "LeetCode":
            LeetCodePlatformHandler lc = new LeetCodePlatformHandler();
            benchmarkRuntimes = lc.benchmarkSolution(thisSolution, benchmarkingIterations);
            break;
          case "ProjectEuler":
            break;
          case "Codeforces":
            break;
          case "AtCoder":
            break;
          case "SPOJ":
            break;
          case "UVa":
            break;
        }

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
        } // End of benchmarking handling
      } // End of solution runner
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
