package io.confluent.developer.spring_ccloud;

import org.apache.logging.log4j.Marker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;
import com.mongodb.client.model.Facet;


import java.time.Duration;
import java.util.stream.Stream;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import com.github.javafaker.Faker;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringCcloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(SpringCcloudApplication.class, args);
	}

}

@RequiredArgsConstructor
@Component

class Producer {
	private final KafkaTemplate<Integer, String> template;
    Faker faker;
    @EventListener(ApplicationStartedEvent.class)

    public void generate(){
        faker = Faker.instance();
        final Flux<Long> interval = Flux.interval(Duration.ofMillis(1_000));
        final Flux<String> quotes = Flux.fromStream(Stream.generate(()-> faker.hobbit().quote()));
        Flux.zip(interval, quotes).map(it -> template.send("topic_1", faker.random().nextInt(42), it.getT2())).blockLast();
    }
}
