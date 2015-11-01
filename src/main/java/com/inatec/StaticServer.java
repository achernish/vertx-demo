package com.inatec;

import com.inatec.infrastructure.db.repositories.PaymentRepository;
import com.inatec.infrastructure.vertx.MyVerticle;
import io.vertx.core.Vertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Anatoly Chernysh
 */
@Component
public class StaticServer {

    @Autowired
    private Vertx vertx;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${vertx.bindPort}")
    private int vertxBindPort;

    @PostConstruct
    public void createServer(){
         vertx.deployVerticle(new MyVerticle(paymentRepository, vertxBindPort));
    }
}