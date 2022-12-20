package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import static com.example.demo.src.user.UserDaoSqls.*;

import javax.sql.DataSource;
import java.util.List;

@Repository //  [Persistence Layer에서 DAO를 명시하기 위해 사용]

/**
 * DAO란?
 * 데이터베이스 관련 작업을 전담하는 클래스
 * 데이터베이스에 연결하여, 입력 , 수정, 삭제, 조회 등의 작업을 수행
 */
public class UserDao {

    // *********************** 동작에 있어 필요한 요소들을 불러옵니다. *************************

    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    // ******************************************************************************

    /**
     * DAO관련 함수코드의 전반부는 크게 String ~~~Query와 Object[] ~~~~Params, jdbcTemplate함수로 구성되어 있습니다.(보통은 동적 쿼리문이지만, 동적쿼리가 아닐 경우, Params부분은 없어도 됩니다.)
     * Query부분은 DB에 SQL요청을 할 쿼리문을 의미하는데, 대부분의 경우 동적 쿼리(실행할 때 값이 주입되어야 하는 쿼리) 형태입니다.
     * 그래서 Query의 동적 쿼리에 입력되어야 할 값들이 필요한데 그것이 Params부분입니다.
     * Params부분은 클라이언트의 요청에서 제공하는 정보(~~~~Req.java에 있는 정보)로 부터 getXXX를 통해 값을 가져옵니다. ex) getEmail -> email값을 가져옵니다.
     *      Notice! get과 get의 대상은 카멜케이스로 작성됩니다. ex) item -> getItem, password -> getPassword, email -> getEmail, userIdx -> getUserIdx
     * 그 다음 GET, POST, PATCH 메소드에 따라 jabcTemplate의 적절한 함수(queryForObject, query, update)를 실행시킵니다(DB요청이 일어납니다.).
     *      Notice!
     *      POST, PATCH의 경우 jdbcTemplate.update
     *      GET은 대상이 하나일 경우 jdbcTemplate.queryForObject, 대상이 복수일 경우, jdbcTemplate.query 함수를 사용합니다.
     * jdbcTeplate이 실행시킬 때 Query 부분과 Params 부분은 대응(값을 주입)시켜서 DB에 요청합니다.
     * <p>
     * 정리하자면 < 동적 쿼리문 설정(Query) -> 주입될 값 설정(Params) -> jdbcTemplate함수(Query, Params)를 통해 Query, Params를 대응시켜 DB에 요청 > 입니다.
     * <p>
     * <p>
     * DAO관련 함수코드의 후반부는 전반부 코드를 실행시킨 후 어떤 결과값을 반환(return)할 것인지를 결정합니다.
     * 어떠한 값을 반환할 것인지 정의한 후, return문에 전달하면 됩니다.
     * ex) return this.jdbcTemplate.query( ~~~~ ) -> ~~~~쿼리문을 통해 얻은 결과를 반환합니다.
     */

    /**
     * 참고 링크
     * https://jaehoney.tistory.com/34 -> JdbcTemplate 관련 함수에 대한 설명
     * https://velog.io/@seculoper235/RowMapper%EC%97%90-%EB%8C%80%ED%95%B4 -> RowMapper에 대한 설명
     */

