# Spring MVC CRUD 복습

1. spring-jdbc,  spring-test, commons-dbcp, mysql-connector/j, mybatis, mybatis-spring, log4jdbc 의존성 추가
```xml
<!-- https://mvnrepository.com/artifact/org.springframework/spring-jdbc -->
		<dependency>
    		<groupId>org.springframework</groupId>
    		<artifactId>spring-jdbc</artifactId>
    		<version>${org.springframework-version}</version>
		</dependency>
		 <!-- https://mvnrepository.com/artifact/commons-dbcp/commons-dbcp -->
		<dependency>
    		<groupId>commons-dbcp</groupId>
    		<artifactId>commons-dbcp</artifactId>
    		<version>1.4</version>
		</dependency>
		   <!-- https://mvnrepository.com/artifact/mysql/mysql-connector-java -->
		<dependency>
    		<groupId>mysql</groupId>
    		<artifactId>mysql-connector-java</artifactId>
    		<version>8.0.26</version>
		</dependency>
		 <!-- https://mvnrepository.com/artifact/org.springframework/spring-test -->
		<dependency>
    		<groupId>org.springframework</groupId>
    		<artifactId>spring-test</artifactId>
    		<version>${org.springframework-version}</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.springframework/spring-test -->
		<dependency>
    		<groupId>org.springframework</groupId>
    		<artifactId>spring-test</artifactId>
    		<version>${org.springframework-version}</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
		<dependency>
    		<groupId>org.mybatis</groupId>
    		<artifactId>mybatis</artifactId>
    		<version>3.5.7</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis-spring -->
		<dependency>
    		<groupId>org.mybatis</groupId>
    		<artifactId>mybatis-spring</artifactId>
    		<version>2.0.6</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis-spring -->
		<dependency>
    		<groupId>org.mybatis</groupId>
    		<artifactId>mybatis-spring</artifactId>
    		<version>2.0.6</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.bgee.log4jdbc-log4j2/log4jdbc-log4j2-jdbc4 -->
<dependency>
    <groupId>org.bgee.log4jdbc-log4j2</groupId>
    <artifactId>log4jdbc-log4j2-jdbc4</artifactId>
    <version>1.16</version>
</dependency>
 
```

2. log4jdbc.log4j2.properties , log4j2.xml 준비
```properties
log4jdbc.spylogdelegator.name=net.sf.log4jdbc.log.slf4j.Slf4jSpyLogDelegator
```

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>
  <include resource="org/springframework/boot/logging/logback/base.xml"/>
  
  <logger name="jdbc.sqlonly" level="DEBUG"/>
  <logger name="jdbc.sqltiming" level="INFO"/>
  <logger name="jdbc.audit" level="WARN"/>
  <logger name="jdbc.resultset" level="ERROR"/>
  <logger name="jdbc.resultsettable" level="ERROR"/>
  <logger name="jdbc.connection" level="INFO"/>
</configuration>
```

3. src/main/resources/mybatis/db.properties
```properties
driverClassName=net.sf.log4jdbc.sql.jdbcapi.DriverSpy
url=jdbc:mysql://127.0.0.1:3306/book_ex
username=root
password=jisoo
```

4. src/main/webapp/WEB-INF/spring/appServlet/applicationContext.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd">
	
	<!-- Root Context: defines shared resources visible to all other web components -->
	<!-- db.properties -->
	<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="locations">
			<list>
			<!-- src/main/resources/~ -->
				<value>classpath:mybatis/db.properties</value>
			</list>
		</property>
	</bean>	
	
	<!-- dataSource -->
	<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
		<property name="driverClassName" value="${driverClassName}"/>
		<property name="url" value="${url}"/>
		<property name="username" value="${username}"/>
		<property name="password" value="${password}"/>
	</bean>
	
	<!-- sqlSessionFactory -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource"/>
		<!-- mapper 설정 config.xml 위치 -->
		<!-- src/main/webapp/~ -->
		<property name="configLocation" value="/sqls/config.xml"/>
	</bean>
	
	<!-- sqlSessionTemplate -->
	<bean id="sqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
		<constructor-arg ref="sqlSessionFactory"/>
	</bean>
</beans>


```

5. src/main/webapp/web.xml
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
	
	<!-- Creates the Spring Container shared by all Servlets and Filters -->
	<listener>
		<listener-class>org.springframework.web.context.ContextLoaderListener</listener-class>
	</listener>

	<!-- Processes application requests -->
	<servlet>
		<servlet-name>appServlet</servlet-name>
		<servlet-class>org.springframework.web.servlet.DispatcherServlet</servlet-class>
		<init-param>
			<param-name>contextConfigLocation</param-name>
			<param-value>/WEB-INF/spring/appServlet/servlet-context.xml</param-value>
		</init-param>
		<load-on-startup>1</load-on-startup>
	</servlet>
		
	<servlet-mapping>
		<servlet-name>appServlet</servlet-name>
		<url-pattern>/</url-pattern>
	</servlet-mapping>

</web-app>

```

6. src/main/resources/sqls/config.xml
```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  
  <configuration>
  	<!-- 별칭 -->
  	<typeAliases>
  	</typeAliases>
  	<!-- 매퍼위치들 -->
  	<mappers>
  	</mappers>
  </configuration>
```

7. 자바 1.8 jdk를 사용하기 위해 jdk, spring 버전을 4로 올려주고, 메이븐 버전도 조정해주자(pom.xml)
```xml
	<properties>
		<java-version>1.8</java-version>
		<org.springframework-version>4.1.7.RELEASE</org.springframework-version>
		<org.aspectj-version>1.6.10</org.aspectj-version>
		<org.slf4j-version>1.6.6</org.slf4j-version>
	</properties>
	(중략)
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

8. dataSource 연결 테스트
```java
package com.zero.mvc;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(
	locations= {"file:src/main/webapp/WEB-INF/spring/**/*.xml"}
)
public class DataSourceTest {
	
	@Autowired
	private DataSource ds;
	
	@Test
	public void testConnection() {
		try(Connection conn=DataSourceUtils.getConnection(ds)){
			System.out.println("connection: "+conn);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}

```

