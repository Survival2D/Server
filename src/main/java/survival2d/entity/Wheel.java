package survival2d.entity;

import com.tvd12.ezyfox.annotation.EzyId;
import com.tvd12.ezyfox.database.annotation.EzyCollection;
import java.util.List;
import lombok.Data;

@Data
@EzyCollection
public class Wheel {

  @EzyId
  private String id;
  private List<WheelFragment> fragments;
}
