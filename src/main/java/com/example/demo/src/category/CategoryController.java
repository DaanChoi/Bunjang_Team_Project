package com.example.demo.src.category;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.category.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/bunjang/categories")

public class CategoryController {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired
    private final CategoryProvider categoryProvider;
    @Autowired
    private final CategoryService categoryService;
    @Autowired
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    public CategoryController(CategoryProvider categoryProvider, CategoryService categoryService, JwtService jwtService) {
        this.categoryProvider = categoryProvider;
        this.categoryService = categoryService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    /**
     * 모든 카테고리 조회 API
     * [GET] /categories
     *
     */
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/bunjang/categories
    public BaseResponse<List<GetCtgRes>> getCategories() {
        try {
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetCtgRes> getCtgRes = categoryProvider.getCategories(userIdxByJwt);
            return new BaseResponse<>(getCtgRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 카테고리로 상품 조회 API
     * [GET] /categories/{categoryNum}
     */
    @ResponseBody
    @GetMapping("/{categoryNum}") // (GET) 127.0.0.1:9000/bunjang/categories/{categoryIdx}
    public BaseResponse<List<GetCtgPrdRes>> getCategoryProducts(@PathVariable("categoryNum") Integer categoryNum) {
        try {
            System.out.println(categoryNum);
//            System.out.println(categoryNum.getClass());
//            int len = categoryNum.length();
//            System.out.println(len);
//            List<GetCtgPrdRes> getCtgPrdRes = null;
//
//            // 대분류만 입력한 경우
//            if(len == 3) {
//                String mainCategoryNum = categoryNum.substring(0,3);
//                int mainCategoryIdx = Integer.parseInt(mainCategoryNum);
//
//                getCtgPrdRes = categoryProvider.getMainCategoryProducts(mainCategoryIdx);
//
//            } else if(len == 6) { // 대분류+중분류 입력한 경우
//                String mainCategoryNum = categoryNum.substring(0,3);
//                int mainCategoryIdx = Integer.parseInt(mainCategoryNum);
//                String midCategoryNum = categoryNum.substring(3,6);
//                int middleCategoryIdx = Integer.parseInt(midCategoryNum);
//
//                getCtgPrdRes = categoryProvider.getMidCategoryProducts(mainCategoryIdx, middleCategoryIdx);
//
//            } else if(len == 9) { // 대분류+중분류+소분류 입력한 경우
//                String mainCategoryNum = categoryNum.substring(0,3);
//                int mainCategoryIdx = Integer.parseInt(mainCategoryNum);
//                String midCategoryNum = categoryNum.substring(3,6);
//                int middleCategoryIdx = Integer.parseInt(midCategoryNum);
//                String subCategoryNum = categoryNum.substring(6,9);
//                int subCategoryIdx = Integer.parseInt(subCategoryNum);
//
//                getCtgPrdRes = categoryProvider.getSubCategoryProducts(mainCategoryIdx, middleCategoryIdx, subCategoryIdx);
//
//            } else { // 입력 형태가 잘못된 경우
//
//            }

            int userIdxByJwt = jwtService.getUserIdx();

            System.out.println(categoryNum);

            if(categoryNum >= 1000 || categoryNum < 100) {
                return new BaseResponse<>(INVALID_CATEGORY_LENGTH);
            }
            if(categoryNum % 100 == 0) {
                System.out.println(categoryNum);
                List<GetCtgPrdRes> getCtgPrdRes = categoryProvider.getMainCategoryProducts(userIdxByJwt, categoryNum);
                return new BaseResponse<>(getCtgPrdRes);
            } else if(categoryNum % 10 == 0) {
                System.out.println(categoryNum);
                List<GetCtgPrdRes> getCtgPrdRes = categoryProvider.getMidCategoryProducts(userIdxByJwt, categoryNum);
                return new BaseResponse<>(getCtgPrdRes);
            } else {
                System.out.println(categoryNum);
                List<GetCtgPrdRes> getCtgPrdRes = categoryProvider.getSubCategoryProducts(userIdxByJwt, categoryNum);
                return new BaseResponse<>(getCtgPrdRes);
            }
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
