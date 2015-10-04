package com.inatec;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientResponse;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

/**
 * @author Anatoly Chernysh
 */
public class MyVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
        System.out.println("MyVerticle started!");

        vertx.eventBus().consumer(deploymentID(), message -> {
            System.out.println(deploymentID());
            vertx.undeploy(deploymentID());
        });

        Router router = Router.router(getVertx());

        router.route().handler(BodyHandler.create());
        router.get("/test").handler(this::handleGetTest);

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

    private void handleGetTest(final RoutingContext routingContext) {
        System.out.println("Start: getTest()");

        try {
           Thread.sleep(2000);
        }
        catch (InterruptedException ie) {
        }

        System.out.println("Create response for getTest()");
        final HttpServerResponse response = routingContext.response();

        final JsonObject jsonObject = new JsonObject()
                .put("code", "0")
                .put("desc", "ok");

        response.putHeader("content-type", "application/json").end(jsonObject.encodePrettily());

        System.out.println("End: getTest()");
    }


    @Override
    public void stop() throws Exception {
        System.out.println("MyVerticle stopped!");
    }

    public static void main(String[] args) throws InterruptedException {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new MyVerticle(), new Handler<AsyncResult<String>>() {
            @Override
            public void handle(AsyncResult<String> result) {
                if (result.succeeded()) {
                    String deploymentId = result.result();

                    try {
                        Thread.sleep(1000);
                    }
                    catch (InterruptedException ie) {
                    }

                    vertx.eventBus().send(deploymentId, null);

                    //vertx.undeploy(deploymentId);
                }
            }
        });

        HttpClient httpClient = vertx.createHttpClient();
        httpClient.getNow(8080, "localhost", "/test", new Handler<HttpClientResponse>() {

            @Override
            public void handle(HttpClientResponse httpClientResponse) {
                httpClientResponse.bodyHandler(new Handler<Buffer>() {
                    @Override
                    public void handle(Buffer buffer) {
                        System.out.println("Response (" + buffer.length() + ")");
                        System.out.println(buffer.getBuffer(0, buffer.length()));
                    }
                });
            }
        });

        //vertx.close();
    }
}
