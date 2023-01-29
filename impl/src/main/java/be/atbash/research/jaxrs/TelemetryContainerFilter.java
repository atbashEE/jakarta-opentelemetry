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
package be.atbash.research.jaxrs;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.Tracer;
import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import io.opentelemetry.context.propagation.TextMapGetter;
import io.opentelemetry.semconv.trace.attributes.SemanticAttributes;
import jakarta.inject.Inject;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.Provider;

/**
 * The Request and Response filter on server side for OpenTelemetry support.
 */
@Provider
public class TelemetryContainerFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final String OTEL_SPAN_SERVER_SCOPE = "otel.span.server.scope";
    private static final String OTEL_SPAN_SERVER_SPAN = "otel.span.server.span";
    @Inject
    private Tracer tracer;

    @Inject
    private OpenTelemetry openTelemetry;

    private final TextMapGetter<? super MultivaluedMap<String, String>> getter =
            new TextMapGetter<>() {
                @Override
                public String get(MultivaluedMap<String, String> carrier, String key) {
                    if (carrier.containsKey(key)) {
                        return carrier.get(key).get(0);
                    }
                    return null;
                }

                @Override
                public Iterable<String> keys(MultivaluedMap<String, String> carrier) {
                    return carrier.keySet();
                }
            };

    @Override
    public void filter(ContainerRequestContext request) {
        // Request side
        Context extractedContext = extractContext(request);
        // We no data found in header, this is just a Root and is fine

        Span span = tracer.spanBuilder(request.getUriInfo().getPath())
                .setSpanKind(SpanKind.SERVER)
                .setParent(extractedContext)
                .startSpan();

        span.setAttribute(SemanticAttributes.HTTP_METHOD, request.getMethod());
        span.setAttribute(SemanticAttributes.HTTP_URL, request.getUriInfo().getRequestUri().toString());

        Scope scope = span.makeCurrent();

        request.setProperty(OTEL_SPAN_SERVER_SCOPE, scope);
        request.setProperty(OTEL_SPAN_SERVER_SPAN, span);

    }

    private Context extractContext(ContainerRequestContext request) {
        return openTelemetry.getPropagators().getTextMapPropagator()
                .extract(Context.current(), request.getHeaders(), getter);
    }

    @Override
    public void filter(ContainerRequestContext request, ContainerResponseContext response) {
        // Response side
        Scope scope = (Scope) request.getProperty(OTEL_SPAN_SERVER_SCOPE);
        // To be sure the request property still exists.
        if (scope == null) {
            return;
        }

        scope.close();

        Span span = (Span) request.getProperty(OTEL_SPAN_SERVER_SPAN);

        // End the span that was started when request was received.
        span.end();
    }
}
