import com.scut.Application;
import com.scut.entity.Product;
import com.scut.exception.DAOException;
import com.scut.mapper.ProductMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ProductMapperTest {

    @Autowired
    ProductMapper productMapper;

    @Test
    public void AddProductTest() throws DAOException {
        productMapper.addProduct(new Product(null,"iPhone13",new BigDecimal(999),10,1));
    }

    @Test
    public void getProductByIdTest() throws DAOException {
        System.out.println(productMapper.getProductById(1));
    }

    @Test
    public void getStockByIdTest() throws DAOException {
        System.out.println(productMapper.getStockById(1));
    }

    @Test
    public void updateProductStockTest() throws DAOException {
        productMapper.updateProductStock(1);
    }
}
