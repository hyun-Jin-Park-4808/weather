package zerobase.weather.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;


@Repository
public interface JPAMemoRepository extends JpaRepository<Memo, Integer> {
// 자바에서 Jpa ORM에서 쓸 함수를 JpaRepository 클래스에 정의해놓음. 어떤 클래스를 가지고 DB에 연결할지와 클래스의 key의 데이터 타입을 명시해줘야 함(Memo, Integer)
// JpaRepository에 save, findAll 등의 함수가 이미 다 정의되어 있음!
}
