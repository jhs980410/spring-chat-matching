import { useEffect } from "react";
import { useSearchParams, useNavigate } from "react-router-dom";
import api from "../../api/axios";

export default function PaymentConfirmPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();

  useEffect(() => {
    const confirm = async () => {
      const paymentKey = params.get("paymentKey");
      const orderId = params.get("orderId");
      const amount = params.get("amount");

      if (!paymentKey || !orderId || !amount) {
        navigate("/payment/fail");
        return;
      }

      try {
        await api.post("/payments/confirm", {
          paymentKey,
          orderId, // ORD-000001 형태 그대로
          amount: Number(amount),
        });

        // ✅ 승인 성공 후에만 success로 이동
        navigate("/payment/success");
      } catch (e) {
        console.error(e);
        navigate("/payment/fail");
      }
    };

    confirm();
  }, []);

  return <div>결제 승인 처리 중...</div>;
}
