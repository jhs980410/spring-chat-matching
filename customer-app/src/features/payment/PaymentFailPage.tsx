import { useEffect } from "react";
import { useSearchParams } from "react-router-dom";
import api from "../../api/axios";

export default function PaymentFailPage() {
  const [params] = useSearchParams();

  useEffect(() => {
    const orderId = params.get("orderId");
    const message = params.get("message");

    if (orderId) {
      api.post("/payments/fail", {
        orderId,
        message,
      });
    }
  }, []);

  return (
    <div>
      <h2>결제가 실패했습니다.</h2>
      <p>다시 시도해주세요.</p>
    </div>
  );
}
