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
import java.util.Map;

@SuppressWarnings("all")
@JsonAutoDetect(getterVisibility = JsonAutoDetect.Visibility.NONE, setterVisibility = JsonAutoDetect.Visibility.NONE, isGetterVisibility = JsonAutoDetect.Visibility.NONE) 
@JsonPropertyOrder({
    "sourceFrom",
    "channelType",
    "requestId",
    "allianceInfo",
    "locale",
    "currencyCode",
    "mobileInfo",
    "sourceCountryId",
    "site",
    "language",
    "sessionId",
    "isWireless",
    "invokeFrom",
    "channelId",
    "uid",
    "patternType",
    "extraTags"
})
public class BaseRequest implements SpecificRecord, Serializable {
    private static final long serialVersionUID = 1L;

    @JsonIgnore
    public static final transient Schema SCHEMA = Schema.parse("{\"type\":\"record\",\"name\":\"BaseRequest\",\"namespace\":\"com.ctriposs.baiji.rpc.common.types\",\"doc\":null,\"fields\":[{\"name\":\"SourceFrom\",\"type\":[\"string\",\"null\"]},{\"name\":\"ChannelType\",\"type\":[\"int\",\"null\"]},{\"name\":\"RequestId\",\"type\":[\"string\",\"null\"]},{\"name\":\"AllianceInfo\",\"type\":[{\"type\":\"record\",\"name\":\"AllianceInfoDTO\",\"namespace\":\"com.ctriposs.baiji.rpc.common.types\",\"doc\":null,\"fields\":[{\"name\":\"AllianceId\",\"type\":[\"int\",\"null\"]},{\"name\":\"Ouid\",\"type\":[\"string\",\"null\"]},{\"name\":\"Sid\",\"type\":[\"int\",\"null\"]},{\"name\":\"DistributorOrderId\",\"type\":[\"string\",\"null\"]},{\"name\":\"DistributorUID\",\"type\":[\"string\",\"null\"]},{\"name\":\"DistributorChannelId\",\"type\":[\"string\",\"null\"]}]},\"null\"]},{\"name\":\"Locale\",\"type\":[\"string\",\"null\"]},{\"name\":\"CurrencyCode\",\"type\":[\"string\",\"null\"]},{\"name\":\"MobileInfo\",\"type\":[{\"type\":\"record\",\"name\":\"MobileDTO\",\"namespace\":\"com.ctriposs.baiji.rpc.common.types\",\"doc\":null,\"fields\":[{\"name\":\"CustomerGPSLat\",\"type\":[\"decimal\",\"null\"]},{\"name\":\"CustomerGPSLng\",\"type\":[\"decimal\",\"null\"]},{\"name\":\"MobileModel\",\"type\":[\"string\",\"null\"]},{\"name\":\"MobileSN\",\"type\":[\"string\",\"null\"]},{\"name\":\"CustomerIP\",\"type\":[\"string\",\"null\"]},{\"name\":\"WirelessVersion\",\"type\":[\"string\",\"null\"]}]},\"null\"]},{\"name\":\"SourceCountryId\",\"type\":[\"int\",\"null\"]},{\"name\":\"Site\",\"type\":[\"string\",\"null\"]},{\"name\":\"Language\",\"type\":[\"string\",\"null\"]},{\"name\":\"SessionId\",\"type\":[\"string\",\"null\"]},{\"name\":\"IsWireless\",\"type\":[\"boolean\",\"null\"]},{\"name\":\"InvokeFrom\",\"type\":[\"int\",\"null\"]},{\"name\":\"ChannelId\",\"type\":[\"int\",\"null\"]},{\"name\":\"Uid\",\"type\":[\"string\",\"null\"]},{\"name\":\"PatternType\",\"type\":[\"int\",\"null\"]},{\"name\":\"ExtraTags\",\"type\":[{\"type\":\"map\",\"values\":\"string\"},\"null\"]}]}");

    @Override
    @JsonIgnore
    public Schema getSchema() { return SCHEMA; }

    public BaseRequest(
        String sourceFrom,
        Integer channelType,
        String requestId,
        AllianceInfoDTO allianceInfo,
        String locale,
        String currencyCode,
        MobileDTO mobileInfo,
        Integer sourceCountryId,
        String site,
        String language,
        String sessionId,
        Boolean isWireless,
        Integer invokeFrom,
        Integer channelId,
        String uid,
        Integer patternType,
        Map<String, String> extraTags) {
        this.sourceFrom = sourceFrom;
        this.channelType = channelType;
        this.requestId = requestId;
        this.allianceInfo = allianceInfo;
        this.locale = locale;
        this.currencyCode = currencyCode;
        this.mobileInfo = mobileInfo;
        this.sourceCountryId = sourceCountryId;
        this.site = site;
        this.language = language;
        this.sessionId = sessionId;
        this.isWireless = isWireless;
        this.invokeFrom = invokeFrom;
        this.channelId = channelId;
        this.uid = uid;
        this.patternType = patternType;
        this.extraTags = extraTags;
    }

