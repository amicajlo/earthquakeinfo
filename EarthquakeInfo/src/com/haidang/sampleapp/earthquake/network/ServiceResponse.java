package com.haidang.sampleapp.earthquake.network;


/**
 * @author Hai Dang
 *This is response interface class - contain response data or error.and some other log info.
 * @param <T>
 */
public final class ServiceResponse<T> {
    public ServiceError error;
    public T result;
    public long lastDeliveredTime;
    public long requestStartTime;
    public long requestEndTime;

    public boolean isNew() {
        return lastDeliveredTime == 0 || lastDeliveredTime < requestStartTime;
    }

    @Override
    public String toString() {
        return "ServiceResponse{" +
            "error=" + error +
            ", result=" + result +
            ", lastDeliveredTime=" + lastDeliveredTime +
            ", requestStartTime=" + requestStartTime +
            ", requestEndTime=" + requestEndTime +
            '}';
    }
    
    public static class ServiceError {
        public int responseCode;
        public String errorMessage;
        public Throwable exception;

        public ServiceError(int responseCode, String errorMessage) {
            this.responseCode = responseCode;
            this.errorMessage = errorMessage;
        }

        public ServiceError(Throwable e) {
            this.exception = e;
        }

        @Override
        public String toString() {
            return "ServiceError{" +
                "responseCode=" + responseCode +
                ", errorMessage='" + errorMessage + '\'' +
                ", exception=" + exception +
                '}';
        }
    }
}
