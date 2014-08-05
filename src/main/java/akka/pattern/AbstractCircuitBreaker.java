package akka.pattern;

/**
 * Copyright (C) 2009-2014 Typesafe Inc. <http://www.typesafe.com>
 */

import com.covariantblabbering.circuitbreaker.CircuitBreaker;

import akka.util.Unsafe;

class AbstractCircuitBreaker {
    protected final static long stateOffset;

    static {
        try {
            stateOffset = Unsafe.instance.objectFieldOffset(CircuitBreaker.class.getDeclaredField("_currentStateDoNotCallMeDirectly"));
        } catch(Throwable t){
            throw new ExceptionInInitializerError(t);
        }
    }
}

