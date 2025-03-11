import java.io.*;
import java.util.*;

public class Compiler {
    CPU cpu;
    public Compiler(int memorySize){
        this.cpu = new CPU(memorySize);
    }

    public void compileAndRun(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();

                // Игнорируем пустые строки и комментарии
                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }

                // Исполняем команду
                cpu.executeInstruction(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

}
