package com.shitao.community.controller;

import com.shitao.community.dto.AccessTokenDTO;
import com.shitao.community.dto.GithubUser;
import com.shitao.community.mapper.UserMapper;
import com.shitao.community.model.User;
import com.shitao.community.provider.GithubProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

@Controller
public class AuthController {

    @Autowired
    private GithubProvider githubProvider;

    @Value("${github.clinnt_id}")
    private String clientId;

    @Value("${github.Redirect_uri}")
    private String redirectURL;

    @Value("${github.Client_secret}")
    private String clientSecret;

    @Autowired
    private UserMapper userMapper;

    @GetMapping("/callback")
    public String callback(
            @RequestParam("code") String code,
            @RequestParam("state") String state,
            HttpServletRequest request){
        AccessTokenDTO accessTokenDTO = new AccessTokenDTO();
        accessTokenDTO.setCode(code);
        accessTokenDTO.setClient_id(clientId);
        accessTokenDTO.setRedirect_uri(redirectURL);
        accessTokenDTO.setState(state);
        accessTokenDTO.setClient_secret(clientSecret);

        String accessToken=githubProvider.getAccessToken(accessTokenDTO);
        GithubUser githubUser=githubProvider.getUser(accessToken);
//        System.out.println(githubUser.getName());
        if(githubUser!=null){
            User user = new User();
            user.setAccountId(String.valueOf(githubUser.getId()));
            user.setGmtCreate(System.currentTimeMillis());
            user.setGmtModified(user.getGmtCreate());
            user.setName(githubUser.getName());
            user.setToken(UUID.randomUUID().toString());
            userMapper.insert(user);
            request.getSession().setAttribute("user",githubUser);
            return "redirect:/";
        }else {
            return "redirect:/";
        }
    }
}
