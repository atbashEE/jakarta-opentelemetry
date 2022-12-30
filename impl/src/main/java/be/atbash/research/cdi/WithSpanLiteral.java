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

import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.util.AnnotationLiteral;

// Adapted from SmallRye OpenTelemetry
public class WithSpanLiteral extends AnnotationLiteral<WithSpan> implements WithSpan {
    public static final WithSpanLiteral INSTANCE = new WithSpanLiteral();

    @Override
    public String value() {
        return null;
    }

    @Override
    public SpanKind kind() {
        return null;
    }

}
