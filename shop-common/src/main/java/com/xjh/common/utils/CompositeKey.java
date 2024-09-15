package com.xjh.common.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.xjh.common.utils.CommonUtils.sizeOf;

public class CompositeKey {
    List<Object> keys = new ArrayList<>();

    public static CompositeKey create(Object... objects) {
        CompositeKey ck = new CompositeKey();
        ck.keys.addAll(Arrays.asList(objects));
        return ck;
    }

    public int compare(CompositeKey other) {
        if (other == null) {
            return 1;
        }
        int otherSize = sizeOf(other.keys);
        for (int index = 0; index < keys.size(); index++) {
            Object this_ = keys.get(index);
            if (!(this_ instanceof Comparable)) {
                return -1;
            }
            Object o = (index < otherSize) ? other.keys.get(index) : null;
            if (!(o instanceof Comparable)) {
                return 1;
            }
            int c = ((Comparable) this_).compareTo(o);
            if (c != 0) {
                return c;
            }
        }
        return 0;
    }

    public boolean startWith(CompositeKey prefix) {
        if (prefix == null) {
            return false;
        }
        if (sizeOf(keys) < sizeOf(prefix.keys)) {
            return false;
        }
        for (int i = 0; i < sizeOf(prefix.keys); i++) {
            if (!Objects.equals(keys.get(i), prefix.keys.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof CompositeKey)) {
            return false;
        }
        CompositeKey ock = (CompositeKey) o;
        if (sizeOf(keys) == 0 || sizeOf(keys) != sizeOf(ock.keys)) {
            return false;
        }
        for (int i = 0; i < keys.size(); i++) {
            if (!Objects.equals(keys.get(i), ock.keys.get(i))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hc = 17;
        if (sizeOf(keys) == 0) {
            return CompositeKey.class.hashCode();
        }
        for (Object k : keys) {
            int khash = (k != null ? k.hashCode() : 31);
            hc = 31 * hc + khash;
        }
        return hc;
    }

}
