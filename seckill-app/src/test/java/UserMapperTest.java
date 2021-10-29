import com.scut.entity.User;
import com.scut.exception.DAOException;
import com.scut.mapper.UserMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import com.scut.Application;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class UserMapperTest {

    @Autowired
    UserMapper userMapper;
    @Test
    public void AddUserTest() throws DAOException {
        userMapper.addUser(new User(null,"lin","18012345678",new Date()));
    }

    @Test
    public void GetUserByIdTest() throws DAOException {
        User user = userMapper.getUserById(1);
        System.out.println(user);
    }

    @Test
    public void batchInsertTest() throws DAOException {
        List<User> list = new ArrayList<>();
        for(int i = 51;i < 5000;i++) {
            list.add(new User(null,"ben" + i,"18012345678",new Date()));
        }
        userMapper.batchInsert(list);
    }
}
