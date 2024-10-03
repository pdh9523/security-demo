```
#application.yml

server:
  port: 8080

spring:
  application:
    name: security-demo

  datasource:
    url: 
    username: 

  jpa:
    show-sql: true
    hibernate:
      ddl-auto: update
#    properties:
#      show-sql: true
#      format-sql: true

  data:
    redis:
      host: localhost
      port: 6379

  mail:
    host: 
    port: 
    username: 
    password: 

  jwt:
    # oepnssl rand -base64 64
    salt: 

#logging:
#  level:
#    org:
#      hibernate:
#        descriptor:
#          sql: trace


  security:
    oauth2:
      client:
        registration:
          naver:
            client-id: 
            client-secret: 
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            redirect-uri: "{baseUrl}/api/{action}/oauth2/code/{registrationId}"
            scope:
              - name
              - email
              - nickname
            client-name: Naver

          kakao:
            client-id: 
            client-secret: 
            client-authentication-method: client_secret_post
            authorization-grant-type: authorization_code
            scope: # https://developers.kakao.com/docs/latest/ko/kakaologin/common#user-info
              - profile_nickname
              - account_email
            redirect-uri: "{baseUrl}/api/{action}/oauth2/code/{registrationId}"
            client-name: Kakao

        provider:
          naver:
            authorization-uri: https://nid.naver.com/oauth2.0/authorize
            token-uri: https://nid.naver.com/oauth2.0/token
            user-info-uri: https://openapi.naver.com/v1/nid/me
            user-info-authentication-method: header
            user-name-attribute: response # Naver 응답 값 resultCode, message, response 중 response 지정

          kakao:
            authorization-uri: https://kauth.kakao.com/oauth/authorize
            token-uri: https://kauth.kakao.com/oauth/token
            user-info-uri: https://kapi.kakao.com/v2/user/me
            user-info-authentication-method: header
            user-name-attribute: id # Kakao 응답 값 id, connected_at, properties, kakao_account 중 id 지정

```