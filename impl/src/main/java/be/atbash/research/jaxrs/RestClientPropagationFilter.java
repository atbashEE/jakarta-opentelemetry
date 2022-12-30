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
import io.opentelemetry.context.Context;
import io.opentelemetry.context.propagation.TextMapSetter;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.ws.rs.client.ClientRequestContext;
import jakarta.ws.rs.client.ClientRequestFilter;
import jakarta.ws.rs.core.MultivaluedMap;

import java.io.IOException;

/**
 * A JAX-RS Client Request Filter that places the Context on the Header of the outgoing call.
 */
public class RestClientPropagationFilter implements ClientRequestFilter {

    private final TextMapSetter<MultivaluedMap<String, Object>> setter =
            (carrier, key, value) -> {
                // Insert the context as Header
                carrier.add(key, value);
            };

    private final OpenTelemetry openTelemetry = CDI.current().select(OpenTelemetry.class).get();

    @Override
    public void filter(ClientRequestContext requestContext) throws IOException {

        openTelemetry.getPropagators().getTextMapPropagator().inject(Context.current(), requestContext.getHeaders(), setter);

    }
}