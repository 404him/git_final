package com.githrd.figurium.home.controller;

import com.githrd.figurium.product.entity.Category;
import com.githrd.figurium.product.entity.Products;
import com.githrd.figurium.product.repository.CategoriesRepository;
import com.githrd.figurium.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final ProductRepository productRepository;
    private final CategoriesRepository categoriesRepository;

    @Autowired
    HomeController(ProductRepository productRepository,
                   CategoriesRepository categoriesRepository) {
        this.productRepository = productRepository;
        this.categoriesRepository = categoriesRepository;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Products> productsList = productRepository.findProductsWithPagination(PageRequest.of(0, 40));
        List<Category> categoriesList = categoriesRepository.findAll();
        model.addAttribute("productsList", productsList);
        model.addAttribute("categoriesList", categoriesList);

        return "home";
    }

//    @GetMapping("/load-more-products")
//    @ResponseBody
//    public ResponseEntity<List<Products>> loadMoreProducts(@RequestParam int page, @RequestParam int size) {
//        Pageable pageable = PageRequest.of(page, size);
//        List<Products> products = productRepository.findProductsWithPagination(pageable);
//        return ResponseEntity.ok(products);
//    }


//    @GetMapping("/load-more-products")
//    public ResponseEntity<?> loadMoreProducts(@RequestParam(value = "lastId", defaultValue = "0") int lastId) {
//        int pageSize = 20;
//        Pageable pageable = PageRequest.of(0, pageSize); // 페이지 번호는 0으로 설정
//
//        // lastId 이후의 상품을 가져오기
//        List<Products> products = productRepository.findByIdGreaterThanOrderByIdAsc(lastId, pageable);
//
//        // 반환할 데이터 구조
//        Map<String, Object> response = new HashMap<>();
//        response.put("products", products);
//
//        return ResponseEntity.ok(response);
//    }


    @GetMapping("/load-more-products")
    public ResponseEntity<?> loadMoreProducts(@RequestParam(value = "lastId", defaultValue = "0") int lastId) {
        int pageSize = 20;
        Pageable pageable = PageRequest.of(0, pageSize);

        // lastId 이후의 상품을 가져오기
        Page<Products> productsPage = productRepository.findByIdGreaterThanOrderByIdAsc(lastId, pageable);

        // 반환할 데이터 구조
        Map<String, Object> response = new HashMap<>();
        response.put("products", productsPage.getContent()); // 현재 페이지의 상품 목록
        response.put("hasNext", productsPage.hasNext()); // 다음 페이지가 있는지 여부

        return ResponseEntity.ok(response);
    }

}
