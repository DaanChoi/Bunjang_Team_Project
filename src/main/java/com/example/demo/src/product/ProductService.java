package com.example.demo.src.product;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.category.model.GetCtgPrdRes;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
@Transactional(readOnly = false)
public class ProductService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ProductDao productDao;
    private final ProductProvider productProvider;
    private final JwtService jwtService;


    @Autowired //readme 참고
    public ProductService(ProductDao productDao, ProductProvider productProvider, JwtService jwtService) {
        this.productDao = productDao;
        this.productProvider = productProvider;
        this.jwtService = jwtService;

    }

    // 찜
    public PostFavPrdRes favoriteProduct(int userIdxByJwt, int productIdx) throws BaseException {
        if(productDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(productDao.checkProductIdx(productIdx) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        try {
            System.out.println(userIdxByJwt);

            System.out.println(productProvider.checkActiveFavorite(productIdx, userIdxByJwt));
            System.out.println(productProvider.checkDeletedFavorite(productIdx, userIdxByJwt));
            // 찜하려고 한 상품이 이미 찜했다가 찜 해제한 상품일 경우 => 해당 찜 목록의 status를 A로 바꿈
            if(productProvider.checkDeletedFavorite(productIdx, userIdxByJwt) == true){
                int result = productDao.activeFavorite(productIdx, userIdxByJwt);
                if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                    throw new BaseException(MODIFY_FAVORITE_FAIL);
                }
                return new PostFavPrdRes(productDao.getFavoriteIdx(productIdx, userIdxByJwt));
            }
            // 찜하려고 한 상품이 이미 찜했던 상품일 경우 => 삭제 status를 D로 바꿈
            if(productProvider.checkActiveFavorite(productIdx, userIdxByJwt) == true){
                int result = productDao.deleteFavorite(productIdx, userIdxByJwt);
                if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                    throw new BaseException(MODIFY_FAVORITE_FAIL);
                }
                return new PostFavPrdRes(productDao.getFavoriteIdx(productIdx, userIdxByJwt));
            }
            // 찜한 기록이 없다면 새롭게 생성
            Integer favoriteIdx = productDao.createFavoriteProduct(productIdx, userIdxByJwt);
            return new PostFavPrdRes(favoriteIdx);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 상품 등록
    @Transactional(rollbackFor = Exception.class)
    public PostPrdRes createProduct(PostPrdReq postPrdReq) throws BaseException {
        if(productDao.checkUser(postPrdReq.getUserIdx()) == 0) {
            throw new BaseException(INVALID_USER);
        }
        if(productDao.checkMainCategory(postPrdReq.getMainCategoryIdx()) == 0) {
            throw new BaseException(INVALID_MAIN_CATEGORY);
        }
        if(productDao.checkMidCategory(postPrdReq.getMiddleCategoryIdx()) == 0) {
            throw new BaseException(INVALID_MIDDLE_CATEGORY);
        }
        if(productDao.checkSubCategory(postPrdReq.getSubCategoryIdx()) == 0) {
            throw new BaseException(INVALID_SUB_CATEGORY);
        }
        if((postPrdReq.getMainCategoryIdx() / 100) != (postPrdReq.getMiddleCategoryIdx() / 100)) {
            throw new BaseException(MAIN_CTG_MIDDLE_CTG_MISS_MATCH);
        }
        if((postPrdReq.getMiddleCategoryIdx() / 10) != (postPrdReq.getSubCategoryIdx() / 10)) {
            throw new BaseException(MIDDLE_CTG_SUB_CTG_MISS_MATCH);
        }
        try {
            Integer productIdx = productDao.createProduct(postPrdReq);
            System.out.println(productIdx);
            List<String> imageList = postPrdReq.getImageList();
            if (!imageList.isEmpty()) {
                for (String img : imageList) {
                    System.out.println(img);
                    productDao.createProductImage(img, productIdx);
                }
            }
            return new PostPrdRes(productIdx);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 상품 이미지 등록
    public void createProductImage(String img, Integer productIdx) throws BaseException {
        if(productDao.checkProductIdx(productIdx) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        try {
            Integer productImageIdx = productDao.createProductImage(img, productIdx);
            System.out.println(productImageIdx);
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }

    // 상품 수정
    public void editProduct(int productIdx, PatchPrdReq patchPrdReq) throws BaseException {
        if (productDao.checkProductIdx(productIdx) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        if (patchPrdReq.getTitle() != null) {
            if (patchPrdReq.getTitle().length() < 2 || patchPrdReq.getTitle().length() > 40) {
                throw new BaseException(POST_PRD_INVALID_TITLE);
            }
        }
        if (patchPrdReq.getContents() != null) {
            if (patchPrdReq.getContents().length() < 10 || patchPrdReq.getContents().length() > 2000) {
                throw new BaseException(POST_PRD_INVALID_CONTENTS);
            }
        }
        if (patchPrdReq.getProductStatus() != null) {
            if (!patchPrdReq.getProductStatus().equals("U") && !patchPrdReq.getProductStatus().equals("N")) {
                throw new BaseException(POST_PRD_INVALID_STATUS);
            }
        }
        if (patchPrdReq.getMainCategoryIdx() != null) {
            if (patchPrdReq.getMainCategoryIdx() < 100 || patchPrdReq.getMainCategoryIdx() > 1000) {
                throw new BaseException(INVALID_CATEGORY_LENGTH);
            }
            if (productDao.checkMainCategory(patchPrdReq.getMainCategoryIdx()) == 0) {
                throw new BaseException(INVALID_MAIN_CATEGORY);
            }
        }
        if (patchPrdReq.getMiddleCategoryIdx() != null) {
            if (patchPrdReq.getMiddleCategoryIdx() < 100 || patchPrdReq.getMiddleCategoryIdx() > 1000) {
                throw new BaseException(INVALID_CATEGORY_LENGTH);
            }
            if (productDao.checkMidCategory(patchPrdReq.getMiddleCategoryIdx()) == 0) {
                throw new BaseException(INVALID_MIDDLE_CATEGORY);
            }
        }
        if (patchPrdReq.getSubCategoryIdx() != null) {
            if (patchPrdReq.getSubCategoryIdx() < 100 || patchPrdReq.getSubCategoryIdx() > 1000) {
                throw new BaseException(INVALID_CATEGORY_LENGTH);
            }
            if (productDao.checkSubCategory(patchPrdReq.getSubCategoryIdx()) == 0) {
                throw new BaseException(INVALID_SUB_CATEGORY);
            }
        }
        if (patchPrdReq.getMainCategoryIdx() != null && patchPrdReq.getMiddleCategoryIdx() != null) {
            if ((patchPrdReq.getMainCategoryIdx() / 100) != (patchPrdReq.getMiddleCategoryIdx() / 100)) {
                throw new BaseException(MAIN_CTG_MIDDLE_CTG_MISS_MATCH);
            }
        }
        if (patchPrdReq.getMiddleCategoryIdx() != null && patchPrdReq.getSubCategoryIdx() != null) {
            if ((patchPrdReq.getMiddleCategoryIdx() / 10) != (patchPrdReq.getSubCategoryIdx() / 10)) {
                throw new BaseException(MIDDLE_CTG_SUB_CTG_MISS_MATCH);
            }
        }

        try {
            Product product = productProvider.checkProduct(productIdx);

            if (patchPrdReq.getTitle() != null) {
                product.setTitle(patchPrdReq.getTitle());
            }
            if (patchPrdReq.getMainCategoryIdx() != null) {
                if ((patchPrdReq.getMainCategoryIdx() / 100) != (product.getMiddleCategoryIdx() / 100)) {
                    throw new BaseException(MAIN_CTG_MIDDLE_CTG_MISS_MATCH);
                }
                if ((patchPrdReq.getMainCategoryIdx() / 100) != (product.getSubCategoryIdx() / 100)) {
                    throw new BaseException(MAIN_CTG_MIDDLE_CTG_MISS_MATCH);
                }
                product.setMainCategoryIdx(patchPrdReq.getMainCategoryIdx());
            }
            if (patchPrdReq.getMiddleCategoryIdx() != null) {
                if ((patchPrdReq.getMainCategoryIdx() / 100) != (product.getMiddleCategoryIdx() / 100)) {
                    throw new BaseException(MAIN_CTG_MIDDLE_CTG_MISS_MATCH);
                }
                if ((patchPrdReq.getMiddleCategoryIdx() / 10) != (product.getSubCategoryIdx() / 10)) {
                    throw new BaseException(MIDDLE_CTG_SUB_CTG_MISS_MATCH);
                }
                product.setMiddleCategoryIdx(patchPrdReq.getMiddleCategoryIdx());
            }
            if (patchPrdReq.getSubCategoryIdx() != null) {
                if ((patchPrdReq.getSubCategoryIdx() / 100) != (product.getMainCategoryIdx() / 100)) {
                    throw new BaseException(MAIN_CTG_MIDDLE_CTG_MISS_MATCH);
                }
                if ((patchPrdReq.getSubCategoryIdx() / 10) != (product.getMiddleCategoryIdx() / 10)) {
                    throw new BaseException(MIDDLE_CTG_SUB_CTG_MISS_MATCH);
                }
                product.setSubCategoryIdx(patchPrdReq.getSubCategoryIdx());
            }
            if (patchPrdReq.getIsChangable() != null) {
                product.setIsChangable(patchPrdReq.getIsChangable());
            }
            if (patchPrdReq.getProductStatus() != null) {
                product.setProductStatus(patchPrdReq.getProductStatus());
            }
            if (patchPrdReq.getPrice() != null) {
                product.setPrice(patchPrdReq.getPrice());
            }
            if (patchPrdReq.getIsFreeShip() != null) {
                product.setIsFreeShip(patchPrdReq.getIsFreeShip());
            }
            if (patchPrdReq.getContents() != null) {
                product.setContents(patchPrdReq.getContents());
            }
            if (patchPrdReq.getQuantity() != null) {
                product.setQuantity(patchPrdReq.getQuantity());
            }
            if (patchPrdReq.getIsSafepay() != null) {
                product.setIsSafepay(patchPrdReq.getIsSafepay());
            }
            System.out.println(product);

            int result = productDao.editProduct(productIdx, product);
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_PRODUCT_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 상품 판매 상태 수정
    public void editProductStatus(int productIdx, String tradeStatus) throws BaseException {
        if(productDao.checkProductIdx(productIdx) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        try {
            int result = productDao.editProductStatus(productIdx, tradeStatus);
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_PRODUCT_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 상품 삭제
    public void deleteProduct(int productIdx) throws BaseException {
        if(productDao.checkProductIdx(productIdx) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        try {
            System.out.println(productIdx);
            int result = productDao.deleteProduct(productIdx);
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(DELETE_PRODUCT_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            exception.printStackTrace();
            throw new BaseException(DATABASE_ERROR);
        }
    }


    // 찜 삭제
    public void deleteFavorite(int productIdx, int userIdxByJwt) throws BaseException {
        if(productDao.checkUser(userIdxByJwt) == 0) {
            throw new BaseException(INVALID_USER_JWT);
        }
        if(productDao.checkProductIdx(productIdx) == 0) {
            throw new BaseException(INVALID_PRODUCT);
        }
        if(productProvider.checkDeletedFavorite(productIdx, userIdxByJwt) == true) {
            throw new BaseException(DELETED_FAVORITE);
        }
        try {
            int result = productDao.deleteFavorite(productIdx, userIdxByJwt);
            if (result == 0) { // result값이 0이면 과정이 실패한 것이므로 에러 메서지를 보냅니다.
                throw new BaseException(MODIFY_FAVORITE_FAIL);
            }
        } catch (Exception exception) { // DB에 이상이 있는 경우 에러 메시지를 보냅니다.
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
