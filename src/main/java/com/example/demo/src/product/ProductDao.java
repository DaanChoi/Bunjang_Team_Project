package com.example.demo.src.product;

import com.example.demo.src.product.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import static com.example.demo.src.product.ProductDaoSqls.*;

import javax.sql.DataSource;
import java.util.List;

@Repository

public class ProductDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }


    // 상품들 조회
    public List<GetPrdRes> getProducts() {
        String getProductsQuery = SELECT_PRODUCTS;
        return this.jdbcTemplate.query(getProductsQuery,
                (rs, rowNum) -> new GetPrdRes(
                        rs.getInt("productIdx"),
                        rs.getString("imageUrl"),
                        rs.getBoolean("isSafepay"),
                        rs.getString("title"),
                        rs.getString("price"),
                        rs.getString("location"),
                        rs.getString("contents"),
                        rs.getTimestamp("createdAt"),
                        rs.getInt("favCnt"),
                        rs.getBoolean("isFav"))
        );
    }

    // 해당 title을 갖는 상품들 조회
    public List<GetPrdRes> getProductsByKeyword(String keyword) {
        System.out.println(keyword);
        String getProductsByKeywordQuery = SELECT_PRODUCTS_BY_TITLE;
        String getProductsByKeywordParams = "%" + keyword + "%";
        return this.jdbcTemplate.query(getProductsByKeywordQuery,
                (rs, rowNum) -> new GetPrdRes(
                        rs.getInt("productIdx"),
                        rs.getString("imageUrl"),
                        rs.getBoolean("isSafepay"),
                        rs.getString("title"),
                        rs.getString("price"),
                        rs.getString("location"),
                        rs.getString("contents"),
                        rs.getTimestamp("createdAt"),
                        rs.getInt("favCnt"),
                        rs.getBoolean("isFav")),
                getProductsByKeywordParams);
    }

    // 특정 상품 조회
    public GetPrdDetailRes getProduct(int productIdx) {
        String getProductQuery = SELECT_PRODUCT;
        int getProductParams = productIdx;
        return this.jdbcTemplate.queryForObject(getProductQuery,
                (rs, rowNum) -> new GetPrdDetailRes(
                        rs.getInt("productIdx"),
                        rs.getString("price"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getTimestamp("createdAt"),
                        rs.getString("productStatus"),
                        rs.getInt("quantity"),
                        rs.getBoolean("isFreeShip"),
                        rs.getBoolean("isChangable"),
                        rs.getInt("favCnt"),
                        rs.getInt("chatCnt"),
                        rs.getString("contents"),
                        rs.getBoolean("isSafepay"),
                        rs.getBoolean("isFav")),
                getProductParams);
    }

    // 특정 상품 이미지 조회
    public List<GetPrdImgRes> getProductImages(int productIdx) {
        String getProductImagesQuery = SELECT_PRODUCT_IMAGES;
        int getProductImagesParams = productIdx;
        return this.jdbcTemplate.query(getProductImagesQuery,
                (rs, rowNum) -> new GetPrdImgRes(
                        rs.getInt("productIdx"),
                        rs.getString("imageUrl")),
                getProductImagesParams);
    }

    // 특정 상품 태그 조회
    public List<GetPrdTagRes> getProductTags(int productIdx) {
        String getProductTagsQuery = SELECT_PRODUCT_TAGS;
        int getProductTagsParams = productIdx;
        return this.jdbcTemplate.query(getProductTagsQuery,
                (rs, rowNum) -> new GetPrdTagRes(
                        rs.getInt("productIdx"),
                        rs.getInt("hashtagIdx"),
                        rs.getString("tagName")),
                getProductTagsParams);
    }

    // 찜
    public int createFavoriteProduct(int productIdx, int userIdxByJwt) {
        String createFavPrdQuery = "insert into Favorite (userIdx, productIdx) VALUES (?,?);"; // 실행될 동적 쿼리문
        Object[] createFavPrdParams = new Object[]{userIdxByJwt, productIdx}; // 동적 쿼리의 ?부분에 주입될 값
        System.out.println(userIdxByJwt);
        System.out.println(productIdx);
        this.jdbcTemplate.update(createFavPrdQuery, createFavPrdParams);
        System.out.println("insert");
        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 Idx번호를 반환한다.
    }

    // 상품 등록
//    public int createProduct(PostPrdReq postPrdReq) {
//        String createPrdQuery = INSERT_PRODUCT; // 실행될 동적 쿼리문
//        Object[] createPrdParams = new Object[]{postPrdReq.getTitle(), postPrdReq.getMainCategoryIdx(), postPrdReq.getMiddleCategoryIdx(),
//                postPrdReq.getSubCategoryIdx(), postPrdReq.getLocation(), postPrdReq.getProductStatus(),
//                postPrdReq.getIsChangable(), postPrdReq.getPrice(), postPrdReq.getIsFreeShip(),
//                postPrdReq.getContents(), postPrdReq.getQuantity(), postPrdReq.getIsSafepay(),
//                postPrdReq.getUserIdx()}; // 동적 쿼리의 ?부분에 주입될 값
//        this.jdbcTemplate.update(createPrdQuery, createPrdParams);
//
//        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
//        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 Idx번호를 반환한다.
//    }
    // 상품 등록
    public int createProduct(PostPrdReq postPrdReq) {
        String createPrdQuery = "INSERT INTO Product (title, mainCategoryIdx, middleCategoryIdx, subCategoryIdx, price, contents, isSafepay, userIdx) \n" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?);"; // 실행될 동적 쿼리문
        Object[] createPrdParams = new Object[]{postPrdReq.getTitle(), postPrdReq.getMainCategoryIdx(), postPrdReq.getMiddleCategoryIdx(), postPrdReq.getSubCategoryIdx(),
                postPrdReq.getPrice(), postPrdReq.getContents(), postPrdReq.getIsSafepay(), postPrdReq.getUserIdx()}; // 동적 쿼리의 ?부분에 주입될 값

        System.out.println(postPrdReq.getTitle());
        System.out.println(postPrdReq.getPrice());
        System.out.println(postPrdReq.getContents());
        System.out.println(postPrdReq.getIsSafepay());
        System.out.println(postPrdReq.getUserIdx());

        this.jdbcTemplate.update(createPrdQuery, createPrdParams);

        System.out.println(postPrdReq.getUserIdx());

        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 Idx번호를 반환한다.
    }

    // 상품 이미지 등록
    public int createProductImage(String img, Integer productIdx) {
        String createPrdImgQuery = "INSERT INTO Product_Image (imageUrl, productIdx) \n" +
                "VALUES (?, ?);"; // 실행될 동적 쿼리문
        Object[] createPrdImgParams = new Object[]{img, productIdx}; // 동적 쿼리의 ?부분에 주입될 값

        System.out.println(img);
        System.out.println(productIdx);

        this.jdbcTemplate.update(createPrdImgQuery, createPrdImgParams);

        System.out.println(productIdx);

        String lastInsertIdQuery = "select last_insert_id()"; // 가장 마지막에 삽입된(생성된) id값은 가져온다.
        return this.jdbcTemplate.queryForObject(lastInsertIdQuery, int.class); // 해당 쿼리문의 결과 마지막으로 삽인된 유저의 Idx번호를 반환한다.
    }
    // 특정 상품 등록 정보 조회(상품 수정에 사용)
    public Product checkProduct(int productIdx) {
        String checkProductQuery = CHECK_PRODUCT;
        int checkProductParams = productIdx;
        return this.jdbcTemplate.queryForObject(checkProductQuery,
                (rs, rowNum) -> new Product(
                        rs.getString("title"),
                        rs.getInt("mainCategoryIdx"),
                        rs.getInt("middleCategoryIdx"),
                        rs.getInt("subCategoryIdx"),
                        rs.getString("location"),
                        rs.getString("productStatus"),
                        rs.getBoolean("isChangable"),
                        rs.getString("price"),
                        rs.getBoolean("isFreeShip"),
                        rs.getString("contents"),
                        rs.getInt("quantity"),
                        rs.getBoolean("isSafepay")),
                checkProductParams);
    }

    // 상품 수정
    public int editProduct(int productIdx, Product product) {
        System.out.println(product);
        String updateProductQuery = UPDATE_PRODUCT; // 실행될 동적 쿼리문
        Object[] updateProductParams = new Object[]{product.getTitle(), product.getMainCategoryIdx(), product.getMiddleCategoryIdx(), product.getSubCategoryIdx(), product.getLocation(),
                product.getProductStatus(), product.getIsChangable(), product.getPrice(), product.getIsFreeShip(),
                product.getContents(), product.getQuantity(), product.getIsSafepay(), productIdx}; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(updateProductQuery, updateProductParams);

    }

    // 상품 거래 상태 수정
    public int editProductStatus(int productIdx, String tradeStatus) {
        String updateProductStatusQuery = "UPDATE Product SET tradeStatus = ? WHERE (productIdx = ?);"; // 실행될 동적 쿼리문
        Object[] updateProductStatusParams = new Object[]{tradeStatus, productIdx}; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(updateProductStatusQuery, updateProductStatusParams);

    }

    // 상품 삭제
    public int deleteProduct(int productIdx) {
        System.out.println(productIdx);
        String updateProductStatusQuery = "UPDATE Product SET status = 'D' WHERE productIdx = ?;"; // 실행될 동적 쿼리문
        int updateProductStatusParams = productIdx; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(updateProductStatusQuery, updateProductStatusParams);

    }
    // 찜 delete
    public int deleteFavorite(int productIdx, int userIdxByJwt) {
        String updateProductStatusQuery = "UPDATE Favorite SET status = 'D' WHERE (productIdx = ? and userIdx = ?);"; // 실행될 동적 쿼리문
        Object[] updateProductStatusParams = new Object[]{productIdx, userIdxByJwt}; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(updateProductStatusQuery, updateProductStatusParams);
    }
    // 찜 active
    public int activeFavorite(int productIdx, int userIdx) {
        String updateProductStatusQuery = "UPDATE Favorite SET status = 'A' WHERE (productIdx = ? and userIdx = ?);"; // 실행될 동적 쿼리문
        Object[] updateProductStatusParams = new Object[]{productIdx, userIdx}; // 동적 쿼리의 ?부분에 주입될 값
        return this.jdbcTemplate.update(updateProductStatusQuery, updateProductStatusParams);

    }

    // 활성화된 찜 여부 확인
    public Boolean checkActiveFavorite(int productIdx, int userIdx) {
        String checkFavoriteQuery = "select exists(select favoriteIdx from Favorite where status = 'A' and productIdx = ? and userIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFavoriteQuery,
                Boolean.class,
                productIdx, userIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 삭제된 찜 여부 확인
    public Boolean checkDeletedFavorite(int productIdx, int userIdx) {
        String checkFavoriteQuery = "select exists(select favoriteIdx from Favorite where status = 'D' and productIdx = ? and userIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFavoriteQuery,
                Boolean.class,
                productIdx, userIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 찜목록 정보 확인
    public Favorite getFavorite(int productIdx, int userIdx) {
        String getFavoriteQuery = "select favoriteIdx, status, userIdx, productIdx from Favorite where productIdx = ? and userIdx = ?"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(getFavoriteQuery,
                (rs, rowNum) -> new Favorite(
                        rs.getInt("favoriteIdx"),
                        rs.getString("status"),
                        rs.getInt("userIdx"),
                        rs.getInt("productIdx")),
                productIdx, userIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 찜 IDX 조회
    public int getFavoriteIdx(int productIdx, int userIdx) {
        String getFavoriteQuery = "select favoriteIdx from Favorite where productIdx = ? and userIdx = ?"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(getFavoriteQuery,
                int.class,
                productIdx, userIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 상품 등록한 회원 IDX 조회
    public int getUserIdxByProductIdx(int productIdx) {
        String getFavoriteQuery = "select userIdx from Product where productIdx = ? and status = 'A'"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(getFavoriteQuery,
                int.class,
                productIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 회원 여부 확인
    public int checkUser(int userIdx) {
        String checkFavoriteQuery = "select exists(select userIdx from User where status = 'A' and userIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFavoriteQuery,
                int.class,
                userIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }

    // 상품IDX로 존재 여부 확인
    public int checkProductIdx(int productIdx) {
        String checkFavoriteQuery = "select exists(select productIdx from Product where status = 'A' and productIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFavoriteQuery,
                int.class,
                productIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    public int checkMainCategory(int categoryNum) {
        String checkCategoryQuery = "select exists(select mainCategoryIdx from Main_Category where status = 'A' and mainCategoryIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkCategoryQuery,
                int.class,
                categoryNum); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    public int checkMidCategory(int categoryNum) {
        String checkCategoryQuery = "select exists(select middleCategoryIdx from Middle_Category where status = 'A' and middleCategoryIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkCategoryQuery,
                int.class,
                categoryNum); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    public int checkSubCategory(int categoryNum) {
        String checkCategoryQuery = "select exists(select subCategoryIdx from Sub_Category where status = 'A' and subCategoryIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkCategoryQuery,
                int.class,
                categoryNum); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
}
