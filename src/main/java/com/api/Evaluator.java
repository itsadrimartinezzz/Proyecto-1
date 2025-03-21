package src.main.java.com.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

public class Evaluator {

    private static Map<String, Object> variables = new HashMap<>();
    private static final Map<String, LispFunction> functions = new HashMap<>();

    public static Object evaluate(Object expr) {
        if (expr instanceof List) {
            List<?> exprList = (List<?>) expr;
            if (exprList.isEmpty()) return null;

            Object firstElement = exprList.get(0);
            if (!(firstElement instanceof String)) {
                // Evaluar cada elemento de la lista si el primer elemento no es un operador
                List<Object> evaluatedList = new ArrayList<>();
                for (Object element : exprList) {
                    evaluatedList.add(evaluate(element));
                }
                return evaluatedList;
            }

            String command = firstElement.toString();

            if (command.equals("IF") && exprList.size() >= 4) {
                Object condition = exprList.get(1);

                // Si la condición es una lista, verificamos si podría ser una comparación con <= o >=
                if (condition instanceof List) {
                    List<?> condList = (List<?>) condition;
                    if (condList.size() >= 3 && condList.get(0) instanceof String) {
                        String op = condList.get(0).toString();

                        // Si el primer elemento es "<=", "<=" o es una lista que comienza con "<"
                        if (op.equals("<=") || op.equals(">=")) {
                            // Esto está bien, es un operador válido
                        } else if ((op.equals("<") || op.equals(">")) && condList.size() >= 2 &&
                                condList.get(1) instanceof String && condList.get(1).toString().equals("=")) {
                            // Esto es un caso especial: el operador está dividido como ["<", "="] o [">", "="]
                            // Reconstruimos la condición con el operador correcto
                            List<Object> newCondList = new ArrayList<>();
                            newCondList.add(op + "="); // Combinamos en "<=" o ">="

                            // Añadimos el resto de los operandos
                            for (int i = 2; i < condList.size(); i++) {
                                newCondList.add(condList.get(i));
                            }

                            // Reemplazamos la condición con nuestra versión corregida
                            List<Object> newExprList = new ArrayList<>(exprList);
                            newExprList.set(1, newCondList);

                            System.out.println("Corrigiendo estructura de la condición IF: " + exprList + " -> " + newExprList);

                            // Evaluamos con la condición corregida
                            return evaluate(newExprList);
                        }
                    }
                }

                // Evaluar la condición
                Object condResult = evaluate(condition);

                // Decidir qué parte del IF ejecutar
                if (isTrue(condResult)) {
                    return evaluate(exprList.get(2)); // Parte verdadera
                } else {
                    return evaluate(exprList.get(3)); // Parte falsa
                }
            }

            switch (command) {

                case "+":
                    return evaluateArithmetic("+", exprList);
                case "-":
                    return evaluateArithmetic("-", exprList);
                case "*":
                    return evaluateArithmetic("*", exprList);
                case "/":
                    return evaluateArithmetic("/", exprList);
                case "=":
                    return evaluateComparison("=", exprList);
                case "<":
                    return evaluateComparison("<", exprList);
                case ">":
                    return evaluateComparison(">", exprList);
                case "<=":
                    return evaluateComparison("<=", exprList);
                case ">=":
                    return evaluateComparison(">=", exprList);
                case "QUOTE":
                    return exprList.get(1);
                case "SETQ":
                    return handleSetq(exprList);
                case "DEFUN":
                    return handleDefun(exprList);
                case "COND":
                    return handleCond(exprList);
                case "ATOM":
                case "LIST":
                case "EQUAL":
                    return handlePredicates(command, exprList);
                case "T":
                    return true;
                default:
                    // Si no es una palabra clave, intentamos llamar a una función definida por el usuario
                    if (functions.containsKey(command)) {
                        return handleFunctionCall(command, exprList);
                    }
                    // Si no es una función definida, evaluamos cada elemento
                    List<Object> evaluatedList = new ArrayList<>();
                    for (Object element : exprList) {
                        evaluatedList.add(evaluate(element));
                    }
                    return evaluatedList;
            }
        } else if (expr instanceof String) {
            String strExpr = expr.toString();
            if (variables.containsKey(strExpr)) {
                return variables.get(strExpr);
            } else if (strExpr.equals("T")) {
                return true;
            } else if (strExpr.equals("NIL")) {
                return false;
            } else if (strExpr.matches("-?\\d+")) {
                return Integer.parseInt(strExpr);
            } else if (strExpr.matches("-?\\d+\\.\\d+")) {
                return Double.parseDouble(strExpr);
            }
        }
        return expr;
    }