```
INFO : org.springframework.test.context.support.DefaultTestContextBootstrapper - Loaded default TestExecutionListener class names from location [META-INF/spring.factories]: [org.springframework.test.context.web.ServletTestExecutionListener, org.springframework.test.context.support.DependencyInjectionTestExecutionListener, org.springframework.test.context.support.DirtiesContextTestExecutionListener, org.springframework.test.context.transaction.TransactionalTestExecutionListener, org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener]
INFO : org.springframework.test.context.support.DefaultTestContextBootstrapper - Using TestExecutionListeners: [org.springframework.test.context.web.ServletTestExecutionListener@770d3326, org.springframework.test.context.support.DependencyInjectionTestExecutionListener@4cc8eb05, org.springframework.test.context.support.DirtiesContextTestExecutionListener@51f116b8, org.springframework.test.context.transaction.TransactionalTestExecutionListener@19d481b, org.springframework.test.context.jdbc.SqlScriptsTestExecutionListener@1f97cf0d]
INFO : org.springframework.beans.factory.xml.XmlBeanDefinitionReader - Loading XML bean definitions from file [D:\SpringReview\zero\src\main\webapp\WEB-INF\spring\appServlet\applicationContext.xml]
INFO : org.springframework.beans.factory.xml.XmlBeanDefinitionReader - Loading XML bean definitions from file [D:\SpringReview\zero\src\main\webapp\WEB-INF\spring\appServlet\servlet-context.xml]
INFO : org.springframework.context.support.GenericApplicationContext - Refreshing org.springframework.context.support.GenericApplicationContext@35aea049: startup date [Sat Dec 25 15:26:30 KST 2021]; root of context hierarchy
INFO : org.springframework.beans.factory.config.PropertyPlaceholderConfigurer - Loading properties file from class path resource [mybatis/db.properties]
INFO : org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor - JSR-330 'javax.inject.Inject' annotation found and supported for autowiring
Loading class `com.mysql.jdbc.Driver'. This is deprecated. The new driver class is `com.mysql.cj.jdbc.Driver'. The driver is automatically registered via the SPI and manual loading of the driver class is generally unnecessary.
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping - Mapped "{[/],methods=[GET]}" onto public java.lang.String com.zero.mvc.HomeController.home(java.util.Locale,org.springframework.ui.Model)
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - Looking for @ControllerAdvice: org.springframework.context.support.GenericApplicationContext@35aea049: startup date [Sat Dec 25 15:26:30 KST 2021]; root of context hierarchy
INFO : org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter - Looking for @ControllerAdvice: org.springframework.context.support.GenericApplicationContext@35aea049: startup date [Sat Dec 25 15:26:30 KST 2021]; root of context hierarchy
INFO : org.springframework.web.servlet.handler.SimpleUrlHandlerMapping - Mapped URL path [/resources/**] onto handler 'org.springframework.web.servlet.resource.ResourceHttpRequestHandler#0'
connection: jdbc:mysql://127.0.0.1:3306/BOOK_EX, UserName=root@localhost, MySQL Connector/J
INFO : org.springframework.context.support.GenericApplicationContext - Closing org.springframework.context.support.GenericApplicationContext@35aea049: startup date [Sat Dec 25 15:26:30 KST 2021]; root of context hierarchy

```

## 01. 테이블 생성

tbl_board 테이블을 생성해보자

```sql
CREATE TABLE BOOK_EX.TBL_BOARD(
-- PK=NOT NULL+UNIQUE CONSTRAINT
	BNO INT PRIMARY KEY AUTO_INCREMENT,
    TITLE VARCHAR(200) NOT NULL,
    CONTENT TEXT NULL,
    WRITER VARCHAR(50) NOT NULL,
    REGDATE TIMESTAMP NOT NULL DEFAULT NOW(),
    VIEWCNT INT DEFAULT 0
);
```

## 02. 더미 데이터 삽입

```sql
INSERT INTO BOOK_EX.TBL_BOARD(TITLE,CONTENT,WRITER)
VALUES('제목입니다','내용입니다','USER00');
```

## 03. UTF-8 인코딩 필터 설정 in `web.xml`

```xml
<filter>
		<filter-name>encoding</filter-name>
		<filter-class>org.springframework.web.filter.CharacterEncodingFilter</filter-class>
	</filter>
	
	<init-param>
		<param-name>encoding</param-name>
		<param-value>UTF-8</param-value>
	</init-param>
	<init-param>
		<param-name>forceEncoding</param-name>
		<param-value>true</param-value>
	</init-param>
```


## 04. Admin LTE 템플릿 활용

(저는 디자인에 대해서 손재주가 없슴다..ㅠㅠ)

개정 이전 인쇄판 기록이 없어서 해당 파일이 있는 링크를 참고하자!
https://nanci.tistory.com/228

1. include.zip 은 `src\main\webapp\WEB-INF\views`에 압축을 풀어주자(css, jQuery 등 링크가 포함되어 있음!)
2. static.zip은 폴더이름으로 하여 압축해제하지 않고, `src\main\webapp\resources\static`에 압축을 풀어주자!
3. home.jsp 작성

```jsp
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page session="false" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8" %>
<%@ include file="include/header.jsp" %>
<html>
<head>
	<title>Home</title>
</head>
<body>
	<section class="content">
		<div class="row">
			<!-- left column -->
			<div class="col-md-12">
				<div class="box">
				    <div class="box-header with-border">
				    	<h3 class="box-title">
				    	HOME PAGE
				    	</h3>
				    </div>
				</div>
			</div>
		</div>
	</section>
	<%@include file="include/footer.jsp" %>
</body>
</html>

```

header.jsp 적절히 수정
```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
   
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>AdminLTE 2 | Dashboard</title>
    <meta content='width=device-width, initial-scale=1, maximum-scale=1, user-scalable=no' name='viewport'>
    <!-- Bootstrap 3.3.4 -->
    <link href="<%=request.getContextPath() %>/resources/static/bootstrap/css/bootstrap.min.css" rel="stylesheet" type="text/css" />
    <!-- Font Awesome Icons -->
    <link href="https://maxcdn.bootstrapcdn.com/font-awesome/4.3.0/css/font-awesome.min.css" rel="stylesheet" type="text/css" />
    <!-- Ionicons -->
    <link href="https://code.ionicframework.com/ionicons/2.0.1/css/ionicons.min.css" rel="stylesheet" type="text/css" />
    <!-- Theme style -->
    <link href="<%=request.getContextPath() %>/resources/static/dist/css/AdminLTE.min.css" rel="stylesheet" type="text/css" />
    <!-- AdminLTE Skins. Choose a skin from the css/skins 
         folder instead of downloading all of them to reduce the load. -->
    <link href="<%=request.getContextPath() %>/resources/static/dist/css/skins/_all-skins.min.css" rel="stylesheet" type="text/css" />

    <!-- HTML5 Shim and Respond.js IE8 support of HTML5 elements and media queries -->
    <!-- WARNING: Respond.js doesn't work if you view the page via file:// -->
    <!--[if lt IE 9]>
        <script src="https://oss.maxcdn.com/html5shiv/3.7.2/html5shiv.min.js"></script>
        <script src="https://oss.maxcdn.com/respond/1.4.2/respond.min.js"></script>
    <![endif]-->
    
  </head>
      <!-- jQuery 2.1.4 -->
    <script src="<%=request.getContextPath() %>/resources/static/plugins/jQuery/jQuery-2.1.4.min.js"></script>
  <body class="skin-blue sidebar-mini">
    <div class="wrapper">
      
      <header class="main-header">
        <!-- Logo -->
        <a href="/resources/static/plugins/datatables/extensions/AutoFill/examples/index.html" class="logo">
          <!-- mini logo for sidebar mini 50x50 pixels -->
          <span class="logo-mini"><b>A</b>LT</span>
          <!-- logo for regular state and mobile devices -->
          <span class="logo-lg"><b>Zerock</b> PROJECT</span>
        </a>
        <!-- Header Navbar: style can be found in header.less -->
        <nav class="navbar navbar-static-top" role="navigation">
          <!-- Sidebar toggle button-->
          <a href="#" class="sidebar-toggle" data-toggle="offcanvas" role="button">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <div class="navbar-custom-menu">
            <ul class="nav navbar-nav">
              <!-- Messages: style can be found in dropdown.less-->
              <li class="dropdown messages-menu">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                  <i class="fa fa-envelope-o"></i>
                  <span class="label label-success">4</span>
                </a>
                <ul class="dropdown-menu">
                  <li class="header">You have 4 messages</li>
                  <li>
                    <!-- inner menu: contains the actual data -->
                    <ul class="menu">
                      <li><!-- start message -->
                        <a href="#">
                          <div class="pull-left">
                            <img src="<%=request.getContextPath() %>/resources/static/dist/img/user7-128x128.jpg" class="img-circle" alt="User Image"/>
                          </div>
                          <h4>
                            Support Team
                            <small><i class="fa fa-clock-o"></i> 5 mins</small>
                          </h4>
                          <p>Why not buy a new awesome theme?</p>
                        </a>
                      </li><!-- end message -->
                      <li>
                        <a href="#">
                          <div class="pull-left">
                            <img src="<%=request.getContextPath() %>/resources/static/dist/img/user3-128x128.jpg" class="img-circle" alt="user image"/>
                          </div>
                          <h4>
                            AdminLTE Design Team
                            <small><i class="fa fa-clock-o"></i> 2 hours</small>
                          </h4>
                          <p>Why not buy a new awesome theme?</p>
                        </a>
                      </li>
                      <li>
                        <a href="#">
                          <div class="pull-left">
                            <img src="<%=request.getContextPath() %>/resources/static/dist/img/user4-128x128.jpg" class="img-circle" alt="user image"/>
                          </div>
                          <h4>
                            Developers
                            <small><i class="fa fa-clock-o"></i> Today</small>
                          </h4>
                          <p>Why not buy a new awesome theme?</p>
                        </a>
                      </li>
                      <li>
                        <a href="#">
                          <div class="pull-left">
                            <img src="<%=request.getContextPath() %>/resources/static/dist/img/user3-128x128.jpg" class="img-circle" alt="user image"/>
                          </div>
                          <h4>
                            Sales Department
                            <small><i class="fa fa-clock-o"></i> Yesterday</small>
                          </h4>
                          <p>Why not buy a new awesome theme?</p>
                        </a>
                      </li>
                      <li>
                        <a href="#">
                          <div class="pull-left">
                            <img src="<%=request.getContextPath() %>/resources/static/dist/img/user4-128x128.jpg" class="img-circle" alt="user image"/>
                          </div>
                          <h4>
                            Reviewers
                            <small><i class="fa fa-clock-o"></i> 2 days</small>
                          </h4>
                          <p>Why not buy a new awesome theme?</p>
                        </a>
                      </li>
                    </ul>
                  </li>
                  <li class="footer"><a href="#">See All Messages</a></li>
                </ul>
              </li>
              <!-- Notifications: style can be found in dropdown.less -->
              <li class="dropdown notifications-menu">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                  <i class="fa fa-bell-o"></i>
                  <span class="label label-warning">10</span>
                </a>
                <ul class="dropdown-menu">
                  <li class="header">You have 10 notifications</li>
                  <li>
                    <!-- inner menu: contains the actual data -->
                    <ul class="menu">
                      <li>
                        <a href="#">
                          <i class="fa fa-users text-aqua"></i> 5 new members joined today
                        </a>
                      </li>
                      <li>
                        <a href="#">
                          <i class="fa fa-warning text-yellow"></i> Very long description here that may not fit into the page and may cause design problems
                        </a>
                      </li>
                      <li>
                        <a href="#">
                          <i class="fa fa-users text-red"></i> 5 new members joined
                        </a>
                      </li>

                      <li>
                        <a href="#">
                          <i class="fa fa-shopping-cart text-green"></i> 25 sales made
                        </a>
                      </li>
                      <li>
                        <a href="#">
                          <i class="fa fa-user text-red"></i> You changed your username
                        </a>
                      </li>
                    </ul>
                  </li>
                  <li class="footer"><a href="#">View all</a></li>
                </ul>
              </li>
              <!-- Tasks: style can be found in dropdown.less -->
              <li class="dropdown tasks-menu">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                  <i class="fa fa-flag-o"></i>
                  <span class="label label-danger">9</span>
                </a>
                <ul class="dropdown-menu">
                  <li class="header">You have 9 tasks</li>
                  <li>
                    <!-- inner menu: contains the actual data -->
                    <ul class="menu">
                      <li><!-- Task item -->
                        <a href="#">
                          <h3>
                            Design some buttons
                            <small class="pull-right">20%</small>
                          </h3>
                          <div class="progress xs">
                            <div class="progress-bar progress-bar-aqua" style="width: 20%" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100">
                              <span class="sr-only">20% Complete</span>
                            </div>
                          </div>
                        </a>
                      </li><!-- end task item -->
                      <li><!-- Task item -->
                        <a href="#">
                          <h3>
                            Create a nice theme
                            <small class="pull-right">40%</small>
                          </h3>
                          <div class="progress xs">
                            <div class="progress-bar progress-bar-green" style="width: 40%" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100">
                              <span class="sr-only">40% Complete</span>
                            </div>
                          </div>
                        </a>
                      </li><!-- end task item -->
                      <li><!-- Task item -->
                        <a href="#">
                          <h3>
                            Some task I need to do
                            <small class="pull-right">60%</small>
                          </h3>
                          <div class="progress xs">
                            <div class="progress-bar progress-bar-red" style="width: 60%" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100">
                              <span class="sr-only">60% Complete</span>
                            </div>
                          </div>
                        </a>
                      </li><!-- end task item -->
                      <li><!-- Task item -->
                        <a href="#">
                          <h3>
                            Make beautiful transitions
                            <small class="pull-right">80%</small>
                          </h3>
                          <div class="progress xs">
                            <div class="progress-bar progress-bar-yellow" style="width: 80%" role="progressbar" aria-valuenow="20" aria-valuemin="0" aria-valuemax="100">
                              <span class="sr-only">80% Complete</span>
                            </div>
                          </div>
                        </a>
                      </li><!-- end task item -->
                    </ul>
                  </li>
                  <li class="footer">
                    <a href="#">View all tasks</a>
                  </li>
                </ul>
              </li>
              <!-- User Account: style can be found in dropdown.less -->
              <li class="dropdown user user-menu">
                <a href="#" class="dropdown-toggle" data-toggle="dropdown">
                  <img src="<%=request.getContextPath() %>/resources/static/dist/img/user2-160x160.jpg" class="user-image" alt="User Image"/>
                  <span class="hidden-xs">Alexander Pierce</span>
                </a>
                <ul class="dropdown-menu">
                  <!-- User image -->
                  <li class="user-header">
                    <img src="<%=request.getContextPath() %>/resources/static/dist/img/user2-160x160.jpg" class="img-circle" alt="User Image" />
                    <p>
                      Alexander Pierce - Web Developer
                      <small>Member since Nov. 2012</small>
                    </p>
                  </li>
                  <!-- Menu Body -->
                  <li class="user-body">
                    <div class="col-xs-4 text-center">
                      <a href="#">Followers</a>
                    </div>
                    <div class="col-xs-4 text-center">
                      <a href="#">Sales</a>
                    </div>
                    <div class="col-xs-4 text-center">
                      <a href="#">Friends</a>
                    </div>
                  </li>
                  <!-- Menu Footer-->
                  <li class="user-footer">
                    <div class="pull-left">
                      <a href="#" class="btn btn-default btn-flat">Profile</a>
                    </div>
                    <div class="pull-right">
                      <a href="#" class="btn btn-default btn-flat">Sign out</a>
                    </div>
                  </li>
                </ul>
              </li>
              <!-- Control Sidebar Toggle Button -->
              <li>
                <a href="#" data-toggle="control-sidebar"><i class="fa fa-gears"></i></a>
              </li>
            </ul>
          </div>
        </nav>
      </header>
      <!-- Left side column. contains the logo and sidebar -->
      <aside class="main-sidebar">
        <!-- sidebar: style can be found in sidebar.less -->
        <section class="sidebar">
          <!-- Sidebar user panel -->
          <div class="user-panel">
            <div class="pull-left image">
              <img src="<%=request.getContextPath() %>/resources/static/dist/img/user2-160x160.jpg" class="img-circle" alt="User Image" />
            </div>
            <div class="pull-left info">
              <p>Alexander Pierce</p>

              <a href="#"><i class="fa fa-circle text-success"></i> Online</a>
            </div>
          </div>
          <!-- search form -->
          <form action="#" method="get" class="sidebar-form">
            <div class="input-group">
              <input type="text" name="q" class="form-control" placeholder="Search..."/>
              <span class="input-group-btn">
                <button type='submit' name='search' id='search-btn' class="btn btn-flat"><i class="fa fa-search"></i></button>
              </span>
            </div>
          </form>
          <!-- /.search form -->
          <!-- sidebar menu: : style can be found in sidebar.less -->
          <ul class="sidebar-menu">
            <li class="header">MAIN NAVIGATION</li>
            <li class="treeview">
              <a href="#">
                <i class="fa fa-dashboard"></i> <span>Dashboard</span> <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li><a href="/resources/index.html"><i class="fa fa-circle-o"></i> Dashboard v1</a></li>
                <li><a href="/resources/index2.html"><i class="fa fa-circle-o"></i> Dashboard v2</a></li>
              </ul>
            </li>
            <li class="treeview">
              <a href="#">
                <i class="fa fa-files-o"></i>
                <span>Layout Options</span>
                <span class="label label-primary pull-right">4</span>
              </a>
              <ul class="treeview-menu">
                <li><a href="../layout/top-nav.html"><i class="fa fa-circle-o"></i> Top Navigation</a></li>
                <li><a href="../layout/boxed.html"><i class="fa fa-circle-o"></i> Boxed</a></li>
                <li><a href="../layout/fixed.html"><i class="fa fa-circle-o"></i> Fixed</a></li>
                <li><a href="../layout/collapsed-sidebar.html"><i class="fa fa-circle-o"></i> Collapsed Sidebar</a></li>
              </ul>
            </li>
            <li>
              <a href="../widgets.html">
                <i class="fa fa-th"></i> <span>Widgets</span> <small class="label pull-right bg-green">new</small>
              </a>
            </li>
            <li class="treeview">
              <a href="#">
                <i class="fa fa-pie-chart"></i>
                <span>Charts</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li><a href="../charts/chartjs.html"><i class="fa fa-circle-o"></i> ChartJS</a></li>
                <li><a href="../charts/morris.html"><i class="fa fa-circle-o"></i> Morris</a></li>
                <li><a href="../charts/flot.html"><i class="fa fa-circle-o"></i> Flot</a></li>
                <li><a href="../charts/inline.html"><i class="fa fa-circle-o"></i> Inline charts</a></li>
              </ul>
            </li>
            <li class="treeview">
              <a href="#">
                <i class="fa fa-laptop"></i>
                <span>UI Elements</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li><a href="../UI/general.html"><i class="fa fa-circle-o"></i> General</a></li>
                <li><a href="../UI/icons.html"><i class="fa fa-circle-o"></i> Icons</a></li>
                <li><a href="../UI/buttons.html"><i class="fa fa-circle-o"></i> Buttons</a></li>
                <li><a href="../UI/sliders.html"><i class="fa fa-circle-o"></i> Sliders</a></li>
                <li><a href="../UI/timeline.html"><i class="fa fa-circle-o"></i> Timeline</a></li>
                <li><a href="../UI/modals.html"><i class="fa fa-circle-o"></i> Modals</a></li>
              </ul>
            </li>
            <li class="treeview active">
              <a href="#">
                <i class="fa fa-edit"></i> <span>Forms</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li class="active"><a href="general.html"><i class="fa fa-circle-o"></i> General Elements</a></li>
                <li><a href="advanced.html"><i class="fa fa-circle-o"></i> Advanced Elements</a></li>
                <li><a href="editors.html"><i class="fa fa-circle-o"></i> Editors</a></li>
              </ul>
            </li>
            <li class="treeview">
              <a href="#">
                <i class="fa fa-table"></i> <span>Tables</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li><a href="../tables/simple.html"><i class="fa fa-circle-o"></i> Simple tables</a></li>
                <li><a href="../tables/data.html"><i class="fa fa-circle-o"></i> Data tables</a></li>
              </ul>
            </li>
            <li>
              <a href="../calendar.html">
                <i class="fa fa-calendar"></i> <span>Calendar</span>
                <small class="label pull-right bg-red">3</small>
              </a>
            </li>
            <li>
              <a href="../mailbox/mailbox.html">
                <i class="fa fa-envelope"></i> <span>Mailbox</span>
                <small class="label pull-right bg-yellow">12</small>
              </a>
            </li>
            <li class="treeview">
              <a href="#">
                <i class="fa fa-folder"></i> <span>Examples</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li><a href="../examples/invoice.html"><i class="fa fa-circle-o"></i> Invoice</a></li>
                <li><a href="../examples/login.html"><i class="fa fa-circle-o"></i> Login</a></li>
                <li><a href="../examples/register.html"><i class="fa fa-circle-o"></i> Register</a></li>
                <li><a href="../examples/lockscreen.html"><i class="fa fa-circle-o"></i> Lockscreen</a></li>
                <li><a href="../examples/404.html"><i class="fa fa-circle-o"></i> 404 Error</a></li>
                <li><a href="../examples/500.html"><i class="fa fa-circle-o"></i> 500 Error</a></li>
                <li><a href="../examples/blank.html"><i class="fa fa-circle-o"></i> Blank Page</a></li>                
              </ul>
            </li>
            <li class="treeview">
              <a href="#">
                <i class="fa fa-share"></i> <span>Multilevel</span>
                <i class="fa fa-angle-left pull-right"></i>
              </a>
              <ul class="treeview-menu">
                <li><a href="#"><i class="fa fa-circle-o"></i> Level One</a></li>
                <li>
                  <a href="#"><i class="fa fa-circle-o"></i> Level One <i class="fa fa-angle-left pull-right"></i></a>
                  <ul class="treeview-menu">
                    <li><a href="#"><i class="fa fa-circle-o"></i> Level Two</a></li>
                    <li>
                      <a href="#"><i class="fa fa-circle-o"></i> Level Two <i class="fa fa-angle-left pull-right"></i></a>
                      <ul class="treeview-menu">
                        <li><a href="#"><i class="fa fa-circle-o"></i> Level Three</a></li>
                        <li><a href="#"><i class="fa fa-circle-o"></i> Level Three</a></li>
                      </ul>
                    </li>
                  </ul>
                </li>
                <li><a href="#"><i class="fa fa-circle-o"></i> Level One</a></li>
              </ul>
            </li>
            <li><a href="/resources/documentation/index.html"><i class="fa fa-book"></i> <span>Documentation</span></a></li>
            <li class="header">LABELS</li>
            <li><a href="#"><i class="fa fa-circle-o text-red"></i> <span>Important</span></a></li>
            <li><a href="#"><i class="fa fa-circle-o text-yellow"></i> <span>Warning</span></a></li>
            <li><a href="#"><i class="fa fa-circle-o text-aqua"></i> <span>Information</span></a></li>
          </ul>
        </section>
        <!-- /.sidebar -->
      </aside>

      <!-- Content Wrapper. Contains page content -->
      <div class="content-wrapper">
        <!-- Content Header (Page header) -->
        <section class="content-header">
          <h1>
            General Form Elements
            <small>Preview</small>
          </h1>
          <ol class="breadcrumb">
            <li><a href="#"><i class="fa fa-dashboard"></i> Home</a></li>
            <li><a href="#">Forms</a></li>
            <li class="active">General Elements</li>
          </ol>
        </section>
```

footer.jsp도 적절히 수정
```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
        <!-- Bootstrap 3.3.2 JS -->
    <script src="<%=request.getContextPath() %>/resources/static/bootstrap/js/bootstrap.min.js" type="text/javascript"></script>
    <!-- FastClick -->
    <script src='<%=request.getContextPath() %>/resources/static/plugins/fastclick/fastclick.min.js'></script>
    <!-- AdminLTE App -->
    <script src="<%=request.getContextPath() %>/resources/static/dist/js/app.min.js" type="text/javascript"></script>
    <!-- AdminLTE for demo purposes -->
    <script src="<%=request.getContextPath() %>/resources/static/dist/js/demo.js" type="text/javascript"></script>
      <footer class="main-footer" style="margin: 0 1em">
        <div class="pull-right hidden-xs">
          <b>Version</b> 2.0
        </div>
        <strong>Copyright &copy; 2014-2015 <a href="http://almsaeedstudio.com">Almsaeed Studio</a>.</strong> All rights reserved.
      </footer>
      
      <!-- Control Sidebar -->      
      <aside class="control-sidebar control-sidebar-dark">                
        <!-- Create the tabs -->
        <ul class="nav nav-tabs nav-justified control-sidebar-tabs">
          <li><a href="#control-sidebar-home-tab" data-toggle="tab"><i class="fa fa-home"></i></a></li>
          
          <li><a href="#control-sidebar-settings-tab" data-toggle="tab"><i class="fa fa-gears"></i></a></li>
        </ul>
        <!-- Tab panes -->
        <div class="tab-content">
          <!-- Home tab content -->
          <div class="tab-pane" id="control-sidebar-home-tab">
            <h3 class="control-sidebar-heading">Recent Activity</h3>
            <ul class='control-sidebar-menu'>
              <li>
                <a href='javascript::;'>
                  <i class="menu-icon fa fa-birthday-cake bg-red"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Langdon's Birthday</h4>
                    <p>Will be 23 on April 24th</p>
                  </div>
                </a>
              </li>
              <li>
                <a href='javascript::;'>
                  <i class="menu-icon fa fa-user bg-yellow"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Frodo Updated His Profile</h4>
                    <p>New phone +1(800)555-1234</p>
                  </div>
                </a>
              </li>
              <li>
                <a href='javascript::;'>
                  <i class="menu-icon fa fa-envelope-o bg-light-blue"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Nora Joined Mailing List</h4>
                    <p>nora@example.com</p>
                  </div>
                </a>
              </li>
              <li>
                <a href='javascript::;'>
                  <i class="menu-icon fa fa-file-code-o bg-green"></i>
                  <div class="menu-info">
                    <h4 class="control-sidebar-subheading">Cron Job 254 Executed</h4>
                    <p>Execution time 5 seconds</p>
                  </div>
                </a>
              </li>
            </ul><!-- /.control-sidebar-menu -->

            <h3 class="control-sidebar-heading">Tasks Progress</h3> 
            <ul class='control-sidebar-menu'>
              <li>
                <a href='javascript::;'>               
                  <h4 class="control-sidebar-subheading">
                    Custom Template Design
                    <span class="label label-danger pull-right">70%</span>
                  </h4>
                  <div class="progress progress-xxs">
                    <div class="progress-bar progress-bar-danger" style="width: 70%"></div>
                  </div>                                    
                </a>
              </li> 
              <li>
                <a href='javascript::;'>               
                  <h4 class="control-sidebar-subheading">
                    Update Resume
                    <span class="label label-success pull-right">95%</span>
                  </h4>
                  <div class="progress progress-xxs">
                    <div class="progress-bar progress-bar-success" style="width: 95%"></div>
                  </div>                                    
                </a>
              </li> 
              <li>
                <a href='javascript::;'>               
                  <h4 class="control-sidebar-subheading">
                    Laravel Integration
                    <span class="label label-waring pull-right">50%</span>
                  </h4>
                  <div class="progress progress-xxs">
                    <div class="progress-bar progress-bar-warning" style="width: 50%"></div>
                  </div>                                    
                </a>
              </li> 
              <li>
                <a href='javascript::;'>               
                  <h4 class="control-sidebar-subheading">
                    Back End Framework
                    <span class="label label-primary pull-right">68%</span>
                  </h4>
                  <div class="progress progress-xxs">
                    <div class="progress-bar progress-bar-primary" style="width: 68%"></div>
                  </div>                                    
                </a>
              </li>               
            </ul><!-- /.control-sidebar-menu -->         

          </div><!-- /.tab-pane -->
          <!-- Stats tab content -->
          <div class="tab-pane" id="control-sidebar-stats-tab">Stats Tab Content</div><!-- /.tab-pane -->
          <!-- Settings tab content -->
          <div class="tab-pane" id="control-sidebar-settings-tab">            
            <form method="post">
              <h3 class="control-sidebar-heading">General Settings</h3>
              <div class="form-group">
                <label class="control-sidebar-subheading">
                  Report panel usage
                  <input type="checkbox" class="pull-right" checked />
                </label>
                <p>
                  Some information about this general settings option
                </p>
              </div><!-- /.form-group -->

              <div class="form-group">
                <label class="control-sidebar-subheading">
                  Allow mail redirect
                  <input type="checkbox" class="pull-right" checked />
                </label>
                <p>
                  Other sets of options are available
                </p>
              </div><!-- /.form-group -->

              <div class="form-group">
                <label class="control-sidebar-subheading">
                  Expose author name in posts
                  <input type="checkbox" class="pull-right" checked />
                </label>
                <p>
                  Allow the user to show his name in blog posts
                </p>
              </div><!-- /.form-group -->

              <h3 class="control-sidebar-heading">Chat Settings</h3>

              <div class="form-group">
                <label class="control-sidebar-subheading">
                  Show me as online
                  <input type="checkbox" class="pull-right" checked />
                </label>                
              </div><!-- /.form-group -->

              <div class="form-group">
                <label class="control-sidebar-subheading">
                  Turn off notifications
                  <input type="checkbox" class="pull-right" />
                </label>                
              </div><!-- /.form-group -->

              <div class="form-group">
                <label class="control-sidebar-subheading">
                  Delete chat history
                  <a href="javascript::;" class="text-red pull-right"><i class="fa fa-trash-o"></i></a>
                </label>                
              </div><!-- /.form-group -->
            </form>
          </div><!-- /.tab-pane -->
        </div>
      </aside><!-- /.control-sidebar -->
      <!-- Add the sidebar's background. This div must be placed
           immediately after the control sidebar -->
      <div class='control-sidebar-bg'></div>
    </div><!-- ./wrapper -->
  </body>
</html>
```


## 05. 퍼시스턴스 계층, 비즈니스 계층 준비

✨ DB와 관련된 작업을 먼저 진행했을 때의 장점

1. 변경되는 화면 설계를 미룰 수 있음
2. WAS 없이 테스트 진행 가능(JUnit)

## 05-1. BoardVO 준비

```java
package com.zero.mvc.domain.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BoardVO {
	private Integer bNo;
	private String title;
	private String content;
	private String writer;
	private LocalDateTime regDate;
	private int viewCnt;
}

```
➕ lombok 의존성 추가!!

## 05-2. mapper 작성, dao 생성
- applicationContext.xml: context.xml: beans, context,mybatis-spring 네임스페이스 선택
```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
	xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xmlns:context="http://www.springframework.org/schema/context"
	xmlns:mybatis-spring="http://mybatis.org/schema/mybatis-spring"
	xsi:schemaLocation="http://mybatis.org/schema/mybatis-spring http://mybatis.org/schema/mybatis-spring-1.2.xsd
		http://www.springframework.org/schema/beans https://www.springframework.org/schema/beans/spring-beans.xsd
		http://www.springframework.org/schema/context http://www.springframework.org/schema/context/spring-context-4.1.xsd">
	
	<!-- Root Context: defines shared resources visible to all other web components -->
	<!-- db.properties -->
	<bean class="org.springframework.beans.factory.config.PropertyPlaceholderConfigurer">
		<property name="locations">
			<list>
			<!-- src/main/resources/~ -->
				<value>classpath:mybatis/db.properties</value>
			</list>
		</property>
	</bean>	
	
	<!-- dataSource -->
	<bean id="dataSource" class="org.apache.commons.dbcp.BasicDataSource">
		<property name="driverClassName" value="${driverClassName}"/>
		<property name="url" value="${url}"/>
		<property name="username" value="${username}"/>
		<property name="password" value="${password}"/>
	</bean>
	
	<!-- sqlSessionFactory -->
	<bean id="sqlSessionFactory" class="org.mybatis.spring.SqlSessionFactoryBean">
		<property name="dataSource" ref="dataSource"/>
		<!-- mapper 설정 config.xml 위치 -->
		<!-- src/main/webapp/~ -->
		<property name="configLocation" value="classpath:/sqls/config.xml"/>
	</bean>
	
	<!-- sqlSessionTemplate -->
	<bean id="sqlSessionTemplate" class="org.mybatis.spring.SqlSessionTemplate">
		<constructor-arg ref="sqlSessionFactory"/>
	</bean>
</beans>

```

- src/main/resources/sqls/config.xml 수정

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE configuration
  PUBLIC "-//mybatis.org//DTD Config 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-config.dtd">
  
  <configuration>
  	<!-- 별칭 -->
   	<typeAliases>
   		<typeAlias type="com.zero.mvc.domain.model.BoardVO" alias="board"/>
  	</typeAliases>
  	<!-- 매퍼위치들 -->
   	<mappers>
   		<mapper resource="mybatis/boardMapper.xml"/>
  	</mappers> 
  </configuration>
```

- src/main/resources/mybatis/~mapper.xml 작성

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
  PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
  "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
  <mapper namespace="board">
  	<resultMap type="boardDto" id="boardMap">
  	   <result property="bNo" column="BNO"/>
  	   <result property="title" column="TITLE"/>
  	   <result property="content" column="CONTENT"/>
  	   <result property="writer" column="WRITER"/>
  	   <result property="regDate" column="REGDATE"/>
  	   <result property="viewCnt" column="VIEWCNT"/>
  	</resultMap>
  	<insert id="insert" parameterType="boardDto">
  		INSERT INTO TBL_BOARD(TITLE,CONTENT,WRITER)
  		VALUES(#{title},#{content},#{writer})
  	</insert>
  	<select id="selectOne" resultType="boardDto">
  		SELECT BNO,TITLE,CONTENT,WRITER,REGDATE,VIEWCNT
  		FROM TBL_BOARD
  		WHERE BNO=#{bNo}
  	</select>
  	<select id="selectAll" resultType="boardDto">
  		<![CDATA[
  			SELECT BNO,TITLE,CONTENT,WRITER,REGDATE,VIEWCNT
  			FROM TBL_BOARD
  			WHERE BNO>0
  			ORDER BY BNO DESC,REGDATE DESC
  		]]>
  	</select>
  	<update id="update" parameterType="boardDto">
  		UPDATE TBL_BOARD
  		SET TITLE=#{title},CONTENT=#{content}
  		WHERE BNO=#{bNo}
  	</update>
  	<delete id="delete" parameterType="int">
  		DELETE FROM TBL_BOARD
  		WHERE BNO=#{bNo}
  	</delete>
  </mapper>
```
- dao 작성
```java
package com.zero.mvc.domain.dao;

import java.util.List;

import com.zero.mvc.domain.model.BoardVO;

public interface BoardDao {
	
	static final String NAMESPACE="board.";
	
	public void insert(BoardVO board) throws Exception;
	public BoardVO selectOne(Integer bNo) throws Exception;
	public List<BoardVO> selectAll() throws Exception;
	public void update(BoardVO board) throws Exception;
	public void delete(Integer bNo) throws Exception;
}


```

```java
package com.zero.mvc.domain.dao;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.stereotype.Repository;

import com.zero.mvc.domain.model.BoardVO;


@Repository
public class BoardDaoImpl implements BoardDao{

	@Autowired
	private SqlSessionTemplate session;
	
	@Override
	public void insert(BoardVO board)  throws Exception{
		// TODO Auto-generated method stub
		session.insert(NAMESPACE+"insert",board);
	}

	@Override
	public BoardVO selectOne(Integer bNo)  throws Exception{
		// TODO Auto-generated method stub
		return session.selectOne(NAMESPACE+"selectOne",bNo);
	}

	@Override
	public List<BoardVO> selectAll() throws Exception {
		// TODO Auto-generated method stub
		return session.selectList(NAMESPACE+"selectList");
	}

	@Override
	public void update(BoardVO board) throws Exception {
		// TODO Auto-generated method stub
		session.update(NAMESPACE+"update",board);
	}

	@Override
	public void delete(Integer bNo) throws Exception {
		// TODO Auto-generated method stub
		session.delete(NAMESPACE+"delete",bNo);
	}

}

```

➕`마이바티스 LocalDateTime 연동하기
```
java.lang.AbstractMethodError: Receiver class org.apache.commons.dbcp.DelegatingResultSet does not define or inherit an implementation of the resolved method 'abstract java.lang.Object getObject(java.lang.String, java.lang.Class)' of interface java.sql.ResultSet.
	at org.apache.ibatis.type.LocalDateTimeTypeHandler.getNullableResult(LocalDateTimeTypeHandler.java:38)
	at org.apache.ibatis.type.LocalDateTimeTypeHandler.getNullableResult(LocalDateTimeTypeHandler.java:28)
	at org.apache.ibatis.type.BaseTypeHandler.getResult(BaseTypeHandler.java:85)
	at org.apache.ibatis.executor.resultset.DefaultResultSetHandler.applyAutomaticMappings(DefaultResultSetHandler.java:561)
	at org.apache.ibatis.executor.resultset.DefaultResultSetHandler.getRowValue(DefaultResultSetHandler.java:403)
	at org.apache.ibatis.executor.resultset.DefaultResultSetHandler.handleRowValuesForSimpleResultMap(DefaultResultSetHandler.java:355)
	at org.apache.ibatis.executor.resultset.DefaultResultSetHandler.handleRowValues(DefaultResultSetHandler.java:329)
	at org.apache.ibatis.executor.resultset.DefaultResultSetHandler.handleResultSet(DefaultResultSetHandler.java:302)
	at org.apache.ibatis.executor.resultset.DefaultResultSetHandler.handleResultSets(DefaultResultSetHandler.java:195)
	at org.apache.ibatis.executor.statement.PreparedStatementHandler.query(PreparedStatementHandler.java:65)
	at org.apache.ibatis.executor.statement.RoutingStatementHandler.query(RoutingStatementHandler.java:79)
	at org.apache.ibatis.executor.SimpleExecutor.doQuery(SimpleExecutor.java:63)
	at org.apache.ibatis.executor.BaseExecutor.queryFromDatabase(BaseExecutor.java:325)
	at org.apache.ibatis.executor.BaseExecutor.query(BaseExecutor.java:156)
	at org.apache.ibatis.executor.CachingExecutor.query(CachingExecutor.java:109)
	at org.apache.ibatis.executor.CachingExecutor.query(CachingExecutor.java:89)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:151)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:145)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:140)
	at org.apache.ibatis.session.defaults.DefaultSqlSession.selectList(DefaultSqlSession.java:135)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.mybatis.spring.SqlSessionTemplate$SqlSessionInterceptor.invoke(SqlSessionTemplate.java:427)
	at com.sun.proxy.$Proxy17.selectList(Unknown Source)
	at org.mybatis.spring.SqlSessionTemplate.selectList(SqlSessionTemplate.java:216)
	at com.zero.mvc.domain.dao.BoardDaoImpl.selectAll(BoardDaoImpl.java:33)
	at com.zero.mvc.domain.dao.BoardDaoTest.selectListTest(BoardDaoTest.java:40)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
	at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
	at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
	at java.base/java.lang.reflect.Method.invoke(Method.java:566)
	at org.junit.runners.model.FrameworkMethod$1.runReflectiveCall(FrameworkMethod.java:59)
	at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
	at org.junit.runners.model.FrameworkMethod.invokeExplosively(FrameworkMethod.java:56)
	at org.junit.internal.runners.statements.InvokeMethod.evaluate(InvokeMethod.java:17)
	at org.springframework.test.context.junit4.statements.RunBeforeTestMethodCallbacks.evaluate(RunBeforeTestMethodCallbacks.java:73)
	at org.springframework.test.context.junit4.statements.RunAfterTestMethodCallbacks.evaluate(RunAfterTestMethodCallbacks.java:82)
	at org.springframework.test.context.junit4.statements.SpringRepeat.evaluate(SpringRepeat.java:73)
	at org.junit.runners.ParentRunner.runLeaf(ParentRunner.java:366)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:224)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.runChild(SpringJUnit4ClassRunner.java:83)
	at org.junit.runners.ParentRunner$4.run(ParentRunner.java:331)
	at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:79)
	at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:329)
	at org.junit.runners.ParentRunner.access$100(ParentRunner.java:66)
	at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:293)
	at org.springframework.test.context.junit4.statements.RunBeforeTestClassCallbacks.evaluate(RunBeforeTestClassCallbacks.java:61)
	at org.springframework.test.context.junit4.statements.RunAfterTestClassCallbacks.evaluate(RunAfterTestClassCallbacks.java:68)
	at org.junit.runners.ParentRunner$3.evaluate(ParentRunner.java:306)
	at org.junit.runners.ParentRunner.run(ParentRunner.java:413)
	at org.springframework.test.context.junit4.SpringJUnit4ClassRunner.run(SpringJUnit4ClassRunner.java:163)
	at org.eclipse.jdt.internal.junit4.runner.JUnit4TestReference.run(JUnit4TestReference.java:93)
	at org.eclipse.jdt.internal.junit.runner.TestExecution.run(TestExecution.java:40)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:529)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.runTests(RemoteTestRunner.java:756)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.run(RemoteTestRunner.java:452)
	at org.eclipse.jdt.internal.junit.runner.RemoteTestRunner.main(RemoteTestRunner.java:210)

```

▶https://github.com/mybatis/typehandlers-jsr310
https://mrkn.tistory.com/368

마이바티스 LocalDateTime 연동이 안되어 생긴 문제 가능성
▶mybatis-typehandlers-jsr310 의존성 추가후 typeHandlers 추가(config.xml 순서 참고)

➕ AbstractError : 스프링, mybatis, mybatis-spring 버전을 낮춰주면 해결되는 것 같다! 너무 높은 버전으로 인한 문제!
```xml
	<properties>
		<java-version>1.8</java-version>
		<org.springframework-version>3.1.1.RELEASE</org.springframework-version>
		<org.aspectj-version>1.6.10</org.aspectj-version>
		<org.slf4j-version>1.6.6</org.slf4j-version>
	</properties>
	<dependencies>

(중략)
<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis -->
		<dependency>
    		<groupId>org.mybatis</groupId>
    		<artifactId>mybatis</artifactId>
    		<version>3.2.8</version>
		</dependency>
		<!-- https://mvnrepository.com/artifact/org.mybatis/mybatis-spring -->
		<dependency>
    		<groupId>org.mybatis</groupId>
    		<artifactId>mybatis-spring</artifactId>
    		<version>1.2.2</version>
		</dependency>
```
다
시 dao 테스트!
```java
package com.zero.mvc.domain.dao;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zero.mvc.domain.model.BoardVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/**/*.xml"})
public class BoardDaoTest {

	@Autowired
	private BoardDao dao;
	
	private static Logger logger=
			LoggerFactory.getLogger(BoardDaoTest.class);
	
	@Test
	public void insertTest() throws Exception{
		BoardVO board=new BoardVO();
		board.setTitle("제목3");
		board.setContent("내용3");
		board.setWriter("작가3");
		System.out.println("board: "+board);
		dao.insert(board);
	}
	
	@Test
	public void selectOneTest() throws Exception {
		logger.info("selectOne: "+dao.selectOne(1));
	}
	
	@Test
	public void selectListTest() throws Exception {
		logger.info("selectList: {}", dao.selectAll());
	}
	

	
	@Test
	public void testUpdate() throws Exception{
		BoardVO board=new BoardVO();
		board.setBNo(1);
		board.setTitle("수정된 제목");
		board.setContent("수정된 내용");
		dao.update(board);
	}
	
	@Test
	public void tes
tDelete() throws Exception{
		dao.delete(2);
	}
}

```
```
INFO : com.zero.mvc.domain.dao.BoardDaoTest - selectList: [BoardVO(bNo=7, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:24:58, viewCnt=0), BoardVO(bNo=6, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:19:06, viewCnt=0), BoardVO(bNo=5, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:10:48, viewCnt=0), BoardVO(bNo=4, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:07:49, viewCnt=0), BoardVO(bNo=3, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:03:19, viewCnt=0), BoardVO(bNo=1, title=수정된 제목, content=수정된 내용, writer=USER00, regDate=2021-12-26T12:58:57, viewCnt=0)]
INFO : com.zero.mvc.domain.dao.BoardDaoTest - selectOne: BoardVO(bNo=1, title=수정된 제목, content=수정된 내용, writer=USER00, regDate=2021-12-26T12:58:57, viewCnt=0)
board: BoardVO(bNo=null, title=제목3, content=내용3, writer=작가3, regDate=null, viewCnt=
0)

```

### 05-3. 비즈니스 계층 구현

고객의 요구사항이 반영되는 영역

`왜 비즈니스 계층이 존재해야 하는지?`

- 컨트롤러와 dao 사이의 접착제 역할
- 고객마다 다른 부분을 처리할 수 있는 완충장치 역할
- 외부 호출이 영속 계층에 종속적인 상황을 막아줌(여러 dao 작업이 일어날 수도 있는 상황 존재)
- 로직을 데이터베이스에 무관하게 처리할 수 있는 완충영역 역할
- 컨트롤러의 작업을 분업

boardService, boardServiceImpl 작성
```java
package com.zero.mvc.service;

import java.util.List;

import com.zero.mvc.domain.model.BoardVO;

public interface BoardService {
	public void register(BoardVO board) throws Exception;
	public BoardVO read(Integer bNo) throws Exception;
	public List<BoardVO> readAll() throws Exception;
	public void modify(BoardVO board) throws Exception;
	public void remove(Integer bNo) throws Except
ion;
}

```
```java
package com.zero.mvc.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.zero.mvc.domain.dao.BoardDao;
import com.zero.mvc.domain.model.BoardVO;

@Service
public class BoardServiceImpl implements BoardService{

	@Autowired
	private BoardDao dao;
	
	@Override
	public void register(BoardVO board) throws Exception {
		// TODO Auto-generated method stub
		dao.insert(board);
	}

	@Override
	public BoardVO read(Integer bNo) throws Exception {
		// TODO Auto-generated method stub
		return dao.selectOne(bNo);
	}

	@Override
	public List<BoardVO> readAll() throws Exception {
		// TODO Auto-generated method stub
		return dao.selectAll();
	}

	@Override
	public void modify(BoardVO board) throws Exception {
		// TODO Auto-generated method stub
		dao.update(board);
	}

	@Override
	public void remove(Integer bNo) throws Exception {
		// TODO Auto-generated
 method stub
		dao.delete(bNo);
	}

}

```
서비스단 테스트
```java
package com.zero.mvc.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.zero.mvc.domain.model.BoardVO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations= {"file:src/main/webapp/WEB-INF/spring/**/*.xml"})
public class BoardServiceTest {
	
