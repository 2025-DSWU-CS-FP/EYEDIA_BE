package com.eyedia.eyedia.service;

import com.eyedia.eyedia.domain.Message;
import com.eyedia.eyedia.domain.Painting;
import com.eyedia.eyedia.domain.enums.SenderType;
import com.eyedia.eyedia.global.error.exception.GeneralException;
import com.eyedia.eyedia.global.error.status.ErrorStatus;
import com.eyedia.eyedia.repository.MessageRepository;
import com.eyedia.eyedia.repository.PaintingRepository;
import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.Response;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseInputItem;
import com.openai.models.responses.ResponseOutputText;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DocentChatService {

    private final PaintingRepository paintingRepository;
    private final OpenAIClient openAI;
    private final MessageRepository messageRepository;

    public Prompt basePrompt(Painting p, String question){

        String system = """
            너는 미술관 도슨트야. 한국어로 친절하고 자연스럽게 설명해.
            - 제공된 작품 정보와 질문만 사용(추측 금지)
            - 3~6문장으로 답변
            - 예술 분야 외 다른 질문 차단
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

        return new Prompt(system, user);
    }

    public Prompt gazeAreaPrompt(String paintingTitle, String quadrant, String description){
        String quadrantPosition = convertQuadrantToPosition(quadrant);

        String system = """
                너는 미술관 도슨트야. 한국어로 친절하고 자연스럽게 설명해.
                - 제공된 작품 정보와 질문만 사용(추측 금지)
                - 3~6문장으로 답변
                - 예술 분야 외 다른 질문 차단
                - 작품 전체 설명보다는 각 객체에 대한 설명을 중심으로 해설해주세요.
            """;

        String user = """
                아래는 %s 그림의 %s 에서 감지된 후보 객체 설명입니다: \s
                %s\s
                → 위의 내용을 바탕으로 관람객에게 설명을 작성해주세요.
            """.formatted(
                nz(paintingTitle), nz(quadrantPosition),
                nz(description)
        );

        return new Prompt(system, user);
    }

    public Answer answer(Prompt prompt) {
        var system = prompt.system();
        var user = prompt.user();

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
                .maxOutputTokens(2000)
                .build();

        Response resp = openAI.responses().create(params);

        // ✅ 텍스트 추출: output -> message -> content -> outputText -> text
        String text = resp.output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(msg -> msg.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(ResponseOutputText::text)
                .collect(Collectors.joining());

        return new Answer(text, params.model().toString());
    }

    private static String nz(String s){ return s == null ? "" : s; }

    public record Prompt(String system, String user) {}
    public record Answer(String text, String model) {}

    public String convertQuadrantToPosition(String quadrant) {
        return switch (quadrant) {
            case "Q1" -> "왼쪽 상단";
            case "Q2" -> "오른쪽 상단";
            case "Q3" -> "왼쪽 하단";
            case "Q4" -> "오른쪽 하단";
            default -> "잘못된 분면";  // 잘못된 입력에 대비한 기본값
        };
    }

}
