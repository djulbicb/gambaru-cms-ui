package com.example.gambarucmsui.ports.impl;

import com.example.gambarucmsui.database.entity.TeamLogoEntity;
import com.example.gambarucmsui.database.repo.PersonPictureRepository;
import com.example.gambarucmsui.database.repo.TeamLogoRepository;
import com.example.gambarucmsui.database.repo.UserRepo;
import com.example.gambarucmsui.ports.interfaces.team.TeamLogoLoadPort;
import com.example.gambarucmsui.ports.interfaces.user.PersonPictureBarcodePurgePort;
import com.example.gambarucmsui.ports.interfaces.user.UserPictureLoad;
import com.example.gambarucmsui.util.DataUtil;
import com.example.gambarucmsui.util.PathUtil;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Optional;

public class ImageService implements PersonPictureBarcodePurgePort, UserPictureLoad, TeamLogoLoadPort {
    private final PersonPictureRepository personPictureRepo;
    private final TeamLogoRepository teamLogoRepository;
    private final UserRepo userService;

    public ImageService(PersonPictureRepository personPictureRepo, UserRepo personRepo, TeamLogoRepository teamLogoRepository) {
        this.personPictureRepo = personPictureRepo;
        this.userService = personRepo;
        this.teamLogoRepository = teamLogoRepository;
    }

    @Override
    public void purge() {
        personPictureRepo.deleteAll();
    }

    @Override
    public ImageView loadUserPictureByUserId(Long userId, int height, int width) {
        Optional<byte[]> picOpt = personPictureRepo.findByPersonId(userId);

        Image userPicture;
        if (picOpt.isEmpty()) {
            userPicture = DataUtil.loadImageFromResources(PathUtil.USER_NOT_FOUND);
        } else {
            userPicture = new Image(new ByteArrayInputStream(picOpt.get()));
        }

        ImageView imageView = new ImageView(userPicture);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);

        return imageView;
    }

    @Override
    public BufferedImage loadTeamLogoByTeamId(Long teamId, int height, int width) throws IOException {
        Optional<TeamLogoEntity> picOpt = teamLogoRepository.findByTeamId(teamId);

        BufferedImage userPicture;
        if (picOpt.isEmpty()) {
            userPicture = new BufferedImage(400, 400, BufferedImage.TYPE_INT_ARGB);
        } else {
            userPicture = ImageIO.read(new ByteArrayInputStream(picOpt.get().getPictureData()));
        }

        return userPicture;
    }

}

