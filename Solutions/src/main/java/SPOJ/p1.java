/*** p1.java ******************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-25                                                 *
 * Description:    Solution to SPOJ problem 1                                 *
\******************************************************************************/

package SPOJ;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;

public class p1 {
  public static void main() throws IOException {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    String line;
    while ((line = br.readLine()) != null) {
      line = line.trim();
      if (line.isEmpty()) continue;          // skip blank lines
      int n = Integer.parseInt(line);
      if (n == 42) break;                    // stop at 42
      System.out.println(n);                 // output the number
    }
  }
}
