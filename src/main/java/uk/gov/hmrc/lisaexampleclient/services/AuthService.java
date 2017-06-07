/*
 * Copyright 2017 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.hmrc.lisaexampleclient.services;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import uk.gov.hmrc.lisaexampleclient.entities.OauthPair;

import java.time.LocalDateTime;

import static org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED;

@Service
public class AuthService {

    @Value("${tax.redirectUrl}")
    String redirectUrl;
    @Value("${tax.authUrl}")
    String authUrl;
    @Value("${tax.clientId}")
    String clientId;
    @Value("${tax.secret}")
    String secret;
    @Value("${tax.scope}")
    String scope;

    private OauthPair oauthPair;

    @Autowired
    @Qualifier("plainRestTemplate")
    private RestTemplate plainRestTemplate;

    public OauthPair getCurrentOauthPair() {
        return oauthPair;
    }

    public OauthPair createNewOauthPair(String authToken) throws InvalidAuthenticationCodeException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_secret", secret);
        map.add("client_id", clientId);
        map.add("grant_type", "authorization_code");
        map.add("redirect_uri", redirectUrl);
        map.add("code", authToken);

        oauthPair = fetchTokensFromHMRCAuth(map);
        return oauthPair;
    }

    public OauthPair refreshOauthPair() throws InvalidAuthenticationCodeException {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_secret", secret);
        map.add("client_id", clientId);
        map.add("grant_type", "refresh_token");
        map.add("redirect_uri", redirectUrl);
        map.add("refresh_token", oauthPair.getRefreshToken());

        oauthPair = fetchTokensFromHMRCAuth(map);
        return oauthPair;
    }

    private OauthPair fetchTokensFromHMRCAuth(MultiValueMap<String, String> parameters) throws InvalidAuthenticationCodeException {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(APPLICATION_FORM_URLENCODED);

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

            ResponseEntity<String> response = plainRestTemplate.postForEntity(
                    authUrl + "/oauth/token",
                    request,
                    String.class);

            String body = response.getBody();

            JSONParser parser = new JSONParser();
            JSONObject json = (JSONObject) parser.parse(body);

            LocalDateTime expires_in = LocalDateTime.now()
                    .plusSeconds((long) Integer.parseInt(String.valueOf(json.get("expires_in"))));
            oauthPair = new OauthPair(String.valueOf(json.get("access_token")),
                    String.valueOf(json.get("refresh_token")),
                    expires_in);

            return oauthPair;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (HttpClientErrorException e) {
            throw new InvalidAuthenticationCodeException();
        }
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }


    public void invalidateOauthPair() {
        oauthPair = null;
    }

    public boolean isOAuthPairValid() {
        return oauthPair != null && oauthPair.isValid();
    }

    public String getScope() {
        return scope;
    }
}