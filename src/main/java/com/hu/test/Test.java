package com.hu.test;

import com.hu.core.io.IResources;
import com.hu.core.session.SqlSession;
import com.hu.core.session.SqlSessionFactory;
import com.hu.core.session.SqlSessionFactoryBuilder;
import com.hu.test.mapper.UserMapper;
import com.hu.test.pojo.User;
import org.junit.Before;

import java.io.InputStream;
import java.util.List;

/**
 * @Author: hu.chen
 * @Description:
 * @DateTime: 2021/12/14 11:29 上午
 **/
public class Test {



    private UserMapper userMapper;

//    @Before
//    public void before() throws Exception {
//        InputStream resourceAsStream = IResources.getResourceAsStream("/core/mybatisCoreConfig.xml");
//        SqlSessionFactory builder = new SqlSessionFactoryBuilder().builder(resourceAsStream);
//        SqlSession sqlSession = builder.openSqlSession();
//        userMapper = sqlSession.getMapper(UserMapper.class);
//    }

    @org.junit.Test
    public void selectOne1() throws Exception {
        InputStream resourceAsStream = IResources.getResourceAsStream("/core/mybatisCoreConfig.xml");
        SqlSessionFactory builder = new SqlSessionFactoryBuilder().builder(resourceAsStream);
        SqlSession sqlSession = builder.openSqlSession();
        User user = new User();
        user.setId(1L);

        User user1 = sqlSession.selectOne("user.selectOne",user);
        System.out.println(user1);

    }


    /**
     * 根据ID查询单个
     */
    @org.junit.Test
    public void selectOne() throws Exception {
        User user = new User();
        user.setId(1L);

        User user1 = userMapper.selectOne(user);
        System.out.println(user1);

    }

    /**
     * 查询所有
     */
    @org.junit.Test
    public void selectList() throws Exception {

        List<User> users = userMapper.selectList();
        for (User user : users) {
            System.out.println(user);
        }
    }

    /**
     * 根据条件查询
     */
    @org.junit.Test
    public void selectListByParam() throws Exception {
        User user = new User();
//        user.setId(1L);
        user.setUserName("花木兰");
//        user.setAge(18);
//        user.setPassWord("123456");

        List<User> users = userMapper.selectListByParam(user);

        for (User user1 : users) {
            System.out.println(user1);
        }
    }

    /**
     * 添加(添加实体对象中不为null的字段）
     */
    @org.junit.Test
    public void add() throws Exception {
        User user = new User();
//        user.setId(8812L);
        user.setUserName("花木兰");
        user.setPassWord("123456");
        user.setAge(34);
        userMapper.add(user);

    }

    /**
     * 修改（动态拼接修改字段和条件）
     */
    @org.junit.Test
    public void update() throws Exception {
        User user = new User();
        user.setId(7L);
//        user.setUserName("");
//        user.setPassWord("123456");
        user.setAge(17);

        System.out.println(userMapper.update(user));
    }

    /**
     * 删除（动态拼接条件）
     */
    @org.junit.Test
    public void delete() throws Exception {
        User user = new User();
        user.setId(7L);
//        user.setUserName("");
//        user.setPassWord("123456");
//        user.setAge(17);

        System.out.println(userMapper.delete(user));


    }
}
