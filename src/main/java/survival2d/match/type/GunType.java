package survival2d.match.type;

public enum GunType {
  PISTOL,
  SHOTGUN,
  SNIPER;

  public byte toFbsGunType() {
    return switch (this) {
      case PISTOL -> survival2d.flatbuffers.GunTypeEnum.PISTOL;
      case SHOTGUN -> survival2d.flatbuffers.GunTypeEnum.SHOTGUN;
      case SNIPER -> survival2d.flatbuffers.GunTypeEnum.SNIPER;
    };
  }

  public static GunType parse(int index) {
    if (index < 0 || index >= values().length)
      return null;
    return values()[index];
  }
}
