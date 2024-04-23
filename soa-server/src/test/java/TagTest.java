import com.ctrip.car.osd.framework.common.utils.LogTagUtil;
import com.ctriposs.baiji.rpc.common.types.BaseRequest;

import java.util.Map;

public class TagTest {
    public static void main(String[] args) {
        Boo boo = new Boo();
        BaseRequest baseRequest = new BaseRequest();
        baseRequest.setRequestId("request");
        boo.setBaseRequest(baseRequest);
        boo.setOrderId("order");
        boo.setUserID("user");
        boo.setCost(32323L);
        Map<String, String> abc = LogTagUtil.getindexTags("abc", boo,boo);
    }
}
