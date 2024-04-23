package com.ctriposs.baiji.rpc.common.types;

import com.ctriposs.baiji.exception.BaijiRuntimeException;
import com.ctriposs.baiji.schema.Field;
import com.ctriposs.baiji.schema.RecordSchema;
import com.ctriposs.baiji.schema.Schema;
import com.ctriposs.baiji.specific.SpecificRecord;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

import java.io.Serializable;

@SuppressWarnings("all")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE) 
@JsonPropertyOrder({
    "isSuccess",
    "code",
    "returnMsg",
    "requestId",
    "cost"
})
public class BaseResponse implements SpecificRecord, Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public static final transient Schema SCHEMA = Schema.parse("{\"type\":\"record\",\"name\":\"BaseResponse\",\"namespace\":\"com.ctriposs.baiji.rpc.common.types\",\"doc\":null,\"fields\":[{\"name\":\"IsSuccess\",\"type\":[\"boolean\",\"null\"]},{\"name\":\"Code\",\"type\":[\"string\",\"null\"]},{\"name\":\"ReturnMsg\",\"type\":[\"string\",\"null\"]},{\"name\":\"RequestId\",\"type\":[\"string\",\"null\"]},{\"name\":\"Cost\",\"type\":[\"long\",\"null\"]}]}");

    @Override
    @JsonIgnore
    public Schema getSchema() { return SCHEMA; }

    public BaseResponse(
        Boolean isSuccess,
        String code,
        String returnMsg,
        String requestId,
        Long cost) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.returnMsg = returnMsg;
        this.requestId = requestId;
        this.cost = cost;
    }

    public BaseResponse() {
    }

    @JsonProperty("isSuccess") 
    private Boolean isSuccess;

    @JsonProperty("code") 
    private String code;

    @JsonProperty("returnMsg") 
    private String returnMsg;

    @JsonProperty("requestId") 
    private String requestId;

    @JsonProperty("cost") 
    private Long cost;

    public Boolean isIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(final Boolean isSuccess) {
        this.isSuccess = isSuccess;
    }
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }
    public String getReturnMsg() {
        return returnMsg;
    }

    public void setReturnMsg(final String returnMsg) {
        this.returnMsg = returnMsg;
    }
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }
    public Long getCost() {
        return cost;
    }

    public void setCost(final Long cost) {
        this.cost = cost;
    }

    // Used by DatumWriter. Applications should not call.
    public Object get(int fieldPos) {
        switch (fieldPos) {
            case 0: return this.isSuccess;
            case 1: return this.code;
            case 2: return this.returnMsg;
            case 3: return this.requestId;
            case 4: return this.cost;
            default: throw new BaijiRuntimeException("Bad index " + fieldPos + " in get()");
        }
    }

    // Used by DatumReader. Applications should not call.
    @SuppressWarnings(value="unchecked")
    public void put(int fieldPos, Object fieldValue) {
        switch (fieldPos) {
            case 0: this.isSuccess = (Boolean)fieldValue; break;
            case 1: this.code = (String)fieldValue; break;
            case 2: this.returnMsg = (String)fieldValue; break;
            case 3: this.requestId = (String)fieldValue; break;
            case 4: this.cost = (Long)fieldValue; break;
            default: throw new BaijiRuntimeException("Bad index " + fieldPos + " in put()");
        }
    }

    @Override
    public Object get(String fieldName) {
        Schema schema = getSchema();
        if (!(schema instanceof RecordSchema)) {
            return null;
        }
        Field field = ((RecordSchema) schema).getField(fieldName);
        return field != null ? get(field.getPos()) : null;
    }

    @Override
    public void put(String fieldName, Object fieldValue) {
        Schema schema = getSchema();
        if (!(schema instanceof RecordSchema)) {
            return;
        }
        Field field = ((RecordSchema) schema).getField(fieldName);
        if (field != null) {
            put(field.getPos(), fieldValue);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;

        final BaseResponse other = (BaseResponse)obj;
        return 
            Objects.equal(this.isSuccess, other.isSuccess) &&
            Objects.equal(this.code, other.code) &&
            Objects.equal(this.returnMsg, other.returnMsg) &&
            Objects.equal(this.requestId, other.requestId) &&
            Objects.equal(this.cost, other.cost);
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = 31 * result + (this.isSuccess == null ? 0 : this.isSuccess.hashCode());
        result = 31 * result + (this.code == null ? 0 : this.code.hashCode());
        result = 31 * result + (this.returnMsg == null ? 0 : this.returnMsg.hashCode());
        result = 31 * result + (this.requestId == null ? 0 : this.requestId.hashCode());
        result = 31 * result + (this.cost == null ? 0 : this.cost.hashCode());

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("isSuccess", isSuccess)
            .add("code", code)
            .add("returnMsg", returnMsg)
            .add("requestId", requestId)
            .add("cost", cost)
            .toString();
    }
}
