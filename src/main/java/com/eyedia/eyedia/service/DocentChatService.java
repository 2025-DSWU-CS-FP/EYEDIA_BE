package com.eyedia.eyedia.service;

import com.eyedia.eyedia.repository.PaintingRepository;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseInputItem;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocentChatService {

    private final PaintingRepository paintingRepository;
    private final OpenAIClient openAI;

    public Answer answer(String objectId, String question) {
        var p = paintingRepository.findByArtIdAndUser(objectId, null)
                .orElseThrow(() -> new IllegalArgumentException("invalid artId: " + objectId));

        String system = """
            너는 미술관 도슨트야. 한국어로 친절하고 자연스럽게 설명해.
            - 제공된 작품 정보와 질문만 사용(추측 금지)
            - 3~6문장, 마지막에 짧은 질문으로 마무리
            """;

        String user = """
            [작품 정보]
            제목: %s
            작가: %s
            전시: %s
            기본설명: %s

            [관람객 질문]
            %s
            """.formatted(
                nz(p.getTitle()), nz(p.getArtist()),
                "메트로폴리탄 미술관", nz(p.getDescription()),
                nz(question)
        );

        // ✅ Responses API의 입력은 ResponseInputItem으로 메시지 역할/내용을 지정
        List<ResponseInputItem> inputs = List.of(
                ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .role(ResponseInputItem.Message.Role.SYSTEM)
                                .addInputTextContent(system)
                                .build()
                ),
                ResponseInputItem.ofMessage(
                        ResponseInputItem.Message.builder()
                                .role(ResponseInputItem.Message.Role.USER)
                                .addInputTextContent(user)
                                .build()
                )
        );

        ResponseCreateParams params = ResponseCreateParams.builder()
                .model(ChatModel.CHATGPT_4O_LATEST)
                .input(ResponseCreateParams.Input.ofResponse(inputs))
                .maxOutputTokens(350)
                .build();

        Response resp = openAI.responses().create(params);

        // ✅ 텍스트 추출: output -> message -> content -> outputText -> text
        String text = resp.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(msg -> msg.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(t -> t.text())
                .collect(Collectors.joining());

        // Todo : Message에 DB 저장

        return new Answer(text, params.model().toString());
    }

    private static String nz(String s){ return s == null ? "" : s; }

    public record Answer(String text, String model) {}
}