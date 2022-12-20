package com.example.demo.src.user;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.product.model.GetPrdRes;
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
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

//Provider : Read의 비즈니스 로직 처리
@Service    // [Business Layer에서 Service를 명시하기 위해서 사용] 비즈니스 로직이나 respository layer 호출하는 함수에 사용된다.
            // [Business Layer]는 컨트롤러와 데이터 베이스를 연결
/**
 * Provider란?
 * Controller에 의해 호출되어 실제 비즈니스 로직과 트랜잭션을 처리: Read의 비즈니스 로직 처리
 * 요청한 작업을 처리하는 관정을 하나의 작업으로 묶음
 * dao를 호출하여 DB CRUD를 처리 후 Controller로 반환
 */
public class UserProvider {


    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************
    private final UserDao userDao;
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public UserProvider(UserDao userDao, JwtService jwtService) {
        this.userDao = userDao;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }
    // ******************************************************************************


    // 로그인(password 검사)
    public PostLoginRes logIn(PostLoginReq postLoginReq) throws BaseException {
        if(userDao.checkUserByLogin(postLoginReq.getEmail()) == 0) {
            throw new BaseException(INVALID_USER_LOGIN);
        }
        User user = userDao.getPwd(postLoginReq);
        String password;
        try {
            password = new AES128(Secret.USER_INFO_PASSWORD_KEY).decrypt(user.getPassword()); // 복호화
            // 회원가입할 때 비밀번호가 암호화되어 저장되었기 떄문에 로그인을 할때도 복호화된 값끼리 비교를 해야합니다.
        } catch (Exception ignored) {
            throw new BaseException(PASSWORD_DECRYPTION_ERROR);
        }
        if (postLoginReq.getPassword().equals(password)) { //비말번호가 일치한다면 userIdx를 가져온다.
            int userIdx = userDao.getPwd(postLoginReq).getUserIdx();
//            return new PostLoginRes(userIdx);
//  *********** 해당 부분은 7주차 - JWT 수업 후 주석해제 및 대체해주세요!  **************** //
            String jwt = jwtService.createJwt(userIdx);
            return new PostLoginRes(userIdx,jwt);
//  **************************************************************************

        } else { // 비밀번호가 다르다면 에러메세지를 출력한다.
            throw new BaseException(FAILED_TO_LOGIN);
        }
    }

    // 네이버 소셜 로그인(ID 중복 검사)
    public PostLoginRes naverLogIn(String responseBody) throws BaseException, JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String socialLoginId = jsonNode.get("response").get("id").asText();

        if(userDao.checkId(socialLoginId) == 0) { // 존재하지 않은 ID 라면 로그인 안됨 => 회원가입 필요
            throw new BaseException(FAILED_TO_SOCIAL_LOGIN);
        }
        try {
            int userIdx = userDao.getUserIdxBySocialLoginId(socialLoginId); // 소셜로그인ID에 해당하는 회원의 userIdx 가져옴
            String jwt = jwtService.createJwt(userIdx); // 해당 회원의 userIdx에 대한 jwt 발급
            return new PostLoginRes(userIdx,jwt);

        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }

    }

    // 해당 이메일이 이미 User Table에 존재하는지 확인
    public int checkEmail(String email) throws BaseException {
        try {
            return userDao.checkEmail(email);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public List<GetPwd> getPassword() throws BaseException {
        try {
            return userDao.getPassword();
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 이름이 이미 User Table에 존재하는지 확인
    public int checkName(String name) throws BaseException {
        try {
            return userDao.checkName(name);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 User의 정보 조회
    public GetUserRes getUser(int userIdx, int userIdxByJwt) throws BaseException {
        if(userDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(userDao.checkUser(userIdx) == 0) {
            throw new BaseException(INVALID_USER);
        }
        try {
            GetUserRes getUserRes = userDao.getUser(userIdx);
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 User 상품 정보 조회
    public List<GetUserPrdRes> getUserProducts(int userIdx, int userIdxByJwt) throws BaseException {
        if(userDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(userDao.checkUser(userIdx) == 0) {
            throw new BaseException(INVALID_USER);
        }
        try {
            List<GetUserPrdRes> getUserPrdRes = userDao.getUserProducts(userIdx);
            for(GetUserPrdRes prd : getUserPrdRes) {
                prd.setIsFav(userDao.checkActiveFavorite(prd.getProductIdx(), userIdxByJwt));
            }
            return getUserPrdRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 User 후기 조회
    public List<GetUserReviewRes> getUserReviews(int userIdx, int userIdxByJwt) throws BaseException {
        if(userDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(userDao.checkUser(userIdx) == 0) {
            throw new BaseException(INVALID_USER);
        }
        try {
            List<GetUserReviewRes> getUserReviewRes = userDao.getUserReviews(userIdx);
            return getUserReviewRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 User 찜한 목록 조회
    public List<GetUserFavRes> getUserFavorites(int userIdx, int userIdxByJwt) throws BaseException {
        if(userDao.checkUser(userIdx) == 0) {
            throw new BaseException(INVALID_USER);
        }
        try {
            List<GetUserFavRes> getUserFavRes = userDao.getUserFavorites(userIdx);
            for(GetUserFavRes prd : getUserFavRes) {
                prd.setIsFav(userDao.checkActiveFavorite(prd.getFavoriteProductIdx(), userIdxByJwt));
            }
            return getUserFavRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 팔로워 목록 조회
    public List<GetFollowerRes> getFollowers(int userIdx, int userIdxByJwt) throws BaseException {
        if(userDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(userDao.checkUser(userIdx) == 0) {
            throw new BaseException(INVALID_USER);
        }
        try {
            List<GetFollowerRes> getFollowerRes = userDao.getFollowers(userIdx);
            return getFollowerRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 userIdx를 갖는 팔로잉 목록 조회
    public List<GetFollowingRes> getFollowings(int userIdx, int userIdxByJwt) throws BaseException {
        if(userDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(userDao.checkUser(userIdx) == 0) {
            throw new BaseException(INVALID_USER);
        }
        try {
            List<GetFollowingRes> getFollowingRes = userDao.getFollowings(userIdx);
            return getFollowingRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // User들의 정보를 조회
    public List<GetUserRes> getUsers() throws BaseException {
        try {
            List<GetUserRes> getUserRes = userDao.getUsers();
            return getUserRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

//    // 해당 nickname을 갖는 User들의 정보 조회
//    public List<GetUserRes> getUsersByNickname(String nickname) throws BaseException {
//        try {
//            List<GetUserRes> getUsersRes = userDao.getUsersByNickname(nickname);
//            return getUsersRes;
//        } catch (Exception exception) {
//            throw new BaseException(DATABASE_ERROR);
//        }
//    }

    // 팔로우 활성화 여부 확인
    public Boolean checkActiveFollow(int userIdxByJwt, int followeeIdx) throws BaseException {
        try {
            return userDao.checkActiveFollow(userIdxByJwt, followeeIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 팔로우 삭제 여부 확인
    public Boolean checkDeletedFollow(int userIdxByJwt, int followeeIdx) throws BaseException {
        try {
            return userDao.checkDeletedFollow(userIdxByJwt, followeeIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 해당 userIdx가 존재하는지 확인
    public int checkUser(int userIdx) throws BaseException {
        try {
            return userDao.checkUser(userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

}
