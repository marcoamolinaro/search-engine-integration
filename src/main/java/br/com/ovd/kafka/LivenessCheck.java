package br.com.ovd.kafka;

import jakarta.inject.Inject;
import org.apache.kafka.streams.KafkaStreams;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Liveness;

@Liveness
public class LivenessCheck implements HealthCheck {

    @Inject
    KafkaStreams streams;

    @Override
    public HealthCheckResponse call() {
        var streamState = this.streams.state();
        var responseBuilder = HealthCheckResponse
                .named("stream")
                .withData("state", streamState.toString());

        if (streamState.isRunningOrRebalancing()) {
            responseBuilder.up();
        } else  {
            responseBuilder.down();
        }
        return responseBuilder.build();
    }
}