    // 회원가입
    public int createUser(PostUserReq postUserReq) {
        String createUserQuery = "insert into User (name, email, password) VALUES (?,?,?)"; // 실행될 동적 쿼리문
        Object[] createUserParams = new Object[]{postUserReq.getName(), postUserReq.getEmail(), postUserReq.getPassword()}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        // email -> postUserReq.getEmail(), password -> postUserReq.getPassword(), nickname -> postUserReq.getNickname() 로 매핑(대응)시킨다음 쿼리문을 실행한다.
        // 즉 DB의 User Table에 (email, password, nickname)값을 가지는 유저 데이터를 삽입(생성)한다.

        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    // 이메일 확인
    public int checkEmail(String email) {
        String checkEmailQuery = "select exists(select email from User where status = 'A' and email = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkEmailParams = email; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 비밀번호 조회
    public List<GetPwd> getPassword() {
        String checkPasswordQuery = "select password from User where status = 'A'"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?

        return this.jdbcTemplate.query(checkPasswordQuery,
                (rs, rowNum) -> new GetPwd(
                        rs.getString("password"))); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 비밀번호 확인
    public int checkName(String name) {
        String checkNameQuery = "select exists(select name from User where status = 'A' and name = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkNameParams = name; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkNameQuery,
                int.class,
                checkNameParams); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 이름 확인
    public int checkUserByLogin(String email) {
        String checkEmailQuery = "select exists(select userIdx from User where status = 'A' and email = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkEmailParams = email; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 회원정보 이름 변경
    public int modifyUserName(PatchUserReq patchUserReq) {
        String modifyUserNameQuery = "update User set name = ? where status = 'A' and userIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyUserNameParams = new Object[]{patchUserReq.getUpdate(), patchUserReq.getUserIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyUserNameQuery, modifyUserNameParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0) 
    }

    // 회원정보 소개글 변경
    public int modifyUserIntro(PatchUserReq patchUserReq) {
        String modifyUserIntroQuery = "update User set introduce = ? where status = 'A' and userIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        Object[] modifyUserIntroParams = new Object[]{patchUserReq.getUpdate(), patchUserReq.getUserIdx()}; // 주입될 값들(nickname, userIdx) 순

        return this.jdbcTemplate.update(modifyUserIntroQuery, modifyUserIntroParams); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 회원 삭제
    public int deleteUser(int userIdx) {
        String modifyUserIntroQuery = "update User set status = 'D', introduce = NULL where userIdx = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.

        return this.jdbcTemplate.update(modifyUserIntroQuery, userIdx); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 회원 재활성화
    public int activeUser(String socialLoginId, String name) {
        String modifyUserIntroQuery = "update User set status = 'A', name = ? where socialLoginId = ? "; // 해당 userIdx를 만족하는 User를 해당 nickname으로 변경한다.
        return this.jdbcTemplate.update(modifyUserIntroQuery, name, socialLoginId); // 대응시켜 매핑시켜 쿼리 요청(생성했으면 1, 실패했으면 0)
    }

    // 로그인: 해당 email에 해당되는 user의 암호화된 비밀번호 값을 가져온다.
    public User getPwd(PostLoginReq postLoginReq) {
        String getPwdQuery = "select userIdx, email, password, name, introduce from User where email = ?"; // 해당 email을 만족하는 User의 정보들을 조회한다.
        String getPwdParams = postLoginReq.getEmail(); // 주입될 email값을 클라이언트의 요청에서 주어진 정보를 통해 가져온다.

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs, rowNum) -> new User(
                        rs.getInt("userIdx"),
                        rs.getString("email"),
                        rs.getString("password"),
                        rs.getString("name"),
                        rs.getString("introduce")
                ), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getPwdParams
        ); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // User 테이블에 존재하는 전체 유저들의 정보 조회
    public List<GetUserRes> getUsers() {
        String getUsersQuery = SELECT_USERS; //User 테이블에 존재하는 모든 회원들의 정보를 조회하는 쿼리
        return this.jdbcTemplate.query(getUsersQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("profileImg"),
                        rs.getString("introduce"),
                        rs.getDouble("rate"),
                        rs.getInt("sellCnt"),
                        rs.getInt("followerCnt"),
                        rs.getInt("followingCnt"),
                        rs.getInt("safeCnt"),
                        rs.getTimestamp("createdAt"),
                        rs.getInt("auth")) // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
        ); // 복수개의 회원정보들을 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보)의 결과 반환(동적쿼리가 아니므로 Parmas부분이 없음)
    }

//    // 해당 nickname을 갖는 유저들의 정보 조회
//    public List<GetUserRes> getUsersByNickname(String nickname) {
//        String getUsersByNicknameQuery = "select * from User where nickname =?"; // 해당 이메일을 만족하는 유저를 조회하는 쿼리문
//        String getUsersByNicknameParams = nickname;
//        return this.jdbcTemplate.query(getUsersByNicknameQuery,
//                (rs, rowNum) -> new GetUserRes(
//                        rs.getInt("userIdx"),
//                        rs.getString("nickname"),
//                        rs.getString("Email"),
//                        rs.getString("password")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
//                getUsersByNicknameParams); // 해당 닉네임을 갖는 모든 User 정보를 얻기 위해 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
//    }

    // 해당 userIdx를 갖는 유저조회
    public GetUserRes getUser(int userIdx) {
        String getUserQuery = SELECT_USER; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserParams = userIdx;
        return this.jdbcTemplate.queryForObject(getUserQuery,
                (rs, rowNum) -> new GetUserRes(
                        rs.getInt("userIdx"),
                        rs.getString("name"),
                        rs.getString("profileImg"),
                        rs.getString("introduce"),
                        rs.getDouble("rate"),
                        rs.getInt("sellCnt"),
                        rs.getInt("followerCnt"),
                        rs.getInt("followingCnt"),
                        rs.getInt("safeCnt"),
                        rs.getTimestamp("createdAt"),
                        rs.getInt("auth")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 해당 userIdx를 갖는 유저 상품들 조회
    public List<GetUserPrdRes> getUserProducts(int userIdx) {
        String getUserPrdQuery = SELECT_USER_PRODUCTS; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserPrdParams = userIdx;
        return this.jdbcTemplate.query(getUserPrdQuery,
                (rs, rowNum) -> new GetUserPrdRes(
                        rs.getInt("productIdx"),
                        rs.getInt("userIdx"),
                        rs.getBoolean("isSafepay"),
                        rs.getString("tradeStatus"),
                        rs.getString("imageUrl"),
                        rs.getString("title"),
                        rs.getString("price"),
                        rs.getString("location"),
                        rs.getTimestamp("createdAt"),
                        rs.getBoolean("isFav")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserPrdParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 해당 userIdx를 갖는 유저 후기들 조회
    public List<GetUserReviewRes> getUserReviews(int userIdx) {
        String getUserReviewQuery = SELECT_USER_REVIEWS; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserReviewParams = userIdx;
        return this.jdbcTemplate.query(getUserReviewQuery,
                (rs, rowNum) -> new GetUserReviewRes(
                        rs.getInt("reviewIdx"),
                        rs.getDouble("rate"),
                        rs.getString("contents"),
                        rs.getString("title"),
                        rs.getInt("reviewerIdx"),
                        rs.getString("name"),
                        rs.getTimestamp("createdAt"),
                        rs.getInt("revieweeIdx"),
                        rs.getInt("productIdx")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserReviewParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 찜 목록 조회
    public List<GetUserFavRes> getUserFavorites(int userIdx) {
        String getUserFavQuery = SELECT_USER_FAVORITES; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getUserFavParams = userIdx;
        return this.jdbcTemplate.query(getUserFavQuery,
                (rs, rowNum) -> new GetUserFavRes(
                        rs.getInt("favoriteIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("favoriteProductIdx"),
                        rs.getString("tradeStatus"),
                        rs.getBoolean("isSafepay"),
                        rs.getString("imageUrl"),
                        rs.getString("title"),
                        rs.getString("price"),
                        rs.getInt("sellerIdx"),
                        rs.getString("name"),
                        rs.getTimestamp("createdAt"),
                        rs.getBoolean("isFav")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getUserFavParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 팔로워 목록 조회
    public List<GetFollowerRes> getFollowers(int userIdx) {
        String getFollowerQuery = SELECT_USER_FOLLOWERS; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getFollowerParams = userIdx;
        return this.jdbcTemplate.query(getFollowerQuery,
                (rs, rowNum) -> new GetFollowerRes(
                        rs.getInt("followIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("followerIdx"),
                        rs.getString("name"),
                        rs.getString("profileImg"),
                        rs.getInt("prdCnt"),
                        rs.getInt("followerCnt")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getFollowerParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 팔로워 목록 조회
    public List<GetFollowingRes> getFollowings(int userIdx) {
        String getFollowingQuery = SELECT_USER_FOLLOWINGS; // 해당 userIdx를 만족하는 유저를 조회하는 쿼리문
        int getFollowingParams = userIdx;
        return this.jdbcTemplate.query(getFollowingQuery,
                (rs, rowNum) -> new GetFollowingRes(
                        rs.getInt("followIdx"),
                        rs.getInt("userIdx"),
                        rs.getInt("followingIdx"),
                        rs.getString("name"),
                        rs.getString("profileImg"),
                        rs.getInt("prdCnt"),
                        rs.getInt("followerCnt")), // RowMapper(위의 링크 참조): 원하는 결과값 형태로 받기
                getFollowingParams); // 한 개의 회원정보를 얻기 위한 jdbcTemplate 함수(Query, 객체 매핑 정보, Params)의 결과 반환
    }

    // 소셜로그인 ID 회원 존재 확인
    public int checkId(String socialLoginId) {
        String checkIdQuery = "select exists(select socialLoginId from User where status = 'A' and socialLoginId = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkIdParams = socialLoginId; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                checkIdParams); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 소셜로그인 탈퇴한 ID 회원 존재 확인
    public int checkDeletedId(String socialLoginId) {
        String checkIdQuery = "select exists(select socialLoginId from User where status = 'D' and socialLoginId = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        String checkIdParams = socialLoginId; // 해당(확인할) 이메일 값
        return this.jdbcTemplate.queryForObject(checkIdQuery,
                int.class,
                checkIdParams); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 네이버 계정 추가
    public String createNaverUser(String responseBody) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);

        String socialLoginId = jsonNode.get("response").get("id").asText();
        String email = jsonNode.get("response").get("email").asText();
        String phoneNum = jsonNode.get("response").get("mobile").asText();
        String name = jsonNode.get("response").get("name").asText();
        String profileImg = jsonNode.get("response").get("profile_image").asText();

        System.out.println(socialLoginId);
        System.out.println(email);
        System.out.println(phoneNum);
        System.out.println(name);
        System.out.println(profileImg);

        String createUserQuery = "insert into Social_Login (socialLoginId, email, phoneNum, name, profileImg, providerType) " +
                "VALUES (?,?,?,?,?,?)"; // 실행될 동적 쿼리문
        Object[] createUserParams = new Object[]{socialLoginId, email, phoneNum, name, profileImg, "Naver"}; // 동적 쿼리의 ?부분에 주입될 값
        this.jdbcTemplate.update(createUserQuery, createUserParams);
        // email -> postUserReq.getEmail(), password -> postUserReq.getPassword(), nickname -> postUserReq.getNickname() 로 매핑(대응)시킨다음 쿼리문을 실행한다.
        // 즉 DB의 User Table에 (email, password, nickname)값을 가지는 유저 데이터를 삽입(생성)한다.

        return socialLoginId;
    }

    // 네이버 ID를 갖는 회원 추가
    public int createUserBySocialLoginId(String socialLoginId, String name) {
        String createUserByIdQuery = "insert into User (name, socialLoginId) VALUES (?,?)"; // 실행될 동적 쿼리문
        Object[] createUserByIdParams = new Object[]{name, socialLoginId};
        this.jdbcTemplate.update(createUserByIdQuery, createUserByIdParams);
        // email -> postUserReq.getEmail(), password -> postUserReq.getPassword(), nickname -> postUserReq.getNickname() 로 매핑(대응)시킨다음 쿼리문을 실행한다.
        // 즉 DB의 User Table에 (email, password, nickname)값을 가지는 유저 데이터를 삽입(생성)한다.

        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 userIdx번호를 반환한다.
    }

    public int getUserIdxBySocialLoginId(String socialLoginId) {
        String getUserIdxByIdQuery = "select userIdx from User where socialLoginId = ?";
        return this.jdbcTemplate.queryForObject(getUserIdxByIdQuery, int.class, socialLoginId);
    }
    // 회원 여부 확인
    public int checkUser(int userIdx) {
        String checkFavoriteQuery = "select exists(select userIdx from User where status = 'A' and userIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFavoriteQuery,
                int.class,
                userIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 팔로우
    public int createFollow(int userIdxByJwt, int followeeIdx) {
        String createFavPrdQuery = "insert into Follow (followerIdx, followeeIdx) VALUES (?,?);"; // 실행될 동적 쿼리문
        Object[] createFavPrdParams = new Object[]{userIdxByJwt, followeeIdx}; // 동적 쿼리의 ?부분에 주입될 값
        System.out.println(userIdxByJwt);
        System.out.println(followeeIdx);
        this.jdbcTemplate.update(createFavPrdQuery, createFavPrdParams);
        System.out.println("insert");
        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 Idx번호를 반환한다.
    }
    // 팔로우 delete
    public int deleteFollow(int userIdxByJwt, int followeeIdx) {
        String updateProductStatusQuery = "UPDATE Follow SET status = 'D' WHERE (followerIdx = ? and followeeIdx = ?);"; // 실행될 동적 쿼리문
        Object[] updateProductStatusParams = new Object[]{userIdxByJwt, followeeIdx}; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(updateProductStatusQuery, updateProductStatusParams);
    }
    // 팔로우 active
    public int activeFollow(int userIdxByJwt, int followeeIdx) {
        String updateProductStatusQuery = "UPDATE Follow SET status = 'A' WHERE (followerIdx = ? and followeeIdx = ?);"; // 실행될 동적 쿼리문
        Object[] updateProductStatusParams = new Object[]{userIdxByJwt, followeeIdx}; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(updateProductStatusQuery, updateProductStatusParams);

    }
    // 활성화된 팔로우 여부 확인
    public Boolean checkActiveFollow(int userIdxByJwt, int followeeIdx) {
        String checkFollowQuery = "select exists(select followIdx from Follow where status = 'A' and followerIdx = ? and followeeIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFollowQuery,
                Boolean.class,
                userIdxByJwt, followeeIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 삭제된 팔로우 여부 확인
    public Boolean checkDeletedFollow(int userIdxByJwt, int followeeIdx) {
        String checkFollowQuery = "select exists(select followIdx from Follow where status = 'D' and followerIdx = ? and followeeIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFollowQuery,
                Boolean.class,
                userIdxByJwt, followeeIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 팔로우 IDX 조회
    public int getFollowIdx(int userIdxByJwt, int followeeIdx) {
        String getFavoriteQuery = "select followIdx from Follow where followerIdx = ? and followeeIdx = ?"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(getFavoriteQuery,
                int.class,
                userIdxByJwt, followeeIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 활성화된 찜 여부 확인
    public Boolean checkActiveFavorite(int productIdx, int userIdx) {
        String checkFavoriteQuery = "select exists(select favoriteIdx from Favorite where status = 'A' and productIdx = ? and userIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFavoriteQuery,
                Boolean.class,
                productIdx, userIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 상품IDX로 존재 여부 확인
    public int checkProductIdx(int productIdx) {
        String checkFavoriteQuery = "select exists(select productIdx from Product where status = 'A' and productIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFavoriteQuery,
                int.class,
                productIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 후기작성
    public int createReview(int userIdxByJwt, PostReviewReq postReviewReq) {
        String createReviewQuery = "insert into Review (rate, contents, reviewerIdx, revieweeIdx, productIdx) VALUES (?,?,?,?,?);"; // 실행될 동적 쿼리문
        Object[] createReviewParams = new Object[]{postReviewReq.getRate(), postReviewReq.getContents(), userIdxByJwt,
                postReviewReq.getRevieweeIdx(), postReviewReq.getProductIdx()}; // 동적 쿼리의 ?부분에 주입될 값

        this.jdbcTemplate.update(createReviewQuery, createReviewParams);
        System.out.println("insert");
        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 Idx번호를 반환한다.
    }
    // 상품IDX와 회원IDX 일치 여부 확인
    public int checkProductIdxByUserIdx(int revieweeIdx, int productIdx) {
        String checkQuery = "select exists(select productIdx from Product where status = 'A' and userIdx = ? and productIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkQuery,
                int.class,
                revieweeIdx, productIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
}
