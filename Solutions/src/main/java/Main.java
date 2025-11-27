/*** Main.java ****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-11-26                                                 *
 * Date Modified:  2025-11-26                                                 *
 * File Type:      Java Source File                                           *
 * Description:    Sets up the AoC solution runner. Runs or tests solutions   *
 *                 as needed.                                                 *
\******************************************************************************/

public class Main {
  public static void main(String[] args) {
    System.out.println("Stop goofing around.\n");
  }

  public static int RunSolution(int year, int day, int part) {
    // Only run the solution if it has a valid corresponding AoC problem.
    //  * This project is designed to contain solutions to years 2015-2025
    //  * Years before 2025 have 25 problems per year; >= 2025 means 12 problems/year
    //  * Each problem always has 2 parts
    if(year < 2015 || year > 2025 || day < 1 || (year < 2025 ? day > 25 : day > 12) ||
        (part != 1 && part != 2)) {
      return -1;
    }


  }
}
