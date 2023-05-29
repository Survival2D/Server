package survival2d.ai.bot.leaf;

import survival2d.ai.bot.BotBehaviorNode;
import survival2d.match.type.GunType;

public class HaveEnoughBulletNode extends BotBehaviorNode {
  @Override
  public void processNode() {
    double percent = this.controller.getConfidencePercent();
    int numBullet = 0;
    for (var gunType: GunType.values()) {
      numBullet += this.controller.getPlayerInfo().getNumBullet(gunType);
      var gun = this.controller.getPlayerInfo().getGun(gunType);
      var numBulletInGun = gun.getRemainBullets();
      numBullet += numBulletInGun;
    }
    int numBulletsNeed = (int) (1 - percent) * 100;
    if (numBullet > numBulletsNeed) success();
    else fail();

    success();
  }
}
