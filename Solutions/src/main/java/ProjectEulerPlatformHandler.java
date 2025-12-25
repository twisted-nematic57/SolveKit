/*** ProjectEulerPlatformHandler.java *****************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-22                                                 *
 * Description:    Handles running and benchmarking solutions to Project      *
 *                 Euler problems.                                            *
\******************************************************************************/

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProjectEulerPlatformHandler implements PlatformHandler {
  @Override
  public long runSolution(SolutionSpecifier thisSolution) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, IOException {
    // Do some reflection voodoo to be able to call the solution's main method
    Class<?> solutionClass = Class.forName("ProjectEuler." + thisSolution.name());
    Method solutionMain = solutionClass.getMethod("main"); // There is no such thing as testing on different inputs for Project Euler solutions, so no int.class.

    // Call the solution and time it.
    long tickStart = System.nanoTime();
    solutionMain.invoke(null);
    return System.nanoTime() - tickStart; // Return execution time of entire solution
  }

  @Override
  public long[] benchmarkSolution(SolutionSpecifier thisSolution, int iterations) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException {
    // Do some reflection voodoo to be able to call the solution's main method
    Class<?> solutionClass = Class.forName("ProjectEuler." + thisSolution.name());
    Method solutionMain = solutionClass.getMethod("main");

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
        solutionMain.invoke(null);
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
