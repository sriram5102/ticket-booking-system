package com.cloudbees.interview.ticket.booking.service;

import com.cloudbees.interview.ticket.booking.dao.UserDao;
import com.cloudbees.interview.ticket.booking.domain.User;
import com.cloudbees.interview.ticket.booking.repository.IUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {

    @Autowired
    IUserRepository userRepository;

    public User save(User user){
        UserDao userDao = convertDomainToDao(user);
        userDao = userRepository.save(userDao);
        user = convertDaoToDomain(userDao);
        return user;
    }

    public UserDao convertDomainToDao(User user) {
        UserDao userDao = new UserDao();
        userDao.setFirstName(user.getFirstName());
        userDao.setLastName(user.getLastName());
        userDao.setEmail(user.getEmail());
        return userDao;
    }

    private User convertDaoToDomain(UserDao userDao) {
        User user = new User();
        user.setId(userDao.getUserId());
        user.setFirstName(userDao.getFirstName());
        user.setLastName(userDao.getLastName());
        user.setEmail(userDao.getEmail());
        return user;
    }
}
