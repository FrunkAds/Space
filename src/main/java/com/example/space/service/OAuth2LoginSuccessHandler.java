package com.example.space.service;

import com.example.space.models.AuthenticationProvider;
import com.example.space.models.CustomOAuth2User;
import com.example.space.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Autowired
    private UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        CustomOAuth2User user = (CustomOAuth2User) authentication.getPrincipal() ;

        String email = user.getEmail();
        String name = user.getFullName();
        String lastName = user.getFamilyName();
        String firstName = user.getGivenName();

        User newUser = userService.getUserByEmail(email);

        if (newUser == null) {
            userService.createNewUserAfterOAuthLoginSuccess(email,name,lastName,firstName,
                    AuthenticationProvider.GOOGLE);
        }else {
            userService.updateUserAfterOAuthLoginSuccess(newUser,name,
                    AuthenticationProvider.GOOGLE);
        }

        response.sendRedirect("/user/home");

    }
}
