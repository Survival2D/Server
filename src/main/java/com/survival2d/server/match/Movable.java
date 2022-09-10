package com.survival2d.server.match;

public interface Movable extends HasPosition {

  // public void moveBy(double x, double y){
//  setPosition();
//}
  default void moveBy(Vector v) {
    setPosition(Vector.add(getPosition(), v));
  }

  //public void moveTo(double x, double y);
  default void moveTo(Vector v) {
    setPosition(v);
  }

}
