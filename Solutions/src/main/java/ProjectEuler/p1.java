/*** p1.java ******************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-24                                                 *
 * Description:    Solution to Project Euler problem #1                       *
\******************************************************************************/

package ProjectEuler;

import org.apfloat.Apint; // It's a good idea to use arbitrary-precision math for Project Euler problems.

public class p1 {
  public static void main() {
    Apint sum = new Apint(0);
    for(int i = 1; i < 1000; i++) {
      if(i % 3 == 0 || i % 5 == 0) {
        sum = sum.add(new Apint(i));
      }
    }

    System.out.println("Sum of all natural numbers in [1,1000) that are multiples of {3,5}:\n" + sum);
  }
}
