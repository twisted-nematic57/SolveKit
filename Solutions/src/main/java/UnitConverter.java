/*** UnitConverter.java *******************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-22                                                 *
 * Description:    Implements some convenient methods that convert units of   *
 *                 time to make benchmark results easier to read.             *
\******************************************************************************/

import org.apfloat.Apfloat;
import org.apfloat.Apint;

public class UnitConverter {
  // Nanoseconds -> Microseconds
  public static double ns_us(long ns) {
    return ns*.001;
  }

  // Nanoseconds -> Milliseconds
  public static double ns_ms(long ns) {
    return ns*.000001;
  }

  // Nanoseconds -> Seconds but with an Apint input
  public static double ns_s(Apint ns) {
    return Double.parseDouble(ns.multiply(new Apfloat(".000000001", 20)).toString());
  }
}
