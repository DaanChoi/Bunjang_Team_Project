# 번개장터 서버
번개장터 클론 서버 프로젝트입니다.

## ✨프로젝트 기간
2022.11.26 ~ 2022.12.09

<img width="80%" src="https://user-images.githubusercontent.com/104367835/213378011-df9aeb58-2d19-4e4c-af0a-e00339b8ec41.mp4"/>


## ✨번개장터 기능 파악
- 회원 로그인 / 회원가입할 수 있습니다.
- 회원은 상점명을 수정할 수 있습니다.
- 회원은 소개글을 수정할 수 있습니다.
- 회원은 상품 등록할 수 있습니다.
- 회원은 등록한 상품을 수정하여 관리할 수 있습니다.
- 회원은 관심있는 상품을 찜 할 수 있습니다.
- 회원 팔로잉을 할 수 있습니다.
- 회원은 상품을 구매할 수 있습니다.
- 회원은 다른 회원과 채팅을 할 수 있습니다.
- 특정 회원에 관련된 정보를 볼 수 있습니다.
- 회원은 당일에 등록된 추천 상품을 볼 수 있습니다.
- 회원은 카테고리 별로 상품을 볼 수 있습니다.
- 회원은 최신순 / 인기순 / 저가순 / 고가순으로 상품을 볼 수 있습니다.

## ✨스펙
- 클라우드 : AWS
- OS : Ubuntu 18.04
- 서버 : Nginx 1.14.0
- IDE : Intellij
- 프레임워크 : Spring Boot
- SDK : 11
- Proxy 서버 적용
- 도메인 주소에 SSL 적용

## ✨ERD 설계
URL : https://aquerytool.com/aquerymain/index/?rurl=927fb142-9ea0-4704-baa6-84f52e940f5b&
Password : 0g7o44

## ✨API 명세서 리스트업
### 도메인별로 작성
- /products
- /users
- /categories
- /chats
- /accounts
- /purchases

API 명세서 보기
-> https://docs.google.com/spreadsheets/d/1q-2XRtYMsKd2jgJ1K1cO_WJCWn2J6Vf3U0Yj7O7nhBU/edit#gid=0

## ✨API 주요 기능
### 회원 관련 싸이클
(소셜 회원가입 - > 소셜 로그인 - > 이름, 소개글 수정 - > 회원 탈퇴 - > 재 회원가입)
0. 회원 테이블과 소셜로그인 회원 테이블로 관리
1. 소셜 회원가입/로그인 : access token으로 네이버에 회원 정보 요청, 네이버로부터 받은 "회원 ID"를 PK로 사용하고 회원 테이블에서 "회원 ID"를 참조
2. 회원 탈퇴 시에는 status 값을 A(ctive)에서 D(elete)로 수정
3. 재 회원가입 시에 status 값을 D(elete)에서 A(ctive)로 수정 => status 값을 이용하여 DB를 효율적으로 관리

### 상품 관련 싸이클
(상품 조회 / 상품 등록 - > 상품 수정 - > 삭제)
1. 제목의 키워드로 검색하거나 특정 상품을 조회
2. 상품 등록 시에 상품 정보와 이미지들을 등록할 수 있도록 구현 => 이때 중간에 에러가 나서 이미지만 등록되고 상품은 등록되지 않은 경우를 방지하기 위해 Transation 어노테이션 사용
3. 상품 수정 시에 원하는 부분만 수정할 수 있음
4. 상품 삭제 시에는 status 값을 A(ctive)에서 D(elete)로 수정 => status 값을 이용하여 DB를 효율적으로 관리

### 카테고리 관련
(대분류 - 중분류 - 소분류 관계)
1. 대분류는 백의 자리로 구분(ex. 100, 200, 300)
2. 중분류는 십의 자리로 구분(ex. 110, 120, 130)
3. 소분류는 일의 자리로 구분(ex. 111, 112, 113)
4. 백의 자리가 같은 것은 같은 대분류 카테고리
5. 십의 자리가 같은 것은 같은 중분류 카테고리

=> 카테고리 종속 관계를 관리하기에 편리 

### 찜하기
(하나의 API에 찜하기 기능과 찜 해제 기능)
1. 처음에 찜 => 찜 생성
2. 두번째 찜 => 찜 해제(status 값을 A에서 D)
3. 세번째 찜 => 찜(status 값을 D에서 A)

