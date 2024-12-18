package Distribuido;

import java.rmi.Naming;
import java.util.Arrays;

public class TSPClient {
    private final int n;  // Número de cidades
    private final double[][] dist;  // Matriz de distâncias
    private final double[][] dp;  // DP para armazenar as menores distâncias
    private final int[][] parent; // Para rastrear o caminho ótimo
    private static final double INF = Double.POSITIVE_INFINITY;

    public TSPClient(double[][] dist) {
        this.n = dist.length;
        this.dist = dist;
        this.dp = new double[1 << n][n];
        this.parent = new int[1 << n][n];

        for (double[] row : dp) {
            Arrays.fill(row, INF);
        }
        for (int[] row : parent) {
            Arrays.fill(row, -1);
        }
        dp[1][0] = 0;  // Define o ponto de partida como a cidade 0
    }

    public double resolver() {
        try {
            TSPService tspService = (TSPService) Naming.lookup("rmi://localhost/TSPService");

            for (int mask = 1; mask < (1 << n); mask++) {
                for (int u = 0; u < n; u++) {
                    if ((mask & (1 << u)) != 0) {
                        for (int v = 0; v < n; v++) {
                            if ((mask & (1 << v)) == 0) {
                                int novaMask = mask | (1 << v);
                                double custo = dp[mask][u] + dist[u][v];

                                if (custo < dp[novaMask][v]) {
                                    dp[novaMask][v] = custo;
                                    parent[novaMask][v] = u;
                                }
                            }
                        }
                    }
                }
            }

            // Finalizar o ciclo voltando ao ponto de partida
            double minCost = INF;
            int finalMask = (1 << n) - 1;
            int lastCity = -1;

            for (int i = 1; i < n; i++) {
                double custo = dp[finalMask][i] + dist[i][0];
                if (custo < minCost) {
                    minCost = custo;
                    lastCity = i;
                }
            }

            if (lastCity != -1) {
                reconstruirCaminho(finalMask, lastCity);
            }

            return minCost;
        } catch (Exception e) {
            e.printStackTrace();
            return INF;
        }
    }

    private void reconstruirCaminho(int mask, int u) {
        int[] caminho = new int[n + 1];
        int index = n;
        caminho[index--] = 0; // Retorna ao ponto de partida

        while (u != -1) {
            caminho[index--] = u;
            int prev = parent[mask][u];
            mask ^= (1 << u);
            u = prev;
        }

        System.out.println("Caminho ótimo: " + Arrays.toString(caminho));
    }

    public static void main(String[] args) {
        double[][] dist = {
            {0, 40, 10, 60},
            {50, 0, 30, 20},
            {20, 60, 0, 50},
            {30, 20, 80, 0},
            {0, 40, 10, 60},
            {50, 0, 30, 20},
            {20, 60, 0, 50},
            {30, 20, 80, 0}
        };

        TSPClient caixeiro = new TSPClient(dist);

        long inicio = System.nanoTime();
        double custoMinimo = caixeiro.resolver();
        long fim = System.nanoTime();

        double tempoExecucao = (fim - inicio) / 1_000_000.0;  // Converter para milissegundos
        System.out.println("Custo mínimo: " + custoMinimo);
        System.out.printf("Tempo de execução: %.3f ms%n", tempoExecucao);
    }
}
