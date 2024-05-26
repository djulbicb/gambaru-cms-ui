package com.example.gambarucmsui.ports;

import com.example.gambarucmsui.database.entity.PersonMembershipEntity;
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
        PersonRepository personRepository = new PersonRepository(entityManager);
        BarcodeRepository barcodeRepository = new BarcodeRepository(entityManager);
        PersonAttendanceRepository personAttendanceRepository = new PersonAttendanceRepository(entityManager);
        TeamRepository teamRepository = new TeamRepository(entityManager);
        UserPictureRepository userPictureRepository = new UserPictureRepository(entityManager);
        TeamLogoRepository teamLogoRepository = new TeamLogoRepository(entityManager);
        PersonPictureRepository personPictureRepository = new PersonPictureRepository(entityManager);
        PersonMembershipRepository personMembershipRepository = new PersonMembershipRepository(entityManager);
        SubscriptionRepository subscriptionRepository = new SubscriptionRepository(entityManager);


        SubscriptionService subscriptionService = new SubscriptionService(subscriptionRepository, barcodeRepository);
        MembershipService membershipService = new MembershipService(barcodeRepository, personMembershipRepository);

        Container.addBean(new ImageService(personPictureRepository, personRepository, teamLogoRepository));
        Container.addBean(subscriptionService);
        Container.addBean(new UserService(barcodeRepository, teamRepository, personRepository, personAttendanceRepository, userPictureRepository, subscriptionService));
        Container.addBean(new TeamService(teamRepository, teamLogoRepository, barcodeRepository));
        Container.addBean(new AttendanceService(barcodeRepository, personAttendanceRepository));
        Container.addBean(new BarcodeService(barcodeRepository));
        Container.addBean(subscriptionService);
        Container.addBean(membershipService);

        return beans;
    }
}
