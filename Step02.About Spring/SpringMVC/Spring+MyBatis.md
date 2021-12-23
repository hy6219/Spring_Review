
# Spring + MyBatis

## 01. MyBatis SQL문 사용 방식

1. XML만을 이용해서 SQL문을 설정하고, DAO에서 XML을 찾아서 실행하는 코드를 작성 ▶ 국내에서 자주 사용하는 방식!

- 장점: SQL문 수정 및 유지보수에 적합
- 단점: 개발 시 코드양이 많아지고, 복잡성이 증가


2. 애노테이션과 인터페이스만을 이용해서 SQL문을 설정

- 장점: 별도의 DAO없이도 개발이 가능하기 때문에 생산성 증가
- 단점: SQL문을 애노테이션으로 작성하기 때문에 매번 수정이 일어나는 경우 다시 컴파일

3. 인터페이스+XML

- 장점: 상황에 따른 유연한 처리
- 단점: 개발방식의 차이로 인해서 유지보수가 중요한 프로젝트의 경우 부적합


## 02. XML 을 활용한 처리방식


1. 테이블 생성 및 개발 준비

- 테이블 생성
```sql
CREATE TABLE BOOK_EX.TBL_MEMBER(
	USERID VARCHAR(50) PRIMARY KEY,
    USERPW VARCHAR(50) NOT NULL,
    USERNAME VARCHAR(50) NOT NULL,
    EMAIL VARCHAR(100),
    REGDATE TIMESTAMP DEFAULT NOW(),
    UPDATEDATE TIMESTAMP DEFAULT NOW()
);
```
✨ 테이블명은 `tbl_` 로 시작하는 것이 DB 내 여러 종류 객체와 혼란을 피하기에 좋음

- dto 준비

```java
package com.test.jisoo.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberVO {
	private String userId;
	private String userPw;
	private String userName;
	private String email;
	private LocalDateTime regDate;
	private LocalDateTime updateDate;
}

```

- DAO 인터페이스 작성
```java
package com.test.jisoo.domain.dao;

import com.test.jisoo.domain.model.MemberVO;

public interface MemberDao {

	static final String NAMESPACE="member.";
	
	public String getTime();
	public void insertMember(MemberVO member);
}

```

- config.xml 수정(별칭, 매퍼파일 연결)
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  <configuration>
  	<!-- 별칭 -->
  	<typeAliases>
  		<typeAlias type="com.test.jisoo.domain.model.MemberVO" alias="memberVo"/>
  	</typeAliases>
  	
  	<!-- mapper파일 위치 -->
  	<mappers>
  	 	<mapper resource="mybatis/memberMapper.xml"/>
  	</mappers>
  </configuration>
```

- xml mapper 작성 `src\main\resources\mybatis`
https://mybatis.org/mybatis-3/ko/getting-started.html
에서 `매핑된 SQL 구문 살펴보기`에 있는 
```xml
<?xml version="1.0" encoding="UTF-8"  ?>  <!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
```
를 활용!!

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="member">
  	<select id="getTime" resultType="string">
  	 SELECT NOW()
  	</select>
  
  	<insert id="insertMember" parameterType="memberVo">
  	  INSERT INTO TBL_MEMBER(USERID,USERPW,USERNAME,EMAIL)
  	  VALUES(#{userId},#{userPw},#{userName},#{email})
  	</insert>
  </mapper>
```

✅ namespace는 원하는 SQL문을 매칭시켜주는데에 활용되므로 신경써주기!!
+ 참고로, config.xml 외에 applicationContext.xml에서
```xml
		<bean id="sqlSession" class="org.mybatis.spring.SqlSessionFactoryBean">
			<property name="dataSource" ref="dataSource"></property>
			<property name="configLocation" value="classpath:\sqls\config.xml"/>
			<property name="mapperLocations" value="classpath:mybatis/**/*.xml"/>
		</bean>
```

로도 설정해줄 수 있다!(`mapperLocations`)

- DAO 인터페이스 구현

