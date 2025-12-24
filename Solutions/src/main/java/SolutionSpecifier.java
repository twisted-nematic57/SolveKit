/*** SolutionSpecifier.java ***************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-23                                                 *
 * Description:    Defines a record that contains the name of a solution and  *
 *                 what test # we're going to run it on.                      *
\******************************************************************************/

public record SolutionSpecifier(String name, int test) { }
