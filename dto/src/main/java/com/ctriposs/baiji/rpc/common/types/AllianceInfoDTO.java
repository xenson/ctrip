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

/**
 * 分销基本信息
 */
@SuppressWarnings("all")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE) 
@JsonPropertyOrder({
    "allianceId",
    "ouid",
    "sid",
    "distributorOrderId",
    "distributorUID",
    "distributorChannelId"
})
public class AllianceInfoDTO implements SpecificRecord, Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public static final transient Schema SCHEMA = Schema.parse("{\"type\":\"record\",\"name\":\"AllianceInfoDTO\",\"namespace\":\"com.ctriposs.baiji.rpc.common.types\",\"doc\":null,\"fields\":[{\"name\":\"AllianceId\",\"type\":[\"int\",\"null\"]},{\"name\":\"Ouid\",\"type\":[\"string\",\"null\"]},{\"name\":\"Sid\",\"type\":[\"int\",\"null\"]},{\"name\":\"DistributorOrderId\",\"type\":[\"string\",\"null\"]},{\"name\":\"DistributorUID\",\"type\":[\"string\",\"null\"]},{\"name\":\"DistributorChannelId\",\"type\":[\"string\",\"null\"]}]}");

    @Override
    @JsonIgnore
    public Schema getSchema() { return SCHEMA; }

    public AllianceInfoDTO(
        Integer allianceId,
        String ouid,
        Integer sid,
        String distributorOrderId,
        String distributorUID,
        String distributorChannelId) {
        this.allianceId = allianceId;
        this.ouid = ouid;
        this.sid = sid;
        this.distributorOrderId = distributorOrderId;
        this.distributorUID = distributorUID;
        this.distributorChannelId = distributorChannelId;
    }

    public AllianceInfoDTO() {
    }

    /**
     * 分销联盟
     */
    @JsonProperty("allianceId") 
    private Integer allianceId;

    /**
     * 分销联盟二级分销ID
     */
    @JsonProperty("ouid") 
    private String ouid;

    /**
     * 分销联盟三级分销ID
     */
    @JsonProperty("sid") 
    private Integer sid;

    /**
     * 分销商订单Id
     */
    @JsonProperty("distributorOrderId") 
    private String distributorOrderId;

    /**
     * 分销商用户Id
     */
    @JsonProperty("distributorUID") 
    private String distributorUID;

    @JsonProperty("distributorChannelId") 
    private String distributorChannelId;

    /**
     * 分销联盟
     */
    public Integer getAllianceId() {
        return allianceId;
    }

    /**
     * 分销联盟
     */
    public void setAllianceId(final Integer allianceId) {
        this.allianceId = allianceId;
    }

    /**
     * 分销联盟二级分销ID
     */
    public String getOuid() {
        return ouid;
    }

    /**
     * 分销联盟二级分销ID
     */
    public void setOuid(final String ouid) {
        this.ouid = ouid;
    }

    /**
     * 分销联盟三级分销ID
     */
    public Integer getSid() {
        return sid;
    }

    /**
     * 分销联盟三级分销ID
     */
    public void setSid(final Integer sid) {
        this.sid = sid;
    }

    /**
     * 分销商订单Id
     */
    public String getDistributorOrderId() {
        return distributorOrderId;
    }

    /**
     * 分销商订单Id
     */
    public void setDistributorOrderId(final String distributorOrderId) {
        this.distributorOrderId = distributorOrderId;
    }

    /**
     * 分销商用户Id
     */
    public String getDistributorUID() {
        return distributorUID;
    }

    /**
     * 分销商用户Id
     */
    public void setDistributorUID(final String distributorUID) {
        this.distributorUID = distributorUID;
    }
    public String getDistributorChannelId() {
        return distributorChannelId;
    }

    public void setDistributorChannelId(final String distributorChannelId) {
        this.distributorChannelId = distributorChannelId;
    }

    // Used by DatumWriter. Applications should not call.
    public Object get(int fieldPos) {
        switch (fieldPos) {
            case 0: return this.allianceId;
            case 1: return this.ouid;
            case 2: return this.sid;
            case 3: return this.distributorOrderId;
            case 4: return this.distributorUID;
            case 5: return this.distributorChannelId;
            default: throw new BaijiRuntimeException("Bad index " + fieldPos + " in get()");
        }
    }

    // Used by DatumReader. Applications should not call.
    @SuppressWarnings(value="unchecked")
    public void put(int fieldPos, Object fieldValue) {
        switch (fieldPos) {
            case 0: this.allianceId = (Integer)fieldValue; break;
            case 1: this.ouid = (String)fieldValue; break;
            case 2: this.sid = (Integer)fieldValue; break;
            case 3: this.distributorOrderId = (String)fieldValue; break;
            case 4: this.distributorUID = (String)fieldValue; break;
            case 5: this.distributorChannelId = (String)fieldValue; break;
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

        final AllianceInfoDTO other = (AllianceInfoDTO)obj;
        return 
            Objects.equal(this.allianceId, other.allianceId) &&
            Objects.equal(this.ouid, other.ouid) &&
            Objects.equal(this.sid, other.sid) &&
            Objects.equal(this.distributorOrderId, other.distributorOrderId) &&
            Objects.equal(this.distributorUID, other.distributorUID) &&
            Objects.equal(this.distributorChannelId, other.distributorChannelId);
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = 31 * result + (this.allianceId == null ? 0 : this.allianceId.hashCode());
        result = 31 * result + (this.ouid == null ? 0 : this.ouid.hashCode());
        result = 31 * result + (this.sid == null ? 0 : this.sid.hashCode());
        result = 31 * result + (this.distributorOrderId == null ? 0 : this.distributorOrderId.hashCode());
        result = 31 * result + (this.distributorUID == null ? 0 : this.distributorUID.hashCode());
        result = 31 * result + (this.distributorChannelId == null ? 0 : this.distributorChannelId.hashCode());

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("allianceId", allianceId)
            .add("ouid", ouid)
            .add("sid", sid)
            .add("distributorOrderId", distributorOrderId)
            .add("distributorUID", distributorUID)
            .add("distributorChannelId", distributorChannelId)
            .toString();
    }
}
