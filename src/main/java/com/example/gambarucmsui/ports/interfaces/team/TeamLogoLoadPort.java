package com.example.gambarucmsui.ports.interfaces.team;

import javafx.scene.image.ImageView;

import java.awt.image.BufferedImage;
import java.io.IOException;

public interface TeamLogoLoadPort {
    default BufferedImage loadTeamLogoByTeamId(Long teamId) throws IOException {
        return loadTeamLogoByTeamId(teamId, 300, 300);
    };
    BufferedImage loadTeamLogoByTeamId(Long userId, int height, int width) throws IOException;
}