✅ DAO 작업과 관련되어서 마이바티스에서는 `SqlSessionTemplate` 라는 클래스를 활용하는데 이의 특징은 아래와 같음

(1) 마이바티스의 SqlSession을 구현한 클래스
(2) 기본적인 트랜잭션 관리, 쓰레드 처리의 안정성  보장
(3) 데이터베이스의 연결과 종료를 책임
(4) SqlSessionFactory를 생성자로 주입해서 설정

```java
package com.test.jisoo.domain.dao;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.test.jisoo.domain.model.MemberVO;

@Repository
public class MemberDaoImpl implements MemberDao{

	@Autowired
	private SqlSessionTemplate session;

	@Override
	public String getTime() {
		// TODO Auto-generated method stub
		return session.selectOne(NAMESPACE+"getTime");
	}

	@Override
	public void insertMember(MemberVO member) {
		// TODO Auto-generated method stub
		session.insert(NAMESPACE+"insertMember",member);
	}
	
	
}

```
- 마이바티스의 로그 log4jdbc-log4j2 활용

SQL 쿼리 작성을 확인하기 위한 로그를 지원해주는!!
아주 좋은!!❤
pom.xml에 아래를 추가!!
```xml
 <!-- https://mvnrepository.com/artifact/org.bgee.log4jdbc-log4j2/log4jdbc-log4j2-jdbc4 -->
		<dependency>
    		<groupId>org.bgee.log4jdbc-log4j2</groupId>
    		<artifactId>log4jdbc-log4j2-jdbc4.1</artifactId>
    		<version>1.16</version>
		</dependency>
```

그리고나서는 이제 dataSource 부분의 값을 바꿔주면 되는데, 나는 db.properties 파일에 기록해주었기 때문에 이 부분을 변경해주었다(driverClassName, url 부분의 value로 바로 적용해줘도 무방하다)

```properties
driver=net.sf.log4jdbc.sql.jdbcapi..DriverSpy
url=jdbc:log4jdbc:mysql://localhost:3306/BOOK_EX
username=root
password=
```
➕ 그리고 !! 로그 관련 설정파일로
`/src/main/resources` 폴더에 log4jdbc.log4j2.properties 파일과 logback.xml 파일을 추가해줘야 함!!

(1) log4jdbc.log4j2.properties
```properties
log4jdbc.spylogdelegator.name=net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator
```

(2) logback.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
	<include resource="org/springframework/boot/logging/logback/base.xml"/>
	<!-- log4jdbc-log4j2 -->
	<logger name="jdbc.sqlonly" level="DEBUG"/>
	<logger name="jdbc.sqltiming" level="INFO"/>
	<logger name="jdbc.audit" level="WARN"/>
	<logger name="jdbc.resultset" level="ERROR"/>
	<logger name="jdbc.resultsettable" level="ERROR"/>
	<logger name="jdbc.connection" level="INFO"/>
</configuration>
```
2. 테스트 코드 준비, 실행

```java
package com.test.jisoo.domain.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.test.jisoo.domain.model.MemberVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
		locations= {"file:src/main/webapp/WEB-INF/spring/**/*.xml"}
)
public class MemberDaoTest {
	
	@Autowired
	private MemberDao dao;
	
	@Test
	public void testTime() throws Exception{
		System.out.println(dao.getTime());
	}
	
	@Test
	public void testInsertMember() throws Exception{
		MemberVO member=new MemberVO();
		member.setUserId("user00");
		member.setUserPw("user00");
		member.setUserName("USER00");
		member.setEmail("user00@aaa.com");
		
		dao.insertMember(member);
	}
	
}

