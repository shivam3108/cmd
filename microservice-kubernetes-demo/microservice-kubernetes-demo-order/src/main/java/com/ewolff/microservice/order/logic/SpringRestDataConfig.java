package com.ewolff.microservice.order.logic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;



import brave.Tracing;
import brave.opentracing.BraveTracer;
import brave.sampler.Sampler;
import zipkin.Span;
import zipkin.reporter.AsyncReporter;
import zipkin.reporter.Encoding;
import zipkin.reporter.okhttp3.OkHttpSender;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

import com.ewolff.microservice.order.clients.Customer;
import com.ewolff.microservice.order.clients.Item;

@Configuration
class SpringRestDataConfig extends RepositoryRestConfigurerAdapter {

	@Bean
	public RepositoryRestConfigurer repositoryRestConfigurer() {

		return new RepositoryRestConfigurerAdapter() {
			@Override
			public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
				config.exposeIdsFor(Order.class, Item.class, Customer.class);
			}
		};
	}
	@Bean
	public io.opentracing.Tracer zipkinTracer() {
		OkHttpSender okHttpSender = OkHttpSender.builder()
				.encoding(Encoding.JSON)
				.endpoint("http://zipkin-estee:9411/api/v1/spans")
				.build();
		AsyncReporter<Span> reporter = AsyncReporter.builder(okHttpSender).build();
		Tracing braveTracer = Tracing.newBuilder()
				.localServiceName("spring-boot")
				.reporter(reporter)
				.traceId128Bit(true)
				.sampler(Sampler.ALWAYS_SAMPLE)
				.build();
		return BraveTracer.create(braveTracer);
	}

}
