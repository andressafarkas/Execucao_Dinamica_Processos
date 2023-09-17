import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class App {
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

    private static void readFile(Task task) {
        try {
            FileReader flieReader = new FileReader(task.file);
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
                    task.dataDict.put(instruction, Double.parseDouble(words[1]));
                } else if (instruction.toLowerCase().equals(".code")) {
                    codeArea = true;
                } else if (instruction.toLowerCase().equals(".endcode")) {
                    codeArea = false;
                } else if (codeArea) {
                    if (words.length == 1) {
                        task.jumpDict.put(words[0].toLowerCase().replace(":", ""), cont);
                    } else {
                        task.codeDict.put(cont, words);
                        cont+=1;
                    }
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Ocorreu um erro ao ler o arquivo: " + e.getMessage());
        }

    }

    private static void execute(Scanner scanner, Task task) {
        boolean endcode = false;

        while(!endcode) {
            String[] codeLine = task.codeDict.get(task.pc);
            String instruction = codeLine[0];
            String op = codeLine[1].toLowerCase();

            switch (instruction.toLowerCase()) {
                case "add":
                    if (op.startsWith("#")) {
                        double inc = Double.parseDouble(op.replace("#", ""));
                        task.updateAcc(task.acc+inc);
                    } else { 
                        double inc = task.dataDict.get(op);
                        task.updateAcc(task.acc+inc);
                    }
                    task.updatePc(task.pc+1);
                    break;

                case "sub":
                    if (op.startsWith("#")) {
                        double inc = Double.parseDouble(op.replace("#", ""));
                        task.updateAcc(task.acc-inc);
                    } else { 
                        double inc = task.dataDict.get(op);
                        task.updateAcc(task.acc-inc);
                    }
                    task.updatePc(task.pc+1);
                    break;

                case "mult":
                    if (op.startsWith("#")) {
                        double inc = Double.parseDouble(op.replace("#", ""));
                        task.updateAcc(task.acc*inc);
                    } else { 
                        double inc = task.dataDict.get(op);
                        task.updateAcc(task.acc*inc);
                    }
                    task.updatePc(task.pc+1);
                    break;

                case "div":
                    if (op.startsWith("#")) {
                        double inc = Double.parseDouble(op.replace("#", ""));
                        task.updateAcc(task.acc/inc);
                    } else { 
                        double inc = task.dataDict.get(op);
                        task.updateAcc(task.acc/inc);
                    }
                    task.updatePc(task.pc+1);
                    break;

                case "load":
                    double data = task.dataDict.get(op);
                    task.updateAcc(data);
                    task.updatePc(task.pc+1);
                    break;

                case "store":
                    task.dataDict.remove(op);
                    task.dataDict.put(op, task.acc);
                    task.updatePc(task.pc+1);
                    break;

                case "brany":
                    task.updatePc(task.jumpDict.get(op));
                    break;

                case "brpos":
                    if (task.acc > 0) {
                        task.updatePc(task.jumpDict.get(op));
                    } else {
                        task.updatePc(task.pc+1);
                    }
                    break;

                case "brzero":
                    if (task.acc == 0) {
                        task.updatePc(task.jumpDict.get(op));
                    } else {
                        task.updatePc(task.pc+1);
                    }
                    break;

                case "brneg":
                    if (task.acc < 0) {
                        task.updatePc(task.jumpDict.get(op));
                    } else {
                        task.updatePc(task.pc+1);
                    }
                    break;

                case "syscall":
                    if (op.equals("0")) {
                        System.out.println("Finalizou");
                        endcode = true;
                    } else if (op.equals("1")) {
                        System.out.println(task.acc);
                    } else if (op.equals("2")) {
                        task.acc = scanner.nextDouble();
                    }
                    task.updatePc(task.pc+1);
                    break;
                default: 
                    break;
            }
        }
    }
}