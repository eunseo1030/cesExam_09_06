package com.example.demo.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.demo.service.FaqService;
import com.example.demo.util.Ut;
import com.example.demo.vo.Article;
import com.example.demo.vo.ResultData;
import com.example.demo.vo.Rq;

import jakarta.servlet.http.HttpServletRequest;

@Controller
public class FaqController {

    @Autowired
    private Rq rq;

    @Autowired
    private FaqService faqService;

    // FAQ 상세 페이지
    @RequestMapping("/usr/faq/detail")
    public String showDetail(HttpServletRequest req, Model model, int id) {
        Rq rq = (Rq) req.getAttribute("rq");

        Article article = faqService.getForPrintArticle(rq.getLoginedMemberId(), id);

        model.addAttribute("article", article);

        return "usr/faq/detail";
    }

    // FAQ 수정 페이지
    @RequestMapping("/usr/faq/modify")
    public String showModify(HttpServletRequest req, Model model, int id) {
        Rq rq = (Rq) req.getAttribute("rq");

        Article article = faqService.getForPrintArticle(rq.getLoginedMemberId(), id);

        if (article == null) {
            return Ut.jsHistoryBack("F-1", Ut.f("%d번 게시글은 없습니다.", id));
        }

        model.addAttribute("article", article);

        return "usr/faq/modify";
    }

    // FAQ 수정 처리
    @RequestMapping("/usr/faq/doModify")
    @ResponseBody
    public String doModify(HttpServletRequest req, int id, String title, String body) {
        Rq rq = (Rq) req.getAttribute("rq");

        Article article = faqService.getArticleById(id);

        if (article == null) {
            return Ut.jsHistoryBack("F-1", Ut.f("%d번 게시글은 없습니다.", id));
        }

        ResultData userCanModifyRd = faqService.userCanModify(rq.getLoginedMemberId(), article);

        if (userCanModifyRd.isFail()) {
            return Ut.jsHistoryBack(userCanModifyRd.getResultCode(), userCanModifyRd.getMsg());
        }

        faqService.modifyArticle(id, title, body);

        return Ut.jsReplace(userCanModifyRd.getResultCode(), userCanModifyRd.getMsg(), "../faq/detail?id=" + id);
    }

    // FAQ 삭제 처리
    @RequestMapping("/usr/faq/doDelete")
    @ResponseBody
    public String doDelete(HttpServletRequest req, int id) {
        Rq rq = (Rq) req.getAttribute("rq");

        Article article = faqService.getArticleById(id);

        if (article == null) {
            return Ut.jsHistoryBack("F-1", Ut.f("%d번 게시글은 없습니다.", id));
        }

        ResultData userCanDeleteRd = faqService.userCanDelete(rq.getLoginedMemberId(), article);

        if (userCanDeleteRd.isFail()) {
            return Ut.jsHistoryBack(userCanDeleteRd.getResultCode(), userCanDeleteRd.getMsg());
        }

        faqService.deleteArticle(id);

        return Ut.jsReplace(userCanDeleteRd.getResultCode(), userCanDeleteRd.getMsg(), "../faq/list");
    }

    // FAQ 작성 페이지
    @RequestMapping("/usr/faq/write")
    public String showWrite(HttpServletRequest req) {
        return "usr/faq/write";
    }

    // FAQ 작성 처리
    @RequestMapping("/usr/faq/doWrite")
    @ResponseBody
    public String doWrite(HttpServletRequest req, String title, String body) {
        Rq rq = (Rq) req.getAttribute("rq");

        if (Ut.isEmptyOrNull(title)) {
            return Ut.jsHistoryBack("F-1", "제목을 입력해주세요");
        }

        if (Ut.isEmptyOrNull(body)) {
            return Ut.jsHistoryBack("F-2", "내용을 입력해주세요");
        }

        ResultData writeArticleRd = faqService.writeArticle(rq.getLoginedMemberId(), title, body, "4"); // FAQ의 boardId = 4

        int id = (int) writeArticleRd.getData1();

        return Ut.jsReplace(writeArticleRd.getResultCode(), writeArticleRd.getMsg(), "../faq/detail?id=" + id);
    }

    // FAQ 목록 페이지
    @RequestMapping("/usr/faq/list")
    public String showList(HttpServletRequest req, Model model, @RequestParam(defaultValue = "4") int boardId,
                           @RequestParam(defaultValue = "1") int page,
                           @RequestParam(defaultValue = "title,body") String searchKeywordTypeCode,
                           @RequestParam(defaultValue = "") String searchKeyword) throws IOException {
        Rq rq = (Rq) req.getAttribute("rq");

        int articlesCount = faqService.getArticlesCount(boardId, searchKeywordTypeCode, searchKeyword);

        int itemsInAPage = 10;
        int pagesCount = (int) Math.ceil(articlesCount / (double) itemsInAPage);

        List<Article> articles = faqService.getForPrintArticles(boardId, itemsInAPage, page, searchKeywordTypeCode, searchKeyword);

        model.addAttribute("articles", articles);
        model.addAttribute("articlesCount", articlesCount);
        model.addAttribute("pagesCount", pagesCount);
        model.addAttribute("page", page);
        model.addAttribute("searchKeywordTypeCode", searchKeywordTypeCode);
        model.addAttribute("searchKeyword", searchKeyword);
        model.addAttribute("boardId", boardId);

        return "usr/faq/list";
    }
}