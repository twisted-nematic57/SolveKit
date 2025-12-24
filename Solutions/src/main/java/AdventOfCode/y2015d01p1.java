/*** y2015d01p1.java **********************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-11-26                                                 *
 * Description:    Solution to Advent of Code 2015 Day 1 Part 1               *
\******************************************************************************/

package AdventOfCode;

import java.util.List;

public class y2015d01p1 {
  public static void main(List<String> input) {
    int floor = 0;
    for(int step = 0; step < input.getFirst().length(); step++) {
      if(input.getFirst().charAt(step) == '(') {
        floor++;
      } else {
        floor--;
      }

      System.out.println("Step = " + step + "; Current floor = " + String.format("% d", floor));
    }

    System.out.println("\nFinal floor = " + floor);
  }
}

