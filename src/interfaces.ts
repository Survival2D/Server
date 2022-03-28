interface MatchLabel {
  open: boolean;
}

interface GameState {
  players: Player[];
  playersWins: number[];
  roundDeclaredWins: number[][];
  roundDeclaredDraw: number[];
  scene: Scene;
  countdown: number;
  endMatch: boolean;
  map: GameMap;
}

interface TimeRemainingData {
  time: number;
}

interface PlayerWonData {
  tick: number;
  playerNumber: number;
}

interface DrawData {
  tick: number;
}

interface TrophiesData {
  amount: number;
}
