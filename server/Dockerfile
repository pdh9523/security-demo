FROM openjdk:17-jdk
LABEL maintainer="jinee9523@gmail.com"

# ./gradlew build
# JAR_FILE의 경로를 server 디렉토리 기준으로 수정
ARG JAR_FILE=build/libs/server-0.0.1-SNAPSHOT.jar

# JAR 파일을 컨테이너에 복사
ADD ${JAR_FILE} server-springboot.jar

# 실행
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom","-jar","server-springboot.jar"]