package io.pucrs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Simulation {
  private String path;
  private int currentTime;
  private List<Task> tasks;
  private ConfigParser config;

  private List<Task> blocked;
  private List<Task> ready;
  private List<Task> running;

  public Simulation(String path) {
    this.path = path;

    this.blocked = new ArrayList<>();
    this.ready = new ArrayList<>();
    this.running = new ArrayList<>();
  }

  public void Run() {
    // Get config information
    this.config = ReadConfigFile();

    // Set initial states
    this.tasks = new ArrayList<>();

    for (int i = 0; i < config.getFiles().size(); i++) {
      this.tasks.add(
          new Task(
              config.getFiles().get(i),
              config.getArrivalTimes().get(i),
              config.getExecTimes().get(i),
              config.getDeadlines().get(i)));
    }

    // Simulation loop
    DisplayHeader();
    this.currentTime = 0;
    while (this.currentTime <= this.config.getTotalTime()) {
      /*
       * 1) analisa o menor deadline
       * 2) executa a tarefa com menor deadline
       * 3) retorna para lista de prontos
       * 4) atualiza os estados
       * 5) analisa novamente o menor deadline
       */

      currentTime++;

      DisplaySimulation(currentTime);
    }

    // TODO End simulation
  }

  private void DisplayHeader() {
    String displayTasks = "";
    for (int i = 0; i < this.tasks.size(); i++) {
      displayTasks += "\tT" + i;
    }
    System.out.println(displayTasks);
  }

  private void DisplaySimulation(int currentTime) {
    String displayTasks = Integer.toString(currentTime);
    for (int i = 0; i < this.tasks.size(); i++) {
      if (this.tasks.get(i).isRunning())
        displayTasks += "\tX";
      else
        displayTasks += "\t-";
    }
    System.out.println(displayTasks);
  }

  private Task GetSmallestDeadlineTask() {
    int smallestDealine = Integer.MAX_VALUE;
    int taskIndex = 0;
    for (Task task : tasks) {
      if (task.getPi() < smallestDealine) {
        smallestDealine = task.getPi();
      }
    }
    return null;
  }

  private ConfigParser ReadConfigFile() {
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      File configFile = new File(this.path);
      return objectMapper.readValue(configFile, ConfigParser.class);
    } catch (IOException e) {
      e.printStackTrace();
      System.out
          .println("\nSomething happened while trying to read information from the config file! Please try again.\n");
    }
    return null;
  }
}
