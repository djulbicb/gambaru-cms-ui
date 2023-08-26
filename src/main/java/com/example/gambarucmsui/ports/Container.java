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
        throw new RuntimeException("No bean with class: " + beanClass.getSimpleName());
    }

    public static Set<Object> initBeans(EntityManager entityManager) {
        UserRepo userRepo = new UserRepo(entityManager);
        BarcodeRepository barcodeRepository = new BarcodeRepository(entityManager);
        UserAttendanceRepository userAttendanceRepository = new UserAttendanceRepository(entityManager);
        TeamRepository teamRepository = new TeamRepository(entityManager);
        UserPictureRepository userPictureRepository = new UserPictureRepository(entityManager);
        PersonPictureRepository personPictureRepository = new PersonPictureRepository(entityManager);
        SubscriptionRepository subscriptionRepository = new SubscriptionRepository(entityManager);
        SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository, barcodeRepository);

        Container.addBean(new ImageService(personPictureRepository, userRepo));
        Container.addBean(subscriptionService);
        Container.addBean(new UserService(barcodeRepository, teamRepository, userRepo, userAttendanceRepository, userPictureRepository, subscriptionService));
        Container.addBean(new TeamService(teamRepository, barcodeRepository));
        Container.addBean(new AttendanceService(barcodeRepository, userAttendanceRepository));
        Container.addBean(new BarcodeService(barcodeRepository));

        return beans;
    }
}
