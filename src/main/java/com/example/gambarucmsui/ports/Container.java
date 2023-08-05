package com.example.gambarucmsui.ports;

import com.example.gambarucmsui.database.repo.*;
import com.example.gambarucmsui.ports.impl.*;
import jakarta.persistence.EntityManager;

import java.util.HashSet;
import java.util.Set;

public class Container {
    private final static Set<Object> beans = new HashSet<>();

    public static void addBean(Object object) {
        beans.add(object);
    }

    public static <T> T getBean(Class<T> beanClass) {
        for (Object bean : beans) {
            if (beanClass.isInstance(bean)) {
                return beanClass.cast(bean);
            }
        }
        throw new RuntimeException(String.format("No bean with class: ", beanClass.getName()));
    }

    public static Set<Object> initBeans(EntityManager entityManager) {
        UserRepository userRepository = new UserRepository(entityManager);
        BarcodeRepository barcodeRepository = new BarcodeRepository(entityManager);
        UserAttendanceRepository userAttendanceRepository = new UserAttendanceRepository(entityManager);
        UserMembershipRepository userMembershipRepository = new UserMembershipRepository(entityManager);
        TeamRepository teamRepository = new TeamRepository(entityManager);
        UserPictureRepository userPictureRepository = new UserPictureRepository(entityManager);

        Container.addBean(new ImageService());

        Container.addBean(new UserServiceSave(barcodeRepository, teamRepository, userRepository, userAttendanceRepository, userPictureRepository));
        Container.addBean(new TeamServiceSaveIf(teamRepository));
        Container.addBean(new AttendanceService(barcodeRepository, userAttendanceRepository, userMembershipRepository));
        Container.addBean(new BarcodeService(barcodeRepository));
        Container.addBean(new StatisticsService(userAttendanceRepository, userMembershipRepository));
        Container.addBean(new MembershipService(barcodeRepository, userAttendanceRepository, userMembershipRepository));

        return beans;
    }
}
