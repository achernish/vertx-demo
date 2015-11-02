package com.inatec.infrastructure.db.repositories;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.List;
import java.util.Random;

/**
 * @author Anatoly Chernysh
 */
@Repository
@Transactional
@Slf4j
public class PaymentRepositoryImpl implements PaymentRepository {

    @PersistenceContext
    private EntityManager em;

    @Transactional(propagation = Propagation.REQUIRED)
    public boolean createAndQueryPayment() {
        Random random = new Random(System.currentTimeMillis());

        Query insertPaymentQuery = em.createNativeQuery("INSERT INTO payments (BATCHID, MERCHANTID, PAYMENTTYPEID, AMOUNT) VALUES (:BATCHID, :MERCHANTID, :PAYMENTTYPEID, :AMOUNT)");
        insertPaymentQuery.setParameter("BATCHID", 1);
        insertPaymentQuery.setParameter("MERCHANTID", random.nextInt(40) + 10);
        String creditCardNumber = String.format("%012d", random.nextInt(Integer.MAX_VALUE));
        insertPaymentQuery.setParameter("PAYMENTTYPEID", creditCardNumber);
        insertPaymentQuery.setParameter("AMOUNT", random.nextInt(1000));
        insertPaymentQuery.executeUpdate();
        log.debug("Payment {} has been inserted.", creditCardNumber);

        Query selectPaymentByCCNQuery = em.createNativeQuery("SELECT * FROM payments WHERE PAYMENTTYPEID = :PAYMENTTYPEID");
        selectPaymentByCCNQuery.setParameter("PAYMENTTYPEID", creditCardNumber);
        List payments  = selectPaymentByCCNQuery.getResultList();
        if (payments != null && !payments.isEmpty()) {
            Object paymentId = ((Object[])payments.get(0))[0];
            log.debug("Payment {} has found by credit card number {}.", paymentId, creditCardNumber);


            Query selectPaymentByIdQuery = em.createNativeQuery("SELECT * FROM payments WHERE ID = :ID");
            selectPaymentByIdQuery.setParameter("ID", paymentId);
            payments = selectPaymentByCCNQuery.getResultList();
            if (payments != null && !payments.isEmpty()) {
                log.debug("Payment {} has found by id {}.", ((Object[])payments.get(0))[0], creditCardNumber);
            }
        }

        Query selectSumQuery = em.createNativeQuery("SELECT sum(AMOUNT) FROM payments");
        Object sum = selectSumQuery.getSingleResult();
        log.debug("Payments sum {}.", sum);

        return true;
    }
}