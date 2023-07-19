package zerobase.weather.service;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import zerobase.weather.WeatherApplication;
import zerobase.weather.domain.DateWeather;
import zerobase.weather.domain.Diary;
import zerobase.weather.error.InvalidDate;
import zerobase.weather.repository.DateWeatherRepository;
import zerobase.weather.repository.DiaryRepository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class DiaryService {

    @Value("${openweathermap.key}") // application.properties에 저장한 openweathermap.key 라는 변수를 가져와서
    private String apiKey; // apiKey 라는 객체에다가 넣어주겠다.

    private final DiaryRepository diaryRepository;// bean 생성될 때 diaryRepository 가져옴.
    private final DateWeatherRepository dateWeatherRepository;

    private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class); // 어떤 클래스에서 로거를 가져올 것이냐

    public DiaryService(DiaryRepository diaryRepository, DateWeatherRepository dateWeatherRepository) {
        this.diaryRepository = diaryRepository;
        this.dateWeatherRepository = dateWeatherRepository;
    }

    @Transactional
    @Scheduled(cron = "0 0 1 * * *") // 매일 새벽 한 시마다 이 함수 동작
    public void saveWeatherDate() {
        logger.info("오늘 날씨 데이터 잘 가져옴.");
        dateWeatherRepository.save(getWeatherFromApi()); // 매일 새벽에 Api로부터 날씨 가져옴.
    }

    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void createDiary(LocalDate date, String text) {
        logger.info("started to create diary");
        // 날씨 데이터 가져오기 (API에서 가져오기 or DB에서 가져오기)
        DateWeather dateWeather = getDateWeather(date);

        //일기 값 우리 db에 넣기
        Diary nowDiary = new Diary();
        nowDiary.setDateWeather(dateWeather);
        nowDiary.setText(text);
        diaryRepository.save(nowDiary);
        logger.info("end to create diary");
    }

    private DateWeather getWeatherFromApi() {
        // open weather map에서 날씨 데이터 가져오기
        String weatherData = getWeatherString();

        // 받아온 날씨 json 파싱하기
        Map<String, Object> parseWeather = parseWeather(weatherData);
        // 파싱한 날씨 dateWeather 객체에 넣어주기
        DateWeather dateWeather = new DateWeather();
        dateWeather.setDate(LocalDate.now());
        dateWeather.setWeather(parseWeather.get("main").toString());
        dateWeather.setIcon(parseWeather.get("icon").toString());
        dateWeather.setTemperature((Double) parseWeather.get("temp"));
        return dateWeather;
    }

    private DateWeather getDateWeather(LocalDate date) {
        List<DateWeather> dateWeatherFromDB = dateWeatherRepository.findAllByDate(date);
        if (dateWeatherFromDB.size() == 0) { // DB에 저장된 날씨 없는 경우
            // 새로 api에서 날씨 정보 가져와야 함.
            // 정책상, 현재 날씨를 가져오도록 하거나, 날씨없이 일기를 쓰도록.
            return getWeatherFromApi();
        } else { // DB에 저장된 날씨 있는 경우
            return dateWeatherFromDB.get(0);
        }
    }

    @Transactional(readOnly = true)
    public List<Diary> readDiary(LocalDate date) {
      //  if (date.isAfter(LocalDate.ofYearDay(3050, 1))) {
        //    throw new InvalidDate();
      //  }
        logger.debug("read diary");
        return diaryRepository.findAllByDate(date);
    }
    public List<Diary> readDiaries(LocalDate startDate, LocalDate endDate) {
        return diaryRepository.findAllByDateBetween(startDate, endDate);
    }

    public void updateDiary(LocalDate date, String text) {
        Diary nowDiary = diaryRepository.getFirstByDate(date);
        nowDiary.setText(text); // 일기 내용 수정
        diaryRepository.save(nowDiary); // 수정 일기 내용 db에 업데이트
    }

    public void deleteDiary(LocalDate date) {
        diaryRepository.deleteAllByDate(date);
    }

    private String getWeatherString() {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q=seoul&appid=" + apiKey;
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection(); // 위에서 얻은 url을 열 수 있는 httpUrlConnection 객체 생성
            connection.setRequestMethod("GET"); // 위에서 생성한 connection 객체에 GET 요청 보내서
            int responseCode = connection.getResponseCode(); // 응답 코드를 받음.
            BufferedReader br;
            if (responseCode == 200) { // 응답 코드가 200이면 정상 동작
                br = new BufferedReader(new InputStreamReader(connection.getInputStream())); // 응답 결과를 br에 넣음.
            } else { // 200이 아니면 에러 메시지를 받아옴.
                br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            }
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = br.readLine()) != null) { // br에 넣은 응답들을 하나씩 읽으면서
                response.append(inputLine); // response라는 stringBuilder에 그 결과값들을 하나씩 쌓음.
            }
            br.close();
            return response.toString();
        } catch (Exception e) {
            return "failed to get response";
        }
    }

    private Map<String, Object> parseWeather(String jsonString) {
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject;

        try {
            jsonObject = (JSONObject) jsonParser.parse(jsonString);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Map<String, Object> resultMap = new HashMap<>();

        JSONObject mainData = (JSONObject) jsonObject.get("main");
        resultMap.put("temp", mainData.get("temp"));
        JSONArray weatherArray = (JSONArray) jsonObject.get("weather");
        JSONObject weatherData = (JSONObject) weatherArray.get(0);
        resultMap.put("main", weatherData.get("main"));
        resultMap.put("icon", weatherData.get("icon"));
        return resultMap;
    }
}
