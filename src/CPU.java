import java.util.Arrays;

public class CPU {
    private final Registers registers = new Registers(); // Регистры процессора
    private final Memory memory; // ОЗУ процессора
    private int stackPointer; // Указатель стека (SP)
    private final int memorySize; // Размер памяти

    /**
     * Конструктор процессора.
     * @param memorySize Размер ОЗУ.
     * @param stackSize Размер выделенной области под стек.
     */
    public CPU(int memorySize, int stackSize) {
        this.memory = new Memory(memorySize);
        int stackBase = memorySize - stackSize; // Определяем начало стека (растёт вниз)
        registers.setRegister("SS", stackBase); // Устанавливаем регистр SS (Stack Segment)
        this.stackPointer = stackBase; // Инициализируем указатель стека
        this.memorySize = memorySize;
    }

    /**
     * Выполняет одну инструкцию, переданную в виде строки.
     * @param instruction Строка с инструкцией (например, "PUSH 10").
     */
    public void executeInstruction(String instruction) {
        execute(instruction);
    }

    /**
     * Выводит текущее состояние регистров.
     */
    public void dumpRegisters() {
        registers.dumpRegisters();
    }

    /**
     * Разбирает и выполняет переданную команду.
     * @param command Инструкция в виде строки.
     */
    private void execute(String command) {
        command = command.replace(",", ""); // Убираем запятые (если есть)
        String[] parts = command.split("\\s+"); // Разделяем строку на части

        if (parts.length < 1) {
            throw new IllegalArgumentException("Invalid command: " + command);
        }

        String instruction = parts[0].toUpperCase(); // Опкод команды
        String[] operands = Arrays.copyOfRange(parts, 1, parts.length); // Операнды

        // Выполняем инструкцию
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

    /**
     * Кладёт значение в стек.
     * @param src Значение или регистр, откуда брать данные.
     */
    private void push(String src) {
        long value = parseValue(src); // Получаем значение (число или из регистра)
        int ss = (int) registers.getRegister("SS"); // Получаем базовый адрес стека (SS)

        // Проверка на переполнение стека
        if (stackPointer - 8 < ss) {
            throw new IllegalStateException("Stack overflow");
        }

        // Записываем 8 байтов (64-битное значение) в память
        for (int i = 0; i < 8; i++) {
            memory.write(stackPointer - i, (byte) (value >> (i * 8)));
        }

        stackPointer -= 8; // Смещаем стек вниз
    }

    /**
     * Извлекает значение из стека и записывает его в регистр.
     * @param dest Регистр, куда записать значение.
     */
    private void pop(String dest) {
        int ss = (int) registers.getRegister("SS"); // Получаем базовый адрес стека (SS)

        // Проверка на выход за границы стека
        if (stackPointer + 8 >= ss + memorySize) {
            throw new IllegalStateException("Stack underflow");
        }

        long value = 0;

        // Читаем 8 байтов из памяти и восстанавливаем 64-битное значение
        for (int i = 0; i < 8; i++) {
            value |= ((long) memory.read(stackPointer + i) & 0xFF) << (i * 8);
        }

        stackPointer += 8; // Смещаем стек вверх
        registers.setRegister(dest, value); // Записываем значение в регистр
    }

    /**
     * Преобразует операнд в числовое значение.
     * @param operand Операнд (число, регистр или адрес).
     * @return Числовое значение операнда.
     */
    private long parseValue(String operand) {
        if (operand.startsWith("0x")) {
            return Long.parseUnsignedLong(operand.substring(2), 16); // Читаем 16-ричное число
        } else if (operand.matches("\\d+")) {
            return Long.parseLong(operand); // Число в десятичной системе
        } else if (isRegister(operand)) {
            return registers.getRegister(operand); // Значение из регистра
        } else {
            throw new IllegalArgumentException("Unknown operand: " + operand);
        }
    }

    /**
     * Проверяет, является ли строка именем регистра.
     * @param operand Строка операнда.
     * @return true, если это регистр.
     */
    private boolean isRegister(String operand) {
        return operand.matches("[A-Z]+[0-9]*");
    }
}
