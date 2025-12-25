/*** ABC424A.java *************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-24                                                 *
 * Description:    Solution to AtCoder problem ABC424A                        *
\******************************************************************************/

package AtCoder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class ABC424A {
  public static void main() throws Exception {
    BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
    StringTokenizer st = new StringTokenizer(br.readLine());

    int a = Integer.parseInt(st.nextToken());
    int b = Integer.parseInt(st.nextToken());
    int c = Integer.parseInt(st.nextToken());

    // An isosceles triangle has at least two equal sides
    if (a == b || a == c || b == c) {
      System.out.println("Yes");
    } else {
      System.out.println("No");
    }
  }
}
