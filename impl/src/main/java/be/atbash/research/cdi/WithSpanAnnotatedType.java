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

import io.opentelemetry.instrumentation.annotations.WithSpan;
import jakarta.enterprise.inject.spi.*;
import jakarta.enterprise.util.Nonbinding;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * The {@link AnnotatedType} for the WithSpan annotation.
 *
 */
// Adapted from SmallRye OpenTelemetry
public class WithSpanAnnotatedType implements AnnotatedType<WithSpan> {
    private final AnnotatedType<WithSpan> delegate;
    private final Set<AnnotatedMethod<? super WithSpan>> methods;

    public WithSpanAnnotatedType(final AnnotatedType<WithSpan> delegate) {
        this.delegate = delegate;
        this.methods = new HashSet<>();

        for (AnnotatedMethod<? super WithSpan> method : delegate.getMethods()) {
            methods.add(new AnnotatedMethod<>() {
                private final AnnotatedMethod<WithSpan> delegate = (AnnotatedMethod<WithSpan>) method;
                private final Set<Annotation> annotations = Collections.singleton(Nonbinding.Literal.INSTANCE);

                @Override
                public Method getJavaMember() {
                    return delegate.getJavaMember();
                }

                @Override
                public List<AnnotatedParameter<WithSpan>> getParameters() {
                    return delegate.getParameters();
                }

                @Override
                public boolean isStatic() {
                    return delegate.isStatic();
                }

                @Override
                public AnnotatedType<WithSpan> getDeclaringType() {
                    return delegate.getDeclaringType();
                }

                @Override
                public Type getBaseType() {
                    return delegate.getBaseType();
                }

                @Override
                public Set<Type> getTypeClosure() {
                    return delegate.getTypeClosure();
                }

                @Override
                public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
                    if (annotationType.equals(Nonbinding.class)) {
                        return (T) annotations.iterator().next();
                    }
                    return null;
                }

                @Override
                public Set<Annotation> getAnnotations() {
                    return annotations;
                }

                @Override
                public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
                    return annotationType.equals(Nonbinding.class);
                }
            });
        }
    }

    @Override
    public Class<WithSpan> getJavaClass() {
        return delegate.getJavaClass();
    }

    @Override
    public Set<AnnotatedConstructor<WithSpan>> getConstructors() {
        return delegate.getConstructors();
    }

    @Override
    public Set<AnnotatedMethod<? super WithSpan>> getMethods() {
        return this.methods;
    }

    @Override
    public Set<AnnotatedField<? super WithSpan>> getFields() {
        return delegate.getFields();
    }

    @Override
    public Type getBaseType() {
        return delegate.getBaseType();
    }

    @Override
    public Set<Type> getTypeClosure() {
        return delegate.getTypeClosure();
    }

    @Override
    public <T extends Annotation> T getAnnotation(final Class<T> annotationType) {
        return delegate.getAnnotation(annotationType);
    }

    @Override
    public Set<Annotation> getAnnotations() {
        return delegate.getAnnotations();
    }

    @Override
    public boolean isAnnotationPresent(final Class<? extends Annotation> annotationType) {
        return delegate.isAnnotationPresent(annotationType);
    }
}
