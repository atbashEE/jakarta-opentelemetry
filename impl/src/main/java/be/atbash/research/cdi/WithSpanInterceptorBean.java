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

import io.opentelemetry.api.trace.Tracer;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.*;
import jakarta.interceptor.InvocationContext;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Set;

/**
 * Interceptor bean for the {@code WithSpan} annotation.
 */
// Adapted from SmallRye OpenTelemetry
public class WithSpanInterceptorBean implements Interceptor<WithSpanInterceptor>, Prioritized {
    private final BeanManager beanManager;

    public WithSpanInterceptorBean(final BeanManager beanManager) {
        this.beanManager = beanManager;
    }

    @Override
    public Set<Annotation> getInterceptorBindings() {
        return Collections.singleton(WithSpanLiteral.INSTANCE);
    }

    @Override
    public boolean intercepts(final InterceptionType type) {
        return InterceptionType.AROUND_INVOKE.equals(type);
    }

    @Override
    public Object intercept(
            final InterceptionType type,
            final WithSpanInterceptor instance,
            final InvocationContext invocationContext)
            throws Exception {

        return instance.span(invocationContext);
    }

    @Override
    public Class<?> getBeanClass() {
        return WithSpanInterceptorBean.class;
    }

    @Override
    public Set<InjectionPoint> getInjectionPoints() {
        return Collections.emptySet();
    }

    @Override
    public WithSpanInterceptor create(final CreationalContext<WithSpanInterceptor> creationalContext) {
        Bean<?> bean = beanManager.resolve(beanManager.getBeans(Tracer.class));
        Tracer tracer = (Tracer) beanManager.getReference(bean, Tracer.class, creationalContext);
        return new WithSpanInterceptor(tracer);
    }

    @Override
    public void destroy(
            final WithSpanInterceptor instance,
            final CreationalContext<WithSpanInterceptor> creationalContext) {

    }

    @Override
    public Set<Type> getTypes() {
        return Collections.singleton(this.getBeanClass());
    }

    @Override
    public Set<Annotation> getQualifiers() {
        return Collections.emptySet();
    }

    @Override
    public Class<? extends Annotation> getScope() {
        return Dependent.class;
    }

    @Override
    public String getName() {
        return getBeanClass().getName();
    }

    @Override
    public Set<Class<? extends Annotation>> getStereotypes() {
        return Collections.emptySet();
    }

    @Override
    public boolean isAlternative() {
        return false;
    }

    @Override
    public int getPriority() {
        return 100;
    }
}
