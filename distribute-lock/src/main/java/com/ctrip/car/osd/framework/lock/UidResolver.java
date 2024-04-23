package com.ctrip.car.osd.framework.lock;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * @author xh.gao
 * @createTime 2022年08月01日 18:28:00
 */
public class UidResolver {

    private static volatile boolean initialized = false;

    private static GetUidInterface uidService;

    static {
        init();
    }

    private static void init() {
        if (initialized) {
            return;
        }
        uidService = resolve(GetUidInterface.class);
        initialized = true;
    }

    private static <T> T resolve(Class<T> type) {
        ServiceLoader<T> loader = ServiceLoader.load(type);
        Iterator<T> iterator = loader.iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        System.err.println("module distribute-lock spi has no impl, type:" + type.getSimpleName());
        return null;
    }

    public static GetUidInterface getUidService() {
        return uidService;
    }
}
