package com.hrm.app;

public class UserServiceImpl implements UserService {
    @Override
    public User getById(Long id) {
        User user = new User();
        user.setId((long) (Math.random() * 12000) + 1);
        user.setLogin("login" + id * 17 * (int) (Math.random() * 31));
        user.setPassword("password" + id * 17 * (int) (Math.random() * 31));
        return user;
    }
}
