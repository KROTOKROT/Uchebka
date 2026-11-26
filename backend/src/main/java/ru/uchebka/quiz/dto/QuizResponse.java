package ru.uchebka.quiz.dto;
import java.util.List;
public record QuizResponse(String title,String description,List<QuestionDto> questions){public record QuestionDto(String question,List<String> options,int correctAnswer,String explanation){}}