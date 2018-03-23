package com.se.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.se.jsonmodel.LoginRequest;
import com.se.jsonmodel.LoginReturn;
import com.se.jsonmodel.Message;
import com.se.jsonmodel.RegisterRequest;
import com.se.mapper.UserRepository;
import com.se.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by takahiro on 2017/7/2.
 */
@Controller
public class LoginController {
    @Autowired
    private UserRepository userRepository;

    private ObjectMapper objectMapper = new ObjectMapper();

    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(){
        return "sign";
    }

    @RequestMapping(value = "/register",method = RequestMethod.GET)
    public String register(){
        return "register";
    }

    @RequestMapping(value = "/register_auth",method = RequestMethod.POST)
    @ResponseBody
    public String register_auth(@RequestBody String body) throws IOException{
        System.out.println(body);
        RegisterRequest obj;
        Message<User> message = new Message<User>();
        try {
            obj = objectMapper.readValue(body, RegisterRequest.class);
            System.out.println("成功解析上传的json");
        } catch (IOException e) {
            System.out.println("不能解析json");
            message.setError("不能解析上传的json");
            return objectMapper.writeValueAsString(message);
        }
        String username = obj.getUsername();
        String password = obj.getPassword();
        String password2 = obj.getPassword2();
        if(userRepository.findByUsername(username)!=null) {
            message.setError("this username has exists");
            return objectMapper.writeValueAsString(message);
        }
        if(!password.equals(password2)) {
            message.setError("two passwords are not the same");
            return objectMapper.writeValueAsString(message);
        }
        User user = new User(username,password);
        userRepository.save(user);
        user = userRepository.findByUsername(username);
        message.setStatus(1);
        message.setData(user);
        return objectMapper.writeValueAsString(message);
    }

    @RequestMapping(value = "/login_auth",method = RequestMethod.POST)
    @ResponseBody
    public String login_auth(@RequestBody String body) throws JsonProcessingException{
        System.out.println(body);
        Message<LoginReturn> message = new Message();
        LoginRequest obj;
        try {
            obj = objectMapper.readValue(body, LoginRequest.class);
            System.out.println("成功解析上传的json");
        } catch (IOException e) {
            System.out.println("不能解析json");
            message.setError("不能解析上传的json");
            return objectMapper.writeValueAsString(message);
        }
        String username = obj.getUsername();
        String password = obj.getPassword();
        User user = userRepository.findByUsernameAndPassword(username,password);
        if(user==null) {
            message.setError("no such user or password error");
            return objectMapper.writeValueAsString(message);
        }
        LoginReturn loginReturn = new LoginReturn();
        loginReturn.setUserId(user.getId());
        loginReturn.setUserName(user.getUsername());
        message.setStatus(1);
        message.setData(loginReturn);
        return objectMapper.writeValueAsString(message);
    }

}
