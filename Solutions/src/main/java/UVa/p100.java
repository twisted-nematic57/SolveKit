/*** p100.java ****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-25                                                 *
 * Description:    Solution to UVa problem 100                                *
\******************************************************************************/

package UVa;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class p100 {
  public static void main() throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String line;
    StringBuilder out = new StringBuilder();

    while ((line = br.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) continue;          // skip empty lines
      StringTokenizer st = new StringTokenizer(line);
      int i = Integer.parseInt(st.nextToken());
      int j = Integer.parseInt(st.nextToken());

      int start = Math.min(i, j);
      int end   = Math.max(i, j);

      int maxCycle = 0;
      for (int n = start; n <= end; n++) {
        int cl = cycleLength(n);
        if (cl > maxCycle) maxCycle = cl;
      }

      out.append(i).append(' ').append(j).append(' ')
          .append(maxCycle).append('\n');
    }

    System.out.print(out.toString());
  }

  // Compute the cycle length for a single number n
  private static int cycleLength(int n) {
    int count = 1;          // the starting n itself
    long value = n;         // use long to avoid overflow during the steps
    while (value != 1) {
      if ((value & 1) == 1) {     // odd
        value = 3 * value + 1;
      } else {                    // even
        value /= 2;
      }
      count++;
    }
    return count;
  }
}
