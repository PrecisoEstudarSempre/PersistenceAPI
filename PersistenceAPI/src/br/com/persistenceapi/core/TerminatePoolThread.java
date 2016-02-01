/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.persistenceapi.core;

/**
 *
 * @author joao.maida
 */
public class TerminatePoolThread extends Thread {

    private JDBCConnectionPool jdbcConnectionPool;

    public TerminatePoolThread(JDBCConnectionPool jdbcConnectionPool) {
        this.jdbcConnectionPool = jdbcConnectionPool;
    }
    
    @Override
    public void run() {
        super.run();
        System.out.println("Thread iniciada");
        int timeoutCounter = 0;
        while (true) {
            if (jdbcConnectionPool.checkIfConnectionPoolIsFull()) {
                timeoutCounter++;
                if (jdbcConnectionPool.getTimeout() == timeoutCounter) {
                    jdbcConnectionPool.terminateAllConnections();
                    break;
                }
            }
            timeoutCounter = 0;
            System.out.println(timeoutCounter);
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
        System.out.println("Thread terminada");
    }
}
