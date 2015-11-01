package com.inatec.infrastructure.vertx;

import com.inatec.infrastructure.db.repositories.PaymentRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Anatoly Chernysh
 */
@Slf4j
public class MyVerticle extends AbstractVerticle {

    private int bindPort;

    private PaymentRepository paymentRepository;

    public MyVerticle() {
    }

    public MyVerticle(PaymentRepository paymentRepository, int bindPort) {
        this.paymentRepository = paymentRepository;
        this.bindPort = bindPort;
    }

    @Override
    public void start() throws Exception {
        log.debug("MyVerticle started!");

        vertx.eventBus().consumer(deploymentID(), message -> {
            System.out.println(deploymentID());
            vertx.undeploy(deploymentID());
        });

        Router router = Router.router(getVertx());

        router.route().handler(BodyHandler.create());
        router.get("/test").handler(this::handleGetTest);

        vertx.createHttpServer().requestHandler(router::accept).listen(bindPort);
    }

    private void handleGetTest(final RoutingContext routingContext) {
        log.debug("Start: getTest()");

        boolean result = paymentRepository.createAndQueryPayment();

        log.debug("Create response for getTest()");
        final HttpServerResponse response = routingContext.response();

        final JsonObject jsonObject = new JsonObject().put("code", result);
        response.putHeader("content-type", "application/json").end(jsonObject.encodePrettily());
        response.setStatusCode(result ? 200 : 500);

        log.debug("End: getTest()");
    }


    @Override
    public void stop() throws Exception {
        log.debug("MyVerticle stopped!");
    }
}