	@Autowired
	private BoardService service;
	
	private final Logger logger=
			LoggerFactory.getLogger(BoardServiceTest.class);
	
	@Test
	public void readTest() throws Exception{
		logger.info("read: "+service.read(1));
	}
	
	@Test
	public void readAllTest() throws Exception{
		logger.info("readAll: "+service.readAll());
	}
	
	@Test
	public void modifyTest() throws Exception{
		BoardVO board=service.read(1);
		board.setTitle("수정수정제목1");
		
		service.modify(board);
		
		logger.info("after modified: {}",service.readAll());
	}
	
	@Test
	public void removeTest() throws Exception{
		service.remove(3);
		logger.info("af
ter remove: {}",service.readAll());
	}
}

```
```
INFO : com.zero.mvc.service.BoardServiceTest - readAll: [BoardVO(bNo=8, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:26:52, viewCnt=0), BoardVO(bNo=7, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:24:58, viewCnt=0), BoardVO(bNo=6, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:19:06, viewCnt=0), BoardVO(bNo=5, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:10:48, viewCnt=0), BoardVO(bNo=4, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:07:49, viewCnt=0), BoardVO(bNo=3, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:03:19, viewCnt=0), BoardVO(bNo=1, title=수정된 제목, content=수정된 내용, writer=USER00, regDate=2021-12-26T12:58:57, viewCnt=0)]
INFO : com.zero.mvc.service.BoardServiceTest - read: BoardVO(bNo=1, title=수정된 제목, content=수정된 내용, writer=USER00, regDate=2021-12-26T12:58:57, viewCnt=0)
INFO : com.zero.mvc.service.BoardServiceTest - after remove: [BoardVO(bNo=8, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:26:52, viewCnt=0), BoardVO(bNo=7, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:24:58, viewCnt=0), BoardVO(bNo=6, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:19:06, viewCnt=0), BoardVO(bNo=5, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:10:48, viewCnt=0), BoardVO(bNo=4, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:07:49, viewCnt=0), BoardVO(bNo=1, title=수정된 제목, content=수정된 내용, writer=USER00, regDate=2021-12-26T12:58:57, viewCnt=0)]
INFO : com.zero.mvc.service.BoardServiceTest - after modified: [BoardVO(bNo=8, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:26:52, viewCnt=0), BoardVO(bNo=7, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:24:58, viewCnt=0), BoardVO(bNo=6, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:19:06, viewCnt=0), BoardVO(bNo=5, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:10:48, viewCnt=0), BoardVO(bNo=4, title=제목3, content=내용3, writer=작가3, regDate=2021-12-26T13:07:49, viewCnt=0), BoardVO(bNo=1, title=수정수정제목1, content=수정된 내용, writer=USER00, regDate=2021-12-26T12:58:57, viewCnt=0)]

```

### 05-4. 컨트롤러 및 프레젠테이션 계층 구현

`컨트롤러에서 고민해야할 부분`

- 공통적인 URI 경로 & 기능몇 URI 경로
- 각 URI에 대한 호출 방식(GET/ POST)
- 결과 처리와 리다이렉트 방식의 페이지 결정
- 예외 페이지

|방식  |URI  |설명|
|--|--|--|
|GET  |/board/register  |게시물 등록 포맷 페이지|
|POST|/board/register|게시물 등록|
|GET|/board/read?bno=xxx|특정 번호의 게시물 조회|
|GET|/board/mod?bno=xxx|게시물 수정화면 포맷 페이지|
|POST|/board/mod|게시물 수정|
|POST|/board/remove|게시물 삭제|
|GET|/board/list|게시물의 목록 확인|

- 특별한 경우가 아니라면 DTO 클래스를 파라미터로 활용하는 것이 편리
- 바인딩: 들어온 요청이 자동으로 파라미터로 지정한 클래스의 객체 속성값으로 처리되는 것
- Model 객체: 해당 메서드에서 뷰에 필요한 데이터를 전달하는 용도로 사용됨

(1) 게시물 등록 포맷 페이지
```java
package com.zero.mvc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.zero.mvc.service.BoardService;

@Controller
@RequestMapping("/board/*")
public class BoardController {

