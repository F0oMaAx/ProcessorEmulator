import java.util.Arrays;

public class CPU {
    private final Registers registers = new Registers(); // Processor registers
    private final Memory memory; // RAM
    private int stackPointer; // Stack pointer (SP)
    private final int memorySize; // Memory size

    // Constructor: initializes CPU with memory and stack
    public CPU(int memorySize, int stackSize) {
        this.memory = new Memory(memorySize);
        int stackBase = memorySize - stackSize; // Stack grows downward
        registers.setRegister("SS", stackBase); // Set stack segment (SS)
        this.stackPointer = stackBase; // Initialize stack pointer
        this.memorySize = memorySize;
    }

    // Executes a single instruction
    public void executeInstruction(String instruction) {
        execute(instruction);
    }

    // Prints current register state
    public void dumpRegisters() {
        registers.dumpRegisters();
    }

    // Parses and executes a command
    private void execute(String command) {
        command = command.replace(",", ""); // Remove commas if present
        String[] parts = command.split("\\s+"); // Split by spaces

        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        String instruction = parts[0].toUpperCase(); // Command name
        String[] operands = Arrays.copyOfRange(parts, 1, parts.length); // Operands

        switch (instruction) {
            case "MOV":
                if (operands.length != 2) throw new IllegalArgumentException("MOV requires 2 operands");
                mov(operands[0], operands[1]);
                break;
            case "ADD":
            case "SUB":
            case "AND":
            case "OR":
            case "MUL":
            case "DIV":
                if (operands.length != 2) throw new IllegalArgumentException(instruction + " requires 2 operands");
                arithmeticOperation(instruction, operands[0], operands[1]);
                break;
            case "PUSH":
                if (operands.length != 1) throw new IllegalArgumentException("PUSH requires 1 operand");
                push(operands[0]);
                break;
            case "POP":
                if (operands.length != 1) throw new IllegalArgumentException("POP requires 1 operand");
                pop(operands[0]);
                break;
            case "RDUMP":
                dumpRegisters();
                break;
            default:
                throw new IllegalArgumentException("Unknown instruction: " + instruction);
        }
    }

    // MOV: moves a value to a register
    private void mov(String dest, String src) {
        if (!isRegister(dest)) {
            throw new IllegalArgumentException("Destination must be a register: " + dest);
        }
        registers.setRegister(dest, parseValue(src));
    }

    // Performs arithmetic or bitwise operations
    private void arithmeticOperation(String operation, String dest, String src) {
        if (!isRegister(dest)) {
            throw new IllegalArgumentException("Destination must be a register: " + dest);
        }

        long value1 = registers.getRegister(dest);
        long value2 = parseValue(src);
        long result;

        switch (operation) {
            case "ADD": result = value1 + value2; break;
            case "SUB": result = value1 - value2; break;
            case "AND": result = value1 & value2; break;
            case "OR":  result = value1 | value2; break;
            case "MUL": result = value1 * value2; break;
            case "DIV":
                if (value2 == 0) throw new ArithmeticException("Division by zero");
                result = value1 / value2;
                break;
            default:
                throw new IllegalArgumentException("Unknown operation: " + operation);
        }

        registers.setRegister(dest, result);
    }

    // PUSH: pushes a value onto the stack
    private void push(String src) {
        long value = parseValue(src);
        int ss = (int) registers.getRegister("SS"); // Get stack segment base

        // Stack overflow check
        if (stackPointer - 8 < ss) {
            throw new IllegalStateException("Stack overflow");
        }

        // Write 8 bytes (64-bit value) to memory
        for (int i = 0; i < 8; i++) {
            memory.write(stackPointer - i, (byte) (value >> (i * 8)));
        }

        stackPointer -= 8; // Move stack pointer down
    }

    // POP: pops a value from the stack into a register
    private void pop(String dest) {
        if (!isRegister(dest)) {
            throw new IllegalArgumentException("Destination must be a register: " + dest);
        }

        int ss = (int) registers.getRegister("SS"); // Get stack segment base

        // Stack underflow check
        if (stackPointer + 8 >= ss + memorySize) {
            throw new IllegalStateException("Stack underflow");
        }

        long value = 0;

        // Read 8 bytes from memory and reconstruct 64-bit value
        for (int i = 0; i < 8; i++) {
            value |= ((long) memory.read(stackPointer + i) & 0xFF) << (i * 8);
        }

        stackPointer += 8; // Move stack pointer up
        registers.setRegister(dest, value); // Store value in register
    }

    // Converts an operand into a numeric value
    private long parseValue(String operand) {
        if (operand.startsWith("0x")) {
            return Long.parseUnsignedLong(operand.substring(2), 16); // Hexadecimal
        } else if (operand.matches("\\d+")) {
            return Long.parseLong(operand); // Decimal integer
        } else if (operand.matches("\\d+\\.\\d+")) {
            return Double.doubleToRawLongBits(Double.parseDouble(operand)); // Convert float to long
        } else if (isRegister(operand)) {
            return registers.getRegister(operand);
        } else {
            throw new IllegalArgumentException("Unknown operand: " + operand);
        }
    }

    // Checks if a string is a valid register name
    private boolean isRegister(String operand) {
        return operand.matches("[A-Z]+[0-9]*");
    }
}
