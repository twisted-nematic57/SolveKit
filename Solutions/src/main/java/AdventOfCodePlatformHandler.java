/*** AdventOfCodePlatformHandler.java *****************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-22                                                 *
 * Description:    Handles running and benchmarking solutions to Advent of    *
 *                 Code problems.                                             *
\******************************************************************************/

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class AdventOfCodePlatformHandler implements PlatformHandler {
  // Load input from an input file into an array of strings.
  @Override
  public String[] loadInput(SolutionSpecifier thisSolution) {
    List<String> input = List.of(); // Empty list
    try (Stream<String> lines = Files.lines(
        Path.of("adventofcode/i_" + thisSolution.name() + "_" + thisSolution.test() + ".txt"))) {
      input = lines.toList();
    } catch (IOException e) {
      System.out.println("\nCouldn't load input.\n" + e.getMessage());
    }

    return input.toArray(new String[input.size()]);
  }

  @Override
  public long runSolution(SolutionSpecifier thisSolution) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
    // Load input for the problem and testcase.
    String[] input = loadInput(thisSolution);

    // Do some reflection voodoo to be able to call the solution's main method
    Class<?> solutionClass = Class.forName("AdventOfCode." + thisSolution.name());
    Method solutionMain = solutionClass.getMethod("main", String[].class);

    // Call the solution and time it.
    long tickStart = System.nanoTime();
    solutionMain.invoke(null, input);
    return System.nanoTime() - tickStart; // Return execution time of entire solution
  }

  @Override
  public long[] benchmarkSolution(SolutionSpecifier thisSolution, int iterations) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Load input for the problem and testcase.
    String[] input = loadInput(thisSolution);

    // Do some reflection voodoo to be able to call the solution's main method
    Class<?> solutionClass = Class.forName("AdventOfCode." + thisSolution.name());
    Method solutionMain = solutionClass.getMethod("main", String[].class);

    // Do not use the JVM argument --add-opens when running benchmarks. It is a legacy argument meant to maintain
    // backwards compatibility with old code and is completely irrelevant to programming puzzle solutions.
    solutionMain.setAccessible(true); // Make reflection-based method calling faster (won't work with --add-opens JVM arg)

    PrintStream originalOut = System.out; // We're going to be enabling and disabling console output in the near future.
    PrintStream originalErr = System.err;

    long[] execTimes = new long[iterations];
    for(int i = 0; i < iterations; i++) { // Run the solution `iterations` times and record execution time of each iteration
      // Disable console output to increase performance and ignore non-algorithmic runtime
      System.setOut(new PrintStream(OutputStream.nullOutputStream()));
      System.setErr(new PrintStream(OutputStream.nullOutputStream()));

      try {
        long tickStart = System.nanoTime(); // Begin timing
        solutionMain.invoke(null, input);
        execTimes[i] = System.nanoTime() - tickStart; // End timing
      } finally {
        System.setOut(originalOut); // Restore console printing functionality for stats printing of this run
        System.setErr(originalErr);
      }

      // Print the amount of time this iteration took to execute in both milliseconds and microseconds, as both may be useful.
      // The format string left-aligns the iteration number integer, then pads it to the max number of digits any iteration # will have.
      System.out.printf("Iteration " +
          String.format("%-" + Integer.toString(iterations).length() + "d", i + 1) + // Iteration #
          ": %.3f ms / %.1f µs\n", UnitConverter.ns_ms(execTimes[i]), UnitConverter.ns_us(execTimes[i])); // Times
    }

    return execTimes; // Return array of all execution times
  }
}
