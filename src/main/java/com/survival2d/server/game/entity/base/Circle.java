package com.survival2d.server.game.entity.base;

import lombok.Value;

@Value
public class Circle implements Shape {
  //Không chứa Position do đã nằm trong MapObject rồi

  double radius;
}
