class Gun {
  type: GunId = GunId.None;
}

interface GunType {
  name: string | "";
  bulletType: BulletId[]; //loại đạn mà súng này có thể dùng
  bulletPerShoot: number; // lượng đạn mỗi lần bắn
  recoin: number; // độ giật ngang của súng, biểu diễn góc theta mà đạn có thể lệch so với đường nhắm
}

class GunType implements GunType {
  constructor(
    name = "",
    bulletType = [BulletId.Default],
    bulletPerShoot = 1,
    recoin = 0
  ) {
    this.name = name;
    this.bulletType = bulletType;
    this.bulletPerShoot = bulletPerShoot;
    this.recoin = recoin;
  }
}

enum GunId {
  None,
  Default,
}

const gunMap = new Map<GunId, GunType>();
gunMap.set(GunId.Default, new GunType());
