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
import java.math.BigDecimal;

/**
 * 无线实体信息
 */
@SuppressWarnings("all")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE) 
@JsonPropertyOrder({
    "customerGPSLat",
    "customerGPSLng",
    "mobileModel",
    "mobileSN",
    "customerIP",
    "wirelessVersion"
})
public class MobileDTO implements SpecificRecord, Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public static final transient Schema SCHEMA = Schema.parse("{\"type\":\"record\",\"name\":\"MobileDTO\",\"namespace\":\"com.ctriposs.baiji.rpc.common.types\",\"doc\":null,\"fields\":[{\"name\":\"CustomerGPSLat\",\"type\":[\"decimal\",\"null\"]},{\"name\":\"CustomerGPSLng\",\"type\":[\"decimal\",\"null\"]},{\"name\":\"MobileModel\",\"type\":[\"string\",\"null\"]},{\"name\":\"MobileSN\",\"type\":[\"string\",\"null\"]},{\"name\":\"CustomerIP\",\"type\":[\"string\",\"null\"]},{\"name\":\"WirelessVersion\",\"type\":[\"string\",\"null\"]}]}");

    @Override
    @JsonIgnore
    public Schema getSchema() { return SCHEMA; }

    public MobileDTO(
        BigDecimal customerGPSLat,
        BigDecimal customerGPSLng,
        String mobileModel,
        String mobileSN,
        String customerIP,
        String wirelessVersion) {
        this.customerGPSLat = customerGPSLat;
        this.customerGPSLng = customerGPSLng;
        this.mobileModel = mobileModel;
        this.mobileSN = mobileSN;
        this.customerIP = customerIP;
        this.wirelessVersion = wirelessVersion;
    }

    public MobileDTO() {
    }

    /**
     * 无线用户纬度
     */
    @JsonProperty("customerGPSLat") 
    private BigDecimal customerGPSLat;

    /**
     * 无线用户经度
     */
    @JsonProperty("customerGPSLng") 
    private BigDecimal customerGPSLng;

    /**
     * 用户手机类型Android、iOS等
     */
    @JsonProperty("mobileModel") 
    private String mobileModel;

    /**
     * 用户手机的SN编号，机器唯一标识
     */
    @JsonProperty("mobileSN") 
    private String mobileSN;

    /**
     * 用户IP地址
     */
    @JsonProperty("customerIP") 
    private String customerIP;

    /**
     * 无线版本号
     */
    @JsonProperty("wirelessVersion") 
    private String wirelessVersion;

    /**
     * 无线用户纬度
     */
    public BigDecimal getCustomerGPSLat() {
        return customerGPSLat;
    }

    /**
     * 无线用户纬度
     */
    public void setCustomerGPSLat(final BigDecimal customerGPSLat) {
        this.customerGPSLat = customerGPSLat;
    }

    /**
     * 无线用户经度
     */
    public BigDecimal getCustomerGPSLng() {
        return customerGPSLng;
    }

    /**
     * 无线用户经度
     */
    public void setCustomerGPSLng(final BigDecimal customerGPSLng) {
        this.customerGPSLng = customerGPSLng;
    }

    /**
     * 用户手机类型Android、iOS等
     */
    public String getMobileModel() {
        return mobileModel;
    }

    /**
     * 用户手机类型Android、iOS等
     */
    public void setMobileModel(final String mobileModel) {
        this.mobileModel = mobileModel;
    }

    /**
     * 用户手机的SN编号，机器唯一标识
     */
    public String getMobileSN() {
        return mobileSN;
    }

    /**
     * 用户手机的SN编号，机器唯一标识
     */
    public void setMobileSN(final String mobileSN) {
        this.mobileSN = mobileSN;
    }

    /**
     * 用户IP地址
     */
    public String getCustomerIP() {
        return customerIP;
    }

    /**
     * 用户IP地址
     */
    public void setCustomerIP(final String customerIP) {
        this.customerIP = customerIP;
    }

    /**
     * 无线版本号
     */
    public String getWirelessVersion() {
        return wirelessVersion;
    }

    /**
     * 无线版本号
     */
    public void setWirelessVersion(final String wirelessVersion) {
        this.wirelessVersion = wirelessVersion;
    }

    // Used by DatumWriter. Applications should not call.
    public Object get(int fieldPos) {
        switch (fieldPos) {
            case 0: return this.customerGPSLat;
            case 1: return this.customerGPSLng;
            case 2: return this.mobileModel;
            case 3: return this.mobileSN;
            case 4: return this.customerIP;
            case 5: return this.wirelessVersion;
            default: throw new BaijiRuntimeException("Bad index " + fieldPos + " in get()");
        }
    }

    // Used by DatumReader. Applications should not call.
    @SuppressWarnings(value="unchecked")
    public void put(int fieldPos, Object fieldValue) {
        switch (fieldPos) {
            case 0: this.customerGPSLat = (BigDecimal)fieldValue; break;
            case 1: this.customerGPSLng = (BigDecimal)fieldValue; break;
            case 2: this.mobileModel = (String)fieldValue; break;
            case 3: this.mobileSN = (String)fieldValue; break;
            case 4: this.customerIP = (String)fieldValue; break;
            case 5: this.wirelessVersion = (String)fieldValue; break;
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

        final MobileDTO other = (MobileDTO)obj;
        return 
            Objects.equal(this.customerGPSLat, other.customerGPSLat) &&
            Objects.equal(this.customerGPSLng, other.customerGPSLng) &&
            Objects.equal(this.mobileModel, other.mobileModel) &&
            Objects.equal(this.mobileSN, other.mobileSN) &&
            Objects.equal(this.customerIP, other.customerIP) &&
            Objects.equal(this.wirelessVersion, other.wirelessVersion);
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = 31 * result + (this.customerGPSLat == null ? 0 : this.customerGPSLat.hashCode());
        result = 31 * result + (this.customerGPSLng == null ? 0 : this.customerGPSLng.hashCode());
        result = 31 * result + (this.mobileModel == null ? 0 : this.mobileModel.hashCode());
        result = 31 * result + (this.mobileSN == null ? 0 : this.mobileSN.hashCode());
        result = 31 * result + (this.customerIP == null ? 0 : this.customerIP.hashCode());
        result = 31 * result + (this.wirelessVersion == null ? 0 : this.wirelessVersion.hashCode());

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("customerGPSLat", customerGPSLat)
            .add("customerGPSLng", customerGPSLng)
            .add("mobileModel", mobileModel)
            .add("mobileSN", mobileSN)
            .add("customerIP", customerIP)
            .add("wirelessVersion", wirelessVersion)
            .toString();
    }
}
