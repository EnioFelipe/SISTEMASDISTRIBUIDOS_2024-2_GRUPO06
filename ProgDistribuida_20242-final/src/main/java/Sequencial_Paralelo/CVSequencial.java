package Sequencial_Paralelo;

import java.util.Arrays;

public class CVSequencial {
    private final int n;  // Número de cidades
    private final float[][] dist;  // Matriz de distâncias
    private final float[][] dp;  // DP para armazenar as menores distâncias
    private final int[][] caminho;  // Para reconstruir o caminho
    private static final float INF = Float.POSITIVE_INFINITY;

    // Construtor para inicializar as variáveis
    public CVSequencial(float[][] dist) {
        this.n = dist.length;
        this.dist = dist;
        this.dp = new float[1 << n][n];
        this.caminho = new int[1 << n][n];

        // Inicializa a tabela dp com o valor infinito
        for (float[] row : dp) {
            Arrays.fill(row, INF);
        }
    }

    // Método para resolver o TSP
    public double resolver() {
        // Define o ponto de partida como a cidade 0
        dp[1][0] = 0;

        // Preencher a tabela DP usando programação dinâmica
        for (int mask = 1; mask < (1 << n); mask++) {
            for (int u = 0; u < n; u++) {
                if ((mask & (1 << u)) != 0) {  // Se u está no conjunto representado por mask
                    for (int v = 0; v < n; v++) {
                        if ((mask & (1 << v)) == 0) {  // Se v não está em mask
                            double novaDist = dp[mask][u] + dist[u][v];
                            int novaMask = mask | (1 << v);
                            if (novaDist < dp[novaMask][v]) {
                                dp[novaMask][v] = (float) novaDist;
                                caminho[novaMask][v] = u;
                            }
                        }
                    }
                }
            }
        }

        // Finalizar o ciclo voltando ao ponto de partida
        double minCost = INF;
        int ultimo = -1;
        int finalMask = (1 << n) - 1;
        for (int i = 1; i < n; i++) {
            double custo = dp[finalMask][i] + dist[i][0];
            if (custo < minCost) {
                minCost = custo;
                ultimo = i;
            }
        }

        return minCost;
    }

    // Método para imprimir o caminho ótimo
    public void imprimirCaminho() {
        int mask = (1 << n) - 1;  // Máscara que representa todas as cidades visitadas
        int[] caminhoOtimo = new int[n + 1];  // +1 para incluir a cidade de início (0)
        int u = -1;  // Variável para armazenar a última cidade visitada

        // Encontra a última cidade visitada com o menor custo
        for (int i = 1; i < n; i++) {
            if (dp[mask][i] + dist[i][0] == dp[mask][i] + dist[i][0]) {
                u = i;
                break;
            }
        }

        // Reconstruir o caminho
        for (int i = n; i >= 0; i--) {
            caminhoOtimo[i] = u;
            int temp = caminho[mask][u];
            mask ^= (1 << u);  // Remove a cidade atual da máscara
            u = temp;
        }
        caminhoOtimo[0] = 0;  // Cidade inicial é 0

        // Exibir o caminho
        System.out.print("Caminho ótimo: ");
        for (int i = 0; i <= n; i++) {
            System.out.print(caminhoOtimo[i] + (i == n ? "" : " -> "));
        }
        System.out.println();
    }

    public static void main(String[] args) {
        // Matriz de distâncias entre as cidades
        float[][] dist = {
            {0, 40, 10, 60},
            {50, 0, 30, 20},
            {20, 60, 0, 50},
            {30, 20, 80, 0},
                {0, 40, 10, 60},
                {50, 0, 30, 20},
                {20, 60, 0, 50},
                {30, 20, 80, 0}
        };

        CVSequencial caixeiro = new CVSequencial(dist);

        // Medir o tempo de execução
        long inicio = System.nanoTime();
        float custoMinimo = (float) caixeiro.resolver();
        long fim = System.nanoTime();

        // Calcular e exibir o tempo de execução
        double tempoExecucao = (fim - inicio) / 1_000_000.0;  // Converter para milissegundos
        System.out.println("Custo mínimo: " + custoMinimo);
        System.out.printf("Tempo de execução: %.3f ms%n", tempoExecucao);

        // Imprimir o caminho ótimo
        caixeiro.imprimirCaminho();
    }
}
