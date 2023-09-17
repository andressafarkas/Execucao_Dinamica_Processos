package io.pucrs;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ConfigParser {
  private String fullPath;
  private List<String> files;
  private List<Integer> execTimes;
  private List<Integer> deadlines;
  private List<Integer> arrivalTimes;
  private Integer totalTime;
}
