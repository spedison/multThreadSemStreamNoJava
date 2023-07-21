package br.com.spedison.main;

import br.com.spedison.tasks.CalculaIntegrais;

import java.util.Date;
import java.util.function.Function;

public class MainIntegral2Grau {
    public static void main(String[] args) {
        Function<Double, Double> funcao = (a) ->
                Math.pow(a, 2.) * 1.0D;
        Double inicio = Double.parseDouble(args[0]);
        Double fim = Double.parseDouble(args[1]);
        Integer N_THREADS = Integer.parseInt(args[2]);
        /**
         * Com um tempo de monitoramento de 5 ms.
         * Intel
         * 92,007000 segundos  - 32 Threads.
         * 93,410000 segundos  - 18 Threads.         *
         * 93,669000 segundos  - 17 Threads.
         * 93,669000 segundos  - 16 Threads.
         * 92,102000 segundos  - 15 Threads.
         * 93,857000 segundos  - 10 Threads.
         * 106,073000 segundos - 2 Threads.
         * ...
         * ARM (BananaPi)
         * 497.064000 segundos - 4 Threads.
         *
         */
        Integer quantidadeProcessamentos = 250_000;
        Integer quantidadeIntervalos = 100_000;
        CalculaIntegrais gi = new CalculaIntegrais(funcao, inicio, fim,
                N_THREADS, quantidadeIntervalos, quantidadeProcessamentos);
        var tempoInicio = System.currentTimeMillis();
        gi.geraProcessamentos();
        final Integer[] contaMonitoramento = {0};
        gi.aguardaProcessamento((a, b) -> {
            contaMonitoramento[0]++;
            if (contaMonitoramento[0] % 1_000 == 0) {
                System.out.println(new Date() + " - Pertentual = %06.2f %%".formatted(100. * (double) b / (double) a));
            }
        }, 5);
        var tempoFinal = System.currentTimeMillis();
        System.out.println(new Date() + " - Processamento terminado.");
        System.out.println("O Resultado da integral de %f a %f foi %f e demorou %f segundos".formatted(
                inicio, fim,
                gi.getResultadoFinal(),
                (double) (tempoFinal - tempoInicio) / 1000.)
        );
    }
}
