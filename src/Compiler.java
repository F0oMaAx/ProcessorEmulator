import java.io.*;

public class Compiler {
    CPU cpu;
    public Compiler(int memorySize, int stacksize){
        this.cpu = new CPU(memorySize, stacksize);
    }

    public void compileAndRun(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }   // Игнорируем пустые строки и комментарии

                // Исполняем команду
                cpu.executeInstruction(line);
            }
        } catch (IOException e) {
            System.err.println("Ошибка чтения файла: " + e.getMessage());
        }
    }

}
