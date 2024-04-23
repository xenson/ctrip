package com.ctrip.car.util.compress;

import com.ctrip.car.osd.framework.common.exception.CompressionException;
import com.ctrip.car.osd.framework.common.utils.compress.GzipCompressor;
import org.junit.Assert;
import org.junit.Test;

public class GzipCompressorTest {

    private final String origin = "{\"pickupRentalInfo\":{\"locationType\":5,\"cityId\":223,\"locationName\":\"莞潢南路20号\",\"poi\":{\"longitude\":113.74242594434658,\"latitude\":23.067176407662963},\"datetime\":\"2021-05-25 09:00:00\"},\"dropoffRentalInfo\":{\"locationType\":5,\"cityId\":223,\"locationName\":\"莞潢南路20号\",\"poi\":{\"longitude\":113.74242594434658,\"latitude\":23.067176407662963},\"datetime\":\"2021-06-25 09:00:00\"},\"requestHeader\":{\"requestId\":\"d5996e85-9c1c-44b5-9693-60e1a421dae2\",\"requestorNo\":17705,\"sourceFrom\":\"Hello_App\",\"targetCurrencyCode\":\"CNY\",\"preLang\":\"CN\",\"preLocale\":\"zh-CN\",\"residency\":\"CN\",\"age\":18,\"allianceInfo\":{\"allianceId\":1309490,\"sid\":4175104}}}";
    private final String compressed = "H4sIAAAAAAAAANVRu07DMBT9FeQ5qWzHdupsqBJqJdQBsTAh49wGizQOrjOUKp8AjGwM/ABjJz6nqJ9Ru6+BP0Dy4HPv8bnnXK9Qa/RT195A41U9aWYWFStUW628sc3tsgVU8ARp45eTEhWUZsm5O1Xz0EXbt8/fn6/N68d2/U3x5n2NEtRac9BpKuO7MtAIyQY5o4xyyVjGBB8GoSBz6NJsgEVOcsFwLgSVIusTVCoP3uxnUExJinlK+QWWBcbhoMhwtrWz2b8wL/6ad/DcwcKPQZXg4sBjIVpFJZdSwJCnUhOdMvYQbkJmqcBAFKOkVEDRWcO6aYhO8hyHuAvbOQ1Xzs6Dzhjq2t5ftm0ge+Uq8KPOOWj0cmSjdzSa3sXIDq5VU+3xEYY91ZHw8pjuaw4WpowPTyRVxc2ETai6NqrRcPqAMw5BSIYlkzi4MgExknOCWd/3O63yfpR4AgAA";

    @Test
    public void compress2StringTest() throws CompressionException {
        String result = GzipCompressor.INSTANCE.compress2String(origin);
        Assert.assertEquals(result, compressed);
    }

    @Test
    public void decompress2StringTest() throws CompressionException {
        String result = GzipCompressor.INSTANCE.decompress2String(compressed);
        Assert.assertEquals(result, origin);
    }

}
