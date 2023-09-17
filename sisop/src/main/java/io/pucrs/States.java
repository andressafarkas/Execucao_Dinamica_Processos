package io.pucrs;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class States {
  private List<Task> blocked;
  private List<Task> ready;
  private List<Task> running;
}
