import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.Semaphore;

class Sem {

    // Semáforo para a região crítica
    private static final Semaphore mutex = new Semaphore(1);

    // Variável compartilhada para acumular a soma total
    private static long totalSum = 0;

    public static int sum(FileInputStream fis) throws IOException {
        int byteRead;
        int sum = 0;
        
        while ((byteRead = fis.read()) != -1) {
            sum += byteRead;
        }

        return sum;
    }

    public static long sum(String path) throws IOException {
        Path filePath = Paths.get(path);
        if (Files.isRegularFile(filePath)) {
            try (FileInputStream fis = new FileInputStream(filePath.toString())) {
                return sum(fis);
            }
        } else {
            throw new RuntimeException("Non-regular file: " + path);
        }
    }

    // Método para atualizar a soma total de forma segura
    public static void updateTotalSum(long sum) {
        try {
            mutex.acquire(); // Adquire o semáforo
            try {
                totalSum += sum;
            } finally {
                mutex.release(); // Libera o semáforo
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(e.getMessage());
        }
    }

    // Método para obter a soma total
    public static long getTotalSum() {
        return totalSum;
    }
    
    public static void main(String[] args) {
	    if (args.length < 1) {
	        System.err.println("Usage: java Sum filepath1 filepath2 filepathN");
	        System.exit(1);
	    }

	    // Criar e iniciar uma nova thread para cada tarefa
	    for (String path : args) {
	        Task task = new Task(path);
	        Thread thread = new Thread(task);
	        thread.start();
	    }

	    // Imprimir a soma total após todas as threads concluírem
	    System.out.println("Total Sum: " + Sem.getTotalSum());
    }
}

class Task implements Runnable {
    private String path;

    public Task(String path) {
        this.path = path;    
    }

    @Override
    public void run() {
        try {
            long sum = Sem.sum(path);
            // Atualizar a soma total de forma segura
            Sem.updateTotalSum(sum); // Região crítica
            System.out.println(path + " : " + sum + " Thread Sum Java");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

