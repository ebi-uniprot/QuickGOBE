package uk.ac.ebi.quickgo.ff.phaseout;

import java.util.*;

public class CollectionUtils {
    public static Comparator<int[]> intArrayComparator = new Comparator<int[]>() {
        public int compare(int[] a, int[] b) {
            for (int i = 0; i < b.length; i++) {
                int c = a[i] - b[i];
                if (c != 0) {
                	return c;
                }
            }
            return 0;
        }
    };

    public static Map<String, String> arrayToMap(String[][] from) {
        Map<String, String> init = new HashMap<>();
        for (String[] s : from) {
            init.put(s[0], s[1]);
        }
        return init;
    }

    @SafeVarargs
	public static <X> Set<X> comparatorSet(Comparator<X> comparator, X... x) {
        Set<X> all = new TreeSet<>(comparator);
        all.addAll(Arrays.asList(x));
        return all;
    }

    public static Set<String> caseInsensitiveSet(String... values) {
        return comparatorSet(String.CASE_INSENSITIVE_ORDER, values);
    }

    public static int[] toIntArray(List<Integer> integers) {
        int[] x = new int[integers.size()];
        for (int i = 0; i < integers.size(); i++) {
        	x[i] = integers.get(i);
        }
        return x;
    }

    public static int argsMap(String[] args, int start, Map<String, String> options, List<String> parameters) {
        int index = start;

        while (index < args.length) {
            String arg = args[index];
            if (arg.startsWith("--")) {
                String[] nv = arg.substring(2).split("=", 2);
                String name = nv[0];
                String value = nv.length> 1 ? nv[1] : null;                
                options.put(name, value);
            }
            else {
                if (parameters != null) {
                	parameters.add(arg);
                }
            }
            index++;
        }
        return index;
    }

    @SuppressWarnings("rawtypes")
	public static Map<String,String> getFromParameterMap(Map parameterMap) {
        return URLUtils.getFromParameterMap(parameterMap);
    }

    public static interface StringFilter {
        String filter(String in);
    }

    public static StringFilter removePrefix(final String prefix) {
        return new StringFilter() {
            public String filter(String in) {
                return in.startsWith(prefix) ? in.substring(prefix.length()) : null;
            }
        };
    }

    public static StringFilter removePostfix(final String postfix) {
        return new StringFilter() {
            public String filter(String in) {
                return in.endsWith(postfix) ? in.substring(0, in.length() - postfix.length()) : null;
            }
        };
    }

    @SuppressWarnings("rawtypes")
	public static String dump(Map in) {
        if (in == null) {
        	return "null";
        }
        StringBuilder sb = new StringBuilder("{");
        for (Iterator it = in.keySet().iterator(); it.hasNext();) {
            String key = (String) it.next();
            sb.append(key).append(":").append(in.get(key));
            if (it.hasNext()) {
            	sb.append(", ");
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @SuppressWarnings("rawtypes")
	public static String printMap(Map in) {
        StringBuilder sb = new StringBuilder();
        for (Object o : in.keySet()) {
            String key = (String)o;
            sb.append(key).append(" : ").append(in.get(key));
            sb.append("\n");
        }

        return sb.toString();
    }

    public static String dump(List<?> list) {
        return list == null ? "null" : new StringBuffer("[").append(concat(list, ", ")).append("]").toString();
    }

    public static List<String> replace(List<String> list, String find, String replace) {
        List<String> l = new ArrayList<>();
        for (String aList : list) {
            l.add(aList.replaceAll(find, replace));
        }
        return l;
    }

    public static String concat(List<?> list, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            if (i != 0) {
            	sb.append(separator);
            }
            sb.append(list.get(i));
        }
        return sb.toString();
    }

    public static String concat(Iterable<?> list, String separator) {
        StringBuffer sb = new StringBuffer();
        int i = 0;
        for (Object o : list) {
            if (i != 0) {
            	sb.append(separator);
            }
            sb.append(o);
            i++;
        }
        return sb.toString();
    }

    public static String concat(Object[] array, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
            	sb.append(separator);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }

    public static String concat(int[] array, String separator) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            if (i != 0) {
            	sb.append(separator);
            }
            sb.append(array[i]);
        }
        return sb.toString();
    }


    public static String dump(int[] oar) {
        if (oar == null) {
        	return "null";
        }
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < oar.length; i++) {
            if (i != 0) {
            	sb.append(", ");
            }
            sb.append(oar[i]);

        }
        sb.append("]");
        return sb.toString();
    }

    public static String dump(Object[] oar) {
        if (oar == null) {
        	return "null";
        }
        StringBuffer sb = new StringBuffer("[");
        for (int i = 0; i < oar.length; i++) {
            if (i != 0) {
            	sb.append(", ");
            }
            sb.append(oar[i]);

        }
        sb.append("]");
        return sb.toString();
    }

    public static <X extends Enum<X>> X enumFind(String name, X fallback) {
        try {
            return Enum.valueOf(fallback.getDeclaringClass(), name);
        }
        catch (Exception e) {
            return fallback;
        }

    }

    public static <X extends Enum<X>> X enumFindIgnoreCase(String name, X fallback) {
        for (X x : fallback.getDeclaringClass().getEnumConstants()) {
            if (x.name().equalsIgnoreCase(name)) {
            	return x;
            }
        }
        return fallback;
    }
    


    public static <V> Map<String, V> keyFilter(Map<String, V> in, StringFilter sf) {
        Map<String, V> init = new HashMap<>();
        for (String key : in.keySet()) {
            String newKey = sf.filter(key);
            if (newKey != null) {
            	init.put(newKey, in.get(key));
            }
        }
        return init;
    }

/*    public static <K, X> Map<K, List<X>> arrayListHashMap() {
        return new AutoMap<K, List<X>>(new HashMap<K, List<X>>(), Creator.<X>arrayList());
    }

    public static <K extends Enum<K>, X> Map<K, X> autoEnumMap(Class<K> key, Class<X> value) {
        return new IterableValueMap<K, X>(new AutoMap<K, X>(new EnumMap<K, X>(key), Creator.reflective(value, key)));
    }

    public static class IterableValueMap<K, V> extends ProxyMap<K, V> implements Iterable<V> {
        public IterableValueMap(Map<K, V> base) {
            super(base);
        }

        public Iterator<V> iterator() {
            return values().iterator();
        }
    }

     public static Map<String, String> loadPropertyMap(URL from) throws IOException {
        return new HashMap<String, String>((Map) loadProperties(new Properties(), from));
    }

    public static Properties loadProperties(URL from) throws IOException {
        return loadProperties(new Properties(), from);
    }

    public static Properties loadProperties(Properties props, URL from) throws IOException {
        InputStream is = new BufferedInputStream(from.openStream());
        props.loader(is);
        is.close();
        return props;
    }
*/
}
