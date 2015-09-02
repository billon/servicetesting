package au.com.billon.stt.db;

import au.com.billon.stt.models.Endpoint;
import io.dropwizard.db.DataSourceFactory;
import org.skife.jdbi.v2.sqlobject.*;
import org.skife.jdbi.v2.sqlobject.customizers.RegisterMapper;

import java.util.List;

/**
 * Created by Trevor Li on 6/30/15.
 */
@RegisterMapper(EndpointMapper.class)
public abstract class EndpointDAO {
    @SqlUpdate("create table IF NOT EXISTS endpoint (id INT PRIMARY KEY auto_increment, name varchar(50) UNIQUE not null, description varchar(500), type varchar(50), " +
            "created timestamp DEFAULT CURRENT_TIMESTAMP, updated timestamp DEFAULT CURRENT_TIMESTAMP)")
    public abstract void createTableIfNotExists();

    @SqlUpdate("insert into endpoint (name, description, type) values (:name, :description, :type)")
    @GetGeneratedKeys
    public abstract long insert(@BindBean Endpoint endpoint);

    @SqlUpdate("update endpoint set name = :name, description = :description, type = :type, updated = CURRENT_TIMESTAMP where id = :id")
    public abstract int update(@BindBean Endpoint endpoint);

    @SqlUpdate("delete from endpoint where id = :id")
    public abstract void deleteById(@Bind("id") long id);

    @SqlQuery("select * from endpoint")
    public abstract List<Endpoint> findAll();

    @SqlQuery("select * from endpoint where id = :id")
    public abstract Endpoint findById(@Bind("id") long id);

    @SqlQuery("select * from endpoint where name = :name")
    public abstract Endpoint findByName(@Bind("name") String name);
}
