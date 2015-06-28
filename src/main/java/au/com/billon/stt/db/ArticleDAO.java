package au.com.billon.stt.db;

import au.com.billon.stt.models.Article;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Zheng on 23/06/2015.
 */
@RegisterMapper(ArticleMapper.class)
public interface ArticleDAO {
    @SqlUpdate("create table IF NOT EXISTS article (id INT PRIMARY KEY auto_increment, title varchar(50), content varchar(500), created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP)")
    void createTableIfNotExists();

    @SqlUpdate("insert into article (title, content) values (:title, :content)")
    @GetGeneratedKeys
    long insert(@BindBean Article article);

    @SqlUpdate("update article set title = :title, content = :content, updated = CURRENT_TIMESTAMP where id = :id")
    int update(@BindBean Article article);

    @SqlUpdate("delete from article where id = :id")
    void deleteById(@Bind("id") long id);

    @SqlQuery("select * from article")
    List<Article> findAll();

    @SqlQuery("select * from article where id = :id")
    Article findById(@Bind("id") long id);
}