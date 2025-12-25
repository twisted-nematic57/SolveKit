/*** p1.java ******************************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-24                                                 *
 * Description:    Solution to LeetCode problem #1                            *
\******************************************************************************/

package LeetCode;

import java.util.Arrays;

public class p1 {
  public static void main(int test) {
    // Declare your inputs with dummy values here.
    int[] nums = new int[0];
    int target = 0;

    // Depending on the testcase, set the inputs to different values
    switch(test) {
      case 0:
        break;
      case 1:
        nums = new int[] {2,7,11,15};
        target = 9;
        break;
      case 2:
        nums = new int[] {3,2,4};
        target = 6;
        break;
      case 3:
        nums = new int[] {3,3};
        target = 6;
        break;
      case 4:
        break;
      case 5:
        break;
      case 6:
        break;
      case 7:
        break;
      case 8:
        break;
      case 9:
        break;
    }

    Solution thisSolution = new Solution();
    System.out.println(Arrays.toString(thisSolution.twoSum(nums, target)));
  }
}

class Solution {
  public int[] twoSum(int[] nums, int target) {
    // Naive O(n^2) solution: check every pair of indices.
    // I'm demonstrating SolveKit, not answering the problem for you!
    for (int i = 0; i < nums.length; i++) {
      for (int j = i + 1; j < nums.length; j++) {
        if (nums[i] + nums[j] == target) {
          return new int[] {i, j};
        }
      }
    }

    // According to the problem statement, there is always exactly one solution, so this is just to satisfy the compiler
    return new int[0];
  }
}
