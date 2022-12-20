package com.example.demo.src.user;

import com.example.demo.src.product.model.PostFavPrdRes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static com.example.demo.config.BaseResponseStatus.*;
import static com.example.demo.utils.ValidationRegex.isRegexEmail;
import static com.example.demo.utils.ValidationRegex.isRegexPassword;

@RestController // Rest API 또는 WebAPI를 개발하기 위한 어노테이션. @Controller + @ResponseBody 를 합친것.
                // @Controller      [Presentation Layer에서 Contoller를 명시하기 위해 사용]
                //  [Presentation Layer?] 클라이언트와 최초로 만나는 곳으로 데이터 입출력이 발생하는 곳
                //  Web MVC 코드에 사용되는 어노테이션. @RequestMapping 어노테이션을 해당 어노테이션 밑에서만 사용할 수 있다.
                // @ResponseBody    모든 method의 return object를 적절한 형태로 변환 후, HTTP Response Body에 담아 반환.
@RequestMapping("/bunjang/users")
// method가 어떤 HTTP 요청을 처리할 것인가를 작성한다.
// 요청에 대해 어떤 Controller, 어떤 메소드가 처리할지를 맵핑하기 위한 어노테이션
// URL(/app/users)을 컨트롤러의 메서드와 매핑할 때 사용
/**
 * Controller란?
 * 사용자의 Request를 전달받아 요청의 처리를 담당하는 Service, Prodiver 를 호출
 */
