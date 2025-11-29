/*** Main.java ****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-11-26                                                 *
 * Description:    Sets up the AoC solution runner. Runs or tests solutions   *
 *                 as needed.                                                 *
\******************************************************************************/

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Main {
  public static void main(String[] args) throws IOException, ClassNotFoundException, InvocationTargetException, NoSuchMethodException, IllegalAccessException {
    // Args will look like this: "yYYYY_dXXpY_Z"
    //  * YYYY is the year
    //  * XX is the day (2 digits)
    //  * Y is the part number
    //  * Z is the test number (inputted via string input dialog from IDE, not part of a filename)
    long runtime = RunSolution(
        Integer.parseInt(args[0].substring(1, 5)), // Year
        Integer.parseInt(args[0].substring(7, 9)),      // Day
        Integer.parseInt(args[0].substring(10, 11)),    // Part
        Integer.parseInt(args[0].substring(12, 13)));   // Test

    System.out.println("\n---------------------------------------------------");
    System.out.printf("Runtime: %.3f ms", runtime*.000001); // Convert to milliseconds before printing, easier to read
  }


  // Runs a specific solution on a certain input (test #).
  //  year,day,part: specifies the specific solution to run
  //  test: specifies the test # to run. If == 0, runs the solution on official input from
  //        adventofcode.com
  // RETURNS: the amount of time in nanoseconds the solution took to execute
  public static long RunSolution(int year, int day, int part, int test) throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    // Only run the solution if it has a valid corresponding AoC problem.
    //  * This project is designed to contain solutions to years 2015-2025
    //  * Years before 2025 have 25 problems per year; >= 2025 have 12 problems/year
    //  * Each problem always has 2 parts
    if(year < 2015 || year > 2025 || day < 1 || (year < 2025 ? day > 25 : day > 12) || (part != 1 && part != 2)) {
      return -1;
    }

    // Load input for the problem and testcase.
    // Input filename format: input_yYYYY_DD_P_T[...].txt where YYYY is the year, DD is a two-digit
    // day #, P is the part number (always 1 or 2), and T[...] is the test number, which can be any
    // # of digits >= 1. If T == 0, it runs the solution on actual input and not a test.
    List<String> input = List.of();
    try (Stream<String> lines = Files.lines(
        Path.of(String.format("input_%d_%02d_%d_%d.txt", year, day, part, test)))) {
      input = lines.toList();
    } catch (IOException e) {
      System.out.println("\n" + e.getMessage());
      System.out.println("\nFATAL ERROR: The input file was not found.");
    }

    // Do some voodoo to be able to call the solution's main method
    Class<?> solutionClass = Class.forName(String.format("y%d_d%02dp%d", year, day, part));
    Method solutionMain = solutionClass.getMethod("main", List.class);

    // Call it and time it.
    long tickStart = System.nanoTime();
    solutionMain.invoke(null, input); // The solution body is responsible for printing its output.
    long tickEnd = System.nanoTime();

    return tickEnd - tickStart; // Return execution time of entire solution
  }
}
