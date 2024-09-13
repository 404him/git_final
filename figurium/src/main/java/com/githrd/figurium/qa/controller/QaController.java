package com.githrd.figurium.qa.controller;

import com.githrd.figurium.qa.service.QaService;
import com.githrd.figurium.qa.vo.QaVo;
import com.githrd.figurium.user.entity.User;
import com.githrd.figurium.util.page.CommonPage;
import com.githrd.figurium.util.page.Paging;
import com.githrd.figurium.util.page.ProductQaCommonPage;
import com.githrd.figurium.util.page.ProductQaPaging;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/qa")
public class QaController {

    private final QaService qaService;
    private final HttpSession session;

    @Autowired
    public QaController(QaService qaService, HttpSession session) {
        this.qaService = qaService;
        this.session = session;
    }

    @GetMapping("/qaList.do")
    public String list(@RequestParam(name="page", defaultValue = "1")int nowPage,
                       Model model) {



            //검색조건을 담을 맵
            Map<String, Object> map = new HashMap<String, Object>();

            //start/end
            int start = (nowPage-1) * CommonPage.qaList.BLOCK_LIST + 1;
            int end   = start+CommonPage.qaList.BLOCK_LIST - 1;


            map.put("start", start);
            map.put("end", end);

            // 총게시물수
            int rowTotal = qaService.selectRowTotal(map);

            //pageMenu만들기
            String pageMenu = Paging.getPaging("qaList.do",
                    nowPage,
                    rowTotal,
                    CommonPage.qaList.BLOCK_LIST ,
                    CommonPage.qaList.BLOCK_PAGE);

            //-------[ End :  Page Menu ]------------------------

            // 결과적으로 request binding
            model.addAttribute("pageMenu", pageMenu);
            model.addAttribute("qaList", qaService.selectAllWithPagination(map));

        return "qa/qaList";
    }

    @GetMapping("/productQaList.do")
    public String getProductQaList(@RequestParam(name = "page", defaultValue = "1") int nowPage,
                                   @RequestParam(name = "productQaId", required = false) Integer productQaId, // Integer로 변경
                                   Model model) {

        // 검색조건을 담을 맵
        Map<String, Object> map = new HashMap<>();

        // start/end
        int start = (nowPage - 1) * ProductQaCommonPage.productQaList.BLOCK_LIST + 1;
        int end = start + ProductQaCommonPage.productQaList.BLOCK_LIST - 1;

        map.put("start", start);
        map.put("end", end);
        map.put("productQaId", productQaId);

        // 총게시물수
        int rowTotal = qaService.selectRowTotal(map);

        // pageMenu 만들기
        String pageMenu = ProductQaPaging.getPaging("productQaList.do",
                nowPage,
                rowTotal,
                ProductQaCommonPage.productQaList.BLOCK_LIST,
                ProductQaCommonPage.productQaList.BLOCK_PAGE);

        //-------[ End :  Page Menu ]------------------------

        // Q&A 목록을 가져오고 변수에 저장
        List<QaVo> productQaList = qaService.selectAllWithPagination(map);

        // 결과적으로 request binding
        model.addAttribute("pageMenu", pageMenu);
        model.addAttribute("productQaList", productQaList); // 수정된 부분
        model.addAttribute("productQaId", productQaId);

        // 디버깅을 위한 출력
        System.out.println("productQaList : " + productQaList);

        return "qa/productQaList";
    }

    //Q&A게시판에서 게시글 작성시
    @GetMapping("/qaInsert.do")
    public String insertForm(Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        // 로그인 상태를 확인
        if (loginUser == null) {
            return "redirect:/";
        }
        return "qa/qaInsert";
    }

    //Q&A게시판에서 게시글 작성시
    @GetMapping("/qaInsertOrderId.do")
    public String insertFormOrderId(Model model, int orderId) {
        User loginUser = (User) session.getAttribute("loginUser");
        // 로그인 상태를 확인
        if (loginUser == null) {
            return "redirect:/";
        }
        model.addAttribute("orderId", orderId);
        return "qa/qaInsert";
    }

    //상품상세페이지에서 게시글 작성시
    @GetMapping("/productQaInsert.do")
    public String productInsertForm(@RequestParam(name = "productId", required = false) Integer productQaId, Model model) {
        User loginUser = (User) session.getAttribute("loginUser");
        // 로그인 상태를 확인
        if (loginUser == null) {
            return "redirect:/";
        }
        model.addAttribute("productQaId", productQaId);

        return "qa/productQaInsert";
    }

    //Q&A게시판에서 게시글 작성시
    @PostMapping("/qaSave.do")
    public String save(@RequestParam("title") String title,
                       @RequestParam("content") String content,
                       @RequestParam("category") String category,
                       @RequestParam(value = "reply", required = false) String reply) {
        User loginUser = (User) session.getAttribute("loginUser");
        // 로그인 상태를 확인
        if (loginUser == null) {
            return "redirect:/";
        }

        // 카테고리와 제목을 처리하기 위한 코드 추가
        if (title != null && !title.startsWith("[" + category + "]")) {
            title = "[" + category + "] " + title;
        }

        QaVo qaVo = new QaVo();
        // User ID 처리
        if (loginUser.getId() != null) {
            // Integer 타입일 경우
            qaVo.setUserId(loginUser.getId());
        }

        qaVo.setTitle(title);
        qaVo.setContent(content);
        qaVo.setReply(reply);

        qaService.saveQa(qaVo);

        return "redirect:/qa/qaList.do";
    }

