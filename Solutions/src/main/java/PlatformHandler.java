/*** PlatformHandler.java *****************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-22                                                 *
 * Description:    Defines an interface that enables implementation of        *
 *                 support for new platforms. Each platform has a slightly    *
 *                 different way of doing things, e.g. I/O and expected       *
 *                 submission formats. This interface tries to encapsulate    *
 *                 perfectly the differences in behavior that exist between   *
 *                 those platforms, and lets the developers implement support *
 *                 for new ones easily.                                       *
\******************************************************************************/

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

public interface PlatformHandler {
  // Used for platforms that rely on plaintext input via a file
  default void loadInput(SolutionSpecifier thisSolution) throws IOException { }

  // Used for platforms that rely on stdin for input
  default void passStdInput() { }

  // Runs the solution once. Returns the runtime of the solution in ns.
  long runSolution(SolutionSpecifier thisSolution) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException, IOException;

  // Runs the solution `iterations` times. Returns an array containing runtimes for each iteration.
  long[] benchmarkSolution(SolutionSpecifier thisSolution, int iterations) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, IOException;
}
