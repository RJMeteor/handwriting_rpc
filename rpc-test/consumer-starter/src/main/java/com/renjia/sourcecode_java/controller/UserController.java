package com.renjia.sourcecode_java.controller;

import com.renjia.sourcecode_java.fetch.User;
import com.renjia.sourcecode_java.fetch.UserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("user")
public class UserController {

    @Resource
    private UserService userService;

    @RequestMapping("login")
    public String user() {

        return userService.login();
    }

    @RequestMapping("name")
    public String name() {

        return userService.get("renjia", "18");
    }

    @RequestMapping("go")
    public User go() {
        return userService.go("renjia", 19);
    }

    @RequestMapping("post")
    public User post() {
        User user = new User();
        user.setUserName("post");
        user.setAge(18);
        User user1 = new User();
        user1.setUserName("post1111111111111");
        user1.setAge(1118);
        return userService.post(user, user1);
    }
}
