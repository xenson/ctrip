package com.ctrip.car.util.compress;

import com.ctrip.car.osd.framework.common.exception.CompressionException;
import com.ctrip.car.osd.framework.common.utils.compress.ZstdCompressor;
import org.junit.Assert;
import org.junit.Test;

public class ZstdCompressorTest {

    private final String origin = "{\"pickupRentalInfo\":{\"locationType\":5,\"cityId\":223,\"locationName\":\"莞潢南路20号\",\"poi\":{\"longitude\":113.74242594434658,\"latitude\":23.067176407662963},\"datetime\":\"2021-05-25 09:00:00\"},\"dropoffRentalInfo\":{\"locationType\":5,\"cityId\":223,\"locationName\":\"莞潢南路20号\",\"poi\":{\"longitude\":113.74242594434658,\"latitude\":23.067176407662963},\"datetime\":\"2021-06-25 09:00:00\"},\"requestHeader\":{\"requestId\":\"d5996e85-9c1c-44b5-9693-60e1a421dae2\",\"requestorNo\":17705,\"sourceFrom\":\"Hello_App\",\"targetCurrencyCode\":\"CNY\",\"preLang\":\"CN\",\"preLocale\":\"zh-CN\",\"residency\":\"CN\",\"age\":18,\"allianceInfo\":{\"allianceId\":1309490,\"sid\":4175104}}}";
    private final String compressed = "KLUv/QBQzQoA1lZLMXAJYgBgIwISE4DGkwAaInjsEfvcRD75o6W2t0XkKopZIP57d9/TukhxIcfCx04ALgRCAD0APABK8Vs6dzqYWZujNzxwSGtCwwOEBg4SKFSI4ODh/GaADKSsxZcLyJ3xqPG7gRRg6rwwVvoIvzaYBYWXaQKNoyDO5pG2JeyF+2hnQVvXaNSmWnn0PCVOiV8C0FbSUqJWWvP2hfLoUdxbzi2JuUUtLiGUdSE9SjTHFMZeW2yxBaEE1lHcdlh4j940AQ5USnvhfIZqBXS0Tn0ctbQsil8Ztn41lGoxqDq5RHnuJQ0BY0lU8SUt0e69Js+Xiwg/z/OwpNqiGF/Ox1F67CVF8WVHBk5K4VFC8dsBIZFpOR9tkwQMzm8KY3UwcEDb5ugGGIrzW1YFJDJtAw4AMwsYRJCFFJ2b12VmIDPN6MBao0kAQIQioJH7DG0gqCMuAecJhdoD";

    @Test
    public void compress2StringTest() throws CompressionException {
        String result = ZstdCompressor.INSTANCE.compress2String(origin);
        Assert.assertEquals(result, compressed);
    }

    @Test
    public void decompress2StringTest() throws CompressionException {
        String result = ZstdCompressor.INSTANCE.decompress2String(compressed);
        Assert.assertEquals(result, origin);
    }

}
