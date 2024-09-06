package com.example.demo.repository;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.example.demo.vo.Article;

@Mapper
public interface FaqRepository {

    @Insert("""
            INSERT INTO article
            SET regDate = NOW(),
            updateDate = NOW(),
            memberId = #{memberId},
            boardId = #{boardId},
            title = #{title},
            `body` = #{body}
            """)
    public void writeArticle(int memberId, String title, String body, String boardId);

    @Delete("""
            DELETE FROM article
            WHERE id = #{id}
            """)
    public void deleteArticle(int id);

    @Update("""
            UPDATE article
            SET updateDate = NOW(),
            title = #{title},
            `body` = #{body}
            WHERE id = #{id}
            """)
    public void modifyArticle(int id, String title, String body);

    @Select("""
            SELECT A.* , M.nickname AS extra__writer
            FROM article AS A
            INNER JOIN `member` AS M
            ON A.memberId = M.id
            WHERE A.id = #{id}
            """)
    public Article getForPrintArticle(int id);

    @Select("""
            SELECT *
            FROM article
            WHERE id = #{id}
            """)
    public Article getArticleById(int id);

    @Select("""
            <script>
                SELECT A.* , M.nickname AS extra__writer
                FROM article AS A
                INNER JOIN `member` AS M
                ON A.memberId = M.id
                WHERE boardId = #{boardId}
                <if test="searchKeyword != ''">
                    <choose>
                        <when test="searchKeywordTypeCode == 'title'">
                            AND A.title LIKE CONCAT('%', #{searchKeyword}, '%')
                        </when>
                        <when test="searchKeywordTypeCode == 'body'">
                            AND A.`body` LIKE CONCAT('%', #{searchKeyword}, '%')
                        </when>
                        <when test="searchKeywordTypeCode == 'nickname'">
                            AND M.nickname LIKE CONCAT('%', #{searchKeyword}, '%')
                        </when>
                        <otherwise>
                            AND A.title LIKE CONCAT('%', #{searchKeyword}, '%')
                            OR A.`body` LIKE CONCAT('%', #{searchKeyword}, '%')
                        </otherwise>
                    </choose>
                </if>
                ORDER BY A.id DESC
                <if test="limitFrom >= 0">
                    LIMIT #{limitFrom}, #{limitTake}
                </if>
            </script>
            """)
    public List<Article> getForPrintArticles(int boardId, int limitFrom, int limitTake, String searchKeywordTypeCode, String searchKeyword);

    @Select("""
            <script>
                SELECT COUNT(*)
                FROM article AS A
                INNER JOIN `member` AS M
                ON A.memberId = M.id
                WHERE boardId = #{boardId}
                <if test="searchKeyword != ''">
                    <choose>
                        <when test="searchKeywordTypeCode == 'title'">
                            AND A.title LIKE CONCAT('%', #{searchKeyword}, '%')
                        </when>
                        <when test="searchKeywordTypeCode == 'body'">
                            AND A.`body` LIKE CONCAT('%', #{searchKeyword}, '%')
                        </when>
                        <when test="searchKeywordTypeCode == 'nickname'">
                            AND M.nickname LIKE CONCAT('%', #{searchKeyword}, '%')
                        </when>
                        <otherwise>
                            AND A.title LIKE CONCAT('%', #{searchKeyword}, '%')
                            OR A.`body` LIKE CONCAT('%', #{searchKeyword}, '%')
                        </otherwise>
                    </choose>
                </if>
            </script>
            """)
    public int getArticleCount(int boardId, String searchKeywordTypeCode, String searchKeyword);

    @Select("SELECT LAST_INSERT_ID();")
    public int getLastInsertId();
}