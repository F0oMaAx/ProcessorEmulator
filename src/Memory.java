public class Memory {
    private final byte[] memory;
    public int memorySize;

    public Memory() {
        memory = new byte[0x00FFFFF]; //16 Mib of memory
        memorySize = memory.length;
    }

    public void write(int address, byte value) {
        if (address >= 0 && address < memory.length) {
            memory[address] = value;
        } else {
            throw new IllegalArgumentException("Memory access out of bounds");
        }
    }

    public byte read(int address) {
        if (address >= 0 && address < memory.length) {
            return memory[address];
        } else {
            throw new IllegalArgumentException("Memory access out of bounds");
        }
    }
}
