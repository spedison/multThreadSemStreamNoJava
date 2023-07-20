package br.com.spedison.tasks;

import java.util.Date;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

public class GeraIntegrais {
    Function<Double, Double> funcao;
    Double inicio;
    Double fim;
    public Queue<Double> resultados;
    public Double resultadoFinal;

    public AtomicBoolean terminado = new AtomicBoolean(false);

    public GeraIntegrais(Function<Double, Double> funcao, Double inicio, Double fim) {
        this.funcao = funcao;
        this.inicio = inicio;
        this.fim = fim;
    }

    public void geraProcessamentos() {
        ExecutorService pool = Executors.newFixedThreadPool(16);
        var intervalo = (fim - inicio) / 100_000D;
        var intervaloInterno = intervalo / 200_000D;
        resultados = new ConcurrentLinkedQueue();
        AtomicInteger contaTerminos = new AtomicInteger(0);
        System.out.println(new Date() + " - Iniciando a carga das tarefas.");
        final int iniciados[] = {0};
        IntStream
                .range(0, 10_000)
                .mapToDouble(p -> inicio + (p * intervalo))
                .parallel()
                .forEach(i -> {
                            iniciados[0]++;
                            pool.execute(
                                    new CalculaUmaIntegral(
                                            "Parte-" + i + "-ate-" + (i + intervalo),
                                            i, i + intervalo,
                                            intervaloInterno,
                                            funcao, resultados, contaTerminos));

                        }
                );

        while (iniciados[0] > contaTerminos.getAcquire()) {
            try {
                Thread.sleep(500);
                System.out.println(new Date() + " - Aguardando processamento.");
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
