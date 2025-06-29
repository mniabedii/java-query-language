import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JavaQL {
    public static int estimatedNumberOfRows;

    // Main Method:
    public static void main(String[] args) {
        cls();

        System.out.println(BLUE + "Hello & Welcome\n" + RESET);

        System.out.println("Please enter estimated number of rows in tables:");
        Scanner scanner = new Scanner(System.in);
        estimatedNumberOfRows = scanner.nextInt();
        scanner.nextLine();

        mainLoop: while (true) {
            System.out.println("\nEnter your " + BLUE + "command:" + RESET);
            System.out.println("(Enter " + BLUE + "help" + RESET + " for command info Or enter " + RED + "quit" + RESET
                    + " to quit the app)");

            String input = scanner.nextLine();
            input = input.trim().toLowerCase();

            String command = (input.split(" "))[0];

            input = input.substring(command.length()).trim();

            switch (command) {
                case "quit" -> {
                    scanner.close();
                    break mainLoop;
                }
                case "help" -> explainCommandInfo();
                case "create" -> createTable(input);
                case "drop" -> dropTable(input);
                case "add" -> System.out.println(addRowToTable(input));
                case "get" -> getTable(input);
                case "set" -> setRow(input);
                case "del" -> delRow(input);
                default -> System.out
                        .println(RED + "Error! '" + command + "' is not recognized as a valid command.\n" + RESET);
            }
        }
    }

    // Color Codes:
    public static final String BLUE = "\033[0;34m";
    public static final String RED = "\033[0;31m";
    public static final String GREEN = "\033[0;32m";
    public static final String RESET = "\033[0m";

    // Global Variables:
    public static final String STANDARD_PATTERN = "\\w+";
    private static final String REGEX_OF_CREATE_TABLE = "\\[\\s*(\\w+\\s*[a-z]{3}\\s*,\\s*)*\\s*\\w+\\s*[a-z]{3}\\s*]";
    private static final String REGEX_OF_ADD_ROW = "\\{\\s*(\\w+\\s*=\\s*([\\w']+|(\\d+(\\.\\d+)*))\\s*,\\s+)*\\s*\\w+\\s*=\\s*([\\w']+|(\\d+(\\.\\d+)*))\\s*\\}";
    private static final String REGEX_OF_SET_ROW_NO_FILTERS = "\\{\\s*(\\w+\\s*=\\s*((\\w+|'\\w+')|(\\d+(\\.\\d+)*))\\s*,\\s*)*\\s*\\w+\\s*=\\s*((\\w+|'\\w+')|(\\d+(\\.\\d+)*))\\s*\\}";
    private static final String REGEX_OF_SET_ROW_WITH_FILTERS = "\\(((\\s*)\\w+(\\s*)=(\\s*)((\\w+|'\\w+')|(\\d+(\\.\\d+)*))(\\s*),)*(\\s*)\\w+(\\s*)=(\\s*)((\\w+|'\\w+')|(\\d+(\\.\\d+)*))(\\s*)\\)\\s*\\{((\\s*)\\w+(\\s*)=(\\s*)((\\w+|'\\w+')|(\\d+(\\.\\d+)*))(\\s*),)*(\\s*)\\w+(\\s*)=(\\s*)((\\w+|'\\w+')|(\\d+(\\.\\d+)*))(\\s*)\\}";
    private static final String REGEX_OF_FILTERS_OF_SET_ROW = "\\(((\\s*)\\w+(\\s*)=(\\s*)((\\w+|'\\w+')|(\\d+(\\.\\d+)*))(\\s*),)*(\\s*)\\w+(\\s*)=(\\s*)((\\w+|'\\w+')|(\\d+(\\.\\d+)*))(\\s*)\\)";
    private static final String REGEX_OF_DATA_OF_SET_ROW = "\\{((\\s*)\\w+(\\s*)=(\\s*)((\\w+|'\\w+')|(\\d+(\\.\\d+)*))(\\s*),)*(\\s*)\\w+(\\s*)=(\\s*)((\\w+|'\\w+')|(\\d+(\\.\\d+)*))(\\s*)\\}";
    private static final String REGEX_OF_DEL_ROW_WITH_FILTERS = "\\(\\s*(\\w+\\s*=\\s*((\\w+|'\\w+')|(\\d+(\\.\\d+)*))\\s*,\\s*)*\\w+\\s*=\\s*((\\w+|'\\w+')|(\\d+(\\.\\d+)*))\\s*\\)";
    private static final String REGEX_OF_GET_ROW_WITH_FILTERS = "\\(\\s*(((\\w+)|(\\d+(\\.\\d+)*))\\s*(\\+|\\-)\\s*)*((\\w+)|(\\d+(\\.\\d+)*))\\s*[<=>]\\s*(((\\w+)|(\\d+(\\.\\d+)*))\\s*(\\+|\\-)\\s*)*(((\\w+)|(\\d+(\\.\\d+)*))|'\\w+')\\s*\\)";
    public static Pattern pattern;
    public static Matcher matcher;
    public static TreeMap<String, String> connectColumnToItsDataType = new TreeMap<>();
    public static TreeMap<String, TreeMap<String, String>> connectTableNamesToTypes = new TreeMap<>();
    public static TreeMap<String, String[][]> connectTableNameToTheTable = new TreeMap<>();

    // important methods:
    public static void cls() {
        System.out.print("\033[H\033[2J");
    }

    public static void explainCommandInfo() {
        System.out.println("Commands:\n");
        System.out
                .println("Command " + BLUE + "create" + RESET + ":\nUse this command to create a new table of data.\n");
        System.out.println("Command " + BLUE + "drop" + RESET
                + ":\nUse this command to delete the a table and all it contains.\n");
        System.out.println("Command " + BLUE + "add" + RESET + ":\nUse this command to add a new row to a table.\n");
        System.out.println(
                "Command " + BLUE + "get" + RESET + ":\nUse this command to print a table based on set filters.\n");
        System.out.println("Command " + BLUE + "set" + RESET
                + ":\nUse this command to update row(s) of a table based on given data.\n");
        System.out.println("Command " + BLUE + "del" + RESET
                + ":\nUse this command to delete row(s) of a table based on set filters.\n");
    }

    public static boolean isTheNameOfTheTableEnteredCorrectly(String regex, String input) {
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(input);
        return matcher.find();
    }

    public static String returnTableName(String regex, String input) {
        String tableName = "";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            tableName = matcher.group();
        }
        pattern = Pattern.compile(STANDARD_PATTERN);
        matcher = pattern.matcher(tableName);
        if (matcher.find()) {
            tableName = matcher.group();
        }
        return tableName;
    }

    public static boolean doesTableExistPreviously(String tableName) {
        return connectTableNameToTheTable.containsKey(tableName);
    }

    public static boolean doArgumentsCheckPattern(String input, String regex) {
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(input);
        return matcher.find();
    }

    public static String returnArguments(String input, String regex) {
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            input = matcher.group();
        }
        return input;
    }

    public static int extractTheColumnNamesAndItsDataAndReturnTheNumberOfColumnsUnderDiscussion(String input,
            String[] columnNames, String[] data) {
        pattern = Pattern.compile("[\\w'\\.]+");
        matcher = pattern.matcher(input);
        int counter = 0;
        while (matcher.find()) {
            if (counter % 2 == 0) {
                columnNames[counter / 2] = matcher.group();
            } else {
                data[counter / 2] = matcher.group();
            }
            counter++;
        }
        return counter / 2;
    }

    public static void addElementToArrayOfInt(int a, int[] array) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == 0) {
                array[i] = a;
                break;
            }
        }
    }

    public static void printNTimes(String input, int n) {
        if (n > 0) {
            System.out.print(input);
            printNTimes(input, n - 1);
        }
    }

    public static void printTable(String[][] table) {
        int[] storingEveryColumnsMaxLength = new int[table[0].length];
        int max = Integer.MIN_VALUE;
        for (int j = 0; j < table[0].length; j++) {
            for (String[] strings : table) {
                if (strings[0] != null) {
                    if (strings[j].length() > max) {
                        max = strings[j].length();
                    }
                }
            }
            storingEveryColumnsMaxLength[j] = max;
            max = Integer.MIN_VALUE;
        }
        int space;
        for (int i = 0; i < table[0].length; i++) {
            space = storingEveryColumnsMaxLength[i] - table[0][i].length() + 2;

            printNTimes(" ", space / 2);
            System.out.print(BLUE + table[0][i] + RESET);

            if (space % 2 != 0)
                space++;

            printNTimes(" ", space / 2);

            if (i != table[0].length - 1)
                System.out.print("|");
        }
        System.out.println();

        for (int i : storingEveryColumnsMaxLength) {
            printNTimes("-", i + 2);
            System.out.print("+");
        }
        System.out.println();

        for (int i = 1; i < table.length; i++) {
            if (table[i][0] != null) {
                for (int j = 0; j < table[0].length; j++) {
                    space = storingEveryColumnsMaxLength[j] - table[i][j].length() + 2;

                    printNTimes(" ", space / 2);
                    System.out.print(table[i][j]);

                    if (space % 2 != 0)
                        space++;

                    printNTimes(" ", space / 2);

                    if (j != table[0].length - 1)
                        System.out.print("|");
                }
                System.out.println();
            }
        }
    }

    public static boolean isColumnNameValid(String[][] Table, String[] columnNames) {
        boolean flag = false;
        for (int i = 0; columnNames[i] != null; i++) {
            for (int j = 0; j < Table[0].length; j++) {
                if (columnNames[i].equals(Table[0][j])) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return false;
            }
            flag = false;
        }
        return true;
    }

    public static boolean isTypeFormatValid(String[] columnNames, String[] data, int numberOfDataAndFilters) {
        for (int i = 0; i < numberOfDataAndFilters; i++) {
            switch (connectColumnToItsDataType.get(columnNames[i])) {
                case "int":
                    if (!data[i].matches("[0-9]\\d*")) {
                        return false;
                    }
                    break;
                case "str":
                    if (!data[i].matches("'(.)*'")) {
                        return false;
                    }
                    break;
                case "dbl":
                    if (!data[i].matches("\\d+(\\.\\d+)*")) {
                        return false;
                    }
                    break;
            }
        }
        return true;
    }

    public static int findingTheEmptyRow(String[][] table) {
        for (int i = 1; i < estimatedNumberOfRows; i++) {
            if (table[i][0] == null) {
                return i;
            }
        }
        return -1;
    }

    public static boolean doesElementExist(String[][] table, String[] data, String[] columnNames) {
        boolean doesElementExist = false;
        for (int x = 0; data[x] != null; x++) {
            secondLoop: for (int i = 1; i < table.length; i++) {
                if (table[i][0] != null) {
                    for (int j = 0; j < table[0].length; j++) {
                        if (data[x].equals(table[i][j]) && columnNames[x].equals(table[0][j])) {
                            doesElementExist = true;
                            break secondLoop;
                        }
                    }
                }
            }
            if (!doesElementExist) {
                return false;
            }
            doesElementExist = false;
        }
        return true;
    }

    public static void findRows(int[] changingRows, String[][] table, String[] data, String[] columnNames) {
        for (int x = 0; data[x] != null; x++) {
            for (int i = 1; i < table.length; i++) {
                if (table[i][0] != null) {
                    for (int j = 0; j < table[0].length; j++) {
                        if (data[x].equals(table[i][j]) && columnNames[x].equals(table[0][j])) {
                            addElementToArrayOfInt(i, changingRows);
                        }
                    }
                }
            }
        }
    }

    // Create Table Methods:
    public static void createTable(String input) {
        String tableName;
        if (isTheNameOfTheTableEnteredCorrectly("\\w+\\s*\\[", input)) {
            tableName = returnTableName("\\w+\\s*\\[", input);
            if (doesTableExistPreviously(tableName)) {
                System.out.println(RED + "Error! a table by this name has been previously created.\n" + RESET);
                return;
            }
        } else {
            System.out.println(RED + "Error! Invalid Input\n" + RESET);
            return;
        }

        if (doArgumentsCheckPattern(input, REGEX_OF_CREATE_TABLE)) {
            input = returnArguments(input, REGEX_OF_CREATE_TABLE);
        } else {
            System.out.println(RED + "Error! Invalid Input\n" + RESET);
            return;
        }
        String[] columnNames = new String[estimatedNumberOfRows];
        String[] dataTypes = new String[estimatedNumberOfRows];

        int numberOfDataAndFilters = extractTheColumnNamesAndItsDataAndReturnTheNumberOfColumnsUnderDiscussion(input,
                columnNames, dataTypes);
        if (!areDataTypesIntStrDbl(numberOfDataAndFilters, dataTypes)) {
            System.out.println(RED + "Error! invalid data type\n" + RESET);
        }

        for (int i = 0; i < numberOfDataAndFilters; i++) {
            connectColumnToItsDataType.put(columnNames[i], dataTypes[i]);
        }
        connectTableNamesToTypes.put(tableName, connectColumnToItsDataType);

        String[][] table = new String[estimatedNumberOfRows][numberOfDataAndFilters];

        initializeColumnNamesToFirstRow(table, columnNames, numberOfDataAndFilters);

        connectTableNameToTheTable.put(tableName, table);

        System.out.println(GREEN + "Provided table was created successfully!\n" + RESET);
    }

    public static boolean areDataTypesIntStrDbl(int numberOfDataAndFilters, String[] dataTypes) {
        for (int i = 0; i < numberOfDataAndFilters; i++) {
            if (!dataTypes[i].equals("int") && dataTypes[i].equals("str") && dataTypes[i].equals("dbl")) {
                return false;
            }
        }
        return true;
    }

    public static void initializeColumnNamesToFirstRow(String[][] table, String[] columnNames,
            int numberOfDataAndFilters) {
        if (numberOfDataAndFilters >= 0)
            System.arraycopy(columnNames, 0, table[0], 0, numberOfDataAndFilters);
    }

    // Drop Table Methods:
    public static void dropTable(String input) {
        if (!connectTableNameToTheTable.containsKey(input)) {
            System.out.println(RED + "Error! Table not found!\n" + RESET);
            return;
        }
        connectTableNameToTheTable.remove(input);
        connectTableNamesToTypes.remove(input);
        System.out.println(GREEN + "Table was deleted successfully!\n" + RESET);
    }

    // Add Row Methods:
    public static String addRowToTable(String input) {
        String tableName;
        if (isTheNameOfTheTableEnteredCorrectly("\\w+\\s*\\{", input)) {
            tableName = returnTableName("\\w+\\s*\\{", input);
        } else {
            return RED + "Error! Invalid Input\n" + RESET;
        }
        if (!doesTableExistPreviously(tableName)) {
            return RED + "Error! table not found\n" + RESET;
        }
        if (doArgumentsCheckPattern(input, REGEX_OF_ADD_ROW)) {
            input = returnArguments(input, REGEX_OF_ADD_ROW);
        } else {
            return RED + "Error! Invalid Input\n" + RESET;
        }
        String[] columnNames = new String[estimatedNumberOfRows];
        String[] addingData = new String[estimatedNumberOfRows];

        int numberOfDataAndFilters = extractTheColumnNamesAndItsDataAndReturnTheNumberOfColumnsUnderDiscussion(input,
                columnNames, addingData);

        String[][] table = connectTableNameToTheTable.get(tableName);
        if (!isColumnNameValid(table, columnNames)) {
            return RED + "Error! column does not exist\n" + RESET;
        }
        if (!isTypeFormatValid(columnNames, addingData, numberOfDataAndFilters)) {
            return RED + "Error! wrong type format\n" + RESET;
        }

        int emptyRow = findingTheEmptyRow(table);
        initializeTableBasedOnGivenData(table, columnNames, addingData, tableName, emptyRow, numberOfDataAndFilters);

        connectTableNameToTheTable.put(tableName, table);
        String output = "In " + BLUE + tableName + RESET + " table:\n";
        for (int i = 0; i < table[0].length; i++) {
            output += BLUE + table[0][i] + RESET + " -> " + BLUE + table[emptyRow][i] + RESET + "\n";
        }
        return output;
    }

    public static void initializeTableBasedOnGivenData(String[][] table, String[] columnNames, String[] data,
            String tableName, int emptyRow, int numberOfDataAndFilters) {
        for (int i = 0; i < table[0].length; i++) {
            for (int j = 0; j < numberOfDataAndFilters; j++) {
                if (table[0][i].equals(columnNames[j])) {
                    table[emptyRow][i] = data[j];
                    break;
                } else {
                    switch (connectTableNamesToTypes.get(tableName).get(table[0][i])) {
                        case "int":
                            table[emptyRow][i] = "0";
                            break;
                        case "str":
                            table[emptyRow][i] = "''";
                            break;
                        case "dbl":
                            table[emptyRow][i] = "0.0";
                            break;
                    }
                }
            }
        }
    }

    // Get Table Methods:
    public static void getTable(String input) {
        String tableName;
        if (isTheNameOfTheTableEnteredCorrectly("\\w+\\s*\\(", input)) {
            tableName = returnTableName("\\w+\\s*\\(", input);
            if (!doesTableExistPreviously(tableName)) {
                System.out.println(RED + "Error! table not found\n" + RESET);
                return;
            }
            if (doArgumentsCheckPattern(input, REGEX_OF_GET_ROW_WITH_FILTERS)) {
                input = returnArguments(input, REGEX_OF_GET_ROW_WITH_FILTERS);
            } else {
                System.out.println(RED + "Error! invalid input\n" + RESET);
                return;
            }
            String[][] table = connectTableNameToTheTable.get(tableName);
            String leftSideOfTheOperation, rightSideOfTheOperation;
            String operation = returnOperation(input);

            leftSideOfTheOperation = returnSidesOfTheOperation(operation, input, "left");
            rightSideOfTheOperation = returnSidesOfTheOperation(operation, input, "right");

            ArrayList<String> elementsOfLeftSide = returningElementsOfInput(leftSideOfTheOperation);
            ArrayList<String> elementsOfRightSide = returningElementsOfInput(rightSideOfTheOperation);

            int[] candidateRows = new int[estimatedNumberOfRows];
            candidateRows[0] = -1;

            pattern = Pattern.compile("('\\w+')");
            matcher = pattern.matcher(rightSideOfTheOperation);
            if (matcher.find()) {
                if (elementsOfLeftSide.size() != 1 || elementsOfRightSide.size() != 1) {
                    System.out.println(RED + "Error! can not perform calculations on 'str' format\n" + RESET);
                    return;
                }
                if (!isAColumn(elementsOfLeftSide.getFirst(), table)) {
                    System.out.println(RED + "Error! column not found\n" + RESET);
                    return;
                }
                if (!isElementOfTypeStr(table, tableName, elementsOfLeftSide.getFirst())) {
                    System.out.println(RED + "Error! invalid type format\n" + RESET);
                    return;
                }
                if (!operation.equals("=")) {
                    System.out.println(RED + "Error! invalid operation" + RESET);
                    return;
                }
                for (int i = 1; i < table.length; i++) {
                    if (table[i][0] != null) {
                        for (int j = 0; j < table[0].length; j++) {
                            if (elementsOfRightSide.getFirst().equals(table[i][j])
                                    && elementsOfLeftSide.getFirst().equals(table[0][j])) {
                                addElementToArrayOfInt(i, candidateRows);
                            }
                        }
                    }
                }
                String[][] newTable = createTableWithCandidateRows(table, candidateRows);
                printTable(newTable);
                System.out.println();
                return;
            }

            for (int i = 0; i < elementsOfLeftSide.size(); i += 2) {
                if (!isANumber(elementsOfLeftSide.get(i)) && !isAColumn(elementsOfLeftSide.get(i), table)) {
                    System.out.println(RED + "Error! Column not found\n" + RESET);
                    return;
                }
            }
            for (int i = 0; i < elementsOfRightSide.size(); i += 2) {
                if (!isANumber(elementsOfRightSide.get(i)) && !isAColumn(elementsOfRightSide.get(i), table)) {
                    System.out.println(RED + "Error! Column not found\n" + RESET);
                    return;
                }
            }
            double sumOfLeftSide, sumOfRightSide;
            for (int row = 1; row < table.length; row++) {
                if (table[row][0] != null) {
                    sumOfLeftSide = calculateEverySide(table, elementsOfLeftSide, tableName, row);
                    sumOfRightSide = calculateEverySide(table, elementsOfRightSide, tableName, row);

                    if ((sumOfLeftSide <= Double.MIN_VALUE) || (sumOfRightSide <= Double.MIN_VALUE)) {
                        return;
                    }

                    switch (operation) {
                        case "=": {
                            if (sumOfLeftSide == sumOfRightSide) {
                                addElementToArrayOfInt(row, candidateRows);
                            }
                            break;
                        }
                        case ">": {
                            if (sumOfLeftSide > sumOfRightSide) {
                                addElementToArrayOfInt(row, candidateRows);
                            }
                            break;
                        }
                        case "<": {
                            if (sumOfLeftSide < sumOfRightSide) {
                                addElementToArrayOfInt(row, candidateRows);
                            }
                            break;
                        }
                    }
                }
            }
            String[][] newTable = createTableWithCandidateRows(table, candidateRows);
            printTable(newTable);
            System.out.println();
        } else {
            if (isTheNameOfTheTableEnteredCorrectly(STANDARD_PATTERN, input)) {
                tableName = returnTableName(STANDARD_PATTERN, input);
                if (!doesTableExistPreviously(tableName)) {
                    System.out.println(RED + "Error! table not found\n" + RESET);
                    return;
                }
                String[][] table = connectTableNameToTheTable.get(tableName);
                printTable(table);
                System.out.println();
            } else {
                System.out.println(RED + "Error! invalid input\n" + RESET);
            }
        }
    }

    public static String returnOperation(String input) {
        pattern = Pattern.compile("[<=>]");
        matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }
        return "";
    }

    public static String returnSidesOfTheOperation(String operation, String input, String leftOrRight) {
        if (leftOrRight.equals("left")) {
            return input.split(operation)[0];
        } else {
            return input.split(operation)[1];
        }
    }

    public static int returnColumnIndex(String input, String[][] table) {
        for (int i = 0; i < table[0].length; i++) {
            if (table[0][i].equals(input)) {
                return i;
            }
        }
        return -1;
    }

    public static ArrayList<String> returningElementsOfInput(String input) {
        ArrayList<String> elements = new ArrayList<>();
        pattern = Pattern.compile("(\\w+)|('\\w+')|(\\d+(\\.\\d+)*)|\\+|\\-");
        matcher = pattern.matcher(input);
        while (matcher.find()) {
            elements.add(matcher.group());
        }
        return elements;
    }

    public static boolean isAColumn(String input, String[][] table) {
        for (int i = 0; i < table[0].length; i++) {
            if (table[0][i].equals(input)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isANumber(String string) {
        return (string.matches("\\d+(\\.\\d+)*"));
    }

    public static boolean isElementOfTypeInt(String[][] table, String tableName, String columnName) {
        return (connectTableNamesToTypes.get(tableName).get(table[0][returnColumnIndex(columnName, table)])
                .equals("int"));
    }

    public static boolean isElementOfTypeDouble(String[][] table, String tableName, String columnName) {
        return (connectTableNamesToTypes.get(tableName).get(table[0][returnColumnIndex(columnName, table)])
                .equals("dbl"));
    }

    public static boolean isElementOfTypeStr(String[][] table, String tableName, String columnName) {
        return (connectTableNamesToTypes.get(tableName).get(table[0][returnColumnIndex(columnName, table)])
                .equals("str"));
    }

    public static String[][] createTableWithCandidateRows(String[][] table, int[] rows) {
        String[][] newTable = new String[estimatedNumberOfRows][table[0].length];
        newTable[0] = table[0];
        for (int i = 1; rows[i] != 0; i++) {
            newTable[i] = table[rows[i]];
        }
        return newTable;
    }

    public static double calculateEverySide(String[][] table, ArrayList<String> elementsOfSide, String tableName,
            int row) {
        double temp = 0.0;
        double sumOfSide = 0.0;
        boolean nextNumberIsNegative = false;
        for (int i = 0; i < elementsOfSide.size(); i++) {
            if (i % 2 == 0) {
                if (isANumber(elementsOfSide.get(i))) {
                    temp = Double.parseDouble(elementsOfSide.get(i));
                } else {
                    if (isElementOfTypeInt(table, tableName, elementsOfSide.get(i))
                            || isElementOfTypeDouble(table, tableName, elementsOfSide.get(i))) {
                        temp = Double.parseDouble(table[row][returnColumnIndex(elementsOfSide.get(i), table)]);
                    } else if (isElementOfTypeStr(table, tableName, elementsOfSide.get(i))) {
                        System.out.println(RED + "Error! wrong type format\n" + RESET);
                        return Double.MIN_VALUE - 5;
                    }
                }
                if (nextNumberIsNegative) {
                    temp *= -1;
                    nextNumberIsNegative = false;
                }
                sumOfSide += temp;
            } else {
                if (elementsOfSide.get(i).equals("-")) {
                    nextNumberIsNegative = true;
                }
            }
        }
        return sumOfSide;
    }

    // Set Table Methods:
    public static int setRow(String input) {
        String tableName;
        if (isTheNameOfTheTableEnteredCorrectly("\\w+\\s*\\{", input)) {
            tableName = returnTableName("\\w+\\s*\\{", input);
            if (!doesTableExistPreviously(tableName)) {
                System.out.println(RED + "Error! table not found\n" + RESET);
                return 0;
            }
            if (doArgumentsCheckPattern(input, REGEX_OF_SET_ROW_NO_FILTERS)) {
                input = returnArguments(input, REGEX_OF_SET_ROW_NO_FILTERS);
            } else {
                System.out.println(RED + "Error! invalid input\n" + RESET);
                return 0;
            }
            String[][] table = connectTableNameToTheTable.get(tableName);
            String[] columnNames = new String[estimatedNumberOfRows];
            String[] settingData = new String[estimatedNumberOfRows];

            int numberOfDataAndFilters = extractTheColumnNamesAndItsDataAndReturnTheNumberOfColumnsUnderDiscussion(
                    input, columnNames, settingData);

            if (!isColumnNameValid(table, columnNames)) {
                System.out.println(RED + "Error! column not found\n" + RESET);
                return 0;
            }
            if (!isTypeFormatValid(columnNames, settingData, numberOfDataAndFilters)) {
                System.out.println(RED + "Error! wrong type format\n" + RESET);
                return 0;
            }

            int numberOfSetRows = setRowsAndCountThem(table, columnNames, settingData, numberOfDataAndFilters);
            System.out.println(GREEN + "Provided data set" + RESET);
            System.out.println(GREEN + "number of " + numberOfSetRows + " row(s) were set\n" + RESET);
            return numberOfSetRows;

        } else if (isTheNameOfTheTableEnteredCorrectly("\\w+\\s*\\(", input)) {
            tableName = returnTableName("\\w+\\s*\\(", input);
            if (!doesTableExistPreviously(tableName)) {
                System.out.println(RED + "Error! table not found\n" + RESET);
                return 0;
            }
            if (doArgumentsCheckPattern(input, REGEX_OF_SET_ROW_WITH_FILTERS)) {
                input = returnArguments(input, REGEX_OF_SET_ROW_WITH_FILTERS);
            } else {
                System.out.println(RED + "Error! invalid input\n" + RESET);
                return 0;
            }
            String inputOfFilters, inputOfData;
            inputOfFilters = returnArguments(input, REGEX_OF_FILTERS_OF_SET_ROW);
            inputOfData = returnArguments(input, REGEX_OF_DATA_OF_SET_ROW);
            String[][] table = connectTableNameToTheTable.get(tableName);
            String[] columnNamesOfFilters = new String[estimatedNumberOfRows];
            String[] filters = new String[estimatedNumberOfRows];
            String[] columnNamesOfSetters = new String[estimatedNumberOfRows];
            String[] settingData = new String[estimatedNumberOfRows];

            int numberOfColumnsAndFilters = extractTheColumnNamesAndItsDataAndReturnTheNumberOfColumnsUnderDiscussion(
                    inputOfFilters, columnNamesOfFilters, filters);
            int numberOfColumnsAndSettingData = extractTheColumnNamesAndItsDataAndReturnTheNumberOfColumnsUnderDiscussion(
                    inputOfData, columnNamesOfSetters, settingData);

            if (!isColumnNameValid(table, columnNamesOfFilters)) {
                System.out.println(RED + "Error! column does not exist\n" + RESET);
                return 0;
            }
            if (!isColumnNameValid(table, columnNamesOfSetters)) {
                System.out.println(RED + "Error! column does not exist\n" + RESET);
                return 0;
            }

            if (!isTypeFormatValid(columnNamesOfFilters, filters, numberOfColumnsAndFilters)) {
                System.out.println(RED + "Error! wrong type format\n" + RESET);
                return 0;
            }
            if (!isTypeFormatValid(columnNamesOfSetters, settingData, numberOfColumnsAndSettingData)) {
                System.out.println(RED + "Error! wrong type format\n" + RESET);
                return 0;
            }
            int[] changingRows = new int[estimatedNumberOfRows];

            if (!doesElementExist(table, filters, columnNamesOfFilters)) {
                System.out.println(RED + "Error! filter not found\n" + RESET);
                return 0;
            }
            findRows(changingRows, table, filters, columnNamesOfFilters);

            int numberOfSetLines = setFilteredRowsAndReturnTheCount(table, columnNamesOfSetters, settingData,
                    changingRows, numberOfColumnsAndFilters);
            System.out.println(GREEN + "Provided data was set based on given filters" + RESET);
            System.out.println(GREEN + "number of " + numberOfSetLines + " row(s) were set\n" + RESET);
            return numberOfSetLines;

        } else {
            System.out.println(RED + "Error! invalid input\n" + RESET);
            return 0;
        }
    }

    public static int setRowsAndCountThem(String[][] table, String[] columnNames, String[] settingData,
            int numberOfDataAndFilters) {
        int counter = 0;
        for (int x = 0; x < numberOfDataAndFilters; x++) {
            for (int j = 0; j < table[0].length; j++) {
                if (columnNames[x].equals(table[0][j])) {
                    counter += setAllRowsOfOneColumn(table, settingData[x], j);
                }
            }
        }
        return counter;
    }

    public static int setAllRowsOfOneColumn(String[][] table, String settingData, int column) {
        int counter = 0;
        for (int i = 1; i < table.length; i++) {
            if (table[i][column] != null) {
                table[i][column] = settingData;
                counter++;
            }
        }
        return counter;
    }

    public static int setFilteredRowsAndReturnTheCount(String[][] table, String[] columnNamesOfSetters,
            String[] settingData, int[] changingRows, int numberOfFilters) {
        HashMap<Integer, Integer> elementCount = new HashMap<>();
        for (int i = 0; changingRows[i] != 0; i++) {
            elementCount.put(changingRows[i], 0);
        }
        for (int i = 0; changingRows[i] != 0; i++) {
            elementCount.put(changingRows[i], elementCount.get(changingRows[i]) + 1);
        }
        int numberOfLinesGettingChanged = 0;
        for (int x = 0; settingData[x] != null; x++) {
            for (int i = 1; i < table.length; i++) {
                if (table[i][0] != null) {
                    if (elementCount.containsKey(i) && elementCount.get(i) == numberOfFilters) {
                        table[i][returnColumnIndexInTable(table[0], columnNamesOfSetters[x])] = settingData[x];
                        numberOfLinesGettingChanged++;
                    }
                }
            }
        }
        return numberOfLinesGettingChanged;
    }

    public static int returnColumnIndexInTable(String[] firstRow, String column) {
        for (int i = 0; i < firstRow.length; i++) {
            if (column.equals(firstRow[i])) {
                return i;
            }
        }
        return -1;
    }

    // Delete Table Methods:
    public static int delRow(String input) {
        String tableName;
        if (isTheNameOfTheTableEnteredCorrectly("\\w+\\s*\\(", input)) {
            tableName = returnTableName("\\w+\\s*\\(", input);
            if (!doesTableExistPreviously(tableName)) {
                System.out.println(RED + "Error! table not found\n" + RESET);
                return 0;
            }
            if (doArgumentsCheckPattern(input, REGEX_OF_DEL_ROW_WITH_FILTERS)) {
                input = returnArguments(input, REGEX_OF_DEL_ROW_WITH_FILTERS);
            } else {
                System.out.println(RED + "Error! invalid input\n" + RESET);
                return 0;
            }
            String[][] table = connectTableNameToTheTable.get(tableName);
            String[] columnNames = new String[estimatedNumberOfRows];
            String[] filters = new String[estimatedNumberOfRows];

            int numberOfFilters = extractTheColumnNamesAndItsDataAndReturnTheNumberOfColumnsUnderDiscussion(input,
                    columnNames, filters);

            if (!isColumnNameValid(table, columnNames)) {
                System.out.println(RED + "Error! column name not found\n" + RESET);
                return 0;
            }

            if (!isTypeFormatValid(columnNames, filters, numberOfFilters)) {
                System.out.println(RED + "Error! invalid type format\n" + RESET);
                return 0;
            }
            if (!doesElementExist(table, filters, columnNames)) {
                System.out.println(RED + "Error! filter not found\n" + RESET);
                return 0;
            }
            int[] deletingRows = new int[estimatedNumberOfRows];
            findRows(deletingRows, table, filters, columnNames);

            int numberOfDeletedRows = delFilteredRowsAndReturnTheCount(table, deletingRows, numberOfFilters);
            System.out.println(GREEN + numberOfDeletedRows + " Filtered row(s) were deleted successfully\n" + RESET);
            return numberOfDeletedRows;

        } else {
            if (isTheNameOfTheTableEnteredCorrectly("\\w+$", input)) {
                tableName = returnTableName("\\w+$", input);
                if (!doesTableExistPreviously(tableName)) {
                    System.out.println(RED + "Error! table not found\n" + RESET);
                    return 0;
                }
                String[][] table = connectTableNameToTheTable.get(tableName);
                int numberOfDeletedRows = deleteAllRowsAndReturnTheCount(table);
                System.out.println(GREEN + numberOfDeletedRows + " Filtered rows were deleted successfully\n" + RESET);
                return numberOfDeletedRows;
            } else {
                System.out.println(RED + "Error! Invalid Input\n" + RESET);
                return 0;
            }
        }
    }

    public static int delFilteredRowsAndReturnTheCount(String[][] table, int[] deletingRows, int numberOfFilters) {
        HashMap<Integer, Integer> elementCount = new HashMap<>();
        int numberOfDeletingLines = 0;
        for (int i = 0; deletingRows[i] != 0; i++) {
            elementCount.put(deletingRows[i], 0);
        }
        for (int i = 0; deletingRows[i] != 0; i++) {
            elementCount.put(deletingRows[i], elementCount.get(deletingRows[i]) + 1);
        }
        for (int i = 1; i < table.length; i++) {
            if (table[i][0] != null) {
                if (elementCount.containsKey(i) && elementCount.get(i) == numberOfFilters) {
                    deleteOneRow(i, table);
                    numberOfDeletingLines++;
                }
            }
        }
        return numberOfDeletingLines;
    }

    public static void deleteOneRow(int row, String[][] table) {
        for (int j = 0; j < table[0].length; j++) {
            table[row][j] = null;
        }
    }

    public static int deleteAllRowsAndReturnTheCount(String[][] table) {
        int row;
        for (row = 1; row < table.length; row++) {
            if (table[row][0] != null) {
                deleteOneRow(row, table);
            }
        }
        return row - 1;
    }
}
