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
package be.atbash.research.servicea.demo.resources;

import be.atbash.research.servicea.demo.service.HelloService;
import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;

@Path(("/hello"))
@ApplicationScoped
public class HelloResource {

    @Inject
    private HelloService helloService;

    @Inject
    private Span curentSpan;

    @GET
    @Path("/{name}")
    public String hello(@PathParam("name") String name) {
        // Example of BaggageItems usage
        Baggage.current()
                .toBuilder()
                .put("baggageItem", name)
                .build()
                .makeCurrent();

        curentSpan.setAttribute("spanAttributeServiceA", "attribute1");

        return String.format(helloService.defineHelloMessage(), name);
    }
}
