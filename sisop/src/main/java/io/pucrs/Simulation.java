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

  public Simulation(String path) {
    this.path = path;
  }

  public void Run() {
    // Get config information
    this.config = ReadConfigFile();

    // Set initial states
    this.tasks = new ArrayList<>();

    for (int i = 0; i < config.getFiles().size(); i++) {
      tasks.add(
          new Task(
              config.getFiles().get(i),
              config.getArrivalTimes().get(i),
              config.getExecTimes().get(i),
              config.getDeadlines().get(i)));
    }

    // Simulation loop
    this.currentTime = 0;
    while (this.currentTime <= this.config.getTotalTime()) {
      /*
       * 1) analisa o menor deadline
       * 2) executa a tarefa com menor deadline
       * 3) retorna para lista de prontos
       * 4) atualiza os estados
       * 5) analisa novamente o menor deadline
       */

      DisplaySimulation();
    }

    // TODO End simulation
  }

  private void DisplaySimulation() {
    String displayTasks = "";
    for (int i = 0; i < tasks.size(); i++) {
      displayTasks += "\tT" + i;
    }
    System.out.println("");
  }

  private Task GetSmallestDeadlineTask() {
    int smallestDealine = Integer.MAX_VALUE;
    int taskIndex = 0;
    for (Task task : tasks) {
      if (task.getPi() < smallestDealine) {
        smallestDealine = task.getPi();
      }
    }
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
