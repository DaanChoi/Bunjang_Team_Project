package com.example.demo.src.user;

public class UserDaoSqls {
    public static String SELECT_USERS = "select u.userIdx, u.name, u.profileImg, u.introduce, review.rate, sell.sellCnt, follower.followerCnt, following.followingCnt, safepay.safeCnt, createdAt, auth\n" +
                                        "from User as u\n" +
                                        "left outer join (select revieweeIdx, avg(rate) as rate\n" +
                                        "\t\t\t\tfrom Review\n" +
                                        "\t\t\t\tgroup by revieweeIdx) as review\n" +
                                        "on u.userIdx = review.revieweeIdx\n" +
                                        "left outer join (select sellerIdx, count(buyerIdx) as sellCnt\n" +
                                        "\t\t\t\tfrom Purchase\n" +
                                        "\t\t\t\tgroup by sellerIdx) as sell\n" +
                                        "on u.userIdx = sell.sellerIdx\n" +
                                        "left outer join (select followeeIdx, count(followerIdx) as followerCnt\n" +
                                        "\t\t\t\tfrom Follow\n" +
                                        "\t\t\t\twhere status = 'A'\n" +
                                        "\t\t\t\tgroup by followeeIdx) as follower\n" +
                                        "on u.userIdx = follower.followeeIdx\n" +
                                        "left outer join (select followerIdx, count(followeeIdx) as followingCnt\n" +
                                        "\t\t\t\tfrom Follow\n" +
                                        "\t\t\t\twhere status = 'A'\n" +
                                        "\t\t\t\tgroup by followerIdx) as following\n" +
                                        "on u.userIdx = following.followerIdx\n" +
                                        "left outer join (select userIdx, count(productIdx) as safeCnt\n" +
                                        "\t\t\t\tfrom Product\n" +
                                        "\t\t\t\twhere isSafepay = 1\n" +
                                        "\t\t\t\tand tradeStatus = 'A'\n" +
                                        "                group by userIdx) as safepay\n" +
                                        "on u.userIdx = safepay.userIdx\n" +
                                        "where u.status = 'A';";
    public static String SELECT_USER = "select u.userIdx, u.name, u.profileImg, u.introduce, review.rate, sell.sellCnt, follower.followerCnt, following.followingCnt, safepay.safeCnt, createdAt, auth\n" +
                                        "from User as u\n" +
                                        "left outer join (select revieweeIdx, avg(rate) as rate\n" +
                                        "\t\t\t\tfrom Review\n" +
                                        "\t\t\t\tgroup by revieweeIdx) as review\n" +
                                        "on u.userIdx = review.revieweeIdx\n" +
                                        "left outer join (select sellerIdx, count(buyerIdx) as sellCnt\n" +
                                        "\t\t\t\tfrom Purchase\n" +
                                        "\t\t\t\tgroup by sellerIdx) as sell\n" +
                                        "on u.userIdx = sell.sellerIdx\n" +
                                        "left outer join (select followeeIdx, count(followerIdx) as followerCnt\n" +
                                        "\t\t\t\tfrom Follow\n" +
                                        "\t\t\t\twhere status = 'A'\n" +
                                        "\t\t\t\tgroup by followeeIdx) as follower\n" +
                                        "on u.userIdx = follower.followeeIdx\n" +
                                        "left outer join (select followerIdx, count(followeeIdx) as followingCnt\n" +
                                        "\t\t\t\tfrom Follow\n" +
                                        "\t\t\t\twhere status = 'A'\n" +
                                        "\t\t\t\tgroup by followerIdx) as following\n" +
                                        "on u.userIdx = following.followerIdx\n" +
                                        "left outer join (select userIdx, count(productIdx) as safeCnt\n" +
                                        "\t\t\t\tfrom Product\n" +
                                        "\t\t\t\twhere isSafepay = 1\n" +
                                        "\t\t\t\tand tradeStatus = 'A'\n" +
                                        "                group by userIdx) as safepay\n" +
                                        "on u.userIdx = safepay.userIdx\n" +
                                        "where u.status = 'A' \n" +
                                        "and u.userIdx = ?;";
    public static String SELECT_USER_PRODUCTS = "select prd.productIdx, prd.userIdx, prd.isSafepay, prd.tradeStatus, prdimg.imageUrl, prd.title, prd.price, prd.location, prd.createdAt, prd.isFav\n" +
                                                "from Product as prd\n" +
                                                "left outer join (select productIdx, imageUrl\n" +
                                                "\t\t\t\tfrom Product_Image\n" +
                                                "\t\t\t\twhere status = 'A'\n" +
                                                "\t\t\t\tgroup by productIdx) as prdimg\n" +
                                                "on prd.productIdx = prdimg.productIdx\n" +
                                                "where prd.status = 'A'\n" +
                                                "and prd.userIdx = ?;";
    public static String SELECT_USER_REVIEWS = "select reviewIdx, rate, contents, prd.title, reviewerIdx, u.name, createdAt, revieweeIdx, Review.productIdx\n" +
                                                "from Review\n" +
                                                "left outer join (select userIdx, name\n" +
                                                "\t\t\t\tfrom User) as u\n" +
                                                "on Review.reviewerIdx = u.userIdx\n" +
                                                "left outer join (select productIdx, title\n" +
                                                "\t\t\t\tfrom Product) as prd\n" +
                                                "on Review.productIdx = prd.productIdx\n" +
                                                "where status = 'A' and Review.revieweeIdx = ?;";
    public static String SELECT_USER_FAVORITES = "select fav.favoriteIdx, fav.userIdx, fav.productIdx as favoriteProductIdx, prd.tradeStatus, prd.isSafepay, prd.imageUrl, prd.title, prd.price, prd.userIdx as sellerIdx, prd.name, prd.createdAt, prd.isFav\n" +
                                                    "from Favorite as fav\n" +
                                                    "left outer join (select prd.productIdx, prd.isSafepay, prd.tradeStatus, prdimg.imageUrl, prd.title, prd.price, prd.userIdx, u.name, prd.createdAt, prd.isFav, prd.status\n" +
                                                    "\t\t\t\tfrom Product as prd\n" +
                                                    "\t\t\t\tleft outer join (select userIdx, profileImg, name\n" +
                                                    "\t\t\t\t\t\t\t\tfrom User) as u\n" +
                                                    "\t\t\t\ton prd.userIdx = u.userIdx\n" +
                                                    "\t\t\t\tleft outer join (select productImageIdx, productIdx, imageUrl\n" +
                                                    "\t\t\t\t\t\t\t\tfrom Product_Image\n" +
                                                    "\t\t\t\t\t\t\t\twhere status = 'A'\n" +
                                                    "\t\t\t\t\t\t\t\tgroup by productIdx) as prdimg\n" +
                                                    "\t\t\t\ton prd.productIdx = prdimg.productIdx) as prd\n" +
                                                    "on fav.productIdx = prd.productIdx\n" +
                                                    "where fav.status = 'A' and prd.status = 'A'\n" +
                                                    "and fav.userIdx = ?;";
    public static String SELECT_USER_FOLLOWERS = "select flw.followIdx, flw.followeeIdx as userIdx, flw.followerIdx, u.name, u.profileImg, prdcnt.prdCnt, flwcnt.followerCnt\n" +
                                                    "from Follow as flw\n" +
                                                    "left outer join (select userIdx, name, profileImg, status\n" +
                                                    "\t\t\t\tfrom User) as u\n" +
                                                    "on flw.followerIdx = u.userIdx\n" +
                                                    "left outer join (select userIdx, count(productIdx) as prdCnt\n" +
                                                    "\t\t\t\tfrom Product\n" +
                                                    "\t\t\t\twhere tradeStatus = 'A'\n" +
                                                    "\t\t\t\tand status = 'A'\n" +
                                                    "\t\t\t\tgroup by userIdx) as prdcnt\n" +
                                                    "on flw.followerIdx = prdcnt.userIdx\n" +
                                                    "left outer join (select followeeIdx, count(followerIdx) as followerCnt\n" +
                                                    "\t\t\t\tfrom Follow\n" +
                                                    "\t\t\t\twhere status = 'A'\n" +
                                                    "\t\t\t\tgroup by followeeIdx) as flwcnt\n" +
                                                    "on flw.followerIdx = flwcnt.followeeIdx\n" +
                                                    "where flw.status = 'A' and u.status ='A'\n" +
                                                    "and flw.followeeIdx = ?;";
    public static String SELECT_USER_FOLLOWINGS = "select flw.followIdx, flw.followerIdx as userIdx, flw.followeeIdx as followingIdx, u.name, u.profileImg, prdcnt.prdCnt, flwcnt.followerCnt\n" +
                                                    "from Follow as flw\n" +
                                                    "left outer join (select userIdx, name, profileImg, status\n" +
                                                    "\t\t\t\tfrom User) as u\n" +
                                                    "on flw.followeeIdx = u.userIdx\n" +
                                                    "left outer join (select userIdx, count(productIdx) as prdCnt\n" +
                                                    "\t\t\t\tfrom Product\n" +
                                                    "\t\t\t\twhere tradeStatus = 'A'\n" +
                                                    "\t\t\t\tand status = 'A'\n" +
                                                    "\t\t\t\tgroup by userIdx) as prdcnt\n" +
                                                    "on flw.followeeIdx = prdcnt.userIdx\n" +
                                                    "left outer join (select followeeIdx, count(followerIdx) as followerCnt\n" +
                                                    "\t\t\t\tfrom Follow\n" +
                                                    "\t\t\t\twhere status = 'A'\n" +
                                                    "\t\t\t\tgroup by followeeIdx) as flwcnt\n" +
                                                    "on flw.followeeIdx = flwcnt.followeeIdx\n" +
                                                    "where flw.status = 'A' and u.status ='A'\n" +
                                                    "and flw.followerIdx = ?;";
}
