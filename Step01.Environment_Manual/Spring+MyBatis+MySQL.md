
# Spring+MyBatis+MySQL 연결해두기

##  1. MySQL 준비

1. MySQL 설치는 [부스트코스에서의 매뉴얼](https://www.boostcourse.org/web326/lecture/58931?isDesc=false)을 참고하자
2. MySQL 서비스를 시작하고, 워크벤치에서 스키마를 생성해주자[나중에 url 작성에 필요 ★&& 스프링부트에서는 콘솔에서 할 수 있던 작업!]
```sql
CREATE SCHEMA `BOOK_EX` DEFAULT CHARACTER SET utf8;
```
3. MySQL Command Line Client를 실행해서 설정을 확인해주자


## 2. MySQL의 JDBC 연결

1. MySQL의 Connector/J 라이브러리[C:\Program Files (x86)\MySQL\Connector J 8.0] 에서 자동다운로드된 부분을 확인가능
- 수동 다운로드: http://dev.mysql.com/downloads/connector/j/

2. maven repository에서 jar 연결 링크를 pom.xml에 붙여넣어주기! https://mvnrepository.com/artifact/mysql/mysql-connector-java/8.0.16

## 3. MySQL 의 JDBC 연결 확인

✔ try-with 구문

- JDK 1.7부터 지원
- try블럭에 AutoCloseable 인터페이스를 구현한 타입의 변수가 와야 함
- try-catch-finally를 대체 가능

```java
try(AutoCloseable 인터페이스를 구현한 타입의 변수){
}catch(Exception e){
//예외처리
}
```

1. src/test/java 하위에 MySQLConnectionTest.java 작성
```java
package com.test.jisoo;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.Test;

public class MySQLConnectionTest {

	private static final String DRIVER   ="com.mysql.cj.jdbc.Driver";
	private static final String URL      ="jdbc:mysql://localhost:3306/BOOK_EX?serverTimezone=Asia/Seoul";
	private static final String USER     ="root";
	private static final String PASSWORD ="default is root";
	private static Connection connection = null;
	
	public void getConnection() {
		try {
			//드라이버 탐색
			Class.forName(DRIVER);
			//url,user,pw로 jdbc 연결
			connection=DriverManager.getConnection(URL, USER, PASSWORD);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testConnection() {
		getConnection();//jdbc 드라이버 연결
		
		System.out.println("connection: "+connection);
	}
}
```

Run as-JUnit Test를 선택해주자!

★ `The server time zone value '????α? ????' is unrecognized or represents more than one time zone. You must configure either the server or JDBC driver (via the serverTimezone configuration property) to use a more specifc time zone value if you want to utilize time zone support.` 
이런 문구가 보이면, 부트에서는 연결할때 설정창에서 serverTimezone을 Asia/Seoul로 해주었던 것을 쿼리스트링처럼 붙여주자
`jdbc:mysql://localhost:3306/BOOK_EX?serverTimezone=Asia/Seoul`
https://www.lesstif.com/dbms/mysql-jdbc-the-server-time-zone-value-kst-is-unrecognized-or-represents-more-than-one-time-zone-100204548.html


그리고! `com.mysql.jdbc.Driver`는 deprecated 되었으므로 `com.mysql.cj.jdbc.Driver` 로 연결하여야 한다!(콘솔에 친절한 안내문구가 뜬다)

```
connection: com.mysql.cj.jdbc.ConnectionImpl@491b9b8
```
완성!!

## 04. 마이바티스의 편리함

MyBatis: SQL Mapper 라이브러리

- 간결한 코드 처리
- SQL문 분리운영(XML/어노테이션 방식)
- 스프링과의 연동으로 자동화된 처리
- 동적 SQL을 활용한 제어기능(제어문, 루프 등)

## 05. Spring+MyBatis+MySQL

### 05-1. pom.xml
다음과 같은 의존성을 추가
maven repository에 취약성 표시가 떠있다! 저번에는 없었는데!!

1. MyBatis
```
<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis</artifactId>
    <version>3.5.6</version>
</dependency>
```

2. MyBatis-Spring
```
<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis-spring -->
<dependency>
    <groupId>org.mybatis</groupId>
    <artifactId>mybatis-spring</artifactId>
    <version>1.3.2</version>
</dependency>

```

3. spring-jdbc

```
<dependency>
    		<groupId>org.springframework</groupId>
    		<artifactId>spring-jdbc</artifactId>
    		<version>${org.springframework-version}</version>
		</dependency>
```

4. spring-test

```
<dependency>
    		<groupId>org.springframework</groupId>
    		<artifactId>spring-test</artifactId>
    		<version>${org.springframework-version}</version>
		</dependency>  
```

### 05-2. root-context.xml

1. `src/main/webapp/WEB-INF/spring/root-context.xml` 을 편의상
`src/main/webapp/WEB-INF/spring/appServlet/applicationContext.xml` 로 변경

2. `src/main/webapp/WEB-INF/web.xml` 에서 root-context.xml 설정부분을 아래와 같이 변경

```xml
<?xml version="1.0" encoding="UTF-8"?>
<web-app version="2.5" xmlns="http://java.sun.com/xml/ns/javaee"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://java.sun.com/xml/ns/javaee https://java.sun.com/xml/ns/javaee/web-app_2_5.xsd">

	<!-- The definition of the Root Spring Container shared by all Servlets and Filters -->
	<context-param>
		<param-name>contextConfigLocation</param-name>
		<param-value>/WEB-INF/spring/appServlet/applicationContext.xml</param-value>
	</context-param>
```

3. applicationContext.xml에서 aop, context, jdbc, mybatis-spring 네임스페이스 추가(namespaces 탭에서 선택)

4. ❤db 연결을 위한 준비❤

[방법1]properties 파일 준비

1. `src/main/resources/mybatis/db.properties` 준비

```properties
driverClassName=com.mysql.cj.jdbc.Driver
url=jdbc:mysql://localhost:3306/BOOK_EX?serverTimezone=Asia/Seoul
username=root
password=jisoo
```

2. `src/main/webapp/WEB-INF/spring/appServlet/applicationContext.xml` 에서 properties를 불러오기

```xml
		<!-- db.properties 불러오기 -->
		<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
			<property name="locations">
			 	<list>
			 		<value>classpath:mybatis\db.properties</value>
			 	</list>
			</property>
		</bean>
```

3. applicationContext.xml에서 dataSource 빈 만들기

```xml
		<!-- dataSource -->
		<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
			<property name="driverClassName" value="${driverClassName}"></property>			
			<property name="url" value="${url}"></property>			
			<property name="username" value="${username}"></property>			
			<property name="password" value="${password}"></property>			
		</bean>
		
		
```

4. dataSource 연결 확인
❤ datasource.getConnection() 시
`Resource specification not allowed here for source level below 1.7` 가 뜨는데, 컴파일 환경을  pom.xml에서 변경해줘야 한다! 아마 1.7보다 낮은 값이 있을 텐데, 1.8로 변경해주자
```xml
 <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>2.5.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <compilerArgument>-Xlint:all</compilerArgument>
                    <showWarnings>true</showWarnings>
                    <showDeprecation>true</showDeprecation>
                </configuration>
            </plugin>

```
```java
package com.test.jisoo;

import java.sql.Connection;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations= {"file:src/main/webapp/WEB-INF/spring/**/*.xml"}
)
public class DataSourceTest {
	
	@Inject
	private DataSource ds;
	
	@Test
	public void testConnection() {
		
		try(Connection conn=DataSourceUtils.getConnection(ds)){
			System.out.println("connection-datasource: "+conn);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}

```

```
(중략)
connection-datasource: com.mysql.cj.jdbc.ConnectionImpl@c9d82f9
(중략)
```
연결 확인 완료!!

5. SqlSession과 SqlSessionTemplate 만들기
```xml
<!-- mybatis
		sqlsession -->
		<bean id="sqlSession" class="org.mybatis.spring.SqlSessionFactoryBean">
			<property name="dataSource" ref="dataSource"></property>
			<property name="configLocation" value="classpath:\sqls\config.xml"></property>
		</bean>
		
		<!-- sqlSessionTemplate -->
		<bean id="sqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
			<constructor-arg ref="sqlSession"></constructor-arg>
		</bean>
```

[방법2] properties 파일 없이 진행

```xml
		<!-- dataSource -->
		<bean id="dataSource" class="org.springframework.jdbc.datasource.DriverManagerDataSource">
			<property name="driverClassName" value="com.mysql.cj.jdbc.Driver"></property>			
			<property name="url" value="jdbc:mysql://localhost:3306/BOOK_EX?serverTimezone=Asia/Seoul"></property>			
			<property name="username" value="root"></property>			
			<property name="password" value="jisoo"></property>			
		</bean>
		
		<!-- mybatis
		sqlsession -->
		<bean id="sqlSession" class="org.mybatis.spring.SqlSessionFactoryBean">
			<property name="dataSource" ref="dataSource"></property>
			<property name="configLocation" value="classpath:\sqls\config.xml"></property>
		</bean>
		
		<!-- sqlSessionTemplate -->
		<bean id="sqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
			<constructor-arg ref="sqlSession"></constructor-arg>
		</bean>
```


### 05-3. config.xml 준비

1. `src/main/resources/sqls/config.xml` 준비

2. https://mybatis.org/mybatis-3/getting-started.html
에서 `Building SqlSessionFactory from XML`  중에서도

```xml
<?xml version="1.0" encoding="UTF-8"  ?>  <!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
```
가져오기!

3. `<configuration></configuration>` 사이에
(1) 별칭 : 계속 com.xx.xx로 사용하기에는 번거로움
(2) mapper 파일 위치(src/main/resources/mybatis/~.xml)

를 미리 알려주기
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
  	<!-- 별칭 -->
  	<typeAliases>
  		<typeAlias type="" alias=""/>
  	</typeAliases>
  	
  	<!-- mapper파일 위치 -->
  	<mappers>
  		<mapper resource="/mybatis/.xml"/>
  	</mappers>
  </configuration>
```

### 05-5. SqlSession 테스트

```java
package com.test.jisoo;

import javax.inject.Inject;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations= {"file:src/main/webapp/WEB-INF/spring/**/*.xml"}
)
public class MyBatisTest {
	
	@Inject
	private SqlSessionFactory factory;
	
	@Test
	public void testFactory() {
		System.out.println("factory: "+factory);
	}
	
	@Test
	public void testSession() {
		
		try(SqlSession session=factory.openSession()){
			System.out.println("session: "+session);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}

```


```
(중략)
factory: org.apache.ibatis.session.defaults.DefaultSqlSessionFactory@51e37590
session: org.apache.ibatis.session.defaults.DefaultSqlSession@45673f68
(중략)
```

