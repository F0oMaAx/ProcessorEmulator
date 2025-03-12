import java.io.*;

public class Compiler {
    CPU cpu = new CPU();

    public void compileAndRun(String filename) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty() || line.startsWith(";")) {
                    continue;
                }   // ignore comments and empty lines

                // Executing command
                cpu.executeInstruction(line);
            }
        } catch (IOException e) {
            System.err.println("File read error: " + e.getMessage());
        }
    }

}
