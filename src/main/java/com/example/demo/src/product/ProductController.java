package com.example.demo.src.product;

import com.example.demo.src.product.model.PatchPrdReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.product.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import static com.example.demo.config.BaseResponseStatus.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/bunjang/products")

public class ProductController {
    final Logger logger = LoggerFactory.getLogger(this.getClass()); // Log를 남기기: 일단은 모르고 넘어가셔도 무방합니다.

    @Autowired
    private final ProductProvider productProvider;
    @Autowired
    private final ProductService productService;
    @Autowired
    private final JwtService jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!


    public ProductController(ProductProvider productProvider, ProductService productService, JwtService jwtService) {
        this.productProvider = productProvider;
        this.productService = productService;
        this.jwtService = jwtService; // JWT부분은 7주차에 다루므로 모르셔도 됩니다!
    }

    /**
     * 모든 상품 조회 API
     * [GET] /products
     *
     * 또는
     *
     * 해당 키워드를 포함하는 제목의 상품들 조회 API
     * [GET] /products?keyword=
     */
    //Query String
    @ResponseBody
    @GetMapping("") // (GET) 127.0.0.1:9000/bunjang/products
    public BaseResponse<List<GetPrdRes>> getProducts(@RequestParam(required = false) String keyword) {
        try {
//            int userIdxByJwt = 4;
            int userIdxByJwt = jwtService.getUserIdx();
            System.out.println(userIdxByJwt);
            if (keyword == null) {
                List<GetPrdRes> getPrdRes = productProvider.getProducts(userIdxByJwt);
                return new BaseResponse<>(getPrdRes);
            }
            // query string인 keyword 값이 존재할 경우
            List<GetPrdRes> getPrdRes = productProvider.getProductsByKeyword(userIdxByJwt, keyword);
            return new BaseResponse<>(getPrdRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 상품 조회 API
     * [GET] /products/{productIdx}
     */
    //Query String
    @ResponseBody
    @GetMapping("/{productIdx}") // (GET) 127.0.0.1:9000/bunjang/products/{productIdx}
    public BaseResponse<GetPrdDetailRes> getProduct(@PathVariable("productIdx") Integer productIdx) {
        try {
            if(productIdx == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_IDX);
            }
//            int userIdxByJwt = 4;
            int userIdxByJwt = jwtService.getUserIdx();
            GetPrdDetailRes getPrdDetailRes = productProvider.getProduct(userIdxByJwt, productIdx);
            return new BaseResponse<>(getPrdDetailRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 상품 이미지 조회 API
     * [GET] /products/{productIdx}/images
     */
    //Query String
    @ResponseBody
    @GetMapping("/{productIdx}/images") // (GET) 127.0.0.1:9000/bunjang/products/{productIdx}/images
    public BaseResponse<GetPrdImgListRes> getProductImages(@PathVariable("productIdx") Integer productIdx) {
        try {
            if(productIdx == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_IDX);
            }
            int userIdxByJwt = jwtService.getUserIdx();
            GetPrdImgListRes getPrdImgListRes = productProvider.getProductImages(userIdxByJwt, productIdx);
            return new BaseResponse<>(getPrdImgListRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 특정 상품 태그 조회 API
     * [GET] /products/{productIdx}/tags
     */
    //Query String
    @ResponseBody
    @GetMapping("/{productIdx}/tags") // (GET) 127.0.0.1:9000/bunjang/products/{productIdx}/tags
    public BaseResponse<List<GetPrdTagRes>> getProductTags(@PathVariable("productIdx") Integer productIdx) {
        try {
            if(productIdx == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_IDX);
            }
            int userIdxByJwt = jwtService.getUserIdx();
            List<GetPrdTagRes> getPrdTagRes = productProvider.getProductTags(userIdxByJwt, productIdx);
            return new BaseResponse<>(getPrdTagRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    /**
     * 찜 API
     * [POST] /products/{productIdx}/favorites
     */
    @ResponseBody
    @PostMapping("/{productIdx}/favorites")
    public BaseResponse<PostFavPrdRes> favoriteProduct(@PathVariable("productIdx") int productIdx) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validation 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            int userIdxByJwt = jwtService.getUserIdx();
            PostFavPrdRes postFavPrdRes = productService.favoriteProduct(userIdxByJwt, productIdx);
            return new BaseResponse<>(postFavPrdRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }


    /**
     * 찜 해제 API
     * [PATCH] /products/{productIdx}/favorites/d
     */
    @ResponseBody
    @PatchMapping("/{productIdx}/favorites/d")
    public BaseResponse<String> deleteFavorite(@PathVariable("productIdx") int productIdx) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();

            productService.deleteFavorite(productIdx, userIdxByJwt);

            String result = "삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 등록 API
     * [POST] /products/new
     */
    @ResponseBody
    @PostMapping("/new")
    public BaseResponse<PostPrdRes> createProduct(@RequestBody PostPrdReq postPrdReq) {
        try {
            // 회원
            if(postPrdReq.getUserIdx() == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_USER);
            }
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            if(postPrdReq.getUserIdx() != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            // 제목 형식 검사
            if(postPrdReq.getTitle().length() < 2 || postPrdReq.getTitle().length() > 40) {
                return new BaseResponse<>(POST_PRD_INVALID_TITLE);
            }
            if(postPrdReq.getTitle() == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_TITLE);
            }
            // 카테고리 범위 검사
            if(postPrdReq.getMainCategoryIdx() >= 1000 || postPrdReq.getMainCategoryIdx() < 100) {
                return new BaseResponse<>(INVALID_CATEGORY_LENGTH);
            }
            if(postPrdReq.getMiddleCategoryIdx() >= 1000 || postPrdReq.getMiddleCategoryIdx() < 100) {
                return new BaseResponse<>(INVALID_CATEGORY_LENGTH);
            }
            if(postPrdReq.getSubCategoryIdx() >= 1000 || postPrdReq.getSubCategoryIdx() < 100) {
                return new BaseResponse<>(INVALID_CATEGORY_LENGTH);
            }

            // 내용 형식 검사
            if(postPrdReq.getContents().length() < 10 || postPrdReq.getContents().length() > 2000) {
                return new BaseResponse<>(POST_PRD_INVALID_CONTENTS);
            }
            if(postPrdReq.getContents() == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_CONTENTS);
            }

            // 가격
            if(postPrdReq.getPrice() == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_PRICE);
            }

            // 이미지
            if(1 > postPrdReq.getImageList().size() || postPrdReq.getImageList().size() > 13) {
                return new BaseResponse<>(POST_PRD_INVALID_IMAGE);
            }
            if(postPrdReq.getImageList().isEmpty()) {
                return new BaseResponse<>(EMPTY_PRODUCT_IMAGE);
            }

            // 안전결제
            if(postPrdReq.getIsSafepay() == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_SAFEPAY);
            }


            System.out.println(postPrdReq);
            PostPrdRes postPrdRes = productService.createProduct(postPrdReq);
            return new BaseResponse<>(postPrdRes);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 이미지 등록 API
     * [POST] /product/images
     */
    @ResponseBody
    @PostMapping("/images")
    public BaseResponse<String> createProductImage(@RequestBody PostPrdImgReq postPrdImgReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            System.out.println(postPrdImgReq);

            List<String> imageList = postPrdImgReq.getImageList();
            Integer productIdx = postPrdImgReq.getProductIdx();

            if(productIdx == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_IDX);
            }
            if(productProvider.checkProductIdx(productIdx) == 0) {
                throw new BaseException(INVALID_PRODUCT);
            }
            // 이미지
            if(1 > postPrdImgReq.getImageList().size() || postPrdImgReq.getImageList().size() > 13) {
                return new BaseResponse<>(POST_PRD_INVALID_IMAGE);
            }
            if(postPrdImgReq.getImageList().isEmpty()) {
                return new BaseResponse<>(EMPTY_PRODUCT_IMAGE);
            }
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //해당 상품의 userIdx와 접근한 유저가 같은지 확인
            int userIdx = productProvider.getUserIdxByProductIdx(productIdx);
            System.out.println(userIdx);

            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            for (String img : imageList) {
                productService.createProductImage(img, productIdx);
            }
            String result = "등록되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 수정 API
     * [PATCH] /products/{productIdx}/edit
     */
    @ResponseBody
    @PatchMapping("/{productIdx}/edit")
    public BaseResponse<String> editProduct(@PathVariable("productIdx") int productIdx, @RequestBody PatchPrdReq patchPrdReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.

            System.out.println(patchPrdReq);
            if(productProvider.checkProductIdx(productIdx) == 0) {
                throw new BaseException(INVALID_PRODUCT);
            }
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            int userIdx = productProvider.getUserIdxByProductIdx(productIdx);
            System.out.println(userIdx);

            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            productService.editProduct(productIdx, patchPrdReq);

            String result = "수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 판매상태 수정 API
     * [PATCH] /products/{productIdx}/status/edit
     */
    @ResponseBody
    @PatchMapping("/{productIdx}/status/edit")
    public BaseResponse<String> editProductStatus(@PathVariable("productIdx") int productIdx, @RequestBody PatchPrdReq patchPrdReq) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.
            System.out.println(patchPrdReq);
            System.out.println(patchPrdReq.getTradeStatus());
            if(productProvider.checkProductIdx(productIdx) == 0) {
                throw new BaseException(INVALID_PRODUCT);
            }
            if(patchPrdReq.getTradeStatus() == null) {
                return new BaseResponse<>(EMPTY_PRODUCT_TRADE_STATUS);
            }
            if(!patchPrdReq.getTradeStatus().equals("A") && !patchPrdReq.getTradeStatus().equals("B") && !patchPrdReq.getTradeStatus().equals("D") && !patchPrdReq.getTradeStatus().equals("C")) {
                return new BaseResponse<>(INVALID_TRADE_STATUS);
            }

            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            int userIdx = productProvider.getUserIdxByProductIdx(productIdx);
            System.out.println(userIdx);

            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            productService.editProductStatus(productIdx, patchPrdReq.getTradeStatus());

            String result = "수정되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }

    /**
     * 상품 삭제 API
     * [PATCH] /products/{productIdx}/d
     */
    @ResponseBody
    @PatchMapping("/{productIdx}/d")
    public BaseResponse<String> deleteProduct(@PathVariable("productIdx") int productIdx) {
        try {
            // TODO: 로그인 값들에 대한 형식적인 validatin 처리해주셔야합니다!
            // TODO: 유저의 status ex) 비활성화된 유저, 탈퇴한 유저 등을 관리해주고 있다면 해당 부분에 대한 validation 처리도 해주셔야합니다.

            if(productProvider.checkProductIdx(productIdx) == 0) {
                throw new BaseException(INVALID_PRODUCT);
            }
            //jwt에서 idx 추출.
            int userIdxByJwt = jwtService.getUserIdx();
            //userIdx와 접근한 유저가 같은지 확인
            int userIdx = productProvider.getUserIdxByProductIdx(productIdx);
            System.out.println(userIdx);

            if(userIdx != userIdxByJwt){
                return new BaseResponse<>(INVALID_USER_JWT);
            }

            productService.deleteProduct(productIdx);

            String result = "삭제되었습니다.";
            return new BaseResponse<>(result);
        } catch (BaseException exception) {
            return new BaseResponse<>(exception.getStatus());
        }
    }
}
