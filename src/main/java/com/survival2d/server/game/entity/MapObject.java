package com.survival2d.server.game.entity;

import lombok.Data;

@Data
public abstract class MapObject implements HasPosition {

  Vector position;
}
