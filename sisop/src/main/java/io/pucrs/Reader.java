package io.pucrs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

public class Reader {

  public ProgramParser ReadFile(String file) {
    ProgramParser parser = new ProgramParser();
    file = "programas_teste/" + file;

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
          parser.getDataDict().put(instruction, Double.parseDouble(words[1]));
        } else if (instruction.toLowerCase().equals(".code")) {
          codeArea = true;
        } else if (instruction.toLowerCase().equals(".endcode")) {
          codeArea = false;
        } else if (codeArea) {
          if (words.length == 1) {
            parser.getJumpDict().put(words[0].toLowerCase().replace(":", ""), cont);
          } else {
            parser.getCodeDict().put(cont, words);
            cont += 1;
          }
        }
      }
      reader.close();
    } catch (IOException e) {
      System.err.println("Ocorreu um erro ao ler o arquivo: " + e.getMessage());
    }
    return parser;
  }
}
