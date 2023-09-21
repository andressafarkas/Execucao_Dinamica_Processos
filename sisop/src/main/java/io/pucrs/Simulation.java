package io.pucrs;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
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
  private String configPath;
  private int currentTime;
  private ConfigParser config;

  private List<Task> blocked;
  private List<Task> ready;
  private List<Task> running;
  private List<Task> waiting;

  public Simulation(String configPath) {
    this.configPath = configPath;

    this.blocked = new ArrayList<>();
    this.ready = new ArrayList<>();
    this.running = new ArrayList<>();
    this.waiting = new ArrayList<>();

  }

  public void Run() {
    // Pega as informações do arquivo de configurações config.json
    ReadConfigFile();

    // Atualiza os estados iniciais
    UpdateInitialStates();

    // Loop da simulação
    DisplayHeader();
    this.currentTime = 0;
    while (this.currentTime <= this.config.getTotalTime()) {
      // Executa a tarefa com maior prioridade (menor deadline)
      GetSmallestDeadlineTask();

      // Atualiza o tempo das tarefas bloqueadas para que possam retornar ao estado de
      // prontas novamente
      UpdateBlockedTasks();

      // Executa linha código refente ao PC atual
      // Atualiza o Acc e Pc da atividade que está sendo executada
      // Atualiza o tempo da atividade que está sendo executada
      if (!this.running.isEmpty())
        this.running.get(0).updateTask();

      // Atualiza o tempo atual
      this.currentTime++;

      // Imprime informação da atividade que está sendo executada
      DisplaySimulation();

      // Atualiza deadlines e verifica se a atividade corrente já finalizou
      // no período atual
      UpdateDeadlines();

      // Verifica se há alguma tarefa que esteja para chegar ainda, e se caso
      // houver, então adiciona essa tarefa a lista de prontos com o deadline
      // atual corrigido (currentTime + deadline)
      CheckArrival();
    }

    DisplayLostDeadline();
  }

  private void CheckArrival() {
    if (!this.waiting.isEmpty()) {
      for (int i = 0; i < this.waiting.size(); i++) {
        if (this.waiting.get(i).getArrivalTime() == this.currentTime) {
          int newDeadline = this.currentTime + this.waiting.get(i).getDeadline();
          this.waiting.get(i).setCurrentDeadline(newDeadline);
          this.ready.add(this.waiting.remove(i));
        }
      }
    }
  }

  private void UpdateBlockedTasks() {
    for (Task task : this.blocked) {
      int currentBlockedTime = task.getParser().getAditionalTime();
      task.getParser().updateAditionalTime(currentBlockedTime - 1);
      if (task.getParser().getAditionalTime() == 0) {
        task.setBlocked(false);
      }
    }

    for (int i = 0; i < this.blocked.size(); i++) {
      if (!this.blocked.get(i).isBlocked())
        this.ready.add(this.blocked.remove(i));
    }
  }

  private void UpdateInitialStates() {
    ProgramReader reader = new ProgramReader();

    for (int i = 0; i < this.config.getFiles().size(); i++) {
      ProgramParser parser = reader.ReadFile(this.config.getFullPath(), this.config.getFiles().get(i));

      if (this.config.getArrivalTimes().get(i) == 0) {
        this.ready.add(
            new Task(
                i,
                config.getFiles().get(i),
                config.getArrivalTimes().get(i),
                config.getExecTimes().get(i),
                config.getDeadlines().get(i),
                parser));
      } else {
        this.waiting.add(
            new Task(
                i,
                config.getFiles().get(i),
                config.getArrivalTimes().get(i),
                config.getExecTimes().get(i),
                config.getDeadlines().get(i),
                parser));
      }

    }
  }

  private void UpdateDeadlines() {
    /*
     * Se a atividade que estava sendo executada chegar ao fim dentro do seu
     * deadline atual, então ela é removida da fila de executando e passa para a
     * fila de prontos
     */
    if (!this.running.isEmpty() && this.running.get(0).isFinished()) {
      this.ready.add(this.running.remove(0));
    }

    /*
     * Se a atividade que estava sendo executada for bloqueada devido a uma chamada
     * de sistema, então ela é removida da fila de executando e passa para a
     * fila de bloqueados
     */
    if (!this.running.isEmpty() && this.running.get(0).isBlocked()) {
      this.blocked.add(this.running.remove(0));
    }

    /*
     * Se o tempo atual alcançar o deadline das atividades na fila de prontas
     * então atualiza o novo deadline dessas atividades e marca a flag isFinished
     * como falsa para que elas possam ser executadas no novo deadline delas
     * 
     * Se caso houver alguma tarefa na fila de prontos que ainda não tenha sido
     * finalizada e tenha alcançado seu deadline, então será gravado o momento da
     * perda do seu deadline
     */
    for (int i = 0; i < this.ready.size(); i++) {
      if (this.ready.get(i).getCurrentDeadline() == this.currentTime) {
        this.ready.get(i).setFinished(false);
        int newDeadline = this.ready.get(i).getDeadline() + this.ready.get(i).getCurrentDeadline();
        this.ready.get(i).setCurrentDeadline(newDeadline);

        if (this.ready.get(i).getCurrentExecutionTime() > 0)
          this.ready.get(i).addLostDeadline(this.currentTime);
      }
    }

    /*
     * Se caso algum processo bloqueado perder o deadline, seu deadline é atualizado
     */
    for (Task task : this.blocked) {
      if (task.getCurrentDeadline() == this.currentTime) {
        int newDeadline = task.getDeadline() + task.getCurrentDeadline();
        task.setCurrentDeadline(newDeadline);
        task.addLostDeadline(this.currentTime);
      }
    }

    /*
     * Se caso algum processo em execução perder o deadline, então o processo
     * continua sendo executado devido sua prioridade e seu deadline é atualizado
     */
    if (!this.running.isEmpty()) {
      for (int i = 0; i < this.running.size(); i++) {
        if (this.running.get(i).getCurrentDeadline() == this.currentTime) {
          this.running.get(i).setFinished(false);
          int newDeadline = this.running.get(i).getDeadline() + this.running.get(i).getCurrentDeadline();
          this.running.get(i).setCurrentDeadline(newDeadline);
          this.running.get(i).addLostDeadline(this.currentTime);
        }
      }
    }

  }

  private void GetSmallestDeadlineTask() {
    /*
     * Busca a tarefa, que esteja disponível para ser executada, que tenha o menor
     * deadline entre a tarefa que estão sendo executada e as que estão prontas
     */
    int smallestDealine = this.running.size() > 0 ? this.running.get(0).getCurrentDeadline() : Integer.MAX_VALUE;
    int taskIndex = 0;
    for (int i = 0; i < this.ready.size(); i++) {
      if (this.ready.get(i).isFinished() == false && this.ready.get(i).getCurrentDeadline() < smallestDealine) {
        smallestDealine = this.ready.get(i).getCurrentDeadline();
        taskIndex = i;
      }
    }

    /*
     * Preempta a atividade com menor deadline para a lista de tarefas em execução
     * e atualiza a lista de tarefas prontas
     */
    if (this.running.size() > 0 && this.running.get(0).isFinished()
        && this.running.get(0).getCurrentDeadline() > smallestDealine) {
      this.ready.add(this.running.remove(0));
      this.running.add(this.ready.remove(taskIndex));
    } else if (this.running.isEmpty() && smallestDealine < Integer.MAX_VALUE) {
      this.running.add(this.ready.remove(taskIndex));
    }
  }

  private void DisplayHeader() {
    String header = "\n\t";
    for (int i = 0; i < this.config.getFiles().size(); i++) {
      header += "P" + i + "\t";
    }

    header += "Deadlines";

    System.out.println(header);
  }

  private void DisplaySimulation() {
    String displayTasks = Integer.toString(this.currentTime);
    String deadlines = "\t";

    if (!this.running.isEmpty()) {
      int runningTaskId = this.running.get(0).getId();
      for (int i = 0; i < this.config.getFiles().size(); i++) {
        if (runningTaskId == i)
          displayTasks += "\tX";
        else
          displayTasks += "\t-";
      }
    } else {
      for (int i = 0; i < this.config.getFiles().size(); i++) {
        displayTasks += "\t-";
      }
    }

    for (Task task : this.ready) {
      if (task.getCurrentDeadline() == this.currentTime)
        deadlines += "D" + task.getId() + " ";
    }

    for (Task task : this.blocked) {
      if (task.getCurrentDeadline() == this.currentTime)
        deadlines += "D" + task.getId() + " ";
    }

    if (!this.running.isEmpty() && this.running.get(0).getCurrentDeadline() == this.currentTime)
      deadlines += "D" + this.running.get(0).getId() + " ";

    System.out.println(displayTasks + deadlines);
  }

  private void DisplayLostDeadline() {
    String lostDeadlines = "";
    for (Task task : this.ready) {
      if (!task.getLostDeadlines().isEmpty())
        lostDeadlines += "\nP" + task.getId() + ": ";
      for (Integer lostDeadline : task.getLostDeadlines()) {
        lostDeadlines += lostDeadline + ",";
      }
    }

    if (!this.running.isEmpty() && !this.running.get(0).getLostDeadlines().isEmpty()) {
      lostDeadlines += "\nP" + this.running.get(0).getId() + ": ";
      for (Integer lostDeadline : this.running.get(0).getLostDeadlines()) {
        lostDeadlines += lostDeadline + ",";
      }
    }

    System.out.println("\nLost Deadlines...");
    System.out.println(lostDeadlines);
  }

  private void ReadConfigFile() {
    /*
     * Realiza a leitura do arquivo de configuração e faz o parser das informações
     * para a classe ConfigParser
     */
    try {
      ObjectMapper objectMapper = new ObjectMapper();
      File configFile = new File(this.configPath);
      this.config = objectMapper.readValue(configFile, ConfigParser.class);
    } catch (IOException e) {
      e.printStackTrace();
      System.out
          .println("\nSomething happened while trying to read information from the config file! Please try again.\n");
    }
  }
}
