public class Main {
    public static void main(String[] args) {
        double startTime = System.nanoTime();

        Compiler compiler = new Compiler();
        compiler.compileAndRun("D:\\JAVA Proj\\ProcessorEmulator\\src\\assembler.asm");

        double stopTime = System.nanoTime();

        System.out.println("Execution finished.");
        System.out.println("Execution time is: " + (stopTime - startTime) / 1000000000 + " sec");
    }
}