package br.com.spedison.tasks;

import java.util.Date;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

public class CalculaUmaIntegral implements Runnable {

    String nome;
    double inicio;
    double fim;

    double passo;
    Queue<Double> resultados;

    Function<Double, Double> funcao;

    AtomicInteger contaTerminos;

    public CalculaUmaIntegral(String nome, double inicio, double fim, double passo, Function<Double, Double> funcao, Queue<Double> resultados, AtomicInteger contaTerminos) {
        this.nome = nome;
        this.inicio = inicio;
        this.fim = fim;
        this.passo = passo;
        this.resultados = resultados;
        this.funcao = funcao;
        this.contaTerminos = contaTerminos;
    }

    @Override
    public void run() {
        System.out.println(new Date() + " - Inicio " + nome);
        Double acc = 0.D;
        for (double atual = inicio; atual < fim; atual += passo) {

            double pontoMedio = (2.*atual + passo)/2.0;
            acc += funcao.apply(pontoMedio) * passo;

            //var in = atual;
            //var fi = Math.min(in + passo, fim);
            //acc += (passo * (funcao.apply(in) + funcao.apply(fi))) / 2.0;
        }
        resultados.add(acc);
        int x = contaTerminos.incrementAndGet();
        System.out.println(new Date() + " - Fim " + nome + " Numero processamento " + x);
    }
}