    private static boolean isTrue(Object condition) {
        if (condition instanceof Boolean) {
            return (Boolean) condition;
        } else if (condition instanceof Number) {
            return ((Number) condition).doubleValue() != 0;
        } else if (condition instanceof String) {
            String str = (String) condition;
            if (str.equals("T")) return true;
            if (str.equals("NIL")) return false;
        } else if (condition instanceof List) {
            return !((List<?>) condition).isEmpty();
        }
        return condition != null;
    }

    private static Object evaluateArithmetic(String operator, List<?> exprList) {
        if (exprList.size() < 2) throw new IllegalArgumentException("Operación aritmética incompleta");

        if (exprList.size() == 2) {
            // Operación unaria
            Object operand = evaluate(exprList.get(1));
            if (!(operand instanceof Number)) {
                throw new IllegalArgumentException("Operando no numérico: " + operand);
            }
            if (operator.equals("-")) {
                if (operand instanceof Integer) {
                    return -((Integer) operand);
                } else {
                    return -((Double) operand);
                }
            }
            return operand;
        }

        // Operación binaria o n-aria
        Object first = evaluate(exprList.get(1));
        if (!(first instanceof Number)) {
            throw new IllegalArgumentException("Operando no numérico: " + first);
        }

        double result = ((Number) first).doubleValue();
        boolean isInteger = first instanceof Integer;

        for (int i = 2; i < exprList.size(); i++) {
            Object nextObj = evaluate(exprList.get(i));
            if (!(nextObj instanceof Number)) {
                throw new IllegalArgumentException("Operando no numérico: " + nextObj);
            }
            double next = ((Number) nextObj).doubleValue();

            if (nextObj instanceof Double) {
                isInteger = false;
            }

            switch (operator) {
                case "+": result += next; break;
                case "-": result -= next; break;
                case "*": result *= next; break;
                case "/":
                    if (next == 0) {
                        throw new ArithmeticException("División por cero");
                    }
                    result /= next;
                    isInteger = false; // División siempre da resultado decimal
                    break;
            }
        }

        // Retornar como entero o decimal según corresponda
        return isInteger ? (int) result : result;
    }

    private static Object evaluateComparison(String operator, List<?> exprList) {
        if (exprList.size() < 3) {
            throw new IllegalArgumentException("Comparación requiere al menos dos operandos");
        }

        Object left = evaluate(exprList.get(1));
        Object right = evaluate(exprList.get(2));

        System.out.println("Comparando: " + left + " (" +
                (left != null ? left.getClass().getName() : "null") +
                ") " + operator + " " + right + " (" +
                (right != null ? right.getClass().getName() : "null") + ")");

        // Intentar convertir los operandos a números si es posible
        if (left instanceof String) {
            String leftStr = (String) left;
            if (leftStr.matches("-?\\d+")) {
                left = Integer.parseInt(leftStr);
            } else if (leftStr.matches("-?\\d+\\.\\d+")) {
                left = Double.parseDouble(leftStr);
            }
        }

        if (right instanceof String) {
            String rightStr = (String) right;
            if (rightStr.matches("-?\\d+")) {
                right = Integer.parseInt(rightStr);
            } else if (rightStr.matches("-?\\d+\\.\\d+")) {
                right = Double.parseDouble(rightStr);
            }
        }

        if (!(left instanceof Number) || !(right instanceof Number)) {
            throw new IllegalArgumentException("Comparación requiere operandos numéricos: " +
                    left + " (" + (left != null ? left.getClass().getName() : "null") +
                    "), " + right + " (" + (right != null ? right.getClass().getName() : "null") + ")");
        }

        double leftVal = ((Number) left).doubleValue();
        double rightVal = ((Number) right).doubleValue();

        switch (operator) {
            case "=": return leftVal == rightVal;
            case "<": return leftVal < rightVal;
            case ">": return leftVal > rightVal;
            case "<=": return leftVal <= rightVal;
            case ">=": return leftVal >= rightVal;
            default: throw new IllegalArgumentException("Operador de comparación desconocido: " + operator);
        }
    }

    private static Object handleSetq(List<?> exprList) {
        if (exprList.size() != 3) throw new IllegalArgumentException("SETQ requiere una variable y un valor");
        String varName = exprList.get(1).toString();
        Object value = evaluate(exprList.get(2));
        variables.put(varName, value);
        return value;
    }

