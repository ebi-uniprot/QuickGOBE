package uk.ac.ebi.quickgo.util;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;

/**
 * @Author Tony Wardell
 * Date: 16/07/2015
 * Time: 13:58
 * Created with IntelliJ IDEA.
 */

public class CPUUtils {

	/** Get CPU time in nanoseconds. */
	public static long getCpuTime( ) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported( ) ?
				bean.getCurrentThreadCpuTime( ) : 0L;
	}

	/** Get user time in nanoseconds. */
	public static long getUserTime( ) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported( ) ?
				bean.getCurrentThreadUserTime( ) : 0L;
	}

	/** Get system time in nanoseconds. */
	public static long getSystemTime( ) {
		ThreadMXBean bean = ManagementFactory.getThreadMXBean( );
		return bean.isCurrentThreadCpuTimeSupported( ) ?
				(bean.getCurrentThreadCpuTime( ) - bean.getCurrentThreadUserTime( )) : 0L;
	}

}
