package survival2d.match.entity;

public abstract class Weapon {


  public abstract WeaponType getWeaponType();

  public AttachType getAttachType() {
    return getWeaponType().getAttachType();
  }
}
