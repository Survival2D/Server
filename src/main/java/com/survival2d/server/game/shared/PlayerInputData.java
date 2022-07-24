package com.survival2d.server.game.shared;

import lombok.Data;

@Data
public class PlayerInputData {
  boolean[] inputs;
  int time;

  public PlayerInputData(boolean[] inputs, int time) {
    this.inputs = inputs;
    this.time = time;
  }
}
