import Presence = nkruntime.Presence;

class Player {
  presence!: Presence;
  displayName: string = "";
  hp: number = DEFAULT_HEATH_POINT; //heath point
  rotation: number = 0; // hướng xoay nhân vật
  position: Position = new Position();

  constructor(presence: Presence, displayName: string = "") {
    this.presence = presence;
    this.displayName = displayName;
  }
}
