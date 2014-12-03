package uk.ac.ebi.quickgo.util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class TimedStorage<X> implements Iterable<X> {
    public List<X> recent = new ArrayList<>();
    public List<X> old = new ArrayList<>();
    private long previousSwap;
    private long horizon;
    public long swapInterval = 30000;

    public TimedStorage(Interval swapInterval) {
        this.swapInterval = swapInterval.getMillis();
        horizon = System.currentTimeMillis();
        previousSwap = horizon;
    }

    public void add(X a) {
        recent.add(a);
        vacuum();
    }

    public void vacuum() {
        long now = System.currentTimeMillis();
        if (now - previousSwap > swapInterval) {
            horizon = previousSwap;
            previousSwap = now;
            old.clear();
            List<X> x = recent;
            recent = old;
            old = x;
        }
    }

    public long age() {
        return System.currentTimeMillis()-horizon;
    }

    public Iterator<X> iterator() {
        vacuum();
        final Iterator<X> rc1 = recent.iterator();
        final Iterator<X> rc2 = old.iterator();

        return new Iterator<X>() {
            public boolean hasNext() {
                return rc1.hasNext() || rc2.hasNext();
            }
            public X next() {
                return rc1.hasNext() ? rc1.next() : rc2.next();
            }
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public int size() {
        return recent.size() + old.size();
    }
}
