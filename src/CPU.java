import java.util.Arrays;
import java.util.HexFormat;

public class CPU {
    private final Registers registers = new Registers(); // Processor registers
    private final Memory memory; // RAM

    // Constructor: initializes CPU with memory and stack
    public CPU() {
        this.memory = new Memory();
    }

    // Parses and executes a command
    public void executeInstruction(String command) {
        command = command.replace(",", ""); // Remove commas if present
        String[] parts = command.split("\\s+"); // Split by spaces

        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        String instruction = parts[0].toUpperCase(); // Command name
        String[] operands = Arrays.copyOfRange(parts, 1, parts.length); // Operands
        if(isRegister(operands[0].toUpperCase())) {
            operands[0] = operands[0].toUpperCase();
        }

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
            case "SHOW":
                if (operands.length != 1) throw new IllegalArgumentException("SHOW requires 1 operand");
                show(operands[0]);
                break;
            case "RDUMP":
                registers.dumpRegisters();
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
        long rsp = registers.getRegister("RSP"); // Get stack pointer
        long ss = registers.getRegister("SS"); // Stack start

        // Stack overflow check
        if (rsp - 8 < ss) {
            throw new IllegalStateException("Stack overflow");
        }

        rsp -= 8; // Decrease RSP before record
        for (int i = 0; i < 8; i++) {
            memory.write((int) (rsp + i), (byte) (value >> (i * 8)));
        }

        registers.setRegister("RSP", rsp); // Update RSP
    }


    // POP: pops a value from the stack into a register
    private void pop(String dest) {
        if (!isRegister(dest)) {
            throw new IllegalArgumentException("Destination must be a register: " + dest);
        }

        long rsp = registers.getRegister("RSP");
        long stackLimit = memory.memorySize; // Upper stack border..?

        // Erase stack check
        if (rsp + 8 > stackLimit) {
            throw new IllegalStateException("Stack underflow");
        }

        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) memory.read((int) (rsp + i)) & 0xFF) << (i * 8);
        }

        registers.setRegister(dest, value); // Record in register
        registers.setRegister("RSP", rsp + 8); // Update RSP
    }


    //Shows 1 register in console
    private void show(String dest){
        if (!isRegister(dest)) {
            throw new IllegalArgumentException("Destination must be a register: " + dest);
        }
        System.out.println(dest + " " + registers.getRegister(dest) + " " + String.format("0x%016X", registers.getRegister(dest)));
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
