package com.example.demo5.writer;

import org.a2aproject.sdk.server.PublicAgentCard;
import org.a2aproject.sdk.spec.AgentCapabilities;
import org.a2aproject.sdk.spec.AgentCard;
import org.a2aproject.sdk.spec.AgentInterface;
import org.a2aproject.sdk.spec.AgentSkill;
import org.a2aproject.sdk.spec.TransportProtocol;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import java.util.Collections;
import java.util.List;
import org.eclipse.microprofile.config.inject.ConfigProperty;

@ApplicationScoped
public class CreativeWriterAgentCardProducer {

    @Inject
    @ConfigProperty(name = "a2a.server.url", defaultValue = "http://localhost:8080")
    String serverUrl;

    @Produces
    @PublicAgentCard
    public AgentCard agentCard() {
        return AgentCard.builder()
                .name("Creative Writer")
                .description("Generate a story based on the given topic")
                .url(serverUrl)
                .version("1.0.0")
                .capabilities(AgentCapabilities.builder()
                        .streaming(true)
                        .pushNotifications(false)
                        .build())
                .defaultInputModes(Collections.singletonList("text"))
                .defaultOutputModes(Collections.singletonList("text"))
                .skills(Collections.singletonList(AgentSkill.builder()
                        .id("creative_writer")
                        .name("Creative Writer")
                        .description("Generate a story based on the given topic")
                        .tags(Collections.singletonList("writing"))
                        .build()))
                .preferredTransport(TransportProtocol.JSONRPC.asString())
                .supportedInterfaces(List.of(
                        new AgentInterface(TransportProtocol.JSONRPC.asString(), serverUrl)))
                .build();
    }
}
