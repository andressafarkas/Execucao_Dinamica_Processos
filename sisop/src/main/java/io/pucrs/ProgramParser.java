package io.pucrs;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

public class ProgramParser {
  private double acc = 0;
  private int pc = 0;
  private int aditionalTime = 0;
  private Map<String, Double> initialDataDict = new HashMap<>();
  private Map<String, Double> dataDict = new HashMap<>();
  private Map<Integer, String[]> codeDict = new HashMap<>();
  private Map<String, Integer> jumpDict = new HashMap<>();

  // public void execute(Scanner scanner) {
  public boolean execute() {

    boolean finished = false;
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
          finished = true;
        } else if (op.equals("1")) {
          updateAditionalTime(new Random().nextInt(3 - 1) + 1);
          System.out.println("Blocked by " + this.aditionalTime + " time units!");
          System.out.println("acc: " + acc);
        } else if (op.equals("2")) {
          updateAditionalTime(new Random().nextInt(3 - 1) + 1);
          System.out.println("Blocked by " + this.aditionalTime + " time units!");
          System.out.println("Enter a value: ");
          Scanner scanner = new Scanner(System.in);
          acc = scanner.nextDouble();
        }
        updatePc(pc + 1);
        break;
      default:
        break;
    }
    return finished;
  }

  private void updateAcc(double newAcc) {
    acc = newAcc;
  }

  private void updatePc(int newPc) {
    pc = newPc;
  }

  public void updateAditionalTime(int newAditionalTime) {
    aditionalTime = newAditionalTime;
  }

  public double getAcc() {
    return acc;
  }

  public int getPc() {
    return pc;
  }

  public int getAditionalTime() {
    return aditionalTime;
  }

  public Map<String, Double> getInitialDataDict() {
    return initialDataDict;
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

  public void restoreValues() {
    this.updateAcc(0);
    this.updatePc(0);
    this.updateAditionalTime(0);

    this.dataDict.clear();
    this.dataDict.putAll(this.initialDataDict);
  }
}
