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

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public class Simulation {
  @Getter
  private String path;

  public void Run() {
    // Get config information
    ConfigParser config = ReadConfigFile();

    // Set initial states
    List<Task> tasks = new ArrayList<>();

    for (int i = 0; i < config.getFiles().size(); i++) {
      tasks.add(
          new Task(
              config.getFiles().get(i),
              config.getArrivalTimes().get(i),
              config.getExecTimes().get(i),
              config.getDeadlines().get(i)));
    }

    // Simulation loop
    int currentTime = 0;
    while (currentTime <= config.getTotalTime()) {
      // TODO Compute program information
      currentTime += ComputeSmallestDeadlineProgram(tasks);
      // TODO Update states
      // TODO Display current time information

      break;
    }

    // TODO End simulation
  }

  public int ComputeSmallestDeadlineProgram(List<Task> tasks) {
    int smallestDeadline = Integer.MAX_VALUE;
    int taskIndex = 0;
    for (int i = 0; i < tasks.size(); i++) {
      if (tasks.get(i).getPi() < smallestDeadline) {
        smallestDeadline = tasks.get(i).getPi();
        taskIndex = i;
      }
    }

    // update task deadline

    return tasks.get(taskIndex).getCi();
  }

  public ConfigParser ReadConfigFile() {
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
