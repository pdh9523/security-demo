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
password: 

jpa:
show-sql: true
hibernate:
ddl-auto: update
properties:
show-sql: true
format-sql: true

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
```