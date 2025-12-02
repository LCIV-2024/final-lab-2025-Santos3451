package com.example.demobase.service;

import com.example.demobase.dto.WordDTO;
import com.example.demobase.model.Word;
import com.example.demobase.repository.WordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WordServiceTest {

    @Mock
    private WordRepository wordRepository;

    @InjectMocks
    private WordService wordService;

    @Test
    void testGetAllWords() {
        // Preparar datos de prueba
        Word word1 = new Word();
        word1.setId(1L);
        word1.setPalabra("PROGRAMADOR");
        word1.setUtilizada(false);

        Word word2 = new Word();
        word2.setId(2L);
        word2.setPalabra("COMPUTADORA");
        word2.setUtilizada(true);

        // Configurar mock
        when(wordRepository.findAllOrdered()).thenReturn(List.of(word1, word2));

        // Ejecutar
        List<WordDTO> result = wordService.getAllWords();

        // Verificar
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("PROGRAMADOR", result.get(0).getPalabra());
        assertEquals(false, result.get(0).getUtilizada());
        assertEquals("COMPUTADORA", result.get(1).getPalabra());
        assertEquals(true, result.get(1).getUtilizada());
        verify(wordRepository, times(1)).findAllOrdered();
    }
}
