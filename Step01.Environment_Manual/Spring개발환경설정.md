# 스프링 개발 환경 설정 매뉴얼 정리

이전까지는 자바8을 이용했지만, 자바11을 새로이 도전하고자 몇가지를 변경하여 정리하도록 하였다

## 1. JDK 설치

기존에는 jdk11을 사용했기 때문에 자바 11을 사용하고자 버전을 달리하여 설치해보도록 하자
(아마존 사랑합니다❤ 무료 ❤)
출처 : https://docs.aws.amazon.com/ko_kr/corretto/latest/corretto-11-ug/windows-7-install.html

현재 저는 윈도우를 사용하고 있습니다

1. msi, zip 두 가지 타입이 있는데 저는 msi로 받아서 설치하도록 하였습니다
2. 설치 단계별로 진행하되, 설치 위치를 꼭 기억해두자(환경변수 설정을 위해서) ◀저는 `C:\Program Files\Amazon Corretto\` 라고 떴습니다
3. 위치 확인하시고, 버전도 확인해주세요. 그런데 윈도우에서 아마존 jdk는 설치되면서 자동으로 환경변수 설정이 되고, 기존 jdk8과 헷갈리지 않게 확인해주시면 될 것 같습니다.
4. cmd에서 java --version으로 마지막 확인해주셨을때, 아래처럼 11.x.x~~ 확인되면 끝!

```
> java --version
openjdk 11.0.13 2021-10-19 LTS
OpenJDK Runtime Environment Corretto-11.0.13.8.1 (build 11.0.13+8-LTS)
OpenJDK 64-Bit Server VM Corretto-11.0.13.8.1 (build 11.0.13+8-LTS, mixed mode)
```

 ## 2. STS(Spring Tool Suite) 설치
 
 1. https://github.com/spring-projects/toolsuite-distribution/wiki/Spring-Tool-Suite-3 에서 알맞은 버전 선택
 2. 버전을 선택해서 다운로드 후, 압축해제
 3. STS3.exe를 실행해주면 끝!