public class UserController {
    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired  // 객체 생성을 스프링에서 자동으로 생성해주는 역할. 주입하려 하는 객체의 타입이 일치하는 객체를 자동으로 주입한다.
    // IoC(Inversion of Control, 제어의 역전) / DI(Dependency Injection, 의존관계 주입)에 대한 공부하시면, 더 깊이 있게 Spring에 대한 공부를 하실 수 있을 겁니다!(일단은 모르고 넘어가셔도 무방합니다.)
    // IoC 간단설명,  메소드나 객체의 호출작업을 개발자가 결정하는 것이 아니라, 외부에서 결정되는 것을 의미
    // DI 간단설명, 객체를 직접 생성하는 게 아니라 외부에서 생성한 후 주입 시켜주는 방식
    private final UserProvider userProvider;
    @Autowired
    private final UserService userService;
    @Autowired
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    public UserController(UserProvider userProvider, UserService userService, JwtService jwtService) {
        this.userProvider = userProvider;
        this.userService = userService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    // ******************************************************************************

    /**
     * 회원가입 API
     * [POST] /users/sign-up
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserRes> createUser(@RequestBody PostUserReq postUserReq) {
        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        // email에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
        if (postUserReq.getEmail() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
        }
        //전화번호 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
        if (!isRegexEmail(postUserReq.getEmail())) {
            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
        }
        //비밀번호
        if (postUserReq.getPassword() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
        }
        //비밀번호 정규표현: 숫자, 문자, 특수문자 포함 8~15자리 이내
        if (!isRegexPassword(postUserReq.getPassword())) {
            return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
        }
        //이름
        if (postUserReq.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        //이름 2~10자 인지 확인
        if (postUserReq.getName().length() < 2 || postUserReq.getName().length() > 10){
            return new BaseResponse<>(POST_USERS_INVALID_NAME);
        }
        try {
            PostUserRes postUserRes = userService.createUser(postUserReq);
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * Naver 소셜 회원가입 API
     * [POST] /users/sign-up/naver
     */
    // Body
    @ResponseBody
    @PostMapping("/sign-up/naver")    // POST 방식의 요청을 매핑하기 위한 어노테이션
    public BaseResponse<PostUserRes> createNaverUser(@RequestBody User user, @RequestHeader("NAVER-ACCESS-TOKEN") String token) {
        //  @RequestBody란, 클라이언트가 전송하는 HTTP Request Body(우리는 JSON으로 통신하니, 이 경우 body는 JSON)를 자바 객체로 매핑시켜주는 어노테이션
        // TODO: email 관련한 짧은 validation 예시입니다. 그 외 더 부가적으로 추가해주세요!
        // email에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
//        if (postUserReq.getEmail() == null) {
//            return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
//        }
//        //전화번호 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
//        if (!isRegexEmail(postUserReq.getEmail())) {
//            return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
//        }
        if(token == null || token.length() == 0){
            return new BaseResponse<>(EMPTY_NAVER_ACCESS_TOKEN);
        }
        //이름
        if (user.getName() == null) {
            return new BaseResponse<>(POST_USERS_EMPTY_NAME);
        }
        //이름 2~10자 인지 확인
        if (user.getName().length() < 2 || user.getName().length() > 10){
            return new BaseResponse<>(POST_USERS_INVALID_NAME);
        }
        try {
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            String apiURL = "https://openapi.naver.com/v1/nid/me";

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("Authorization", header);
            String responseBody = get(apiURL,requestHeaders);
            System.out.println(token);
            System.out.println(responseBody);

            PostUserRes postUserRes = userService.createNaverUser(responseBody, user.getName());
            return new BaseResponse<>(postUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 로그인 API
     * [POST] /users/logIn
     */
    @ResponseBody
    @PostMapping("/log-in")
    public BaseResponse<PostLoginRes> logIn(@RequestBody PostLoginReq postLoginReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            // email에 값이 존재하는지, 빈 값으로 요청하지는 않았는지 검사합니다. 빈값으로 요청했다면 에러 메시지를 보냅니다.
            if (postLoginReq.getEmail() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_EMAIL);
            }
            //전화번호 정규표현: 입력받은 이메일이 email@domain.xxx와 같은 형식인지 검사합니다. 형식이 올바르지 않다면 에러 메시지를 보냅니다.
            if (!isRegexEmail(postLoginReq.getEmail())) {
                return new BaseResponse<>(POST_USERS_INVALID_EMAIL);
            }
            //비밀번호
            if (postLoginReq.getPassword() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_PASSWORD);
            }
            //비밀번호 정규표현: 숫자, 문자, 특수문자 포함 8~15자리 이내
            if (!isRegexPassword(postLoginReq.getPassword())) {
                return new BaseResponse<>(POST_USERS_INVALID_PASSWORD);
            }
            PostLoginRes postLoginRes = userProvider.logIn(postLoginReq);
            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 네이버 소셜 로그인 API
     * [POST] /users/log-in/naver
     */
    @ResponseBody
    @PostMapping("/log-in/naver")
    public BaseResponse<PostLoginRes> naverLogIn(@RequestHeader("NAVER-ACCESS-TOKEN") String token) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validation 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.

            if(token == null || token.length() == 0){
                return new BaseResponse<>(EMPTY_NAVER_ACCESS_TOKEN);
            }
            String header = "Bearer " + token; // Bearer 다음에 공백 추가
            String apiURL = "https://openapi.naver.com/v1/nid/me";

            Map<String, String> requestHeaders = new HashMap<>();
            requestHeaders.put("Authorization", header);
            String responseBody = get(apiURL,requestHeaders);

            System.out.println(responseBody);

            PostLoginRes postLoginRes = userProvider.naverLogIn(responseBody);

            return new BaseResponse<>(postLoginRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        } catch (JsonMappingException e) {
            throw new RuntimeException(e);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static String get(String apiUrl, Map<String, String> requestHeaders){
        HttpURLConnection con = connect(apiUrl);
        try {
            con.setRequestMethod("GET");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }


            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 호출
                return readBody(con.getInputStream());
            } else { // 에러 발생
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }

    private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);


        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();


            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }


            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }


    /**
     * 회원 조회 API
     * [GET] /users
     */
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/bunjang/users/:userIdx
    public BaseResponse<List<GetUserRes>> getUsers() {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            if(userIdxByJwt != 11) {
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetUserRes> getUserRes = userProvider.getUsers();
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }


    /**
     * 특정 회원 조회 API
     * [GET] /users/:userIdx
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}") // (GET) 127.0.0.1:9000/bunjang/users/:userIdx
    public BaseResponse<GetUserRes> getUser(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            if(userProvider.checkUser(userIdx) == 0) {
                throw new BaseException(INVALID_USER);
            }
            int userIdxByJwt = jwtService.getUserIdx();
            GetUserRes getUserRes = userProvider.getUser(userIdx, userIdxByJwt);
            return new BaseResponse<>(getUserRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 특정 회원 상품 조회 API
     * [GET] /users/:userIdx/products
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}/products") // (GET) 127.0.0.1:9000/bunjang/users/:userIdx
    public BaseResponse<List<GetUserPrdRes>> getUserProducts(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            if(userProvider.checkUser(userIdx) == 0) {
                throw new BaseException(INVALID_USER);
            }
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetUserPrdRes> getUserPrdRes = userProvider.getUserProducts(userIdx, userIdxByJwt);
            return new BaseResponse<>(getUserPrdRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 특정 회원에 대한 후기 조회 API
     * [GET] /users/:userIdx/reviews
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}/reviews") // (GET) 127.0.0.1:9000/bunjang/users/:userIdx
    public BaseResponse<List<GetUserReviewRes>> getUserReviews(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetUserReviewRes> getUserReviewRes = userProvider.getUserReviews(userIdx, userIdxByJwt);
            return new BaseResponse<>(getUserReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 특정 회원 찜 목록 조회 API
     * [GET] /users/:userIdx/favorites
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}/favorites") // (GET) 127.0.0.1:9000/bunjang/users/:userIdx
    public BaseResponse<List<GetUserFavRes>> getUserFavorites(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            List<GetUserFavRes> getUserFavRes = userProvider.getUserFavorites(userIdx, userIdxByJwt);
            return new BaseResponse<>(getUserFavRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 특정 회원 팔로워 목록 조회 API
     * [GET] /users/:userIdx/followers
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}/followers") // (GET) 127.0.0.1:9000/bunjang/users/:userIdx/follower
    public BaseResponse<List<GetFollowerRes>> getUserFollowers(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            if(userProvider.checkUser(userIdx) == 0) {
                throw new BaseException(INVALID_USER);
            }
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetFollowerRes> getFollowerRes = userProvider.getFollowers(userIdx, userIdxByJwt);
            return new BaseResponse<>(getFollowerRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

    /**
     * 특정 회원 팔로잉 목록 조회 API
     * [GET] /users/:userIdx/followings
     */
    // Path-variable
    @ResponseBody
    @GetMapping("/{userIdx}/followings") // (GET) 127.0.0.1:9000/bunjang/users/:userIdx/followings
    public BaseResponse<List<GetFollowingRes>> getUserFollowings(@PathVariable("userIdx") int userIdx) {
        // @PathVariable RESTful(URL)에서 명시된 파라미터({})를 받는 어노테이션, 이 경우 userId값을 받아옴.
        //  null값 or 공백값이 들어가는 경우는 적용하지 말 것
        //  .(dot)이 포함된 경우, .을 포함한 그 뒤가 잘려서 들어감
        // Get Users
        try {
            if(userProvider.checkUser(userIdx) == 0) {
                throw new BaseException(INVALID_USER);
            }
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetFollowingRes> getFollowingRes = userProvider.getFollowings(userIdx, userIdxByJwt);
            return new BaseResponse<>(getFollowingRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }

    }

//    /**
//     * 모든 회원들의  조회 API
//     * [GET] /users
//     *
//     * 또는
//     *
//     * 해당 닉네임을 같는 유저들의 정보 조회 API
//     * [GET] /users? NickName=
//     */
//    //Query String
//    @ResponseBody   // return되는 자바 객체를 JSON으로 바꿔서 HTTP body에 담는 어노테이션.
//    //  JSON은 HTTP 통신 시, 데이터를 주고받을 때 많이 쓰이는 데이터 포맷.
//    @GetMapping("") // (GET) 127.0.0.1:9000/app/users
//    // GET 방식의 요청을 매핑하기 위한 어노테이션
//    public BaseResponse<List<GetUserRes>> getUsers(@RequestParam(required = false) String nickname) {
//        //  @RequestParam은, 1개의 HTTP Request 파라미터를 받을 수 있는 어노테이션(?뒤의 값). default로 RequestParam은 반드시 값이 존재해야 하도록 설정되어 있지만, (전송 안되면 400 Error 유발)
//        //  지금 예시와 같이 required 설정으로 필수 값에서 제외 시킬 수 있음
//        //  defaultValue를 통해, 기본값(파라미터가 없는 경우, 해당 파라미터의 기본값 설정)을 지정할 수 있음
//        try {
//            if (nickname == null) { // query string인 nickname이 없을 경우, 그냥 전체 유저정보를 불러온다.
//                List<GetUserRes> getUsersRes = userProvider.getUsers();
//                return new BaseResponse<>(getUsersRes);
//            }
//            // query string인 nickname이 있을 경우, 조건을 만족하는 유저정보들을 불러온다.
//            List<GetUserRes> getUsersRes = userProvider.getUsersByNickname(nickname);
//            return new BaseResponse<>(getUsersRes);
//        } catch (BaseException exception) {
//            return new BaseResponse<>((exception.getStatus()));
//        }
//    }




    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx/name
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/name")
    public BaseResponse<String> modifyUserName(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {
/**
  *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //같다면 유저네임 변경
  **************************************************************************
 */

            if(userProvider.checkUser(userIdx) == 0) {
                throw new BaseException(INVALID_USER);
            }

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            //이름
            if (user.getName() == null) {
                return new BaseResponse<>(POST_USERS_EMPTY_NAME);
            }
            //이름 2~10자 인지 확인
            if (user.getName().length() < 2 || user.getName().length() > 10){
                return new BaseResponse<>(POST_USERS_INVALID_NAME);
            }
            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getName());
            userService.modifyUserName(patchUserReq);

            String result = "회원정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 유저정보변경 API
     * [PATCH] /users/:userIdx/introduce
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/introduce")
    public BaseResponse<String> modifyUserIntro(@PathVariable("userIdx") int userIdx, @RequestBody User user) {
        try {
            /**
             *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
             //jwt에서 idx 추출.
             int userIdxByJwt = jwtService.getUserIdx();
             //userIdx와 접근한 유저가 같은지 확인
             if(userIdx != userIdxByJwt){
             return new BaseResponse<>(INVALID_USER_JWT);
             }
             //같다면 유저네임 변경
             **************************************************************************
             */
            if(userProvider.checkUser(userIdx) == 0) {
                throw new BaseException(INVALID_USER);
            }
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            PatchUserReq patchUserReq = new PatchUserReq(userIdx, user.getIntroduce());
            userService.modifyUserIntro(patchUserReq);

            String result = "회원정보가 수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 회원 계정 삭제 API
     * [PATCH] /users/:userIdx/d
     */
    @ResponseBody
    @PatchMapping("/{userIdx}/d")
    public BaseResponse<String> deleteUser(@PathVariable("userIdx") int userIdx) {
        try {
            /**
             *********** 해당 부분은 7주차 - JWT 수업 후 주석해체 해주세요!  ****************
             //jwt에서 idx 추출.
             int userIdxByJwt = jwtService.getUserIdx();
             //userIdx와 접근한 유저가 같은지 확인
             if(userIdx != userIdxByJwt){
             return new BaseResponse<>(INVALID_USER_JWT);
             }
             //같다면 유저네임 변경
             **************************************************************************
             */
            if(userProvider.checkUser(userIdx) == 0) {
                throw new BaseException(INVALID_USER);
            }
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }
            userService.deleteUser(userIdx);

            String result = "회원이 삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 팔로우 API
     * [POST] /users/follows
     */
    @ResponseBody
    @PostMapping("/follows")
    public BaseResponse<PostFollowRes> followUser(@RequestBody PostFollowReq postFollowReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validation 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            PostFollowRes postFollowRes = userService.followUser(userIdxByJwt, postFollowReq.getFolloweeIdx());
            return new BaseResponse<>(postFollowRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * 후기작성 API
     * [POST] /users/reviews
     */
    @ResponseBody
    @PostMapping("/reviews")
    public BaseResponse<PostReviewRes> createReview(@RequestBody PostReviewReq postReviewReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validation 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.

            if (postReviewReq.getRate() < 0 || postReviewReq.getRate() > 5) {
                return new BaseResponse<>(INVALID_RATE);
            }
            if (postReviewReq.getContents().length() < 5 || postReviewReq.getContents().length() > 100) {
                return new BaseResponse<>(POST_REVIEW_INVALID_CONTENTS);
            }
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            PostReviewRes postReviewRes = userService.createReview(userIdxByJwt, postReviewReq);
            return new BaseResponse<>(postReviewRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
    /**
     * 언팔로우 API
     * [PATCH] /users/follows/{followeeIdx}/d
     */
    @ResponseBody
    @PatchMapping("/follows/{followeeIdx}/d")
    public BaseResponse<String> unfollowUser(@PathVariable("followeeIdx") int followeeIdx) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validation 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            userService.unfollowUser(userIdxByJwt, followeeIdx);

            String result = "언팔로우 되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
