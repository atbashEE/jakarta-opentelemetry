# Jakarta OpenTelemetry

OpenTelemetry integration with Jakarta EE 10

This library provides integration between the OpenTelemetry Java SDK and Jakarta EE 10. It makes use of MicroProfile Config and when available, it integrates with MicroProfile Rest Client.

- *v0.9*

* Initial version
* Makes use of OpenTelemetry 1.21

# Setup

Add the following dependency to your project

```
    <dependency>
        <groupId>be.atbash.jakarta</groupId>
        <artifactId>opentelemetry</artifactId>
        <version>0.9</version>
    </dependency>
```

Define the service name for your application. MicroProfile Config is used to pick up this name and uses the key `atbash.otel.service.name` for this.

Configure the OpenTelemetry Collector that is used to send the traces to.  This needs to be performed through environment variables as required by the OpenTelemetry specification.  Have a look at [page](https://opentelemetry.io/docs/concepts/sdk-configuration/otlp-exporter-configuration/) for an example.

# Using

Once added, each JAX-RS request is traced. If the header indicates that there is already a trace ongoing (trace Context can be constructed), the request joins the existing trace as a child.

When you create a JAX-RS client and you want to propagate the trace context in the outgoing call, register the `be.atbash.research.jaxrs.RestClientPropagationFilter` class through the Client builder.

If you make use of MicroProfile Rest Client, this registration is performed automatically. You don't need to do anything.

If you want to visualize any CDI method call within the trace, annotate it with `io.opentelemetry.instrumentation.annotations.WithSpan` and a Span will be created with the method name as identification.

Ability to define custom Span attributes.

BaggageItems are not propagated (but can be implemented by using the Propagator of OpenTelemetry SDK)