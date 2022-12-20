package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class ProductProvider {
    private final ProductDao productDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired //readme 참고
    public ProductProvider(ProductDao productDao, JwtService jwtService) {
        this.productDao = productDao;
        this.jwtService = jwtService;
    }

    // 상품들 조회
    public List<GetPrdRes> getProducts(int userIdxByJwt) throws BaseException {
        if(productDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        try {
            List<GetPrdRes> getPrdRes = productDao.getProducts();
            for(GetPrdRes prd : getPrdRes) {
                prd.setIsFav(productDao.checkActiveFavorite(prd.getProductIdx(), userIdxByJwt));
            }
            return getPrdRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 해당 title을 갖는 상품들 조회
    public List<GetPrdRes> getProductsByKeyword(int userIdxByJwt, String keyword) throws BaseException {
        if(productDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        try {
            List<GetPrdRes> getPrdRes = productDao.getProductsByKeyword(keyword);
            for(GetPrdRes prd : getPrdRes) {
                prd.setIsFav(productDao.checkActiveFavorite(prd.getProductIdx(), userIdxByJwt));
            }
            return getPrdRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 상품 조회
    public GetPrdDetailRes getProduct(int userIdxByJwt, int productIdx) throws BaseException {
        if(productDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(productDao.checkProductIdx(productIdx) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        try {
            GetPrdDetailRes getPrdDetailRes = productDao.getProduct(productIdx);
            getPrdDetailRes.setIsFav(productDao.checkActiveFavorite(productIdx, userIdxByJwt));
            return getPrdDetailRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 상품 이미지 조회
    public GetPrdImgListRes getProductImages(int userIdxByJwt, int productIdx) throws BaseException {
        if(productDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(productDao.checkProductIdx(productIdx) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        try {
            List<String> list = new ArrayList<>();

            List<GetPrdImgRes> getPrdImgRes = productDao.getProductImages(productIdx);
            System.out.println("조회");

            for(GetPrdImgRes img : getPrdImgRes) {
                System.out.println(img.getImageUrl());
                list.add(img.getImageUrl());
            }
            GetPrdImgListRes getPrdImgListRes = new GetPrdImgListRes(productIdx, list);
            return getPrdImgListRes;
        } catch (Exception exception) {
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 상품 태그 조회
    public List<GetPrdTagRes> getProductTags(int userIdxByJwt, int productIdx) throws BaseException {
        if(productDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(productDao.checkProductIdx(productIdx) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        try {
            List<GetPrdTagRes> getPrdTagRes = productDao.getProductTags(productIdx);
            return getPrdTagRes;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 특정 상품 등록 정보 조회
    public Product checkProduct(int productIdx) throws BaseException {
        try {

            Product product = productDao.checkProduct(productIdx);
            System.out.println(productIdx);
            return product;
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 찜 활성화 여부 확인
    public Boolean checkActiveFavorite(int productIdx, int userIdx) throws BaseException {
        try {
            return productDao.checkActiveFavorite(productIdx, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 찜 삭제 여부 확인
    public Boolean checkDeletedFavorite(int productIdx, int userIdx) throws BaseException {
        try {
            return productDao.checkDeletedFavorite(productIdx, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 찜 목록 정보 조회
    public Favorite getFavorite(int productIdx, int userIdx) throws BaseException {
        try {
            return productDao.getFavorite(productIdx, userIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 상품을 등록한 회원 IDX 조회
    public int getUserIdxByProductIdx(int productIdx) throws BaseException {
        try {
            return productDao.getUserIdxByProductIdx(productIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    // 상품 IDX 여부 확인
    public int checkProductIdx(int productIdx) throws BaseException {
        try {
            return productDao.checkProductIdx(productIdx);
        } catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
