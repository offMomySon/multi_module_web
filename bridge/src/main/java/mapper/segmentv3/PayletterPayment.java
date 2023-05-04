package mapper.segmentv3;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Data
public class PayletterPayment {
    private static final long serialVersionUID = -7415507621720287668L;

    private static final Logger LOGGER = LoggerFactory.getLogger(PayletterPayment.class);

    // 결제요청 (레진 > 페이레터)
    private Long userId;                    // [필수] 레진 사용자번호
    private String locale;                  // [필수] 사용자 선택언어 {ja-JP, en-US} (최대 5자)
    private String paymentType;             // 레진 결제타입 (payletter, mpayletter: 일반결제, billingcredit: 정기결제)
    private Long paymentId;                 // [필수] 레진 결제 ID
    private Long coinProductId;             // [필수] 레진 상품 ID
    private String amount;                   // [필수] 과금상품 금액
    private String currency;                // [필수] 결제 통화 {JPY, USD} (최대 3자)
    private Integer pointAmount;            // [필수] 차감 포인트
    private String productTitle;            // [필수] 과금상품 설명 (최대 200자)
    private Boolean isMobile;
    private Boolean isApp;
    private String platForm;
    private String store;
    private String returnTo;
    private Long timeStamp;                 // [필수] 타임스탬프(UTC+0)
    private String callbackUrl;
    // 결제요청 (레진 > 페이레터): 정기결제
    private Integer recurringInterval;      // (정기결제인경우) 정기결제 간격, Default: 1
    private String signature;               // [필수] SHA256 Hash value
    private String actionUrl;               // [web-front] payletter 결제 URL

    // 결제취소 (레진 > 페이레터)
    private String reason;                  // 취소사유
    private String adminId;                 // (레진)

    // 결제통보 (페이레터 > 레진)
    private String isConfirm;               // [필수] 결제완료여부 {Y, N}
    private String pgCode;                  // [필수] 결제 게이트웨이명 (paypal)
    private String paymentMethod;           // [필수] 결제 수단명 (eWallet)
    private Long transactionId;             // [필수] PLPB 거래번호
    private String externalTransactionId;   // [필수] PG 거래번호 (최대 50자)
    private String vatAmount;               // [필수] 부가세
    // 결제통보 (페이레터 > 레진): 정기결제
    private Integer recurringResultCode;    // 정기결제처리 결과 {1: 성송, 2: 실패}
    private Long recurringId;               // (페이레터)정기결제 ID
    private Integer nextPaymentAt;          // 다음결제일(YYYYMMDD)
    private String cardCompanyCode;         // 카드사 코드
    private String cardCompanyName;         // 카드사 명
    private String cardNumber;              // 카드번호(with 마스킹)
    private String cardSeq;                 // ??
    private String errCode;                 // PG사 오류코드(recurringResultCode = 2일때 전달)
    private String firstPaymentFlag;        // 정기결제 1회차 여부 (user-action or server-notify)

    // 정기결제 정보 조회
    private String statusCode;              // 정기결제 상태 {1: 정상, 2: 해지신청중, 3: 해지완료}

    // 응답
    private String errorCode;               // [필수] 에러 코드
    private String errorMsg;                // [필수] 에러 메시지


    private Long retryRecurringId;          // 정기결제 재결제 시 사용

    private String externalStoreProductId;


    @Override
    public String toString() {
        return "PayletterPayment{" +
            "userId=" + userId +
            ", locale='" + locale + '\'' +
            ", paymentType='" + paymentType + '\'' +
            ", paymentId=" + paymentId +
            ", coinProductId=" + coinProductId +
            ", amount='" + amount + '\'' +
            ", currency='" + currency + '\'' +
            ", pointAmount=" + pointAmount +
            ", productTitle='" + productTitle + '\'' +
            ", isMobile=" + isMobile +
            ", isApp=" + isApp +
            ", platForm='" + platForm + '\'' +
            ", store='" + store + '\'' +
            ", returnTo='" + returnTo + '\'' +
            ", timeStamp=" + timeStamp +
            ", callbackUrl='" + callbackUrl + '\'' +
            ", recurringInterval=" + recurringInterval +
            ", signature='" + signature + '\'' +
            ", actionUrl='" + actionUrl + '\'' +
            ", reason='" + reason + '\'' +
            ", adminId='" + adminId + '\'' +
            ", isConfirm='" + isConfirm + '\'' +
            ", pgCode='" + pgCode + '\'' +
            ", paymentMethod='" + paymentMethod + '\'' +
            ", transactionId=" + transactionId +
            ", externalTransactionId='" + externalTransactionId + '\'' +
            ", vatAmount='" + vatAmount + '\'' +
            ", recurringResultCode=" + recurringResultCode +
            ", recurringId=" + recurringId +
            ", nextPaymentAt=" + nextPaymentAt +
            ", cardCompanyCode='" + cardCompanyCode + '\'' +
            ", cardCompanyName='" + cardCompanyName + '\'' +
            ", cardNumber='" + cardNumber + '\'' +
            ", cardSeq='" + cardSeq + '\'' +
            ", errCode='" + errCode + '\'' +
            ", firstPaymentFlag='" + firstPaymentFlag + '\'' +
            ", statusCode='" + statusCode + '\'' +
            ", errorCode='" + errorCode + '\'' +
            ", errorMsg='" + errorMsg + '\'' +
            ", retryRecurringId=" + retryRecurringId +
            ", externalStoreProductId='" + externalStoreProductId + '\'' +
            '}';
    }
}
