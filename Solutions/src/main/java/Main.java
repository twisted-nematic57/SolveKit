import java.io.IOException;
import java.time.Instant;

/*** Main.java ****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-11-26                                                 *
 * Description:    Sets up the solution runner by recognizing the platform    *
 *                 type and setting inputs. Runs, tests, or benchmarks        *
 *                 solutions using reflection.                                *
\******************************************************************************/

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

    final boolean saveBenchResultsToCSV;
    if(args[0].contains("S")) { // We must save benchmark results to CSV
      saveBenchResultsToCSV = true;
      args[0] = args[0].replace("S", ""); // To make code down the line look a little cleaner
    } else {
      saveBenchResultsToCSV = false;
    }

    final int testNum = Integer.parseInt(args[0].substring(args[0].indexOf("-")+1, args[0].indexOf("-")+2));
    final boolean benchmarking = args[0].contains("B");
    final String platformName = args[0].substring(0, args[0].indexOf("."));

    try {
      if(!benchmarking) { // Not benchmarking, running only once
        SolutionSpecifier thisSolution = new SolutionSpecifier(
            args[0].substring(args[0].indexOf(".") + 1, args[0].indexOf("-")),
            testNum
        );
        long runtime = 0; // in nanoseconds
        switch (platformName) {
          case "AdventOfCode":
            AdventOfCodePlatformHandler thisProblem = new AdventOfCodePlatformHandler();
            String[] input = thisProblem.loadInput(thisSolution);
            runtime = thisProblem.runSolution(thisSolution); // Possible runtime exception for this is handled.

            // The solution will be responsible for printing its own output.
            break;
          case "LeetCode":
            break;
          case "ProjectEuler":
            break;
          case "CodeForces":
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
        SolutionSpecifier thisSolution = new SolutionSpecifier(
            args[0].substring(args[0].indexOf(".") + 1, args[0].indexOf("-")),
            testNum
        );
        long[] benchmarkRuntimes = new long[benchmarkingIterations];

        switch (platformName) {
          case "AdventOfCode":
            AdventOfCodePlatformHandler thisProblem = new AdventOfCodePlatformHandler();
            String[] input = thisProblem.loadInput(thisSolution);


            benchmarkRuntimes = thisProblem.benchmarkSolution(thisSolution, benchmarkingIterations);

            // If we're supposed to save the data to a CSV, then save it
            try {
              if(saveBenchResultsToCSV) {
                long now = Instant.now().getEpochSecond(); // Current Unix timestamp
                BenchmarkReporter.saveToCSV(benchmarkRuntimes, now);
                System.out.println("Benchmark results saved to runtimes_" + now + ".csv in inputs directory.\n");
              }
            } catch (IOException e) {
              System.out.println("Error: Couldn't save benchmark results to CSV. Error details:\n" + e.getMessage() + "\n");
            }

            break;
          case "LeetCode":
            break;
          case "ProjectEuler":
            break;
          case "CodeForces":
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

        // Print the pretty stats table
        BenchmarkReporter.showBenchmarkResults(allRuns, last80p);
      }
    } catch(IOException e) {
      System.out.println("\nError: The input file was not found. \n\nError details:\n" + e.getMessage());
    } catch(Exception e) {
      System.out.println("\nError: Failed to run the solution. If you are in IntelliJ IDEA, try clicking anywhere in the" +
          "solution source code window and try again. \n\nError details:\n" + e.getMessage());
    }

    /*
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
        System.out.println("\nThe solution couldn't be run. Are you sure the specified solution exists?");
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
    }*/
  }
}
