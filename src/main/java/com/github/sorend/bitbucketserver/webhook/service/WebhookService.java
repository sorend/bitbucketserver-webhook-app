package com.github.sorend.bitbucketserver.webhook.service;

import io.helidon.common.http.Http;
import io.helidon.webserver.Routing;
import io.helidon.webserver.ServerRequest;
import io.helidon.webserver.ServerResponse;
import io.helidon.webserver.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.JsonException;
import java.util.List;
import java.util.Map;

public class WebhookService implements Service {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebhookService.class);

    WebhookDispatcher dispatcher;

    public WebhookService(WebhookDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @Override
    public void update(Routing.Rules rules) {
        rules.post(this::processEventHandler);
    }

    private void processEventHandler(ServerRequest request, ServerResponse response) {
        String xEventKey = request.headers().value("X-Event-Key").orElse("invalid");

        if ("diagnostics:ping".equals(xEventKey)) {
            processPing(response);
        }
        else {
            request.content().as(String.class)
                    .thenAccept(x -> processEvent(xEventKey, request, x, response)).exceptionally(ex -> processErrors(ex, request, response));
        }
    }

    private void processEvent(String eventKey, ServerRequest serverRequest, String json, ServerResponse response) {
        try {
            dispatcher.dispatch(eventKey, serverRequest, json);
            response.status(200).send(eventKey + ": Received");
        } catch (Exception e) {
            LOGGER.warn("Error triggering inbound service", e);
            response.status(500).send(eventKey + ": " + e.getMessage());
        }
    }

    private void processPing(ServerResponse response) {
        response.status(200).send("pong");
    }

    private static <T> T processErrors(Throwable ex, ServerRequest request, ServerResponse response) {
        if (ex.getCause() instanceof JsonException) {
            LOGGER.warn("Invalid JSON", ex);
            response.status(Http.Status.BAD_REQUEST_400).send("Invalid JSON");
        } else {
            LOGGER.warn("Internal error", ex);
            response.status(Http.Status.INTERNAL_SERVER_ERROR_500).send("Internal error");
        }
        return null;
    }
}
