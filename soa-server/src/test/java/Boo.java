import com.ctriposs.baiji.rpc.common.types.BaseRequest;
import com.ctriposs.baiji.rpc.common.types.BaseResponse;

public class Boo {
    BaseRequest baseRequest;
    BaseResponse abc;
    String orderId;
    String userID;
    Long cost;

    public BaseRequest getBaseRequest() {
        return baseRequest;
    }

    public void setBaseRequest(BaseRequest baseRequest) {
        this.baseRequest = baseRequest;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public BaseResponse getAbc() {
        return abc;
    }

    public void setAbc(BaseResponse abc) {
        this.abc = abc;
    }

    public Long getCost() {
        return cost;
    }

    public void setCost(Long cost) {
        this.cost = cost;
    }
}
