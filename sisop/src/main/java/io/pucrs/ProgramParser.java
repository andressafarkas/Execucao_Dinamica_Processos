package io.pucrs;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class ProgramParser {
  private double acc = 0;
  private int pc = 0;
  private Map<String, Double> dataDict = new HashMap<>();
  private Map<Integer, String[]> codeDict = new HashMap<>();
  private Map<String, Integer> jumpDict = new HashMap<>();

  private void execute(Scanner scanner) {
    boolean endcode = false;

    while (!endcode) {
      String[] codeLine = codeDict.get(pc);
      String instruction = codeLine[0];
      String op = codeLine[1].toLowerCase();

      // Descomentar para imprimir qual instruçao esta executando
      // System.out.println(instruction + " " + op);

      switch (instruction.toLowerCase()) {
        case "add":
          if (op.startsWith("#")) {
            double inc = Double.parseDouble(op.replace("#", ""));
            updateAcc(acc + inc);
          } else {
            double inc = dataDict.get(op);
            updateAcc(acc + inc);
          }
          updatePc(pc + 1);
          break;

        case "sub":
          if (op.startsWith("#")) {
            double inc = Double.parseDouble(op.replace("#", ""));
            updateAcc(acc - inc);
          } else {
            double inc = dataDict.get(op);
            updateAcc(acc - inc);
          }
          updatePc(pc + 1);
          break;

        case "mult":
          if (op.startsWith("#")) {
            double inc = Double.parseDouble(op.replace("#", ""));
            updateAcc(acc * inc);
          } else {
            double inc = dataDict.get(op);
            updateAcc(acc * inc);
          }
          updatePc(pc + 1);
          break;

        case "div":
          if (op.startsWith("#")) {
            double inc = Double.parseDouble(op.replace("#", ""));
            updateAcc(acc / inc);
          } else {
            double inc = dataDict.get(op);
            updateAcc(acc / inc);
          }
          updatePc(pc + 1);
          break;

        case "load":
          double data = dataDict.get(op);
          updateAcc(data);
          updatePc(pc + 1);
          break;

        case "store":
          dataDict.remove(op);
          dataDict.put(op, acc);
          updatePc(pc + 1);
          break;

        case "brany":
          updatePc(jumpDict.get(op));
          break;

        case "brpos":
          if (acc > 0) {
            updatePc(jumpDict.get(op));
          } else {
            updatePc(pc + 1);
          }
          break;

        case "brzero":
          if (acc == 0) {
            updatePc(jumpDict.get(op));
          } else {
            updatePc(pc + 1);
          }
          break;

        case "brneg":
          if (acc < 0) {
            updatePc(jumpDict.get(op));
          } else {
            updatePc(pc + 1);
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
          updatePc(pc + 1);
          break;
        default:
          break;
      }
    }
  }

  private void updateAcc(double newAcc) {
    acc = newAcc;
  }

  private void updatePc(int newPc) {
    pc = newPc;
  }

  public double getAcc() {
    return acc;
  }

  public int getPc() {
    return pc;
  }

  public Map<String, Double> getDataDict() {
    return dataDict;
  }

  public Map<Integer, String[]> getCodeDict() {
    return codeDict;
  }

  public Map<String, Integer> getJumpDict() {
    return jumpDict;
  }
  
}
