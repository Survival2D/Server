class Position extends Vector {}

class Size {
  width: number = 0;
  height: number = 0;
}

class GameMap {
  size: Size = new Size();
  playerMap: Map<String, Player> = new Map<String, Player>();
  bullets: Map<number, Bullet> = new Map<number, Bullet>();
}
