package br.com.spedison.tasks;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.IntStream;


@Data
@RequiredArgsConstructor(onConstructor_ = {@NotNull})
public class CalculaIntegrais {
    @NotNull
    private Function<Double, Double> funcao;

    @NotNull
    private Double inicio;

    @NotNull
    private Double fim;

    private Queue<Double> resultados;
    private Double resultadoFinal;

    @NotNull
    private int N_THREADS;

    private AtomicBoolean terminado = new AtomicBoolean(false);
    private AtomicInteger contaTerminos = new AtomicInteger(0);
    private AtomicInteger iniciados = new AtomicInteger(0);

    private Integer quantidadeProcessamentos = 100_000;
    private Integer quantidadeIntervalos = 200_000;
    private ExecutorService pool;

    public CalculaIntegrais(Function<Double, Double> funcao, Double inicio, Double fim,
                            Integer nThreads, Integer quantidadeIntervalos, Integer quantidadeProcessamentos) {
        this.funcao = funcao;
        this.inicio = inicio;
        this.fim = fim;
        this.N_THREADS = nThreads;
        this.quantidadeIntervalos = quantidadeIntervalos;
        this.quantidadeProcessamentos = quantidadeProcessamentos;
    }

    public void aguardaProcessamento(Integer interval) {
        BiConsumer<Integer, Integer> mostraMsg = (a, b) -> {
            System.out.println(new Date() + " - Quantidade de Processamentos Lancados é " + a + " - Terminados é " + b);
        };
        aguardaProcessamento(mostraMsg, interval);
    }

    public void aguardaProcessamento(BiConsumer<Integer, Integer> mostraMsg, Integer interval) {
        while (quantidadeProcessamentos > contaTerminos.getAcquire()) {
            mostraMsg.accept(iniciados.get(), contaTerminos.get());
            try {
                Thread.sleep(interval);
            } catch (InterruptedException ie) {
            }
        }
        System.out.println(new Date() + " - Threads finalizadas.");
        pool.shutdown();
        System.out.println(new Date() + " - Pool de Threads finalizado.");
        resultadoFinal = resultados
                .stream()
                .reduce(0., (a, b) -> a + b);

    }

    public void geraProcessamentos() {
        pool = Executors.newFixedThreadPool(N_THREADS);
        var intervalo = (fim - inicio) / quantidadeProcessamentos;
        resultados = new ConcurrentLinkedQueue();
        contaTerminos.set(0);
        iniciados.set(0);
        System.out.println(new Date() + " - Iniciando a carga das tarefas.");
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
                                            quantidadeIntervalos,
                                            funcao, resultados, contaTerminos));
                        }
                );
    }
}