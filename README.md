# 시큐리티 설정 데모


### Trouble Shooting
1. 백엔드와 프론트 엔드의 역할 분담 문제
    
    현재 백엔드에서 너무 많은 부분을 담당하고 있음. 리프레시 관련 동작 또한 백엔드에서 임의로 진행하고 있기 때문에 이를 프론트가 해야할 일과 백엔드가 해야할 일을 구분하는 작업이 필요해보임.

2. accessToken과 refreshToken의 저장 위치 문제

    현재 쿠키 저장소에 accessToken과 refreshToken이 동시에 들어가있고, 이를 withCredential을 통해 매 요청 시 모두 담아 보내고 있음.
    refreshToken 탈취 위험을 줄이기 위해 accessToken만 Authorization header 에 담아 보내고, 리프레시 기능을 백엔드에서 프론트엔드로 이관해 리프레시하는 경우에만 refreshToken을 사용해야 안전하다고 생각함.



#### 현재 진행 상황

백엔드

    일반적인 react, vue같은 csr 프레임워크로 만들었다면 그대로 사용해도 될 것음. 
    하지만 목적이 next-auth의 적용이기 때문에, 새로운 버전으로 따로 만들 예정

프론트 엔드

    next-auth 진행중
    spring-security를 next-auth와 함께 사용하는 경우를 상정하지 않아서 잠시 보류