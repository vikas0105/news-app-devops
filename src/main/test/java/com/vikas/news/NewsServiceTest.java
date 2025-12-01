package com.vikas.news;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NewsServiceTest {

    @Test
    void verifyConfigLoading() {
        NewsServlet servlet = new NewsServlet();
        servlet.init();

        assertNotNull(servlet);
    }
}
