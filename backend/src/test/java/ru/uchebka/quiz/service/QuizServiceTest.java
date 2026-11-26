package ru.uchebka.quiz.service;
import static org.junit.jupiter.api.Assertions.*;import static org.mockito.Mockito.*;import com.fasterxml.jackson.databind.ObjectMapper;import org.junit.jupiter.api.Test;import ru.uchebka.llm.LlmClient;import ru.uchebka.quiz.dto.*;
class QuizServiceTest{
 private final LlmClient llm=mock(LlmClient.class);private final QuizService service=new QuizService(llm,new ObjectMapper());
 @Test void parsesValidQuiz(){when(llm.generate(anyString(),anyString())).thenReturn("{\"title\":\"Основы\",\"description\":\"Тест\",\"questions\":[{\"question\":\"Вопрос?\",\"options\":[\"A\",\"B\",\"C\",\"D\"],\"correctAnswer\":2,\"explanation\":\"Потому что так.\"}]}");var req=new GenerateQuizRequest("x".repeat(100),1,GenerateQuizRequest.Difficulty.EASY);assertEquals("Основы",service.generate(req).title());}
 @Test void rejectsInvalidQuestionCount(){when(llm.generate(anyString(),anyString())).thenReturn("{\"title\":\"Основы\",\"description\":\"Тест\",\"questions\":[]}");var req=new GenerateQuizRequest("x".repeat(100),2,GenerateQuizRequest.Difficulty.EASY);assertThrows(IllegalStateException.class,()->service.generate(req));}
}