import java.util.Arrays;
import java.util.Stack;

public class ALU {
    private final Registers registers = new Registers();
    private final Stack<Long> stack = new Stack<>(); // Стек для PUSH и POP

    // Проверяет, является ли операнд регистром
    private boolean isRegister(String operand) {
        return operand.matches("[A-Z]+[0-9]*");
    }

    // Выполняет арифметические и логические операции с тремя операндами
    private void addThreeOperands(String dest, String src1, String src2, String instruction) {
        long value1 = parseValue(src1);
        long value2 = parseValue(src2);
        long result = switch (instruction) {
            case "ADD" -> value1 + value2;
            case "SUB" -> value1 - value2;
            case "AND" -> value1 & value2;
            case "OR"  -> value1 | value2;
            case "XOR" -> value1 ^ value2;
            case "MUL" -> value1 * value2;
            case "DIV" -> value1 / value2;
            default -> throw new IllegalArgumentException("Unknown instruction: " + instruction);
        };
        registers.setRegister(dest, result);
    }

    // Выполнение команды
    public void execute(String command) {
        command = command.replace(",", ""); // Убираем запятые
        String[] parts = command.split("\\s+"); // Разделяем по пробелам

        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        String instruction = parts[0].toUpperCase();

        if (instruction.equals("RDUMP")) {
            registers.dumpRegisters(); // Выводит содержимое регистров
            return;
        }

        if (parts.length < 2 && !instruction.equals("POP")) {
            throw new IllegalArgumentException("Missing operands for: " + instruction);
        }

        String[] operands = Arrays.copyOfRange(parts, 1, parts.length); // Берём всё, кроме инструкции

        switch (instruction) {
            case "MOV":
                if (operands.length != 2) throw new IllegalArgumentException("MOV requires exactly 2 operands");
                mov(operands[0], operands[1]);
                break;
            case "ADD":
            case "SUB":
            case "AND":
            case "OR":
            case "XOR":
            case "MUL":
            case "DIV":
                if (operands.length == 3) {
                    addThreeOperands(operands[0], operands[1], operands[2], instruction);
                } else if (operands.length == 2) {
                    add(operands[0], operands[1]);
                } else {
                    throw new IllegalArgumentException(instruction + " requires 2 or 3 operands");
                }
                break;
            case "NEG":
                neg(operands[0]);
                break;
            case "NOT":
                not(operands[0]);
                break;
            case "LSL":
                shiftLeft(operands[0], operands[1]);
                break;
            case "LSR":
                shiftRight(operands[0], operands[1]);
                break;
            case "CMP":
                compare(operands[0], operands[1]);
                break;
            case "PUSH":
                push(operands[0]);
                break;
            case "POP":
                pop(operands[0]);
                break;
            default:
                throw new IllegalArgumentException("Unknown instruction: " + instruction);
        }
    }

    // MOV (копирование значений между регистрами или установление значений)
    private void mov(String dest, String src) {
        if (!isRegister(dest)) {
            throw new IllegalArgumentException("Destination must be a register: " + dest);
        }
        registers.setRegister(dest, parseValue(src));
    }

    private void add(String dest, String src) {
        registers.setRegister(dest, registers.getRegister(dest) + parseValue(src));
    }

    private void neg(String dest) {
        registers.setRegister(dest, -registers.getRegister(dest));
    }

    private void not(String dest) {
        registers.setRegister(dest, ~registers.getRegister(dest));
    }

    private void shiftLeft(String dest, String amount) {
        registers.setRegister(dest, registers.getRegister(dest) << parseValue(amount));
    }

    private void shiftRight(String dest, String amount) {
        registers.setRegister(dest, registers.getRegister(dest) >>> parseValue(amount));
    }

    private void compare(String src1, String src2) {
        long value1 = parseValue(src1);
        long value2 = parseValue(src2);
        registers.setFlag("Z", value1 == value2);
        registers.setFlag("N", value1 < value2);
    }

    private void push(String src) {
        stack.push(parseValue(src));
    }

    private void pop(String dest) {
        if (stack.isEmpty()) {
            throw new IllegalStateException("Stack underflow");
        }
        registers.setRegister(dest, stack.pop());
    }

    private long parseValue(String operand) {
        if (operand.startsWith("0x")) {
            return Long.parseUnsignedLong(operand.substring(2), 16); // Преобразуем шестнадцатеричное число
        } else if (operand.matches("\\d+")) { // Целые числа
            return Long.parseLong(operand);
        } else if (operand.matches("\\d+\\.\\d+")) { // Числа с плавающей точкой
            return (long) Double.parseDouble(operand);
        } else if (isRegister(operand)) { // Проверяем, регистр ли это
            return registers.getRegister(operand);
        } else {
            throw new IllegalArgumentException("Unknown operand: " + operand);
        }
    }
}
