/*** Main.java ****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-11-26                                                 *
 * Description:    Sets up the AoC solution runner. Runs or tests solutions   *
 *                 as needed.                                                 *
\******************************************************************************/

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

public class Main {
  public static void main(String[] args) {
    System.out.println("Stop goofing around.\n");
  }

  // Runs a specific solution on a certain input (test #).
  //  year,day,part: specifies the specific solution to run
  //  test: specifies the test # to run. If == 0, runs the solution on official input from
  //        adventofcode.com
  // RETURNS: the amount of time in nanoseconds the solution took to execute
  public static long RunSolution(int year, int day, int part, int test) throws IOException {
    // Only run the solution if it has a valid corresponding AoC problem.
    //  * This project is designed to contain solutions to years 2015-2025
    //  * Years before 2025 have 25 problems per year; >= 2025 means 12 problems/year
    //  * Each problem always has 2 parts
    if(year < 2015 || year > 2025 || day < 1 || (year < 2025 ? day > 25 : day > 12) ||
        (part != 1 && part != 2)) {
      return -1;
    }

    // Input filename format: input_YYYY-D[D]-P-T[...].txt where YYYY is the year, D[D] is a one or
    // two-digit day #, P is the part number (always 1 or 2), and T[...] is the test number, which
    // can be any # of digits >= 1. If T is 0, it runs the solution on actual input and not a test.
    List<String> input;
    try (Stream<String> lines = Files.lines(
        Path.of(String.format("input_%d-%d-%d-%d.txt", year, day, part, test)))) {
      input = lines.toList();
    }

    return -2; // TEMP
  }
}
