FROM openjdk:17-jdk

# UTF-8 로케일 설정 (로케일 패키지 설치 후 적용)
#RUN apt-get update && apt-get install -y locales \
#    && locale-gen ko_KR.UTF-8 \
#    && update-locale LANG=ko_KR.UTF-8

# UTF-8 로케일 설정
#ENV LANG=ko_KR.UTF-8
#ENV LANGUAGE=ko_KR:ko
#ENV LC_ALL=ko_KR.UTF-8

WORKDIR /app

# 애플리케이션 복사 및 실행 설정
COPY build/libs/*SNAPSHOT.jar lipit.jar

ENTRYPOINT ["java", "-jar", "lipit.jar"]