=> status 값을 이용하여 DB를 효율적으로 관리
=> 하나의 API로 통합하여 클라이언트에게 편리한 API 제공

### 팔로우
(하나의 API에 팔로우 기능과 언팔로우 기능) 
1. 처음에 팔로우 => 팔로우 생성
2. 두번째 팔로우 => 언팔로우(status 값을 A에서 D)
3. 세번째 팔로우 => 팔로우(status 값을 D에서 A)

=> status 값을 이용하여 DB를 효율적으로 관리
=> 하나의 API로 통합하여 클라이언트에게 편리한 API 제공

## ✨구현
### 상품 관련
- 상품 등록 시 여러 이미지 등록, 두개의 상품 테이블과 상품 이미지 테이블에 update sql문 사용하므로 transaction 처리
- 현재 로그인 한 회원이 찜한 상품인지 조회하기 위해 상품 테이블에 false를 디폴트로 갖는 isFav컬럼을 만듦
현재 로그인 한 회원의 jwt로부터 userIdx를 발췌해서, 찜 테이블에 해당 상품을 찜했는지 보고 찜 했다면 isFav에 true 값을 넣어 response

### JWT 관련
- 회원가입, 로그인 API 제외한 나머지 API에는 JWT적용
- JWT로부터 발췌한 userIdx가 회원 테이블에 status가 Active로 존재하는지 유효한 회원인지 validation
- 권한이 필요한 API에서 권한이 있는 userIdx와 현재 접근하는 회원의 JWT에서 userIdx가 같은지 validation

### 소셜 로그인
1. 네이버 id와 pw를 입력
2. 네이버 서버에서 클라이언트(프론트)한테 access token을 줌
3. 클라이언트 서버에서 백엔드 서버에게 그 토큰을 줌
4. 네이버 서버에 가서 필요한 정보를 가져옴
5. 이미 저장해놨던 소셜로그인 ID와 비교
6. 비교해서 맞으면 JWT 뿌림

### 찜, 팔로우
- 처음에 찜 API와 찜 해제 API로 나눠서 구현했지만 두 가지 기능을 하나의 API로 통합
1. 해당 회원과 해당 상품에 대해 찜 테이블에 존재하지 않다면 새로 생성
2. 해당 회원과 해당 상품에 대해 찜 테이블에 status가 D로 존재한다면 status를 A로 수정
3. 해당 회원과 해당 상품에 대해 찜 테이블에 status가 A로 존재한다면 status를 D로 수정
4. 동일한 회원과 상품인 경우 2, 3번 과정이 반복됨

- 마찬가지로 팔로우 API와 언팔로우 API로 나눠서 구현했던 것을 두 가지를 하나의 API로 통합
1. 해당 회원과 팔로우 대상 회원에 대해 팔로우 테이블에 존재하지 않다면 새로 생성
2. 해당 회원과 팔로우 대상 회원에 대해 팔로우 테이블에 status가 D로 존재한다면 status를 A로 수정
3. 해당 회원과 팔로우 대상 회원에 대해 팔로우 테이블에 A로 존재한다면 status를 D로 수정
4. 동일한 회원과 대상 회원인 경우 2, 3번 과정이 반복됨

## ✨에러
### 소셜 로그인 과정에서 네이버로부터 회원 정보 가져올때
=> SSL 인증한 서버로부터 요청하지 않으면 네이버에 회원 정보를 요청할 수 없음
=> SSL 인증하여 소셜 로그인 구현

### nullpointerexception
=> null인 객체의 메소드를 사용하려고 할 때 발생한 에러
=> 처음에 객체가 null이 아니도록 값을 넣어서 선언하여 해결

### @Transactional(readOnly = true)
=> @Transactional(readOnly = true) 이 어노테이션을 사용한 경우 insert, update sql문 에러가 발생
=> @Transactional(readOnly = false)

<br>
<br>
<br>
<br>

※더 자세히 보고 싶다면 아래 링크를 참고해주세요※
- ERD 설계
https://aquerytool.com/aquerymain/index/?rurl=927fb142-9ea0-4704-baa6-84f52e940f5b&
Password : 0g7o44
- API 명세서 (postman 결과 화면)
https://docs.google.com/spreadsheets/d/1q-2XRtYMsKd2jgJ1K1cO_WJCWn2J6Vf3U0Yj7O7nhBU/edit#gid=0
- 기술 벨로그
https://velog.io/@dadodo
