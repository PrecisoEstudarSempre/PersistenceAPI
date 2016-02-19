package br.com.persistenceapi.core.pool;

/**
 * Thread usada para a liberação das conexões do pool. É necessário utilizar uma thread para a realizar a liberação pois, 
 * essa tarefa deve ser feito de forma paralela juntamente com as tarefas desempenhadas pelo pool.
 * @author Preciso Estudar Sempre - precisoestudarsempre@gmail.com
 */
public class TerminatePoolThread extends Thread {

    /*instância do pool*/
    private final JDBCConnectionPool jdbcConnectionPool;

    /**
     * Construtor da Thread.
     * @param jdbcConnectionPool Representa o pool de conexões. É necessário receber a instância do pool para que, possa
     * realizar acesso a propriedade timeout.
     */
    public TerminatePoolThread(JDBCConnectionPool jdbcConnectionPool) {
        this.jdbcConnectionPool = jdbcConnectionPool;
    }
    
    /**
     * Implementação de método que contém o comportamento executado pela thread. A thread verificará com o delay de meio segundo
     * se todas as conexões não estão sendo utilizadas. Se não estiverem, o contador do timeout começa sua contagem.
     * Caso contrário, o contador é zerado e o código cliente pode usar a conexão.
     */
    @Override
    public void run(){
        super.run();
        System.out.println("Thread de timeout iniciada");
        int timeoutCounter = 0;
        while (true) {
            if (jdbcConnectionPool.checkIfConnectionPoolIsFull()) {
                timeoutCounter++;
                if (jdbcConnectionPool.getTimeout() == timeoutCounter) {
                    jdbcConnectionPool.terminateAllConnections();
                    break;
                }
            } else {
                timeoutCounter = 0;
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException ex) {                
                ex.printStackTrace();
            }
        }
        System.out.println("Thread de timeout terminada");
    }
}
