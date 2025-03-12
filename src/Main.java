public class Main {
    public static void main(String[] args) {

        Compiler compiler = new Compiler();
        compiler.compileAndRun("D:\\JAVA Proj\\ProcessorEmulator\\src\\assembler.asm");

        System.out.println("Execution finished.");
    }
}