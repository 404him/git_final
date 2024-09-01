package com.githrd.figurium.product.controller;

import com.githrd.figurium.product.dao.CartsMapper;
import com.githrd.figurium.product.dao.ProductsMapper;
import com.githrd.figurium.product.dto.ProductDto;
import com.githrd.figurium.product.vo.CartsVo;
import com.githrd.figurium.product.vo.ProductsVo;
import com.githrd.figurium.user.entity.User;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class CartsController {

    private HttpSession session;
    private CartsMapper cartsMapper;
    private ProductsMapper productsMapper;

    @Autowired
    public CartsController(HttpSession session, CartsMapper cartsMapper,
                           ProductsMapper productsMapper) {
        this.session = session;
        this.cartsMapper = cartsMapper;
        this.productsMapper = productsMapper;
    }

    // 장바구니 추가 버튼 눌렀을 시에 실행
    @GetMapping("/shopingCart.do")
        public String shopingCart(Model model, int productId, int quantity) {

        // 세션에서 로그인 사용자 정보 가져오기
        User loginUser = (User)session.getAttribute("loginUser");

        // 로그인 사용자 정보가 null인지 체크
        if (loginUser == null) {
            model.addAttribute("errorMessage", "로그인 후 구매가 가능합니다.");
            return "redirect:/";
        }

        // 이 상품이 추가가 되어있으면, 장바구니에 상품 추가를 거치지 않고 넘기기
        CartsVo checkCart = cartsMapper.selectCartsById(productId);
        if (checkCart == null) {

            Map<String, Object> map = new HashMap<>();
            map.put("loginUserId", loginUser.getId());
            map.put("productId", productId);
            map.put("quantity", quantity);

            // 정바구니에 해당 ID로 상품 추가
            cartsMapper.insertCarts(map);

        }






        List<CartsVo> cartsVo = cartsMapper.selectList(loginUser.getId());
//        List<ProductsVo> products = new ArrayList<>();
//
//        for (CartsVo vo : cartsVo) {
//            ProductsVo product = productsMapper.selectOneGetName(vo.getProductId());
//            products.add(product);
//            System.out.println(product);
//        }


        model.addAttribute("cartsVo", cartsVo);
//        model.addAttribute("products", products);


        return "products/shopingCart";
        }

        // 장바구니에 담긴 상품 삭제
        @RequestMapping(value = "/CartDelete.do")
        @ResponseBody
        public String CartDelete(int productId, int loginUser) {

            int res = cartsMapper.deleteCartProduct(productId, loginUser);

            return "success";
        }

        // 내 장바구니 리스트 호출
        @RequestMapping(value = "/CartList.do")
        public String CartList(int loginUser, Model model) {
            List<CartsVo> cartsVo = cartsMapper.selectList(loginUser);

            model.addAttribute("cartsVo", cartsVo);
            return "products/shopingCart";
        }




}
