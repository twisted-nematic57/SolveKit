/*** p1A.java *****************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-24                                                 *
 * Description:    Solution to Codeforces problem 1A                          *
\******************************************************************************/

package Codeforces;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class p1A {
  public static void main() throws Exception {
    FastScanner fs = new FastScanner();
    long n = fs.nextLong();
    long m = fs.nextLong();
    long a = fs.nextLong();

    long horizontal = (n + a - 1) / a;
    long vertical = (m + a - 1) / a;
    long result = horizontal * vertical;

    System.out.println(result);
  }

  // Simple fast scanner for integers
  private static class FastScanner {
    private final BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    private StringTokenizer st;

    long nextLong() throws IOException {
      while (st == null || !st.hasMoreElements()) {
        String line = br.readLine();
        if (line == null) return -1;
        st = new StringTokenizer(line);
      }
      return Long.parseLong(st.nextToken());
    }
  }
}