    public BaseRequest() {
    }

    @JsonProperty("sourceFrom") 
    private String sourceFrom;

    @JsonProperty("channelType") 
    private Integer channelType;

    @JsonProperty("requestId") 
    private String requestId;

    /**
     * 分销渠道
     */
    @JsonProperty("allianceInfo") 
    private AllianceInfoDTO allianceInfo;

    /**
     * 本地语言
     */
    @JsonProperty("locale") 
    private String locale;

    /**
     * 货币
     */
    @JsonProperty("currencyCode") 
    private String currencyCode;

    /**
     * 移动设备信息
     */
    @JsonProperty("mobileInfo") 
    private MobileDTO mobileInfo;

    /**
     * 客源国ID
     */
    @JsonProperty("sourceCountryId") 
    private Integer sourceCountryId;

    @JsonProperty("site") 
    private String site;

    @JsonProperty("language") 
    private String language;

    @JsonProperty("sessionId") 
    private String sessionId;

    @JsonProperty("isWireless") 
    private Boolean isWireless;

    @JsonProperty("invokeFrom") 
    private Integer invokeFrom;

    @JsonProperty("channelId") 
    private Integer channelId;

    @JsonProperty("uid") 
    private String uid;

    @JsonProperty("patternType") 
    private Integer patternType;

    @JsonProperty("extraTags") 
    private Map<String, String> extraTags;

    public String getSourceFrom() {
        return sourceFrom;
    }

    public void setSourceFrom(final String sourceFrom) {
        this.sourceFrom = sourceFrom;
    }
    public Integer getChannelType() {
        return channelType;
    }

    public void setChannelType(final Integer channelType) {
        this.channelType = channelType;
    }
    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(final String requestId) {
        this.requestId = requestId;
    }

    /**
     * 分销渠道
     */
    public AllianceInfoDTO getAllianceInfo() {
        return allianceInfo;
    }

    /**
     * 分销渠道
     */
    public void setAllianceInfo(final AllianceInfoDTO allianceInfo) {
        this.allianceInfo = allianceInfo;
    }

    /**
     * 本地语言
     */
    public String getLocale() {
        return locale;
    }

    /**
     * 本地语言
     */
    public void setLocale(final String locale) {
        this.locale = locale;
    }

    /**
     * 货币
     */
    public String getCurrencyCode() {
        return currencyCode;
    }

    /**
     * 货币
     */
    public void setCurrencyCode(final String currencyCode) {
        this.currencyCode = currencyCode;
    }

    /**
     * 移动设备信息
     */
    public MobileDTO getMobileInfo() {
        return mobileInfo;
    }

    /**
     * 移动设备信息
     */
    public void setMobileInfo(final MobileDTO mobileInfo) {
        this.mobileInfo = mobileInfo;
    }

    /**
     * 客源国ID
     */
    public Integer getSourceCountryId() {
        return sourceCountryId;
    }

    /**
     * 客源国ID
     */
    public void setSourceCountryId(final Integer sourceCountryId) {
        this.sourceCountryId = sourceCountryId;
    }
    public String getSite() {
        return site;
    }

