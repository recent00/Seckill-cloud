import com.scut.Application;
import com.scut.entity.Record;
import com.scut.exception.DAOException;
import com.scut.mapper.RecordMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class RecordMapperTest {

    @Autowired
    RecordMapper recordMapper;
    @Test
    public void addRecordTest() throws DAOException {
        recordMapper.addRecord(new Record(null,1,1,0,new Date()));
    }
}
