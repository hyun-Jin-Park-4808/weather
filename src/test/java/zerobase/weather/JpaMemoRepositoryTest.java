package zerobase.weather;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Memo;
import zerobase.weather.repository.JPAMemoRepository;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional // 테스트 코드에 transactional 달면 커밋 안 하고, 롤백 시킴.
public class JpaMemoRepositoryTest {

    @Autowired // jpaMemoRepository 불러오기 위한 어노테이션
    JPAMemoRepository jpaMemoRepository;

    @Test
    void insertMemoTest() {
        //given
        Memo newMomo = new Memo(10, "this is jpa memo.");

        //when
        jpaMemoRepository.save(newMomo);

        //then
        List<Memo> memoList = jpaMemoRepository.findAll();
        assertTrue(memoList.size() > 0);
        }

    @Test
    void findByIdTest() {
        //given
        Memo newMemo = new Memo(11, "jpa"); // 우리는 datatbase에 key 생성을 맡겼으므로, 여기서 지정하는 id는 의미가 없음.

        //when
        Memo memo = jpaMemoRepository.save(newMemo);

        //then
        Optional<Memo> result = jpaMemoRepository.findById(memo.getId()); // 테이블에서 생성한 Id 값을가져와야 함.
        assertEquals(result.get().getText(), "jpa");
        }

}
