package uk.ac.ebi.quickgo.util;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.Map;

public class WeakValueMap<K,V> extends AbstractReferenceValueMap<K,V> {
    public WeakValueMap(Map<K, Reference<V>> underlying, Interval interval) {
        super(underlying, interval);
    }

    public WeakValueMap(Map<K, Reference<V>> underlying) {
        super(underlying);
    }

    public WeakValueMap(Interval interval) {
        super(interval);
    }

    public WeakValueMap() {
    }

    public V put(K key, V value) {
        return putReference(key, new WeakReference<V>(value));
    }
}
