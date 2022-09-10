package com.survival2d.server.match;

import lombok.Data;

@Data
public abstract class MapObject implements HasPosition {

  Vector position;
}
