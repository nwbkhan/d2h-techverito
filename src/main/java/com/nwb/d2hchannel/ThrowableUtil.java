package com.nwb.d2hchannel;

import com.nwb.d2hchannel.exception.D2Exception;

public class ThrowableUtil {


    public static Throwable getRootCause(Throwable ex) {
        if (ex == null) return new D2Exception("System caught error");
        Throwable cause = ex;
        while (cause.getCause() != null && cause != cause.getCause()) {
            cause = cause.getCause();
        }
        return cause;
    }
}
