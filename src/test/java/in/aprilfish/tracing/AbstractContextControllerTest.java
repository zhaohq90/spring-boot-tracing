package in.aprilfish.tracing;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SimpleSpringApplication.class)
@WebAppConfiguration
public class AbstractContextControllerTest {

    // 注入WebApplicationContext
    @Autowired
    protected WebApplicationContext wac;

    // 模拟MVC对象，通过MockMvcBuilders.webAppContextSetup(this.wac).build()初始化。
    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

}
