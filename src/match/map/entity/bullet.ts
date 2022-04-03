class Bullet {
  position: Position = new Position();
  type: BulletId = BulletId.Default;
  velocity: number = 1;
  range: number = 1;
  direction: Vector = new Vector();
}

//loại đạn, đạn 5-7-9
interface BulletType {
  name: string;
  damage: number;
}

enum BulletId {
  Default,
}

const bulletMap = new Map<BulletId, BulletType>();
bulletMap.set(BulletId.Default, {
  name: "Default",
  damage: 1,
});