	private static Logger logger=
			LoggerFactory.getLogger(BoardController.class);
	
	@Autowired
	private BoardService service;
	
	//등록 포맷 페이지 리턴
	@RequestMapping(value="/register", method=RequestMethod.GET)
	public String registerFormatPage() {
		return "/boar
d/register";
	}
	
}

```
```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/header.jsp" %>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<form role="form" method="post" action="/board/register">
		<div class="box-body">
			<div class="form-group">
				<label for="exampleInputEmail1">Title</label>
				<input type="text" name="title" class="form-control" placeholder="Enter Title"/>
			</div>
			<div class="form-group">
				<label for="exampleInputPassword1">Content</label>
				<textarea class="form-control" name="content" rows="3"
				placeholder="Enter..."></textarea>
			</div>
			<div class="form-group">
				<label for="exampleInputEmail1">Title</label>
				<input type="text" name="writer" class="form-control" placeholder="Enter Writer"/>
			</div>
		</div>
		<div class="box-footer">
			<button type="submit" class="btn btn-primary">Submit</button>
		</div>
	</form>
	<%@ include file="../include/foot
er.jsp" %>
</body>
</html>
```
register.jsp에서 input 포맷들에서 받은 값은 컨트롤러로 전달될 것(post)로!
단, 단순히 페이지를 반환해주게 된다면, 동일글로 게시물 목록을 차지하게 될 수 있기 때문에 가장 단순하게 `다른 페이지로 리다이렉트` 해주는 것!!을 진행해볼 것!

```java
//게시물 등록
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String registerArticle(Model model,BoardVO board) {
		int insertRes=0;
		
		try {
			insertRes=service.register(board);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(insertRes>0) {
			//삽입 성공
			model.addAttribute("msg", "success");
		}else {
			//삽입실패
			model.addAttribute("msg", "failed");
		}
		
		
		return "redirect: /bo
ard/listAll";
	}
```
단, msg 객체를 숨기기 위해서는 RedirectAttributes를 활용해주어도 좋다!!

```java
	//게시물 등록
	@RequestMapping(value="/register", method=RequestMethod.POST)
	public String registerArticle(RedirectAttributes rttr,BoardVO board) {
		int insertRes=0;
		
		try {
			insertRes=service.register(board);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		// 리다이렉트시 정보 전달
		if(insertRes>0) {
			//삽입 성공
			rttr.addFlashAttribute("msg", "success");
		}else {
			//삽입실패
			rttr.addFlashAttribute("msg", "failed");
		}
		
		
		return "redirect: /board/listAll
";
	}
```

- 전체 조회 기능
```java
//전체조회
	@RequestMapping(value="/listAll",method=RequestMethod.GET)
	public String listAll(Model model) {
		List<BoardVO> articles=new ArrayList<>();
		
		try {
			articles=service.readAll();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		model.addAttribute("articles", articles);
		
		return "/board/listAll";
	}

```

```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ include file="../include/header.jsp" %>  
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<table class="table table-bordered">
		<thead>
			<tr>
				<th style="width:10px">BNO</th>
				<th>TITLE</th>
				<th>WRITER</th>
				<th>REGDATE</th>
				<th style="width:40px">VIEWCNT</th>
			</tr>
		</thead>
		<tbody>
			<c:choose>
				<c:when test="${empty articles }">
					<tr>
						<td colspan="5">
						---등록된 게시글이 없습니다---
						</td>
					</tr>
				</c:when>
				<c:otherwise>
					<c:forEach items="${articles }" var="item">
						<tr>
							<fmt:parseDate value="${item.regDate}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateTime" type="both"/>
							<td>${item.bNo }</td>
							<td><a href="<%=request.getContextPath()%>/board/read?bno=${item.bNo}" title="${item.bNo} 게시글 보기" target="_blank">${item.title }</a></td>
							<td>${item.writer }</td>
							<td><fmt:formatDate pattern="yyyy-MM-dd hh:mm:ss aa" value="${parsedDateTime }"/></td>
							<td>${item.viewCnt}</td>
						</tr>
					</c:forEach>
				</c:otherwise>
			</c:choose>
		</tbody>	
	</table>
	<%@ include file="../include/footer.jsp" %>
	<script>
		var result='${msg}';
		
		if(result=="success"){
			alert(`처리가 완료되었습니다.`);
		}else{
			alert(`게시글 조회/등록 과정에 문제가 발생했습니다`);
		}
	</script>
</body>
</html>
```
➕ jdk8 이상에서 jstl로 LocalDateTime 표시하기
```
<fmt:parseDate value="${item.regDate}" pattern="yyyy-MM-dd'T'HH:mm:ss" var="parsedDateTime" type="both"/>
<fmt:formatDate pattern="yyyy-MM-dd hh:mm:ss aa" value="${parsedDateTime }"/>
```

날짜 형태로 변환해준후, 형식화해주기

- 특정 게시글 상세보기 (/board/read?bno=xxx)

```java
	//특정글 상세보기
	@RequestMapping(value="/read", method=RequestMethod.GET)
	public String read(@RequestPara, Integer bNo,Model model,RedirectAttributes rttr) {
		
		BoardVO target=new BoardVO();
		
		try {
			target=service.read(bNo);
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(target==null) {
			//존재하지 않는 경우
			rttr.addFlashAttribute("msg","failed");
			return "redirect: /board/listAll";
		}
		
		//존재하는 경우
		model.addAttribute("article",
target);
		
		return "/board/read";
	}
	
```

```html
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../include/header.jsp" %> 
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<script>
	$(document).ready(function(){
		var formObj=$("form[role='form']");
		
		console.log("form요소: "+formObj);
		
		$(".btn-warning").on("click",function(){
			formObj.attr("action","/board/modify");
			formObj.attr("method","get");
			formObj.submit();
		});
		
		$(".btn-danger").on("click",function(){
			formObj.attr("action","/board/remove");
			formObj.submit();
		});
		
		$(".btn-primary").on("click",function(){
		
			self.location="/board/listAll";
		});
		
</script>
<body>
	<form role="form" method="POST">
		<input type="hidden" name="bno" value="${article.bNo}"/>
	</form>
	<div class="box-body">
		<div class="form-group">
			<label for="exampleInputEmail1">Title</label>
			<input type="text" name="title" class="form-control" placeholder="Enter Title"/>
		</div>
		<div class="form-group">
			<label for="exampleInputPassword1">Content</label>
			<textarea class="form-control" name="content" rows="3"
				placeholder="Enter..."></textarea>
		</div>
		<div class="form-group">
			<label for="exampleInputEmail1">Title</label>
			<input type="text" name="writer" class="form-control" placeholder="Enter Writer"/>
		</div>
	</div>
	<div class="box-footer">
		<button type="submit" class="btn btn-warning">수정</button>
		<button type="submit" class="btn btn-danger">삭제</button>
		<button type="submit" class="btn btn-primary">목록</button>
	</div>
	<%@ incl
ude file="../include/footer.jsp" %>	
</body>
</html>
```

