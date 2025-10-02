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

        return new Prompt(system, user);
    }

    public Prompt gazeAreaPrompt(String paintingTitle, String quadrant, String description){
        String system = """
                당신은 미술관의 도슨트입니다.\s
                관람객에게 친절하고 감성적인 어조로 작품 속 객체를 설명해야 합니다.\s
                너무 기술적이거나 딱딱하지 않게 풀어주세요.\s
                그림에 없는 내용은 설명하지 말고, 해당 그림 외의 다른 그림은 언급하지 마세요.\s
                작품 전체 설명보다는 각 객체에 대한 설명을 중심으로 해설해주세요.
            """;

        String user = """
                아래는 %s 그림의 %s 분면에서 감지된 후보 객체 설명입니다: \s
                %s\s
                → 위의 내용을 바탕으로 관람객에게 설명을 작성해주세요.
            """.formatted(
                nz(paintingTitle), nz(quadrant),
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
}