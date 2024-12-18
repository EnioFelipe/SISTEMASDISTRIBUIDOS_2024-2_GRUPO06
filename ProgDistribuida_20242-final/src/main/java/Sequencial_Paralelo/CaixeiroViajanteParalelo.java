package Sequencial_Paralelo;

import java.util.Arrays;

public class CaixeiroViajanteParalelo {
    private final int numCidades;
    private final float[][] matrizDistancias;
    private final float[][] dp;
    private final int[][] caminhoReconstruido;
    private static final float INFINITO = Float.POSITIVE_INFINITY;

    public CaixeiroViajanteParalelo(float[][] matrizDistancias) {
        this.numCidades = matrizDistancias.length;
        this.matrizDistancias = matrizDistancias;
        this.dp = new float[1 << numCidades][numCidades];
        this.caminhoReconstruido = new int[1 << numCidades][numCidades];

        for (float[] linha : dp) {
            Arrays.fill(linha, INFINITO);
        }
        for (int[] linha : caminhoReconstruido) {
            Arrays.fill(linha, -1);
        }
    }

    public float resolver() {
        dp[1][0] = 0; // A cidade inicial (cidade 0) tem distância 0 para si mesma

        // Criação e execução das threads
        Thread[] threads = new Thread[(1 << numCidades) - 1];
        for (int mascara = 1; mascara < (1 << numCidades); mascara++) {
            final int mascaraAtual = mascara; // Variável final para uso na thread
            threads[mascara - 1] = new Thread(() -> preencherDP(mascaraAtual));
            threads[mascara - 1].start();
        }

        // Espera todas as threads terminarem
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        float custoMinimo = INFINITO;
        int ultimaCidade = -1;
        int mascaraFinal = (1 << numCidades) - 1;

        for (int i = 1; i < numCidades; i++) {
            float custoAtual = dp[mascaraFinal][i] + matrizDistancias[i][0]; // Retorno à cidade 0
            if (custoAtual < custoMinimo) {
                custoMinimo = custoAtual;
                ultimaCidade = i;
            }
        }

        if (ultimaCidade != -1) {
            caminhoReconstruido[mascaraFinal][0] = ultimaCidade;
        }

        return custoMinimo;
    }

    private void preencherDP(int mascara) {
        for (int u = 0; u < numCidades; u++) {
            if ((mascara & (1 << u)) != 0) { // Verifica se a cidade 'u' está no conjunto
                for (int v = 0; v < numCidades; v++) {
                    if ((mascara & (1 << v)) == 0) { // Verifica se a cidade 'v' ainda não foi visitada
                        float novaDistancia = dp[mascara][u] + matrizDistancias[u][v];
                        int novaMascara = mascara | (1 << v);

                        synchronized (dp) { // Sincroniza para evitar condições de corrida
                            if (novaDistancia < dp[novaMascara][v]) {
                                dp[novaMascara][v] = novaDistancia;
                                caminhoReconstruido[novaMascara][v] = u;
                            }
                        }
                    }
                }
            }
        }
    }

    public void imprimirCaminho() {
        int mascara = (1 << numCidades) - 1; // Máscara final com todas as cidades visitadas
        int cidadeAtual = caminhoReconstruido[mascara][0]; // Última cidade antes de retornar à cidade inicial

        int[] caminhoOtimo = new int[numCidades + 1]; // +1 para incluir a cidade 0 no final
        caminhoOtimo[numCidades] = 0; // A cidade final é sempre a cidade 0

        for (int i = numCidades - 1; i >= 0; i--) {
            caminhoOtimo[i] = cidadeAtual;
            int proximaCidade = caminhoReconstruido[mascara][cidadeAtual];
            mascara ^= (1 << cidadeAtual); // Remove a cidade atual da máscara
            cidadeAtual = proximaCidade;
        }
        caminhoOtimo[0] = 0; // Cidade inicial é 0

        // Exibe o caminho ótimo
        System.out.print("Caminho ótimo: ");
        for (int i = 0; i <= numCidades; i++) {
            System.out.print(caminhoOtimo[i]);
            if (i < numCidades) {
                System.out.print(" -> ");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        float[][] matrizDistancias = {
            {0, 40, 10, 60},
            {50, 0, 30, 20},
            {20, 60, 0, 50},
            {30, 20, 80, 0},
            {10, 20, 0, 30}
        };

        CaixeiroViajanteParalelo tsp = new CaixeiroViajanteParalelo(matrizDistancias);

        long inicio = System.nanoTime();
        float custoMinimo = tsp.resolver();
        long fim = System.nanoTime();

        System.out.printf("Custo mínimo: %.2f%n", custoMinimo);
        System.out.printf("Tempo de execução: %.3f ms%n", (fim - inicio) / 1_000_000.0);

        tsp.imprimirCaminho();
    }
}
