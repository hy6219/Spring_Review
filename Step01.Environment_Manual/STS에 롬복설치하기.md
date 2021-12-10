# STS에서도 롬복사용하기

스프링부트에서 정~말 편하게 사용했던 롬복을 스프링 레거시에서도 사용해보기 위해서 설치해주자

## 롬복 특징
- 보일러플레이트 코드를 자동으로 생성해줌

❤보일러 플레이트

- 많은 부분에서 거의 변경되지 않고 반복되는 코드

## 롬복 설치

- 다양한 설치 방법: https://the-dev.tistory.com/27
- 내가 선택한 방법: jar 파일 다운로드

1. https://projectlombok.org/download 에서 jar 파일 다운로드받기
2. STS 종료
3. jar 파일이 다운로드된 경로로 이동
4. lombok.jar 더블클릭 혹은 cmd/파워쉘에 java -jar lombok.jar 입력
5. installer에 스캔된 IDE를 선택하거나, `specify location` 선택 ▶ sts 선택
6. install/update 클릭
7. `Install Successful`이 나오면 `Quit Installer`를 눌러 종료!
8. sts에서 프로젝트의 pom.xml 밑에 버전에 맞는 의존성을 추가
https://mvnrepository.com/artifact/org.projectlombok/lombok/1.18.22
9. 아래처럼 롬복이 잘 적용되어 getter, setter, constructors, toString, equals, hashCode가 확인되는 것을 알 수 있다!
```java
package com.test.jisoo.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class User {

	private String name;
	private int age;
}
 ```
