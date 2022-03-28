class Position {
  x: number = 0;
  y: number = 0;

  constructor(x: number = 0, y: number = 0) {
    this.x = x;
    this.y = y;
  }
}

class GameMap {
  playerMap: Map<String, Player> = new Map<String, Player>();
}
