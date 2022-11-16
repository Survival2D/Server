package com.survival2d.server.game.entity.obstacle;


import com.survival2d.server.game.entity.base.Destroyable;
import com.survival2d.server.game.entity.base.MapObject;
import com.survival2d.server.game.entity.base.Shape;

public interface Obstacle extends MapObject, Destroyable {

  Shape getShape();
}
