package main;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.security.PublicKey;
import java.util.ArrayList;

public interface RMIInterface extends Remote {
	
	public String getLeftNode(int level) throws RemoteException ;
	public String getRightNode(int level) throws RemoteException ;
	public void setLeftNode(int level, NodeInfo newNode) throws RemoteException;
	public void setRightNode(int level,NodeInfo newNode) throws RemoteException;
	public int getNumID() throws RemoteException;
	public String getNameID() throws RemoteException;
	public NodeInfo searchByNameID(String targetString) throws RemoteException;
	public NodeInfo searchByNumID(int targetNum) throws RemoteException;
	public NodeInfo searchName(String searchTarget,int level,int direction) throws RemoteException;
	public NodeInfo searchNum(int searchTarget,int level) throws RemoteException;
	public int getLeftNumID(int level) throws RemoteException;
	public int getRightNumID(int level) throws RemoteException;
	public String getLeftNameID(int level) throws RemoteException;
	public String getRightNameID(int level) throws RemoteException;
	public NodeInfo insertSearch(int level, int direction, String target) throws RemoteException;
	
}
