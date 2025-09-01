FROM tomcat:10.1-jdk21-openjdk-slim
LABEL maintainer="Banco de Chile <[email del TL]>" \
      version="1.0.0" \
      description="Microservicio Seguros Empresas"
WORKDIR /usr/local/tomcat
COPY target/seguros-empresas.war webapps/
COPY docker/context.xml conf/
COPY docker/server.xml conf/
COPY docker/Wallet_BD01 /u/Wallet_BD01
ENV SPRING_PROFILES_ACTIVE=prod \
    TNS_ADMIN=/u/Wallet_BD01 \
    CATALINA_OPTS="-Xms512m -Xmx1024m --add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED --add-opens=java.base/java.util=ALL-UNNAMED --add-opens=java.base/java.util.concurrent=ALL-UNNAMED --add-opens=java.rmi/sun.rmi.transport=ALL-UNNAMED"
EXPOSE 8080
CMD ["catalina.sh", "run"]