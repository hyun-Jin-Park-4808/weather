// 스프링 부트의 메모 테이블
package zerobase.weather.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "memo")
public class Memo {
    @Id // primary key가 Id임을 명시해줌.
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 데이터베이스에게 키 생성을 맡기겠다.
    private int id;
    private String text;
}
