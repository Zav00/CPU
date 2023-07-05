import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Main {
    public static BufferedReader reader;
    public static void main(String[] args) throws IOException {
        String path = "C:\\Users\\vaheg\\IdeaProjects\\CPU\\src\\commands.txt";
        CPU cpu = new CPU();

        reader = new BufferedReader(new FileReader(path));
        String line;
        reader.mark(1000);
        while ((line = reader.readLine()) != null) {
            cpu.decode(line);
            cpu.execute();
        }

        cpu.dumpMemory();
        cpu.printRegisters();
    }
}