package com.survival2d.server.response;

import com.tvd12.ezyfox.binding.annotation.EzyObjectBinding;
import java.util.List;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@EzyObjectBinding
public class JoinLobbyResponse {
  long lobbyRoomId;
}
