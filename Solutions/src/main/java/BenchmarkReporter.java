/*** BenchmarkReporter.java ***************************************************\
 * Author:         twisted_nematic57                                          *
 * Date Created:   2025-12-22                                                 *
 * Description:    Displays a table of statistics. Designed to be used with   *
 *                 benchmarking data. Also implements support for saving      *
 *                 data to a CSV file if requested.                           *
\******************************************************************************/

import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;

public class BenchmarkReporter {
  public static void showBenchmarkResults(Statistics all, Statistics last80p) {
    // all contains statistical variables for all runs, and last80p contains those for the last 80% of runs.
    
    /* Expected datatypes & formats before printing begins:
     - Runs:      int, unitless
     - Mean:      long, nanoseconds
     - Min:       long, nanoseconds
     - Q1:        long, nanoseconds
     - Median:    long, nanoseconds
     - Q3:        long, nanoseconds
     - Max:       long, nanoseconds
     - Stddev:    long, nanoseconds
     - Σ(time):   Apint, nanoseconds
    Time sums are accompanied by sets of four ints representing hours, minutes, seconds and milliseconds.
    Individual time sum components are labeled with _h, _m, _s, and _ms respectively.
    All of the above are repeated once again for the last 80% of runs.
   
    Conversions:
    Nanosecond -> Microsecond: *.001
    Nanosecond -> Millisecond: *.000001
    Nanosecond -> Second     : *.000000001
   
    The goal is to have benchmark stats be printed in this pretty and predictable format:
    +-------------------------------------------------+-------------------------------------------------+
    | Benchmark results (runtime, all runs):          | Benchmark results (runtime, last 80% of runs):  |
    |  * Runs     : X[...]                            |  * Runs     : X[...]                            |
    |  * Mean     : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |  * Mean     : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |
    |-------------------------------------------------+-------------------------------------------------|
    |  * Min      : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |  * Min      : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |
    |  * Q1       : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |  * Q1       : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |
    |  * Median   : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |  * Median   : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |
    |  * Q3       : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |  * Q3       : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |
    |  * Max      : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |  * Max      : XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |
    |  * Stddev[σ]: XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |  * Stddev[σ]: XXXXXXX.XXX ms / XXXXXXXXXX.X μs  |
    |  * Σ(time)  : XXXXXXX.XXX  s /  HHHH:MM:SS.III  |  * Σ(time)  : XXXXXXX.XXX  s /  HHHH:MM:SS.III  |
    +-------------------------------------------------+-------------------------------------------------+
    */

    /* Please excuse the semi-spaghettiesque code below, but this will print a table in the above format exactly,
       assuming that no runs took more than 9999999 seconds (~2777 hours, or ~115 days).

       If your runs are taking longer than that, you are probably serious enough to use the CSV output option; just include
       "S" at the end of the command line argument or use the "Benchmark Solution -> CSV" Run/Debug config in IntelliJ. */

    System.out.println("+-------------------------------------------------+-------------------------------------------------+");
    System.out.println("| Benchmark results (runtime, all runs):          | Benchmark results (runtime, last 80% of runs):  |");
    System.out.printf ("|  * Runs     : %-32d  |  * Runs     : %-32d  |\n", all.getRuns(), last80p.getRuns());
    System.out.printf ("|  * Mean     : %-11.3f ms / %-12.1f µs  |  * Mean     : %-11.3f ms / %-12.1f µs  |\n", UnitConverter.ns_ms(all.getMean()), UnitConverter.ns_us(all.getMean()), UnitConverter.ns_ms(last80p.getMean()), UnitConverter.ns_us(last80p.getMean()));
    System.out.println("|-------------------------------------------------+-------------------------------------------------|");
    System.out.printf ("|  * Min      : %-11.3f ms / %-12.1f µs  |  * Min      : %-11.3f ms / %-12.1f µs  |\n", UnitConverter.ns_ms(all.getMin()), UnitConverter.ns_us(all.getMin()), UnitConverter.ns_ms(last80p.getMin()), UnitConverter.ns_us(last80p.getMin()));
    System.out.printf ("|  * Q1       : %-11.3f ms / %-12.1f µs  |  * Q1       : %-11.3f ms / %-12.1f µs  |\n", UnitConverter.ns_ms(all.getQ1()), UnitConverter.ns_us(all.getQ1()), UnitConverter.ns_ms(last80p.getQ1()), UnitConverter.ns_us(last80p.getQ1()));
    System.out.printf ("|  * Median   : %-11.3f ms / %-12.1f µs  |  * Median   : %-11.3f ms / %-12.1f µs  |\n", UnitConverter.ns_ms(all.getMedian()), UnitConverter.ns_us(all.getMedian()), UnitConverter.ns_ms(last80p.getMedian()), UnitConverter.ns_us(last80p.getMedian()));
    System.out.printf ("|  * Q3       : %-11.3f ms / %-12.1f µs  |  * Q3       : %-11.3f ms / %-12.1f µs  |\n", UnitConverter.ns_ms(all.getQ3()), UnitConverter.ns_us(all.getQ3()), UnitConverter.ns_ms(last80p.getQ3()), UnitConverter.ns_us(last80p.getQ3()));
    System.out.printf ("|  * Max      : %-11.3f ms / %-12.1f µs  |  * Max      : %-11.3f ms / %-12.1f µs  |\n", UnitConverter.ns_ms(all.getMax()), UnitConverter.ns_us(all.getMax()), UnitConverter.ns_ms(last80p.getMax()), UnitConverter.ns_us(last80p.getMax()));
    System.out.printf ("|  * Stddev[σ]: %-11.3f ms / %-12.1f µs  |  * Stddev[σ]: %-11.3f ms / %-12.1f µs  |\n", UnitConverter.ns_ms(all.getStddev()), UnitConverter.ns_us(all.getStddev()), UnitConverter.ns_ms(last80p.getStddev()), UnitConverter.ns_us(last80p.getStddev()));
    System.out.printf ("|  * Σ(time)  : %-11.3f  s /  %4d:%02d:%02d.%03d  |  * Σ(time)  : %-11.3f  s /  %4d:%02d:%02d.%03d  |\n", UnitConverter.ns_s(all.getTimeSum()), all.getTimeSum_h(), all.getTimeSum_m(), all.getTimeSum_s(), all.getTimeSum_ms(), UnitConverter.ns_s(last80p.getTimeSum()), last80p.getTimeSum_h(), last80p.getTimeSum_m(), last80p.getTimeSum_s(), last80p.getTimeSum_ms());
    System.out.println("+-------------------------------------------------+-------------------------------------------------+");
  }

  /* Saves all data points to a CSV file in the inputs directory. Appends the current Unix timestamp
     to the end of the filename to avoid filename conflicts. The current timestamp is an argument;
     it's not gotten from inside the method because it has to be synchronized with what the caller
     knows.

     (The inputs directory is configured to be the cwd by default, and it somewhat makes sense to
     store the CSV to that directory as the user will most likely spend a lot of time there editing
     test inputs.) */
  public static void saveToCSV(long[] data, long currentTime) throws Exception {
    RandomAccessFile stream = new RandomAccessFile("runtimes_" + currentTime + ".csv", "rw");
    FileChannel channel = stream.getChannel();
    FileLock lock = null;
    try {
      lock = channel.tryLock();

      // Save every element to a row in the CSV
      for(int i = 0; i < data.length; i++) {
        stream.write(Long.toString(data[i]).getBytes());
        stream.write("\n".getBytes());
      }
    } catch (final OverlappingFileLockException e) {
      stream.close();
      channel.close();
    }

    lock.release();
    stream.close();
    channel.close();
  }
}
