package com.renjia.sourcecode_java.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("renjia")
public class UserController {

    @RequestMapping("name")
    public String name() {
        return "任家";
    }

    @RequestMapping("get")
    public String get(@RequestParam("userName") String userName, @RequestParam("age") String age) {
        return userName + age;
    }

    @RequestMapping("go")
    public User go(@RequestParam("userName") String userName, @RequestParam("age") Integer age) {
        User user = new User();
        user.setUserName(userName);
        user.setAge(age);
        return user;
    }

    @RequestMapping(value = "post",method = RequestMethod.POST)
    public User post(@RequestBody Parent parent) {
        parent.getUser1().setUserName("appfafsafdfda");
        parent.getUser().setUserName("fffffffffffffffff");
        return parent.getUser();
    }
}
