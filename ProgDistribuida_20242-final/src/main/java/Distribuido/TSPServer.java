package Distribuido;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class TSPServer extends UnicastRemoteObject implements TSPService {
    protected TSPServer() throws RemoteException {
        super();
    }

    @Override
    public double calcularDistancia(int mask, int u, double[][] dist, double[][] dp) throws RemoteException {
        double minDist = Double.POSITIVE_INFINITY;

        for (int v = 0; v < dist.length; v++) {
            if ((mask & (1 << v)) == 0) {  // Se a cidade v ainda não foi visitada
                int novaMask = mask | (1 << v);
                double novaDist = dp[mask][u] + dist[u][v];

                if (novaDist < dp[novaMask][v]) {
                    dp[novaMask][v] = novaDist;
                }
                minDist = Math.min(minDist, dp[novaMask][v]);
            }
        }
        return minDist;
    }

    public static void main(String[] args) {
        try {
            java.rmi.registry.LocateRegistry.createRegistry(1099); // Porta padrão do RMI
            TSPServer server = new TSPServer();
            Naming.rebind("TSPService", server);
            System.out.println("Servidor TSP pronto.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
