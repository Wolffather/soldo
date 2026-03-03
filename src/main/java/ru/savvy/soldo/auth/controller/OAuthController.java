package ru.savvy.soldo.auth.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.savvy.soldo.auth.dto.TokenResponse;
import ru.savvy.soldo.user.model.User;
import ru.savvy.soldo.user.model.AuthProviderType;
import ru.savvy.soldo.shared.security.JwtTokenProvider;
import ru.savvy.soldo.user.service.UserService;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/auth/oauth")
@RequiredArgsConstructor
public class OAuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final RestTemplate restTemplate;

    @Value("${vk.client.id}")
    private String vkClientId;

    @Value("${vk.client.secret}")
    private String vkClientSecret;

    @Value("${yandex.client.id}")
    private String yandexClientId;

    @Value("${yandex.client.secret}")
    private String yandexClientSecret;

    // ─── VK ────────────────────────────────────────────────────────────────────

    @PostMapping("/vk")
    public ResponseEntity<TokenResponse> vkAuth(
            @RequestParam String code,
            @RequestParam String redirectUri) {

        // 1. Exchange code → VK access token (VK uses GET for token exchange)
        String tokenUrl = "https://oauth.vk.com/access_token"
                + "?client_id=" + vkClientId
                + "&client_secret=" + vkClientSecret
                + "&redirect_uri=" + redirectUri
                + "&code=" + code;

        VkTokenResponse vkToken = restTemplate.getForObject(tokenUrl, VkTokenResponse.class);

        if (vkToken == null || vkToken.getAccessToken() == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

        // 2. Get VK user profile
        String userUrl = "https://api.vk.com/method/users.get"
                + "?user_ids=" + vkToken.getUserId()
                + "&fields=screen_name,first_name,last_name"
                + "&access_token=" + vkToken.getAccessToken()
                + "&v=5.199";

        VkUsersResponse usersResponse = restTemplate.getForObject(userUrl, VkUsersResponse.class);

        if (usersResponse == null || usersResponse.getResponse() == null
                || usersResponse.getResponse().isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

        VkUserInfo vkUser = usersResponse.getResponse().get(0);

        // 3. Find or create user
        User user = userService.findOrCreateByOAuth(
                AuthProviderType.VK,
                String.valueOf(vkToken.getUserId()),
                vkUser.getFirstName(),
                vkUser.getLastName(),
                vkUser.getScreenName()
        );

        // 4. Return JWT
        String jwt = jwtTokenProvider.generateToken(user.getId().toString(), user.getRole());
        return ResponseEntity.ok(new TokenResponse(jwt, user.getRole(), user.getId()));
    }

    // ─── Yandex ────────────────────────────────────────────────────────────────

    @PostMapping("/yandex")
    public ResponseEntity<TokenResponse> yandexAuth(
            @RequestParam String code,
            @RequestParam String redirectUri) {

        // 1. Exchange code → Yandex access token (POST with Basic auth)
        String credentials = Base64.getEncoder()
                .encodeToString((yandexClientId + ":" + yandexClientSecret).getBytes());

        HttpHeaders tokenHeaders = new HttpHeaders();
        tokenHeaders.set("Authorization", "Basic " + credentials);
        tokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> tokenBody = new LinkedMultiValueMap<>();
        tokenBody.add("grant_type", "authorization_code");
        tokenBody.add("code", code);
        tokenBody.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> tokenRequest =
                new HttpEntity<>(tokenBody, tokenHeaders);

        YandexTokenResponse yToken = restTemplate.postForObject(
                "https://oauth.yandex.ru/token", tokenRequest, YandexTokenResponse.class);

        if (yToken == null || yToken.getAccessToken() == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

        // 2. Get Yandex user info
        HttpHeaders infoHeaders = new HttpHeaders();
        infoHeaders.set("Authorization", "OAuth " + yToken.getAccessToken());
        HttpEntity<?> infoRequest = new HttpEntity<>(infoHeaders);

        ResponseEntity<YandexUserInfo> infoResponse = restTemplate.exchange(
                "https://login.yandex.ru/info?format=json",
                HttpMethod.GET,
                infoRequest,
                YandexUserInfo.class);

        YandexUserInfo yUser = infoResponse.getBody();
        if (yUser == null || yUser.getId() == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).build();
        }

        // 3. Find or create user
        User user = userService.findOrCreateByOAuth(
                AuthProviderType.YANDEX,
                yUser.getId(),
                yUser.getFirstName(),
                yUser.getLastName(),
                yUser.getLogin()
        );

        // 4. Return JWT
        String jwt = jwtTokenProvider.generateToken(user.getId().toString(), user.getRole());
        return ResponseEntity.ok(new TokenResponse(jwt, user.getRole(), user.getId()));
    }

    // ─── Inner DTOs ────────────────────────────────────────────────────────────

    @Data
    static class VkTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("user_id")
        private Long userId;
        private String error;
        @JsonProperty("error_description")
        private String errorDescription;
    }

    @Data
    static class VkUsersResponse {
        private List<VkUserInfo> response;
    }

    @Data
    static class VkUserInfo {
        private Long id;
        @JsonProperty("first_name")
        private String firstName;
        @JsonProperty("last_name")
        private String lastName;
        @JsonProperty("screen_name")
        private String screenName;
    }

    @Data
    static class YandexTokenResponse {
        @JsonProperty("access_token")
        private String accessToken;
        @JsonProperty("token_type")
        private String tokenType;
        private String error;
    }

    @Data
    static class YandexUserInfo {
        private String id;
        private String login;
        @JsonProperty("first_name")
        private String firstName;
        @JsonProperty("last_name")
        private String lastName;
        private String[] emails;
    }
}
