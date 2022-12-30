/*
 * Copyright 2022 Rudy De Busscher (https://www.atbash.be)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package be.atbash.research.cdi;

import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import jakarta.interceptor.AroundInvoke;
import jakarta.interceptor.InvocationContext;

import java.lang.reflect.Method;

/**
 * Interceptor handling @WithSpan
 */
public class WithSpanInterceptor {


    private final Tracer tracer;

    public WithSpanInterceptor(Tracer tracer) {

        this.tracer = tracer;
    }


    @AroundInvoke
    public Object span(InvocationContext invocationContext) throws Exception {
        String spanName = retrieveSpanName(invocationContext);

        Context parentContext = Context.current();

        Span span = tracer.spanBuilder(spanName)
                .setSpanKind(SpanKind.INTERNAL)
                .setParent(parentContext)
                .startSpan();

        Object result;
        try (Scope ss = span.makeCurrent()) {
            result = invocationContext.proceed();
        } finally {
            span.end();
        }
        return result;
    }

    private String retrieveSpanName(InvocationContext invocationContext) {
        String result = "";
        Method method = invocationContext.getMethod();
        // TODO Handle when not used on a method
        if (method != null) {
            result = method.getDeclaringClass().getName() +
                    "#" +
                    method.getName();
        }
        return result;
    }
}
