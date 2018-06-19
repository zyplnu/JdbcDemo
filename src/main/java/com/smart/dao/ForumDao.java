package com.smart.dao;

import com.smart.pojo.Forum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ForumDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //插入数据。有几种方法
    public void addForumDao(final Forum forum){
        //第一种，不带回调接口的
        /*String sql = "insert into t_forum(forum_name,forum_desc) values(?,?)";
        Object[] params = new Object[]{forum.getForumName() , forum.getForumDesc()};
        jdbcTemplate.update(sql , params , new int[]{Types.VARCHAR , Types.VARCHAR});*/
        //第二种，带回调接口的
        /*jdbcTemplate.update(sql, new PreparedStatementSetter() {
            public void setValues(PreparedStatement ps) throws SQLException {
                ps.setString(1 , forum.getForumName());
                ps.setString(2 , forum.getForumDesc());
            }
        });*/

        //第三种，带回调接口的update方法
       /* jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1 , forum.getForumName());
                ps.setString(2 , forum.getForumDesc());
            }
        });*/

       //第四种，带回调接口并且新增后主键值绑定到对象的update方法
        final String sql = "INSERT INTO t_forum(forum_name,forum_desc) VALUEA(?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();//创建一个主键持有者
        jdbcTemplate.update(new PreparedStatementCreator() {
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setString(1 , forum.getForumName());
                ps.setString(2 , forum.getForumDesc());
                return ps;
            }
        },keyHolder);
        forum.setForumId(keyHolder.getKey().intValue());
    }

    //批量插入方法
    public void addForums(final List<Forum> forumList){
        final String sql = "INSERT INTO t_forum(forum_name,forum_desc) VALUEA(?,?)";
        jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Forum forum = forumList.get(i);
                ps.setString(1 , forum.getForumName());
                ps.setString(2 , forum.getForumDesc());
            }

            public int getBatchSize() {
                return forumList.size();
            }
        });
    }

    //查询数据,使用RowCallbackHandler处理结果集
    public Forum getForum(final int forumId){
        String sql = "select forum_name,forum_desc from t_forum where forum_id=?";
        final Forum forum = new Forum();

        //将结果集数据行中的数据抽取到forum对象中
        jdbcTemplate.query(sql, new Object[]{forumId}, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                forum.setForumId(forumId);
                forum.setForumName(rs.getString("forum_name"));
                forum.setForumDesc(rs.getString("forum_desc"));
            }
        });
        return forum;
    }

    //查询数据，批量查询
    public List<Forum> getForums(final int fromId , final int toId){
        String sql = "select * from t_forum where forum_id between ? and ?";
        final List<Forum> forumList = new ArrayList<Forum>();

        jdbcTemplate.query(sql, new Object[]{fromId, toId}, new RowCallbackHandler() {//将结果集中的数据映射到List中
            public void processRow(ResultSet rs) throws SQLException {
                Forum forum = new Forum();
                forum.setForumId(rs.getInt("forum_id"));
                forum.setForumName(rs.getString("forum_name"));
                forum.setForumDesc(rs.getString("forum_desc"));
                forumList.add(forum);
            }
        });
        return forumList;
    }

}
