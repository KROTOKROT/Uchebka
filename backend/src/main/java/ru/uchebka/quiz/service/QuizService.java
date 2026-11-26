package ru.uchebka.quiz.service;
import com.fasterxml.jackson.databind.ObjectMapper;import java.util.regex.*;import org.springframework.stereotype.Service;import ru.uchebka.llm.LlmClient;import ru.uchebka.quiz.dto.*;
@Service public class QuizService{
 private static final Pattern JSON=Pattern.compile("\\{[\\s\\S]*}");private final LlmClient llm;private final ObjectMapper mapper;
 public QuizService(LlmClient llm,ObjectMapper mapper){this.llm=llm;this.mapper=mapper;}
 public QuizResponse generate(GenerateQuizRequest r){
  String prompt=("Ты — преподаватель и методист. Создай тест строго по предоставленному учебному материалу. Верни только JSON без Markdown в формате: {\"title\":\"Название\",\"description\":\"Описание\",\"questions\":[{\"question\":\"Вопрос\",\"options\":[\"A\",\"B\",\"C\",\"D\"],\"correctAnswer\":0,\"explanation\":\"Объяснение\"}]}. Ровно %d вопросов, сложность %s. Четыре варианта, correctAnswer — индекс от 0 до 3. Не добавляй факты вне материала.").formatted(r.questionCount(),r.difficulty());
  String raw=llm.generate(prompt,r.material());Matcher m=JSON.matcher(raw);if(!m.find())throw new IllegalStateException("AI response is not JSON");
  try{QuizResponse quiz=mapper.readValue(m.group(),QuizResponse.class);validate(quiz,r.questionCount());return quiz;}catch(Exception e){throw new IllegalStateException("AI вернул тест в некорректном формате",e);}
 }
 private void validate(QuizResponse q,int count){if(q.title()==null||q.questions()==null||q.questions().size()!=count)throw new IllegalStateException("Invalid quiz structure");for(var item:q.questions())if(item.question()==null||item.options()==null||item.options().size()!=4||item.correctAnswer()<0||item.correctAnswer()>3||item.explanation()==null)throw new IllegalStateException("Invalid question structure");}
}