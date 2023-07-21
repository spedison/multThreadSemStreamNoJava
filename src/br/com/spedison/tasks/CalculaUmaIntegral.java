package br.com.spedison.tasks;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.IntStream;

public class CalculaUmaIntegral implements Runnable {

    private final int quantidadeIntervalos;
    private final String nome;
    private final double inicio;
    private final double fim;

    private final double passo;
    Queue<Double> resultados;

    Function<Double, Double> funcao;

    AtomicInteger contaTerminos;

    public CalculaUmaIntegral(String nome, double inicio, double fim, int quantidadeIntervalos, Function<Double, Double> funcao, Queue<Double> resultados, AtomicInteger contaTerminos) {
        this.nome = nome;
        this.inicio = inicio;
        this.fim = fim;
        this.passo = (fim - inicio) / quantidadeIntervalos;
        this.quantidadeIntervalos = quantidadeIntervalos;
        this.resultados = resultados;
        this.funcao = funcao;
        this.contaTerminos = contaTerminos;
    }

    @Override
    public void run() {
        //System.out.println(new Date() + " - Inicio " + nome);
        Double[] acc = {0.D};
        final Double[] posAtual = {inicio};
        IntStream
                .range(0, quantidadeIntervalos)
                .sequential()
                .forEach(i -> {
                            // Calculando com Retângulos
                            //double pontoMedio = (2. * posAtual[0] + passo) / 2.0;
                            //acc[0] += funcao.apply(pontoMedio) * passo;
                            // Calculando com trapézios
                            posAtual[0] += passo;
                            var in = inicio + (i * passo);
                            var fi = in + passo;
                            acc[0] += (passo * (funcao.apply(in) + funcao.apply(fi))) / 2.0;
                        });
                        resultados.add(acc[0]);
        int x = contaTerminos.incrementAndGet();
        //System.out.println(new Date() + " - Fim " + nome + " Numero processamento " + x);
    }
}
