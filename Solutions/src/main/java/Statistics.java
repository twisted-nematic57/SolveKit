/*** Statistics.java **********************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-22                                                 *
 * Description:    The Statistics class holds stats variables and implements  *
 *                 methods that does math on a dataset of longs to calculate  *
 *                 the appropriate values for those stat vars.                *
\******************************************************************************/

import org.apfloat.Apint; // For accurate statistics calculations
import org.apfloat.ApintMath;

import java.util.Arrays;

public class Statistics {
  private int runs; // == "n" in stats formulas as seen in textbooks
  private long mean;
  private long min;
  private long q1;
  private long median;
  private long q3;
  private long max;
  private long stddev; // Population, not sample, since sample makes no sense for our case
  private Apint timeSum;
  private int timeSum_h;
  private int timeSum_m;
  private int timeSum_s;
  private int timeSum_ms;

  // As soon as the class is constructed, calculate statistics about it.
  // data is expected to be an unsorted array of longs.
  public Statistics(long[] data) {
    // Sort the data ascending (the least element goes to position 0, etc.)
    data = Arrays.stream(data).sorted().toArray();

    runs = data.length; // Total number of data points
    min = data[0]; // Minimum
    max = data[runs-1]; // Maximum

    timeSum = new Apint(0); // Sum of all runtimes
    for(int i = 0; i < runs; i++) {
      timeSum = timeSum.add(new Apint(data[i]));
    }
    mean = Long.parseLong(timeSum.divide(new Apint(runs)).toString()); // Mean

    // Quartile formula: `(x/4)*(n+1)`th term, where x = 1 for Q1, x = 2 for median, and x = 3 for Q3
    // Form used below: `(n+1)*(x/4)` where `x/4` is in decimal form
    final double q1pos = ((runs+1)*0.25)-1; // Doubles are accurate enough in [2^31 - 1, 2^32 - 1] for this usecase.
    final double medianpos = ((runs+1)*0.50)-1; // -1 at the end because arrays are zero-indexed. The quartile formula
    final double q3pos = ((runs+1)*0.75)-1;     // only gives answers assuming 1-indexing of elements

    if(q1pos - (int)q1pos < 0.1) { // Q1 is at data[q1pos] exactly.
      q1 = data[(int)q1pos];
    } else { // If Q1 isn't exactly at q1pos, average the values adjacent to it
      q1 = (data[(int)(q1pos+1)] + data[(int)q1pos]) / 2;
    }

    if(medianpos - (int)medianpos < 0.1) { // Median is at data[medianpos] exactly.
      median = data[(int)medianpos];
    } else { // If median isn't exactly at medianpos, average the values adjacent to it
      median = (data[(int)(medianpos+1)] + data[(int)medianpos]) / 2;
    }

    if(q3pos - (int)q3pos < 0.1) { // Q3 is at data[q3pos] exactly.
      q3 = data[(int)q3pos];
    } else { // If Q3 isn't exactly at q3pos, average the values adjacent to it
      q3 = (data[(int)(q3pos+1)] + data[(int)q3pos]) / 2;
    }

    // Population standard deviation calculation
    Apint unnormalizedVariance = new Apint(0);
    for(int i = 0; i < runs; i++) { // Compute unnormalized variance = Σ(X - μ)^2
      unnormalizedVariance = unnormalizedVariance
          .add(ApintMath.pow(new Apint(data[i] - mean), 2));
    }

    // Calculate stddev to 20 sigfigs (we can't possibly need more than 20 right?)
    // Computes sqrt(u/runs) where u = unnormalized variance; array access [0] because we don't care about the
    // remainder from integer square rooting (since we're dealing in nanoseconds, it isn't significant)
    stddev = Long.parseLong(ApintMath.sqrt(unnormalizedVariance.divide(new Apint(runs)))[0].toString());

    Apint remainder = timeSum;
    timeSum_h = Integer.parseInt(remainder.divide(new Apint("3600000000000")).toString());
    remainder = remainder.mod(new Apint("3600000000000"));
    timeSum_m = Integer.parseInt(remainder.divide(new Apint("60000000000")).toString());
    remainder = remainder.mod(new Apint("60000000000"));
    timeSum_s = Integer.parseInt(remainder.divide(new Apint("1000000000")).toString());
    remainder = remainder.mod(new Apint("1000000000"));
    timeSum_ms = Integer.parseInt(remainder.divide(new Apint("1000000")).toString());
  }

  public int getRuns() {
    return runs;
  }

  public long getMean() {
    return mean;
  }

  public long getMin() {
    return min;
  }

  public long getQ1() {
    return q1;
  }

  public long getMedian() {
    return median;
  }

  public long getQ3() {
    return q3;
  }

  public long getMax() {
    return max;
  }

  public long getStddev() {
    return stddev;
  }

  public Apint getTimeSum() {
    return timeSum;
  }

  public int getTimeSum_h() {
    return timeSum_h;
  }

  public int getTimeSum_m() {
    return timeSum_m;
  }

  public int getTimeSum_s() {
    return timeSum_s;
  }

  public int getTimeSum_ms() {
    return timeSum_ms;
  }
}
