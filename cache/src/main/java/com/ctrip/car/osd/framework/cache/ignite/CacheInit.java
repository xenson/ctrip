package com.ctrip.car.osd.framework.cache.ignite;

import com.ctrip.framework.vi.IgniteManager;

public interface CacheInit {
    String name();
    boolean init(IgniteManager.SimpleLogger logger);
}
