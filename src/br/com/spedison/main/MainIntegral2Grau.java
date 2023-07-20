package br.com.spedison.main;

import br.com.spedison.tasks.GeraIntegrais;

import java.util.function.Function;

public class MainIntegral2Grau {
    public static void main(String[] args) {
        Function<Double, Double> funcao = (a) ->
                Math.pow(a, 2.) * 1.0D;
        Double inicio = Double.parseDouble(args[0]);
        Double fim = Double.parseDouble(args[1]);
        GeraIntegrais gi = new GeraIntegrais(funcao, inicio, fim);
        gi.geraProcessamentos();
        System.out.println("O Resultado da integral de %f a %f foi %f".formatted(
                inicio,
                fim,
                gi.resultadoFinal));
    }
}