    //상품상세페이지에서 게시글 작성시
    @GetMapping("/productQaSave.do")
    public String productQaSave(@RequestParam("title") String title,
                                @RequestParam("content") String content,
                                @RequestParam("category") String category,
                                @RequestParam(value = "reply", required = false) String reply,
                                @RequestParam(value = "productQaId") int productQaId) {
        User loginUser = (User) session.getAttribute("loginUser");
        // 로그인 상태를 확인
        if (loginUser == null) {
            return "redirect:/";
        }

        // 카테고리와 제목을 처리하기 위한 코드 추가
        if (title != null && !title.startsWith("[" + category + "]")) {
            title = "[" + category + "] " + title;
        }

        QaVo qaVo = new QaVo();
        // User ID 처리
        if (loginUser.getId() != null) {
            // Integer 타입일 경우
            qaVo.setUserId(loginUser.getId());
        }


        qaVo.setTitle(title);
        qaVo.setContent(content);
        qaVo.setReply(reply);
        qaVo.setProductQaId(productQaId);
        qaService.saveProductQa(qaVo);

        // 상품 상세 페이지로 리디렉션하며 해당 상품의 Q&A 목록도 함께 포함
        return "redirect:/productInfo.do?id=" + productQaId + "&showQa=true";
    }

    @GetMapping("/qaSelect.do")
    public String select(@RequestParam("id") int id, Model model, RedirectAttributes redirectAttributes) {
        User loginUser = (User) session.getAttribute("loginUser");
        QaVo qaVo = qaService.getQaById(id);

        if (qaVo == null) {
            return "redirect:/qa/qaList.do"; // 게시글이 존재하지 않을 때
        }

        // 로그인하지 않은 상태에서 게시글을 클릭한 경우
        if (loginUser == null) {
            // 로그인 페이지로 리디렉션하며 메시지를 전달
            redirectAttributes.addFlashAttribute("alertMessage", "로그인 후 사용해 주세요.");
            return "redirect:/qa/qaList.do"; // 리스트페이지로 리다이렉트
        }


        // 관리자일 경우, 모든 게시글에 접근 가능
        if (loginUser != null && (loginUser.getRole() == 1 || qaVo.getUserId().equals(loginUser.getId()))) {
            model.addAttribute("qa", qaVo);
            return "qa/qaSelect";
        }

        // 관리자도 아니고, 작성자와 다를 경우
        if (loginUser != null && !qaVo.getUserId().equals(loginUser.getId())) {
            redirectAttributes.addFlashAttribute("message", "다른 사용자의 게시글입니다.");
            return "redirect:/qa/qaList.do"; // 경고 메시지와 함께 목록 페이지로 이동
        }

        // 로그인하지 않았거나, 작성자와 같을 경우
        model.addAttribute("qa", qaVo);
        return "qa/qaSelect";
    }

    @GetMapping("/productQaSelect.do")
    public String selectQa(@RequestParam("id") int id,
                           @RequestParam("productQaId") int productId,
                           Model model, RedirectAttributes redirectAttributes) {
        User loginUser = (User) session.getAttribute("loginUser");
        QaVo qaVo = qaService.getQaById(id);

        if (qaVo == null) {
            return "redirect:/qa/productQaList.do"; // 게시글이 존재하지 않을 때
        }

        // 로그인하지 않은 상태에서 게시글을 클릭한 경우
        if (loginUser == null) {
            // 로그인 페이지로 리디렉션하며 메시지를 전달
            redirectAttributes.addFlashAttribute("alertMessage", "로그인 후 사용해 주세요.");
            return "redirect:/qa/productQaList.do"; // 리스트페이지로 리다이렉트
        }


        // 관리자일 경우, 모든 게시글에 접근 가능
        if (loginUser != null && (loginUser.getRole() == 1 || qaVo.getUserId().equals(loginUser.getId()))) {
            model.addAttribute("qa", qaVo);
            return "qa/productQaSelect";
        }

        // 관리자도 아니고, 작성자와 다를 경우
        if (loginUser != null && !qaVo.getUserId().equals(loginUser.getId())) {
            redirectAttributes.addFlashAttribute("message", "다른 사용자의 게시글입니다.");
            return "redirect:/qa/productQaList.do"; // 경고 메시지와 함께 목록 페이지로 이동
        }



        // 로그인하지 않았거나, 작성자와 같을 경우
        model.addAttribute("qa", qaVo);

        return "qa/productQaSelect";
    }

    @PostMapping("/qaReplySave.do")
    public String saveReply(@RequestParam("id") int id,
                            @RequestParam("content") String content) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null) {
            return "redirect:/";
        }

        QaVo qaVo = qaService.getQaById(id);
        qaVo.setReply(content); // 답변 설정
        qaVo.setReplyStatus("답변완료"); // 상태 업데이트
        qaService.updateQa(qaVo);

        return "redirect:/qa/qaSelect.do?id=" + id;
    }

    @PostMapping("/qaReplyDelete.do")
    public String deleteReply(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        User loginUser = (User) session.getAttribute("loginUser");
        if (loginUser == null || loginUser.getRole() != 1) {
            return "redirect:/";
        }
        qaService.deleteReply(id);
        // 게시글 상세 페이지로 리다이렉트
        return "redirect:/qa/qaSelect.do?id=" + id;
    }

    @PostMapping("/qaDelete.do")
    public String delete(@RequestParam("id") int id, RedirectAttributes redirectAttributes) {
        qaService.deleteQa(id);
        return "redirect:/qa/qaList.do";
    }
    // 만약 GET 요청을 처리하려면:
    @RequestMapping(value = "/qaDelete.do", method = RequestMethod.GET)
    public String deleteQaGet(@RequestParam("id") int id) {
        // GET 요청으로 삭제 로직
        return "redirect:/qa/qaList.do";
    }



}