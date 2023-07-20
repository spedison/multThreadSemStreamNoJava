package br.com.spedison.test.tasks;

import br.com.spedison.tasks.CalculaUmaIntegral;
import org.junit.jupiter.api.Assertions;

import java.util.Date;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class CalculaUmaIntegralTest {

    @org.junit.jupiter.api.Test
    void run() {
        Queue<Double> results = new LinkedList<>();
        CalculaUmaIntegral calc = new CalculaUmaIntegral(
                "teste",
                0,
                1,
                0.001,
                (x)->x*x,
                results,
                new AtomicInteger(0));
        calc.run();
        Double r = results.poll();
        System.out.println(new Date() + " - Resultado = %f".formatted(r));
        Assertions.assertEquals(r,1./3.,0.01);
    }
}