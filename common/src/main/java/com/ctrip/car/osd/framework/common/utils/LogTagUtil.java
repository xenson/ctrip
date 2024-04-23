package com.ctrip.car.osd.framework.common.utils;

import com.ctrip.car.osd.framework.common.clogging.CatFactory;
import com.ctrip.car.osd.framework.common.clogging.LogContext;
import com.ctrip.car.osd.framework.common.clogging.Logger;
import com.ctrip.car.osd.framework.common.clogging.LoggerFactory;
import com.ctriposs.baiji.rpc.common.types.AllianceInfoDTO;
import com.ctriposs.baiji.rpc.common.types.BaseRequest;
import com.ctriposs.baiji.rpc.common.types.BaseResponse;
import com.ctriposs.baiji.rpc.common.types.MobileDTO;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang.reflect.FieldUtils;

import java.util.*;


public class LogTagUtil{
    private static final Logger LOGGER = LoggerFactory.getLogger(LogTagUtil.class);

    public static Map<String, String> buildExtraIndexTags(BaseResponse baseResponse){
        if (baseResponse == null){
            LOGGER.warn("baseResponse is null.");
            return new HashMap<>();
        }
        Object extraIndexTags = null;

        try {
            extraIndexTags = PropertyUtils.getProperty(baseResponse, "extraIndexTags");
            if (extraIndexTags == null) {
                extraIndexTags = new HashMap<>();
                PropertyUtils.setProperty(baseResponse,"extraIndexTags",extraIndexTags);
            }
        } catch (Exception e) {
            LOGGER.warn("set extraIndexTags error!", e);
            extraIndexTags = new HashMap<>();
        }

        return (Map<String, String>)extraIndexTags;

    }

    public static Map<String, String> getIndexTags(LogContext logContext) {
        Map<String, String> indexTags = getindexTags(logContext.getTitle(), logContext.getRequest(), logContext.getResponse());
        indexTags.put("compressType", logContext.getCompressAlgorithm() == 0 ? "GZIP" : "ZSTD");
        indexTags.put("exception", Objects.isNull(logContext.getException()) ? null : ExceptionUtils.getStackTrace(logContext.getException()));
        return indexTags;
    }

