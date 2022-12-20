package com.example.demo.src.category;

import com.example.demo.src.category.model.*;
import com.example.demo.src.product.model.Favorite;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import static com.example.demo.src.category.CategoryDaoSqls.*;

import javax.sql.DataSource;
import java.util.List;

@Repository

public class CategoryDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired //readme 참고
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    // 모든 카테고리 조회
    public List<GetCtgRes> getCategories() {
        String getCategoriesQuery = SELECT_CATEGORIES;
        return this.jdbcTemplate.query(getCategoriesQuery,
                (rs, rowNum) -> new GetCtgRes(
                        rs.getInt("mainCategoryIdx"),
                        rs.getString("mainCategoryName"),
                        rs.getInt("middleCategoryIdx"),
                        rs.getString("middleCategoryName"),
                        rs.getInt("subCategoryIdx"),
                        rs.getString("subCategoryName"))
        );
    }

    // 대분류 카테고리 상품들 조회
    public List<GetCtgPrdRes> getMainCategoryProducts(int categoryNum) {
        String getMainCategoryProductsQuery = SELECT_MAIN_CATEGORY_PRODUCTS;
        return this.jdbcTemplate.query(getMainCategoryProductsQuery,
                (rs, rowNum) -> new GetCtgPrdRes(
                        rs.getInt("productIdx"),
                        rs.getTimestamp("createdAt"),
                        rs.getString("imageUrl"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("price"),
                        rs.getBoolean("isFreeShip"),
                        rs.getBoolean("isSafepay"),
                        rs.getString("tradeStatus"),
                        rs.getBoolean("isFav")),
                categoryNum
        );
    }

    // 중분류 카테고리 상품들 조회
    public List<GetCtgPrdRes> getMidCategoryProducts(int categoryNum) {
        String getMidCategoryProductsQuery = SELECT_MID_CATEGORY_PRODUCTS;
        return this.jdbcTemplate.query(getMidCategoryProductsQuery,
                (rs, rowNum) -> new GetCtgPrdRes(
                        rs.getInt("productIdx"),
                        rs.getTimestamp("createdAt"),
                        rs.getString("imageUrl"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("price"),
                        rs.getBoolean("isFreeShip"),
                        rs.getBoolean("isSafepay"),
                        rs.getString("tradeStatus"),
                        rs.getBoolean("isFav")),
                categoryNum
        );
    }

    // 소분류 카테고리 상품들 조회
    public List<GetCtgPrdRes> getSubCategoryProducts(int categoryNum) {
        String getSubCategoryProductsQuery = SELECT_SUB_CATEGORY_PRODUCTS;
        return this.jdbcTemplate.query(getSubCategoryProductsQuery,
                (rs, rowNum) -> new GetCtgPrdRes(
                        rs.getInt("productIdx"),
                        rs.getTimestamp("createdAt"),
                        rs.getString("imageUrl"),
                        rs.getString("title"),
                        rs.getString("location"),
                        rs.getString("price"),
                        rs.getBoolean("isFreeShip"),
                        rs.getBoolean("isSafepay"),
                        rs.getString("tradeStatus"),
                        rs.getBoolean("isFav")),
                categoryNum
        );
    }

    // 찜 여부 확인
    public Boolean checkFavorite(int productIdx, int userIdx) {
        String checkFavoriteQuery = "select exists(select favoriteIdx from Favorite where productIdx = ? and userIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkFavoriteQuery,
                Boolean.class,
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
        String checkUserQuery = "select exists(select userIdx from User where status = 'A' and userIdx = ?)"; // User Table에 해당 email 값을 갖는 유저 정보가 존재하는가?
        return this.jdbcTemplate.queryForObject(checkUserQuery,
                int.class,
                userIdx); // checkEmailQuery, checkEmailParams를 통해 가져온 값(intgud)을 반환한다. -> 쿼리문의 결과(존재하지 않음(False,0),존재함(True, 1))를 int형(0,1)으로 반환됩니다.
    }
    // 카테고리 여부 확인
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
