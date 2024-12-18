package Distribuido;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface TSPService extends Remote {
    double calcularDistancia(int mask, int u, double[][] dist, double[][] dp) throws RemoteException;
}
