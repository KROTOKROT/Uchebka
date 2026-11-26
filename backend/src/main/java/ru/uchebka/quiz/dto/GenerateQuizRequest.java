package ru.uchebka.quiz.dto;
import jakarta.validation.constraints.*;
public record GenerateQuizRequest(@NotBlank @Size(min=100,max=50000) String material,@NotNull @Min(3) @Max(30) Integer questionCount,@NotNull Difficulty difficulty){public enum Difficulty{EASY,MEDIUM,HARD}}