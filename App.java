import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
    private static double acc = 0;
    private static int pc = 0;
    private static Map<String, Double> dataDict = new HashMap<>();
    private static Map<Integer, String[]> codeDict = new HashMap<>();
    private static Map<String, Integer> jumpDict = new HashMap<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<Task>();

        System.out.println("Informe as tarefas que serão executadas.");

        boolean reading = true;

        while (reading) {
            System.out.print("Digite o nome da tarefa: ");
            String file = scanner.nextLine();
            file = "programas_teste/" + file;

            System.out.print("Informe o instante de carga da tarefa: ");
            int arrivalTime = scanner.nextInt();

            System.out.print("Informe o tempo de execução da tarefa: ");
            int ci = scanner.nextInt();

            System.out.print("Informe o período da tarefa: ");
            int pi = scanner.nextInt();

            Task task = new Task(file, arrivalTime, ci, pi);
            tasks.add(task);
            
            System.out.print("Digite 0 se você terminou de inserir tarefas, ou qualquer tecla para adicionar mais tarefas: ");
            scanner.nextLine();
            String moreTask = scanner.nextLine();

            if(moreTask.equals("0")) {
                reading = false;
            }
            
        }
        
        for (Task task : tasks) {
            System.out.println(task.toString());
        }
        

        // Executa o programa
        // execute(scanner);
        scanner.close();
    }

    private static void readFile(String file) {
        try {
            FileReader flieReader = new FileReader(file);
            BufferedReader reader = new BufferedReader(flieReader);
            String line;
            boolean dataArea = false;
            boolean codeArea = false;
            int cont = 0;

            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty()) { 
                    continue;
                }

                String[] rawWords = line.split("\\s+");
                String[] words = Arrays.stream(rawWords)
                .filter(word -> !word.trim().isEmpty())
                .toArray(String[]::new);

                String instruction = words[0];

                if (instruction.toLowerCase().equals(".data")) {
                    dataArea = true;
                } else if (instruction.toLowerCase().equals(".enddata")) {
                    dataArea = false;
                } else if (dataArea) {
                    dataDict.put(instruction, Double.parseDouble(words[1]));
                } else if (instruction.toLowerCase().equals(".code")) {
                    codeArea = true;
                } else if (instruction.toLowerCase().equals(".endcode")) {
                    codeArea = false;
                } else if (codeArea) {
                    if (words.length == 1) {
                        jumpDict.put(words[0].toLowerCase().replace(":", ""), cont);
                    } else {
                        codeDict.put(cont, words);
                        cont+=1;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Ocorreu um erro ao ler o arquivo: " + e.getMessage());
        }

    }

    private static void execute(Scanner scanner) {
        boolean endcode = false;

        while(!endcode) {
            String[] codeLine = codeDict.get(pc);
            String instruction = codeLine[0];
            String op = codeLine[1].toLowerCase();

            switch (instruction.toLowerCase()) {
                case "add":
                    if (op.startsWith("#")) {
                        double inc = Double.parseDouble(op.replace("#", ""));
                        updateAcc(acc+inc);
                    } else { 
                        double inc = dataDict.get(op);
                        updateAcc(acc+inc);
                    }
                    updatePc(pc+1);
                    break;

                case "sub":
                    if (op.startsWith("#")) {
                        double inc = Double.parseDouble(op.replace("#", ""));
                        updateAcc(acc-inc);
                    } else { 
                        double inc = dataDict.get(op);
                        updateAcc(acc-inc);
                    }
                    updatePc(pc+1);
                    break;

                case "mult":
                    if (op.startsWith("#")) {
                        double inc = Double.parseDouble(op.replace("#", ""));
                        updateAcc(acc*inc);
                    } else { 
                        double inc = dataDict.get(op);
                        updateAcc(acc*inc);
                    }
                    updatePc(pc+1);
                    break;

                case "div":
                    if (op.startsWith("#")) {
                        double inc = Double.parseDouble(op.replace("#", ""));
                        updateAcc(acc/inc);
                    } else { 
                        double inc = dataDict.get(op);
                        updateAcc(acc/inc);
                    }
                    updatePc(pc+1);
                    break;

                case "load":
                    double data = dataDict.get(op);
                    updateAcc(data);
                    updatePc(pc+1);
                    break;

                case "store":
                    dataDict.remove(op);
                    dataDict.put(op, acc);
                    updatePc(pc+1);
                    break;

                case "brany":
                    updatePc(jumpDict.get(op));
                    break;

                case "brpos":
                    if (acc > 0) {
                        updatePc(jumpDict.get(op));
                    } else {
                        updatePc(pc+1);
                    }
                    break;

                case "brzero":
                    if (acc == 0) {
                        updatePc(jumpDict.get(op));
                    } else {
                        updatePc(pc+1);
                    }
                    break;

                case "brneg":
                    if (acc < 0) {
                        updatePc(jumpDict.get(op));
                    } else {
                        updatePc(pc+1);
                    }
                    break;

                case "syscall":
                    if (op.equals("0")) {
                        System.out.println("Finalizou");
                        endcode = true;
                    } else if (op.equals("1")) {
                        System.out.println(acc);
                    } else if (op.equals("2")) {
                        acc = scanner.nextDouble();
                    }
                    updatePc(pc+1);
                    break;
                default: 
                    break;
            }
        }
    }

    private static void updateAcc(double newAcc) {
        acc = newAcc;
    }
    
    private static void updatePc(int newPc) {
        pc = newPc;
    }
}