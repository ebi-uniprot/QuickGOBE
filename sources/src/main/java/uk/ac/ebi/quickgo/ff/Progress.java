package uk.ac.ebi.quickgo.ff;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Progress {
    public static final int BYTES_IN_MB = 1048576;
    long time;
    int count;
    long startMemory;
    private String name;
    Runtime rt = Runtime.getRuntime();
    RowReader.ProgressMonitor monitor;

    private static final Logger logger = LoggerFactory.getLogger(Progress.class);


    public Progress(RowReader.ProgressMonitor monitor, String name) {
        this(name);
        this.monitor = monitor;
    }

    public Progress(String name) {
        this.name = name;
        reset();
    }

    public void reset() {
        count = 0;
        logger.info(name + ":");
        time = System.nanoTime();

        startMemory = memory();
    }

    public void next() {
        if ((count++) % 100000 ==0) {
            long memory = memory();
            if (monitor != null) {
                long now = System.nanoTime();
                double pct = monitor.getFraction();
                String estimate = (pct == 0) ? "" : (int)((now - time) / pct * (1 - pct) / Interval.SECOND_NS) + "s";
                logger.info("[" + (int) (pct * 100) + "%" + estimate + " Memory: " + toMegaBytes(memory) + "]");
            }
            else {
                logger.info("[" + count / 100000 + ":" + " Memory: " + toMegaBytes(memory) + "]");
            }
        }
    }

    private String toMegaBytes(long memory) {
        return memory / BYTES_IN_MB + "MB";
    }

    private long memory() {
        //rt.gc();
        long memory = (rt.totalMemory() - rt.freeMemory());
        return memory;
    }

    public void end() {
    	time = System.nanoTime() - time;
        long usedMemory = memory() - startMemory;
        String message = "Total Time:" + time / Interval.SECOND_NS + "s "
                + "records: " + count
                + " " + (count == 0 ? "" : time / count + "ns per record; "
                + usedMemory / count + " bytes each");
        logger.info(message);
    }


    public static RowReader monitor(String name, final RowReader.MonitorRowReader in) {
        final Progress p = new Progress(in, name);
        return new RowReader() {
            public boolean read(String[] data) throws Exception {
                p.next();
                return in.read(data);
            }

            public void open() throws Exception {
                in.open();
                p.reset();
            }

            public void close() throws Exception {
                p.end();
                in.close();
            }

            public String[] getColumns() throws Exception {
                return in.getColumns();
            }
        };
    }
}
