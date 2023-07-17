// application.properties에 데이터소스에 관한 정보들을 지정해 줌. (jdbc 드라이버 사용할 것이고, 어느 포트 사용할 것이고, 사용자 이름과 패스워드는 무엇인지)
// dataSource 객체에 application.properties에 작성한 정보들이 담기게 됨.
// 이 정보를 활용해서 jdbcTemplate 변수 만들고, 데이터 소스 정보를 jdbcTemplate 이라는 변수에 넣어줌.

package zerobase.weather.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import zerobase.weather.domain.Memo;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbcMemoRepository {
    private final JdbcTemplate jdbcTemplate; // mysql에 스프링부트에서 입력한 테이블 내용 업데이트 해주기 위한 연결 객체 생성

    @Autowired // datasource를 application.properties에서 알아서 가져오게 해주는 어노테이션
    public JdbcMemoRepository(DataSource dataSource) { // JdbcMemoRepository의 생성자 구현
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Memo save(Memo memo) { // Memo 클래스의 값을 저장하면 Mysql에 memo 클래스 값이 저장이 되고, 반환값은 저장한 Memo 값을 반환하는 함수
        String sql = "insert into memo values(?, ?)"; // ?에 들어갈 내용은 아래에 입력해줌. (momo.getId(), memo.getText())
        jdbcTemplate.update(sql, memo.getId(), memo.getText()); // sql에 memo에 입력한 id와 text를 업데이트 해주겠다.
        return memo;
    }

    private RowMapper<Memo> memoRowMapper() { // jdbc 통해서 mysql에서 데이터 가져오면, 가져온 데이터 값은 ResultSet 형식의 데이터임.
        // ResultSet
        // {id = 1, text = 'this is memo~'} 이런 형식으로 데이터 가져와짐.
        // 이 ResultSet에 Memo 형식으로 데이터를 매핑해주는 것을 RowMapper<Memo> 라고 함.
        return (rs, rowNum) -> new Memo(
                rs.getInt("id"),
                rs.getString("text")
        );
    }

    public List<Memo> findAll() { // 데이터 조회 함수
        String sql = "select * from memo";
        return jdbcTemplate.query(sql, memoRowMapper()); // query() : 데이터 조회하기 위한 함수
        // jdbcTemplate이 mysql로 가서, sql 쿼리를 던지고, 던졌을 때 반환된 객체들을 resultSet 형태로 가져와서 memoRowMapper함수를 이용해서 메모 객체로 가져옴.
    }

    public Optional<Memo> findById(int id) { // id를 통해 메모 객체를 찾는 함수, Optional<> => 찾는 id가 혹시 null인 경우를 다루기 위해 자바에서 제공하는 함수
        String sql = "select * from memo where id = ?";
        return jdbcTemplate.query(sql, memoRowMapper(), id).stream().findFirst(); // 입력한 id가 여러 개인 경우, 가장 위에 있는 데이터 가져오겠다.
    }
}