```

이제 로그를 살펴보면

```
INFO : org.springframework.test.context.TestContextManager - @TestExecutionListeners is not present for class [class com.test.jisoo.domain.dao.MemberDaoTest]: using defaults.
INFO : org.springframework.beans.factory.xml.XmlBeanDefinitionReader - Loading XML bean definitions from file [D:\SpringReview\test\src\main\webapp\WEB-INF\spring\appServlet\applicationContext.xml]
INFO : org.springframework.beans.factory.xml.XmlBeanDefinitionReader - Loading XML bean definitions from file [D:\SpringReview\test\src\main\webapp\WEB-INF\spring\appServlet\servlet-context.xml]
INFO : org.springframework.context.annotation.ClassPathBeanDefinitionScanner - JSR-330 'javax.inject.Named' annotation found and supported for component scanning
INFO : org.springframework.context.support.GenericApplicationContext - Refreshing org.springframework.context.support.GenericApplicationContext@515aebb0: startup date [Thu Dec 23 17:44:22 KST 2021]; root of context hierarchy
INFO : org.springframework.beans.factory.config.PropertyPlaceholderConfigurer - Loading properties file from class path resource [mybatis/db.properties]
INFO : org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor - JSR-330 'javax.inject.Inject' annotation found and supported for autowiring
INFO : org.springframework.beans.factory.support.DefaultListableBeanFactory - Pre-instantiating singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@cb191ca: defining beans [org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#0,dataSource,sqlSession,sqlSessionTemplate,org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#0,org.springframework.format.support.FormattingConversionServiceFactoryBean#0,org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#0,org.springframework.web.servlet.handler.MappedInterceptor#0,org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#0,org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver#0,org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver#0,org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,org.springframework.web.servlet.resource.ResourceHttpRequestHandler#0,org.springframework.web.servlet.handler.SimpleUrlHandlerMapping#0,org.springframework.web.servlet.view.InternalResourceViewResolver#0,jsonController,modelController,redirectController,stringController,voidController,memberDaoImpl,homeController,org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.ConfigurationClassPostProcessor$ImportAwareBeanPostProcessor#0]; root of factory hierarchy
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/doJSON01],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public org.springframework.http.ResponseEntity<com.test.jisoo.domain.model.ProductVO> com.test.jisoo.controller.JsonController.doJSON01()
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/doJSON02],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public com.test.jisoo.domain.model.ProductVO com.test.jisoo.controller.JsonController.doJSON02()
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/doD],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.String com.test.jisoo.controller.ModelController.doD(org.springframework.ui.Model)
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/doE],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.String com.test.jisoo.controller.RedirectController.doE(org.springframework.web.servlet.mvc.support.RedirectAttributes)
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/doF],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public void com.test.jisoo.controller.RedirectController.doF(java.lang.String)
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/doC],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.String com.test.jisoo.controller.StringController.doC(java.lang.String)
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/doA],methods=[],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public void com.test.jisoo.controller.VoidController.doA()
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/],methods=[GET],params=[],headers=[],consumes=[],produces=[],custom=[]}" onto public java.lang.String com.test.jisoo.HomeController.home(java.util.Locale,org.springframework.ui.Model)
INFO : org.springframework.web.servlet.handler.SimpleUrlHandlerMapping - Mapped URL path [/resources/**] onto handler 'org.springframework.web.servlet.resource.ResourceHttpRequestHandler#0'
INFO : jdbc.connection - 1. Connection opened
INFO : jdbc.audit - 1. Connection.new Connection returned 
INFO : jdbc.audit - 1. Connection.isClosed() returned false
INFO : jdbc.audit - 1. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 1. Connection.isClosed() returned false
INFO : jdbc.audit - 1. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 1. Connection.clearWarnings() returned 
INFO : jdbc.audit - 1. Connection.getAutoCommit() returned true
INFO : jdbc.connection - 1. Connection closed
INFO : jdbc.audit - 1. Connection.close() returned 
INFO : jdbc.connection - 2. Connection opened
INFO : jdbc.audit - 2. Connection.new Connection returned 
INFO : jdbc.audit - 2. Connection.isClosed() returned false
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 2. PreparedStatement.new PreparedStatement returned 
INFO : jdbc.audit - 2. Connection.prepareStatement(INSERT INTO TBL_MEMBER(USERID,USERPW,USERNAME,EMAIL)
  	  VALUES(?,?,?,?)) returned net.sf.log4jdbc.sql.jdbcapi.PreparedStatementSpy@2a685eba
INFO : jdbc.audit - 2. PreparedStatement.setString(1, "user00") returned 
INFO : jdbc.audit - 2. PreparedStatement.setString(2, "user00") returned 
INFO : jdbc.audit - 2. PreparedStatement.setString(3, "USER00") returned 
INFO : jdbc.audit - 2. PreparedStatement.setString(4, "user00@aaa.com") returned 
INFO : jdbc.sqlonly - INSERT INTO TBL_MEMBER(USERID,USERPW,USERNAME,EMAIL) VALUES('user00','user00','USER00','user00@aaa.com') 

ERROR: jdbc.audit - 2. PreparedStatement.execute() INSERT INTO TBL_MEMBER(USERID,USERPW,USERNAME,EMAIL) VALUES('user00','user00','USER00','user00@aaa.com') 

java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'user00' for key 'tbl_member.PRIMARY'
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:117)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)
	at com.mysql.cj.jdbc.ClientPreparedStatement.executeInternal(ClientPreparedStatement.java:953)
	at com.mysql.cj.jdbc.ClientPreparedStatement.execute(ClientPreparedStatement.java:370)
	at net.sf.log4jdbc.sql.jdbcapi.PreparedStatementSpy.execute(PreparedStatementSpy.java:443)
	at org.apache.commons.dbcp.DelegatingPreparedStatement.execute(DelegatingPreparedStatement.java:172)
	at org.apache.commons.dbcp.DelegatingPreparedStatement.execute(DelegatingPreparedStatement.java:172)
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.update(PreparedStatementHandler.java:47)
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.update(RoutingStatementHandler.java:74)
	at org.apache.ibatis.executor.SimpleExecutor.doUpdate(SimpleExecutor.java:50)
	at org.apache.ibatis.executor.BaseExecutor.update(BaseExecutor.java:117)
	at org.apache.ibatis.executor.CachingExecutor.update(CachingExecutor.java:76)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.update(DefaultSqlSession.java:197)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.insert(DefaultSqlSession.java:184)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:433)
	at com.sun.proxy.$Proxy14.insert(Unknown Source)
	at org.mybatis.spring.SqlSessionTemplate.insert(SqlSessionTemplate.java:278)
	at com.test.jisoo.domain.dao.MemberDaoImpl.insertMember(MemberDaoImpl.java:24)
	at com.test.jisoo.domain.dao.MemberDaoTest.testInsertMember(MemberDaoTest.java:33)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:74)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:83)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:72)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:231)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:71)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:174)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:93)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:40)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:529)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:756)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:452)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:210)
ERROR: jdbc.sqlonly - 2. PreparedStatement.execute() INSERT INTO TBL_MEMBER(USERID,USERPW,USERNAME,EMAIL) VALUES('user00','user00','USER00','user00@aaa.com') 

java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'user00' for key 'tbl_member.PRIMARY'
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:117)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)
	at com.mysql.cj.jdbc.ClientPreparedStatement.executeInternal(ClientPreparedStatement.java:953)
	at com.mysql.cj.jdbc.ClientPreparedStatement.execute(ClientPreparedStatement.java:370)
	at net.sf.log4jdbc.sql.jdbcapi.PreparedStatementSpy.execute(PreparedStatementSpy.java:443)
	at org.apache.commons.dbcp.DelegatingPreparedStatement.execute(DelegatingPreparedStatement.java:172)
	at org.apache.commons.dbcp.DelegatingPreparedStatement.execute(DelegatingPreparedStatement.java:172)
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.update(PreparedStatementHandler.java:47)
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.update(RoutingStatementHandler.java:74)
	at org.apache.ibatis.executor.SimpleExecutor.doUpdate(SimpleExecutor.java:50)
	at org.apache.ibatis.executor.BaseExecutor.update(BaseExecutor.java:117)
	at org.apache.ibatis.executor.CachingExecutor.update(CachingExecutor.java:76)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.update(DefaultSqlSession.java:197)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.insert(DefaultSqlSession.java:184)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:433)
	at com.sun.proxy.$Proxy14.insert(Unknown Source)
	at org.mybatis.spring.SqlSessionTemplate.insert(SqlSessionTemplate.java:278)
	at com.test.jisoo.domain.dao.MemberDaoImpl.insertMember(MemberDaoImpl.java:24)
	at com.test.jisoo.domain.dao.MemberDaoTest.testInsertMember(MemberDaoTest.java:33)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:74)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:83)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:72)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:231)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:71)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:174)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:93)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:40)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:529)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:756)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:452)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:210)
ERROR: jdbc.sqltiming - 2. PreparedStatement.execute() FAILED! INSERT INTO TBL_MEMBER(USERID,USERPW,USERNAME,EMAIL) VALUES('user00','user00','USER00','user00@aaa.com') 
 {FAILED after 13 msec}
java.sql.SQLIntegrityConstraintViolationException: Duplicate entry 'user00' for key 'tbl_member.PRIMARY'
	at com.mysql.cj.jdbc.exceptions.SQLError.createSQLException(SQLError.java:117)
	at com.mysql.cj.jdbc.exceptions.SQLExceptionsMapping.translateException(SQLExceptionsMapping.java:122)
	at com.mysql.cj.jdbc.ClientPreparedStatement.executeInternal(ClientPreparedStatement.java:953)
	at com.mysql.cj.jdbc.ClientPreparedStatement.execute(ClientPreparedStatement.java:370)
	at net.sf.log4jdbc.sql.jdbcapi.PreparedStatementSpy.execute(PreparedStatementSpy.java:443)
	at org.apache.commons.dbcp.DelegatingPreparedStatement.execute(DelegatingPreparedStatement.java:172)
	at org.apache.commons.dbcp.DelegatingPreparedStatement.execute(DelegatingPreparedStatement.java:172)
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.update(PreparedStatementHandler.java:47)
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.update(RoutingStatementHandler.java:74)
	at org.apache.ibatis.executor.SimpleExecutor.doUpdate(SimpleExecutor.java:50)
	at org.apache.ibatis.executor.BaseExecutor.update(BaseExecutor.java:117)
	at org.apache.ibatis.executor.CachingExecutor.update(CachingExecutor.java:76)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.update(DefaultSqlSession.java:197)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.insert(DefaultSqlSession.java:184)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:433)
	at com.sun.proxy.$Proxy14.insert(Unknown Source)
	at org.mybatis.spring.SqlSessionTemplate.insert(SqlSessionTemplate.java:278)
	at com.test.jisoo.domain.dao.MemberDaoImpl.insertMember(MemberDaoImpl.java:24)
	at com.test.jisoo.domain.dao.MemberDaoTest.testInsertMember(MemberDaoTest.java:33)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:44)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:15)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:41)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:20)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:74)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:83)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:72)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:231)
	at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:50)
	at org.junit.runners.ParentRunner$3.run(ParentRunner.java:193)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:52)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:191)
	at org.junit.runners.ParentRunner.access$000(ParentRunner.java:42)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:184)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:71)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:236)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:174)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:93)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:40)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:529)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:756)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:452)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:210)
INFO : jdbc.audit - 2. PreparedStatement.close() returned 
INFO : jdbc.audit - 2. Connection.isClosed() returned false
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 2. Connection.clearWarnings() returned 
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : org.springframework.beans.factory.xml.XmlBeanDefinitionReader - Loading XML bean definitions from class path resource [org/springframework/jdbc/support/sql-error-codes.xml]
INFO : org.springframework.jdbc.support.SQLErrorCodesFactory - SQLErrorCodes loaded: [DB2, Derby, H2, HSQL, Informix, MS-SQL, MySQL, Oracle, PostgreSQL, Sybase]
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 2. Connection.getMetaData() returned com.mysql.cj.jdbc.DatabaseMetaDataUsingInfoSchema@4b3fe06e
INFO : jdbc.audit - 2. Connection.isClosed() returned false
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 2. Connection.clearWarnings() returned 
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 2. PreparedStatement.new PreparedStatement returned 
INFO : jdbc.audit - 2. Connection.prepareStatement(SELECT NOW()) returned net.sf.log4jdbc.sql.jdbcapi.PreparedStatementSpy@29eda4f8
INFO : jdbc.sqlonly - SELECT NOW() 

INFO : jdbc.sqltiming - SELECT NOW() 
 {executed in 29 msec}
INFO : jdbc.audit - 2. PreparedStatement.execute() returned true
INFO : jdbc.resultset - 2. ResultSet.new ResultSet returned 
INFO : jdbc.audit - 2. PreparedStatement.getResultSet() returned net.sf.log4jdbc.sql.jdbcapi.ResultSetSpy@42b28ff1
INFO : jdbc.resultset - 2. ResultSet.getMetaData() returned com.mysql.cj.jdbc.result.ResultSetMetaData@3e104d4b - Field level information: 
	com.mysql.cj.result.Field@55e2fe3c[dbName=null,tableName=null,originalTableName=null,columnName=NOW(),originalColumnName=null,mysqlType=12(FIELD_TYPE_DATETIME),sqlType=93,flags= BINARY, charsetIndex=63, charsetName=ISO-8859-1]
INFO : jdbc.resultset - 2. ResultSet.getType() returned 1003
INFO : jdbc.resultset - 2. ResultSet.isClosed() returned false
INFO : jdbc.resultset - 2. ResultSet.next() returned true
INFO : jdbc.resultset - 2. ResultSet.getString(NOW()) returned 2021-12-23 17:44:23
INFO : jdbc.resultset - 2. ResultSet.isClosed() returned false
INFO : jdbc.resultsettable - 
|--------------------|
|now()               |
|--------------------|
|2021-12-23 17:44:23 |
|--------------------|

INFO : jdbc.resultset - 2. ResultSet.next() returned false
INFO : jdbc.resultset - 2. ResultSet.close() returned void
INFO : jdbc.audit - 2. Connection.getMetaData() returned com.mysql.cj.jdbc.DatabaseMetaDataUsingInfoSchema@49fe3142
INFO : jdbc.audit - 2. PreparedStatement.getMoreResults() returned false
INFO : jdbc.audit - 2. PreparedStatement.getUpdateCount() returned -1
INFO : jdbc.audit - 2. PreparedStatement.close() returned 
INFO : jdbc.audit - 2. Connection.isClosed() returned false
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
INFO : jdbc.audit - 2. Connection.clearWarnings() returned 
INFO : jdbc.audit - 2. Connection.getAutoCommit() returned true
2021-12-23 17:44:23
INFO : org.springframework.context.support.GenericApplicationContext - Closing org.springframework.context.support.GenericApplicationContext@515aebb0: startup date [Thu Dec 23 17:44:22 KST 2021]; root of context hierarchy
INFO : org.springframework.beans.factory.support.DefaultListableBeanFactory - Destroying singletons in org.springframework.beans.factory.support.DefaultListableBeanFactory@cb191ca: defining beans [org.springframework.beans.factory.config.PropertyPlaceholderConfigurer#0,dataSource,sqlSession,sqlSessionTemplate,org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping#0,org.springframework.format.support.FormattingConversionServiceFactoryBean#0,org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter#0,org.springframework.web.servlet.handler.MappedInterceptor#0,org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver#0,org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver#0,org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver#0,org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter,org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter,org.springframework.web.servlet.resource.ResourceHttpRequestHandler#0,org.springframework.web.servlet.handler.SimpleUrlHandlerMapping#0,org.springframework.web.servlet.view.InternalResourceViewResolver#0,jsonController,modelController,redirectController,stringController,voidController,memberDaoImpl,homeController,org.springframework.context.annotation.internalConfigurationAnnotationProcessor,org.springframework.context.annotation.internalAutowiredAnnotationProcessor,org.springframework.context.annotation.internalRequiredAnnotationProcessor,org.springframework.context.annotation.ConfigurationClassPostProcessor$ImportAwareBeanPostProcessor#0]; root of factory hierarchy

```
위와 같은 로그를 확인해볼 수 있고, mysql 워크밴치를 확인해보면, 데이터가 잘 삽입된 것을 확인해볼 수 있다
