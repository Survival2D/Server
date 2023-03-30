package survival2d;

import lombok.extern.slf4j.Slf4j;
import survival2d.network.NetworkStartup;

@Slf4j
public class Survival2D {

  public static void main(String[] args) throws Exception {
    log.trace("Begin init server");
    NetworkStartup.setup();
    log.trace("Complete init server!");
  }
}
