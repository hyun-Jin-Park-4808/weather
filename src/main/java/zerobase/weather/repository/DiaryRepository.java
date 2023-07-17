package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.domain.Diary;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Integer> {
    // repository 에 저장할 Entity 객체 이름과, pk인 id의 타입인 integer 입력

    List<Diary> findAllByDate(LocalDate date); // 날짜 기준으로 데이터 찾아서 리스트 형태로 받는 함수

    List<Diary> findAllByDateBetween(LocalDate startDate, LocalDate endDate); // 시작날짜와 종료 날짜 사이 모든 데이터를 찾아주는 함수

    Diary getFirstByDate(LocalDate date); // 해당 date에 있는 다이어리 중 첫번째 일기만 가져오겠다.

    @Transactional
    void deleteAllByDate(LocalDate date); // 해당 날짜부터 모든 일기 삭제

}