    private static Object handleDefun(List<?> exprList) {
        if (exprList.size() < 4) {
            throw new IllegalArgumentException("DEFUN necesita un nombre, parámetros y un cuerpo.");
        }

        String functionName = exprList.get(1).toString();
        Object paramsObj = exprList.get(2);

        List<String> params = new ArrayList<>();

        // Manejo correcto de los parámetros
        if (paramsObj instanceof List) {
            List<?> paramsList = (List<?>) paramsObj;
            for (Object param : paramsList) {
                params.add(param.toString());
            }
        } else {
            params.add(paramsObj.toString());
        }

        // El cuerpo es todo lo que viene después de los parámetros
        List<Object> body = new ArrayList<>();
        for (int i = 3; i < exprList.size(); i++) {
            body.add(exprList.get(i));
        }

        LispFunction function = new LispFunction(params, body);
        functions.put(functionName, function);

        return "Función " + functionName + " definida.";
    }

    private static Object handleFunctionCall(String functionName, List<?> exprList) {
        if (!functions.containsKey(functionName)) {
            throw new IllegalArgumentException("Función no definida: " + functionName);
        }

        System.out.println("Llamando a función: " + functionName + " con argumentos: " + exprList.subList(1, exprList.size()));

        LispFunction function = functions.get(functionName);
        List<String> params = function.getParams();
        List<?> body = function.getBody();

        // Verificar número correcto de argumentos
        if (exprList.size() - 1 != params.size()) {
            throw new IllegalArgumentException("Número incorrecto de argumentos para " + functionName);
        }

        // Guardar el ámbito actual de variables
        Map<String, Object> previousVariables = new HashMap<>(variables);

        try {
            // Evaluar argumentos y asignarlos a los parámetros
            for (int i = 0; i < params.size(); i++) {
                Object argValue = evaluate(exprList.get(i + 1));

                // Depuración - imprimir el valor del argumento
                System.out.println("Parámetro " + params.get(i) + " = " + argValue + " (tipo: " +
                        (argValue != null ? argValue.getClass().getName() : "null") + ")");

                // Intentar convertir a número si es posible y debería ser un número
                if (argValue instanceof String) {
                    String argStr = (String) argValue;
                    if (argStr.matches("-?\\d+")) {
                        argValue = Integer.parseInt(argStr);
                    } else if (argStr.matches("-?\\d+\\.\\d+")) {
                        argValue = Double.parseDouble(argStr);
                    }
                    System.out.println("Después de conversión: " + argValue + " (tipo: " +
                            (argValue != null ? argValue.getClass().getName() : "null") + ")");
                }

                variables.put(params.get(i), argValue);
            }

            System.out.println("Variables en el ámbito de " + functionName + ": " + variables);

            // Evaluar el cuerpo de la función
            Object result = null;
            for (Object expr : body) {
                System.out.println("Evaluando expresión en el cuerpo de " + functionName + ": " + expr);
                result = evaluate(expr);
                System.out.println("Resultado parcial: " + result);
            }

            return result;
        } finally {
            // Restaurar el ámbito previo de variables
            variables = new HashMap<>(previousVariables);
        }
    }

    private static Object handleCond(List<?> exprList) {
        for (int i = 1; i < exprList.size(); i++) {
            Object clause = exprList.get(i);

            if (!(clause instanceof List)) {
                throw new IllegalArgumentException("Cláusula COND debe ser una lista");
            }

            List<?> condClause = (List<?>) clause;
            if (condClause.size() < 2) {
                throw new IllegalArgumentException("Cláusula COND debe tener condición y expresión");
            }

            Object condition = evaluate(condClause.get(0));

            if (isTrue(condition)) {
                // Evaluar todas las expresiones en la cláusula y devolver el valor de la última
                Object result = null;
                for (int j = 1; j < condClause.size(); j++) {
                    result = evaluate(condClause.get(j));
                }
                return result;
            }
        }

        return null; // Si ninguna condición se cumple
    }

    private static Object handlePredicates(String predicate, List<?> exprList) {
        if (exprList.size() < 2) {
            throw new IllegalArgumentException("Predicado requiere al menos un argumento");
        }

        switch (predicate) {
            case "ATOM":
                Object arg = evaluate(exprList.get(1));
                return !(arg instanceof List);
            case "LIST":
                return evaluate(exprList.get(1)) instanceof List;
            case "EQUAL":
                if (exprList.size() != 3) {
                    throw new IllegalArgumentException("EQUAL requiere exactamente dos argumentos");
                }
                Object arg1 = evaluate(exprList.get(1));
                Object arg2 = evaluate(exprList.get(2));
                return arg1.equals(arg2);
            default:
                throw new IllegalArgumentException("Predicado desconocido: " + predicate);
        }
    }
}

class LispFunction {
    private final List<String> params;
    private final List<?> body;

    public LispFunction(List<String> params, List<?> body) {
        this.params = params;
        this.body = body;
    }

    public List<String> getParams() {
        return params;
    }

    public List<?> getBody() {
        return body;
    }
}