package backend.futurefinder.controller.house;

import backend.futurefinder.dto.request.house.*;
import backend.futurefinder.dto.response.house.*;
import backend.futurefinder.model.house.ChatMessageEntry;
import backend.futurefinder.response.HttpResponse;
import backend.futurefinder.response.SuccessOnlyResponse;
import backend.futurefinder.service.house.ChatbotService;
import backend.futurefinder.service.house.HouseService;
import backend.futurefinder.model.user.LocationType;
import backend.futurefinder.util.helper.ResponseHelper;
import backend.futurefinder.util.security.CurrentUser;
import backend.futurefinder.model.user.UserId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/house")
public class HouseController {

    private final HouseService houseService;
    private final ChatbotService chatbotService;

    private String uid(UserId userId) { return userId.getId(); }

    // ------- 지역 -------
    @Operation(summary = "현재 주거지 등록", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/location/current")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> registerCurrent(
            @CurrentUser UserId userId,
            @RequestBody LocationRequest req
    ) {
        houseService.upsertLocation(uid(userId), req.province(), req.city(), LocationType.CURRENT);
        return ResponseHelper.successOnly(); // ✅ 인자 없음 OK
    }

    @Operation(summary = "관심 지역 등록", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/location/interest")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> registerInterest(
            @CurrentUser UserId userId,
            @RequestBody LocationRequest req
    ) {
        houseService.upsertLocation(uid(userId), req.province(), req.city(), LocationType.INTEREST);
        return ResponseHelper.successOnly(); // ✅
    }

    @Operation(summary = "현재 주거지 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/location/current")
    public ResponseEntity<HttpResponse<List<LocationResponse>>> getCurrent(
            @CurrentUser UserId userId
    ) {
        List<LocationResponse> res = houseService.getLocations(uid(userId), LocationType.CURRENT)
                .stream()
                .map(e -> new LocationResponse(e.getProvince(), e.getCity(), "CURRENT"))
                .toList();
        return ResponseHelper.success(res); // ✅ success(data)
    }

    @Operation(summary = "관심 지역 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/location/interest")
    public ResponseEntity<HttpResponse<List<LocationResponse>>> getInterest(
            @CurrentUser UserId userId
    ) {
        List<LocationResponse> res = houseService.getLocations(uid(userId), LocationType.INTEREST)
                .stream()
                .map(e -> new LocationResponse(e.getProvince(), e.getCity(), "INTEREST"))
                .toList();
        return ResponseHelper.success(res);
    }

    // ------- 청약 계좌/입금 -------
    @Operation(summary = "청약 계좌 등록/수정", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/subscription/account")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> upsertAccount(
            @CurrentUser UserId userId,
            @RequestBody AccountRequest req
    ) {
        houseService.upsertSubscriptionAccount(uid(userId), req.bankName(), req.accountNumber());
        return ResponseHelper.successOnly(); // ✅
    }

    @Operation(summary = "청약 총액 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/subscription/amount")
    public ResponseEntity<HttpResponse<BigDecimal>> getAmount(@CurrentUser UserId userId) {
        return ResponseHelper.success(houseService.getSubscriptionTotal(uid(userId)));
    }

    @Operation(summary = "입금 등록", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/subscription/deposit")
    public ResponseEntity<HttpResponse<SuccessOnlyResponse>> addDeposit(
            @CurrentUser UserId userId,
            @RequestBody DepositRequest req
    ) {
        houseService.addDeposit(uid(userId), req.accountNumber(), req.amount(), req.memo());
        return ResponseHelper.successOnly(); // ✅
    }

    @Operation(summary = "최근 입금 내역 조회(기본 3건)", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/subscription/deposits")
    public ResponseEntity<HttpResponse<List<DepositResponse>>> recentDeposits(
            @CurrentUser UserId userId,
            @RequestParam(defaultValue = "3") int limit
    ) {
        List<DepositResponse> res = houseService.getRecentDeposits(uid(userId), limit)
                .stream()
                .map(d -> new DepositResponse(
                        d.getId(),
                        d.getSubscriptionAccountId(),
                        d.getDepositAmount(),
                        d.getMemo(),
                        d.getCreatedAt()
                ))
                .toList();
        return ResponseHelper.success(res);
    }

    @Operation(summary = "하우스 요약", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/summary")
    public ResponseEntity<HttpResponse<HouseSummaryResponse>> getSummary(@CurrentUser UserId userId) {

        var current = houseService.getLocations(uid(userId), LocationType.CURRENT).stream()
                .map(e -> new LocationResponse(e.getProvince(), e.getCity(), "CURRENT")).toList();

        var interest = houseService.getLocations(uid(userId), LocationType.INTEREST).stream()
                .map(e -> new LocationResponse(e.getProvince(), e.getCity(), "INTEREST")).toList();

        var total = houseService.getSubscriptionTotal(uid(userId));

        var deposits = houseService.getRecentDeposits(uid(userId), 3).stream()
                .map(d -> new DepositResponse(d.getId(), d.getSubscriptionAccountId(),
                        d.getDepositAmount(), d.getMemo(), d.getCreatedAt()))
                .toList();

        return ResponseHelper.success(new HouseSummaryResponse(current, interest, total, deposits));
    }

    // ------- 챗봇 -------
    // HouseController.java에서
    @Operation(summary = "챗봇에게 질문하기", security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping("/chatbot/ask")
    public ResponseEntity<HttpResponse<ChatResponse>> askChatbot(
            @CurrentUser UserId userId,
            @RequestBody @Valid ChatRequest req
    ) {
        String botResponse = chatbotService.sendMessage(uid(userId), req.message());

        ChatResponse response = new ChatResponse(
                null, // messageId는 간단히 null로
                req.message(),
                botResponse,
                LocalDateTime.now()
        );

        return ResponseHelper.success(response);
    }

    @Operation(summary = "챗봇 대화 내역 조회", security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/chatbot/history")
    public ResponseEntity<HttpResponse<ChatHistoryResponse>> getChatHistory(
            @CurrentUser UserId userId,
            @RequestParam(defaultValue = "10") int limit
    ) {
        List<ChatMessageEntry> messages = chatbotService.getChatHistory(uid(userId), limit);

        List<ChatResponse> chatResponses = messages.stream()
                .map(msg -> new ChatResponse(
                        msg.getId(),
                        msg.getUserMessage(),
                        msg.getBotResponse(),
                        msg.getCreatedAt()
                ))
                .toList();

        ChatHistoryResponse history = new ChatHistoryResponse(chatResponses, messages.size());
        return ResponseHelper.success(history);
    }
}
