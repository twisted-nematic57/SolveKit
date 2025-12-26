/*** IllegalSpecifierException.java *******************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-24                                                 *
 * Description:    Defines an exception that is thrown when an incorrect test *
 *                 # / number of benchmarking iterations / unimplemented      *
 *                 platform is provided.                                      *
\******************************************************************************/

public class IllegalSpecifierException extends RuntimeException {
  public IllegalSpecifierException(String message) {
    super(message);
  }
}
