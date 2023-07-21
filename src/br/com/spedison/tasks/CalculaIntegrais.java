package br.com.spedison.tasks;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

public class CalculaIntegrais {
    Function<Double, Double> funcao;
    Double inicio;
    Double fim;
    public Queue<Double> resultados;
    public Double resultadoFinal;

    public AtomicBoolean terminado = new AtomicBoolean(false);

    public CalculaIntegrais(Function<Double, Double> funcao, Double inicio, Double fim) {
        this.funcao = funcao;
        this.inicio = inicio;
        this.fim = fim;
    }

    public void geraProcessamentos() {
        ExecutorService pool = Executors.newFixedThreadPool(16);
        var quantidadeProcessamentos = 100_000;
        var intervalo = (fim - inicio) / quantidadeProcessamentos;
        var quantidadeIntervalosInterno = 200_000;
        resultados = new ConcurrentLinkedQueue();
        AtomicInteger contaTerminos = new AtomicInteger(0);
        System.out.println(new Date() + " - Iniciando a carga das tarefas.");
        final AtomicInteger iniciados = new AtomicInteger(0);
        IntStream
                .range(0, quantidadeProcessamentos)
                .mapToDouble(p -> inicio + (p * intervalo))
                .parallel()
                .forEach(i -> {
                            iniciados.incrementAndGet();
                            pool.execute(
                                    new CalculaUmaIntegral(
                                            "Parte-" + i + "-ate-" + (i + intervalo),
                                            i, i + intervalo,
                                            quantidadeIntervalosInterno,
                                            funcao, resultados, contaTerminos));
                        }
                );

        while (quantidadeProcessamentos > contaTerminos.getAcquire()) {
            System.out.println(new Date() + " - Quantidade de Processamentos Lancados é " + iniciados.get() + " - Terminados é " + contaTerminos.get());
            try {
                Thread.sleep(500);
                //System.out.println(new Date() + " - Aguardando processamento.");
            } catch (InterruptedException ie) {
            }
        }
        pool.shutdown();
        resultadoFinal = resultados
                .stream()
                .reduce(0., (a, b) -> a + b);
        System.out.println(new Date() + " - Processamento terminado.");
    }
}
