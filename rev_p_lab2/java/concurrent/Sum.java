import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.*;

public class Sum {

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
       	    FileInputStream fis = new FileInputStream(filePath.toString());
            return sum(fis);
        } else {
            throw new RuntimeException("Non-regular file: " + path);
        }
    }

    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            System.err.println("Usage: java Sum filepath1 filepath2 filepathN");
            System.exit(1);
        }

	//many exceptions could be thrown here. we don't care
        for (String path : args) {
            Task task = new Task(path);
            Thread thread = new Thread(task);
            thread.start();
        }
    }
}

class Task implements Runnable {
    private String path;

    public Task(String path) {
        this.path = path;    
    }

    @Override
    public void run(){
        try {
            long sum = Sum.sum(path);
            System.out.println(path + " : " + sum + " Thread Sum Java");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