    public static Map<String, String> getindexTags(String title, Object request, Object response) {
        Map<String, String> indexTags = new HashMap<>(8);
        indexTags.put("title", title);
        try {
            if (request != null) {
                String vendorGroupName = "vendorGroup";
                boolean hasVendorGroup = PropertyUtils.isReadable(request, vendorGroupName);
                if (hasVendorGroup) {
                    Object vendorGroup = PropertyUtils.getProperty(request, vendorGroupName);
                    if (vendorGroup != null) {
                        indexTags.put(vendorGroupName,vendorGroup.toString());
                    }
                }
                fillBoleanTags(indexTags, request, "withCache");
                fillBoleanTags(indexTags, request, "withPrice");

                boolean hasBaseRequest = PropertyUtils.isReadable(request, "baseRequest");
                boolean hasRequestHeader = PropertyUtils.isReadable(request, "requestHeader");
                if (hasBaseRequest) {
                    boolean hasPickupRentalInfo = PropertyUtils.isReadable(request, "pickupLocation");
                    if (hasPickupRentalInfo) {
                        Object pickupLocation = PropertyUtils.getProperty(request, "pickupLocation");
                        if (pickupLocation != null) {
                            fillTags(indexTags, pickupLocation, "pcityId","cityId");
                            fillTags(indexTags, pickupLocation, "plocationType", "locationType");
                            fillTags(indexTags, pickupLocation, "plocationCode", "locationCode");
                            fillTags(indexTags, pickupLocation, "plocationName", "locationName");
                        }
                    }
                    boolean hasDropoffRentalInfo = PropertyUtils.isReadable(request, "returnLocation");
                    if (hasDropoffRentalInfo) {
                        Object returnLocation = PropertyUtils.getProperty(request, "returnLocation");
                        if (returnLocation != null) {
                            fillTags(indexTags, returnLocation, "rcityId","cityId");
                            fillTags(indexTags, returnLocation, "rlocationType", "locationType");
                            fillTags(indexTags, returnLocation, "rlocationCode", "locationCode");
                            fillTags(indexTags, returnLocation, "rlocationName", "locationName");
                        }
                    }
                    Object baseRequestObj = PropertyUtils.getProperty(request, "baseRequest");
                    if (baseRequestObj != null && baseRequestObj instanceof BaseRequest) {
                        BaseRequest baseRequest = (BaseRequest) baseRequestObj;
                        if (baseRequest.getRequestId() != null) {
                            indexTags.put("requestId",baseRequest.getRequestId());
                        }
                        if (baseRequest.getSessionId() != null) {
                            indexTags.put("sessionId",baseRequest.getSessionId());
                        }
                        if (baseRequest.getChannelType() != null) {
                            indexTags.put("channelType",baseRequest.getChannelType().toString());
                        }
                        if (baseRequest.getSourceCountryId() != null) {
                            indexTags.put("sourceCountryId",baseRequest.getSourceCountryId().toString());
                        }
                        if (baseRequest.getLanguage() != null) {
                            indexTags.put("language",baseRequest.getLanguage());
                        }
                        if (baseRequest.getSourceFrom() != null) {
                            indexTags.put("sourceFrom",baseRequest.getSourceFrom());
                        }
                        if (baseRequest.getCurrencyCode() != null) {
                            indexTags.put("currencyCode",baseRequest.getCurrencyCode());
                        }
                        if (baseRequest.getInvokeFrom() != null) {
                            indexTags.put("invokeFrom",baseRequest.getInvokeFrom().toString());
                        }
                        if (baseRequest.getLocale() != null) {
                            indexTags.put("locale",baseRequest.getLocale());
                        }
                        if (baseRequest.getSite() != null) {
                            indexTags.put("site",baseRequest.getSite());
                        }
                        MobileDTO mobileInfo = baseRequest.getMobileInfo();
                        if (mobileInfo != null) {
                            String customerIP = mobileInfo.getCustomerIP();
                            if (customerIP != null) {
                                indexTags.put("cip",customerIP);
                            }
                        }
                        AllianceInfoDTO allianceInfo = baseRequest.getAllianceInfo();
                        if (allianceInfo != null) {
                            if (allianceInfo.getAllianceId() != null) {
                                indexTags.put("allianceId",allianceInfo.getAllianceId().toString());
                            }
                            if (allianceInfo.getOuid() != null) {
                                indexTags.put("ouid",allianceInfo.getOuid());
                            }
                            if (allianceInfo.getSid() != null) {
                                indexTags.put("sid",allianceInfo.getSid().toString());
                            }
                            if (allianceInfo.getDistributorOrderId() != null) {
                                indexTags.put("distributorOrderId",allianceInfo.getDistributorOrderId());
                            }
                            if (allianceInfo.getDistributorUID() != null) {
                                indexTags.put("distributorUID",allianceInfo.getDistributorUID());
                            }
                        }
                    }
                } else if (hasRequestHeader) {
                    boolean hasPickupRentalInfo = PropertyUtils.isReadable(request, "pickupRentalInfo");
                    if (hasPickupRentalInfo) {
                        Object pickupRentalInfo = PropertyUtils.getProperty(request, "pickupRentalInfo");
                        if (pickupRentalInfo != null) {
                            fillTags(indexTags, pickupRentalInfo, "pcityId","cityId");
                            fillTags(indexTags, pickupRentalInfo, "plocationType", "locationType");
                            fillTags(indexTags, pickupRentalInfo, "plocationCode", "locationCode");
                            fillTags(indexTags, pickupRentalInfo, "plocationName", "locationName");
                        }
                    }
                    boolean hasDropoffRentalInfo = PropertyUtils.isReadable(request, "dropoffRentalInfo");
                    if (hasDropoffRentalInfo) {
                        Object dropoffRentalInfo = PropertyUtils.getProperty(request, "dropoffRentalInfo");
                        if (dropoffRentalInfo != null) {
                            fillTags(indexTags, dropoffRentalInfo, "rcityId","cityId");
                            fillTags(indexTags, dropoffRentalInfo, "rlocationType", "locationType");
                            fillTags(indexTags, dropoffRentalInfo, "rlocationCode", "locationCode");
                            fillTags(indexTags, dropoffRentalInfo, "rlocationName", "locationName");
                        }
                    }
                    Object requestHeaderObj = PropertyUtils.getProperty(request, "requestHeader");
                    if (requestHeaderObj != null) {
                        boolean hasRequestId = PropertyUtils.isReadable(requestHeaderObj, "requestId");
                        if (hasRequestId) {
                            String requestId = BeanUtils.getProperty(requestHeaderObj, "requestId");
                            indexTags.put("requestId",requestId);
                        }
                        boolean hasSessionId = PropertyUtils.isReadable(requestHeaderObj, "sessionId");
                        if (hasSessionId) {
                            String sessionId = BeanUtils.getProperty(requestHeaderObj, "sessionId");
                            indexTags.put("sessionId",sessionId);
                        }
                        boolean hasChannelType = PropertyUtils.isReadable(requestHeaderObj, "requestorNo");
                        if (hasChannelType) {
                            String channelType = BeanUtils.getProperty(requestHeaderObj, "requestorNo");
                            indexTags.put("channelType",channelType);
                        }
                        boolean hasSourceCountryId = PropertyUtils.isReadable(requestHeaderObj, "residency");
                        if (hasSourceCountryId) {
                            String sourceCountryId = BeanUtils.getProperty(requestHeaderObj, "residency");
                            indexTags.put("sourceCountryId",sourceCountryId);
                        }
                        boolean hasLanguage = PropertyUtils.isReadable(requestHeaderObj, "preLang");
                        if (hasLanguage) {
                            String language = BeanUtils.getProperty(requestHeaderObj, "preLang");
                            indexTags.put("language",language);
                        }
                        boolean hasSourceFrom = PropertyUtils.isReadable(requestHeaderObj, "sourceFrom");
                        if (hasSourceFrom) {
                            String sourceFrom = BeanUtils.getProperty(requestHeaderObj, "sourceFrom");
                            indexTags.put("sourceFrom",sourceFrom);
                        }
                        boolean hasTargetCurrencyCode = PropertyUtils.isReadable(requestHeaderObj, "targetCurrencyCode");
                        if (hasTargetCurrencyCode) {
                            String targetCurrencyCode = BeanUtils.getProperty(requestHeaderObj, "targetCurrencyCode");
                            indexTags.put("currencyCode",targetCurrencyCode);
                        }
                    }
                } else {
                    fillTags(indexTags, request, "requestId", "requestid", "requestID");
                    fillTags(indexTags, request, "sessionId", "sessionid", "sessionID");
                }
                boolean hasRequestInfo = PropertyUtils.isReadable(request, "requestInfo");
                if (hasRequestInfo) {
                    Object requestInfo = PropertyUtils.getProperty(request, "requestInfo");
                    if (requestInfo != null) {
                        fillTags(indexTags, requestInfo, "orderId", "orderid", "orderID");
                        fillTags(indexTags, requestInfo, "uid", "userId", "userid", "userID");
                    }
                }else {
                    fillTags(indexTags, request, "orderId", "orderid", "orderID");
                    fillTags(indexTags, request, "uid","userId","userid","userID");
                }
            }
            if (response != null) {
                boolean hasResponseInfo = PropertyUtils.isReadable(response, "baseResponse");
                boolean hasResponseHeader = PropertyUtils.isReadable(response, "responseHeader");
                if (hasResponseInfo) {
                    Object baseResponse = PropertyUtils.getProperty(response, "baseResponse");
                    if (baseResponse != null) {
                        fillTags(indexTags, baseResponse, "cost");
                        fillBoleanTags(indexTags, baseResponse, "isSuccess");
                        fillTags(indexTags, baseResponse, "code");

                        Object extraIndexTags = PropertyUtils.getProperty(baseResponse, "extraIndexTags");
                        if (extraIndexTags != null) {
                            indexTags.putAll((Map<? extends String, ? extends String>) extraIndexTags);
                        }
                    }
                } else if (hasResponseHeader) {
                    Object responseHeader = PropertyUtils.getProperty(response, "responseHeader");
                    if (responseHeader != null) {
                        fillBoleanTags(indexTags, responseHeader, "isSuccess");
                        fillTags(indexTags, responseHeader, "errorCode");
                    }
                } else {
                    fillTags(indexTags, response, "cost");
                }
            }
        } catch (Exception e) {

        }
        return indexTags;
    }

