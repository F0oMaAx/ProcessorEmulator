public class CPU {
    private final Registers registers = new Registers();
    private final ALU alu = new ALU();

    public CPU(int memorySize) {
        Memory memory = new Memory(memorySize); // 1 MB памяти
    }

    public void executeInstruction(String instruction) {
        alu.execute(instruction);
    }

    public void dumpRegisters() {
        registers.dumpRegisters();
    }
}

//comment :D
