
# 스프링 MVC 패턴

[MVC 패턴이란?](https://github.com/hy6219/TIL/blob/main/JSP%20Servlet/MVCPattern/%5Bjsp%20servlet%5DMVC%ED%8C%A8%ED%84%B4.md)

▶ 요약: 비즈니스 로직과 프레젠테이션 간의 작업을 중재해주는 패턴

- Model: 비즈니스 로직+엔티티
- Controller: 모델과 뷰 사이의 중재자
- View: 서버로부터의 응답을 클라이언트에게 보여주는 부분


## 01. 스프링에서의 MVC패턴2의 특징, `FrontController`

`왜 프론트컨트롤러 패턴을 도입했는지?`
![스프링 MVC 패턴2-프론트 컨트롤러 패턴](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMvc2Front.jpg?raw=true)
▶ (1) 각 컨트롤러 사이의 중복적인 코드
(2) 개발자의 개발 패턴 차이 등등..

✨✨가장 중요한 특징 `위임 delegation`

- 전체 로직의 일부만 컨트롤러가 처리
- 모든 흐름의 제어는 앞쪽의 프론트 컨트롤러가 담당!!
▶ 개발자는 전체 로직의 일부분만 처리하면 되기 때문에 코드작성이 줄어들고, 보다 규격화된 코드를 작성하게 됨

✨✨✨MVC 패턴 구조 플로우 ✨✨✨
![스프링 MVC 패턴 구조](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMvcPattern.jpg?raw=true)

1. 사용자의 모든 요청이 프론트 컨트롤러에게 전달됨
2. 전달된 요청은 적절한 컨트롤러를 찾아서 호출하게 됨
3. 컨트롤러는 적절한 서비스 객체를 찾아서 호출
4. 서비스는 데이터베이스의 작업을 담당하는 DAO(Data Access Object)를 이용해서 원하는 데이터를 요청
5. DAO 객체는 MyBatis를 이용하는 mapper를 통해서 원하는 작업을 수행하고, 이 과정에서 DTO(Data Transfer Object)를 받게 됨
6. 서비스가 처리한 데이터를 컨트롤러에서 받게 되면 컨트롤러는 다시 스프링 MVC 쪽으로 데이터를 전달
7. 프론트 컨트롤러는 뷰에게 그에 해당되는 응답을 뷰에게 넘겨줌

✨ 과거 : ~.do로 활용
▶ 변화: 특정 URI 경로 활용

✨ 스프링 MVC 패턴2 특징!!
(1) 개발자와 웹 퍼블리셔의 영역 분리
(2) 컨트롤러의 URI를 통해 뷰를 제어 ▶ 뷰의 교체 및 변경과 같은 유지보수에 용이

|스프링 MVC가 처리해주는 작업|개발자가 직접해야하는 작업  |
|--|--|
|URI를 분석해서 적절한 컨트롤러를 매칭  |특정 URI에 동작하는 컨트롤러 설계  |
|컨트롤러에 필요한 메서드 호출|서비스, DAO 객체 생성
|컨트롤러 결과 데이터를 뷰로 전달|컨트롤러 내에 원하는 결과를 메서드로 설계
|적절한 뷰를 찾는 작업|뷰에서 전달받은 데이터 출력

## 02. 스프링 MVC의 컨트롤러
`일종의 부품`

`스프링 MVC의 컨트롤러는 무엇을 처리해주는지?`

1. 파라미터 수집
- 사용자의 요청에 필요한 데이터를 추출하고, VO(Value Object)/ DTO(Data Transfer Object) 로 변환하는 파라미터 수집을 자동으로 처리해줌

2. 애노테이션을 통한 간편 설정
3. 로직집중 ◀  by 2
4. 테스트의 편리함

`기존 자바 코드에 비해 다른 점`
-  상속 및 인터페이스를 구현하지 않아도 됨
- 메서드의 파라미터, 리턴타입에 대한 제약x
- 스프링 MVC가 제공하는 유용한 클래스들 존재


## 03. `servlet-context.xml`
- 스프링 MVC 관련 설정을 분리하기 위한 파일

✨✨✨ 중요한 부분 ✨✨✨

```xml
	<!-- Enables the Spring MVC @Controller programming model -->
	<annotation-driven />

	<!-- Handles HTTP GET requests for /resources/** by efficiently serving up static resources in the ${webappRoot}/resources directory -->
	<resources mapping="/resources/**" location="/resources/" />

	<!-- Resolves views selected for rendering by @Controllers to .jsp resources in the /WEB-INF/views directory -->
	<beans:bean class="org.springframework.web.servlet.view.InternalResourceViewResolver">
		<beans:property name="prefix" value="/WEB-INF/views/" />
		<beans:property name="suffix" value=".jsp" />
	</beans:bean>
	
	<context:component-scan base-package="com.test.jisoo" />
	
```
(1) `<annotation-driven>	` : 클래스 선언에 애노테이션을 이용해서 컨트롤러를 작성할 수 있다는 것!

(2) `InternalResourceViewResolver` 빈 : 

- `/WEB-INF/views/` : 뷰 경로
▶ WEB-INF 자체가 브라우저에서 직접 접근하기 어렵기 때문에 스프링 MVC패턴2에 더욱이 부합!

- `.jsp` : jsp 확장자의 뷰페이지를 제공

(3)`<resources>` : css,js, 이미지와 같은 고정된 자원들의 위치

(4) `<component-scan>`: base-package 속성값에 해당하는 패키지 내부의 클래스들에 대해서 애노테이션을 인식하여 빈 객체를 인식

## 04. Spring MVC에서 주로 사용하는 애너테이션의 종류

|애노테이션  |설명  |사용 |
|--|--|--|
|@Controller  |스프링 MVC의 컨트롤러 객체[@RestController: REST API 구축시 사용]  |클래스|
|@Repository|DAO객체|클래스|
|@Service|서비스 객체|클래스|
|@RequestMapping|특정 URI에 매칭되는 클래스(예:컨트롤러)/메서드(예:컨트롤러 내부의 특정 메서드)임을 명시|클래스,메서드|
|@RequestParam|요청 파라미터|파라미터|
|@PathVariable|현재 URI에서 원하는 정보추출시 사용,값이 변동되는 변수같은 존재(ex:/{userId})|파라미터|
|@CookieValue|현재 사용자의 쿠키가 존재하는 경우, 쿠키의 이름을 이용해서 쿠키의 값을 호출|파라미터|
|@ModelAttribute|자동으로 해당 객체를 뷰까지 전달하도록 함|메서드, 파라미터|
|@SessionAttribute|세션상에서 모델의 정보를 유지하고 싶은 경우 사용| 클래스
|@InitBinder|파라미터를 수집해서 객체로 만들 경우 커스터마이징|메서드|
|@ResponseBody|HTTP의 응답메시지로 전송|메서드, 리턴타입|
|@RequestBody|요청문자열이 그대로 파라미터로 전달(post,put일때 사용됨)|파라미터|

https://goodgid.github.io/Spring-MVC-InitBinder/
https://2ham-s.tistory.com/310


▶ @InitBinder: 스프링 밸리데이터 사용시, 검증이 필요한 객체를 가져오기 전에 수행할 메서드를 지정[with WebDataBinder]

## 05. 컨트롤러의 리턴타입

1. void
2. String
3. 만들어진 결과 데이터 전달
4. +리다이렉트
5. +json

- void
▶ 페이지가 리턴되지 않음
```java
package com.test.jisoo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class VoidController {

	private static final Logger logger=
			LoggerFactory.getLogger(VoidController.class);
	
	@RequestMapping("doA")
	public void doA() {
		logger.info("doA called...");
	}
	
}

```
![스프링 MVC 컨트롤러-void 리턴타입](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/%EC%8A%A4%ED%94%84%EB%A7%81MVC%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC_%EB%A6%AC%ED%84%B4%ED%83%80%EC%9E%85_void.PNG?raw=true)

![스프링 MVC 컨트롤러-void 리턴타입2](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/%EC%8A%A4%ED%94%84%EB%A7%81MVC%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC_%EB%A6%AC%ED%84%B4%ED%83%80%EC%9E%85_void2.PNG?raw=true)

- String
 ▶페이지 리턴(~.jsp)
```java
package com.test.jisoo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class StringController {

	private static final Logger logger=
			LoggerFactory.getLogger(StringController.class);
	
	@RequestMapping("doC")
	public String doC(@ModelAttribute("msg") String msg) {
		logger.info("doC called...");
		
		return "result";
	}
	
}

```
```java
<%@ page language="java" contentType="text/html; charset=EUC-KR"
    pageEncoding="UTF-8"%>
<% request.setCharacterEncoding("utf-8"); %>
<% response.setContentType("text/html;charset=utf-8"); %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<span>Hello ${msg}</span>
</body>
</html>
```
![스프링 MVC 컨트롤러 - String 리턴타입](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/%EC%8A%A4%ED%94%84%EB%A7%81MVC%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC_%EB%A6%AC%ED%84%B4%ED%83%80%EC%9E%85_string.PNG?raw=true)

![스프링 MVC 컨트롤러 - String 리턴타입2](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/%EC%8A%A4%ED%94%84%EB%A7%81MVC%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC_%EB%A6%AC%ED%84%B4%ED%83%80%EC%9E%85_string2.PNG?raw=true)

- 만들어진 결과 데이터 전달
 ▶ 스프링 MVC의 Model 객체 활용
```java
package com.test.jisoo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVO {
	private String name;
	private double price;
}
```
```java
package com.test.jisoo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import com.test.jisoo.model.ProductVO;

@Controller
public class ModelController {
	private static final Logger logger=
			LoggerFactory.getLogger(ModelController.class);
	
	@RequestMapping("doD")
	public String doD(Model model) {
		
		ProductVO productVo=new ProductVO("Sample product",10000);
		logger.info("doD");
		
		model.addAttribute("product",productVo);
		return "productDetail";
	}
}

```
```java
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<span>${product.name }</span>&nbsp;&nbsp;
	<span>${product.price }</span>
</body>
</html>
```
![스프링 MVC 컨트롤러- 로직상 만들어진 데이터를 전달해야 하는 경우](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/%EC%8A%A4%ED%94%84%EB%A7%81MVC%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC_%EB%A1%9C%EC%A7%81%EC%83%81_%EB%A7%8C%EB%93%A4%EC%96%B4%EC%A7%84%20%EB%8D%B0%EC%9D%B4%ED%84%B0%EB%A5%BC%20%EC%A0%84%EB%8B%AC%ED%95%B4%EC%95%BC%ED%95%98%EB%8A%94%20%EA%B2%BD%EC%9A%B0.PNG?raw=true)

- 리다이렉트를 해야하는 경우
(1) `redirect:`를 이용 ▶ 포워딩에는 `forward:` 사용
+`RedirectAttributes` : 리다이렉트 시점에 원하는 데이터를 임시로 추가해서 넘기는 작업을 수행할 수 있음
- addFlashAttribute: URI에 노출없이 데이터를 전달가능
```java
package com.test.jisoo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class RedirectController {

	private static final Logger logger=
			LoggerFactory.getLogger(RedirectController.class);
	
	
	@RequestMapping("doE")
	public String doE(RedirectAttributes rttr) {
		logger.info("doE called but redirect to doF...");
		
		
		rttr.addFlashAttribute("msg","This is Message!");
		
		return "redirect:/doF";
	}
	
	@RequestMapping("doF")
	public void doF(String msg) {
		logger.info("doF called.. msg: {}",msg);
	}
	
}

```
```java
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
	<span>${msg }</span>
</body>
</html>
```

![스프링 MVC 컨트롤러- 리다이렉트](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/%EC%8A%A4%ED%94%84%EB%A7%81%20MVC%20%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC_%EB%A6%AC%EB%8B%A4%EC%9D%B4%EB%A0%89%ED%8A%B8.gif?raw=true)

로그:
```
INFO : com.test.jisoo.controller.RedirectController - doF called.. msg: null
```
- json 데이터 다루기

1. jackson 의존성 추가


2. 컨트롤러 리턴타입을 `@ResponseBody`/`ResponseEntity<?>: HttpStatus도 함께 확인 가능`로 지정해주기

```java
package com.test.jisoo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.test.jisoo.model.ProductVO;

@Controller
public class JsonController {

	private static final Logger logger=
			LoggerFactory.getLogger(JsonController.class);
	
	@RequestMapping("/doJSON01")
	public ResponseEntity<ProductVO> doJSON01(){
		ProductVO product=new ProductVO("과자",2000);
		logger.info("response entity- vo: "+product);
		return new ResponseEntity<>(product,HttpStatus.OK);
	}
	
	@RequestMapping("/doJSON02")
	public @ResponseBody ProductVO doJSON02() {
		ProductVO product=new ProductVO("과자",2000);
		logger.info("response body- vo: "+product);
		return product;
	}
}

```
![스프링MVC컨트롤러-json_데이터 처리 with ResponseEntity](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/%EC%8A%A4%ED%94%84%EB%A7%81MVC%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC_json_ResponseEntity.PNG?raw=true)

![스프링MVC컨트롤러-json_데이터 처리 with ResponseBody](https://github.com/hy6219/Spring_Review/blob/main/Step02.About%20Spring/SpringMVC/%EC%8A%A4%ED%94%84%EB%A7%81MVC%EC%BB%A8%ED%8A%B8%EB%A1%A4%EB%9F%AC_json_ResponseBody.PNG?raw=true)

-------------
WAS 없이 컨트롤러를 테스트해보고 싶다면, javax.servlet(>servlet-api)를
javax.servlet>javax.servlet-api(version 3.1.0) 으로 변경해준다면 가능하다.[WAS 없이는 스프링 3.2부터 지원]
```xml
		<!-- Servlet -->
		<dependency>
			<groupId>javax.servlet</groupId>
			<artifactId>javax.servlet-api</artifactId>
			<version>3.1.0</version>
		</dependency>
```

