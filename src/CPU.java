import java.util.Arrays;
import java.util.Stack;

public class CPU {
    private final Registers registers = new Registers();
    private final Memory memory;
    private int stackPointer; // Указатель стека (SP)
    private int memorySize;

    public CPU(int memorySize, int stackSize) {
        this.memory = new Memory(memorySize);
        int stackBase = memorySize - stackSize; // Стек растёт вниз
        registers.setRegister("SS", stackBase); // Устанавливаем сегмент стека
        this.stackPointer = stackBase;
        this.memorySize = memorySize;
    }

    public void executeInstruction(String instruction) {
        execute(instruction);
    }

    public void dumpRegisters() {
        registers.dumpRegisters();
    }

    private void execute(String command) {
        command = command.replace(",", "");
        String[] parts = command.split("\\s+");

        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        String instruction = parts[0].toUpperCase();
        String[] operands = Arrays.copyOfRange(parts, 1, parts.length);

        switch (instruction) {
            case "PUSH":
                if (operands.length != 1) throw new IllegalArgumentException("PUSH requires 1 operand");
                push(operands[0]);
                break;
            case "POP":
                if (operands.length != 1) throw new IllegalArgumentException("POP requires 1 operand");
                pop(operands[0]);
                break;
            default:
                throw new IllegalArgumentException("Unknown instruction: " + instruction);
        }
    }

    private void push(String src) {
        long value = parseValue(src);
        int ss = (int) registers.getRegister("SS"); // Получаем SS (Stack Segment)
        if (stackPointer - 8 < ss) {
            throw new IllegalStateException("Stack overflow");
        }
        for (int i = 0; i < 8; i++) {
            memory.write(stackPointer - i, (byte) (value >> (i * 8)));
        }
        stackPointer -= 8;
    }

    private void pop(String dest) {
        int ss = (int) registers.getRegister("SS");
        if (stackPointer + 8 >= ss + memorySize) {
            throw new IllegalStateException("Stack underflow");
        }
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) memory.read(stackPointer + i) & 0xFF) << (i * 8);
        }
        stackPointer += 8;
        registers.setRegister(dest, value);
    }

    private long parseValue(String operand) {
        if (operand.startsWith("0x")) {
            return Long.parseUnsignedLong(operand.substring(2), 16);
        } else if (operand.matches("\\d+")) {
            return Long.parseLong(operand);
        } else if (isRegister(operand)) {
            return registers.getRegister(operand);
        } else {
            throw new IllegalArgumentException("Unknown operand: " + operand);
        }
    }

    private boolean isRegister(String operand) {
        return operand.matches("[A-Z]+[0-9]*");
    }
}
