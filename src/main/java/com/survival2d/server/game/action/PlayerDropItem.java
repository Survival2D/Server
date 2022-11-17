package com.survival2d.server.game.action;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PlayerDropItem implements PlayerAction {

  String itemId;
}
