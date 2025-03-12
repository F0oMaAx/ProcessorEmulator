public class Main {
    public static void main(String[] args) {
        // НАДО СТЭК ДЕЛАТЬ ЕПТ
        Compiler compiler = new Compiler(8192 * 1024, 8192 * 1024);
        compiler.compileAndRun("D:\\JAVA Proj\\ProcessorEmulator\\src\\assembler.asm");

        System.out.println("Execution finished.");
    }
}