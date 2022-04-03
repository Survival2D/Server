class Match {
  id: string;
  map: GameMap;
  constructor(id: string) {
    this.id = id;
    this.map = new GameMap();
  }
}
