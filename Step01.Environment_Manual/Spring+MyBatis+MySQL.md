
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