    /**
     * get track map from request or response fields(or from Qconfig node path)
     *
     * @param request
     * @param response
     * @return
     */
    public static Map<String, String> getExtMaps(Object request, Object response) {
        Map<String, String> indexTags = new HashMap<>(8);
        try {
            indexTags.putAll(getSpecifyNodeMap(request, "baseRequest.extraMaps"));
            indexTags.putAll(getSpecifyNodeMap(request, "baseRequest.extraTags"));
            indexTags.putAll(getSpecifyNodeMap(response, "baseResponse.extMap"));
            indexTags.putAll(CatFactory.getCommonTags());
            //specify node by Qconfig
            List<String> nodePaths = QconfigProperty.getFrameworkTrackNodes();
            if (CollectionUtils.isNotEmpty(nodePaths)) {
                for (String nodePath : nodePaths) {
                    if (nodePath.startsWith("request")) {
                        indexTags.putAll(getSpecifyNodeMap(request, nodePath.replaceFirst("request.", "")));
                    } else if (nodePath.startsWith("response")) {
                        indexTags.putAll(getSpecifyNodeMap(response, nodePath.replaceFirst("response.", "")));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Framework_getExtMaps", e);
        }
        return indexTags;
    }

    /**
     * get specify node val to map
     *
     * @param source   {request or response}
     * @param nodePath {ex:baseRequest.extraTags}
     * @return
     */
    public static Map<String, String> getSpecifyNodeMap(Object source, String nodePath) {
        Map<String, String> nodeTags = new HashMap<>(8);
        try {
            if (source == null) {
                return nodeTags;
            }
            boolean hasNode;
            try {
                hasNode = PropertyUtils.isReadable(source, nodePath);
            } catch (Exception e) {
                hasNode = false;
            }
            if (hasNode) {
                Object nodeVal = PropertyUtils.getProperty(source, nodePath);
                if (nodeVal != null) {
                    if (nodeVal.getClass().equals(LinkedHashMap.class)) {
                        nodeTags.putAll((Map<? extends String, ? extends String>) nodeVal);
                    } else {
                        String[] nodeNames = nodePath.split("\\.");
                        String nodeName = nodePath.length() > 1 ? nodeNames[nodeNames.length - 1] : nodePath;
                        nodeTags.put(nodeName, String.valueOf(nodeVal));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Framework_getSpecifyNodeMap", e);
        }
        return nodeTags;
    }

    private static void fillBoleanTags(Map<String, String> indexTags, Object baseResponse, String name) throws IllegalAccessException {
        boolean hasWithPrice = PropertyUtils.isWriteable(baseResponse, name);
        if (hasWithPrice) {
            Object withPrice = FieldUtils.readField(baseResponse, name,true);
            if (withPrice != null) {
                indexTags.put(name,withPrice.toString());
            }
        }
    }

    private static void fillTags(Map<String, String> tags, Object obj, String key,String... alias) {
        boolean isFound = fillTagsCore(tags, obj, key, key);
        if (!isFound && alias != null && alias.length > 0) {
            for (String name : alias) {
                isFound = fillTagsCore(tags, obj, key, name);
                if (isFound) {
                    break;
                }
            }
        }
    }

    private static boolean fillTagsCore(Map<String, String> tags, Object obj, String key,String propertyName) {
        boolean isFound = false;
        try {
            boolean hasProperty = PropertyUtils.isReadable(obj,propertyName);
            if (hasProperty) {
                String value = BeanUtils.getProperty(obj, propertyName);
                if (value != null) {
                    tags.put(key, value);
                    isFound = true;
                }
            }
        } catch (Exception e) {
        }
        return isFound;
    }

}
