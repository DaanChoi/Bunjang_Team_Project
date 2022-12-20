package com.example.demo.src.category;

import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import com.example.demo.src.category.model.*;
import com.example.demo.utils.AES128;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.jdbc.core.JdbcTemplate;
import javax.sql.DataSource;
import static com.example.demo.config.BaseResponseStatus.*;

@Service

public class CategoryService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final CategoryDao categoryDao;
    private final CategoryProvider categoryProvider;
    private final JwtService jwtService;


    @Autowired //readme 참고
    public CategoryService(CategoryDao categoryDao, CategoryProvider categoryProvider, JwtService jwtService) {
        this.categoryDao = categoryDao;
        this.categoryProvider = categoryProvider;
        this.jwtService = jwtService;

    }
}
