/*
 * Copyright 2022-2023 Rudy De Busscher (https://www.atbash.be)
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
package be.atbash.research.serviceb.demo.resources;

import io.opentelemetry.api.baggage.Baggage;
import io.opentelemetry.api.trace.Span;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path(("/remote"))
@ApplicationScoped
public class RemoteResource {

    @Inject
    private Span curentSpan;

    @GET
    public String remote() {
        System.out.println("BaggageItems" + Baggage.current()
                .asMap());

        curentSpan.setAttribute("spanAttributeServiceB", "attribute2");
        return "Remote value";
    }
}
