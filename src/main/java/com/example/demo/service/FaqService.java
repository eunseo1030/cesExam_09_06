package com.example.demo.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.repository.FaqRepository;
import com.example.demo.util.Ut;
import com.example.demo.vo.Article;
import com.example.demo.vo.ResultData;

@Service
public class FaqService {

    @Autowired
    private FaqRepository faqRepository;

    public ResultData writeArticle(int memberId, String title, String body, String boardId) {
        faqRepository.writeArticle(memberId, title, body, boardId);

        int id = faqRepository.getLastInsertId();

        return ResultData.from("S-1", Ut.f("%d번 글이 등록되었습니다", id), "등록된 게시글의 id", id);
    }

    public void deleteArticle(int id) {
        faqRepository.deleteArticle(id);
    }

    public void modifyArticle(int id, String title, String body) {
        faqRepository.modifyArticle(id, title, body);
    }

    public Article getForPrintArticle(int loginedMemberId, int id) {
        Article article = faqRepository.getForPrintArticle(id);
        controlForPrintData(loginedMemberId, article);
        return article;
    }

    public Article getArticleById(int id) {
        return faqRepository.getArticleById(id);
    }

    public List<Article> getForPrintArticles(int boardId, int itemsInAPage, int page, String searchKeywordTypeCode,
                                             String searchKeyword) {
        int limitFrom = (page - 1) * itemsInAPage;
        int limitTake = itemsInAPage;
        return faqRepository.getForPrintArticles(boardId, limitFrom, limitTake, searchKeywordTypeCode, searchKeyword);
    }

    public int getArticlesCount(int boardId, String searchKeywordTypeCode, String searchKeyword) {
        return faqRepository.getArticleCount(boardId, searchKeywordTypeCode, searchKeyword);
    }

    private void controlForPrintData(int loginedMemberId, Article article) {
        if (article == null) {
            return;
        }
        ResultData userCanModifyRd = userCanModify(loginedMemberId, article);
        article.setUserCanModify(userCanModifyRd.isSuccess());

        ResultData userCanDeleteRd = userCanDelete(loginedMemberId, article);
        article.setUserCanDelete(userCanDeleteRd.isSuccess());
    }

    public ResultData userCanDelete(int loginedMemberId, Article article) {
        if (article.getMemberId() != loginedMemberId) {
            return ResultData.from("F-2", Ut.f("%d번 게시글에 대한 삭제 권한이 없습니다", article.getId()));
        }
        return ResultData.from("S-1", Ut.f("%d번 게시글을 삭제했습니다", article.getId()));
    }

    public ResultData userCanModify(int loginedMemberId, Article article) {
        if (article.getMemberId() != loginedMemberId) {
            return ResultData.from("F-2", Ut.f("%d번 게시글에 대한 수정 권한이 없습니다", article.getId()));
        }
        return ResultData.from("S-1", Ut.f("%d번 게시글을 수정했습니다", article.getId()), "수정된 게시글", article);
    }
}