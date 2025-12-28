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
        alert("결제 정보가 올바르지 않습니다.");
        return;
      }

      try {
        await api.post("/payments/confirm", {
          paymentKey,
          orderId,
          amount: Number(amount),
        });

        navigate("/payment/success");
      } catch (e) {
        console.error(e);
        navigate("/payment/fail");
      }
    };

    confirm();
  }, []);

  return <div>결제 처리 중...</div>;
}
