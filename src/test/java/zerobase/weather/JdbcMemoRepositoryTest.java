package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JdbcMemoRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
 @Transactional
// 데이터베이스 테스트할 때 많이 사용되는 어노테이션, 테스트 코드 때문에 데이터 베이스 안에 있는 정보가 변경되는 것을 막기위한 어노테이션
// @Transactional 안 달면, 아래 테스트한 결과가 mysql DB에 실제로 저장이 됨.
public class JdbcMemoRepositoryTest {

    @Autowired
    JdbcMemoRepository jdbcMemoRepository;

    @Test
    void insetMemoTest() {
        //given
        Memo newMemo = new Memo(2, "insert Memo test"); // 이러한 메모가 주어지고

        //when
        jdbcMemoRepository.save(newMemo); // 이 메모를 jdbcMemoRepository에 저장했을 때,

        //then
        Optional<Memo> result = jdbcMemoRepository.findById(2); // jdbcMemoRepository에서 id가 1인 메모를 찾아서 result 변수에 담고
        assertEquals(result.get().getText(), "insert Memo test"); // 해당 메모의 Text 내용을 확인해봤더니 this is new momo~ 이더라.
    }

    @Test
    void findAllMemoTest() {
        List<Memo> memoList = jdbcMemoRepository.findAll();
        System.out.println(memoList);
        assertNotNull(memoList);
        }
}
