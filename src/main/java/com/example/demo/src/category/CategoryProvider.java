package com.example.demo.src.category;


import com.example.demo.config.BaseException;
import com.example.demo.src.category.model.*;
import com.example.demo.src.product.model.GetPrdRes;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service

public class CategoryProvider {
    private final CategoryDao categoryDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public CategoryProvider(CategoryDao categoryDao, JwtService jwtService) {
        this.categoryDao = categoryDao;
        this.jwtService = jwtService;
    }

    // 모든 카테고리 조회
    public List<GetCtgRes> getCategories(int userIdxByJwt) throws BaseException {
        if(categoryDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        try {
            List<GetCtgRes> getCtgRes = categoryDao.getCategories();
            return getCtgRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 대분류 카테고리 상품 조회
    public List<GetCtgPrdRes> getMainCategoryProducts(int userIdxByJwt, int categoryNum) throws BaseException {
        if(categoryDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(categoryDao.checkMainCategory(categoryNum) == 0) {
            throw new BaseException(INVALID_CATEGORY);
        }
        try {
            System.out.println(categoryNum);
            List<GetCtgPrdRes> getCtgPrdRes = categoryDao.getMainCategoryProducts(categoryNum);
            for(GetCtgPrdRes prd : getCtgPrdRes) {
                prd.setIsFav(categoryDao.checkFavorite(prd.getProductIdx(), userIdxByJwt));
            }
            System.out.println("조회");
            return getCtgPrdRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 중분류 카테고리 상품 조회
    public List<GetCtgPrdRes> getMidCategoryProducts(int userIdxByJwt, int categoryNum) throws BaseException {
        if(categoryDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(categoryDao.checkMidCategory(categoryNum) == 0) {
            throw new BaseException(INVALID_CATEGORY);
        }
        try {
            System.out.println(categoryNum);
            List<GetCtgPrdRes> getCtgPrdRes = categoryDao.getMidCategoryProducts(categoryNum);
            for(GetCtgPrdRes prd : getCtgPrdRes) {
                prd.setIsFav(categoryDao.checkFavorite(prd.getProductIdx(), userIdxByJwt));
            }
            System.out.println("조회");
            return getCtgPrdRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 소분류 카테고리 상품 조회
    public List<GetCtgPrdRes> getSubCategoryProducts(int userIdxByJwt, int categoryNum) throws BaseException {
        if(categoryDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(categoryDao.checkSubCategory(categoryNum) == 0) {
            throw new BaseException(INVALID_CATEGORY);
        }
        try {
            System.out.println(categoryNum);
            List<GetCtgPrdRes> getCtgPrdRes = categoryDao.getSubCategoryProducts(categoryNum);
            for(GetCtgPrdRes prd : getCtgPrdRes) {
                prd.setIsFav(categoryDao.checkFavorite(prd.getProductIdx(), userIdxByJwt));
            }
            System.out.println("조회");
            return getCtgPrdRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