    public void setSite(final String site) {
        this.site = site;
    }
    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }
    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(final String sessionId) {
        this.sessionId = sessionId;
    }
    public Boolean isIsWireless() {
        return isWireless;
    }

    public void setIsWireless(final Boolean isWireless) {
        this.isWireless = isWireless;
    }
    public Integer getInvokeFrom() {
        return invokeFrom;
    }

    public void setInvokeFrom(final Integer invokeFrom) {
        this.invokeFrom = invokeFrom;
    }
    public Integer getChannelId() {
        return channelId;
    }

    public void setChannelId(final Integer channelId) {
        this.channelId = channelId;
    }
    public String getUid() {
        return uid;
    }

    public void setUid(final String uid) {
        this.uid = uid;
    }
    public Integer getPatternType() {
        return patternType;
    }

    public void setPatternType(final Integer patternType) {
        this.patternType = patternType;
    }
    public Map<String, String> getExtraTags() {
        return extraTags;
    }

    public void setExtraTags(final Map<String, String> extraTags) {
        this.extraTags = extraTags;
    }

    // Used by DatumWriter. Applications should not call.
    public Object get(int fieldPos) {
        switch (fieldPos) {
            case 0: return this.sourceFrom;
            case 1: return this.channelType;
            case 2: return this.requestId;
            case 3: return this.allianceInfo;
            case 4: return this.locale;
            case 5: return this.currencyCode;
            case 6: return this.mobileInfo;
            case 7: return this.sourceCountryId;
            case 8: return this.site;
            case 9: return this.language;
            case 10: return this.sessionId;
            case 11: return this.isWireless;
            case 12: return this.invokeFrom;
            case 13: return this.channelId;
            case 14: return this.uid;
            case 15: return this.patternType;
            case 16: return this.extraTags;
            default: throw new BaijiRuntimeException("Bad index " + fieldPos + " in get()");
        }
    }

    // Used by DatumReader. Applications should not call.
    @SuppressWarnings(value="unchecked")
    public void put(int fieldPos, Object fieldValue) {
        switch (fieldPos) {
            case 0: this.sourceFrom = (String)fieldValue; break;
            case 1: this.channelType = (Integer)fieldValue; break;
            case 2: this.requestId = (String)fieldValue; break;
            case 3: this.allianceInfo = (AllianceInfoDTO)fieldValue; break;
            case 4: this.locale = (String)fieldValue; break;
            case 5: this.currencyCode = (String)fieldValue; break;
            case 6: this.mobileInfo = (MobileDTO)fieldValue; break;
            case 7: this.sourceCountryId = (Integer)fieldValue; break;
            case 8: this.site = (String)fieldValue; break;
            case 9: this.language = (String)fieldValue; break;
            case 10: this.sessionId = (String)fieldValue; break;
            case 11: this.isWireless = (Boolean)fieldValue; break;
            case 12: this.invokeFrom = (Integer)fieldValue; break;
            case 13: this.channelId = (Integer)fieldValue; break;
            case 14: this.uid = (String)fieldValue; break;
            case 15: this.patternType = (Integer)fieldValue; break;
            case 16: this.extraTags = (Map<String, String>)fieldValue; break;
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

        final BaseRequest other = (BaseRequest)obj;
        return 
            Objects.equal(this.sourceFrom, other.sourceFrom) &&
            Objects.equal(this.channelType, other.channelType) &&
            Objects.equal(this.requestId, other.requestId) &&
            Objects.equal(this.allianceInfo, other.allianceInfo) &&
            Objects.equal(this.locale, other.locale) &&
            Objects.equal(this.currencyCode, other.currencyCode) &&
            Objects.equal(this.mobileInfo, other.mobileInfo) &&
            Objects.equal(this.sourceCountryId, other.sourceCountryId) &&
            Objects.equal(this.site, other.site) &&
            Objects.equal(this.language, other.language) &&
            Objects.equal(this.sessionId, other.sessionId) &&
            Objects.equal(this.isWireless, other.isWireless) &&
            Objects.equal(this.invokeFrom, other.invokeFrom) &&
            Objects.equal(this.channelId, other.channelId) &&
            Objects.equal(this.uid, other.uid) &&
            Objects.equal(this.patternType, other.patternType) &&
            Objects.equal(this.extraTags, other.extraTags);
    }

    @Override
    public int hashCode() {
        int result = 1;

        result = 31 * result + (this.sourceFrom == null ? 0 : this.sourceFrom.hashCode());
        result = 31 * result + (this.channelType == null ? 0 : this.channelType.hashCode());
        result = 31 * result + (this.requestId == null ? 0 : this.requestId.hashCode());
        result = 31 * result + (this.allianceInfo == null ? 0 : this.allianceInfo.hashCode());
        result = 31 * result + (this.locale == null ? 0 : this.locale.hashCode());
        result = 31 * result + (this.currencyCode == null ? 0 : this.currencyCode.hashCode());
        result = 31 * result + (this.mobileInfo == null ? 0 : this.mobileInfo.hashCode());
        result = 31 * result + (this.sourceCountryId == null ? 0 : this.sourceCountryId.hashCode());
        result = 31 * result + (this.site == null ? 0 : this.site.hashCode());
        result = 31 * result + (this.language == null ? 0 : this.language.hashCode());
        result = 31 * result + (this.sessionId == null ? 0 : this.sessionId.hashCode());
        result = 31 * result + (this.isWireless == null ? 0 : this.isWireless.hashCode());
        result = 31 * result + (this.invokeFrom == null ? 0 : this.invokeFrom.hashCode());
        result = 31 * result + (this.channelId == null ? 0 : this.channelId.hashCode());
        result = 31 * result + (this.uid == null ? 0 : this.uid.hashCode());
        result = 31 * result + (this.patternType == null ? 0 : this.patternType.hashCode());
        result = 31 * result + (this.extraTags == null ? 0 : this.extraTags.hashCode());

        return result;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("sourceFrom", sourceFrom)
            .add("channelType", channelType)
            .add("requestId", requestId)
            .add("allianceInfo", allianceInfo)
            .add("locale", locale)
            .add("currencyCode", currencyCode)
            .add("mobileInfo", mobileInfo)
            .add("sourceCountryId", sourceCountryId)
            .add("site", site)
            .add("language", language)
            .add("sessionId", sessionId)
            .add("isWireless", isWireless)
            .add("invokeFrom", invokeFrom)
            .add("channelId", channelId)
            .add("uid", uid)
            .add("patternType", patternType)
            .add("extraTags", extraTags)
            .toString();
    }
}
