interface PlayerAndPosition {
  userID: string;
  position: Position;
}

interface JoinData extends PlayerAndPosition {}

interface PlayerInMatchData {
  players: PlayerAndPosition[];
}
