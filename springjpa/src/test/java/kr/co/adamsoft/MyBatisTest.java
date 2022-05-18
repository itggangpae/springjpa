package kr.co.adamsoft;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import kr.co.adamsoft.mapper.MemoMapper;

@SpringBootTest
public class MyBatisTest {
    @Autowired
    MemoMapper memoMapper;

    @Test
    public void testMyBatis(){
        System.out.println(memoMapper);
        System.out.println(memoMapper.listMemo());
    }
}
