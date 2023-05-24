package org.example.newsstreaming.controller;

import org.example.newsstreaming.response.NewsSentimentReportResponse;
import org.example.newsstreaming.response.NewsSentimentResponse;
import org.example.newsstreaming.service.NewsSentimentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class NewsSentimentControllerTest {

    private MockMvc mockMvc;

    @Mock
    private NewsSentimentService newsSentimentService;

    @InjectMocks
    private NewsSentimentController newsSentimentController;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(newsSentimentController)
                .build();
    }

    @Test
    void getNewsSentiments() throws Exception {
        when(newsSentimentService.getNewsSentiments(any(), any(), anyInt(), anyString()))
                .thenReturn(new NewsSentimentResponse(HttpStatus.OK.name(), "Some Message"));
        mockMvc.perform(get("/news-sentiment/list")
                        .param("startDate", "2000-01-01")
                        .param("endDate", "2023-05-31")
                        .param("pageSize", "10")
                        .param("nextPage", "any"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(HttpStatus.OK.name())));
    }

    @Test
    void searchByTitle() throws Exception {
        when(newsSentimentService.searchByTitle(anyString(), anyInt(), anyString()))
                .thenReturn(new NewsSentimentResponse(HttpStatus.OK.name(), "Some Message"));
        mockMvc.perform(get("/news-sentiment/search")
                        .param("q", "any")
                        .param("pageSize", "10")
                        .param("nextPage", "any"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(HttpStatus.OK.name())));
    }

    @Test
    void getNewsSentimentsBySentiment() throws Exception {
        when(newsSentimentService.getNewsSentimentsBySentiment(any(), anyInt(), anyString()))
                .thenReturn(new NewsSentimentResponse(HttpStatus.OK.name(), "Some Message"));
        mockMvc.perform(get("/news-sentiment/Positive/sentiment")
                        .param("pageSize", "10")
                        .param("nextPage", "any"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(HttpStatus.OK.name())));
    }

    @Test
    void getNewsSentimentReport() throws Exception {
        when(newsSentimentService.getNewsSentimentReport(any(), any()))
                .thenReturn(new NewsSentimentReportResponse(HttpStatus.OK.name(), "Some Message"));
        mockMvc.perform(get("/news-sentiment/report")
                        .param("startDate", "2000-01-01")
                        .param("endDate", "2023-05-31"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(HttpStatus.OK.name())));
    }
}