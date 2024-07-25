import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

class Sum_Conc {

    // Semáforo para a região crítica
    private static final Semaphore mutex = new Semaphore(1);

    // Variável compartilhada para acumular a soma total
    private static long totalSum = 0;

    public static int sum(FileInputStream fis, long start, long end) throws IOException {
        int byteRead;
        int sum = 0;
        fis.getChannel().position(start);
        for (long i = start; i < end; i++) {
            if ((byteRead = fis.read()) == -1) {
                break;
            }
            sum += byteRead;
        }
        return sum;
    }

    public static long sum(String path, long start, long end) throws IOException {
        File file = new File(path);
        if (file.isFile()) {
            try (FileInputStream fis = new FileInputStream(file)) {
                return sum(fis, start, end);
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

        List<Thread> threads = new ArrayList<>();

        for (String path : args) {
            File file = new File(path);
            long fileSize = file.length();
            long chunkSize = fileSize / 4;

            for (int i = 0; i < 4; i++) {
                long start = i * chunkSize;
                long end = (i == 3) ? fileSize : (i + 1) * chunkSize;
                Task task = new Task(path, start, end);
                Thread thread = new Thread(task);
                threads.add(thread);
                thread.start();
            }
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                thread.currentThread().interrupt();
                System.out.println(e.getMessage());
            }
        }

        // Imprimir a soma total após todas as threads concluírem
        System.out.println("Total Sum: " + Sum_Conc.getTotalSum());
    }
}

class Task implements Runnable {
    private String path;
    private long start;
    private long end;

    public Task(String path, long start, long end) {
        this.path = path;
        this.start = start;
        this.end = end;
    }

    @Override
    public void run() {
        try {
            long sum = Sum_Conc.sum(path, start, end);
            // Atualizar a soma total de forma segura
            Sum_Conc.updateTotalSum(sum); // Região crítica
            System.out.println(path + " [" + start + ", " + end + "] : " + sum + " Thread Sum Java");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

