package com.example.demo.src.user;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.product.model.PostFavPrdRes;
import com.example.demo.src.user.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

/**
 * Service란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Create, Update, Delete 의 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다. // [Business Layer]는 컨트롤러와 데이터 베이스를 연결
@Transactional(readOnly = false)
public class UserService {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log 처리부분: Log를 기록하기 위해 필요한 함수입니다.

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserDao userDao;
    private final UserProvider userProvider;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    @Autowired //readme 참고
    public UserService(UserDao userDao, UserProvider userProvider, JwtService jwtService) {
        this.userDao = userDao;
        this.userProvider = userProvider;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!

    }
    // ******************************************************************************
    // 회원가입(POST)
    public PostUserRes createUser(PostUserReq postUserReq) throws BaseException {
        // 중복 확인: 해당 이메일을 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkEmail(postUserReq.getEmail()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_EMAIL);
        }
        // 중복 확인: 해당 패스워드를 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        List<GetPwd> getPwd = userProvider.getPassword();
        String password;
        for(GetPwd p : getPwd) {
            if (p.getPassword() != null) {
                System.out.println(p.getPassword());
                try {
                    password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(p.getPassword()); // 복호화
                } catch (Exception ignored) {
                    throw new BaseException(PASSWORD_DECRYPTION_ERROR);
                }
                // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 복호화된 값끼리 비교를 해야합니다.
                System.out.println(password);
                if (postUserReq.getPassword().equals(password)) {
                    throw new BaseException(POST_USERS_EXISTS_PASSWORD);
                }
            }
        }
        // 중복 확인: 해당 이름을 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkName(postUserReq.getName()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_NAME);
        }
        String pwd;
        try {
            // 암호화: postUserReq에서 제공받은 비밀번호를 보안을 위해 암호화시켜 DB에 저장합니다.
            // ex) password123 -> dfhsjfkjdsnj4@!$!@chdsnjfwkenjfnsjfnjsd.fdsfaifsadjfjaf
            pwd = new AES128(Secret.USER_INFO_PASSWORD_KEY).encrypt(postUserReq.getPassword()); // 암호화코드
            postUserReq.setPassword(pwd);
        } catch (Exception ignored) { // 암호화가 실패하였을 경우 에러 발생
            throw new BaseException(PASSWORD_ENCRYPTION_ERROR);
        }
        try {
            int userIdx = userDao.createUser(postUserReq);
//            return new PostUserRes(userIdx);
              //jwt 발급.
            String jwt = jwtService.createJwt(userIdx);
            return new PostUserRes(userIdx, jwt);
//  *********** 해당 부분은 7주차 수업 후 주석해제하서 대체해서 사용해주세요! ***********
//            //jwt 발급.
//            String jwt = jwtService.createJwt(userIdx);
//            return new PostUserRes(jwt,userIdx);
//  *********************************************************************
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 소셜 로그인 회원가입(POST)
    @Transactional(rollbackFor = Exception.class)
    public PostUserRes createNaverUser(String responseBody, String name) throws BaseException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String socialId = jsonNode.get("response").get("id").asText();
        System.out.println(socialId);
        // User 테이블에 해당 소셜로그인 ID가 존재한다면 => 이미 생성되었던 회원
        // 회원가입을 했던 회원이고 현재도 유효한 계정이라면 회원가입 안됨
        if (userDao.checkId(socialId) == 1) {
            throw new BaseException(FAILED_TO_SIGNUP);
        }

        try {
            System.out.println(responseBody);
            // 회원가입을 했던 회원인데 과거에 삭제(delete)했던 계정이라면, 회원 status를 A로 다시 전환하는 방식으로 회원가입
            if (userDao.checkDeletedId(socialId) == 1){
                int result = userDao.activeUser(socialId, name);
                if (result == 0) {
                    throw new BaseException(MODIFY_FAIL_USER_STATUS);
                }
                int userId = userDao.getUserIdxBySocialLoginId(socialId);
                String jwt = jwtService.createJwt(userId);
                return new PostUserRes(userId, jwt);
            }

            String socialLoginId = userDao.createNaverUser(responseBody); // 소셜로그인 테이블에 생성

            System.out.println(socialLoginId);
            int userIdx = userDao.createUserBySocialLoginId(socialLoginId, name); // 소셜로그인 ID를 갖는 회원 생성
            String jwt = jwtService.createJwt(userIdx);
            System.out.println(userIdx);
            return new PostUserRes(userIdx, jwt);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 회원정보 이름 수정(Patch)
    public void modifyUserName(PatchUserReq patchUserReq) throws BaseException {
        // 중복 확인: 해당 이름을 가진 유저가 있는지 확인합니다. 중복될 경우, 에러 메시지를 보냅니다.
        if (userProvider.checkName(patchUserReq.getUpdate()) == 1) {
            throw new BaseException(POST_USERS_EXISTS_NAME);
        }
        try {
            int result = userDao.modifyUserName(patchUserReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_USERNAME);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 회원정보 소개글 수정(Patch)
    public void modifyUserIntro(PatchUserReq patchUserReq) throws BaseException {
        try {
            int result = userDao.modifyUserIntro(patchUserReq); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAIL_INTRODUCE);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 회원 삭제(Patch)
    public void deleteUser(int userIdx) throws BaseException {
        try {
            int result = userDao.deleteUser(userIdx); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_DELETE_USER_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 팔로우
    public PostFollowRes followUser(int userIdxByJwt, int followeeIdx) throws BaseException {
        if(userDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(userDao.checkUser(followeeIdx) == 0) {
            throw new BaseException(INVALID_FOLLOWEE_USER);
        }

        try {
            System.out.println(userIdxByJwt);

            System.out.println(userProvider.checkActiveFollow(userIdxByJwt, followeeIdx));
            System.out.println(userProvider.checkDeletedFollow(userIdxByJwt, followeeIdx));
            // 팔로우하려고 한 회원이 이미 팔로우했다가 언팔한 회원일 경우 => 해당 팔로우 목록의 status를 A로 바꿈
            if(userProvider.checkDeletedFollow(userIdxByJwt, followeeIdx) == true){
                int result = userDao.activeFollow(userIdxByJwt, followeeIdx);
                if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                    throw new BaseException(MODIFY_FAIL_ACTIVE_FOLLOW);
                }
                return new PostFollowRes(userDao.getFollowIdx(userIdxByJwt, followeeIdx));
            }

            // 팔로우하려고 한 상품이 이미 팔로우했던 상품일 경우 => 삭제 status를 D로 바꿈
            if(userProvider.checkActiveFollow(userIdxByJwt, followeeIdx) == true){
                int result = userDao.deleteFollow(userIdxByJwt, followeeIdx);
                if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                    throw new BaseException(MODIFY_FAIL_DELETE_FOLLOW);
                }
                return new PostFollowRes(userDao.getFollowIdx(userIdxByJwt, followeeIdx));
            }

            // 팔로우한 기록이 없다면 새롭게 생성
            Integer followIdx = userDao.createFollow(userIdxByJwt, followeeIdx);
            return new PostFollowRes(followIdx);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 언팔로우(Patch)
    public void unfollowUser(int userIdxByJwt, int followeeIdx) throws BaseException {
        if(userDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(userDao.checkUser(followeeIdx) == 0) {
            throw new BaseException(INVALID_FOLLOWEE_USER);
        }
        if(userDao.checkDeletedFollow(userIdxByJwt, followeeIdx) == true) {
            throw new BaseException(DELETED_FOLLOW);
        }

        try {
            int result = userDao.deleteFollow(userIdxByJwt, followeeIdx); // 해당 과정이 무사히 수행되면 True(1), 그렇지 않으면 False(0)입니다.
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_UNFOLLOW_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 후기 작성
    public PostReviewRes createReview(int userIdxByJwt, PostReviewReq postReviewReq) throws BaseException {
        if(userDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(userDao.checkUser(postReviewReq.getRevieweeIdx()) == 0) {
            throw new BaseException(INVALID_REVIEWEE_USER);
        }
        if(userDao.checkProductIdx(postReviewReq.getProductIdx()) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        if(userDao.checkProductIdxByUserIdx(postReviewReq.getRevieweeIdx(), postReviewReq.getProductIdx()) == 0) {
            throw new BaseException(PRODUCT_USER_MISS_MATCH);
        }

        try {
            Integer reviewIdx = userDao.createReview(userIdxByJwt, postReviewReq);
            return new PostReviewRes(reviewIdx);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
