import Presence = nkruntime.Presence;

const NoneGun = -1;

class Player {
  presence!: Presence;
  displayName: string = "";
  hp: number = DEFAULT_HEATH_POINT; //heath point
  rotation: number = 0; // hướng xoay nhân vật
  position: Position = new Position();
  guns: Gun[] = [];
  currentGun: number = NoneGun;

  constructor(presence: Presence, displayName: string = "") {
    this.presence = presence;
    this.displayName = displayName;
  }

  async updatePositionOnTick(position: Position) {}
}
